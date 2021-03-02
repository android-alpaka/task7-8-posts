package com.example.posts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class PostAdapter(
    val items: MutableList<Post>,
    private val onClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val holder = PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
        holder.root.findViewById<Button>(R.id.delete).setOnClickListener {
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
        private val titleItem = root.findViewById<TextView>(R.id.titleItem)
        private val textItem = root.findViewById<TextView>(R.id.textItem)
        private val buttonItem = root.findViewById<MaterialButton>(R.id.delete)

        fun bind(item: Post) {
            if (!item.deleted) {
                titleItem.visibility = View.VISIBLE
                textItem.visibility = View.VISIBLE
                buttonItem.visibility = View.VISIBLE
                titleItem.text =
                    String.format("%1\$d : %2\$s", this.adapterPosition + 1, item.title)
                textItem.text = item.body
            } else {
                titleItem.visibility = View.GONE
                textItem.visibility = View.GONE
                buttonItem.visibility = View.GONE
            }
        }
    }

}