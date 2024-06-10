package dipan.ProjectManagement.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

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

    //to pass the member list
    const val BOARD_MEMBERS_LIST:String="board_members_lisr"

    //for selecting and deselcting the members inside card details
    const val SELECT:String="select"
    const val UNSELECT:String="unselect"

    //to get card details for editing the card inside card details activity
    const val TASK_LIST_ITEM_POSITION="taskListItemPosition"
    const val CARD_LIST_ITEM_POSITION="cardListItemPosition"


    //permission codes
    const val READ_STORAGE_PERMISSION_CODE=1
    const val PICK_IMAGE_REQUEST_CODE=2


    //for storing tokens in shared preferences & notifications
    const val PROJECT_MANAGER_PREFERENCES: String="ProjectManagementPrefs"
    const val FCM_TOKEN_UPDATED:String="fcmTokenUpdated"
    const val FCM_TOKEN:String="fcmToken"



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