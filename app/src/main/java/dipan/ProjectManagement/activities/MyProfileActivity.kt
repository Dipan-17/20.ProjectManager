package dipan.ProjectManagement.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.io.IOException
import java.util.jar.Manifest

class MyProfileActivity : BaseActivity() {
    private var profileBinding: ActivityMyProfileBinding? = null //the parent layout


    //for permission code
    companion object{
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val PICK_IMAGE_REQUEST_CODE=2
    }

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    //for downloading
    private var mProfileImageURL: String = ""

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
                            showImageChooser()
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
            }
        }

    }

    //to pick image from gallery
    private fun showImageChooser(){
        //pick image from gallery
        val galleryIntent=Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

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
    }


    //upload to storage
    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        //image exists then only upload
        if(mSelectedImageFileUri!=null){

            val sRef:StorageReference=FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE"+System.currentTimeMillis()+"."+
                        getFileExtension(mSelectedImageFileUri!!) //name of the file we want => generate unique name
            )

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->//write the file in storage
                Log.i("Firebase Image URL",taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                //store the link to the image int somewhere -> storage
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageURL=uri.toString() //get the link to the image in storage

                    //TODO update the profile using this URI
                    showErrorSnackBar("Task successfully completed")
                }
            }.addOnFailureListener{
                showErrorSnackBar(it.message.toString())

            }

        }
        hideProgressDialog()
    }


    //check the file type from the URI  to check if we can use as image
    private fun getFileExtension(uri: Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
    }

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