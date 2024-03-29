package com.tolgakurucay.mynotebook.views.signup


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.FragmentSignUpBinding
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialog
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialogWithOneButtonFunc
import com.tolgakurucay.mynotebook.viewmodels.signup.SignUpFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding:FragmentSignUpBinding
    private val viewModel: SignUpFragmentViewModel by viewModels()
    @Inject lateinit var loadingDialog: CustomLoadingDialog



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSignUpBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        textChangeListener()
        buttonClickListener()
        observeLiveData()

    }

    private fun textChangeListener(){
        binding.emailSignUpInput.addTextChangedListener {
            viewModel.validateMail(binding.emailSignUpInput.text.toString())
        }
        binding.nameSignUpInput.addTextChangedListener {
            viewModel.validateName(binding.nameSignUpInput.text.toString())
        }
        binding.surnameSignUpInput.addTextChangedListener {
            viewModel.validateSurname(binding.surnameSignUpInput.text.toString())
        }
        binding.passwordSignUpInput.addTextChangedListener {
            viewModel.validatePassword(binding.passwordSignUpInput.text.toString())
        }

    }

    private fun buttonClickListener(){
        binding.buttonSignUpNow.setOnClickListener {
            if(validateFields()){
                val name=binding.nameSignUpInput.text.toString()
                val surname=binding.surnameSignUpInput.text.toString()
                val email=binding.emailSignUpInput.text.toString()
                val password=binding.passwordSignUpInput.text.toString()
                viewModel.createUserWithEmailAndPassword(email,password,name,surname)

            }
            else
            {
                Toast.makeText(this.context,R.string.blankfields, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateFields() : Boolean{
        val name=binding.nameSignUpLayout.helperText
        val surname=binding.surnameSignUpLayout.helperText
        val email=binding.emailSignUpLayout.helperText
        val password=binding.passwordSignUpLayout.helperText
         return name==null && surname==null && email==null && password==null

    }

    private fun observeLiveData(){
        viewModel.mail.observe(viewLifecycleOwner, Observer {
            it?.let { mailMessage->
                when(mailMessage){
                    "Enter An Mail" -> binding.emailSignUpLayout.helperText=resources.getText(R.string.enteranmail)
                    "Invalid Email" -> binding.emailSignUpLayout.helperText=resources.getText(R.string.invalidemail)
                    "validated" -> binding.emailSignUpLayout.helperText=null

                }

            }
        })
        viewModel.name.observe(viewLifecycleOwner, Observer {
            it?.let { nameMessage->
                when(nameMessage){
                    "Enter An Name" -> binding.nameSignUpLayout.helperText=resources.getText(R.string.enteranname)
                    "validated" -> binding.nameSignUpLayout.helperText=null
                }

            }
        })
        viewModel.surname.observe(viewLifecycleOwner, Observer {
            it?.let { surnameMessage->
                when(surnameMessage){
                    "Enter An Surname" -> binding.surnameSignUpLayout.helperText=resources.getText(R.string.enteransurname)
                    "validated" -> binding.surnameSignUpLayout.helperText=null
                }

            }
        })
        viewModel.password.observe(viewLifecycleOwner, Observer {
            it?.let { passwordMessage->
                when(passwordMessage){
                    "Enter An Password" -> binding.passwordSignUpLayout.helperText=resources.getText(R.string.enteranpassword)
                    "Minimum 8 Characters" -> binding.passwordSignUpLayout.helperText=resources.getText(R.string.minimum8character)
                    "validated" -> binding.passwordSignUpLayout.helperText=null
                }

            }
        })
        viewModel.createMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    "success" -> {
                        showAlertDialogWithOneButtonFunc(getString(R.string.createduserlabel),getString(R.string.createduser),R.drawable.ic_baseline_person_add_surname,getString(R.string.okay)){
                            val action= SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
                            Navigation.findNavController(requireView()).navigate(action)
                        }
                    }
                        "fail" -> showAlertDialog(getString(R.string.emailexistTitle),getString(R.string.emailexist),R.drawable.email,getString(R.string.okay))
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