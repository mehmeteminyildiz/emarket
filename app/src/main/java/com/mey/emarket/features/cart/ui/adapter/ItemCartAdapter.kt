package com.mey.emarket.features.cart.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mey.emarket.databinding.ItemCartBinding
import com.mey.emarket.features.cart.data.CartEntity

class ItemCartAdapter : ListAdapter<CartEntity, ItemCartAdapter.ItemCartViewHolder>(CartDiffCallback()) {
    private lateinit var context: Context

    class ItemCartViewHolder(var binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCartViewHolder {
        context = parent.context
        return ItemCartViewHolder(
            ItemCartBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemCartViewHolder, position: Int) {
        val item = getItem(position)
        bindItemProductViewHolder(holder, item)
    }

    private fun bindItemProductViewHolder(holder: ItemCartViewHolder, item: CartEntity?) {
        holder.binding.apply {
            item?.let { item ->
                tvName.text = item.name
                tvPrice.text = item.price
                tvQuantity.text = item.quantity.toString()

                cvIncrement.setOnClickListener { onIncrementClickListener?.invoke(item) }
                cvDecrement.setOnClickListener { onDecrementClickListener?.invoke(item) }
            }
        }
    }

    private var onIncrementClickListener: ((item: CartEntity) -> Unit)? = null
    fun setOnIncrementClickListener(f: ((item: CartEntity) -> Unit)) {
        onIncrementClickListener = f
    }

    private var onDecrementClickListener: ((item: CartEntity) -> Unit)? = null
    fun setOnDecrementClickListener(f: ((item: CartEntity) -> Unit)) {
        onDecrementClickListener = f
    }


}

class CartDiffCallback : DiffUtil.ItemCallback<CartEntity>() {
    override fun areItemsTheSame(oldItem: CartEntity, newItem: CartEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CartEntity, newItem: CartEntity): Boolean {
        return oldItem == newItem
    }
}