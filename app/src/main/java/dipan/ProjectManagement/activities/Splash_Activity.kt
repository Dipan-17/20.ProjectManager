package dipan.ProjectManagement.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import dipan.ProjectManagement.databinding.ActivitySplashBinding
import dipan.ProjectManagement.firebase.FirestoreClass

class Splash_Activity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //set the app to full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //use a custom font
        val typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding?.tvAppName?.typeface = typeface

        //start intro activity after 2.5 seconds
        Handler().postDelayed({


            var currentUserID=FirestoreClass().getCurrentUserID()
            if(currentUserID.isNotEmpty()){
                //auto login
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }


        }, 2500)

    }
}