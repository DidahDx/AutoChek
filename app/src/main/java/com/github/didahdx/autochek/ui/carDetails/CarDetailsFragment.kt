package com.github.didahdx.autochek.ui.carDetails

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.github.didahdx.autochek.ui.carDetails.adapter.CarDetailsAdapter
import com.github.didahdx.autochek.ui.carDetails.adapter.CarMediaAdapter
import com.github.didahdx.autochek.ui.carDetails.adapter.OnItemClickListener
import com.github.didahdx.autochek.ui.fullScreenImage.ImageFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CarDetailsFragment : Fragment() {

    companion object {
        const val CARD_ID = "cardId"
    }

    val viewModel by viewModels<CarDetailsViewModel>()
    private var videoPlayer: MediaPlayer? = null
    private var _binding: FragmentCarDetailsBinding? = null
    private val binding get() = _binding!!

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

        return binding.root
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.container.toolbar.menu.findItem(R.id.cart)
            .createCartBadge(3, requireContext())



        val managerCarDetail =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val carDetailsAdapter = CarDetailsAdapter()

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

        binding.rvCarDetails.apply {
            adapter = carDetailsAdapter
            layoutManager = managerCarDetail
        }

        viewModel.carDetailList.observe(viewLifecycleOwner) {
            carDetailsAdapter.submitList(it)
        }

        viewModel.selectedCarDetail.observe(viewLifecycleOwner) {
            if (it.type.contains("video")) {
                videoPlayer = MediaPlayer()
                binding.videoView.player = videoPlayer!!.getPlayerImpl(requireContext())
                binding.videoView.show()
                binding.ivCar.hide()
                videoPlayer?.play(it.url,requireContext())

            } else {
                binding.videoView.hide()
                binding.ivCar.show()
                binding.ivCar.loadImage(it.url)
            }

        }

        viewModel.currentSeekTime.observe(viewLifecycleOwner){
            videoPlayer?.setSeekTime(viewModel.currentSeekTime.value ?: 0)
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
                    binding.videoView.hide()
                    binding.progressBar.hide()

                }
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.groupTop.hide()
                    binding.retryButton.hide()
                    binding.tvError.hide()
                    binding.videoView.hide()
                }
                UiState.Success -> {
                    binding.retryButton.hide()
                    binding.tvError.hide()
                    binding.progressBar.hide()
                    binding.groupTop.show()
                }
            }
        }


        viewModel.carDetails.observe(viewLifecycleOwner) { carDetail ->
            if (carDetail != null) {
                val notAvailable = getString(R.string.not_available)
                binding.tvCar.text = carDetail.carName ?: notAvailable

                val price = if (carDetail.marketplacePrice != null) {
                    getString(R.string.price_amount,
                        NumberFormat.formatNumber(carDetail.marketplacePrice))
                } else getString(R.string.price_not_available)

                binding.tvPrice.text = price

                if (carDetail.sold != null && carDetail.sold) {
                    binding.tvSold.show()
                } else {
                    binding.tvSold.hide()
                }

            }
        }


    }

    override fun onPause() {
        super.onPause()
        viewModel.updateSeekTime(videoPlayer?.getSeekTime() ?: 0)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoPlayer?.releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            videoPlayer?.releasePlayer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        videoPlayer?.setMediaSessionState(false)
        videoPlayer = null
        _binding = null
    }


}