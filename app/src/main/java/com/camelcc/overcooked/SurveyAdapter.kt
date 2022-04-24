package com.camelcc.overcooked

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView

class SurveyAdapter(private val items: List<SurveyItem>,
                    private val onPrev: () -> Unit,
                    private val onNext: () -> Unit) :
    RecyclerView.Adapter<SurveyAdapter.ViewHolder>() {

    val contents: MutableList<String> = items.map { it.content }.toMutableList()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val content: EditText = view.findViewById(R.id.content)
        val prevButton: Button = view.findViewById(R.id.prev)
        val nextButton: Button = view.findViewById(R.id.next)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.survey_item, parent, false)
        val vh = ViewHolder(view)
        vh.prevButton.setOnClickListener {
            onPrev()
        }
        vh.nextButton.setOnClickListener {
            onNext()
        }
        return vh
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        vh.title.text = items[position].title
        vh.content.setText(contents[position], TextView.BufferType.EDITABLE)
        vh.prevButton.isEnabled = position > 0
        vh.content.addTextChangedListener(afterTextChanged = {
            contents[position] = it?.toString() ?: ""
        })
    }

//    override fun getItemViewType(position: Int): Int {
//        return if (position < titles.size) SURVEY_TYPE else SUMMARY_TYPE
//    }

    override fun getItemCount() = items.size
}