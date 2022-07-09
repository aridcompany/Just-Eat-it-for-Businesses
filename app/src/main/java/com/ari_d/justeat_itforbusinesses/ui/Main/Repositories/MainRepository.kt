package com.ari_d.justeat_itforbusinesses.ui.Main.Repositories

import android.net.Uri
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.ari_d.justeat_itforbusinesses.data.entities.User
import com.ari_d.justeat_itforbusinesses.other.Resource

interface MainRepository {

    suspend fun getProducts() : Resource<List<Product>>

    suspend fun createProduct(product: Product): Resource<Product>

    suspend fun uploadProductPictures(mainImg: Uri, uri1: Uri, uri2: Uri, uri3: Uri): Resource<MutableList<String>>

    suspend fun getUser(uid: String) : Resource<User>

    suspend fun deleteProduct(product: Product) : Resource<Unit>

}