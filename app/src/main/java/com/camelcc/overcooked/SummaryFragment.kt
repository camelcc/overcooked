package com.camelcc.overcooked

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SummaryFragment : Fragment() {
    private lateinit var summaryList: RecyclerView
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        summaryList = view.findViewById(R.id.summary)
        summaryList.setHasFixedSize(true)
        summaryList.layoutManager = LinearLayoutManager(context)
        val adapter = SummaryAdapter()
        summaryList.adapter = adapter
        summaryList.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        val items = viewModel.items()
        Log.e("TTT", "summary items $items")
        adapter.submitList(items)
    }
}