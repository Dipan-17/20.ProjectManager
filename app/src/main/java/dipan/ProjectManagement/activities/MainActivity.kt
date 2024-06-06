package dipan.ProjectManagement.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.graphics.toColorInt
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ActivityMainBinding
import dipan.ProjectManagement.databinding.AppBarMainBinding
import dipan.ProjectManagement.databinding.MainContentBinding
import dipan.ProjectManagement.databinding.NavHeaderMainBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.User

class MainActivity : BaseActivity() {
    private var mainBinding: ActivityMainBinding? = null //the parent layout
    private var appBarBinding: AppBarMainBinding? = null //the top toolbar
    private var mainContentBinding: MainContentBinding? = null //the recycler view containing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding?.root)

        appBarBinding= AppBarMainBinding.bind(mainBinding?.drawerAppBar?.root!!)
        mainContentBinding= MainContentBinding.bind(mainBinding?.drawerAppBar?.mainContent?.root!!)

        //set action bar
        setUpActionBar()

        //side drawer ke buttons ke on click listeners
        mainBinding?.navView?.setNavigationItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.nav_myProfile -> {
                    val intent= Intent(this@MainActivity,MyProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_signOut -> {
                    FirebaseAuth.getInstance().signOut()
                    showErrorSnackBar("Successfully Signed out")
                    val intent= Intent(this@MainActivity,IntroActivity::class.java)

                    //if we had previous intro activity in stack use that one
                    intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    true
                }else ->false
            }
            mainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }


        //set navigation drawer details
        FirestoreClass().signInUser(this)

    }

    private fun setUpActionBar(){

        val toolbar=appBarBinding?.toolbarMainActivity
        setSupportActionBar(toolbar)
        toolbar?.title = "Home"
        toolbar?.setTitleTextColor("#ffffff".toColorInt())
        toolbar?.setNavigationIcon(R.drawable.ic_navigation_menu)
        toolbar?.setNavigationOnClickListener {
            toggleDrawer()
        }

    }

    private fun toggleDrawer() {
        if(mainBinding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            mainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            mainBinding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }


    fun updateNavigationUserDetails(user: User){
        val navView=mainBinding?.navView// Get a reference to the NavigationView
        val headerView=navView?.getHeaderView(0) // Get a reference to the header view
        val headerBinding = headerView?.let { NavHeaderMainBinding.bind(it) }// Bind the header view

        //access
        val userName=headerBinding?.tvHeaderUserName
        val profile=headerBinding?.ivHeaderProfile

        //set the image
        Glide.with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(profile!!)

        userName?.text=user.name
    }

    override fun onBackPressed() {

        if(mainBinding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            mainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
        super.onBackPressed()

    }


}
