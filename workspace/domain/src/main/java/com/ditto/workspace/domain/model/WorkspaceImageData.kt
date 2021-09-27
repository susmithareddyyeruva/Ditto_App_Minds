package com.ditto.workspace.domain.model

import android.graphics.Bitmap

data class WorkspaceImageData(
    var bitmap: Bitmap?,
    var workspaceItem: WorkspaceItems?,
    var scaleFactor: Double,
    var showProjection: Boolean,
    var isDraggedPiece: Boolean
)