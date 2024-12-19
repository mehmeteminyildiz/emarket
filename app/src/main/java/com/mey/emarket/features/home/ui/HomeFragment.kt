package com.mey.emarket.features.home.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mey.emarket.R
import com.mey.emarket.core.utils.Resource
import com.mey.emarket.databinding.FragmentHomeBinding
import com.mey.emarket.features.cart.viewmodel.CartViewModel
import com.mey.emarket.features.favorite.viewmodel.FavoriteViewModel
import com.mey.emarket.features.filter.data.model.FilterModel
import com.mey.emarket.features.filter.ui.FilterFragment
import com.mey.emarket.features.home.data.model.Product
import com.mey.emarket.features.home.ui.adapter.ItemProductAdapter
import com.mey.emarket.features.home.viewmodel.HomeViewModel
import com.mey.emarket.shared.data.mapper.toCartEntity
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
        observeProducts()
        if (isPopBackStack) {
        } else {
            getProducts()
        }
        setupEditTextDone()
        setupSearchListener()
        observeSearchResults()
        initialize()
        handleClickEvents()
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

    private fun getProducts() {
        homeViewModel.getProducts()
    }

    private fun observeSearchResults() {
        homeViewModel.searchResults.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    // Loading durumunu göster
                }

                is Resource.Success -> {
                    val filteredProducts = result.data ?: listOf()
                    processProducts(filteredProducts)
                }

                is Resource.Error -> {
                    // Hata durumunu göster
                }

                else -> {
                    // Boş arama için varsayılan ürünleri göster
                }
            }
        }
    }

    private fun observeProducts() {
        homeViewModel.productsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {}
                is Resource.Error -> {
                    homeViewModel.clearProductsResponse()

                }

                is Resource.Success -> {
                    homeViewModel.clearProductsResponse()

                    response.data?.let { data ->
                        println("response:: $data")
                        processProducts(data)
                    }
                }

                else -> {}
            }
        }
    }

    private fun processProducts(data: List<Product>) {
        binding.apply {
            rvProducts.adapter = adapter
            rvProducts.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter.setList(data)
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

    private fun initialize() {

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

    private fun getBrandList(): List<FilterModel> {
        val brandList = ArrayList<FilterModel>()
        for (i in 0..10) {
            brandList.add(FilterModel("brand-$i", isSelected = false))
        }
        return brandList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.clearProductsResponse()
    }
}
