package com.mey.emarket.features.home.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mey.emarket.R
import com.mey.emarket.core.utils.Resource
import com.mey.emarket.core.utils.gone
import com.mey.emarket.core.utils.visible
import com.mey.emarket.databinding.FragmentHomeBinding
import com.mey.emarket.features.cart.viewmodel.CartViewModel
import com.mey.emarket.features.favorite.viewmodel.FavoriteViewModel
import com.mey.emarket.features.filter.data.FilterModel
import com.mey.emarket.features.filter.ui.FilterFragment
import com.mey.emarket.features.home.data.Product
import com.mey.emarket.features.home.ui.adapter.ItemProductAdapter
import com.mey.emarket.features.home.viewmodel.HomeViewModel
import com.mey.emarket.core.utils.toCartEntity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    private var isPopBackStack: Boolean = false

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var cartViewModel: CartViewModel


    private val adapter = ItemProductAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            isPopBackStack = false
            _binding = FragmentHomeBinding.inflate(inflater, container, false)
        } else {
            isPopBackStack = true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        setupEditTextDone()
        setupSearchListener()
        setupRecyclerView()
        observeSearchResults()
        handleClickEvents()
        observeSearchQuery()
        setupFavoriteChangeListener()
    }

    private fun setupFavoriteChangeListener() {
        parentFragmentManager.setFragmentResultListener("FavoriteChanged", this) { _, _ ->
            homeViewModel.reloadFilteredProducts()
        }
    }

    private fun setupSearchListener() {
        binding.apply {
            etSearch.addTextChangedListener {
                val query = it?.toString().orEmpty()
                homeViewModel.updateSearchQuery(query)
            }
        }
    }

    private fun setupEditTextDone() {
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                binding.etSearch.clearFocus()
                val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

                true
            } else {
                false
            }
        }
    }

    private fun initViewModel() {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        favoriteViewModel = ViewModelProvider(requireActivity())[FavoriteViewModel::class.java]
        cartViewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]
    }

    private fun observeSearchQuery() {
        binding.etSearch.setText(homeViewModel.searchQuery.value)
    }

    private fun observeSearchResults() {
        homeViewModel.searchResults.observe(viewLifecycleOwner) { result ->
            println("observe:: searchResults")
            when (result) {
                is Resource.Loading -> {
                    startShimmer()
                }

                is Resource.Success -> {
                    stopShimmer()
                    val filteredProducts = result.data ?: listOf()
                    processProducts(filteredProducts)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                }

                else -> {
                }
            }
        }
    }

    private fun startShimmer() {
        binding.apply {
            tvEmpty.gone()
            rvProducts.gone()
            shimmer.visible()
            shimmer.startShimmer()
        }
    }

    private fun stopShimmer() {
        binding.apply {
            rvProducts.visible()
            shimmer.gone()
            shimmer.stopShimmer()
        }
    }


    private fun setupRecyclerView() {
        binding.apply {
            rvProducts.adapter = adapter
            rvProducts.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter.setOnFavClickListenerCustom { item ->
                favClicked(item)
            }
            adapter.setOnItemClickListenerCustom { item ->
                itemClicked(item)
            }
            adapter.setOnCartClickListenerCustom { item ->
                cartViewModel.addOrIncrementProduct(item.toCartEntity())
            }
        }
    }

    private fun processProducts(data: List<Product>) {
        binding.apply {
            if (data.isEmpty()) {
                tvEmpty.visible()
                rvProducts.gone()
            } else {
                tvEmpty.gone()
                rvProducts.visible()
                adapter.setList(data)
            }
        }
    }

    private fun itemClicked(item: Product) {
        val bundle = Bundle()
        bundle.putParcelable("product", item)
        findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
    }

    private fun favClicked(item: Product) {
        if (item.isFavorite) {
            favoriteViewModel.removeFromFavorites(item)
        } else {
            favoriteViewModel.addToFavorites(item)
        }
    }

    private fun handleClickEvents() {
        binding.apply {
            cvFilter.setOnClickListener {
                val bottomSheet = FilterFragment(
                    closeClick = {},
                    onApply = { brandList, modelList, selection ->
                        homeViewModel.updateSelectedBrands(brandList)
                        homeViewModel.updateSelectedModels(modelList)
                        homeViewModel.updateSortOption(selection)
                    },
                    viewLifecycleOwner = viewLifecycleOwner
                )
                val existingFragment = childFragmentManager.findFragmentByTag("FilterFragment")
                if (existingFragment == null) {
                    bottomSheet.show(childFragmentManager, "FilterFragment")
                }
            }

        }
    }
}
