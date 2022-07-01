package com.tolgakurucay.mynotebook.views.login

import android.content.Intent
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
import com.tolgakurucay.mynotebook.Util
import com.tolgakurucay.mynotebook.databinding.FragmentLoginBinding
import com.tolgakurucay.mynotebook.viewmodels.login.LoginFragmentViewModel
import com.tolgakurucay.mynotebook.views.main.MainActivity


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var viewModel: LoginFragmentViewModel


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
                viewModel.signInWithEmailAndPassword(binding.emailSignInInput.text.toString(),binding.passwordSignInInput.text.toString())
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

        viewModel.signMessage.observe(viewLifecycleOwner, Observer {
            it?.let { signMessage->
               if(signMessage=="okay"){
                   //Intent
                   val intent=Intent(activity, MainActivity::class.java)
                   startActivity(intent)
                   this.activity?.let {
                       it.finish()
                   }

               }
                else if(signMessage=="notverified"){

                    Util.alertDialog(this.context!!,getString(R.string.emailnotverifiedlabel),getString(R.string.emailnotverified),R.drawable.email,getString(R.string.okay))

               }
                else
               {

                   Util.alertDialog(this.context!!,getString(R.string.mailpasswordwronglabel),getString(R.string.mailpasswordwrong),R.drawable.ic_baseline_person_24,getString(R.string.okay))


               }

            }
        })

        viewModel.loadingDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it){
                    binding.progressBarLogin.visibility=View.VISIBLE
                }
                else
                {
                    binding.progressBarLogin.visibility=View.INVISIBLE
                }
            }
        })



    }


}