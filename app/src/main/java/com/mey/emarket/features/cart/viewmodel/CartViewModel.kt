package com.mey.emarket.features.cart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mey.emarket.core.utils.toDoubleOrZero
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


    private val _cartItemCount = MutableLiveData<Int>()
    val cartItemCount: LiveData<Int> get() = _cartItemCount

    init {
        refreshCartItems()
    }


    private val _cartItems = MutableLiveData<List<CartEntity>?>()
    val cartItems: LiveData<List<CartEntity>?> get() = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> get() = _totalPrice

    fun addOrIncrementProduct(product: CartEntity) {
        viewModelScope.launch {
            repository.addOrIncrementProduct(product)
            refreshCartItems()
        }
    }

    fun decrementProduct(productId: String) {
        viewModelScope.launch {
            repository.decrementProduct(productId)
            refreshCartItems()
        }
    }

    fun refreshCartItems() {
        viewModelScope.launch {
            val response = repository.getAllCartItems()
            _cartItems.postValue(response)
            processPrice(response)
            processCartItemCount(response)
        }
    }

    private fun processCartItemCount(response: List<CartEntity>) {
        var totalCount = 0
        response.forEach {
            totalCount += it.quantity
        }
        _cartItemCount.postValue(totalCount)
    }

    private fun processPrice(response: List<CartEntity>) {
        var totalPrice: Double = 0.0
        response.forEach {
            val priceForProduct = it.price.toDoubleOrZero() * it.quantity
            totalPrice += priceForProduct
        }
        _totalPrice.postValue(totalPrice)
    }


}