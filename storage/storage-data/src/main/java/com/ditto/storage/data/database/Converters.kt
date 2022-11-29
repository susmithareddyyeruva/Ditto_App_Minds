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
    fun stringToWorkspaceItemsList(data: String?): List<WorkspaceItems>? {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<WorkspaceItems>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun stringToWorkspaceItemAPIList(data: String?): List<WorkspaceItemOffline> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<WorkspaceItemOffline>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun workspaceItemAPIListToString(someObjects: List<WorkspaceItemOffline>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun stringToPatternPiecesFromApiList(data: String?): List<PatternPiecesOffline> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<PatternPiecesOffline>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun patternPiecesFromApiListToString(someObjects: List<PatternPiecesOffline>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun stringToNumberOfPieces(string: String?): NumberOfCompletedPiecesOffline? {
        return Gson().fromJson(string, NumberOfCompletedPiecesOffline::class.java)
    }

    @TypeConverter
    fun numberOfPiecesToString(numberOfCompletedPieces: NumberOfCompletedPiecesOffline?): String {
        return Gson().toJson(numberOfCompletedPieces)
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
    fun workspaceItemsListToString(someObjects: List<WorkspaceItems>?): String? {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun stringToSelvageDataList(data: String?): List<SelvageData> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<SelvageData>>() {
        }.type
        return Gson().fromJson(data, listType)
    }


    @TypeConverter
    fun selvageDataListToString(someObjects: List<SelvageData>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun mannequinDataListToString(someObjects: List<MannequinData>): String {
        return Gson().toJson(someObjects)
    }


    @TypeConverter
    fun stringToMannequinDataList(data: String?): List<MannequinData> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<MannequinData>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun stringToPatternPieceDataList(data: String?): List<PatternPieceData> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<PatternPieceData>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun patternPieceDataListToString(someObjects: List<PatternPieceData>): String {
        return Gson().toJson(someObjects)
    }

    @TypeConverter
    fun stringToYardageDetailsList(data: String?): List<String> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<String>>() {
        }.type
        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun yardageDetailsListToString(someObjects: List<String>): String {
        return Gson().toJson(someObjects)
    }
}
