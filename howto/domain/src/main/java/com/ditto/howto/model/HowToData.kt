package com.ditto.howto.model

/**
 * Created by Sesha on  15/08/2020.
 * Model class representing  List of Instructions Data for "How To"
 */
data class HowToData(
    var id1: Int = 0,
    var title1: String = "",
    var description1: String = "",
    var imagePath1: String = "",
    var videopath1: String = "",
    var tutorialPdfUrl1: String = "",
    var instructions1: List<HowToModel> = emptyList()
)