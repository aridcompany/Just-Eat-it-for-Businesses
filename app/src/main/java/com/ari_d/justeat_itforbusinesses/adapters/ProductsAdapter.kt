package com.ari_d.justeat_itforbusinesses.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_create_products.view.img_product
import kotlinx.android.synthetic.main.product_layout.view.*
import javax.inject.Inject

class ProductsAdapter @Inject constructor(
    private val glide: RequestManager
) : PagingDataAdapter<Product, ProductsAdapter.ProductsViewHolder>(Companion) {
    inner class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_product: ImageView = itemView.img_product
        val btn_delete_products: ImageView = itemView.btn_delete_product
        val txt_productName: TextView = itemView.txt_product_name
        val txt_productPrice: TextView = itemView.txt_product_price
    }

    companion object : DiffUtil.ItemCallback<Product>() {
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.product_id == newItem.product_id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.product_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = getItem(position) ?: return
        holder.apply {
            glide.load(product.images[0]).into(img_product)
            txt_productName.text = product.name
            txt_productPrice.text = "₦" + product.price

            btn_delete_products.setOnClickListener {
                onDeleteProductClickListener?.let { click ->
                    click(product, holder.layoutPosition)
                }
            }
            itemView.setOnClickListener {
                onNavigateToProductDetailsListener?.let { click ->
                    click(product, holder.layoutPosition)
                }
            }
        }
    }

    private var onDeleteProductClickListener: ((Product, Int) -> Unit)? = null

    private var onNavigateToProductDetailsListener: ((Product, Int) -> Unit)? = null

    fun setOnDeleteProductClickListener(listener: (Product, Int) -> Unit) {
        onDeleteProductClickListener = listener
    }

    fun setOnNavigateToProductsDetailsClickListener(listener: (Product, Int) -> Unit) {
        onNavigateToProductDetailsListener = listener
    }
}