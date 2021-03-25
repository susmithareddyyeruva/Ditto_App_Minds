package com.ditto.instructions.domain.model

/**
 * Created by Vishnu A V on  03/08/2020.
 * Model class representing Inner List of Instructions Data for OnBoarding
 */
data class InstructionModel(
    var id: Int = 0,
    var title: String = "",
    var description: String = "",
    var imagePath: String? = "",
    var videoPath: String? = "",
    var instructions: List<InstructionsData> = emptyList()
)