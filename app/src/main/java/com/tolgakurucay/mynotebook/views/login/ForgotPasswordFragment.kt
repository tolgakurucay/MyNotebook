package com.tolgakurucay.mynotebook.views.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
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
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.databinding.FragmentForgotPasswordBinding
import com.tolgakurucay.mynotebook.viewmodels.login.ForgotPasswordFragmentViewModel


class ForgotPasswordFragment : Fragment() {

    private lateinit var binding:FragmentForgotPasswordBinding
    private lateinit var viewModel: ForgotPasswordFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentForgotPasswordBinding.inflate(inflater)
        viewModel=ViewModelProvider(this)[ForgotPasswordFragmentViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        textChangeListener()
        buttonClickListener()
        observeLiveData()
    }

    private fun buttonClickListener(){
        binding.sentCodeToMail.setOnClickListener {
            if(validateFields()){
                viewModel.forgotPassword(binding.emailForgotPasswordInput.text.toString())
            }
            else
            {
                Toast.makeText(this.context,R.string.blankfields,Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun validateFields() : Boolean{
        val email=binding.emailForgotPasswordLayout.helperText
        return email==null

    }

    private fun observeLiveData(){
        viewModel.emailMessage.observe(viewLifecycleOwner, Observer {
            it?.let { emailMessage->
                when(emailMessage){
                    "Enter An Mail" -> binding.emailForgotPasswordLayout.helperText=resources.getText(R.string.enteranmail)
                    "Invalid Email" -> binding.emailForgotPasswordLayout.helperText=resources.getText(R.string.invalidemail)
                    "validated" -> binding.emailForgotPasswordLayout.helperText=null
                }

            }
        })


        viewModel.forgotPasswordMessage.observe(viewLifecycleOwner, Observer {
            it?.let { forgotPasswordMessage->
                when(forgotPasswordMessage){
                    "true" ->

                    AlertDialog.Builder(this.context)
                        .setIcon(R.drawable.password)
                        .setTitle(R.string.forgotpasswordtitle)
                        .setMessage(R.string.forgotpassword)
                        .setPositiveButton(R.string.okay,object: DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                val action=ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment()
                                Navigation.findNavController(view!!).navigate(action)
                            }

                        })
                        .create()
                        .show()


                    "error" ->
                        Util.alertDialog(this.context!!,getString(R.string.nousertitle),getString(R.string.nouser),R.drawable.password,getString(R.string.okay))


                }

            }
        })
        viewModel.loadingDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    binding.progressBarForgotPassword.visibility=View.VISIBLE
                }
                else
                {
                    binding.progressBarForgotPassword.visibility=View.INVISIBLE
                }
            }
        })

    }

    private fun textChangeListener(){
        binding.emailForgotPasswordInput.addTextChangedListener {
            viewModel.validateEmail(binding.emailForgotPasswordInput.text.toString())
        }

    }


}