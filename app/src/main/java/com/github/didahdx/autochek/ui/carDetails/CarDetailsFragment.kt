package com.github.didahdx.autochek.ui.carDetails

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
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
class CarDetailsFragment : Fragment() {

    companion object {
        const val CARD_ID = "cardId"
    }

    val viewModel by viewModels<CarDetailsViewModel>()
    var cardId = ""
    private var _binding: FragmentCarDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments?.getString(CARD_ID).isNullOrEmpty()) this.findNavController().navigateUp()
        cardId = arguments?.getString(CARD_ID) ?: ""
        Timber.e("cardId $cardId")
        viewModel.fetchData(cardId)
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
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)
        viewModel.selectedCarDetail.observe(viewLifecycleOwner) {
            if (it.type.contains("video")) {
                //play video

                binding.videoView.show()
                binding.ivCar.hide()
                binding.videoView.apply {
                    setMediaController(mediaController)
                    setVideoURI(Uri.parse(it.url))
                    requestFocus()
                    start()
                }
            } else {
                binding.videoView.setMediaController(null)
                mediaController.removeAllViews()
                binding.videoView.hide()
                binding.ivCar.show()
                binding.ivCar.loadImage(it.url)
            }

        }


        viewModel.carMedia.observe(viewLifecycleOwner) {
            carMediaAdapter.submitList(it)
        }

        viewModel.carDetails.observe(viewLifecycleOwner) { carDetail ->
            val notAvailable = getString(R.string.not_available)
            if (carDetail != null) {
                binding.tvCarNameDetail.text = carDetail.carName ?: notAvailable
                Timber.e("sell condit ${carDetail.sellingCondition}")
                binding.tvModelDetail.text = carDetail.model?.name ?: notAvailable
                binding.tvYearDetail.text = carDetail.year.toString()
                binding.tvEngineTypeDetail.text = carDetail.engineType ?: notAvailable

                val millage = if (carDetail.mileage != null) {
                    NumberFormat.formatNumber(carDetail.mileage) + " " + carDetail.mileageUnit
                } else notAvailable

                binding.tvMileageDetail.text = millage


                binding.tvLocationDetail.text =
                    "${carDetail.state}, ${carDetail.city}, ${carDetail.country}"
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
        binding.videoView.setMediaController(null)
        _binding = null
    }
}