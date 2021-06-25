package com.ditto.menuitems_ui.faq.ui.json

import android.content.Context
import com.ditto.menuitems_ui.faq.ui.models.FAQGlossaryResponse
import com.ditto.menuitems_ui.faq.ui.models.FAQModel
import com.google.gson.Gson
import org.json.JSONObject


open class JsonHelper(private var context: Context) {
    private var newspaperList: MutableList<FAQModel>? = null
    private var glossaryList: MutableList<FAQModel>? = null

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

    open fun getFAQDataMain(): FAQGlossaryResponse? {
        if (newspaperList == null)
            newspaperList = ArrayList()
        if (glossaryList == null)
            glossaryList = ArrayList()

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

            val jsonArrayGlossary = jsonObject.getJSONArray("Glossary")
            val v = jsonArrayGlossary.length()

            for (i in 0 until v) {
                val tempJsonObject = jsonArrayGlossary.getJSONObject(i).toString()
                val gson = Gson()
                val newsPaper = gson.fromJson<FAQModel>(tempJsonObject, FAQModel::class.java)
                glossaryList?.add(newsPaper)
            }
            val faqGlossaryResponse = FAQGlossaryResponse()
            faqGlossaryResponse.fAQ = newspaperList
            faqGlossaryResponse.glossary = glossaryList

            return faqGlossaryResponse


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