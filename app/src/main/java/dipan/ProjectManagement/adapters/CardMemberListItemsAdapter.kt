package dipan.ProjectManagement.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ItemCardSelectedMembersBinding
import dipan.ProjectManagement.models.SelectedMembers

open class CardMemberListItemsAdapter(
    private val context: Context,
    private val list: ArrayList<SelectedMembers>,
    private val assignMembers: Boolean
): RecyclerView.Adapter<CardMemberListItemsAdapter.MainViewHolder>() {

    private var listener: OnItemClickListener? = null



    inner class MainViewHolder(val itemBinding: ItemCardSelectedMembersBinding): RecyclerView.ViewHolder(itemBinding.root){
        //bind the data to the card
        fun bindItem(model:SelectedMembers) {
            try {
                Glide
                    .with(context)
                    .load(model.image)
                    .fitCenter()
                    .placeholder(R.drawable.ic_person_placeholder_black)
                    .into(itemBinding.civSelectedMemberImage)
            } catch (e: Exception) {
                Log.e("GlideError", "Error inflating member image")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ItemCardSelectedMembersBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val model= list[position]

        //assign members variable is used cause we are using the same adapter for both assign members and members list
        //members list -> in task list inside individual card -> we dont want the plus to show
        //assign members -> in card details activity -> we want the plus to show
        if(position == list.size-1 && assignMembers){//this is the last item -> empty item -> the + icon
            holder.itemBinding.civSelectedMemberImage.visibility = android.view.View.GONE
            holder.itemBinding.civAddMember.visibility = android.view.View.VISIBLE
        }else{
            holder.itemBinding.civSelectedMemberImage.visibility = android.view.View.VISIBLE
            holder.itemBinding.civAddMember.visibility = android.view.View.GONE
        }

        holder.bindItem(model)


        holder.itemView.setOnClickListener {
            listener?.onItemClick()
        }
    }

    interface OnItemClickListener {
        fun onItemClick()
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}