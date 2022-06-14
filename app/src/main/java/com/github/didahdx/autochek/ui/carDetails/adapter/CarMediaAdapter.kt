package com.github.didahdx.autochek.ui.carDetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.didahdx.autochek.common.extension.loadImage
import com.github.didahdx.autochek.data.remote.dto.CarMedia
import com.github.didahdx.autochek.databinding.ItemCarMediaBinding

/**
 * @author Daniel Didah on 6/13/22
 */
class CarMediaAdapter(private val onItemClickListener: OnItemClickListener) :
    ListAdapter<CarMedia, CarMediaAdapter.CarMediaViewHolder>(CarMediaDiff()) {

    inner class CarMediaViewHolder(private val binding: ItemCarMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClickListener(getItem(position))
                    }
                }

            }
        }

        fun bind(carMedia: CarMedia) {
            binding.ivCar.loadImage(carMedia.url)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarMediaViewHolder {
        val binding =
            ItemCarMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarMediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarMediaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

interface OnItemClickListener {
    fun onItemClickListener(carMedia: CarMedia)
}

class CarMediaDiff : DiffUtil.ItemCallback<CarMedia>() {
    override fun areItemsTheSame(oldItem: CarMedia, newItem: CarMedia): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CarMedia, newItem: CarMedia): Boolean {
        return oldItem == newItem
    }

}