package dipan.ProjectManagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dipan.ProjectManagement.activities.TaskListActivity
import dipan.ProjectManagement.databinding.ItemCardBinding
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.Card
import dipan.ProjectManagement.models.SelectedMembers

open class CardListItemAdapter(val context: Context,
                               val cardList:List<Card>):
    RecyclerView.Adapter<CardListItemAdapter.MainViewHolder>()

{
    private var onClickListener: onClickInterface?=null
    inner class MainViewHolder(val itemBinding:ItemCardBinding):RecyclerView.ViewHolder(itemBinding.root){

        //where do we get data
        fun bindItem(card:Card){
            itemBinding?.tvCardName?.text=card.name

            //set the color of the card
            val cardColor=card.labelColor
            if(cardColor.isNotEmpty()){
                itemBinding.viewLabelColor?.visibility=android.view.View.VISIBLE
                itemBinding.viewLabelColor?.setBackgroundColor(cardColor.toColorInt())
            }else{
                itemBinding.viewLabelColor?.visibility=android.view.View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {

        return cardList.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val card=cardList[position]
        holder.bindItem(card)

        if((context as TaskListActivity)
                .mAssignedMembersDetailList.size>0){

            //the members for current card
            val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()


            for(i in context.mAssignedMembersDetailList.indices){//board ke assigned members (users) -> global
                for(j in card.assignedTo){//card ke assigned members -> local (string -> id)
                    if(context.mAssignedMembersDetailList[i].id==j){
                        val selectedMember = SelectedMembers(
                            context.mAssignedMembersDetailList[i].id,
                            context.mAssignedMembersDetailList[i].image
                        )
                        selectedMembersList.add(selectedMember)
                    }

                }
            }

            if(selectedMembersList.size>0){
                //if the creator itself only is the member
                if(selectedMembersList.size==1 && selectedMembersList[0].id==context.mAssignedMembersDetailList[0].id){
                    holder.itemBinding.rvMembersCardFront.visibility= View.GONE
                }else{
                    holder.itemBinding.rvMembersCardFront.visibility= View.VISIBLE
                    holder.itemBinding.rvMembersCardFront.setHasFixedSize(true)
                    holder.itemBinding.rvMembersCardFront.layoutManager=GridLayoutManager(context,4)

                    //for the + icon showing in the cards of the taskList also
                    val adapter= CardMemberListItemsAdapter(context,selectedMembersList,false)

                    holder.itemBinding.rvMembersCardFront.adapter= adapter

                }

            }else{
                holder.itemBinding.rvMembersCardFront.visibility= View.GONE
            }

        }

        holder.itemView.setOnClickListener{
            //whatever you want to do when clicked
            if(onClickListener!=null){
                onClickListener!!.onClick(position)
            }
        }

    }

    //for clicking recycler view items
    fun setOnClickListener(onClickListener: onClickInterface){
        this.onClickListener=onClickListener
    }

    interface onClickInterface{
        //we need position to identify
        //model to populate the detail activity
        fun onClick(position: Int)
    }
}