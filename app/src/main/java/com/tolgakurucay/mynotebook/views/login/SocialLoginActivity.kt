@file:Suppress("OverrideDeprecatedMigration")

package com.tolgakurucay.mynotebook.views.login

import android.content.Intent
import android.content.IntentSender
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.tolgakurucay.mynotebook.utils.Util.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SocialLoginActivity : AppCompatActivity() {
    
    private lateinit var binding:ActivitySocialLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso:GoogleSignInOptions
    private lateinit var auth:FirebaseAuth
    private lateinit var phoneCallBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var forceResendingToken:PhoneAuthProvider.ForceResendingToken
    private val TAG="bilgi"
    @Inject lateinit var loadingDialog:CustomLoadingDialog
    private lateinit var callbackManager:CallbackManager
    private lateinit var signInRequest:BeginSignInRequest
   
    
    
    
    private var isPhone10=false
    private var isCode6=false
    private var phoneNumber=""
    private var verificationId=""

    init {
        auth= FirebaseAuth.getInstance()
    }
    
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySocialLoginBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.from_left,R.anim.to_right)
        setContentView(binding.root)
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
        loadingDialog.show(supportFragmentManager,null)
        val credential=FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {

                Toast.makeText(this@SocialLoginActivity, getString(R.string.signinsuccessful), Toast.LENGTH_LONG).show()


                isUserSavedToFirebase {
                    if(it){
                        val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        Util.saveSignType(this,"facebook")
                        startActivity(intent)
                        this@SocialLoginActivity.finish()
                    }
                    else
                    {
                        saveToFirebase {
                            if(it){
                                val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                Util.saveSignType(this,"facebook")
                                startActivity(intent)
                                this@SocialLoginActivity.finish()
                            }
                            else{
                                showAlertDialog(getString(R.string.error),getString(R.string.somethingwentwrong),R.drawable.error,getString(R.string.okay)){
                                    val intent=Intent(this@SocialLoginActivity,LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    this@SocialLoginActivity.finish()
                                }
                            }
                        }
                    }
                }

            }
            .addOnFailureListener {
                showAlertDialog(getString(R.string.error),it.localizedMessage!!.toString(),R.drawable.error,getString(R.string.okay)){
                    val intent=Intent(this@SocialLoginActivity,LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    this@SocialLoginActivity.finish()
                }


            }
    }

    private lateinit var oneTapClient: SignInClient
    private fun googleSignIn() {

        loadingDialog.show(supportFragmentManager,null)
        oneTapClient = Identity.getSignInClient(this)

        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result->
                try {
                    startIntentSenderForResult(result.pendingIntent.intentSender,REQ_ONE_TAP,null,0,0,0,null)
                }
                catch (ex:IntentSender.SendIntentException){
                    showAlertDialog(getString(R.string.error),ex.localizedMessage!!.toString(),R.drawable.error,getString(R.string.okay)){
                        val intent=Intent(this,LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }

                }
                finally {
                    loadingDialog.dismiss()
                }

            }
            .addOnFailureListener { exception->
                exception.localizedMessage?.let { exception.localizedMessage?.let { it1 ->
                    showAlertDialog(getString(R.string.error),
                        it1,R.drawable.error,getString(R.string.okay)){
                        val intent=Intent(this,LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        loadingDialog.dismiss()
                        startActivity(intent)
                        finish()
                    }
                   }
                }
            }




    }



    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.d(TAG, "onActivityResult: $requestCode")

         if(requestCode==REQ_ONE_TAP){
            try {
                loadingDialog.show(supportFragmentManager,null)
                val credential=oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                if(!idToken.isNullOrEmpty()){
                    val firebaseCredential= GoogleAuthProvider.getCredential(idToken,null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnSuccessListener {
                            isUserSavedToFirebase {
                                if(!it){
                                    saveToFirebase {
                                        if(it){
                                            Toast.makeText(this, getString(R.string.signinsuccessful), Toast.LENGTH_SHORT).show()
                                            val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            Util.saveSignType(this,"google")
                                            startActivity(intent)
                                            finish()
                                        }
                                        else
                                        {
                                            showAlertDialog(getString(R.string.error),getString(R.string.somethingwentwrong),R.drawable.error,getString(R.string.okay)){
                                                val intent=Intent(this@SocialLoginActivity,LoginActivity::class.java)
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                startActivity(intent)
                                                finish()
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    val intent=Intent(this@SocialLoginActivity,MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    Util.saveSignType(this,"google")
                                    startActivity(intent)
                                    finish()
                                }
                            }


                        }
                        .addOnFailureListener {
                            showAlertDialog(getString(R.string.error),it.localizedMessage!!.toString(),R.drawable.error,getString(R.string.okay)){
                                val intent=Intent(this@SocialLoginActivity,LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
                else
                {
                    showAlertDialog(getString(R.string.error),getString(R.string.somethingwentwrong),R.drawable.error,getString(R.string.okay)){
                        val intent=Intent(this@SocialLoginActivity,LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            catch (ex:Exception){

                showAlertDialog(getString(R.string.error),ex.localizedMessage!!.toString(),R.drawable.error,getString(R.string.okay)){
                    val intent=Intent(this@SocialLoginActivity,LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }
            }
            finally {
                loadingDialog.dismiss()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)

        if(::callbackManager.isInitialized){
            callbackManager.onActivityResult(requestCode, resultCode, data)
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


    override fun onBackPressed() {
        val intent=Intent(this,LoginActivity::class.java)
        startActivity(intent)

    }

}