package com.github.didahdx.autochek.ui.carDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
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
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


private const val TAG = "CarDetailsFragment"

@AndroidEntryPoint
class CarDetailsFragment : Fragment() {

    companion object {
        const val CARD_ID = "cardId"
    }

    private val playbackStateListener: Player.Listener = playbackStateListener()
    private var player: ExoPlayer? = null


    val viewModel by viewModels<CarDetailsViewModel>()
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

        val bottomBar: BottomAppBar = requireActivity().findViewById(R.id.bottom_bar)
        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)

        fab.hide()
        bottomBar.hide()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().setTheme(R.style.Theme_SampleApp)
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
                binding.videoView.show()
                binding.ivCar.hide()
                playVideo(it.url)
            } else {
                if (player != null && player?.isPlaying == true) {
                    player?.pause()
                    viewModel.playbackPosition = player?.currentPosition ?: 0
                    viewModel.currentItem = player?.currentMediaItemIndex ?: 0
                    viewModel.playWhenReady = player?.playWhenReady ?: true
                }
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
                    binding.videoView.hide()
                    binding.progressBar.hide()
                    binding.btnLoan.hide()

                }
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.groupTop.hide()
                    binding.retryButton.hide()
                    binding.tvError.hide()
                    binding.videoView.hide()
                    binding.btnLoan.hide()
                }
                UiState.Success -> {
                    binding.retryButton.hide()
                    binding.tvError.hide()
                    binding.progressBar.hide()
                    binding.groupTop.show()
                    binding.btnLoan.show()
                }
            }
        }


        viewModel.carDetails.observe(viewLifecycleOwner) { carDetail ->
            if (carDetail != null) {
                val notAvailable = getString(R.string.not_available)
                binding.tvCar.text = carDetail.carName ?: notAvailable

                val price = if (carDetail.marketplacePrice != null) {
                    getString(
                        R.string.price_amount,
                        NumberFormat.formatNumber(carDetail.marketplacePrice)
                    )
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

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            val carMedia = viewModel.selectedCarDetail.value
            if (carMedia != null && carMedia.type.contains("video")) {
                playVideo(carMedia.url)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            val carMedia = viewModel.selectedCarDetail.value
            if (carMedia != null && carMedia.type.contains("video")) {
                playVideo(carMedia.url)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(requireContext()).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
            }
    }

    private fun playVideo(url: String) {
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.BASE_TYPE_VIDEO)
            .build()
        player?.setMediaItem(mediaItem)
        player?.playWhenReady = viewModel.playWhenReady
        player?.seekTo(viewModel.currentItem, viewModel.playbackPosition)
        player?.addListener(playbackStateListener)
        player?.prepare()
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            viewModel.playbackPosition = exoPlayer.currentPosition
            viewModel.currentItem = exoPlayer.currentMediaItemIndex
            viewModel.playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
        }
        player = null
    }


}

private fun playbackStateListener() = object : Player.Listener {
    override fun onPlaybackStateChanged(playbackState: Int) {
        val stateString: String = when (playbackState) {
            ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
            ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
            ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
            ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
            else -> "UNKNOWN_STATE             -"
        }
        Timber.tag(TAG).d("changed state to %s", stateString)
    }
}