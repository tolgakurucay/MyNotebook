package com.tolgakurucay.mynotebook.views.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.databinding.FragmentLoginBinding
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialog
import com.tolgakurucay.mynotebook.viewmodels.login.LoginFragmentViewModel
import com.tolgakurucay.mynotebook.views.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment @Inject constructor(): Fragment() {

    private lateinit var binding:FragmentLoginBinding
    lateinit var viewModel: LoginFragmentViewModel
    @Inject lateinit var loadingDialog:CustomLoadingDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel=ViewModelProvider(this)[LoginFragmentViewModel::class.java]

        textChangeListener()
        buttonClickListener()
        observeLiveData()

    }



    private fun buttonClickListener(){
        binding.textViewForgotYourPassword.setOnClickListener {
           val direction=LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            Navigation.findNavController(this.requireView()).navigate(direction)

        }

        binding.textViewRegister.setOnClickListener {
            val direction=LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            Navigation.findNavController(this.requireView()).navigate(direction)

        }
        binding.buttonSignInNow.setOnClickListener {
            if (validateFields()){
                viewModel.signInWithEmailAndPassword(binding.emailSignInInput.text.toString(),binding.passwordSignInInput.text.toString())
            }
            else
            {

                Toast.makeText(this.context,R.string.blankfields,Toast.LENGTH_LONG).show()

            }
        }

        binding.imageViewPhoneSign.setOnClickListener {
            val intent=Intent(this.activity,SocialLoginActivity::class.java)
            intent.putExtra("signType","phoneSignIn")
            startActivity(intent)
            requireActivity().finish()

        }
        binding.imageViewFacebookSign.setOnClickListener {


            val intent=Intent(this.activity,SocialLoginActivity::class.java)
            intent.putExtra("signType","facebookSignIn")
            startActivity(intent)
            requireActivity().finish()

        }
        binding.imageViewGoogleSign.setOnClickListener {

            val intent=Intent(this.activity,SocialLoginActivity::class.java)
            intent.putExtra("signType","googleSignIn")
            startActivity(intent)
            requireActivity().finish()

        }


    }

    private fun validateFields() : Boolean{
        val emailHelperText=binding.emailSignInLayout.helperText
        val passwordHelperText=binding.passwordSignInLayout.helperText
        return emailHelperText==null && passwordHelperText==null

    }

    private fun textChangeListener(){

        binding.emailSignInInput.addTextChangedListener {
            viewModel.validateEmail(binding.emailSignInInput.text.toString())

        }
        binding.passwordSignInInput.addTextChangedListener {
            viewModel.validatePassword(binding.passwordSignInInput.text.toString())
        }

    }

    private fun observeLiveData(){
        viewModel.emailMessage.observe(viewLifecycleOwner, Observer {
            it?.let { mailMessage->
                when(mailMessage){
                    "Enter An Mail" -> binding.emailSignInLayout.helperText=resources.getText(R.string.enteranmail)
                    "validated" -> binding.emailSignInLayout.helperText=null
                    "Invalid Email" -> binding.emailSignInLayout.helperText=resources.getText(R.string.invalidemail)

                }

            }
        })

        viewModel.passwordMessage.observe(viewLifecycleOwner, Observer {
            it?.let { passwordMessage->
                when(passwordMessage){
                    "Enter An Password" -> binding.passwordSignInLayout.helperText=resources.getText(R.string.enteranpassword)
                    "validated" -> binding.passwordSignInLayout.helperText=null
                }

            }
        })

        viewModel.signMessageLiveData.observe(viewLifecycleOwner, Observer {
            it?.let { signMessage->
               if(signMessage=="okay"){
                   //Intent
                   val intent=Intent(activity, MainActivity::class.java)
                   Util.saveSignType(requireActivity(),"email")
                   startActivity(intent)
                   this.activity?.finish()

               }
                else if(signMessage=="notverified"){

                    showAlertDialog(getString(R.string.emailnotverifiedlabel),getString(R.string.emailnotverified),R.drawable.email,getString(R.string.okay))

               }
                else
               {
                    showAlertDialog(getString(R.string.mailpasswordwronglabel),getString(R.string.mailpasswordwrong),R.drawable.ic_baseline_person_24,getString(R.string.okay))



               }

            }
        })

        viewModel.loadingDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    loadingDialog.show(childFragmentManager,"started")
                }
                else
                {
                    loadingDialog.dismiss()
                }
            }
        })



    }


}