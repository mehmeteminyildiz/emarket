package com.mey.emarket.features.filter.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mey.emarket.databinding.ItemSelectionBinding
import com.mey.emarket.features.filter.data.FilterModel

class FilterAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var _list = ArrayList<FilterModel>()
    private val list get() = _list.toList()
    private lateinit var context: Context


    fun setList(newList: List<FilterModel>) {
        _list.clear()
        _list.addAll(newList)
        notifyDataSetChanged()
    }

    class ItemSelectionViewHolder(var binding: ItemSelectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ItemSelectionViewHolder(
            ItemSelectionBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindItemSelectionViewHolder(
            holder as ItemSelectionViewHolder,
            position
        )
    }

    private fun bindItemSelectionViewHolder(holder: ItemSelectionViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            checkbox.isChecked = item.isSelected
            checkbox.text = item.name

            checkbox.setOnCheckedChangeListener { compoundButton, isChecked ->
                item.isSelected = isChecked
            }
        }
    }

    fun getSelectedItems(): List<String> {
        return list.filter { it.isSelected }.map { it.name }
    }

    fun getSelectedModels(): List<String>{
        return list.filter { it.isSelected }.map { it.name}
    }


}