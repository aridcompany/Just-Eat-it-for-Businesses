package com.ari_d.justeat_itforbusinesses.ui.Details.Repositories

import com.ari_d.justeat_itforbusinesses.data.entities.Comment
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.ari_d.justeat_itforbusinesses.data.entities.User
import com.ari_d.justeat_itforbusinesses.other.Resource
import com.ari_d.justeat_itforbusinesses.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class DetailsRepositoryImplementation : DetailsRepository {

    private val products = Firebase.firestore.collection("products")
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val users = Firebase.firestore.collection("users")
    private val firestore = Firebase.firestore
    private val comments = Firebase.firestore.collection("comments")

    override suspend fun getProductDetails(product_id: String) = withContext(Dispatchers.IO) {
        safeCall {
            val _product = products.document(product_id).get().await()
            val product = _product.toObject<Product>()
            Resource.Success(product!!)
        }
    }

    override suspend fun setUiInterface(product_id: String) = withContext(Dispatchers.IO) {
        safeCall {
            val product = products.document(product_id)
                .get()
                .await()
                .toObject<Product>()
            if (product!!.stock.toInt() == 0) {
                products.document(product_id)
                    .update(
                        "available",
                        false
                    )
            }
            Resource.Success(product)
        }
    }

    override suspend fun createComment(commentText: String, productId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val uid = currentUser!!.uid
                val commentId = UUID.randomUUID().toString()
                val user = getUser(uid).data!!
                val comment = Comment(
                    commentId,
                    productId,
                    uid,
                    user.name,
                    commentText
                )
                comments.document(commentId).set(comment).await()
                Resource.Success(comment)
            }
        }

    override suspend fun deleteComment(comment: Comment) =
        withContext(Dispatchers.IO) {
            safeCall {
                comments.document(comment.commentId).delete().await()
                Resource.Success(comment)
            }
        }

    override suspend fun getCommentForProduct(productId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val commentsForProduct = comments
                    .whereEqualTo("productId", productId)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(Comment::class.java)
                    .onEach { comment ->
                        val user = getUser(comment.uid).data!!
                        comment.name = user.name
                    }
                Resource.Success(commentsForProduct)
            }
        }

    override suspend fun updateProductNo(
        product_id: String,
        value: String
    ) = withContext(Dispatchers.IO) {
        safeCall {
            if (value.toInt() > 0) {
                products.document(product_id)
                    .update(
                        "stock",
                        value
                    )
                products.document(product_id)
                    .update(
                        "available",
                        true
                    )
            } else if (value.toInt() == 0) {
                products.document(product_id)
                    .update(
                        "available",
                        false
                    )
                products.document(product_id)
                    .update(
                        "stock",
                        "0"
                    )
            } else if (value.toInt() < 0) {
                products.document(product_id)
                    .update(
                        "available",
                        false
                    )
                products.document(product_id)
                    .update(
                        "stock",
                        "0"
                    )
            }
            Resource.Success(Unit)
        }
    }

    override suspend fun updateProductDetails(
        product: Product,
        productt: Map<String, Any>
    ) = withContext(Dispatchers.IO) {
        safeCall {
            products.document(product.product_id)
                .set(productt, SetOptions.merge())
            Resource.Success(Unit)
        }
    }

    override suspend fun getUser(uid: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val user = users.document(uid).get().await().toObject<User>()
                    ?: throw IllegalStateException()
                Resource.Success(user)
            }
        }
}