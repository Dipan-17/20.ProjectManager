package dipan.ProjectManagement.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ActivityCreateBoardBinding
import dipan.ProjectManagement.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var createBoardBinding: ActivityCreateBoardBinding? = null

    private var mSelectedImageFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createBoardBinding= ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(createBoardBinding?.root)

        setupToolbar()

        //image picker
        createBoardBinding?.ivBoardImage?.setOnClickListener {
            checkPermission()
        }

    }

    //permission handling
    private fun checkPermission(){
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted()){
                        Constants.showImageChooser(this@CreateBoardActivity)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
    }
    //rationale for permission
    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permissions required for this feature. It can be enabled under the Application settings")
            .setPositiveButton("GO TO SETTINGS"){
                    _,_ ->
                try{
                    val intent= Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri= Uri.fromParts("package",packageName,null)
                    intent.data=uri
                    startActivity(intent)
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
            .setNegativeButton("CANCEL"){
                    dialog,_ ->
                dialog.dismiss()
            }.show()
    }


    //activity result handling
    //1.Set image from gallery intent
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //if the result is ok
        if (resultCode == Activity.RESULT_OK) {
            //activity result is of gallery
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    showErrorSnackBar("Image selection successful")
                    mSelectedImageFileUri = data.data
                    Log.i("Image URI", mSelectedImageFileUri.toString())
                    try {
                        Glide.with(this@CreateBoardActivity)
                            .load(mSelectedImageFileUri)
                            .fitCenter()
                            .placeholder(R.drawable.ic_board_place_holder)
                            .into(createBoardBinding!!.ivBoardImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        showErrorSnackBar("Image selection failed")
                    }
                } else {
                    Toast.makeText(this@CreateBoardActivity, "Invalid URI", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            //other activity results

        }
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