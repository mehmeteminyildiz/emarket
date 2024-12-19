package com.mey.emarket.features.cart.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mey.emarket.core.utils.gone
import com.mey.emarket.core.utils.visible
import com.mey.emarket.databinding.FragmentCartBinding
import com.mey.emarket.features.cart.ui.adapter.ItemCartAdapter
import com.mey.emarket.features.cart.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding get() = _binding!!
    private var isPopBackStack: Boolean = false

    private lateinit var viewModel: CartViewModel
    private val adapter = ItemCartAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            isPopBackStack = false
            _binding = FragmentCartBinding.inflate(inflater, container, false)
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
        setupRecyclerView()
        handleObserve()
        viewModel.refreshCartItems()
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvCart.adapter = adapter
            rvCart.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            adapter.setOnIncrementClickListener { item ->
                viewModel.addOrIncrementProduct(item)
            }
            adapter.setOnDecrementClickListener { item ->
                viewModel.decrementProduct(item.id)
            }
        }

    }

    private fun handleObserve() {
        viewModel.cartItems.observe(viewLifecycleOwner) {
            it?.let { data ->
                val recyclerViewState = binding.rvCart.layoutManager?.onSaveInstanceState()
                adapter.submitList(data) {
                    binding.rvCart.layoutManager?.onRestoreInstanceState(recyclerViewState)
                }
            }
        }

        viewModel.totalPrice.observe(viewLifecycleOwner){
            if (it > 0){
                binding.llPriceArea.visible()
                binding.tvEmpty.gone()
            }else{
                binding.llPriceArea.gone()
                binding.tvEmpty.visible()
            }
            binding.tvPrice.text = "$it ₺"
        }
    }


    private fun initViewModels() {
        viewModel = ViewModelProvider(requireActivity())[CartViewModel::class.java]
    }

    private fun initialize() {
    }

    private fun handleClickEvents() {
        binding.apply {

        }
    }
}
