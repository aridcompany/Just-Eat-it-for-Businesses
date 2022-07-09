package com.ari_d.justeat_itforbusinesses.ui.Details.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ari_d.justeat_itforbusinesses.extensions.MyBounceInterpolator
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.adapters.ImagesViewPager
import com.ari_d.justeat_itforbusinesses.other.EventObserver
import com.ari_d.justeat_itforbusinesses.ui.Details.ViewModels.DetailsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_details.*
import java.text.DecimalFormat

@AndroidEntryPoint
class Details_Fragment : Fragment(R.layout.fragment_details) {

    val viewModel: DetailsViewModel by activityViewModels()

    private lateinit var auth: FirebaseAuth

    private lateinit var product_id: String

    private var stock: String = ""

    private var contact_no: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product_id = requireActivity().intent.getStringExtra("product_id").toString()
        viewModel.setUiInterface(product_id)
        subscribeToObservers()
        auth = FirebaseAuth.getInstance()
        tabLayout.background = null
        increase_layout.background = null

        btn_edit_product.setOnClickListener {

        }
        
        btn_increase.setOnClickListener {
            val value = txt_stock_value.text.toString().toInt() + 1
            txt_stock_value.text = value.toString()
        }

        btn_decrease.setOnClickListener {
            val value = txt_stock_value.text.toString().toInt() - 1
            txt_stock_value.text = value.toString()
        }

        btn_product_comments.setOnClickListener {
            findNavController().navigate(
                R.id.globalActionToCommentsDialog,
                Bundle().apply { putString("productId", product_id) }
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers() {
        val myAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
        val interpolator = MyBounceInterpolator(0.2, 20.0)
        myAnim.interpolator = interpolator

        viewModel.setUiInterfaceStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {
                progressBar.isVisible = true
                scrollView.isVisible = false
                progressBar2.isVisible = true
            },
            onError = {
                getString(R.string.title_unknown_error_occurred)
            }
        ) { product ->
            viewModel.getProductDetails(product_id)
        })
        viewModel.getProductDetailsStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {},
            onError = {
                getString(R.string.title_error_loading)
            }
        ) { product ->
            progressBar.isVisible = false
            scrollView.isVisible = true
            progressBar2.isVisible = false
            btn_edit_product.isVisible = true
            setUpViewPager(product.images)
            val decimalFormat = DecimalFormat("#,###,###")
            txt_product_name.text = product.name
            txt_product_price.text =
                getString(R.string.title_naira_sign) + decimalFormat.format(product.price.toInt())
            txt_product_shipping_fee.text =
                getString(R.string.title_shipping_fee) + decimalFormat.format(product.shipping_fee.toInt())
            txt_product_details.text = product.description
            stock = product.stock
            txt_stock_value
            contact_no = product.contact_no


            if (product.stock.toInt() > 4) {
                product_stock.isVisible = true
                product_stock.text = getString(R.string.title_in_stock)
            } else if (product.stock.toInt() < 5) {
                product_stock.isVisible = true
                product_stock.text =
                    getString(R.string.title_product_stock, product.stock.toInt())
            }
            txt_stock_value.text = product.stock
            when {
                product.favoritesList.size == 1 -> product_likes.text =
                    getString(R.string.title_like, product.favoritesList.size)
                product.favoritesList.size > 1 -> product_likes.text =
                    decimalFormat.format(product.favoritesList.size) + " " + getString(R.string.title_likes)
                product.favoritesList.isEmpty() -> product_likes.text =
                    getString(R.string.title_no_likes)
            }
        })
    }

    private fun setUpViewPager(images: List<String>) = recycler_product_details.apply {
        val viewPagerAdapter = ImagesViewPager(images)
        adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, recycler_product_details) { tab, position ->
        }.attach()
    }
}