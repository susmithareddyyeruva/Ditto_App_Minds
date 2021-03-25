package com.ditto.howto_data.model.mapper

import com.ditto.howto_domain.model.HowToData
import com.ditto.howto_domain.model.HowToModel
import com.ditto.storage.data.model.Instructions
import com.ditto.storage.data.model.OnBoarding

/**
 * Created by Sesha on  15/08/2020.
 * Mapper class for mapping model class with DB values
 */
internal fun OnBoarding.toDomain():HowToData{
    return HowToData(
        id1 = this.id,
        title1 = this.title,
        description1 = this.description,
        imagePath1 =  this.imagepath ,
        videopath1 = this.videoPath,
        instructions1 = this.instructions.map { it.toDomain() }
    )
}

internal fun Instructions.toDomain():HowToModel{
    return HowToModel(
        id = this.id,
        title = this.title,
        description = this.description,
        imagePath =  this.imagePath,
        videoPath = this.videoPath,
        instructions = this.instructions.map { it.toDomain() }
    )
}

/*internal fun Instructions.toDomain():HowToData{
    return HowToData(
        id1 = this.id,
        title1 = this.title,
        description1 = this.description,
        imagePath1 =  this.imagePath,
        instructions1 = this.instructions.map { it.toDomain() }
    )
}*/

/*

internal fun Tabcontent.toDomain(): HowToModel {
    return HowToModel(
        id = this.id_tc,
        title = this.title_tc,
        description = this.description_tc,
        imagePath=  this.imagePath_tc,
        videoPath = this.videoPath_tc

    )
}
*/


