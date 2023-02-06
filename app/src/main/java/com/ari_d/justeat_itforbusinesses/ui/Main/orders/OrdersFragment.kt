package com.ari_d.justeat_itforbusinesses.ui.Main.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.adapters.OrdersAdapter
import com.ari_d.justeat_itforbusinesses.databinding.FragmentOrdersBinding
import com.ari_d.justeat_itforbusinesses.ui.Main.ViewModels.MainViewModel
import com.bumptech.glide.RequestManager
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var ordersAdapter: OrdersAdapter

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .build()

        binding.moreOptions.setOnClickListener{
            datePicker.show(requireActivity().supportFragmentManager, "MATERIAL_DATE_PICKER")
        }

       /* setupRecyclerView()
        // subscribeToObservers()
        // viewModel.getOrders()
        getOrders()
        binding.shimmer_layout?.apply {
            startShimmer()
            isVisible = true
        }

        ordersAdapter.setOnConfirmOrdersClickLIstener { orders, i ->

        }*/
        return root
    }

  /*  private fun subscribeToObservers() {
        viewModel.getOrdersStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.empty_layout.isVisible = true
                binding.shimmer_layout.isVisible = false
                snackbar(it)
            },
            onLoading = {}
        ){ orders ->
            binding.shimmer_layout.isVisible = false
        })
    }*/

    @InternalCoroutinesApi
    private fun getOrders() {
        lifecycleScope.launch {
            viewModel.getPagingFlow2().collect {
                ordersAdapter.submitData(it)
            }
        }

        /*lifecycleScope.launch {
            ordersAdapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Error) {
                    binding.empty_layout.isVisible = true
                    binding.shimmer_layout.isVisible = false
                } else if (it.refresh is LoadState.NotLoading || it.append is LoadState.NotLoading) {
                    binding.shimmer_layout.isVisible = false
                }
            }
        }*/
    }

   /* private fun setupRecyclerView() = recycler_my_orders.apply {
        adapter = ordersAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
