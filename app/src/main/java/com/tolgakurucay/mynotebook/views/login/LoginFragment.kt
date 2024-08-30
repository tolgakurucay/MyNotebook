package com.tolgakurucay.mynotebook.views.login

import android.content.Intent
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.databinding.FragmentLoginBinding
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialog
import com.tolgakurucay.mynotebook.viewmodels.login.LoginFragmentViewModel
import com.tolgakurucay.mynotebook.views.DataBindingFragment
import com.tolgakurucay.mynotebook.views.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment @Inject constructor() :
    DataBindingFragment<FragmentLoginBinding>(R.layout.fragment_login), ILoginFragment {

    lateinit var viewModel: LoginFragmentViewModel

    @Inject
    lateinit var loadingDialog: CustomLoadingDialog


    override fun onDataBound() {
        super.onDataBound()
        binding.listener = this
        viewModel = ViewModelProvider(this)[LoginFragmentViewModel::class.java]
        textChangeListener()
        listenEvents()
    }


    private fun validateFields(): Boolean {
        val emailHelperText = binding.tilEmail.helperText
        val passwordHelperText = binding.passwordSignInLayout.helperText
        return emailHelperText == null && passwordHelperText == null

    }

    private fun textChangeListener() {

        binding.emailSignInInput.addTextChangedListener {
            viewModel.validateEmail(binding.emailSignInInput.text.toString())

        }
        binding.passwordSignInInput.addTextChangedListener {
            viewModel.validatePassword(binding.passwordSignInInput.text.toString())
        }

    }

    private fun listenEvents() {
        viewModel.emailMessage.observe(viewLifecycleOwner) {
            it?.let { mailMessage ->
                when (mailMessage) {
                    "Enter An Mail" -> binding.tilEmail.helperText =
                        resources.getText(R.string.enteranmail)

                    "validated" -> binding.tilEmail.helperText = null
                    "Invalid Email" -> binding.tilEmail.helperText =
                        resources.getText(R.string.invalidemail)

                }

            }
        }

        viewModel.passwordMessage.observe(viewLifecycleOwner) {
            it?.let { passwordMessage ->
                when (passwordMessage) {
                    "Enter An Password" -> binding.passwordSignInLayout.helperText =
                        resources.getText(R.string.enteranpassword)

                    "validated" -> binding.passwordSignInLayout.helperText = null
                }

            }
        }

        viewModel.signMessageLiveData.observe(viewLifecycleOwner) { signMessage ->
            if (signMessage == "okay") {
                //Intent
                val intent = Intent(activity, MainActivity::class.java)
                Util.saveSignType(requireActivity(), "email")
                startActivity(intent)
                this.activity?.finish()

            } else if (signMessage == "notverified") {

                showAlertDialog(
                    getString(R.string.emailnotverifiedlabel),
                    getString(R.string.emailnotverified),
                    R.drawable.email,
                    getString(R.string.okay)
                )

            } else {
                showAlertDialog(
                    getString(R.string.mailpasswordwronglabel),
                    getString(R.string.mailpasswordwrong),
                    R.drawable.ic_baseline_person_24,
                    getString(R.string.okay)
                )


            }

        }

        viewModel.loadingDialog.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    loadingDialog.show(childFragmentManager, "started")
                } else {
                    loadingDialog.dismiss()
                }
            }
        }


    }

    override fun onForgotPasswordClick() =
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())


    override fun onGoogleSignInClick() {
        val intent = Intent(this.activity, SocialLoginActivity::class.java)
        intent.putExtra("signType", "googleSignIn")
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onFacebookSignInClick() {
        val intent = Intent(this.activity, SocialLoginActivity::class.java)
        intent.putExtra("signType", "facebookSignIn")
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onPhoneSignInClick() {
        val intent = Intent(this.activity, SocialLoginActivity::class.java)
        intent.putExtra("signType", "phoneSignIn")
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onSignUpClick() =
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())


    override fun onSignInClick() {
        if (validateFields()) {
            viewModel.signInWithEmailAndPassword(
                binding.emailSignInInput.text.toString(),
                binding.passwordSignInInput.text.toString()
            )
        } else {

            Toast.makeText(this.context, R.string.blankfields, Toast.LENGTH_LONG).show()

        }
    }


}