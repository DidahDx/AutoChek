package com.github.didahdx.autochek.ui.home

import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.common.extension.navigateSafe
import com.github.didahdx.autochek.common.extension.show
import com.github.didahdx.autochek.data.remote.dto.CarDetails
import com.github.didahdx.autochek.databinding.FragmentHomeBinding
import com.github.didahdx.autochek.ui.carDetails.CarDetailsFragment
import com.github.didahdx.autochek.ui.customViews.BadgeDrawable
import com.github.didahdx.autochek.ui.home.adpaters.CarAdapter
import com.github.didahdx.autochek.ui.home.adpaters.CarLoadStateAdapter
import com.github.didahdx.autochek.ui.home.adpaters.OnItemClickListener
import com.github.didahdx.autochek.ui.home.adpaters.PopularMakeAdapter
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {


    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.container.toolbar.title = getString(R.string.explore)
        binding.container.toolbar.navigationIcon = ContextCompat
            .getDrawable(requireContext(), R.drawable.ic_squares_four)

        val bottomBar: BottomAppBar = requireActivity().findViewById(R.id.bottom_bar)
        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        fab.show()
        bottomBar.show()
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val adapter = PopularMakeAdapter()
        val carAdapter = CarAdapter(object : OnItemClickListener {
            override fun onItemClickListener(carDetails: CarDetails) {
                val bundle = bundleOf(CarDetailsFragment.CARD_ID to carDetails.id)
                findNavController()
                    .navigateSafe(R.id.action_homeFragment_to_carDetailsFragment, bundle)
            }
        })


        viewLifecycleOwner.lifecycleScope.launch {
            carAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && carAdapter.itemCount == 0
                // show empty list
                binding.emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                binding.rvCars.isVisible = !isListEmpty
            }
        }


        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val carManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvItems.apply {
            layoutManager = manager
            this.adapter = adapter
        }

        binding.rvCars.apply {
            layoutManager = carManager
            this.adapter = carAdapter.withLoadStateHeaderAndFooter(
                header = CarLoadStateAdapter { carAdapter.retry() },
                footer = CarLoadStateAdapter { carAdapter.retry() }
            )
        }


        viewModel.popularMake.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            carAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && carAdapter.itemCount == 0
                // show empty list
                binding.emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                binding.rvCars.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
                    Snackbar.make(
                        fab,
                        getString(R.string.error_message, it.error),
                        Snackbar.LENGTH_LONG
                    ).setAnchorView(fab)
                        .show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.carFlowList.collectLatest(carAdapter::submitData)
        }

        binding.retryButton.setOnClickListener {
            carAdapter.retry()
            viewModel.fetchCarData()
        }

        val fab: FloatingActionButton = requireActivity().findViewById(R.id.fab)
        binding.rvCars.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.visibility == View.VISIBLE) {
                    fab.hide()
                } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                    fab.show()
                }
            }
        })

        createCartBadge(3)

    }


    private fun createCartBadge(paramInt: Int) {
        if (Build.VERSION.SDK_INT <= 15) {
            return
        }
        val cartItem: MenuItem = binding.container.toolbar.menu.findItem(R.id.cart)
        val localLayerDrawable = cartItem.icon as LayerDrawable
        val cartBadgeDrawable = localLayerDrawable
            .findDrawableByLayerId(R.id.ic_badge)
        val badgeDrawable: BadgeDrawable
        if (cartBadgeDrawable != null
            && cartBadgeDrawable is BadgeDrawable
            && paramInt < 10
        ) {
            badgeDrawable = cartBadgeDrawable
        } else {
            badgeDrawable = BadgeDrawable(requireContext())
        }
        badgeDrawable.setCount(paramInt)
        localLayerDrawable.mutate()
        localLayerDrawable.setDrawableByLayerId(R.id.ic_badge, badgeDrawable)
        cartItem.icon = localLayerDrawable
    }
}