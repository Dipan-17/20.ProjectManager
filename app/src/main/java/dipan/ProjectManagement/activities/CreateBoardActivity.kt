package dipan.ProjectManagement.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.toColorInt
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : AppCompatActivity() {
    private var createBoardBinding: ActivityCreateBoardBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createBoardBinding= ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(createBoardBinding?.root)

        setupToolbar()
    }





    private fun setupToolbar(){
        val toolbar=createBoardBinding?.toolbarCreateBoardActivity

        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        toolbar?.title = resources.getString(R.string.create_board_title)
        toolbar?.setTitleTextColor("#ffffff".toColorInt())

        toolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }
}