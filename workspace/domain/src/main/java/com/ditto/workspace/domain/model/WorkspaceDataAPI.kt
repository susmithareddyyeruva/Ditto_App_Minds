package com.ditto.workspace.domain.model
data class WorkspaceDataAPI (

    //getWorkspace data response
    var tailornaovaDesignId: String? = "",
    var selectedTab: String? ="",
    var status:String="",
    var numberOfPieces: NumberOfPieces?,
    var patternPiecesFromApi: List<PatternPieceDomain> = emptyList(),
    var garmetWorkspaceItems: MutableList<WorkspaceItemDomain>? = ArrayList(),
    var liningWorkspaceItems: MutableList<WorkspaceItemDomain>? = ArrayList(),
    var interfaceWorkspaceItems: MutableList<WorkspaceItemDomain>? = ArrayList()

    // tailernova response
    /*var id: Int = 0,
    var name:String="",
    var description:String="",
    var numberOfPieces:NumberOfPieces,
    var orderModificationDate:Date,
    var orderCreationDate:Date,
    var instructionFileName:String="",
    var instructionUrl:String="",
    var thumbnailImageUrl:String="",
    var thumbnailImageName:String="",
    var thumbnailEnlargedImageName:String="",
    var patternDescriptionImageUrl:String="",
    var selvages: List<SelvagesApi> = emptyList(),
    var patternPieces: List<PatternPiecesAPI> = emptyList(),
    var brand:String="",
    var size:String="",
    var gender:String="",
    var customization:Boolean=false,
    var dressType:String="",
    var suitableFor:String="",
    var occasion:String=""*/

)