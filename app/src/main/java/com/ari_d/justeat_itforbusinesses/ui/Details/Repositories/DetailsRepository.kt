package com.ari_d.justeat_itforbusinesses.ui.Details.Repositories

import com.ari_d.justeat_itforbusinesses.data.entities.Comment
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.ari_d.justeat_itforbusinesses.data.entities.User
import com.ari_d.justeat_itforbusinesses.other.Resource

interface DetailsRepository {

    suspend fun getProductDetails(product_id: String) : Resource<Product>

    suspend fun setUiInterface(product_id: String) : Resource<Product>

    suspend fun getUser(uid: String) : Resource<User>

    suspend fun createComment(commentText: String, postId: String): Resource<Comment>

    suspend fun deleteComment(comment: Comment) : Resource<Comment>

    suspend fun getCommentForProduct(productId: String) : Resource<List<Comment>>
}