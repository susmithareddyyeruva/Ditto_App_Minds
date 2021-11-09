package com.ditto.storage.data.database

import android.util.Log
import androidx.room.*
import com.ditto.storage.data.model.*

@Dao
abstract class OfflinePatternDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertOfflinePatternData(offlinePatterns: OfflinePatterns): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertOfflinePatternDataList(offlinePatterns: List<OfflinePatterns>)

    @Query("SELECT * FROM offline_pattern_data")//todo need to fetch respective of id i.e custmoer id
    abstract fun getTailernovaData(): List<OfflinePatterns>

    @Query("SELECT * FROM offline_pattern_data WHERE tailornaovaDesignId = :id")
    abstract fun getTailernovaDataByID(id: String): OfflinePatterns


    @Query("SELECT * FROM offline_pattern_data WHERE patternType = :type")
    abstract fun getListOfTrialPattern(type: String): List<OfflinePatterns>

    @Query("UPDATE offline_pattern_data SET selectedTab = :selectedTab , status = :status , numberOfCompletedPiece = :numberOfCompletedPiece , patternPieces = :patternPieces , garmetWorkspaceItems = :garmetWorkspaceItems , liningWorkspaceItems = :liningWorkspaceItems ,interfaceWorkspaceItems = :interfaceWorkspaceItems WHERE tailornaovaDesignId = :tailornaovaDesignId")
    abstract fun updateOfflinePatternData(
        tailornaovaDesignId: String?,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPiece: NumberOfCompletedPiecesOffline?,
        patternPieces: List<PatternPiecesOffline>?,
        garmetWorkspaceItems: MutableList<WorkspaceItemOffline>,
        liningWorkspaceItems: MutableList<WorkspaceItemOffline>,
        interfaceWorkspaceItems: MutableList<WorkspaceItemOffline>
    ): Int

    //if patternType!= trial >> delete it (keeping trial patterns only)
    @Query("DELETE from offline_pattern_data where patternType  != :patternType and custId == :custId  and designId != :designId")// for specific user
    abstract fun deletePatternsExceptTrial(patternType: String, custId: String?,designId: String)

    @Transaction
    open fun upsert(obj: OfflinePatterns) {
        val id: Long = insertOfflinePatternData(obj)
        Log.d("offlinePatternDataDao", "insert $id")
        if (id == -1L) {
            val i = updateTailornovaOfflineData(
                obj.designId,
                obj.patternName,
                obj.description,
                obj.patternType,
                obj.numberOfPieces,
                obj.orderModificationDate,
                obj.orderCreationDate,
                obj.instructionFileName,
                obj.instructionUrl,
                obj.thumbnailImageUrl,
                obj.thumbnailImageName,
                obj.thumbnailEnlargedImageName,
                obj.brand,
                obj.size,
                obj.patternPiecesFromTailornova,
                obj.gender,
                obj.dressType,
                obj.suitableFor,
                obj.occasion,
                obj.patternDescriptionImageUrl,
                obj.customization,
                obj.selvages
            )
            Log.d("offlinePatternDataDao", "update $i")
        }
    }

    @Query("UPDATE offline_pattern_data SET patternName= :patternName, description= :description, patternType= :patternType, totalNumberOfPieces= :numberOfPieces, orderModificationDate= :orderModificationDate, orderCreationDate= :orderCreationDate,instructionFileName= :instructionFileName,instructionUrl= :instructionUrl,thumbnailImageUrl= :thumbnailImageUrl,thumbnailImageName= :thumbnailImageName,thumbnailEnlargedImageName= :thumbnailEnlargedImageName,patternDescriptionImageUrl= :patternDescriptionImageUrl,customization=:customization,brand=:brand,size=:size,gender=:gender,dressType=:dressType,suitableFor=:suitableFor,occasion=:occasion,selvages=:selvages,patternPiecesTailornova=:patternPiecesFromTailornova WHERE designId= :designId")
    abstract fun updateTailornovaOfflineData(
        designId: String,
        patternName: String?,
        description: String?,
        patternType: String?,
        numberOfPieces: NumberOfCompletedPiecesOffline?,
        orderModificationDate: String?,
        orderCreationDate: String?,
        instructionFileName: String?,
        instructionUrl: String?,
        thumbnailImageUrl: String?,
        thumbnailImageName: String?,
        thumbnailEnlargedImageName: String?,
        brand: String?,
        size: String?,
        patternPiecesFromTailornova: List<PatternPieceData>?,
        gender: String?,
        dressType: String?,
        suitableFor: String?,
        occasion: String?,
        patternDescriptionImageUrl: String?,
        customization: Boolean?,
        selvages: List<SelvageData>?
    ): Int
}