@file:Suppress("OverrideDeprecatedMigration")

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
import android.view.View
import androidx.core.view.ContentInfoCompat
import androidx.core.widget.addTextChangedListener
import com.facebook.*
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.ActivitySocialLoginBinding
import com.tolgakurucay.mynotebook.models.CreateUserModel
import com.tolgakurucay.mynotebook.models.CreateUserWithPhone
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.views.main.MainActivity
import java.util.concurrent.TimeUnit
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import java.util.*



class SocialLoginActivity : AppCompatActivity() {
    
    private lateinit var binding:ActivitySocialLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso:GoogleSignInOptions
    private lateinit var auth:FirebaseAuth
    private lateinit var phoneCallBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var forceResendingToken:PhoneAuthProvider.ForceResendingToken
    private val TAG="bilgi"
    private var loadingDialog=CustomLoadingDialog()
    private lateinit var callbackManager:CallbackManager
   
    
    
    
    private var isPhone10=false
    private var isCode6=false
    private var phoneNumber=""
    private var verificationId=""
    
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySocialLoginBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.from_left,R.anim.to_right)
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

        
                binding.buttonSignPhone.setOnClickListener {
                        loadingDialog.show(supportFragmentManager,"started")
                    if(isPhone10==true){
                        //doğrulama
                        phoneNumber=binding.countryCodePicker.selectedCountryCodeWithPlus+binding.phoneSignInInput.text.toString()
                        phoneCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            override fun onVerificationCompleted(p0: PhoneAuthCredential) {


                            }

                            override fun onCodeSent(storedVerificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
                                verificationId=storedVerificationId
                                forceResendingToken=p1
                                Toast.makeText(this@SocialLoginActivity,getString(R.string.codesent),Toast.LENGTH_SHORT).show()
                                hideUp()
                                loadingDialog.dismiss()

                            }

                            override fun onVerificationFailed(p0: FirebaseException) {
                                Log.d(TAG, "onVerificationFailed: ${p0.localizedMessage}")
                                Toast.makeText(this@SocialLoginActivity,p0.localizedMessage,Toast.LENGTH_LONG).show()
                                loadingDialog.dismiss()

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

                    }
                    

                }
        
        binding.buttonSignPhone1.setOnClickListener {

            loadingDialog.show(supportFragmentManager,"started")
            if(isCode6){

                    val credential=PhoneAuthProvider.getCredential(verificationId,binding.phoneOTB.text.toString())
                    auth.signInWithCredential(credential)
                        .addOnSuccessListener {

                            isUserSavedToFirebase {
                                if(!it){
                                    saveToFirebaseWithPhone {
                                        if(it){
                                            Log.d(TAG, "phoneSignIn: yeni kayıt olundu")
                                            Toast.makeText(this@SocialLoginActivity,getString(R.string.verificationsuccessful),Toast.LENGTH_SHORT).show()
                                            val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        else
                                        {
                                            Toast.makeText(this,"Error!",Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "phoneSignIn:  kayıt olunmuş")
                                    Log.d(TAG, auth.currentUser!!.phoneNumber!!)
                                    Toast.makeText(this@SocialLoginActivity,getString(R.string.verificationsuccessful),Toast.LENGTH_SHORT).show()
                                    val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                                    //SignType.signType="phone"
                                    Util.saveSignType(this,"phone")
                                    startActivity(intent)
                                    finish()
                                }
                            }




                        }
                        .addOnFailureListener {
                            Toast.makeText(this@SocialLoginActivity,getString(R.string.verificationfailed)+"\n"+it.toString(),Toast.LENGTH_SHORT).show()
                            loadingDialog.dismiss()

                        }

            }
            else
            {
                Toast.makeText(this@SocialLoginActivity,getString(R.string.code6),Toast.LENGTH_LONG).show()
                loadingDialog.dismiss()

            }
        }
        
        binding.textViewResendCode.setOnClickListener {
            loadingDialog.show(supportFragmentManager,"started")
            binding.phoneOTB.setText("")

            phoneCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {


                }

                override fun onCodeSent(storedVerificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {

                    verificationId=storedVerificationId
                    forceResendingToken=p1
                    Toast.makeText(this@SocialLoginActivity,getString(R.string.codesent),Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                    

                }

                override fun onVerificationFailed(p0: FirebaseException) {


                    Toast.makeText(this@SocialLoginActivity,p0.localizedMessage,Toast.LENGTH_LONG).show()
                    loadingDialog.dismiss()
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

         callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("email"))

        LoginManager.getInstance().registerCallback(callbackManager,object:FacebookCallback<LoginResult>{
            override fun onCancel() {
                Log.d(TAG, "onCancel: ")
                Toast.makeText(this@SocialLoginActivity, getString(R.string.registrationcancelled), Toast.LENGTH_LONG).show()
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "onError: error")
                Toast.makeText(this@SocialLoginActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "onSuccess: ${result.accessToken}")
                authWithFacebookAccessToken(result.accessToken)

                    
            }

        })





    }
    
    private fun authWithFacebookAccessToken(accessToken: AccessToken){
        val credential=FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {

                Toast.makeText(this@SocialLoginActivity, getString(R.string.signinsuccessful), Toast.LENGTH_LONG).show()


                isUserSavedToFirebase {
                    if(it){
                        val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        Util.saveSignType(this,"facebook")
                        startActivity(intent)
                        this@SocialLoginActivity.finish()
                    }
                    else
                    {
                        saveToFirebase {
                            if(it){
                                val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                Util.saveSignType(this,"facebook")
                                startActivity(intent)
                                this@SocialLoginActivity.finish()
                            }
                            else{
                                Log.d(TAG, "authWithFacebookAccessToken: hata var kayıt olunamadı")
                            }
                        }
                    }
                }

            }
            .addOnFailureListener {

                Toast.makeText(this@SocialLoginActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                val intent=Intent(this@SocialLoginActivity,LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this@SocialLoginActivity.finish()
            }
    }

    private fun googleSignIn() {
        loadingDialog.show(supportFragmentManager,"started")
        gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso)

        val signInIntent: Intent =mGoogleSignInClient.signInIntent

        startActivityForResult(signInIntent,58)




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if(requestCode==58){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)

        }

        super.onActivityResult(requestCode, resultCode, data)

        if(::callbackManager.isInitialized){
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun handleResult(completedTask:Task<GoogleSignInAccount>) {

        try{
            val account: GoogleSignInAccount?=completedTask.getResult(ApiException::class.java)
            if(account!=null){
                updateUI(account)

            }
            else
            {

            }
        }
        catch (e: ApiException){


        }

    }


    private fun saveToFirebase(completion:(isTrue:Boolean)->Unit){

        val auth=FirebaseAuth.getInstance()
        val firebase=FirebaseFirestore.getInstance()
        auth.currentUser?.let {
            val user=CreateUserModel(it.displayName,null,it.email,null,null)
            firebase.collection("Users").document(it.uid).set(user).addOnSuccessListener {
                completion(true)
            }
                .addOnFailureListener {
                    completion(false)
                }
        }
    }

    private fun saveToFirebaseWithPhone(completion:(isTrue:Boolean)->Unit){

        val auth=FirebaseAuth.getInstance()
        val firebase=FirebaseFirestore.getInstance()
        auth.currentUser?.let {
            val user=CreateUserWithPhone(it.phoneNumber.toString(),null,null,null)
            firebase.collection("Users").document(it.uid).set(user).addOnSuccessListener {
                completion(true)
            }
                .addOnFailureListener {
                    completion(false)
                }
        }
    }

    private fun isUserSavedToFirebase(completion:(isTrue:Boolean)->Unit){
        val auth=FirebaseAuth.getInstance()
        val firebase=FirebaseFirestore.getInstance()
        auth.currentUser?.let {
            
            firebase.collection("Users").document(it.uid).get()
                .addOnSuccessListener { 
                    if(it!=null){
                        if(it.exists()){
                            completion(true)
                        }
                        else
                        {
                            completion(false)
                        }
                    }
                    else
                    {
                        completion(false)
                    }
                }
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {



        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {

                isUserSavedToFirebase {
                    if(!it){

                        saveToFirebase {

                            if(it){

                                val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                                Log.d(TAG, auth.currentUser!!.email!!)

                                Util.saveSignType(this,"google")
                                startActivity(intent)
                                finish()
                            }
                            else
                            {
                                Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    else{

                        val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                        Util.saveSignType(this,"google")
                        startActivity(intent)
                        finish()
                    }
                }



            }
            .addOnFailureListener {


            }

    }

    override fun onBackPressed() {
        val intent=Intent(this,LoginActivity::class.java)
        startActivity(intent)
       // super.onBackPressed()
    }


}