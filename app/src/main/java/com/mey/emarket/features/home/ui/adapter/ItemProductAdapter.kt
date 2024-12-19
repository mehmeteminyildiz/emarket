package com.mey.emarket.features.home.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mey.emarket.R
import com.mey.emarket.databinding.ItemProductBinding
import com.mey.emarket.features.home.data.Product

class ItemProductAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var _list = ArrayList<Product>()
    private val list get() = _list.toList()
    private lateinit var context: Context

    fun setList(newList: List<Product>) {
        _list.clear()
        _list.addAll(newList)
        notifyDataSetChanged()
    }

    class ItemProductViewHolder(var binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return ItemProductViewHolder(
            ItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindItemProductViewHolder(
            holder as ItemProductViewHolder,
            position
        )
    }

    private fun bindItemProductViewHolder(holder: ItemProductViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            Glide.with(context).load(item.image).into(imgImage)
            tvPrice.text = "${item.price} ₺"
            tvName.text = item.name

            val favColor = if (item.isFavorite) ContextCompat.getColor(context, R.color.star_selected)
            else ContextCompat.getColor(context, R.color.grey_2)

            if (item.isFavorite) {
                imgFav.setColorFilter(favColor)
            } else {
                imgFav.setColorFilter(favColor)
            }

            mcvItem.setOnClickListener {
                onItemClickListenerCustom?.invoke(item)
            }
            imgFav.setOnClickListener {
                onFavClickListenerCustom?.invoke(item)
                item.isFavorite = !item.isFavorite
                notifyItemChanged(position)
            }
            btnAddToCart.setOnClickListener {
                onCartClickListenerCustom?.invoke(item)
            }
        }
    }

    private var onCartClickListenerCustom: ((item: Product) -> Unit)? = null
    fun setOnCartClickListenerCustom(f: ((item: Product) -> Unit)) {
        onCartClickListenerCustom = f
    }

    private var onItemClickListenerCustom: ((item: Product) -> Unit)? = null
    fun setOnItemClickListenerCustom(f: ((item: Product) -> Unit)) {
        onItemClickListenerCustom = f
    }

    private var onFavClickListenerCustom: ((item: Product) -> Unit)? = null
    fun setOnFavClickListenerCustom(f: ((item: Product) -> Unit)) {
        onFavClickListenerCustom = f
    }

}