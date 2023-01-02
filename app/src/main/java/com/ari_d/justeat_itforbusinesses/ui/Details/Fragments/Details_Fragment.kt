package com.ari_d.justeat_itforbusinesses.ui.Details.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ari_d.justeat_itforbusinesses.extensions.MyBounceInterpolator
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.adapters.ImagesViewPager
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.ari_d.justeat_itforbusinesses.extensions.snackbar
import com.ari_d.justeat_itforbusinesses.other.Constants.SEARCH_TIME_DELAY
import com.ari_d.justeat_itforbusinesses.other.EventObserver
import com.ari_d.justeat_itforbusinesses.ui.Details.ViewModels.DetailsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_details.TextInputEditText_product_name
import kotlinx.android.synthetic.main.fragment_details.TextInputEditText_product_price
import kotlinx.android.synthetic.main.fragment_details.TextInputLayout_product_name
import kotlinx.android.synthetic.main.fragment_details.TextInputLayout_product_price
import kotlinx.android.synthetic.main.fragment_details.progressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class Details_Fragment : Fragment(R.layout.fragment_details) {

    val viewModel: DetailsViewModel by activityViewModels()

    private lateinit var auth: FirebaseAuth

    private lateinit var product_id: String

    private var stock: String = ""

    private var contact_no: String = ""

    private val decimalFormat = DecimalFormat("#,###,###")

    private var _product: Product? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product_id = requireActivity().intent.getStringExtra("product_id").toString()
        viewModel.setUiInterface(product_id)
        subscribeToObservers()
        auth = FirebaseAuth.getInstance()
        tabLayout.background = null
        increase_layout.background = null

        var job: Job? = null

        btn_increase.setOnClickListener {
            val value = txt_stock_value.text.toString().toInt() + 1
            txt_stock_value.text = value.toString()

            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                viewModel.updateProductNo(product_id, txt_stock_value.text.toString())
            }
        }

        btn_decrease.setOnClickListener {
            val value = txt_stock_value.text.toString().toInt() - 1
            txt_stock_value.text = value.toString()

            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                viewModel.updateProductNo(product_id, txt_stock_value.text.toString())
            }
        }

        btn_product_comments.setOnClickListener {
            findNavController().navigate(
                R.id.globalActionToCommentsDialog,
                Bundle().apply { putString("productId", product_id) }
            )
        }

        btn_edit_product.setOnClickListener {
            val map = mutableMapOf<String, Any>()
            if (txt_product_name.visibility == View.GONE) {
                val fadeInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
                val fadeOutAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
                TextInputEditText_product_name.animation = fadeOutAnim
                TextInputEditText_product_price.animation = fadeOutAnim
                TextInputEditText_product_details.animation = fadeOutAnim
                TextInputLayout_product_name.isVisible = false
                TextInputLayout_product_price.isVisible = false
                TextInputLayout_product_details.isVisible = false
                txt_product_name.animation = fadeInAnim
                txt_product_price.animation = fadeInAnim
                txt_product_details.animation = fadeInAnim
                txt_product_name.isVisible = true
                txt_product_price.isVisible = true
                txt_product_details.isVisible = true
                if (TextInputEditText_product_name.text.toString().isEmpty())
                    return@setOnClickListener
                else if (TextInputEditText_product_price.text.toString().isEmpty())
                    return@setOnClickListener
                else if (TextInputEditText_product_details.text.toString().isEmpty())
                    return@setOnClickListener
                map["name"] =  TextInputEditText_product_name.text.toString()
                map["price"] =  TextInputEditText_product_price.text.toString()
                map["description"] =  TextInputEditText_product_details.text.toString()
                _product?.let { it ->
                    viewModel.updateProductDetails(
                        it,
                        map
                    )
                }
            } else {
                makeEditViewsVisible()
            }
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
                btn_edit_product.isEnabled = false
            },
            onError = {
                getString(R.string.title_unknown_error_occurred)
            }
        ) { product ->
            viewModel.getProductDetails(product_id)
        })
        viewModel.updateProductNoStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
                progressBar.isVisible = false
            },
            onLoading = { progressBar.isVisible = true }
        ) {
            viewModel.getProductDetails(product_id)
            progressBar.isVisible = false
        })
        viewModel.updateProductDetailsStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
                progressBar.isVisible = false
            },
            onLoading = { progressBar.isVisible = false }
        ) {
            snackbar(getString(R.string.title_product_updated_successfully))
            viewModel.setUiInterface(product_id)
        })
        viewModel.getProductDetailsStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {},
            onError = {
                getString(R.string.title_error_loading)
            }
        ) { product ->
            _product = product
            progressBar.isVisible = false
            scrollView.isVisible = true
            progressBar2.isVisible = false
            btn_edit_product.isVisible = true
            setUpViewPager(product.images)
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
                product_stock.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            } else if (product.stock.toInt() == 0) {
                product_stock.isVisible = true
                product_stock.text =
                    getString(R.string.title_out_of_stock)
                product_stock.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))

            } else if (product.stock.toInt() < 5) {
                product_stock.isVisible = true
                product_stock.text =
                    getString(R.string.title_product_stock, product.stock.toInt())
                product_stock.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent))
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
            btn_edit_product.isEnabled = true
        })
    }

    private fun setUpViewPager(images: List<String>) = recycler_product_details.apply {
        val viewPagerAdapter = ImagesViewPager(images)
        adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, recycler_product_details) { tab, position ->
        }.attach()
    }

    private fun makeEditViewsVisible() {
        val fadeInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val fadeOutAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        txt_product_name.animation = fadeOutAnim
        txt_product_price.animation = fadeOutAnim
        txt_product_details.animation = fadeOutAnim
        txt_product_name.isVisible = false
        txt_product_price.isVisible = false
        txt_product_details.isVisible = false
        TextInputEditText_product_name.animation = fadeInAnim
        TextInputEditText_product_price.animation = fadeInAnim
        TextInputEditText_product_details.animation = fadeInAnim
        TextInputLayout_product_name.isVisible = true
        TextInputLayout_product_price.isVisible = true
        TextInputLayout_product_details.isVisible = true
        TextInputEditText_product_name.setText(_product?.name.toString())
        TextInputEditText_product_price.setText(_product?.price)
        TextInputEditText_product_details.setText(_product?.description.toString())
    }
}