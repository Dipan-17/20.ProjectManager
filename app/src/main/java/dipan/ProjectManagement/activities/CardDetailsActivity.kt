package dipan.ProjectManagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        var currentCard=mBoardDetails.taskList[mTaskListPosition].card[mCardListPosition]//these are indexes only -> not one based

        setupToolbar(currentCard.name)



    }

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