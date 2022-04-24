package com.camelcc.overcooked

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2

class SurveyFragment : Fragment() {
    private lateinit var surveyList: ViewPager2
    private lateinit var adapter: SurveyAdapter
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        surveyList = view.findViewById(R.id.survey_pager)
        adapter = SurveyAdapter(
            viewModel.items(),
            onPrev = {
                surveyList.setCurrentItem(surveyList.currentItem-1, true)
            }, onNext = {
                if (surveyList.currentItem+1 == viewModel.titles.size) {
                    viewModel.contents = adapter.contents
                    findNavController().navigate(R.id.action_surveyFragment_to_summaryFragment)
                } else {
                    surveyList.setCurrentItem(surveyList.currentItem+1, true)
                }
        })
        surveyList.adapter = adapter
    }
}