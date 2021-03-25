package trace.mylibrary.domain.model

import com.ditto.storage.data.model.DescriptionImages
import com.ditto.storage.data.model.PatternPieces
import com.ditto.storage.data.model.Selvages
import com.ditto.storage.data.model.WorkspaceItems

data class MyLibraryData(
    val id: Int,
    var patternName: String,
    val description: String,
    val totalPieces: Int,
    val completedPieces: Int,
    val status: String,
    var thumbnailImagePath: String,
    var descriptionImages: List<DescriptionImages>,
    var selvages: List<Selvages>,
    var patternPieces: List<PatternPieces>,
    var workspaceItems: MutableList<WorkspaceItems>? = ArrayList()
)