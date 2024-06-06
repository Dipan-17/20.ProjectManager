package dipan.ProjectManagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ActivitySignUpBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.User

class SignUpActivity : BaseActivity() {

    private var binding: ActivitySignUpBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //set the app to full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        binding?.btnSignUp?.setOnClickListener {
            registerUser()
        }

    }
    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding?.toolbarSignUpActivity?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //register new users in firebase auth
    private fun registerUser(){
        val name=binding?.etName?.text.toString().trim{it <=' '}
        val email=binding?.etEmail?.text.toString().trim{it <=' '}
        val password=binding?.etPassword?.text.toString().trim{it <=' '}

        if(validateForm(name,email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email,
                password
            ).addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful) {
                    val firebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!

                    //user is stored in authenticaion -> now store in firestore database
                    val user = User(
                        firebaseUser.uid,
                        name,
                        registeredEmail
                    )

                    FirestoreClass().registerUser(this, user)



                } else {
                    showErrorSnackBar(task.exception!!.message.toString())
                }
            }
        }
    }
    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name")
                 false
            }
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

    //for success listener of storing user in firestore
     fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "You have successfully registered",
            Toast.LENGTH_SHORT
        ).show()

        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()
        finish()
    }
}