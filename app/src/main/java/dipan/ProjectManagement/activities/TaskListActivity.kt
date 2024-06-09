package dipan.ProjectManagement.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import dipan.ProjectManagement.R
import dipan.ProjectManagement.adapters.TaskListItemAdapter
import dipan.ProjectManagement.databinding.ActivityTaskListBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.Card
import dipan.ProjectManagement.models.Task
import dipan.ProjectManagement.utils.Constants

class TaskListActivity : BaseActivity() {
    private var taskListBinding: ActivityTaskListBinding? = null

    private lateinit var mBoardDetails:Board

    //retrieve the board document id
    private lateinit var mBoardDocumentId:String

    //we want to update our taskListActivity when we come back from members activity
    companion object{
        const val MEMBERS_REQUEST_CODE:Int=14
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //activate binding
        taskListBinding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(taskListBinding?.root)




        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId=intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        //retrieve the relevant board details from document id
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDocumentId)
    }


    //this is called by the firestore class on success retrieval of a board
    //updates the task list recycler view
    fun boardDetails(board: Board){
        hideProgressDialog()

        //assign the details
        mBoardDetails=board

        //setup toolbar with the doc name
        setupToolbar(board.name)

        //just to show the in the UI => This is used as empty list don't look good
        //this is later used in the adapter as an add button
        val addTaskList=Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        //set the adapter
        taskListBinding?.rvTaskList?.setHasFixedSize(true)
        taskListBinding?.rvTaskList?.layoutManager=LinearLayoutManager(this@TaskListActivity,
            LinearLayoutManager.HORIZONTAL,false)

        val adapter= TaskListItemAdapter(this,board.taskList)
        taskListBinding?.rvTaskList?.adapter=adapter
        //adapter.notifyDataSetChanged()

    }


    private fun setupToolbar(title:String){

        val toolbar=taskListBinding?.toolbarTaskListActivity

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
    //for the members button in the top taskbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }
    //onclick of menu options
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when(item.itemId){
            taskListBinding?.toolbarTaskListActivity.let{R.id.action_member}->{
                //start the members activity
                val intent=Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun addUpdateTaskListSuccess(){
        hideProgressDialog() //we have added to firestore

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)

    }
    //create a new task
    fun createTaskList(taskListName:String){
        val task=Task(taskListName,FirestoreClass().getCurrentUserID())
        //update the board
        mBoardDetails.taskList.add(0,task) //add a new task to the beginning of board


        //we are removing the last item cause it is just the add button
        //when we call addUpdateTaskList -> at the end it will call fun boardDetails() -> which will add the add button again
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1) //remove the last item


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    //edit the name of a list
    fun updateTaskList(position:Int,taskListName:String,model:Task){
        val task=Task(taskListName,model.createdBy)

        mBoardDetails.taskList[position]=task //update the task list -> replace the old task with the new one
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1) //remove the last item -> add button

        //same as creating a new task
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    //delete a task
    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position) //remove the task from the list
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1) //remove the last item -> add button

        //same as creating a new task
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails) //this functions just updates the hashmap
    }

    //for cards creation
    fun addCardToTaskList(position: Int,cardName:String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1) //remove the last item -> add button


        val cardAssignedUsersList:ArrayList<String> = ArrayList()
        val currentUserID=FirestoreClass().getCurrentUserID()
        cardAssignedUsersList.add(currentUserID)


        val card= Card(cardName,currentUserID,cardAssignedUsersList)
        //existing cards in existing list
        val cardsList=mBoardDetails.taskList[position].card
        cardsList.add(card) //add the new card to the list

        //create a new task
        val task=Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        //replace the old task with the new one
        mBoardDetails.taskList[position]=task

        //use the old update function only -> now the board is updated
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)


    }


    //to reload activity if any changes were made
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== MEMBERS_REQUEST_CODE){
            //reload the board
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }

}