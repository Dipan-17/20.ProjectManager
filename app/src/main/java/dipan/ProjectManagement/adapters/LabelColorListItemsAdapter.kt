package dipan.ProjectManagement.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dipan.ProjectManagement.databinding.ItemLabelColorBinding

open class LabelColorListItemsAdapter
    (private val context: Context,
     private val list: ArrayList<String>, //these are the color codes
     private val mSelectedColor: String):

    RecyclerView.Adapter<LabelColorListItemsAdapter.MainViewHolder>() {

    var onClickListener: LabelColorListItemsAdapter.onClickInterface?=null




    inner class MainViewHolder(val binding: ItemLabelColorBinding): RecyclerView.ViewHolder(binding.root){
        //bind the data to the card
        fun bindItem(color: String){
            binding.viewColorMain?.setBackgroundColor(Color.parseColor(color))
            binding.ivSelectedColor.setBackgroundColor(Color.parseColor(color)) //the yes button

            //if the color is selected, show the yes button
            if(color==mSelectedColor) {
                binding.ivSelectedColor.visibility = android.view.View.VISIBLE
            }else{
                binding.ivSelectedColor.visibility = android.view.View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val itemBinding = ItemLabelColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val color=list[position]
        holder.bindItem(color)

        holder.itemView.setOnClickListener{
            //whatever you want to do when clicked
            if(onClickListener!=null){
                onClickListener!!.onClick(position,color)
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
        fun onClick(position: Int, color: String)
    }
}