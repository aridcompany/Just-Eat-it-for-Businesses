package com.ari_d.justeat_itforbusinesses.ui.Main.ViewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.ari_d.justeat_itforbusinesses.other.Event
import com.ari_d.justeat_itforbusinesses.other.Resource
import com.ari_d.justeat_itforbusinesses.ui.Main.Repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _createProductStatus = MutableLiveData<Event<Resource<Product>>>()
    val createProductStatus: LiveData<Event<Resource<Product>>> = _createProductStatus

    private val _uploadProductImagesStatus = MutableLiveData<Event<Resource<MutableList<String>>>>()
    val uploadProductImagesStatus: LiveData<Event<Resource<MutableList<String>>>> =
        _uploadProductImagesStatus

    fun createProduct(product: Product) {
        val error = if (
            product.name.isEmpty() ||
            product.description.isEmpty() ||
            product.contact_no.isEmpty() ||
            product.price.isEmpty() ||
            product.stock.isEmpty() ||
            product.shipping_fee.isEmpty() ||
            product.seller_id.isEmpty() ||
            product.seller.isEmpty() ||
            product.images.isEmpty()
        ) {
            applicationContext.getString(R.string.title_fill_out_all_fields)
        } else null

        error?.let {
            _createProductStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _createProductStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.createProduct(product)
            _createProductStatus.postValue(Event(result))
        }
    }

    fun uploadProductImages(
        mainImg: Uri,
        uri1: Uri,
        uri2: Uri,
        uri3: Uri
    ) {
        val error = if (mainImg.toString().isEmpty()) {
            applicationContext.getString(R.string.title_please_select_some_product_images)
        } else null

        error?.let {
            _uploadProductImagesStatus.postValue(Event(Resource.Error(it)))
        }
        _uploadProductImagesStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.uploadProductPictures(mainImg, uri1, uri2, uri3)
            _uploadProductImagesStatus.postValue(Event(result))
        }
    }
}