package com.ditto.storage.data.database

import androidx.room.TypeConverter
import com.ditto.storage.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections.emptyList


/**
 * Type converters to allow Room to reference complex data types.
 */
class Converters {
    //converters for ExperienceResponse list
    @TypeConverter
    fun stringToOnboardingList(data: String?): List<OnBoarding> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<OnBoarding>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun onboardingListToString(someObjects: List<OnBoarding>): String {
        return Gson().toJson(someObjects)
    }


    //converters for KnowledgeResponse list
    @TypeConverter
    fun stringToInstructionsList(data: String?): List<Instructions> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<Instructions>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun stringToDescriptionImagesList(data: String?): List<DescriptionImages> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<DescriptionImages>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun stringToPatternPiecesList(data: String?): List<PatternPieces> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<PatternPieces>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun stringToSelvagesList(data: String?): List<Selvages> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<Selvages>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun stringToWorkspaceItemsList(data: String?): List<WorkspaceItems> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<WorkspaceItems>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun stringToWorkspaceItemAPIList(data: String?): List<WorkspaceItemAPI> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<WorkspaceItemAPI>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun workspaceItemAPIListToString(someObjects: List<WorkspaceItemAPI>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun stringToPatternPiecesFromApiList(data: String?): List<PatternPiecesFromApiWorkspcaeData> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<PatternPiecesFromApiWorkspcaeData>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun patternPiecesFromApiListToString(someObjects: List<PatternPiecesFromApiWorkspcaeData>): String {
        return Gson().toJson(someObjects)
    }
    @TypeConverter
    fun stringToNumberOfPieces(string: String?): NumberOfPiecesStorage? {
        return Gson().fromJson(string, NumberOfPiecesStorage::class.java)
    }

    @TypeConverter
    fun NumberOfPiecesToString(numberOfPieces: NumberOfPiecesStorage?): String {
        return Gson().toJson(numberOfPieces)
    }


    @TypeConverter
    fun instructionsListToString(someObjects: List<Instructions>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun descriptionImagesListToString(someObjects: List<DescriptionImages>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun patternPiecesListToString(someObjects: List<PatternPieces>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun selvagesListToString(someObjects: List<Selvages>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun workspaceItemsListToString(someObjects: List<WorkspaceItems>): String {
        return Gson().toJson(someObjects)
    }

}
