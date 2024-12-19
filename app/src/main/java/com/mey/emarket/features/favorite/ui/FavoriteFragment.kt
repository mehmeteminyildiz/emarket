package com.mey.emarket.features.favorite.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mey.emarket.databinding.FragmentFavoriteBinding
import com.mey.emarket.features.cart.viewmodel.CartViewModel
import com.mey.emarket.features.favorite.ui.adapter.FavoriteAdapter
import com.mey.emarket.features.favorite.viewmodel.FavoriteViewModel
import com.mey.emarket.features.home.viewmodel.HomeViewModel
import com.mey.emarket.shared.data.mapper.toCartEntity
import com.mey.emarket.shared.data.mapper.toProduct
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

        if (isPopBackStack) {
            // geri gelinmiş
        } else {
            // ilk açılış
        }

        initialize()
        handleClickEvents()
        setupRecyclerView()
        handleObserve()
        viewModel.fetchFavorites()

    }

    private fun handleObserve() {
        viewModel.favItems.observe(viewLifecycleOwner) { updatedList ->
            val recyclerViewState = binding.rvFavorites.layoutManager?.onSaveInstanceState() // Kaydırma durumunu kaydet
            adapter.submitList(updatedList) {
                binding.rvFavorites.layoutManager?.onRestoreInstanceState(recyclerViewState) // Kaydırma durumunu geri yükle
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvFavorites.adapter = adapter
            rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter.setOnFavClickListenerCustom { item ->
                viewModel.removeFromFavorites(item.toProduct())
                homeViewModel.getProducts()
            }
            adapter.setOnCartClickListenerCustom { item ->
                cartViewModel.addOrIncrementProduct(item.toProduct().toCartEntity())
            }
        }
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

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearFavItems()
    }
}
