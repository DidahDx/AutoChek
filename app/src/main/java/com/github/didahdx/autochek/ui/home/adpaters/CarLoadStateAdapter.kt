package com.github.didahdx.autochek.ui.home.adpaters

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

/**
 * @author Daniel Didah on 6/14/22
 */
class CarLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<CarLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: CarLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState)
            : CarLoadStateViewHolder {
        return CarLoadStateViewHolder.create(parent, retry)
    }
}
