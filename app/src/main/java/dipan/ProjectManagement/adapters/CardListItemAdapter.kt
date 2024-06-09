package dipan.ProjectManagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import dipan.ProjectManagement.databinding.ItemCardBinding
import dipan.ProjectManagement.models.Board
import dipan.ProjectManagement.models.Card

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