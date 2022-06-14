package com.github.didahdx.autochek.ui.home

import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.didahdx.autochek.R
import com.github.didahdx.autochek.common.extension.navigateSafe
import com.github.didahdx.autochek.data.remote.dto.CarDetails
import com.github.didahdx.autochek.databinding.FragmentHomeBinding
import com.github.didahdx.autochek.ui.carDetails.CarDetailsFragment
import com.github.didahdx.autochek.ui.customViews.BadgeDrawable
import com.github.didahdx.autochek.ui.home.adpaters.CarAdapter
import com.github.didahdx.autochek.ui.home.adpaters.OnItemClickListener
import com.github.didahdx.autochek.ui.home.adpaters.PopularMakeAdapter
import dagger.hilt.android.AndroidEntryPoint

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
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.container.toolbar.title = getString(R.string.explore)
        binding.container.toolbar.navigationIcon = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_squares_four
        )
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
        val manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val carManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvItems.apply {
            layoutManager = manager
            this.adapter = adapter
        }

        binding.rvCars.apply {
            layoutManager = carManager
            this.adapter = carAdapter
        }

        viewModel.fetchCarData()

        viewModel.popularMake.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.carList.observe(viewLifecycleOwner) {
            carAdapter.submitList(it)
        }
        createCartBadge(3)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        createCartBadge(0)
        super.onPrepareOptionsMenu(menu)
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