package com.mey.emarket.features.filter.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mey.emarket.R
import com.mey.emarket.databinding.FragmentFilterBinding
import com.mey.emarket.features.favorite.data.SortOption
import com.mey.emarket.features.filter.data.model.FilterModel
import com.mey.emarket.features.filter.ui.adapter.FilterAdapter
import com.mey.emarket.features.home.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FilterFragment(
    private val closeClick: (() -> Unit)? = null,
    private val onApply: ((
        brandList: List<String>,
        modelList: List<String>,
        sort: SortOption
    ) -> Unit)? = null,
    private val viewLifecycleOwner: LifecycleOwner
) : DialogFragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding: FragmentFilterBinding get() = _binding!!

    private val brandAdapter = FilterAdapter()
    private val modelAdapter = FilterAdapter()

    private lateinit var homeViewModel: HomeViewModel

    override fun getTheme(): Int {
        return R.style.TransparentBottomSheetDialogStyle
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog)
        _binding = FragmentFilterBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        dialog.setOnShowListener {
            val bottomSheet = (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = false
            }
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        handleClickEvents()

        observeBrands()
        observeModels()
        setSelectedSortOption()

        return dialog
    }


    private fun observeBrands() {
        homeViewModel.allBrands.observe(viewLifecycleOwner) {
            it?.let {
                processBrandList(it)
            }
        }
    }

    private fun observeModels() {
        homeViewModel.allModels.observe(viewLifecycleOwner) {
            it?.let {
                processModelList(it)
            }
        }
    }

    private fun setSelectedSortOption() {
        binding.rgSort.clearCheck()
        when (homeViewModel.selectedSortOption.value) {
            SortOption.DATE_ASCENDING -> {
                binding.rbOldToNew.isChecked = true
            }

            SortOption.DATE_DESCENDING -> {
                binding.rbNewToOld.isChecked = true
            }

            SortOption.PRICE_DESCENDING -> {
                binding.rbHeightToLow.isChecked = true
            }

            SortOption.PRICE_ASCENDING -> {
                binding.rbLowToHigh.isChecked = true
            }
        }
    }


    private fun processBrandList(brandList: List<FilterModel>) {
        binding.apply {
            rvBrand.adapter = brandAdapter
            rvBrand.setItemViewCacheSize(100)
            rvBrand.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            brandAdapter.setList(brandList)

        }
    }

    private fun processModelList(modelList: List<FilterModel>) {
        binding.apply {
            rvModel.adapter = modelAdapter
            rvModel.setItemViewCacheSize(100)
            rvModel.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            modelAdapter.setList(modelList)
        }
    }

    private fun handleClickEvents() {
        binding.apply {
            imgClose.setOnClickListener { dismiss() }
            btnApply.setOnClickListener {
                val selectedBrands = brandAdapter.getSelectedItems()
                val selectedModels = modelAdapter.getSelectedItems()
                val selection = getSelectedSort()
                onApply?.invoke(selectedBrands, selectedModels, selection)
                dismiss()
            }
        }
    }

    private fun getSelectedSort(): SortOption {
        val selectedId = binding.rgSort.checkedRadioButtonId
        return when (selectedId) {
            R.id.rbOldToNew -> SortOption.DATE_ASCENDING
            R.id.rbNewToOld -> SortOption.DATE_DESCENDING
            R.id.rbHeightToLow -> SortOption.PRICE_DESCENDING
            R.id.rbLowToHigh -> SortOption.PRICE_ASCENDING
            else -> SortOption.DATE_ASCENDING
        }
    }

}
