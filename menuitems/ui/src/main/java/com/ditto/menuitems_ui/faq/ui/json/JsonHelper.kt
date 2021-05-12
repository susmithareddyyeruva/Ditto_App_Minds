package com.ditto.menuitems_ui.faq.ui.json

import android.content.Context
import android.util.Log
import com.ditto.menuitems_ui.faq.ui.models.FAQModel
import org.json.JSONObject
import com.google.gson.Gson


open class JsonHelper(private var context: Context) {
    private var newspaperList: MutableList<FAQModel>? = null

    open fun getFAQData(): List<FAQModel>? {
        if (newspaperList == null)
            newspaperList = ArrayList()

        try {
            val jsonObject = JSONObject(getJSONFromAssets("sample.json"))
            val jsonArray = jsonObject.getJSONArray("FAQ")
            val k = jsonArray.length()

            for (i in 0 until k) {
                val tempJsonObject = jsonArray.getJSONObject(i).toString()
                val gson = Gson()
                val newsPaper = gson.fromJson<FAQModel>(tempJsonObject, FAQModel::class.java)
                newspaperList?.add(newsPaper)
            }
            return newspaperList
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getJSONFromAssets(fileName: String): String? {
        val json: String
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return json
    }
}