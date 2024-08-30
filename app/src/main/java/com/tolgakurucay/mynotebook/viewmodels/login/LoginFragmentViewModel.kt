package com.tolgakurucay.mynotebook.viewmodels.login


import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.tolgakurucay.mynotebook.views.login.LoginFragment
import com.tolgakurucay.mynotebook.views.login.LoginFragmentValidateType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor() : ViewModel() {

    private var auth = FirebaseAuth.getInstance()

    var emailMessage = MutableLiveData<LoginFragmentValidateType>()
    var passwordMessage = MutableLiveData<LoginFragmentValidateType>()
    private val signMessage = MutableLiveData<LoginFragmentValidateType>()
    val signMessageLiveData: LiveData<LoginFragmentValidateType>
        get() = signMessage
    var loadingDialog = MutableLiveData<Boolean>()


    fun validateEmail(email: String) {
        if (email.isEmpty()) {
            emailMessage.value = LoginFragmentValidateType.ENTER_AN_EMAIL
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailMessage.value = LoginFragmentValidateType.INVALID_EMAIL
        } else {
            emailMessage.value = LoginFragmentValidateType.VALIDATED
        }

    }


    fun validatePassword(password: String) {
        if (password.isEmpty()) {
            passwordMessage.value = LoginFragmentValidateType.ENTER_AN_PASSWORD
        }  else {
            passwordMessage.value = LoginFragmentValidateType.VALIDATED
        }

    }


    fun signInWithEmailAndPassword(email: String, password: String) {
        loadingDialog.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                auth.currentUser?.let { user ->
                    if (user.isEmailVerified) {
                        signMessage.postValue(LoginFragmentValidateType.OKAY)
                        loadingDialog.value = false
                    } else {
                        signMessage.value = LoginFragmentValidateType.NOT_VERIFIED
                        loadingDialog.value = false
                    }
                }
            }
            .addOnFailureListener {
                signMessage.value = LoginFragmentValidateType.ERROR
                loadingDialog.value = false
            }

    }


}