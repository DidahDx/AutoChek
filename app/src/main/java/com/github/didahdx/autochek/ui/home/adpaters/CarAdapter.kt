package com.github.didahdx.autochek.ui.home.adpaters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.common.NumberFormat
import com.github.didahdx.autochek.common.extension.hide
import com.github.didahdx.autochek.common.extension.show
import com.github.didahdx.autochek.data.remote.dto.CarDetails
import com.github.didahdx.autochek.databinding.ItemProductBinding
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * @author Daniel Didah on 6/13/22
 */
class CarAdapter(private val clickListener: OnItemClickListener) :
    PagingDataAdapter<CarDetails, CarAdapter.CarViewHolder>(CarDiffUtil()) {

    inner class CarViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val twoDecimalPoint = DecimalFormat("###,###,###.00")
        private val ratingDecimalPoint = DecimalFormat("##.0")

        init {
            twoDecimalPoint.roundingMode = RoundingMode.DOWN
            ratingDecimalPoint.roundingMode = RoundingMode.DOWN
            binding.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        getItem(position)?.let { it1 -> clickListener.onItemClickListener(it1) }
                    }
                }
            }
        }

        fun bind(carDetails: CarDetails) {
            val notAvailable = binding.root.context.getString(R.string.not_available)
            Glide.with(binding.root.context)
                .load(carDetails.imageUrl)
                .error(R.drawable.ic_error_image)
                .fitCenter()
                .into(binding.ivImage)

            if (carDetails.sold) binding.tvSold.show() else binding.tvSold.hide()
            binding.tvTitle.text = carDetails.title
            binding.tvBrand.text = carDetails.sellingCondition

            val locationBuilder = StringBuilder()
            if (carDetails.city != null && carDetails.city.isNotEmpty()) {
                locationBuilder.append(carDetails.city)
            }
            if (carDetails.state != null && carDetails.state.isNotEmpty()) {
                locationBuilder.append(" ${carDetails.state}")
            }
            if (locationBuilder.isEmpty()) {
                locationBuilder.append(notAvailable)
            }
            binding.tvLocation.text = locationBuilder.toString()

            val price = if (carDetails.marketplacePrice != null) {
                binding.root.context.getString(
                    R.string.price_amount,
                    NumberFormat.formatNumber(carDetails.marketplacePrice)
                )
            } else null

            binding.tvPrice.text = price

            val rating = if (carDetails.gradeScore == null) {
                notAvailable
            } else {
                ratingDecimalPoint.format(carDetails.gradeScore).toString()
            }
            binding.tvRating.text = rating
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}

interface OnItemClickListener {
    fun onItemClickListener(carDetails: CarDetails)
}

class CarDiffUtil : DiffUtil.ItemCallback<CarDetails>() {
    override fun areItemsTheSame(oldItem: CarDetails, newItem: CarDetails): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CarDetails, newItem: CarDetails): Boolean {
        return oldItem == newItem
    }

}