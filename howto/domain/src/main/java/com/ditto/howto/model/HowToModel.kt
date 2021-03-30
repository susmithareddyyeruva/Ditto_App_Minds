package com.ditto.howto.model

/**
 * Created by Sesha  on  15/08/2020.
 * Model class representing  Inner List of Instructions Data for "How To"
 */
data class HowToModel(
    var id: Int = 0,
    var title: String = "",
    var description: String = "",
    var imagePath: String? = "",
    var videoPath: String? = "",
    var instructions: List<HowToData> = emptyList()
)