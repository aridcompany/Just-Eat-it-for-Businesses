package com.ari_d.justeat_itforbusinesses.ui.Auth.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ari_d.justeat_itforbusinesses.extensions.alertDialog
import com.ari_d.justeat_itforbusinesses.R
import com.ari_d.justeat_itforbusinesses.ui.Auth.Auth_Activity
import com.ari_d.justeat_itforbusinesses.extensions.slideUpViews
import com.ari_d.justeat_itforbusinesses.extensions.snackbar
import com.ari_d.justeat_itforbusinesses.other.EventObserver
import com.ari_d.justeat_itforbusinesses.ui.Auth.ViewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_reset_password.*

@AndroidEntryPoint
class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        btn_back.setOnClickListener {
            requireActivity().finish()
        }
        btn_resetPassword.setOnClickListener {
            viewModel.resetPassword(TextInputEditText_email.text.toString().replace(" ", ""))
        }

        slideUpViews(
            requireContext(),
            btn_back,
            textView,
            TextInputEditText_email,
            textView2,
            btn_resetPassword,
            btn_navigation_signIn
        )

        val login = LoginFragment()

        btn_navigation_signIn.setOnClickListener {
            (activity as Auth_Activity).setCurrentFragment(login)
        }
    }

    private fun subscribeToObservers() {
        viewModel.restPasswordStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressBar.isVisible = false
                btn_resetPassword.isVisible = true
                snackbar(it)
            },
            onLoading = {
                progressBar.isVisible = true
                btn_resetPassword.isVisible = false
            }
        ) {
            progressBar.isVisible = false
            btn_resetPassword.isVisible = true
            alertDialog(
                getString(R.string.email_sent),
                getString(R.string.email_sent_msg),
                requireActivity().resources.getDrawable(R.drawable.ic_mark_email_read)
            )
        })
    }
}