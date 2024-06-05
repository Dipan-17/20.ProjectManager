package dipan.ProjectManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import dipan.ProjectManagement.databinding.ActivitySignInBinding
import dipan.ProjectManagement.databinding.ActivitySignUpBinding

class SignInActivity : AppCompatActivity() {

        private var binding: ActivitySignInBinding? = null

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
}