package com.github.didahdx.autochek.ui.carDetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.data.model.Details
import com.github.didahdx.autochek.databinding.ItemCarDetailsBinding

/**
 * @author Daniel Didah on 6/18/22
 */
class CarDetailsAdapter :
    ListAdapter<Details, CarDetailsAdapter.CarDetailsViewHolder>(CarDetailsDiffUtil()) {

    inner class CarDetailsViewHolder(private val binding: ItemCarDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val notAvailable = binding.root.context.getString(R.string.not_available)

        fun bind(details: Details) {
            binding.tvTitle.text = binding.root.context.getString(details.title)
            binding.tvDescription.text = details.description ?: notAvailable
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarDetailsViewHolder {
        val binding =
            ItemCarDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarDetailsViewHolder, position: Int) {
        holder.bind(getItem(position))
        val color = if (position % 2 == 1) {
            ContextCompat.getColor(holder.itemView.context, R.color.light_grey)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.white)
        }

        holder.itemView.setBackgroundColor(color)
    }


}

class CarDetailsDiffUtil : DiffUtil.ItemCallback<Details>() {
    override fun areItemsTheSame(oldItem: Details, newItem: Details): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Details, newItem: Details): Boolean {
        return oldItem == newItem
    }

}