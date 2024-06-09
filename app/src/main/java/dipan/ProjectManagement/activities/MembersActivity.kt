package dipan.ProjectManagement.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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


    //set the menu options to add the members
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }
    //onclick of menu options
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when(item.itemId){
            membersBinding?.toolbarMembersActivity.let{R.id.action_add_member}->{
                //display the dialogue
                dialogueAddMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogueAddMember() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_member)

        val tvAdd = dialog.findViewById<TextView>(R.id.tv_add)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        tvAdd.setOnClickListener {
            // Handle add button click
            val email = dialog.findViewById<TextView>(R.id.et_email_search_member).text.toString()

            if(email.isNotEmpty()) {
                dialog.dismiss()
                //showProgressDialog(resources.getString(R.string.please_wait))
                //FirestoreClass().getMemberDetails(this@MembersActivity,email)

                //TODO
            }
            else{
                //Toast.makeText(this@MembersActivity,"Please enter email address",Toast.LENGTH_SHORT).show()
                showErrorSnackBar("Please enter email address")
            }
        }

        tvCancel.setOnClickListener {
            // Handle cancel button click
            dialog.dismiss()
        }

        dialog.show()
    }

}