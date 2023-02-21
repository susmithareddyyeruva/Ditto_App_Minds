package com.ditto.storage.data.model

/**
 * Model/Entity class representing Instructions for OnBoarding
 */
data class Instructions(
    var id: Int = 0,
    var title: String = "",
    var description: String = "",
    var imagePath: String = "",
    var videoPath: String = "",
    var tutorialPdfUrl: String = "",
    var instructions: List<OnBoarding> = emptyList()
    )