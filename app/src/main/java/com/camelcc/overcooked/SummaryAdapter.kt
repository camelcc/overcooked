package com.camelcc.overcooked

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class SummaryAdapter: ListAdapter<SurveyItem, SummaryAdapter.ViewHolder>(Diff) {
    object Diff: DiffUtil.ItemCallback<SurveyItem>() {
        override fun areItemsTheSame(oldItem: SurveyItem, newItem: SurveyItem) = oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: SurveyItem, newItem: SurveyItem) = oldItem == newItem
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.summary_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.title.text = item.title
        holder.content.text = item.content
    }
}