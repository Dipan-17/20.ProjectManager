package dipan.ProjectManagement.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.graphics.toColorInt
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dipan.ProjectManagement.R
import dipan.ProjectManagement.adapters.BoardItemsAdapter
import dipan.ProjectManagement.databinding.ActivityMainBinding
import dipan.ProjectManagement.databinding.AppBarMainBinding
import dipan.ProjectManagement.databinding.MainContentBinding
import dipan.ProjectManagement.databinding.NavHeaderMainBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.User
import dipan.ProjectManagement.utils.Constants

class MainActivity : BaseActivity() {
    private var mainBinding: ActivityMainBinding? = null //the parent layout
    private var appBarBinding: AppBarMainBinding? = null //the top toolbar
    private var mainContentBinding: MainContentBinding? = null //the recycler view containing

    private lateinit var mUserName:String//to know who created the board

    companion object{
        const val MY_PROFILE_REQUEST_CODE:Int=11
        const val CREATE_BOARD_REQUEST_CODE:Int=12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding?.root)

        appBarBinding= AppBarMainBinding.bind(mainBinding?.drawerAppBar?.root!!)
        mainContentBinding= MainContentBinding.bind(mainBinding?.drawerAppBar?.mainContent?.root!!)

        //set action bar
        setUpActionBar()


        //set navigation drawer details
        FirestoreClass().loadUserData(this,true)//first time we should read the boards list


        //side drawer ke buttons ke on click listeners
        mainBinding?.navView?.setNavigationItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.nav_myProfile -> {
                    val intent= Intent(this@MainActivity,MyProfileActivity::class.java)
                    startActivityForResult(intent,MY_PROFILE_REQUEST_CODE)
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


        //fab -> create board
        appBarBinding?.fabCreateBoard?.setOnClickListener {
            val intent= Intent(this@MainActivity,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode==Activity.RESULT_OK && requestCode==MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        }
        //refresh the list if a new item is created
        else if(resultCode==Activity.RESULT_OK && requestCode== CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().getBoardsList(this)
        }
        else{
            Log.e("Activity_Result_error","Cancelled or something went wrong")
        }
    }



    //recycler view list inside main screen
    fun populateBoardsListToUI(boardList:ArrayList<Board>){
        hideProgressDialog()

        if(boardList.size>0){
            //play with visibility
            mainContentBinding?.rvBoardsList?.visibility=android.view.View.VISIBLE
            mainContentBinding?.tvNoBoardsAvailable?.visibility=android.view.View.GONE

            //assign the adapter
            val adapter=BoardItemsAdapter(this@MainActivity,boardList)

            mainContentBinding?.rvBoardsList?.setHasFixedSize(true)
            mainContentBinding?.rvBoardsList?.layoutManager= LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            mainContentBinding?.rvBoardsList?.adapter= adapter



        }else{
            //visibility
            mainContentBinding?.rvBoardsList?.visibility=android.view.View.GONE
            mainContentBinding?.tvNoBoardsAvailable?.visibility=android.view.View.VISIBLE
        }
    }


    //action bar setup
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

    //toggle button on setup action bar
    private fun toggleDrawer() {
        if(mainBinding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            mainBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            mainBinding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    //side ke drawer main details update -> called by firestore class after successfully retrieving user info
    fun updateNavigationUserDetails(user: User,readBoardsList:Boolean){
        val navView=mainBinding?.navView// Get a reference to the NavigationView
        val headerView=navView?.getHeaderView(0) // Get a reference to the header view
        val headerBinding = headerView?.let { NavHeaderMainBinding.bind(it) }// Bind the header view

        //access
        val userName=headerBinding?.tvHeaderUserName
        val profile=headerBinding?.ivHeaderProfile

        //set the image
        Glide.with(this)
            .load(user.image)
            .fitCenter()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(profile!!)

        userName?.text=user.name

        mUserName=user.name

        //read boards if required
        if(readBoardsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }

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
