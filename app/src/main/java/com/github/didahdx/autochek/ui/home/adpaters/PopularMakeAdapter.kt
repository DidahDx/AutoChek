package com.github.didahdx.autochek.ui.home.adpaters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.didahdx.autochek.common.extension.loadImage
import com.github.didahdx.autochek.data.remote.dto.Make
import com.github.didahdx.autochek.databinding.ItemPopularMakeBinding

/**
 * @author Daniel Didah on 6/13/22
 */
class PopularMakeAdapter : ListAdapter<Make, PopularMakeAdapter.PopularMakeView>(MakeDiffUtil()) {

    inner class PopularMakeView(private val binding: ItemPopularMakeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(make: Make) {
            binding.ivLogo.loadImage(make.imageUrl)
            binding.tvName.text = make.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMakeView {
        val binding =
            ItemPopularMakeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularMakeView(binding)
    }

    override fun onBindViewHolder(holder: PopularMakeView, position: Int) {
        holder.bind(getItem(position))
    }


}

class MakeDiffUtil : DiffUtil.ItemCallback<Make>() {
    override fun areItemsTheSame(oldItem: Make, newItem: Make): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Make, newItem: Make): Boolean {
        return oldItem == newItem
    }

}