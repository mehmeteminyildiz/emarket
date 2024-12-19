package com.mey.emarket.features.cart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mey.emarket.features.cart.data.CartEntity
import com.mey.emarket.features.cart.data.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel
@Inject constructor(
    application: Application,
    private val repository: CartRepository,
) : AndroidViewModel(application) {
    private val _cartItems = MutableLiveData<List<CartEntity>?>()
    val cartItems: LiveData<List<CartEntity>?> get() = _cartItems

    // Sepete ürün ekleme veya artırma
    fun addOrIncrementProduct(product: CartEntity) {
        viewModelScope.launch {
            repository.addOrIncrementProduct(product)
            refreshCartItems()
        }
    }

    // Sepetteki ürün miktarını azaltma
    fun decrementProduct(productId: String) {
        viewModelScope.launch {
            repository.decrementProduct(productId)
            refreshCartItems()
        }
    }

    // Sepetteki ürünleri güncelleme
    fun refreshCartItems() {
        viewModelScope.launch {
            _cartItems.postValue(repository.getAllCartItems())
        }
    }


}