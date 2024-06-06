package dipan.ProjectManagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.toColorInt
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ActivityMyProfileBinding

class MyProfileActivity : AppCompatActivity() {
    private var profileBinding: ActivityMyProfileBinding? = null //the parent layout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileBinding= ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding?.root)

        setupToolbar()
    }



    private fun setupToolbar(){

        val toolbar=profileBinding?.toolbarMyProfileActivity

        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        toolbar?.title = "My Profile"
        toolbar?.setTitleTextColor("#ffffff".toColorInt())

        toolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }
}