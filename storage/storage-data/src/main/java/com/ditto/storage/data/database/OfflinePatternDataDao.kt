package com.ditto.storage.data.database

import android.util.Log
import androidx.room.*
import com.ditto.storage.data.model.*

@Dao
abstract class OfflinePatternDataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertOfflinePatternData(offlinePatterns: OfflinePatterns): Long


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @JvmSuppressWildcards
    abstract fun insertOfflinePatternDataList(offlinePatterns: List<OfflinePatterns>)

    @Query("SELECT * FROM offline_pattern_data where patternType ='Trial' or custId = :custId")//todo need to fetch respective of id i.e custmoer id
    abstract fun getAllPatterns(custId: String?): List<OfflinePatterns>

    @Query("SELECT * FROM offline_pattern_data WHERE designId = :id and custId = :custId")
    abstract fun getTailernovaDataByID(id: String, custId: String?): OfflinePatterns

    @Query("SELECT * FROM offline_pattern_data WHERE designId = :id")
    abstract fun getTailernovaDataByIDTrial(id: String): OfflinePatterns

    @Query("SELECT * FROM offline_pattern_data WHERE patternType = :type or custId = :custId")
    abstract fun getListOfTrialPattern(type: String, custId: String?): List<OfflinePatterns>

    @Query("UPDATE offline_pattern_data SET selectedTab = :selectedTab , numberOfCompletedPiece = :numberOfCompletedPiece , patternPieces = :patternPieces , garmetWorkspaceItems = :garmetWorkspaceItems , liningWorkspaceItems = :liningWorkspaceItems ,interfaceWorkspaceItems = :interfaceWorkspaceItems WHERE tailornaovaDesignId = :tailornaovaDesignId and custId = :custId")
    abstract fun updateOfflinePatternData(
        custId: String?,
        tailornaovaDesignId: String?,
        selectedTab: String?,
        numberOfCompletedPiece: NumberOfCompletedPiecesOffline?,
        patternPieces: List<PatternPiecesOffline>?,
        garmetWorkspaceItems: MutableList<WorkspaceItemOffline>,
        liningWorkspaceItems: MutableList<WorkspaceItemOffline>,
        interfaceWorkspaceItems: MutableList<WorkspaceItemOffline>
    ): Int

    //if patternType!= trial >> delete it (keeping trial patterns only)
    @Query("DELETE from offline_pattern_data where patternType  != :patternType and custId == :custId  and designId != :designId")// for specific user
    abstract fun deletePatternsExceptTrial(patternType: String, custId: String?, designId: String)

    //    Delete pattern in offline pttern where custId != nivedh.custID and custId != 0
    @Query("DELETE from offline_pattern_data where custId != :custId  and custId != 0")
    abstract fun deleteOtherUserRecord(custId: String?)

    // new pattern >>insert 1 update -1
    // already there pattern >>insert -1 update 1
    @Transaction
    open fun upsert(obj: OfflinePatterns):Int {
        val id: Long = insertOfflinePatternData(obj)
        Log.d("offlinePatternDataDao", "insertTailornovaDetailsToDB insert  >>>>> $id")
        if (id == (-1).toLong()) {
            val i = updateTailornovaOfflineData(
                obj.custId,
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
                obj.selvages,
                obj.selectedMannequinId,
                obj.selectedMannequinName,
                obj.mannequin
            )
            Log.d("offlinePatternDataDao", "insertTailornovaDetailsToDB update >>>>>>> $i")
            return i
        }else{
            return id.toInt()
        }
    }

    @Query("UPDATE offline_pattern_data SET custId= :custId,patternName= :patternName, description= :description, patternType= :patternType, totalNumberOfPieces= :numberOfPieces, orderModificationDate= :orderModificationDate, orderCreationDate= :orderCreationDate,instructionFileName= :instructionFileName,instructionUrl= :instructionUrl,thumbnailImageUrl= :thumbnailImageUrl,thumbnailImageName= :thumbnailImageName,thumbnailEnlargedImageName= :thumbnailEnlargedImageName,patternDescriptionImageUrl= :patternDescriptionImageUrl,customization=:customization,brand=:brand,size=:size,gender=:gender,dressType=:dressType,suitableFor=:suitableFor,occasion=:occasion,selvages=:selvages,patternPiecesTailornova=:patternPiecesFromTailornova,selectedMannequinId=:selectedMannequinId,selectedMannequinName=:selectedMannequinName,mannequinArray=:mannequin WHERE designId= :designId")
    abstract fun updateTailornovaOfflineData(
        custId: String?,
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
        selvages: List<SelvageData>?,
        selectedMannequinId: String?,
        selectedMannequinName: String?,
        mannequin: List<MannequinData>?/*,
        selectedTab: String?,
        status: String,
        numberOfCompletedPieces: NumberOfCompletedPiecesOffline?,
        patternPiecesFromApi: List<PatternPiecesOffline>,
        garmetWorkspaceItemOfflines: MutableList<WorkspaceItemOffline>,
        liningWorkspaceItemOfflines: MutableList<WorkspaceItemOffline>,
        interfaceWorkspaceItemOfflines: MutableList<WorkspaceItemOffline>*/

    ): Int

    @Query("UPDATE offline_pattern_data SET custId= :custId,patternName= :patternName, description= :description, patternType= :patternType, totalNumberOfPieces= :numberOfPieces, orderModificationDate= :orderModificationDate, orderCreationDate= :orderCreationDate,instructionFileName= :instructionFileName,instructionUrl= :instructionUrl,thumbnailImageUrl= :thumbnailImageUrl,thumbnailImageName= :thumbnailImageName,thumbnailEnlargedImageName= :thumbnailEnlargedImageName,patternDescriptionImageUrl= :patternDescriptionImageUrl,customization=:customization,brand=:brand,size=:size,gender=:gender,dressType=:dressType,suitableFor=:suitableFor,occasion=:occasion,selvages=:selvages,patternPiecesTailornova=:patternPiecesFromTailornova,selectedTab=:selectedTab,status=:status, numberOfCompletedPiece=:numberOfCompletedPieces, patternPieces= :patternPiecesFromApi,garmetWorkspaceItems=:garmetWorkspaceItemOfflines,liningWorkspaceItems=:liningWorkspaceItemOfflines,interfaceWorkspaceItems= :interfaceWorkspaceItemOfflines,selectedMannequinId=:selectedMannequinId,selectedMannequinName=:selectedMannequinName,mannequinArray=:mannequin WHERE designId= :designId ")
    abstract fun updateTailornovaTrailForPatternDifferentUser(
        custId: String?,
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
        selvages: List<SelvageData>?,
        selectedTab: String?,
        status: String?,
        numberOfCompletedPieces: NumberOfCompletedPiecesOffline?,
        patternPiecesFromApi: List<PatternPiecesOffline>,
        garmetWorkspaceItemOfflines: MutableList<WorkspaceItemOffline>,
        liningWorkspaceItemOfflines: MutableList<WorkspaceItemOffline>,
        interfaceWorkspaceItemOfflines: MutableList<WorkspaceItemOffline>,
        selectedMannequinId: String?,
        selectedMannequinName: String?,
        mannequin: List<MannequinData>?
    ): Int

    @Transaction
    open fun upsertList(trialPatternList: List<OfflinePatterns>, custID: String?) {
        for (obj in trialPatternList) {
            val id: Long = insertOfflinePatternData(obj)
            val objInDB = getTailernovaDataByIDTrial(obj.designId) // obj2 cust id inside db
            Log.d("offlinePatternDataDao", "upsertList insert $id")
            if (id == (-1).toLong()) {
                //todo need to updaate the other like garment and all
                if (obj.custId != objInDB.custId) {
                    //1. if logged in custId is != Db custID we will insert garment lining
                    val i = updateTailornovaTrailForPatternDifferentUser(
                        obj.custId,
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
                        obj.selvages,
                        obj.selectedTab,
                        obj.status,
                        obj.numberOfCompletedPieces,
                        obj.patternPiecesFromApi,
                        obj.garmetWorkspaceItemOfflines,
                        obj.liningWorkspaceItemOfflines,
                        obj.interfaceWorkspaceItemOfflines,
                        obj.selectedMannequinId,
                        obj.selectedMannequinName,
                        obj.mannequin
                    )
                    Log.d("offlinePatternDataDao", "upsertList update trial if  different user $i")


                } else {
                    val i = updateTailornovaOfflineData(
                        obj.custId,
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
                        obj.selvages,
                        obj.selectedMannequinId,
                        obj.selectedMannequinName,
                        obj.mannequin
                        /*obj.selectedTab,
                obj.garmetWorkspaceItemOfflines,
                obj.liningWorkspaceItemOfflines,
                obj.interfaceWorkspaceItemOfflines,
                obj.status,
                obj.numberOfCompletedPieces,
                obj.patternPiecesFromApi*/
                    )
                    Log.d("offlinePatternDataDao", "upsertList update trial pattern  $i")
                }
            }
        }
    }

}