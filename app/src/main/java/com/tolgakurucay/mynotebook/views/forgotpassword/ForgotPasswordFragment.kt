package com.tolgakurucay.mynotebook.views.forgotpassword

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.tolgakurucay.mynotebook.databinding.FragmentForgotPasswordBinding
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.viewmodels.forgotpassword.ForgotPasswordFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding:FragmentForgotPasswordBinding
    private val viewModel: ForgotPasswordFragmentViewModel by viewModels()
    @Inject lateinit var loadingDialog:CustomLoadingDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentForgotPasswordBinding.inflate(inflater)

        /*the code line of this deprecated to added the hilt(dependency injection)
        viewModel=ViewModelProvider(this)[ForgotPasswordFragmentViewModel::class.java]*/
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
                        .setIcon(R.drawable.password_black)
                        .setTitle(R.string.forgotpasswordtitle)
                        .setMessage(R.string.forgotpassword)
                        .setPositiveButton(R.string.okay,object: DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                val action=
                                    ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment()
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
                    loadingDialog.show(parentFragmentManager,null)
                }
                else
                {
                    loadingDialog.dismiss()

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