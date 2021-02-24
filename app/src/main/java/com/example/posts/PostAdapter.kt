package com.example.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(
    private val items : MutableList<Post>,
    private val onClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val holder = PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
        holder.root.findViewById<ImageButton>(R.id.motionItem).setOnClickListener {
            onClick(items[holder.adapterPosition])
            notifyItemChanged(holder.adapterPosition)
        }
        return holder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(items[position])

    }

    override fun getItemCount() = items.size

    inner class PostViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        val titleItem = root.findViewById<TextView>(R.id.titleItem)
        val textItem = root.findViewById<TextView>(R.id.textItem)
        val buttonItem = root.findViewById<ImageButton>(R.id.button)
        fun bind(item: Post) {
            if (!item.deleted) {
                titleItem.visibility = View.VISIBLE
                textItem.visibility = View.VISIBLE
                buttonItem.visibility = View.VISIBLE
                titleItem.text = String.format("%1\$d : %2\$s",this.adapterPosition+1, item.title)
                textItem.text = item.body
            } else {
                titleItem.visibility = View.GONE
                textItem.visibility = View.GONE
                buttonItem.visibility = View.GONE
            }
        }
    }

}