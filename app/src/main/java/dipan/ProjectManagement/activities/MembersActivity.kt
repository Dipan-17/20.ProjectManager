package dipan.ProjectManagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import dipan.ProjectManagement.R
import dipan.ProjectManagement.adapters.MemberItemsAdapter
import dipan.ProjectManagement.databinding.ActivityMembersBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.User
import dipan.ProjectManagement.utils.Constants

class MembersActivity : BaseActivity() {

    private var membersBinding:ActivityMembersBinding?=null

    //to get the details from tasklist activity
    private lateinit var mBoardDetails:Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        membersBinding=ActivityMembersBinding.inflate(layoutInflater)
        setContentView(membersBinding?.root)

        if(intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails= intent.getParcelableExtra<Board>(Constants.BOARD_DETAILS)!!

            Log.e("MembersActivity",mBoardDetails.assignedTo[mBoardDetails.assignedTo.size-1].toString())

            showProgressDialog(resources.getString(R.string.please_wait))



            //get the members list
            FirestoreClass().getAssignedMemberListDetails(this@MembersActivity,mBoardDetails.assignedTo)
        }

        setupToolbar("Members")


    }

     fun setUpMemberListRV(list:ArrayList<User>){
        hideProgressDialog()

        membersBinding?.rvMembersList?.layoutManager=LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)

        membersBinding?.rvMembersList?.setHasFixedSize(true)

        val adapter= MemberItemsAdapter(this@MembersActivity,list)
        membersBinding?.rvMembersList?.adapter=adapter
    }

    private fun setupToolbar(title:String){

        val toolbar=membersBinding?.toolbarMembersActivity

        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        toolbar?.title = title
        toolbar?.setTitleTextColor("#ffffff".toColorInt())

        toolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }



    }
}