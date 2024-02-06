package com.evanemran.gemini.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evanemran.gemini.R
import com.evanemran.gemini.listeners.ClickListener
import com.evanemran.gemini.model.DrawerMenu

class DrawerAdapter (private val context: Context, private val list: List<DrawerMenu>, private val listener: ClickListener<DrawerMenu>, private val selectedNavMenu: DrawerMenu)
    : RecyclerView.Adapter<DrawerViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.list_drawer, parent, false)
        return DrawerViewHolder(layout)
    }

    override fun onBindViewHolder(holder: DrawerViewHolder, position: Int) {
        val item = list[holder.adapterPosition]

        holder.textView_title.text = item.title
        holder.textView_subTitle.text = item.subTitle

        if(selectedNavMenu==item) {
            holder.drawer_container.setBackgroundColor(context.getColor(R.color.primary))
        }
        else {
            holder.drawer_container.setBackgroundColor(context.getColor(R.color.black))
        }


        holder.drawer_container.setOnClickListener {
            if(selectedNavMenu==item) {
                holder.drawer_container.setBackgroundColor(context.getColor(R.color.primary))
            }
            else {
                holder.drawer_container.setBackgroundColor(context.getColor(R.color.black))
                listener.onClicked(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

class DrawerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textView_title: TextView
    var textView_subTitle: TextView
    var drawer_container: LinearLayout

    init {
        textView_title = itemView.findViewById(R.id.textView_title)
        textView_subTitle = itemView.findViewById(R.id.textView_subTitle)
        drawer_container = itemView.findViewById(R.id.drawer_container)
    }
}