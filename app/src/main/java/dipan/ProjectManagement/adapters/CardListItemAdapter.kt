package dipan.ProjectManagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dipan.ProjectManagement.databinding.ItemCardBinding
import dipan.ProjectManagement.models.Card

open class CardListItemAdapter(val context: Context,
                               val cardList:List<Card>):
    RecyclerView.Adapter<CardListItemAdapter.MainViewHolder>()

{
    inner class MainViewHolder(val itemBinding:ItemCardBinding):RecyclerView.ViewHolder(itemBinding.root){

        //where do we get data
        fun bindItem(card:Card){
            itemBinding?.tvCardName?.text=card.name
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

        //Toast.makeText(context,"Card List Size: ${cardList.size}",Toast.LENGTH_SHORT).show()

        holder.bindItem(card)
    }
}