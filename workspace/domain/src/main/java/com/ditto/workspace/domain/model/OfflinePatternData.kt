package com.ditto.workspace.domain.model

data class OfflinePatternData(
    var tailornaovaDesignId: String,
    var selectedMannequinId:String?,
    var selectedMannequinName:String?,
    var selectedTab: String?,
    var status: String?,
    var numberOfCompletedPieces: NumberOfPieces?,
    var patternPiecesFromApi: List<PatternPieceSFCCAPI> = emptyList(),
    var garmetWorkspaceItemOfflines: MutableList<WorkspaceItemOfflineDomain> = ArrayList(),
    var liningWorkspaceItemOfflines: MutableList<WorkspaceItemOfflineDomain> = ArrayList(),
    var interfaceWorkspaceItemOfflines: MutableList<WorkspaceItemOfflineDomain> = ArrayList(),
    var otherWorkspaceItemOfflines: MutableList<WorkspaceItemOfflineDomain> = ArrayList(),
    var id: String,
    var patternName: String?,
    var description: String?,
    var patternType: String?,
    var numberOfPieces: NumberOfPieces?,
    var orderModificationDate: String?,
    var orderCreationDate: String?,
    var instructionFileName: String?,
    var instructionUrl: String?,
    var thumbnailImageUrl: String?,
    var thumbnailImageName: String?,
    var thumbnailEnlargedImageName: String?,
    var patternDescriptionImageUrl: String?,
    var selvages: List<SelvageDomain>? = emptyList(),
    var patternPiecesTailornova: List<PatternPieceDataDomain>? = emptyList(),
    var brand: String? = "",
    var size: String? = "",
    var gender: String? = "",
    var customization: Boolean? = false,
    var dressType: String? = "",
    var suitableFor: String? = "",
    var occasion: String? = "",
    var notes: String? = ""
)


data class WorkspaceItemOfflineDomain(
    val id: Int = 0,
    val patternPiecesId: Int = 0,
    val isCompleted: Boolean = false,
    val xcoordinate: Float = 0.0f,
    val ycoordinate: Float = 0.0f,
    val pivotX: Float = 0.0f,
    val pivotY: Float = 0.0f,
    val transformA: String? = "",
    val transformD: String? = "",
    val rotationAngle: Float = 0.0f,
    val isMirrorH: Boolean = false,
    val isMirrorV: Boolean = false,
    val showMirrorDialog: Boolean = false,
    val currentSplicedPieceNo: String? = "",
    var currentSplicedPieceRow:Int = 0,
    var currentSplicedPieceColumn:Int = 0
)

data class SelvageDomain(
    val fabricLength: String? = "",
    val id: Int,
    val imageName: String? = "",
    val imageUrl: String? = "",
    val tabCategory: String? = ""
)

data class PatternPieceDataDomain(
    val cutOnFold: Boolean,
    val cutQuantity: String = "",
    val pieceDescription: String? = "",
    val id: Int,
    val imageName: String? = "",
    val imageUrl: String? = "",
    val thumbnailImageUrl: String? = "",
    val thumbnailImageName: String? = "",
    val isSpliced: Boolean,
    val isMirrorOption: Boolean? = false,
    val pieceNumber: String? = "",
    val positionInTab: String? = "",
    val size: String? = "",
    //val spliceDirection: String? = "",
    val spliceScreenQuantity: String? = "",
    val splicedImages: List<SplicedImageDomain>?= emptyList(),
    val tabCategory: String? = "",
    val view: String? = "",
    val contrast: String? = ""
)

data class SplicedImageDomain(
    val column: Int,
    val designId: String? = "",
    val id: Int,
    val imageName: String? = "",
    val imageUrl: String? = "",
    val mapImageName: String? = "",
    val mapImageUrl: String? = "",
    val pieceId: Int,
    val row: Int
)