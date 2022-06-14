package com.github.didahdx.autochek.ui.home.adpaters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.databinding.FooterLoaderBinding

/**
 * @author Daniel Didah on 6/14/22
 */
class CarLoadStateViewHolder(
    private val binding: FooterLoaderBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {


    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): CarLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.footer_loader, parent, false)
            val binding = FooterLoaderBinding.bind(view)
            return CarLoadStateViewHolder(binding, retry)
        }
    }


}