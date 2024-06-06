package dipan.ProjectManagement.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ActivitySignInBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.User

class SignInActivity : BaseActivity() {

        private var binding: ActivitySignInBinding? = null

        private lateinit var auth:FirebaseAuth
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding= ActivitySignInBinding.inflate(layoutInflater)
            setContentView(binding?.root)

            //set the app to full screen
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            setupActionBar()

            auth=FirebaseAuth.getInstance()


            binding?.btnSignIn?.setOnClickListener {
                signInRegisteredUser()
            }
        }

    private fun signInRegisteredUser(){
            val email=binding?.etEmail?.text.toString().trim{it <=' '}
            val password=binding?.etPassword?.text.toString().trim{it <=' '}

            if(validateForm(email, password)){
                showProgressDialog(resources.getString(R.string.please_wait))

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        hideProgressDialog()

                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SIGN IN", "signInWithEmail:success")
                            val user = auth.currentUser
                            FirestoreClass().loadUserData(this)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SIGN IN", "signInWithEmail:failure", task.exception)
                            showErrorSnackBar(task!!.exception!!.message.toString())

                        }
                    }
            }
        }

    private fun validateForm(email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password")
                false
            }
            else->{
                true
            }
        }
    }

    private fun setupActionBar(){
            setSupportActionBar(binding?.toolbarSignInActivity)
            val actionBar = supportActionBar
            if(actionBar != null){
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            }
            binding?.toolbarSignInActivity?.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }

    //sign in after getting data from firestore
    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}