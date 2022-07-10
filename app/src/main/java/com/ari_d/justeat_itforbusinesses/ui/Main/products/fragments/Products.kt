package com.ari_d.justeat_itforbusinesses.ui.Main.products.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.adapters.ProductsAdapter
import com.ari_d.justeat_itforbusinesses.databinding.FragmentsProductsBinding
import com.ari_d.justeat_itforbusinesses.extensions.snackbar
import com.ari_d.justeat_itforbusinesses.other.EventObserver
import com.ari_d.justeat_itforbusinesses.ui.Details.Details_Activity
import com.ari_d.justeat_itforbusinesses.ui.Main.ViewModels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragments_products.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
@AndroidEntryPoint
class Products : Fragment() {

    private var _binding: FragmentsProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val productsViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var productsAdapter: ProductsAdapter

    private var curAddedIndex: Int? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentsProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        binding.shimmerLayout.isVisible = true
        binding.shimmerLayout.startShimmer()

        binding.productsRecycler.isVisible = false
        getPosts()

        binding.homeSwipe.setOnRefreshListener {
            getPosts()
        }

        setupRecyclerView()

        productsAdapter.setOnDeleteProductClickListener { product, i ->
            curAddedIndex = i
            val currentUser = auth.currentUser
            if (currentUser == null) {
                snackbar(getString(R.string.title_signIn_to_continue))
            }
            currentUser?.let {
                productsViewModel.deleteProduct(product)
            }
        }
        productsAdapter.setOnNavigateToProductsDetailsClickListener { product, i ->
            val currentUser = auth.currentUser
            if (currentUser == null) {
                snackbar(getString(R.string.title_signIn_to_continue))
            }
            currentUser?.let {
                Intent(requireActivity(), Details_Activity::class.java).also {
                    it.putExtra("product_id", product.product_id)
                    startActivity(it)
                }
            }
        }

        return root
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun getPosts() {
        lifecycleScope.launch {
            productsViewModel.getPagingFlow().collect {
                productsAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            productsAdapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading || it.append is LoadState.Loading) {
                    binding.emptyLayout.isVisible = false
                } else if (it.refresh is LoadState.Error) {
                    binding.emptyLayout.isVisible = true
                    binding.shimmerLayout.apply {
                        stopShimmer()
                        isVisible = false
                    }
                    binding.homeSwipe.isRefreshing = false
                } else if (it.refresh is LoadState.NotLoading || it.append is LoadState.NotLoading) {
                    @Suppress("DEPRECATION")
                    if (userVisibleHint) {
                        binding.shimmerLayout.apply {
                            stopShimmer()
                            isVisible = false
                        }
                    }
                    binding.productsRecycler.isVisible = true
                    binding.homeSwipe.isRefreshing = false
                    binding.emptyLayout.isVisible = false
                    binding.homeSwipe.isRefreshing = false
                    binding.productsRecycler.adapter?.stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
            }
        }
    }

    private fun setupRecyclerView() = binding.productsRecycler.apply {
        layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        adapter = productsAdapter
        itemAnimator = null
        binding.productsRecycler.adapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun subscribeToObservers() {
        productsViewModel.deleteProductStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {
                binding.progressBar.isVisible = true
            },
            onError = {
                snackbar(it)
                binding.progressBar.isVisible = false
            }
        ) {
            binding.progressBar.isVisible = true
            snackbar(getString(R.string.title_product_deleted_sc))
            getPosts()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}