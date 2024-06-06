package dipan.ProjectManagement.firebase

import com.google.firebase.firestore.FirebaseFirestore
import dipan.ProjectManagement.activities.SignUpActivity
import dipan.ProjectManagement.models.User

class FirestoreClass {
    private var mFireStore = FirebaseFirestore.getInstance()

    //register user not only in auth -> but also store in database so that we can use it later
    fun registerUser(activity: SignUpActivity, userInfo: User) {

    }
}