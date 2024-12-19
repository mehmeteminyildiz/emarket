package com.mey.emarket.features.favorite.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mey.emarket.R
import com.mey.emarket.databinding.ItemProductBinding
import com.mey.emarket.features.favorite.data.FavoritesEntity

class FavoriteAdapter : ListAdapter<FavoritesEntity, FavoriteAdapter.ItemProductViewHolder>(FavoritesDiffCallback()) {

    private lateinit var context: Context

    class ItemProductViewHolder(var binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemProductViewHolder {
        context = parent.context
        return ItemProductViewHolder(
            ItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemProductViewHolder, position: Int) {
        val item = getItem(position) // `ListAdapter`'in getItem() fonksiyonu kullanılır
        bindItemProductViewHolder(holder, item)
    }

    private fun bindItemProductViewHolder(holder: ItemProductViewHolder, item: FavoritesEntity) {
        holder.binding.apply {
            Glide.with(context).load(item.image).into(imgImage)
            tvPrice.text = "${item.price} ₺"
            tvName.text = item.name

            val favColor = ContextCompat.getColor(context, R.color.star_selected)
            imgFav.setColorFilter(favColor)

            imgFav.setOnClickListener { onFavClickListenerCustom?.invoke(item) }

            mcvItem.setOnClickListener { onItemClickListenerCustom?.invoke(item) }


            btnAddToCart.setOnClickListener { onCartClickListenerCustom?.invoke(item) }
        }
    }

    private var onItemClickListenerCustom: ((item: FavoritesEntity) -> Unit)? = null
    fun setOnItemClickListenerCustom(f: ((item: FavoritesEntity) -> Unit)) {
        onItemClickListenerCustom = f
    }

    private var onCartClickListenerCustom: ((item: FavoritesEntity) -> Unit)? = null
    fun setOnCartClickListenerCustom(f: ((item: FavoritesEntity) -> Unit)) {
        onCartClickListenerCustom = f
    }

    private var onFavClickListenerCustom: ((item: FavoritesEntity) -> Unit)? = null
    fun setOnFavClickListenerCustom(f: ((item: FavoritesEntity) -> Unit)) {
        onFavClickListenerCustom = f
    }


}

class FavoritesDiffCallback : DiffUtil.ItemCallback<FavoritesEntity>() {
    override fun areItemsTheSame(oldItem: FavoritesEntity, newItem: FavoritesEntity): Boolean {
        return oldItem.id == newItem.id // Her öğenin benzersiz bir ID'si olmalı
    }

    override fun areContentsTheSame(oldItem: FavoritesEntity, newItem: FavoritesEntity): Boolean {
        return oldItem == newItem // İçerik aynıysa hiçbir değişiklik yapma
    }
}