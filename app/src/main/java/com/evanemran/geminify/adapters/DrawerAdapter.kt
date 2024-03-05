package com.evanemran.geminify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evanemran.geminify.R
import com.evanemran.geminify.listeners.ClickListener
import com.evanemran.geminify.model.DrawerMenu

class DrawerAdapter (private val context: Context, private val list: List<DrawerMenu>, private val listener: ClickListener<DrawerMenu>, private val selectedNavMenu: DrawerMenu)
    : RecyclerView.Adapter<DrawerViewHolder>(){

    private var selectedPos = getSelectedPos()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.list_drawer, parent, false)
        return DrawerViewHolder(layout)
    }

    override fun onBindViewHolder(holder: DrawerViewHolder, position: Int) {
        val item = list[holder.adapterPosition]

        holder.textView_title.text = item.title
        holder.textView_subTitle.text = item.subTitle

        holder.drawer_container.setBackgroundColor(
                if (selectedPos == holder.adapterPosition) context.getColor(R.color.primary)
                else context.getColor(R.color.black)
                )


        holder.drawer_container.setOnClickListener {
            if(selectedNavMenu==item) {
                holder.drawer_container.setBackgroundColor(context.getColor(R.color.primary))
            }
            else {
                holder.drawer_container.setBackgroundColor(context.getColor(R.color.black))
            }

            listener.onClicked(item)
            notifyItemChanged(selectedPos)
            selectedPos = holder.adapterPosition
            notifyItemChanged(selectedPos)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getSelectedPos(): Int {
        list.forEachIndexed { index, element ->
            if (element.title == selectedNavMenu.title){
                selectedPos = index
            }
        }

        return selectedPos

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