package com.mey.emarket.features.favorite.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mey.emarket.R
import com.mey.emarket.core.utils.gone
import com.mey.emarket.core.utils.visible
import com.mey.emarket.databinding.FragmentFavoriteBinding
import com.mey.emarket.features.cart.viewmodel.CartViewModel
import com.mey.emarket.features.favorite.ui.adapter.FavoriteAdapter
import com.mey.emarket.features.favorite.viewmodel.FavoriteViewModel
import com.mey.emarket.features.home.viewmodel.HomeViewModel
import com.mey.emarket.core.utils.toCartEntity
import com.mey.emarket.core.utils.toProduct
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding: FragmentFavoriteBinding get() = _binding!!
    private var isPopBackStack: Boolean = false

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var cartViewModel: CartViewModel
    private val adapter = FavoriteAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            isPopBackStack = false
            _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        } else {
            isPopBackStack = true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        initialize()
        handleClickEvents()
        setupRecyclerView()
        handleObserve()
        viewModel.fetchFavorites()

    }

    private fun handleObserve() {
        viewModel.favItems.observe(viewLifecycleOwner) { updatedList ->
            if (updatedList.isNullOrEmpty()) {
                binding.tvEmpty.visible()
                binding.rvFavorites.gone()
            } else {
                binding.tvEmpty.gone()
                binding.rvFavorites.visible()
                val recyclerViewState = binding.rvFavorites.layoutManager?.onSaveInstanceState()
                adapter.submitList(updatedList) {
                    binding.rvFavorites.layoutManager?.onRestoreInstanceState(recyclerViewState)
                }
            }

        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvFavorites.adapter = adapter
            rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter.setOnFavClickListenerCustom { item ->
                viewModel.removeFromFavorites(item.toProduct())
                sendResultToHome()
            }
            adapter.setOnCartClickListenerCustom { item ->
                cartViewModel.addOrIncrementProduct(item.toProduct().toCartEntity())
            }
            adapter.setOnItemClickListenerCustom { item ->
                val bundle = Bundle()
                bundle.putParcelable("product", item.toProduct())
                findNavController().navigate(R.id.action_favoriteFragment_to_detailsFragment,bundle)
            }
        }
    }

    private fun sendResultToHome() {
        setFragmentResult("FavoriteChanged", Bundle())
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[FavoriteViewModel::class.java]
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        cartViewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]
    }

    private fun initialize() {
    }

    private fun handleClickEvents() {
        binding.apply {
            imgBack.setOnClickListener { findNavController().popBackStack() }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearFavItems()
    }
}
