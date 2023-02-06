package com.ari_d.justeat_itforbusinesses.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.data.entities.Orders
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.orders_layout.view.*
import javax.inject.Inject

class OrdersAdapter @Inject constructor(
    private val glide: RequestManager
) : PagingDataAdapter<Orders, OrdersAdapter.OrdersViewHolder>(Companion) {
    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_Orders: ImageView = itemView.order_image
        val txt_OrdersName: TextView = itemView.order_name
        val txt_OrdersPrice: TextView = itemView.order_price
        val txt_OrdersId: TextView = itemView.order_id
        val btn_confirm: ImageView = itemView.confirm
        val btn_revert: ImageView = itemView.revert
    }

    companion object : DiffUtil.ItemCallback<Orders>() {
        override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
            return oldItem.orderID == newItem.orderID
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_unconfirmed_orders,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val Order = getItem(position) ?: return
        holder.apply {
            glide.load(Order.image).into(img_Orders)
            txt_OrdersName.text = Order.name
            txt_OrdersPrice.text = "₦" + Order.price + "   X" + Order.quantity
            txt_OrdersId.text = itemView.context.getString(R.string.title_order_id) +  Order.orderID.substring(0, 12)

            btn_confirm.setOnClickListener {
                onConfirmOrdersClickListener?.let { click ->
                    click(Order, holder.layoutPosition)
                }
            }
            btn_revert.setOnClickListener {
                onDeleteOrdersClickListener?.let { click ->
                    click(Order, holder.layoutPosition)
                }
            }
        }
    }

    private var onConfirmOrdersClickListener: ((Orders, Int) -> Unit)? = null
    private var onDeleteOrdersClickListener: ((Orders, Int) -> Unit)? = null

    fun setOnConfirmOrdersClickLIstener(listener: (Orders, Int) -> Unit) {
        onConfirmOrdersClickListener = listener
    }

    fun setOnDeleteOrdersClickLIstener(listener: (Orders, Int) -> Unit) {
        onDeleteOrdersClickListener = listener
    }
}