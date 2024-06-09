package dipan.ProjectManagement.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.core.graphics.toColorInt
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ActivityCardDetailsBinding
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.utils.Constants

class CardDetailsActivity : AppCompatActivity() {

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
                //TODO delet
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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