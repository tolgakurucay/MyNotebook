package com.tolgakurucay.mynotebook.views.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentLoginBinding
import com.tolgakurucay.mynotebook.viewmodels.LoginFragmentViewModel


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var viewModel:LoginFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentLoginBinding.inflate(inflater)
        viewModel=ViewModelProvider(this)[LoginFragmentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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

            }
            else
            {
                Toast.makeText(this.context,R.string.blankfields,Toast.LENGTH_LONG).show()

            }
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



    }


}