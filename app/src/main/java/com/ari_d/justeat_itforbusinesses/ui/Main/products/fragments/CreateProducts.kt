package com.ari_d.justeat_itforbusinesses.ui.Main.products.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ari_d.justeat_itforbusinesses.extensions.snackbar
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.data.entities.Product
import com.ari_d.justeat_itforbusinesses.databinding.FragmentCreateProductsBinding
import com.ari_d.justeat_itforbusinesses.other.EventObserver
import com.ari_d.justeat_itforbusinesses.ui.Main.ViewModels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_products.*
import java.util.*

@AndroidEntryPoint
class CreateProducts : Fragment() {

    private var _binding: FragmentCreateProductsBinding? = null
    val viewModel: MainViewModel by viewModels()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var sellerName: String = ""
    private var mainUrl: String = ""
    private var uri1: String = ""
    private var uri2: String = ""
    private var uri3: String = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
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
    private lateinit var cropActivityResultLauncher2: ActivityResultLauncher<Any?>
    private lateinit var cropActivityResultLauncher3: ActivityResultLauncher<Any?>
    private lateinit var cropActivityResultLauncher4: ActivityResultLauncher<Any?>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        subsrcibeToObservers()

        binding.btnSaveProduct.setOnClickListener {
            viewModel.getUser(currentUser!!.uid)
        }

        binding.dropdownMenu.setOnClickListener { view ->
            val popUpMenu = PopupMenu(requireContext(), view)
            popUpMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.available -> {
                        binding.available.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        binding.available.text = getString(R.string.title_available)
                        true
                    }
                    R.id.unavailable -> {
                        binding.available.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
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
                        binding.available.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.green
                            )
                        )
                        binding.available.text = getString(R.string.title_available)
                        true
                    }
                    R.id.unavailable -> {
                        binding.available.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
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
        }

        binding.imgProfile.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }

        binding.imgProduct.setOnClickListener {
            cropActivityResultLauncher2.launch(null)
        }

        binding.imgProduct2.setOnClickListener {
            cropActivityResultLauncher3.launch(null)
        }

        binding.imgProduct3.setOnClickListener {
            cropActivityResultLauncher4.launch(null)
        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                binding.imgProfile.setImageURI(uri)
                mainUrl = uri.toString()
            }
        }

        cropActivityResultLauncher2 = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                binding.imgProduct.setImageURI(uri)
                uri1 = uri.toString()
            }
        }

        cropActivityResultLauncher3 = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                binding.imgProduct2.setImageURI(uri)
                uri2 = uri.toString()
            }
        }

        cropActivityResultLauncher4 = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                binding.imgProduct3.setImageURI(uri)
                uri3 = uri.toString()
            }
        }

        return root
    }

    private fun subsrcibeToObservers() {
        viewModel.createProductStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {},
            onError = {
                snackbar(it)
                progressBar.isVisible = false
                binding.btnSaveProduct.isVisible = true
            }
        ) {
            snackbar(getString(R.string.title_successfully_uploaded_product))
            progressBar.isVisible = false
            binding.btnSaveProduct.isVisible = true
        })
        viewModel.uploadProductImagesStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {},
            onError = {
                progressBar.isVisible = false
                binding.btnSaveProduct.isVisible = true
                snackbar(it)
            }
        ) { ImageUrls ->
            if (binding.available.text.toString() == "Available") {
                viewModel.createProduct(
                    Product(
                        name = binding.TextInputEditTextProductName.text.toString(),
                        description = binding.TextInputEditTextProductDescription.text.toString(),
                        images = ImageUrls,
                        price = binding.TextInputEditTextProductPrice.text.toString(),
                        product_id = UUID.randomUUID().toString(),
                        seller_id = FirebaseAuth.getInstance().currentUser!!.uid,
                        seller = sellerName,
                        shipping_fee = binding.TextInputEditTextProductShippingFee.text.toString(),
                        contact_no = binding.TextInputEditTextProductContactNo.text.toString(),

                        isAvailable = true,
                        stock = binding.TextInputEditTextProductStock.text.toString(),
                    )
                )
            } else
                viewModel.createProduct(
                    Product(
                        name = binding.TextInputEditTextProductName.text.toString(),
                        description = binding.TextInputEditTextProductDescription.text.toString(),
                        images = ImageUrls,
                        price = binding.TextInputEditTextProductPrice.text.toString(),
                        product_id = UUID.randomUUID().toString(),
                        seller_id = FirebaseAuth.getInstance().currentUser!!.uid,
                        seller = sellerName,
                        shipping_fee = binding.TextInputEditTextProductShippingFee.text.toString(),
                        contact_no = binding.TextInputEditTextProductContactNo.text.toString(),

                        isAvailable = false,
                        stock = binding.TextInputEditTextProductStock.text.toString(),
                    )
                )
        })
        viewModel.getUserStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {
                binding.progressBar.isVisible = true
                binding.btnSaveProduct.isVisible = false
            },
            onError = {
                progressBar.isVisible = false
                binding.btnSaveProduct.isVisible = true
                snackbar(it)
            }
        ) { user ->
            sellerName = user.name
            viewModel.uploadProductImages(
                mainUrl.toUri(),
                uri1.toUri(),
                uri2.toUri(),
                uri3.toUri()
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}