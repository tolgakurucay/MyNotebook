package com.tolgakurucay.mynotebook.views.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.internal.ApiKey
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.ActivitySocialLoginBinding
import com.tolgakurucay.mynotebook.views.main.MainActivity
import java.util.concurrent.TimeUnit

class SocialLoginActivity : AppCompatActivity() {
    
    private lateinit var binding:ActivitySocialLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso:GoogleSignInOptions
    private lateinit var auth:FirebaseAuth
    private lateinit var phoneCallBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var forceResendingToken:PhoneAuthProvider.ForceResendingToken
    private val TAG="bilgi"
    
    
    
    private var isPhone10=false
    private var isCode6=false
    private var phoneNumber=""
    private var verificationId=""
    
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySocialLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= FirebaseAuth.getInstance()
        auth.signOut()
        binding.baseConstraint.visibility=View.INVISIBLE


        val signInMethod=intent.getStringExtra("signType")
        when(signInMethod){
            "googleSignIn" -> googleSignIn()
            "facebookSignIn" -> facebookSignIn()
            "phoneSignIn" -> phoneSignIn()
        }
        
        
        
        
        





    }
    
    private fun hideDown(){
        binding.baseConstraint.visibility=View.VISIBLE
        binding.layoutUp.visibility=View.VISIBLE
        binding.layoutDown.visibility=View.INVISIBLE
    }
    private fun hideUp(){
        binding.baseConstraint.visibility=View.VISIBLE
        binding.layoutUp.visibility=View.INVISIBLE
        binding.layoutDown.visibility=View.VISIBLE
    }
    


    private fun phoneSignIn() {
        hideDown()
        phoneListener()
                binding.progressBar.visibility=View.INVISIBLE
        
                binding.buttonSignPhone.setOnClickListener {
                    binding.progressBar.visibility=View.VISIBLE
                    
                    if(isPhone10==true){
                        //doğrulama
                        phoneNumber=binding.countryCodePicker.selectedCountryCodeWithPlus+binding.phoneSignInInput.text.toString()
                        phoneCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                                binding.progressBar.visibility=View.INVISIBLE
                            }

                            override fun onCodeSent(storedVerificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
                                verificationId=storedVerificationId
                                forceResendingToken=p1
                                hideUp()
                                binding.progressBar.visibility=View.INVISIBLE

                            }

                            override fun onVerificationFailed(p0: FirebaseException) {
                                Log.d(TAG, "onVerificationFailed: ${p0.localizedMessage}")
                                Toast.makeText(this@SocialLoginActivity,p0.localizedMessage,Toast.LENGTH_LONG).show()
                                binding.progressBar.visibility=View.INVISIBLE
                            }

                        }


                        val options= PhoneAuthOptions.newBuilder()
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L,TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(phoneCallBacks)
                            .build()

                        PhoneAuthProvider.verifyPhoneNumber(options)
                        
                        
                    }
                    else
                    {
                        Toast.makeText(this@SocialLoginActivity,getString(R.string.phonenumber10),Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility=View.INVISIBLE
                    }
                    

                }
        
        binding.buttonSignPhone1.setOnClickListener {
            binding.progressBar.visibility=View.VISIBLE
            if(isCode6){

                    val credential=PhoneAuthProvider.getCredential(verificationId,binding.phoneOTB.text.toString())
                    auth.signInWithCredential(credential)
                        .addOnSuccessListener {
                            Toast.makeText(this@SocialLoginActivity,getString(R.string.verificationsuccessful),Toast.LENGTH_SHORT).show()
                            val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            binding.progressBar.visibility=View.INVISIBLE
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@SocialLoginActivity,getString(R.string.verificationfailed)+"\n"+it.toString(),Toast.LENGTH_SHORT).show()
                            binding.progressBar.visibility=View.INVISIBLE
                        }

            }
            else
            {
                Toast.makeText(this@SocialLoginActivity,getString(R.string.code6),Toast.LENGTH_LONG).show()
                binding.progressBar.visibility=View.INVISIBLE
            }
        }
        
        binding.textViewResendCode.setOnClickListener {
            binding.phoneOTB.setText("")
            binding.progressBar.visibility=View.VISIBLE
            phoneCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    Log.d(TAG, "onVerifivationCompleted: ${p0.toString()}")
                    binding.progressBar.visibility=View.INVISIBLE
                }

                override fun onCodeSent(storedVerificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    binding.progressBar.visibility=View.INVISIBLE
                    verificationId=storedVerificationId
                    forceResendingToken=p1
                    Toast.makeText(this@SocialLoginActivity,getString(R.string.codesent),Toast.LENGTH_SHORT).show()
                    

                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    binding.progressBar.visibility=View.INVISIBLE
                    Log.d(TAG, "onVerificationFailed: ${p0.localizedMessage}")
                    Toast.makeText(this@SocialLoginActivity,p0.localizedMessage,Toast.LENGTH_LONG).show()
                }

            }


            val options= PhoneAuthOptions.newBuilder()
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(phoneCallBacks)
                .setForceResendingToken(forceResendingToken)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        

    }
    
    
    
    private fun phoneListener(){
        
        binding.phoneSignInInput.addTextChangedListener { 
            if(binding.phoneSignInInput.text.toString().length!=10){
                binding.phoneSignInLayout.helperText=getString(R.string.phonenumber10)
                isPhone10=false
            }
            else
            {
                isPhone10=true
                binding.phoneSignInLayout.helperText=null
            }
        }
        binding.phoneOTB.addTextChangedListener {
            isCode6 = binding.phoneOTB.text.toString().length==6
        }
        
        
    }

    private fun facebookSignIn() {

    }

    private fun googleSignIn() {
        gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso)

        val signInIntent: Intent =mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,58)



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==58){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask:Task<GoogleSignInAccount>) {
        try{
            val account: GoogleSignInAccount?=completedTask.getResult(ApiException::class.java)
            if(account!=null){
                updateUI(account)
            }
        }
        catch (e: ApiException){
            Log.d(TAG, "handleResult: ${e.localizedMessage}")
        }

    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                Log.d(TAG, "updateUI: giriş başarılı")
                val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Log.d(TAG, "updateUI: giriş başarısız")
            }

    }


}