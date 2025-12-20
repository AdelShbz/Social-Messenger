package com.example.socialmessenger


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmessenger.databinding.ItemCreateGroupBinding
import androidx.core.view.isInvisible

class CreateGroupAdapter(
    private val items: List<ContactList>,
    private val activity: AppCompatActivity):
    RecyclerView.Adapter<CreateGroupAdapter.CreateGroupHolder>(){
    var selectedItems = mutableListOf<ContactList>()
    class CreateGroupHolder(private val binding: ItemCreateGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(textItem:String,selectContact:(isVisible: Boolean)->Unit){
            binding.tvCreateGroup.text = textItem
            binding.root.setOnClickListener {
                if(binding.imageView.isInvisible){
                    binding.imageView.visibility = View.VISIBLE
                    selectContact(false)
                }else{
                    binding.imageView.visibility = View.INVISIBLE
                    selectContact(true)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateGroupHolder {
        val binding = ItemCreateGroupBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return CreateGroupHolder(binding)
    }

    override fun onBindViewHolder(holder: CreateGroupHolder, position: Int) {
        val username = items[position].username
        holder.bind(username,fun(isVisible: Boolean){
            if(isVisible == true){
                // remove from list
                var selectIndex = 0
                selectedItems.forEachIndexed { index , selectedItem ->
                    if(selectedItem._id == items[position]._id){
                        selectIndex = index
                        return@forEachIndexed
                    }
                }
                selectedItems.removeAt(selectIndex)
            }else{
                // add to list
                selectedItems.add(items[position])
            }
//            Log.d("test", selectedItems.toString())
        })
    }

    override fun getItemCount(): Int = items.size



}