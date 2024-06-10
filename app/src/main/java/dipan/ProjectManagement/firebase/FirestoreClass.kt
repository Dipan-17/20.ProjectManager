package dipan.ProjectManagement.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dipan.ProjectManagement.activities.CardDetailsActivity
import dipan.ProjectManagement.activities.CreateBoardActivity
import dipan.ProjectManagement.activities.MainActivity
import dipan.ProjectManagement.activities.MembersActivity
import dipan.ProjectManagement.activities.MyProfileActivity
import dipan.ProjectManagement.activities.SignInActivity
import dipan.ProjectManagement.activities.SignUpActivity
import dipan.ProjectManagement.activities.TaskListActivity
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.User
import dipan.ProjectManagement.utils.Constants

class FirestoreClass {
    private var mFireStore = FirebaseFirestore.getInstance()


    //User collection related functions


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
    fun loadUserData(activity: Activity,readBoardsList: Boolean=false){//kyuki calling activity ke instance pr  hi wapas jana hain
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
                        activity.updateNavigationUserDetails(loggedInUser,readBoardsList)
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





    //board collection functions

    //create a new board
    fun createBoard(activity: CreateBoardActivity,board:Board){
            mFireStore.collection(Constants.BOARDS)//collection name we want to access
                .document()//every board has a document id in the firebase storage -> this is stored in the document id column of the board class
                .set(board, SetOptions.merge())//set the data in the document
                .addOnSuccessListener {
                    Toast.makeText(activity,"Board created successfully",Toast.LENGTH_SHORT).show()
                    activity.boardCreatedSuccessfully()
                }
                .addOnFailureListener {
                    e ->
                    activity.hideProgressDialog()
                    Log.e("FireStoreError", "Error while Creating the board", e)
                }

    }


    //get list of all boards
    fun getBoardsList(activity:MainActivity){
        mFireStore.collection(Constants.BOARDS)
            //get all the matching documents -> whereArray
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserID())//check if the user is assigned to the board
            .get()
            .addOnSuccessListener { document ->
                Log.e("FireStore: ","DocumentList" + document.documents.toString())
                val boardList:ArrayList<Board> = ArrayList()

                //get all boards in my list
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!

                    board.documentId=i.id
                    boardList.add(board)
                }
                activity.populateBoardsListToUI(boardList)
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e("FireStoreError", "Error while getting the board list", e)
            }
    }


    //get the specific board details for task list activity
    fun getBoardDetails(activity: TaskListActivity,documentId:String){
        mFireStore.collection(Constants.BOARDS)
            //only get the specific board
            .document(documentId)
            .get()
            .addOnSuccessListener { document->

                val board=document.toObject(Board::class.java)!!
                board.documentId=documentId
                activity.boardDetails(board)

            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e("FireStoreError", "Error while getting the board details", e)
            }
    }

    //create a new task, update name, delete one
    //this function opens up the current board
    //each board has a list of task
    //this function directly updates the hashmap -> create update delete everything

    //UPDATE: we are updating the activity type from TaskListActivity to any activity
    //earlier we were only updating the task list -> adding, updating or deleting old task
    //now we want to edit the cards also
    //cards are what -> inside the task list
    //task list are inside the board

    //earlier to update the board -> we were passing the entire edited board
    //this time also do the same
    //cause editCardDetails has got the information of the entire parent board

    //we will update the taskList only inside and then pass it here

    fun addUpdateTaskList(activity: Activity,board:Board){
        val taskListHashMap=HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST]=board.taskList//this is the updated task list

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)//we are passing id-> Create | if empty -> create a new one
            .update(taskListHashMap)//replace the prev hash map with the new one
            .addOnSuccessListener {
                Log.e("FireStoreSuccess","Task List updated successfully")

                //if the activity is TaskListActivity
                if(activity is TaskListActivity){
                    activity.addUpdateTaskListSuccess()
                }
                else if(activity is CardDetailsActivity){
                    activity.addUpdateTaskListSuccess()
                }


            }
            .addOnFailureListener {
                e->
                if(activity is TaskListActivity){
                    activity.hideProgressDialog()
                }
                else if(activity is CardDetailsActivity){
                    activity.hideProgressDialog()
                }

                Log.e("FireStoreError", "Error while adding the task list", e)
            }
    }

    //get all the members details from the assigned list
    fun getAssignedMemberListDetails(activity:Activity,memberID:ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            //get all the matching documents -> whereArray
            .whereIn(Constants.ID,memberID)//check if the id matches the current
            .get()
            .addOnSuccessListener {
                document ->

                Log.e("FireStore: ","MembersList: " + document.documents.toString())
                Log.e("FireStore: ","MembersListSize: " + document.size().toString())

                val memberList:ArrayList<User> = ArrayList()
                //get all users in my list
                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    memberList.add(user)
                }

                if(activity is MembersActivity){
                    activity.setUpMemberListRV(memberList)
                }else if(activity is TaskListActivity){
                    activity.boardMemberDetailsList(memberList)
                }

            }
            .addOnFailureListener {
                e->
                if(activity is MembersActivity){
                    activity.hideProgressDialog()
                }else if(activity is TaskListActivity){
                    activity.hideProgressDialog()
                }
                Log.e("FireStoreError", "Error while getting the members list", e)
            }

    }

    fun getMemberDetails(activity: MembersActivity,email:String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email) //one email only -> equal to
            .get()
            .addOnSuccessListener {
                document ->
                if(document.documents.size>0){
                    val user=document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                }
                else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such user exists")
                }
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e("FireStoreError", "Error while getting the member details", e)
            }
    }

    fun assignMemberToBoard(activity: MembersActivity,board: Board,user: User){
        val assignedToHashMap=HashMap<String,Any>()
        //the key to this hashmap -> should be the field we want to update in the firestore
        //the value is the new value that we want to put in the field
        assignedToHashMap[Constants.ASSIGNED_TO]=board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e("FireStoreError", "Error while assigning the member to the board", e)
            }
    }

}