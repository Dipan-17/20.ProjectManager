package dipan.ProjectManagement.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ActivityCardDetailsBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.Card
import dipan.ProjectManagement.models.Task
import dipan.ProjectManagement.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private var cardDetailsBinding: ActivityCardDetailsBinding? = null

    //to get values
    private lateinit var mBoardDetails:Board
    private  var mTaskListPosition:Int=-1
    private var mCardListPosition:Int=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardDetailsBinding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(cardDetailsBinding?.root)

        getIntentData()


        //set the toolbar and edit text name to the existing name
        var currentCard=mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition]//these are indexes only -> not one based
        setupToolbar(currentCard.name)
        cardDetailsBinding?.etNameCardDetails?.setText(currentCard.name)

        //directly move the cursor to the end of the text
        cardDetailsBinding?.etNameCardDetails?.setSelection(currentCard.name.length)

        //update button
        cardDetailsBinding?.btnUpdateCardDetails?.setOnClickListener {
            if(cardDetailsBinding?.etNameCardDetails?.text.toString().isNotEmpty()) {
                updateCardDetails()
            }
            else{
                Toast.makeText(this,"Please enter a card name",Toast.LENGTH_SHORT).show()
            }

        }


    }

    //get the intent data
    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoardDetails=intent.getParcelableExtra<Board>(Constants.BOARD_DETAILS)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition=intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardListPosition=intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
    }

    //setup menu icon
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }
    //onclick of menu options
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when(item.itemId){
            cardDetailsBinding?.toolbarCardDetailsActivity.let{R.id.action_delete_member}->{
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //to update the card :
    //we have the entire current board
    //edit at the position we want
    //update the board
    private fun updateCardDetails(){
        //create a new card from the upated details
        val card=Card(
            cardDetailsBinding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].assignedTo
        )

        //assign the new card in the position of the old card
        mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition]=card

        showProgressDialog(resources.getString(R.string.please_wait))

        //update the board
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    //delete the card
    //remove from board
    private fun deleteCardDetails(){

        val cardList:ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].card
        cardList.removeAt(mCardListPosition)//remove the card from the list

        //access the tasklist inside the board
        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1) //remove the last item -> add button

        taskList[mTaskListPosition].card=cardList //assign the updated card list to the task list

        showProgressDialog(resources.getString(R.string.please_wait))

        //update the board
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }


    //delete alert dialog
    private fun alertDialogForDeleteCard(cardName:String) {
        val builder = AlertDialog.Builder(this@CardDetailsActivity)
        //set the title
        builder.setTitle("Alert")
        //set the message
        builder.setMessage("Are you sure you want to delete ${cardName}?")
        builder.setIcon(R.drawable.ic_alert_24_dp)

        //set the positive button
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            deleteCardDetails()
        }
        //set the negative button
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }


        //create the dialog
        val alertDialog: AlertDialog = builder.create()

        alertDialog.setCancelable(false)
        //show the dialog
        alertDialog.show()
    }


    //updating of card list is success
    //called by firestore on success of updating
    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }


    //setup toolbar
    private fun setupToolbar(title:String){

        val toolbar=cardDetailsBinding?.toolbarCardDetailsActivity

        setSupportActionBar(toolbar)

        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        actionBar?.title = title
        toolbar?.setTitleTextColor("#ffffff".toColorInt())

        toolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }
}