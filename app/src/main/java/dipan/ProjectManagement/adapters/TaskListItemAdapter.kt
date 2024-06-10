package dipan.ProjectManagement.adapters

import android.annotation.SuppressLint
import android.content.Context


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dipan.ProjectManagement.R
import dipan.ProjectManagement.activities.TaskListActivity
import dipan.ProjectManagement.databinding.ItemTaskBinding
import dipan.ProjectManagement.models.Task
import java.util.Collections

open class TaskListItemAdapter(private val context: Context,
                               private val list: ArrayList<Task>):

    RecyclerView.Adapter<TaskListItemAdapter.MainViewHolder>() {

    //for dragging
        private var mPositionDraggedFrom = -1
        private var mPositionDraggedTo = -1
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

    override fun onBindViewHolder(holder: TaskListItemAdapter.MainViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model=list[position]

        //WHAT TO DISPLAY

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


        //FOR LISTS


        //add a new task
        holder.itemBinding.tvAddTaskList.setOnClickListener {
            holder.itemBinding.tvAddTaskList.visibility=View.GONE //add button is gone
            holder.itemBinding.cvAddTaskListName.visibility=View.VISIBLE //the card to take the name as input
        }

        //cancel adding a new task
        holder.itemBinding.ibCloseListName.setOnClickListener {
            holder.itemBinding.tvAddTaskList.visibility=View.VISIBLE
            holder.itemBinding.cvAddTaskListName.visibility=View.GONE
        }

        //agreed to creating a new task
        holder.itemBinding.ibDoneListName.setOnClickListener {
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



        //on click listeners to the add and delete list buttons
        holder.itemBinding.ibEditListName.setOnClickListener {
            holder.itemBinding.etTaskListName.setText(model.title)
            holder.itemBinding.llTitleView.visibility=View.GONE
            holder.itemBinding.cvEditTaskListName.visibility=View.VISIBLE
        }
        //cancel editing the name
        holder.itemBinding.ibCloseEditableView.setOnClickListener {
            holder.itemBinding.llTitleView.visibility=View.VISIBLE
            holder.itemBinding.cvEditTaskListName.visibility=View.GONE
        }
        //done editing the name
        holder?.itemBinding?.ibDoneEditListName?.setOnClickListener {
            val listName=holder.itemBinding.etEditTaskListName.text.toString()
            if(listName.isNotEmpty()){
                if(context is TaskListActivity) {
                    //edit the name of the list
                    context.updateTaskList(position,listName,model)
                }
            }else{
                //show error
                Toast.makeText(context,"Name cannot be empty",Toast.LENGTH_SHORT).show()
            }
        }
        //delete the list
        holder.itemBinding.ibDeleteList.setOnClickListener {
            alertDialogForDeleteList(position,model)
        }



        //for CARDS


        //for adding card
        holder.itemBinding.tvAddCard.setOnClickListener {
            holder.itemBinding.tvAddCard.visibility=View.GONE
            holder.itemBinding.cvAddCard.visibility=View.VISIBLE
        }
        //cancel adding card
        holder.itemBinding.ibCloseCardName.setOnClickListener {
            holder.itemBinding.tvAddCard.visibility=View.VISIBLE
            holder.itemBinding.cvAddCard.visibility=View.GONE
        }
        //done adding card
        holder.itemBinding.ibDoneCardName.setOnClickListener {
            val cardName=holder.itemBinding.etCardName.text.toString()
            if(cardName.isNotEmpty()){
                if(context is TaskListActivity) {
                    //add the card to the list
                    context.addCardToTaskList(position,cardName)
                }
            }else{
                //show error
                Toast.makeText(context,"Please enter a card name",Toast.LENGTH_SHORT).show()
            }
        }


        //CHILD RECYCLER VIEW:

        //recycler view for cards
        holder.itemBinding.rvCardList.layoutManager= LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        //holder.itemBinding.rvCardList.setHasFixedSize(true)
        val cardAdapter=CardListItemAdapter(context,model.card)
        holder.itemBinding.rvCardList.adapter=cardAdapter


        //onclick of cards
        cardAdapter.setOnClickListener(object:CardListItemAdapter.onClickInterface{
            override fun onClick(cardPosition: Int) {
                if(context is TaskListActivity){
                    context.cardDetails(position, cardPosition )
                }
            }
        })


        //parent holder
        holder.bindItem(model)




        //for drag and drop
        val dividerItemDecoration= DividerItemDecoration(context,LinearLayoutManager.VERTICAL)
        holder.itemBinding.rvCardList.addItemDecoration(dividerItemDecoration)

        val touchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    dragged: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPosition = dragged.adapterPosition
                    val targetPosition = target.adapterPosition

                    if(mPositionDraggedFrom==-1)mPositionDraggedFrom=draggedPosition

                    if(mPositionDraggedTo==-1)mPositionDraggedTo=targetPosition

                    //swap the cards
                    Collections.swap(list[position].card,draggedPosition,targetPosition)
                    cardAdapter?.notifyItemMoved(draggedPosition,targetPosition)

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    //not needed
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)

                    if(mPositionDraggedFrom!=-1 && mPositionDraggedTo!=-1 && mPositionDraggedFrom!=mPositionDraggedTo){
                        (context as TaskListActivity).updateCardInTaskList(position,list[position].card)
                    }

                    mPositionDraggedFrom=-1
                    mPositionDraggedTo=-1
                }
            }
        )

        touchHelper.attachToRecyclerView(holder.itemBinding.rvCardList)
    }

    override fun getItemCount(): Int {
        return list.size
    }



    //for delete button
    private fun alertDialogForDeleteList(position: Int, model: Task) {
        val builder = AlertDialog.Builder(context)
        //set the title
        builder.setTitle("Alert")
        //set the message
        builder.setMessage("Are you sure you want to delete ${model.title}?")
        builder.setIcon(R.drawable.ic_alert_24_dp)

        //set the positive button
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }
        //set the negative button
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        //create the dialog
        val alertDialog: AlertDialog = builder.create()
        //show the dialog
        alertDialog.show()
    }




    //we need the recycler view to cover only certain part of the screen
    //so we need to calculate the width accurately in dp
    private fun Int.toDP():Int= (this/context.resources.displayMetrics.density).toInt()
    //to get in pixel value
    private fun Int.toPx():Int= (this*context.resources.displayMetrics.density).toInt()
}