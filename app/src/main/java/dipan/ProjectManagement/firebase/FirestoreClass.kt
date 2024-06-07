package dipan.ProjectManagement.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dipan.ProjectManagement.activities.MainActivity
import dipan.ProjectManagement.activities.MyProfileActivity
import dipan.ProjectManagement.activities.SignInActivity
import dipan.ProjectManagement.activities.SignUpActivity
import dipan.ProjectManagement.models.User
import dipan.ProjectManagement.utils.Constants

class FirestoreClass {
    private var mFireStore = FirebaseFirestore.getInstance()

    //register user not only in auth -> but also store in database so that we can use it later
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)//collection name we want to access
            .document(getCurrentUserID())//every user has his own document -> unique for each user
            .set(userInfo, SetOptions.merge())//set the data in the document
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("FireStoreError", "Error while registering the user.", e)
            }
    }


    //make the activity now general so that anyone can call
    fun loadUserData(activity: Activity){//kyuki calling activity ke instance pr  hi wapas jana hain
        mFireStore.collection(Constants.USERS)//is collection -> table
            .document(getCurrentUserID())//ka ye row -> cause the rows are identified by the user id
            .get()
            .addOnSuccessListener { document->
                val loggedInUser=document.toObject(User::class.java)!! // we are creating a user object from the document we get

                when(activity){
                    is SignInActivity ->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity ->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }
                }


            }
            .addOnFailureListener {
                e->
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e("FireStoreError", "Error while signing in")
            }
    }


    //function to updateProfile once update pressed
    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>) {
        //in fire store
        //there is a table -> collection
        //each user has a document -> row
        //inside each user: there is hashmap: name, email, mobile, image
        //there is key-value pair
        //so we are also passing a hash map to update

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.e("Firebase","Profile data updated successfully")
                //Toast.makeText(activity,"Profile updated successfully",Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e("Firebase", "Error while updating the user details.", e)
                Toast.makeText(activity,"Error in updating profile",Toast.LENGTH_SHORT).show()
            }

    }


     fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        //if the user is not null then get the uid of the user -> auto login
        //else return an empty string
        var currentUserID=""
        if(currentUser!=null){
            currentUserID=currentUser.uid.toString()
        }
        return currentUserID
    }
}