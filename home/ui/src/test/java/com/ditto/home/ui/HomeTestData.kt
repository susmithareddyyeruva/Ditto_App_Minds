package com.ditto.home.ui

import com.ditto.data.mapper.toDomain
import com.ditto.data.model.MyLibraryResult
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.mylibrary.domain.model.PatternIdData
import com.ditto.mylibrary.domain.model.TailornovaTrialPatternResponse
import com.google.gson.Gson

fun mapResponseToModel(responseTypeError: Boolean): List<PatternIdData> {
    val gson = Gson()
    val result = gson.fromJson(dummyResponse, TailornovaTrialPatternResponse::class.java)
    return result.trial
}

const val dummyResponse = """
{    
  "trial": [
    {
      "orderCreationDate": "",
      "instructionFileName": "Instruction.pdf",
      "instructionUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Instruction.pdf",
      "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Model+A1.png",
      "thumbnailImageName": "Model A1.png",
      "thumbnailEnlargedImageName": "Model A.png",
      "patternDescriptionImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Model+A.png",
      "selvages": [
        {
          "id": 1,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Garment_45_1.convert%403x.png",
          "imageName": "Garment_45_1.convert@3x.png",
          "tabCategory": "Garment",
          "fabricLength": "45"
        },
        {
          "id": 2,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Garment_45_3.convert%403x.png",
          "imageName": "Garment_45_3.convert@3x.png",
          "tabCategory": "Garment",
          "fabricLength": "45"
        },
        {
          "id": 3,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Garment_55_2.convert%403x.png",
          "imageName": "Garment_55_2.convert@3x.png",
          "tabCategory": "Garment",
          "fabricLength": "55"
        },
        {
          "id": 4,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Garment_60_2.convert%403x.png",
          "imageName": "Garment_60_2.convert@3x.png",
          "tabCategory": "Garment",
          "fabricLength": "60"
        },
        {
          "id": 5,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Garment_60_3.convert%403x.png",
          "imageName": "Garment_60_3.convert@3x.png",
          "tabCategory": "Garment",
          "fabricLength": "60"
        },
        {
          "id": 6,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Interfacing_18_4.convert%403x.png",
          "imageName": "Interfacing_18_4.convert@3x.png",
          "tabCategory": "Interfacing",
          "fabricLength": "18"
        },
        {
          "id": 6,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Interfacing_20_4.convert%403x.png",
          "imageName": "Interfacing_20_4.convert@3x.png",
          "tabCategory": "Interfacing",
          "fabricLength": "20"
        },
        {
          "id": 8,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/Lining_45_5.convert%403x.png",
          "imageName": "Lining_45_5.convert@3x.png",
          "tabCategory": "lining",
          "fabricLength": "45"
        }
      ],
      "patternPieces": [
        {
          "id": 1,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-1.original%401x.png",
          "imageName": "A_detail-1.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-1.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-1.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "1",
          "pieceDescription": "Bottom",
          "positionInTab": "1",
          "tabCategory": "Garment",
          "cutQuantity": "Cut 2",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 2,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-1.original%401x.png",
          "imageName": "A_detail-1.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-1.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-1.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "1",
          "pieceDescription": "Bottom",
          "positionInTab": "2",
          "tabCategory": "Interfacing",
          "cutQuantity": "Cut 2",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 3,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-2.original%401x.png",
          "imageName": "A_detail-2.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-2.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-2.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "2",
          "pieceDescription": "Front and Back",
          "positionInTab": "3",
          "tabCategory": "Garment",
          "cutQuantity": "Cut 2",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 4,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-2.original%401x.png",
          "imageName": "A_detail-2.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-2.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-2.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "2",
          "pieceDescription": "Front and Back",
          "positionInTab": "4",
          "tabCategory": "Interfacing",
          "cutQuantity": "Cut 2",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 5,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-3.original%401x.png",
          "imageName": "A_detail-3.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-3.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-3.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "3",
          "pieceDescription": "Facing",
          "positionInTab": "5",
          "tabCategory": "Garment",
          "cutQuantity": "Cut 2",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 6,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-3.original%401x.png",
          "imageName": "A_detail-3.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-3.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-3.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "3",
          "pieceDescription": "Facing",
          "positionInTab": "6",
          "tabCategory": "Interfacing",
          "cutQuantity": "Cut 2",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 7,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-4.original%401x.png",
          "imageName": "A_detail-4.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-4.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-4.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "4",
          "pieceDescription": "Front and Back Lining",
          "positionInTab": "7",
          "tabCategory": "Garment",
          "cutQuantity": "Cut 2",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 8,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-5.original%401x.png",
          "imageName": "A_detail-5.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-5.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-5.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "5",
          "pieceDescription": "Handle",
          "positionInTab": "8",
          "tabCategory": "Garment",
          "cutQuantity": "Cut 2",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 9,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-5.original%401x.png",
          "imageName": "A_detail-5.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-5.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-5.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "5",
          "pieceDescription": "Handle",
          "positionInTab": "9",
          "tabCategory": "Interfacing",
          "cutQuantity": "Cut 2",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 10,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-6.original%401x.png",
          "imageName": "A_detail-6.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-6.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-6.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "6",
          "pieceDescription": "Cover",
          "positionInTab": "10",
          "tabCategory": "Lining",
          "cutQuantity": "Cut 1",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        },
        {
          "id": 11,
          "imageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-7.original%401x.png",
          "imageName": "A_detail-7.original@1x.png",
          "thumbnailImageUrl": "https://splicing-app.s3.us-east-2.amazonaws.com/Trial/McCalls+M7611/A_detail-7.thumbnail%403x.png",
          "thumbnailImageName": "A_detail-7.thumbnail@3x.png",
          "view": "View A",
          "pieceNumber": "7",
          "pieceDescription": "Guide",
          "positionInTab": "11",
          "tabCategory": "Garment",
          "cutQuantity": "Cut 1",
          "isSpliced": false,
          "mirrorOption": true,
          "spliceDirection": "noSplicing",
          "spliceScreenQuantity": "NA",
          "cutOnFold": false,
          "splicedImages": []
        }
      ],
      "brand": "McCall's",
      "size": "One size",
      "gender": "female",
      "customization": false,
      "type": "bag",
      "season": "unknown",
      "suitableFor": "adult",
      "occasion": "casual",
      "designId": "trial_1002",
      "patternName": "M7611",
      "description": "Some description",
      "patternType": "Trial",
      "numberOfPieces": {
        "garment": 11,
        "lining": 1,
        "interface": 8
      }
    }
  ]
}
"""

fun mapResponseToModelFilterApi(): MyLibraryDetailsDomain {
    val gson = Gson()
    val result = gson.fromJson(filterApidummyResponse, MyLibraryResult::class.java)
    return result.toDomain()

}

const val filterApidummyResponse = """
    {
    "action": "TraceAppMyLibrary-Shows",
    "queryString": "",
    "locale": "default",
    "prod": [
        {
            "isOwned": false,
            "ID": "trial_1002",
            "image": "https://development.dittopatterns.com/dw/image/v2/BFKQ_DEV/on/demandware.static/-/Sites-ditto-navigation-catalog-en/default/dw91891aab/images/medium/M7611.JPG?sw=350&sh=350&sm=fit",
            "name": "M7611",
            "description": "Lined Tote Bag with Handle",
            "customization": true,
            "type": [],
            "seasons": [],
            "orderNo": "111",
            "occasion": "",
            "suitableFor": "",
            "size": "One size",
            "gender": "Women",
            "brand": "McCall's",
            "tailornovaDesignId": "trial_1002",
            "purchasedSizeId": "",
            "patternType": "Trial",
            "tailornovaDesignName": "",
            "subscriptionExpiryDate": "",
            "creationDate": "2021-12-16T06:31:15.000Z",
            "dateOfModification": "2022-01-27T09:27:35.000Z",
            "isFavourite": false,
            "status": "TRIAL"
        }
    ],
    "totalPatternCount": 1,
    "totalPageCount": 1,
    "currentPageId": 1,
    "filter": {
        "brand": [
            "McCall's"
        ],
        "gender": [
            "Women"
        ],
        "size": [
            "One size"
        ],
        "seasons": [
            "One size"
        ],
        "productTypes": [
            "undefined"
        ]
    }
}
"""