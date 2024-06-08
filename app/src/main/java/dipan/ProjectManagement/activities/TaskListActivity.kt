package dipan.ProjectManagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import dipan.ProjectManagement.R
import dipan.ProjectManagement.adapters.TaskListItemAdapter
import dipan.ProjectManagement.databinding.ActivityTaskListBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.Task
import dipan.ProjectManagement.utils.Constants

class TaskListActivity : BaseActivity() {
    private var taskListBinding: ActivityTaskListBinding? = null

    private lateinit var mBoardDetails:Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //activate binding
        taskListBinding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(taskListBinding?.root)



        //retrieve the board document id
        var boardDocumentId=""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId=intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        //retrieve the relevant board details from document id
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this,boardDocumentId)
    }


    //this is called by the firestore class on success retrieval of a board
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
}