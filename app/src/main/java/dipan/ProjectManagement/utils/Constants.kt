package dipan.ProjectManagement.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import dipan.ProjectManagement.activities.MyProfileActivity

object Constants {
    //collections in firestore
    const val USERS: String="Users" //the table
    const val BOARDS:String="Boards"



    //fields in the user Users table
    const val IMAGE:String="image"
    const val NAME:String="name"
    const val MOBILE:String="mobile"
    const val ASSIGNED_TO:String="assignedTo"


    //for passing ids to tasklist to retrieve info
    const val DOCUMENT_ID: String ="documentID"

    //create a new board
    const val TASK_LIST:String="taskList"

    //for passing details to show members
    const val BOARD_DETAILS: String="boardDetails"

    //to get the user details from the user id -> for displaying members -> cause board only has assigned members id
    const val ID:String="id"

    //getting data from email address
    const val EMAIL:String="email"

    //permission codes
    const val READ_STORAGE_PERMISSION_CODE=1
    const val PICK_IMAGE_REQUEST_CODE=2


    //helper functions
     fun getFileExtension(activity: Activity,uri: Uri):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri))
    }

     fun showImageChooser(activity: Activity){
        //pick image from gallery
        val galleryIntent= Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
}