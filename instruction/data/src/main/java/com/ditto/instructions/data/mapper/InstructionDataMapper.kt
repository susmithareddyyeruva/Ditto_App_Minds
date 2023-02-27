package com.ditto.instructions.data.mapper

/**
 * Created by Vishnu A V on  03/08/2020.
 * Mapper class for mapping model class with DB values
 */
import com.ditto.instructions.domain.model.InstructionModel
import com.ditto.instructions.domain.model.InstructionsData
import com.ditto.storage.data.model.Instructions
import com.ditto.storage.data.model.OnBoarding


internal fun OnBoarding.toDomain(): InstructionsData {
    return InstructionsData(

        id = this.id,
        title = this.title,
        description = this.description,
        imagePath = this.imagepath,
        videoPath = this.videoPath,
        tutorialPdfUrl = this.tutorialPdfUrl,
        instructions = this.instructions.map { it.toDomain() }
    )

}

internal fun Instructions.toDomain(): InstructionModel {
    return InstructionModel(
        id = this.id,
        title = this.title,
        description = this.description,
        imagePath = this.imagePath,
        videoPath = this.videoPath,
        tutorialPdfUrl = this.tutorialPdfUrl,
        instructions = this.instructions.map { it.toDomain() }

    )
}