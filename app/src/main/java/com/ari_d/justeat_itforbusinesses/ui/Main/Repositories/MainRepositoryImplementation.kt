package com.ari_d.justeat_itforbusinesses.ui.Main.Repositories

import android.net.Uri
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.ari_d.justeat_itforbusinesses.data.entities.User
import com.ari_d.justeat_itforbusinesses.other.Resource
import com.ari_d.justeat_itforbusinesses.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class MainRepositoryImplementation: MainRepository {

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val users = Firebase.firestore.collection("users")
    private val sellers = Firebase.firestore.collection("sellers")
    private val storageRef = Firebase.storage.reference
    private val products = Firebase.firestore.collection("products")
    private val sellersProducts = Firebase.firestore.collection("sellers")
        .document(currentUser!!.uid)
        .collection("products")

    override suspend fun getProducts() =
        withContext(Dispatchers.IO) {
            safeCall {
                val result = products.get().await()
                val products = result.toObjects<Product>().onEach { product ->
                    currentUser?.let {
                        product.isAddedToShoppingBag = currentUser.uid in product.shoppingBagList
                        product.isAddedToFavorites = currentUser.uid in product.favoritesList
                    }
                }
                Resource.Success(products)
            }
        }

    override suspend fun createProduct(product: Product) = withContext(Dispatchers.IO) {
        safeCall {
            products.document(product.product_id).set(product).await()
            sellersProducts.document(product.product_id).set(product).await()
            Resource.Success(product)
        }
    }

    override suspend fun uploadProductPictures(
        mainImg: Uri,
        uri1: Uri,
        uri2: Uri,
        uri3: Uri
    ) = withContext(Dispatchers.IO) {
        val downloadUrls = mutableListOf<String>()
        safeCall {
            val picMain = if (mainImg.toString().isNotEmpty()) {
                storageRef.child("products/${UUID.randomUUID().toString()}")
                    .putFile(mainImg).await()
            } else null
            val pic1 = if (uri1.toString().isNotEmpty()) {
                storageRef.child("products/${UUID.randomUUID().toString()}")
                    .putFile(uri1).await()
            } else null
            val pic2 = if (uri2.toString().isNotEmpty()) {
                storageRef.child("products/${UUID.randomUUID().toString()}")
                    .putFile(uri2).await()
            } else null
            val pic3 = if (uri3.toString().isNotEmpty()) {
                storageRef.child("products/${UUID.randomUUID().toString()}")
                    .putFile(uri3).await()
            } else null
            val mainPicUrl = picMain?.metadata?.reference?.downloadUrl?.await().toString()
            val Pic1Url = pic1?.metadata?.reference?.downloadUrl?.await().toString()
            val Pic2Url = pic2?.metadata?.reference?.downloadUrl?.await().toString()
            val Pic3Url = pic3?.metadata?.reference?.downloadUrl?.await().toString()

            downloadUrls.add(0, mainPicUrl)
            downloadUrls.add(1, Pic1Url)
            downloadUrls.add(2, Pic2Url)
            downloadUrls.add(3, Pic3Url)

            Resource.Success(downloadUrls)
        }
    }

    override suspend fun getUser(uid: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val user = users.document(uid).get().await().toObject(User::class.java)
                    ?: throw IllegalStateException()
                Resource.Success(user)
            }
        }

    override suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        safeCall {
            sellers.document(product.product_id).delete().await()
            products.document(product.product_id).delete().await()
            Resource.Success(Unit)
        }
    }
}