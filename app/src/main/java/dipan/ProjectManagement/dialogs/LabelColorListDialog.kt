package dipan.ProjectManagement.dialogs

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

abstract class LabelColorListDialog
    (context: Context,
     private val list: ArrayList<String>,
     private val title:String,
     private var mSelectedColor: String

):Dialog(context)

{
    private var adapter: LabelColorListItemsAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //give a layout to the dialog
        val view= LayoutInflater.from(context).inflate(R.layout.dialog_color_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView(view)
    }

    private fun setupRecyclerView(view: View){
        view.findViewById<TextView>(R.id.tvColorTitle).text=title

        val rvColorList=view.findViewById<RecyclerView>(R.id.rvColorList)
        rvColorList.layoutManager = LinearLayoutManager(context)
        //set the adapter
        adapter= LabelColorListItemsAdapter(context,list,mSelectedColor)
        rvColorList.adapter=adapter

        adapter!!.setOnClickListener(
            object: LabelColorListItemsAdapter.onClickInterface{
                override fun onClick(position: Int, color: String) {
                    dismiss()
                    onItemSelected(color)
                }
            }
        )
    }

    //need to implement this in the activity where we create objects
    protected abstract fun onItemSelected(color: String)
}