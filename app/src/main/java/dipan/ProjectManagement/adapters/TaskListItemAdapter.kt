package dipan.ProjectManagement.adapters

import android.content.Context


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dipan.ProjectManagement.R
import dipan.ProjectManagement.activities.TaskListActivity
import dipan.ProjectManagement.databinding.ItemTaskBinding
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.Task

open class TaskListItemAdapter(private val context: Context,
                               private val list: ArrayList<Task>):

    RecyclerView.Adapter<TaskListItemAdapter.MainViewHolder>() {


    inner class MainViewHolder(val itemBinding: ItemTaskBinding):RecyclerView.ViewHolder(itemBinding.root){
        //bind the data to the card
        fun bindItem(model: Task){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListItemAdapter.MainViewHolder {
        val holder= MainViewHolder(ItemTaskBinding.inflate
            (LayoutInflater.from(parent.context),
            parent,false))

        //the view holder should be 0.7 times the parent width
        val layoutParams= LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT
        )
        //assign margins to the view holder
        layoutParams.setMargins((15.toDP().toPx()),0,(40.toDP().toPx()),0)

        holder.itemView.layoutParams = layoutParams

        return holder
    }

    override fun onBindViewHolder(holder: TaskListItemAdapter.MainViewHolder, position: Int) {
        val model=list[position]

        if(position==list.size-1){
            //kyuki this is last element
            //iske andr kuch nhi hain
            //iska only function is to show the button to add a new task
            holder.itemBinding.tvAddTaskList.visibility= View.VISIBLE //this is the button that becomes active
            holder.itemBinding.llTaskItem.visibility=View.GONE //since there is no task to show
        }else{
            //this a existing item
            holder.itemBinding.tvAddTaskList.visibility= View.GONE
            holder.itemBinding.llTaskItem.visibility=View.VISIBLE
        }

        holder.itemBinding.tvTaskListTitle.text =model.title

        //add a new task
        holder.itemBinding.tvAddTaskList.setOnClickListener {
            holder.itemBinding.tvAddTaskList.visibility=View.GONE //add button is gone
            holder.itemBinding.cvAddTaskListName.visibility=View.VISIBLE //the card to take the name as input
        }

        //cancel adding a new task
        holder?.itemBinding?.ibCloseListName?.setOnClickListener {
            holder.itemBinding.tvAddTaskList.visibility=View.VISIBLE
            holder.itemBinding.cvAddTaskListName.visibility=View.GONE
        }

        //agreed to creating a new task
        holder?.itemBinding?.ibDoneListName?.setOnClickListener {
            val listName=holder.itemBinding.etTaskListName.text.toString()
            if(listName.isNotEmpty()){
                if(context is TaskListActivity) {
                    //add the task to the list
                    context.createTaskList(listName)
                }
            }else{
                //show error
                Toast.makeText(context,"Please enter a task name",Toast.LENGTH_SHORT).show()
            }


        }

        holder.bindItem(model)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //we need the recycler view to cover only certain part of the screen
    //so we need to calculate the width accurately in dp
    private fun Int.toDP():Int= (this/context.resources.displayMetrics.density).toInt()
    //to get in pixel value
    private fun Int.toPx():Int= (this*context.resources.displayMetrics.density).toInt()
}