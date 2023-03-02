package com.ditto.storage.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ditto.storage.data.model.OfflinePatterns
import com.ditto.storage.data.model.OnBoarding
import com.ditto.storage.data.model.Patterns
import com.ditto.storage.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import storage.data.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executors


/**
 * The Room database for this app
 */
@Database(entities = [OnBoarding::class, User::class, Patterns::class, OfflinePatterns::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TraceDataDatabase : RoomDatabase() {

    abstract fun onBoardingDataDao(): OnBoardingDao
    abstract fun userDataDao(): UserDao
    abstract fun patternDataDao(): PatternsDao
    abstract fun offlinePatternDataDao():OfflinePatternDataDao

    companion object {
        val TAG = TraceDataDatabase::class.java.simpleName
        private const val DATABASE_NAME = "trace_db"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE onboard_data ADD COLUMN tutorialPdfUrl TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE user_data ADD COLUMN c_saveCalibrationPhotos INTEGER DEFAULT 0")
            }
        }

        // For Singleton instantiation
        @Volatile
        private var instance: TraceDataDatabase? = null

        fun getInstance(context: Context): TraceDataDatabase {
            return instance
                ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    )
                        .also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): TraceDataDatabase {
            return Room.databaseBuilder(context, TraceDataDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
        }

        /*fun preLoadOnboardingData(context: Context) {
            val dao = getInstance(
                context
            ).onBoardingDataDao()
            val jsonArray =
                loadJsonArray(
                    context,
                    "onboarding",
                    R.raw.onboarding
                )
            try {
                val collectionType = object : TypeToken<Collection<OnBoarding>>() {}.type
                val lcs = Gson().fromJson(jsonArray.toString(), collectionType) as List<OnBoarding>
                Executors.newSingleThreadExecutor()
                    .execute(Runnable { dao.insertAllOnboardingData(lcs) })
                Log.d(TAG, "insertOnboardingData complete")
            } catch (e: JSONException) {
                Log.d("insertOnboardingData","exception")
            }
        }*/

        fun preLoadPatternData(context: Context) {
            val dao = getInstance(
                context
            ).patternDataDao()
            val jsonArray =
                loadJsonArray(
                    context,
                    "patterns",
                    R.raw.patterns
                )
            try {
                val collectionType = object : TypeToken<Collection<Patterns>>() {}.type
                val lcs = Gson().fromJson(jsonArray.toString(), collectionType) as List<Patterns>
                Executors.newSingleThreadExecutor()
                    .execute(Runnable { dao.insertAllPatternsData(lcs) })
                Log.d(TAG, "insertPatternsData complete")
            } catch (e: JSONException) {
                Log.d("insertPatternsData","exception")
            }
        }

        private fun loadJsonArray(
            context: Context,
            jsonObject: String,
            rawJson: Int
        ): JSONArray? {
            val stringBuilder = StringBuilder()
            val inputStream = context.resources.openRawResource(rawJson)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = null
            try {
                while ({ line = reader.readLine(); line }() != null) {
                    stringBuilder.append(line)
                }
                val json = JSONObject(stringBuilder.toString())
                Log.d(TAG, "" + json.toString())
                return json.getJSONArray(jsonObject)
            } catch (e: IOException) {
                Log.d("loadJsonArray","exception")
            }
            return null
        }
    }
}
