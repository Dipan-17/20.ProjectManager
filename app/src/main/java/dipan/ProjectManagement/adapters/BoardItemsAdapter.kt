package dipan.ProjectManagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ItemBoardBinding
import dipan.ProjectManagement.models.Board

open class BoardItemsAdapter(private val context: Context,
                        private val list: ArrayList<Board>):
    RecyclerView.Adapter<BoardItemsAdapter.MainViewHolder>() {
    inner class MainViewHolder(val itemBinding:ItemBoardBinding):RecyclerView.ViewHolder(itemBinding.root){
        //bind the data to the card
        fun bindItem(model:Board){
            itemBinding.tvBoardName.text =model.name
            itemBinding.tvCreatedBy.text =model.createdBy
            Glide.with(context)
                .load(model.image)
                .fitCenter()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(itemBinding.ivBoardImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val model=list[position]
        holder.bindItem(model)
    }

    }