package dipan.ProjectManagement.activities

import MembersListDialog
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.GridLayoutManager
import dipan.ProjectManagement.R
import dipan.ProjectManagement.adapters.CardMemberListItemsAdapter
import dipan.ProjectManagement.databinding.ActivityCardDetailsBinding
import dipan.ProjectManagement.dialogs.LabelColorListDialog
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.Card
import dipan.ProjectManagement.models.SelectedMembers
import dipan.ProjectManagement.models.Task
import dipan.ProjectManagement.models.User
import dipan.ProjectManagement.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CardDetailsActivity : BaseActivity() {

    private var cardDetailsBinding: ActivityCardDetailsBinding? = null

    //to get values
    private lateinit var mBoardDetails:Board
    private  var mTaskListPosition:Int=-1
    private var mCardListPosition:Int=-1

    //all the members of the current "board"
    //get from intent
    private lateinit var mMembersList : ArrayList<User>

    //card color
    private var mSelectedColor:String="FFFFFF"

    //due date
    private var mSelectedDueDateMilliSeconds:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardDetailsBinding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(cardDetailsBinding?.root)

        getIntentData()


        //set the toolbar and edit text name to the existing name
        //set the color to existing
        //set date to existing
        var currentCard=mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition]//these are indexes only -> not one based
        setupToolbar(currentCard.name)
        cardDetailsBinding?.etNameCardDetails?.setText(currentCard.name)

        //directly move the cursor to the end of the text
        cardDetailsBinding?.etNameCardDetails?.setSelection(currentCard.name.length)

        //color
        mSelectedColor=mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].labelColor
        if(mSelectedColor.isNotEmpty()){
            setColor()
        }

        //original date
        mSelectedDueDateMilliSeconds=currentCard.dueDate
        if(mSelectedDueDateMilliSeconds>0){
            val sdf= SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate=sdf.format(mSelectedDueDateMilliSeconds)
            cardDetailsBinding?.tvSelectDueDate?.text=selectedDate
        }


        //update button
        cardDetailsBinding?.btnUpdateCardDetails?.setOnClickListener {
            if(cardDetailsBinding?.etNameCardDetails?.text.toString().isNotEmpty()) {
                updateCardDetails()
            }
            else{
                Toast.makeText(this,"Please enter a card name",Toast.LENGTH_SHORT).show()
            }

        }

        //card color
        cardDetailsBinding?.tvSelectLabelColor?.setOnClickListener {
            labelColorListDialog()
        }

        //select member list
        cardDetailsBinding?.tvSelectMembers?.setOnClickListener {
            membersListDialog()
        }

        //date
        cardDetailsBinding?.tvSelectDueDate?.setOnClickListener {
            showDatePicker()
        }



        //members display
        setupSelectedMembersList()
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
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersList=intent.getParcelableArrayListExtra<User>(Constants.BOARD_MEMBERS_LIST)!!
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
            mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds

        )

        //we do not want to add "Add card" to database
        val taskList:ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)//remove the last item -> add button

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


    //for card colors
    private fun colorsList():ArrayList<String>{
        val colorsList:ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#5A67D8")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        return colorsList
    }

    private fun setColor(){
        cardDetailsBinding?.tvSelectLabelColor?.text = ""
        cardDetailsBinding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))

    }

    //trigger dialog
    private fun labelColorListDialog(){
        val colorsList=colorsList()
        val listDialog=object: LabelColorListDialog(
            this,//context
            colorsList, //color list
            resources.getString(R.string.str_select_label_color), //title
            mSelectedColor)

        {
            override fun onItemSelected(color: String) {
                mSelectedColor=color
                setColor()
            }
        }
        listDialog.show()
    }

    //members list
    private fun membersListDialog() {
        //user ids of all the members of the "CARD"
        val cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].assignedTo

        if (cardAssignedMembersList.size > 0) {
            for (i in mMembersList.indices) {//for all members in the board
                for (j in cardAssignedMembersList) {//check each card -> if it is assigned to the member
                    if (mMembersList[i].id == j) {
                        mMembersList[i].selected = true
                    }
                }
            }
        }else{
            for(i in mMembersList.indices){
                mMembersList[i].selected=false
            }

        }

        val listDialog=object:MembersListDialog(
            this,
            mMembersList,
            resources.getString(R.string.str_select_members)
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    //if not already present -> add the user id to the card
                    if(!mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].assignedTo.add(user.id)
                        Toast.makeText(this@CardDetailsActivity,"Successfully assigned",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@CardDetailsActivity,"The member is already assigned",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    //remove the user id from the card
                    mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].assignedTo.remove(user.id)
                    Toast.makeText(this@CardDetailsActivity,"Successfully removed",Toast.LENGTH_SHORT).show()

                    for(i in mMembersList.indices){
                       if(mMembersList[i].id==user.id){
                           mMembersList[i].selected=false
                       }
                    }
                }
                //update the rv
                setupSelectedMembersList()

                //we do not update in database => we update only when we update the card
                //i.e only when update button is pressed
                //we are making all channges in the local board object
                //on clicking update we update the entire board in the database

            }

        }

        listDialog.show()
    }



    //rv adapter
    private fun setupSelectedMembersList(){
        val cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition].assignedTo

        val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()

        if(cardAssignedMembersList.size>0){//comes from the card -> all ids of card members
            for(i in mMembersList.indices){//all members of the board -> user model
                for(j in cardAssignedMembersList){
                    if(mMembersList[i].id==j){
                        val selectedMember=SelectedMembers(
                            mMembersList[i].id,
                            mMembersList[i].image
                        )
                        selectedMembersList.add(selectedMember)
                    }
                }
            }
        }

        if(selectedMembersList.size>0){
            selectedMembersList.add(SelectedMembers("",""))//add the last item -> add button -> cause we use the last index item as a + icon

            cardDetailsBinding?.tvSelectMembers?.visibility=View.GONE
            cardDetailsBinding?.rvSelectedMembersList?.visibility=View.VISIBLE


            val adapter=CardMemberListItemsAdapter(this,selectedMembersList,true)
            cardDetailsBinding?.rvSelectedMembersList?.adapter= adapter
            cardDetailsBinding?.rvSelectedMembersList?.layoutManager= GridLayoutManager(this,6)

            adapter.setOnItemClickListener(object:CardMemberListItemsAdapter.OnItemClickListener{
                override fun onItemClick() {
                    //this function was called if we click on select members tv
                    //if there are members in the list -> hide the tv
                    //so to access it click on any item
                    membersListDialog()
                }
            })
        }else{
            cardDetailsBinding?.tvSelectMembers?.visibility=View.VISIBLE
            cardDetailsBinding?.rvSelectedMembersList?.visibility=View.GONE

        }

    }


    //date picker
    private fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val sDayOfMonth = if(dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if(month < 10) "0${month + 1}" else "${month + 1}"
                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"

                //set in the text view
                cardDetailsBinding?.tvSelectDueDate?.text = selectedDate

                val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )

        // Set the DatePickerDialog to not show dates before today
        dpd.datePicker.minDate = System.currentTimeMillis()

        dpd.show()
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