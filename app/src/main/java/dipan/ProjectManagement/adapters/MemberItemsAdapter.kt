package dipan.ProjectManagement.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dipan.ProjectManagement.R
import dipan.ProjectManagement.databinding.ItemMembersBinding
import dipan.ProjectManagement.models.User


open class MemberItemsAdapter(private val context: Context,
                              private val list: ArrayList<User>):
    RecyclerView.Adapter<MemberItemsAdapter.MainViewHolder>() {


    private var onClickListener: onClickInterface?=null

    inner class MainViewHolder(val itemBinding:ItemMembersBinding):RecyclerView.ViewHolder(itemBinding.root){
        //bind the data to the card
        fun bindItem(model:User){
           itemBinding?.tvMemberName?.text=model.name
            itemBinding?.tvMemberEmail?.text=model.email
            try{
                Glide
                    .with(context)
                    .load(model.image)
                    .fitCenter()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(itemBinding?.ivMemberImage!!)
            }catch(e:Exception){
                Log.e("GlideError","Error inflating member image")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ItemMembersBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val model=list[position]
        holder.bindItem(model)

        holder.itemView.setOnClickListener{
            //whatever you want to do when clicked
            if(onClickListener!=null){
                onClickListener!!.onClick(position,model)
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
        fun onClick(position: Int, model: User)
    }


}