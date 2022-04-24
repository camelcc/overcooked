package com.camelcc.overcooked

import androidx.lifecycle.ViewModel

data class SurveyItem(val title: String, val content: String)

class MainViewModel : ViewModel() {
    val titles: List<String>
    var contents = listOf<String>()

    init {
        titles = (1..3).map { "title$it" }.toList()
        contents = (1..3).map { "" }.toList()
    }

    fun items(): List<SurveyItem> {
        val res = mutableListOf<SurveyItem>()
        for (i in titles.indices) {
            res.add(SurveyItem(title = titles[i], content = contents[i]))
        }
        return res
    }
}