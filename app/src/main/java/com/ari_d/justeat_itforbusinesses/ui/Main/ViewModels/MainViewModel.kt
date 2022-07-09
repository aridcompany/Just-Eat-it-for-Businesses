package com.ari_d.justeat_itforbusinesses.ui.Main.ViewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.ari_d.justeat_itforbusinesses.data.entities.User
import com.ari_d.justeat_itforbusinesses.data.pagingsource.ProductsPagingSource
import com.ari_d.justeat_itforbusinesses.other.Constants.PAGE_SIZE
import com.ari_d.justeat_itforbusinesses.other.Event
import com.ari_d.justeat_itforbusinesses.other.Resource
import com.ari_d.justeat_itforbusinesses.ui.Main.Repositories.MainRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    private val _getUserStatus = MutableLiveData<Event<Resource<User>>>()
    val getUserStatus: LiveData<Event<Resource<User>>> = _getUserStatus

    private val _deleteProductStatus = MutableLiveData<Event<Resource<Unit>>>()
    val deleteProductStatus: LiveData<Event<Resource<Unit>>> = _deleteProductStatus

    fun getPagingFlow(): Flow<PagingData<Product>> {
        val pagingSource = ProductsPagingSource(
            FirebaseFirestore.getInstance(),
        )
        return Pager(PagingConfig(PAGE_SIZE)) {
            pagingSource
        }.flow.cachedIn(viewModelScope)
    }

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

    fun getUser (uid: String) {
        _getUserStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch (dispatcher){
            val result = repository.getUser(uid)
            _getUserStatus.postValue(Event(result))
        }
    }

    fun deleteProduct(product: Product) {
        _deleteProductStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch (dispatcher) {
            val result = repository.deleteProduct(product)
            _deleteProductStatus.postValue(Event(result))
        }
    }
}