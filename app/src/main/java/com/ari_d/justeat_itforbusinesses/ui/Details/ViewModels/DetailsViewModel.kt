package com.ari_d.justeat_itforbusinesses.ui.Details.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ari_d.justeat_itforbusinesses.data.entities.Comment
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.ari_d.justeat_itforbusinesses.other.Constants
import com.ari_d.justeat_itforbusinesses.other.Event
import com.ari_d.justeat_itforbusinesses.other.Resource
import com.ari_d.justeat_itforbusinesses.ui.Details.Repositories.DetailsRepositoryImplementation
import com.ari_d.justeat_itforbusinesses.ui.Details.Repositories.DetailsRepository
import com.ari_d.justeat_itforbusinesses.data.pagingsource.CommentsPagingSource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: DetailsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _getProductDetailsStatus = MutableLiveData<Event<Resource<Product>>>()
    val getProductDetailsStatus : LiveData<Event<Resource<Product>>> = _getProductDetailsStatus

    private val _setUiInterfaceStatus = MutableLiveData<Event<Resource<Product>>>()
    val setUiInterfaceStatus : LiveData<Event<Resource<Product>>> = _setUiInterfaceStatus

    private val _createCommentStatus = MutableLiveData<Event<Resource<Comment>>>()
    val createCommentStatus: LiveData<Event<Resource<Comment>>> = _createCommentStatus

    private val _deleteCommentStatus = MutableLiveData<Event<Resource<Comment>>>()
    val deleteCommentStatus: LiveData<Event<Resource<Comment>>> = _deleteCommentStatus

    private val _commentForProductStatus = MutableLiveData<Event<Resource<List<Comment>>>>()
    val commentForProductStatus: LiveData<Event<Resource<List<Comment>>>> = _commentForProductStatus

    private val _updateProductNoStatus = MutableLiveData<Event<Resource<Unit>>>()
    val updateProductNoStatus: LiveData<Event<Resource<Unit>>> = _updateProductNoStatus

    private val _updateProductDetailsStatus = MutableLiveData<Event<Resource<Unit>>>()
    val updateProductDetailsStatus: LiveData<Event<Resource<Unit>>> = _updateProductDetailsStatus

    fun getProductDetails(product_id: String) {
        _getProductDetailsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getProductDetails(product_id)
            _getProductDetailsStatus.postValue(Event(result))
        }
    }

    fun setUiInterface(product_id: String) {
        _setUiInterfaceStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.setUiInterface(product_id)
            _setUiInterfaceStatus.postValue(Event(result))
        }
    }

    fun createComment(commentText: String, product_id: String) {
        if (commentText.isEmpty()) return
        _createCommentStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.createComment(commentText, product_id)
            _createCommentStatus.postValue(Event(result))
        }
    }

    fun deleteComment(comment: Comment) {
        _deleteCommentStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch (dispatcher){
            val result = repository.deleteComment(comment)
            _deleteCommentStatus.postValue(Event(result))
        }
    }

    fun updateProductNo(product_id: String, value: String) {
        _updateProductNoStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.updateProductNo(product_id, value)
            _updateProductNoStatus.postValue(Event(result))
        }
    }

    fun updateProductDetails(product: Product, productt: Map<String, Any>) {
        _updateProductDetailsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.updateProductDetails(product, productt)
            _updateProductDetailsStatus.postValue(Event(result))
        }
    }

    fun getPagingFlow(productId: String): Flow<PagingData<Comment>> {
        return Pager(PagingConfig(Constants.PAGE_SIZE)) {
            CommentsPagingSource(
                FirebaseFirestore.getInstance(),
                repository as DetailsRepositoryImplementation,
                productId
            )
        }.flow.cachedIn(viewModelScope)
    }
}