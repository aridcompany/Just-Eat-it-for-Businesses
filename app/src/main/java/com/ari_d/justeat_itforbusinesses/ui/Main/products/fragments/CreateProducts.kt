package com.ari_d.justeat_itforbusinesses.ui.Main.products.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.databinding.FragmentCreateProductsBinding
import com.ari_d.justeat_itforbusinesses.other.EventObserver
import com.ari_d.justeat_itforbusinesses.ui.Main.ViewModels.MainViewModel
import com.ari_d.justeat_itforbusinesses.ui.Main.orders.DashboardViewModel
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_products.*

@AndroidEntryPoint
class CreateProducts: Fragment() {

    private var _binding: FragmentCreateProductsBinding? = null
    private lateinit var imageView: ImageView
    val viewModel: MainViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subsrcibeToObservers()

        binding.dropdownMenu.setOnClickListener { view ->
            val popUpMenu = PopupMenu(requireContext(), view)
            popUpMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.available -> {
                        binding.available.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                        binding.available.text = getString(R.string.title_available)
                        true
                    }
                    R.id.unavailable -> {
                        binding.available.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.available.text = getString(R.string.title_unavailable_)
                        true
                    }
                    else -> false
                }
            }
            popUpMenu.inflate(R.menu.availabity_menu)
            popUpMenu.show()
        }

        binding.available.setOnClickListener { view ->
            val popUpMenu = PopupMenu(requireContext(), binding.dropdownMenu)
            popUpMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.available -> {
                        binding.available.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                        binding.available.text = getString(R.string.title_available)
                        true
                    }
                    R.id.unavailable -> {
                        binding.available.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.available.text = getString(R.string.title_unavailable_)
                        true
                    }
                    else -> false
                }
            }
            popUpMenu.inflate(R.menu.availabity_menu)
            popUpMenu.show()
        }

        binding.editProfilePic.setOnClickListener {
            cropActivityResultLauncher.launch(null)
            imageView = binding.imgProfile
        }

        binding.imgProduct.setOnClickListener {
            cropActivityResultLauncher.launch(null)
            imageView = binding.imgProduct
        }

        binding.imgProduct2.setOnClickListener {
            cropActivityResultLauncher.launch(null)
            imageView = binding.imgProduct2
        }

        binding.imgProduct3.setOnClickListener {
            cropActivityResultLauncher.launch(null)
            imageView = binding.imgProduct3
        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                imageView.setImageURI(uri)
            }
        }
    }

    private fun subsrcibeToObservers() {
        viewModel.createProductStatus.observe(viewLifecycleOwner, EventObserver(
          onLoading = {},
          onError = {}
        ){})
        viewModel.uploadProductImagesStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {},
            onError = {}
        ){})
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}