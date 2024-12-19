package com.mey.emarket.features.details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.mey.emarket.R
import com.mey.emarket.databinding.FragmentDetailsBinding
import com.mey.emarket.features.cart.viewmodel.CartViewModel
import com.mey.emarket.features.favorite.viewmodel.FavoriteViewModel
import com.mey.emarket.features.home.data.Product
import com.mey.emarket.core.utils.toCartEntity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding get() = _binding!!
    private var isPopBackStack: Boolean = false

    private lateinit var cartViewModel: CartViewModel
    private lateinit var favViewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            isPopBackStack = false
            _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        } else {
            isPopBackStack = true
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModels()
        initialize()
        handleClickEvents()
        handleArguments()

    }

    private fun initViewModels() {
        cartViewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]
        favViewModel = ViewModelProvider(requireActivity())[FavoriteViewModel::class.java]
    }

    private fun handleArguments() {
        arguments?.let {
            val item = it.getParcelable("product") as Product?
            processProductDetails(item)
        }
    }

    private fun processProductDetails(item: Product?) {
        item?.let { product: Product ->
            binding.apply {
                tvProductName.text = product.name
                tvName.text = product.name
                tvPrice.text = "${product.price} ₺"
                tvDescription.text = product.description
                Glide.with(requireContext()).load(product.image).into(imgProduct)

                processFavColor(item)

                btnAddToCart.setOnClickListener {
                    cartViewModel.addOrIncrementProduct(product.toCartEntity())
                }
                imgFav.setOnClickListener {
                    if (item.isFavorite){
                        favViewModel.removeFromFavorites(product)
                    }else{
                        favViewModel.addToFavorites(product)
                    }
                    item.isFavorite = !item.isFavorite
                    processFavColor(item)
                }
            }
        }
    }

    private fun processFavColor(item: Product) {
        val favColor = if (item.isFavorite) ContextCompat.getColor(requireContext(), R.color.star_selected)
        else ContextCompat.getColor(requireContext(), R.color.grey_2)

        if (item.isFavorite) {
            binding.imgFav.setColorFilter(favColor)
        } else {
            binding.imgFav.setColorFilter(favColor)
        }
    }

    private fun initialize() {
    }

    private fun handleClickEvents() {
        binding.apply {
            imgBack.setOnClickListener { findNavController().popBackStack() }
        }
    }
}
