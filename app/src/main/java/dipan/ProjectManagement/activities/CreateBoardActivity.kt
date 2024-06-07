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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ActivityCreateBoardBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var createBoardBinding: ActivityCreateBoardBinding? = null

    private var mSelectedImageFileUri: Uri? = null //for uploading from device

    private lateinit var mUserName:String//to know who created the board

    private var mBoardImageURL:String=""//to store the image url in remote



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createBoardBinding= ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(createBoardBinding?.root)

        setupToolbar()

        //get user name
        if(intent.hasExtra(Constants.NAME)){
            mUserName=intent.getStringExtra(Constants.NAME).toString()
        }

        //image picker
        createBoardBinding?.ivBoardImage?.setOnClickListener {
            checkPermission()
        }

        createBoardBinding?.btnCreate?.setOnClickListener {
            if(mSelectedImageFileUri!=null) {
                uploadBoardImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
            //if we have not selected any image -> mSelectedImageFileUri will be null
            //we directly call createBoard
            //create board just passes the URL of the firebase storage image as "empty"
            //so we can create without any image as well in this code
        }


    }

    //board creation
    private fun createBoard(){
        //add owner to user list
        val assignedUsersArrayList:ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        //create the board information
        val board=Board(
            createBoardBinding?.etBoardName?.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this@CreateBoardActivity,board)

    }

    //to know successful creation
    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)

        finish()
    }


    //picture upload in storage
    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        //image exists then only upload
        if(mSelectedImageFileUri!=null){
            showProgressDialog(resources.getString(R.string.please_wait))

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE"+System.currentTimeMillis()+"."+
                        Constants.getFileExtension(this,mSelectedImageFileUri!!) //name of the file we want => generate unique name
            )

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->//write the file in storage

                //store the link to the image int somewhere -> storage
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {uri->
                    Log.e("Board_Image_URL",uri.toString())

                    //we are storing the link to the image in storage in a global variable
                    //we will use this to check whether the image is updated or is it the same as before

                    mBoardImageURL=uri.toString() //get the link to the image in storage
                    createBoard() //create board in remote



                }
            }.addOnFailureListener{
                hideProgressDialog()
                showErrorSnackBar("Error in uploading image")

            }
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


    //utility
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