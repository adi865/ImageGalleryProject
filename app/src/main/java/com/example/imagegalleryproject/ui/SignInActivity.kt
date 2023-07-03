package com.example.imagegalleryproject.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.databinding.ActivitySignInBinding
import com.example.imagegalleryproject.ui.fragments.ResetPasswordFragment
import com.example.imagegalleryproject.util.FirebaseUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {
    private var _binding: ActivitySignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val TAG = "SignInActivity"

    private lateinit var loginProgress: ProgressDialog

    private lateinit var loadingBar: ProgressDialog

    private val REG_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
//        callBackManager = CallbackManager.Factory.create()

//        binding.fbLoginButton.setReadPermissions(listOf("email","public_profile", "user_birthday"))

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        loginProgress = ProgressDialog(this)

        authStateListener = FirebaseAuth.AuthStateListener {
            val user: FirebaseUser =  mAuth.currentUser!!

            if(user != null) {
//                startActivity(Intent(this, com.example.imagegalleryproject.MainActivity::class.java))
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
//      getting the value of gso inside the GoogleSigninClient
        mGoogleSignInClient= GoogleSignIn.getClient(this, gso)

        //fb login to be added later
//        binding.fbLoginButton.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
//
//            override fun onSuccess(result: LoginResult) {
//                val userId = result.accessToken.userId
//                Log.d(TAG, "onSuccess: userId $userId")
//
//                val bundle = Bundle()
//                bundle.putString("fields", "id, email, first_name, last_name, gender, age_range")
//
//
//                //Graph API to access the data of user's facebook account
//                val request = GraphRequest.newMeRequest(
//                    result.accessToken
//                ) { fbObject, response ->
//                    Log.v("Login Success", response.toString())
//
//                    //For safety measure enclose the request with try and catch
//                    try {
//
//                        Log.d(TAG, "onSuccess: fbObject $fbObject")
//
//                        val firstName = fbObject?.getString("first_name")
//                        val lastName = fbObject?.getString("last_name")
//                        val gender = fbObject?.getString("gender")
//                        val email = fbObject?.getString("email")
//
//                        Log.d(TAG, "onSuccess: firstName $firstName")
//                        Log.d(TAG, "onSuccess: lastName $lastName")
//                        Log.d(TAG, "onSuccess: gender $gender")
//                        Log.d(TAG, "onSuccess: email $email")
//
//                    } //If no data has been retrieve throw some error
//                    catch (e: JSONException) {
//
//                    }
//                }
//                //Set the bundle's data as Graph's object data
//                request.parameters = bundle
//
//                //Execute this Graph request asynchronously
//                request.executeAsync()
//
//            }
//
//            override fun onCancel() {
//                Log.d(TAG, "onCancel: called")
//            }
//
//            override fun onError(error: FacebookException) {
//                Log.d(TAG, "onError: called")
//            }
//        })


        binding.goToRegistration.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnSignIn.setOnClickListener {
                val email = binding.uName.text.toString()
                val password = binding.password.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {

                        if (it.isSuccessful) {
//                            startActivity(Intent(this, com.example.imagegalleryproject.MainActivity::class.java))
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show()
                }
            }
//        binding.googleSignInBtn.setOnClickListener {
//            signInGoogle()
//        }

        binding.resetPass.setOnClickListener {
            openResetPassDialog()
        }
    }
    private fun signInGoogle(){

        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,REG_CODE)
    }

    // onActivityResult() function : this is where we provide the task and data for the Google Account
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REG_CODE){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
//        callBackManager.onActivityResult(requestCode, resultCode, data);
    }
    // handleResult() function -  this is where we update the UI after Google signin takes place
    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException){
//            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
        }
    }
    // UpdateUI() function - this is where we specify what UI updation are needed after google signin has taken place.
    private fun UpdateUI(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                SavedPreference.setEmail(this,account.email.toString())
                SavedPreference.setUsername(this,account.displayName.toString())
                val userId = task.result.user!!.uid
                val account = {userId}

                FirebaseUtils().firebaseStoreDatabase.collection("accounts").document(userId).set(account)

//                val intent = Intent(this, com.example.imagegalleryproject.MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    object SavedPreference {

        const val EMAIL= "email"
        const val USERNAME="username"

        private  fun getSharedPreference(ctx: Context?): SharedPreferences? {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
        }

        private fun  editor(context: Context, const:String, string: String){
            getSharedPreference(
                context
            )?.edit()?.putString(const,string)?.apply()
        }

        fun getEmail(context: Context)= getSharedPreference(
            context
        )?.getString(EMAIL,"")

        fun setEmail(context: Context, email: String){
            editor(
                context,
                EMAIL,
                email
            )
        }

        fun setUsername(context: Context, username:String){
            editor(
                context,
                USERNAME,
                username
            )
        }

        fun getUsername(context: Context) = getSharedPreference(
            context
        )?.getString(USERNAME,"")
    }


    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser != null || GoogleSignIn.getLastSignedInAccount(this) !=null) {
//            startActivity(Intent(this, com.example.imagegalleryproject.MainActivity::class.java))
            finish()
        }
    }


    private fun openResetPassDialog() {
        ResetPasswordFragment().show(supportFragmentManager, ResetPasswordFragment().tag)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}