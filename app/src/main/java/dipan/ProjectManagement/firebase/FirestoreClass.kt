package dipan.ProjectManagement.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dipan.ProjectManagement.activities.SignUpActivity
import dipan.ProjectManagement.models.User
import dipan.ProjectManagement.utils.Constants

class FirestoreClass {
    private var mFireStore = FirebaseFirestore.getInstance()

    //register user not only in auth -> but also store in database so that we can use it later
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)//collection name
            .document(getCurrentUserID())//every user has his own document -> unique for each user
            .set(userInfo, SetOptions.merge())//set the data in the document
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }

    private fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}