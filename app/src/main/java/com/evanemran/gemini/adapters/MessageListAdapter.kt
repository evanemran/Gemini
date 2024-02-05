package com.evanemran.gemini.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.evanemran.gemini.R
import com.evanemran.gemini.config.ChatType
import com.evanemran.gemini.model.MessageModel

class MessageListAdapter(private val context: Context, private val list: List<MessageModel>, private val type: ChatType)
    : RecyclerView.Adapter<RecyclerView.ViewHolder> (){

    private val OWN_VIEW = 1
    private val REPLY_VIEW = 2

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            REPLY_VIEW
        } else {
            OWN_VIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            OWN_VIEW -> {
                if(type==ChatType.TEXT) {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_message, parent, false)
                    OwnMessageViewHolder(view)
                }
                else {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_voice_message, parent, false)
                    OwnMessageViewHolder(view)
                }
            }
            REPLY_VIEW -> {
                if(type==ChatType.TEXT) {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_reply, parent, false)
                    ReplyMessageViewHolder(view)
                }
                else {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_voice_reply, parent, false)
                    ReplyMessageViewHolder(view)
                }
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        when (holder.itemViewType) {
            OWN_VIEW -> {
                holder as OwnMessageViewHolder
                holder.textViewCommand.text = item.message
                if(item.isImagePrompt) {
                    holder.imageViewPrompt.visibility = View.VISIBLE
                    holder.imageViewPrompt.setImageBitmap(item.image)
                }
            }
            REPLY_VIEW -> {
                holder as ReplyMessageViewHolder
                holder.textViewCommand.text = item.message
            }
        }
    }
}


class OwnMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textViewCommand: TextView = itemView.findViewById(R.id.textView_message)
    var imageViewPrompt: ImageView = itemView.findViewById(R.id.imageView_prompt)
    var commandContainer: LinearLayout = itemView.findViewById(R.id.command_item_container)
}

class ReplyMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textViewCommand: TextView = itemView.findViewById(R.id.textView_reply)
    var commandContainer: LinearLayout = itemView.findViewById(R.id.command_item_container)
}