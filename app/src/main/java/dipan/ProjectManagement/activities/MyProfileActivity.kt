package dipan.ProjectManagement.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import dipan.ProjectManagement.databinding.ActivityMyProfileBinding
import dipan.ProjectManagement.firebase.FirestoreClass
import dipan.ProjectManagement.models.User
import dipan.ProjectManagement.utils.Constants
import dipan.ProjectManagement.utils.Constants.PICK_IMAGE_REQUEST_CODE
import java.io.IOException
import java.util.jar.Manifest

class MyProfileActivity : BaseActivity() {
    private var profileBinding: ActivityMyProfileBinding? = null //the parent layout


    //for permission code -> In constants
//    companion object{
//        private const val READ_STORAGE_PERMISSION_CODE=1
//        private const val PICK_IMAGE_REQUEST_CODE=2
//    }

    // Add a global variable for URI of a selected image from "phone storage."
    private var mSelectedImageFileUri: Uri? = null

    //URI of the image in "firebase storage"
    //used for updating
    private var mProfileImageURL: String = ""

    //for updating profile
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileBinding= ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding?.root)

        setupToolbar()

        //to populate data
        FirestoreClass().loadUserData(this)

        //for updating the image
        profileBinding?.ivProfileUserImage?.setOnClickListener {
            //check for permission
            Dexter.withActivity(this)
                .withPermissions(
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.ACCESS_NETWORK_STATE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if(report!!.areAllPermissionsGranted()){
                            Constants.showImageChooser(this@MyProfileActivity)
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

        //update button
        profileBinding?.btnUpdate?.setOnClickListener {
            if(mSelectedImageFileUri!=null){
                uploadUserImage()


            // we will need to call update profile from inside the uploadUserImage
                //cause if we dont do that -> even before image is uploaded the update will be called
                //it give error

//                Handler().postDelayed({
//                    //just wait 1 s
//                },1000)
            }


            else{
                showProgressDialog(resources.getString(R.string.please_wait_update))
                updateUserProfileData()
            }



        }

    }

    //to pick image from gallery
    //function in constant now

    //get activity result
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //if the result is ok
        if (resultCode == Activity.RESULT_OK) {
            //activity result is of gallery
            if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    showErrorSnackBar("Image selection successful")
                    mSelectedImageFileUri=data.data
                    Log.i("Image URI",mSelectedImageFileUri.toString())
                    try {
                        Glide.with(this@MyProfileActivity)
                            .load(mSelectedImageFileUri)
                            .fitCenter()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(profileBinding!!.ivProfileUserImage)
                    }catch (e: IOException) {
                        e.printStackTrace()
                        showErrorSnackBar("Image selection failed")
                    }
                }
                else{
                    Toast.makeText(this@MyProfileActivity,"Invalid URI",Toast.LENGTH_SHORT).show()
                }
            }

            //other activity results

        }
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


    //to populate user data from remote
     fun setUserDataInUI(user: User){
        profileBinding?.apply {
            etName.setText(user.name)
            etEmail.setText(user.email)
            etMobile.setText(user.mobile.toString())

            Glide.with(this@MyProfileActivity)
                .load(user.image)
                .fitCenter()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(ivProfileUserImage)
        }

        //set the data also in global variable so that it can be used for updating
        mUserDetails=user
    }


    //upload to storage
    private fun uploadUserImage(){


        //image exists then only upload
        if(mSelectedImageFileUri!=null){
            showProgressDialog(resources.getString(R.string.please_wait))

            val sRef:StorageReference=FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE"+System.currentTimeMillis()+"."+
                        Constants.getFileExtension(this,mSelectedImageFileUri!!) //name of the file we want => generate unique name
            )

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->//write the file in storage

                //store the link to the image int somewhere -> storage
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {uri->
                    Log.e("Firebase_Image_URL",uri.toString())

                    //we are storing the link to the image in storage in a global variable
                    //we will use this to check whether the image is updated or is it the same as before

                    mProfileImageURL=uri.toString() //get the link to the image in storage


                    updateUserProfileData() //update the profile after image is uploaded



                }
            }.addOnFailureListener{
                hideProgressDialog()
                showErrorSnackBar("Error in uploading image")

            }

        }

    }


    //to update profile after button pressed successfully
    private fun updateUserProfileData(){


        val userHashMap=HashMap<String, Any>()

        var anyChangesMade=false

        //add values to hashmap

        //update image if it is not the same
        //mUserDetails is the current user details fetched from remote
        //mProfileImage will be empty if we have not selected a new image
        if(!mProfileImageURL.isNullOrEmpty() && mProfileImageURL!=mUserDetails.image){
            userHashMap[Constants.IMAGE]=mProfileImageURL
            anyChangesMade=true
        }
        if(profileBinding?.etName?.text.toString()!=mUserDetails.name){
            userHashMap[Constants.NAME]=profileBinding?.etName?.text.toString()
            anyChangesMade=true
        }
        if(profileBinding?.etMobile?.text.toString()!=mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=profileBinding?.etMobile?.text.toString().toLong()
            anyChangesMade=true
        }

        FirestoreClass().updateUserProfileData(this,userHashMap)

//        if(anyChangesMade){
//            showProgressDialog(resources.getString(R.string.please_wait_update))
//            FirestoreClass().updateUserProfileData(this,userHashMap)
//        }else{
//            showErrorSnackBar("No changes made")
//        }

    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        showErrorSnackBar("Profile updated successfully")

        setResult(Activity.RESULT_OK)//for updating the side nav

    }


    //check the file type from the URI  to check if we can use as image
    //  =>Now in constant
    private fun setupToolbar(){

        val toolbar=profileBinding?.toolbarMyProfileActivity

        setSupportActionBar(toolbar)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)

        toolbar?.title = "My Profile"
        toolbar?.setTitleTextColor("#ffffff".toColorInt())

        toolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }
}