package io.sakurasou

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.sakurasou.common.ApiResult
import io.sakurasou.entity.MajorPlan
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

/**
 * @author ShiinaKin
 * 2024/7/8 16:51
 */
class TestDeserialization {
    @Test
    fun testDeserializeMajorPlan() {
        val json = """
            {
              "result": 0,
              "message": null,
              "data": {
                "id": 9518,
                "programId": 1230,
                "beginSemesterId": 52,
                "currentTerm": "5",
                "term": "TERM_5",
                "programNameZh": "2022软件工程",
                "programNameEn": null,
                "cultivateTypeZh": "主修",
                "cultivateTypeEn": "Major",
                "courseModules": [
                  {
                    "id": 9518,
                    "programId": 1230,
                    "typeNameZh": "通识限选",
                    "typeNameEn": null,
                    "programNameZh": "2022软件工程",
                    "programNameEn": null,
                    "beginSemesterId": 52,
                    "parentId": 7246,
                    "index": 0,
                    "reference": false,
                    "requireCredits": null,
                    "requireModuleNum": null,
                    "children": [
                      {
                        "id": 10211,
                        "programId": 1230,
                        "typeNameZh": "“四史”课程",
                        "typeNameEn": null,
                        "programNameZh": "2022软件工程",
                        "programNameEn": null,
                        "beginSemesterId": 52,
                        "parentId": 9518,
                        "index": 0,
                        "reference": false,
                        "requireCredits": null,
                        "requireModuleNum": null,
                        "children": [
                          {
                            "id": 13177,
                            "programId": 1230,
                            "typeNameZh": "“四史”",
                            "typeNameEn": null,
                            "programNameZh": "2022软件工程",
                            "programNameEn": null,
                            "beginSemesterId": 52,
                            "parentId": 10211,
                            "index": 0,
                            "reference": true,
                            "requireCredits": null,
                            "requireModuleNum": null,
                            "children": [],
                            "planCourses": [
                              {
                                "id": 10311,
                                "nameZh": "史",
                                "nameEn": "History of .",
                                "code": "0545364",
                                "credits": 1.0,
                                "flags": ["MARXIST", "PUBLIC"],
                                "department": {
                                  "id": 17,
                                  "nameZh": "鹿院",
                                  "nameEn": null,
                                  "code": "05",
                                  "telephone": null
                                },
                                "planCourseId": 84542,
                                "compulsory": false,
                                "suggestTermsText": "1,2,3,4,5,6,7,8",
                                "programNameZh": null,
                                "programNameEn": null,
                                "courseModuleId": 10763,
                                "programId": 1230,
                                "planCourseSuggestTermsText": "",
                                "marks": [],
                                "flag": "MARXIST,PUBLIC",
                                "substituteCourseProfileVms": [],
                                "open": 1
                              },
                              {
                                "id": 10313,
                                "nameZh": "新史",
                                "nameEn": "History of .",
                                "code": "654161",
                                "credits": 1.0,
                                "flags": ["MARXIST", "PUBLIC"],
                                "department": {
                                  "id": 17,
                                  "nameZh": "鹿院",
                                  "nameEn": null,
                                  "code": "05",
                                  "telephone": null
                                },
                                "planCourseId": 84543,
                                "compulsory": false,
                                "suggestTermsText": "1,2,3,4,5,6,7,8",
                                "programNameZh": null,
                                "programNameEn": null,
                                "courseModuleId": 10763,
                                "programId": 1230,
                                "planCourseSuggestTermsText": "",
                                "marks": [],
                                "flag": "MARXIST,PUBLIC",
                                "substituteCourseProfileVms": [],
                                "open": 1
                              }
                            ]
                          }
                        ],
                        "planCourses": []
                      }
                    ]
                  }
                ]
              }
            }
        """.trimIndent()
        val jsonMapper = JsonMapper().registerModules(kotlinModule(), JavaTimeModule())
        val result = jsonMapper.readValue<ApiResult<MajorPlan>>(json)
        val majorPlan = result.data
        assertNotNull(majorPlan)
    }
}