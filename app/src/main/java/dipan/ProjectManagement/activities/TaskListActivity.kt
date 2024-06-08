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


    fun boardDetails(board: Board){
        hideProgressDialog()

        //setup toolbar with the doc name
        setupToolbar(board.name)

        //just to show the in the UI
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
}