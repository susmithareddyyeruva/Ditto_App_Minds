package com.ditto.workspace.ui.util

import android.view.View
import com.ditto.workspace.domain.model.WorkspaceItems

interface DraggableListener {
    fun onPositionChanged(view: View,  workspaceItem: WorkspaceItems?)
    fun onTouch(view: View,  workspaceItem: WorkspaceItems?)
    fun onSelectAll()
    fun onDragOut(view: View,  workspaceItem: WorkspaceItems?)
    fun onDragCompleted()
    fun onOverlapped(showToast: Boolean)
    fun onProjectWorkspace()
}