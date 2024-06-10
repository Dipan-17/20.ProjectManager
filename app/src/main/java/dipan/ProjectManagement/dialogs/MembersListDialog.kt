
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dipan.ProjectManagement.R
import dipan.ProjectManagement.adapters.LabelColorListItemsAdapter
import dipan.ProjectManagement.adapters.MemberItemsAdapter
import dipan.ProjectManagement.models.User


// START
abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
) :
    Dialog(context) {

    private var adapter: MemberItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        view.findViewById<TextView>(R.id.tvTitle).text=title

        if (list.size > 0) {

            val rvMembersList=view.findViewById<RecyclerView>(R.id.rvList)
            rvMembersList.layoutManager = LinearLayoutManager(context)

            adapter = MemberItemsAdapter(context, list)
            rvMembersList.adapter = adapter

            adapter!!.setOnClickListener(
                object: MemberItemsAdapter.onClickInterface{
                    override fun onClick(position: Int, user: User, action:String) {
                        dismiss()
                        onItemSelected(user,action)
                    }
                }
            )


        }
    }

    protected abstract fun onItemSelected(user: User, action:String)
}
// END