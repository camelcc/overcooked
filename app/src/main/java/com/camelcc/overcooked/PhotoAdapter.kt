package com.camelcc.overcooked

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load

class PhotoAdapter(private val onItemClicked: (Photo) -> Unit,
                   private val onToggleClicked: (Photo) -> Unit
): ListAdapter<Photo, PhotoAdapter.ViewHolder>(Diff) {
    object Diff: DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo) = oldItem == newItem

        override fun getChangePayload(oldItem: Photo, newItem: Photo): Any {
            return newItem
        }
    }

    class ViewHolder(view: View,
                     onItemClicked: (Int) -> Unit,
                     onToggleClicked: (Int) -> Unit):
        RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val title: TextView = view.findViewById(R.id.title)
        val counter: TextView = view.findViewById(R.id.counter)
        val toggle: ImageButton = view.findViewById(R.id.toggle)

        init {
            view.setOnClickListener {
                onItemClicked(adapterPosition)
            }
            toggle.setOnClickListener {
                onToggleClicked(adapterPosition)
            }
        }
    }

    fun visiblePhotos(first: Int, last: Int): List<Photo> {
        val res = mutableListOf<Photo>()
        for (i in IntRange(first, last)) {
            res.add(getItem(i))
        }
        return res
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.photo_item, parent, false)
        return ViewHolder(view,
            onItemClicked = { pos ->
                if (pos != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(pos))
                }
            },
            onToggleClicked = { pos ->
            if (pos != RecyclerView.NO_POSITION) {
                onToggleClicked(getItem(pos))
            }
        })
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int) {
        val photo = getItem(position)
        vh.image.load(photo.src.large2x)
        vh.title.text = photo.title
        if (photo.enabled) {
            vh.toggle.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            vh.toggle.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
        vh.counter.text = photo.counter.toString()
    }

    override fun onBindViewHolder(vh: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            return onBindViewHolder(vh, position)
        }

        val photo = payloads[0] as Photo
        vh.image.load(photo.src.large2x)
        vh.title.text = photo.title
        if (photo.enabled) {
            vh.toggle.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            vh.toggle.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
        vh.counter.text = photo.counter.toString()
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id
    }
}