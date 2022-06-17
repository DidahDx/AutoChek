package com.github.didahdx.autochek.ui.carDetails

import android.media.MediaDrm
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.common.NumberFormat
import com.github.didahdx.autochek.common.extension.*
import com.github.didahdx.autochek.data.remote.dto.CarMedia
import com.github.didahdx.autochek.databinding.FragmentCarDetailsBinding
import com.github.didahdx.autochek.ui.carDetails.adapter.CarMediaAdapter
import com.github.didahdx.autochek.ui.carDetails.adapter.OnItemClickListener
import com.github.didahdx.autochek.ui.fullScreenImage.ImageFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CarDetailsFragment : Fragment(), SurfaceHolder.Callback,
    MediaPlayer.OnPreparedListener {

    companion object {
        const val CARD_ID = "cardId"
    }
    private var mediaPlayer = MediaPlayer()
    val viewModel by viewModels<CarDetailsViewModel>()

    private var _binding: FragmentCarDetailsBinding? = null
    private val binding get() = _binding!!
        var URL: Uri = Uri.parse("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments?.getString(CARD_ID).isNullOrEmpty()) this.findNavController().navigateUp()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCarDetailsBinding.inflate(inflater, container, false)
        binding.container.toolbar.title = getString(R.string.product)
        binding.container.toolbar.navigationIcon = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_arrow_back
        )
        binding.container.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        mediaPlayer.setOnPreparedListener(this)
        binding.videoView.holder.addCallback(this)
        binding.playButton.isEnabled = false
        binding.playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.playButton.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayer.start()
                binding.playButton.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.container.toolbar.menu.findItem(R.id.cart)
            .createCartBadge(3, requireContext())


        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val carMediaAdapter = CarMediaAdapter(object : OnItemClickListener {
            override fun onItemClickListener(carMedia: CarMedia) {
                viewModel.update(carMedia)
            }
        })

        binding.ivCar.setOnClickListener {
            val bundle = bundleOf(ImageFragment.IMAGE_URL to viewModel.selectedCarDetail.value?.url)
            findNavController().navigateSafe(
                R.id.action_carDetailsFragment_to_imageFragment,
                bundle
            )
        }

        binding.rvCarImages.apply {
            adapter = carMediaAdapter
            layoutManager = manager
        }

        viewModel.selectedCarDetail.observe(viewLifecycleOwner) {
            if (it.type.contains("video")) {
                URL = Uri.parse(it.url)
                binding.videoView.show()
                binding.playButton.show()
                binding.videoView.holder.addCallback(this)
                binding.ivCar.hide()
            } else {
                binding.playButton.hide()
                binding.videoView.hide()
                binding.ivCar.show()
                binding.ivCar.loadImage(it.url)
            }

        }


        viewModel.carMedia.observe(viewLifecycleOwner) {
            carMediaAdapter.submitList(it)
        }

        binding.retryButton.setOnClickListener {
            viewModel.fetchData()
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Error -> {
                    binding.retryButton.show()
                    binding.tvError.show()
                    binding.tvError.text = it.error
                    binding.groupTop.hide()
                    binding.groupCarDetails.hide()
                    binding.videoView.hide()
                    binding.progressBar.hide()
                    binding.playButton.hide()
                }
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.groupTop.hide()
                    binding.groupCarDetails.hide()
                    binding.retryButton.hide()
                    binding.tvError.hide()
                    binding.videoView.hide()
                    binding.playButton.hide()
                }
                UiState.Success -> {
                    binding.retryButton.hide()
                    binding.tvError.hide()
                    binding.progressBar.hide()
                    binding.groupTop.show()
                    binding.groupCarDetails.show()
                }
            }
        }


        viewModel.carDetails.observe(viewLifecycleOwner) { carDetail ->
            val notAvailable = getString(R.string.not_available)
            if (carDetail != null) {
                binding.tvCarNameDetail.text = carDetail.carName ?: notAvailable
                binding.tvModelDetail.text = carDetail.model?.name ?: notAvailable
                binding.tvYearDetail.text = carDetail.year.toString()
                binding.tvEngineTypeDetail.text = carDetail.engineType ?: notAvailable

                val millage = if (carDetail.mileage != null) {
                    NumberFormat.formatNumber(carDetail.mileage) + " " + carDetail.mileageUnit
                } else notAvailable

                binding.tvMileageDetail.text = millage

                val locationBuilder = StringBuilder()
                if (carDetail.state != null && carDetail.state.isNotEmpty()) {
                    locationBuilder.append(carDetail.state)
                }
                if (carDetail.city != null && carDetail.city.isNotEmpty()) {
                    locationBuilder.append(", ${carDetail.city}")
                }
                if (carDetail.country != null && carDetail.country.isNotEmpty()) {
                    locationBuilder.append(", ${carDetail.country}")
                }

                if (locationBuilder.isEmpty()) {
                    locationBuilder.append(notAvailable)
                }

                if (carDetail.sold != null && carDetail.sold) {
                    binding.tvSold.show()
                } else {
                    binding.tvSold.hide()
                }

                val price = if (carDetail.marketplacePrice != null) {
                  NumberFormat.formatNumber(carDetail.marketplacePrice)
                } else notAvailable

                binding.tvPrice.text =  getString(R.string.price,price)
                binding.tvLocationDetail.text = locationBuilder.toString()
                binding.tvOwnerTypeDetail.text = carDetail.ownerType ?: notAvailable
                binding.tvTransmissionDetail.text = carDetail.transmission ?: notAvailable
                binding.tvConditionDetail.text = carDetail.sellingCondition ?: notAvailable
                binding.tvFuelTypeDetail.text = carDetail.fuelType ?: notAvailable
                binding.tvBodyTypeDetail.text = carDetail.bodyType?.name ?: notAvailable
                binding.tvInteriorColorDetail.text = carDetail.interiorColor ?: notAvailable
                binding.tvExteriorColorDetail.text = carDetail.exteriorColor ?: notAvailable

            }
        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.release()
        _binding = null
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        mediaPlayer.apply {
            setDataSource(requireContext().applicationContext, URL)
            setDisplay(surfaceHolder)
            prepareAsync()
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        binding.playButton.isEnabled = true
    }

}