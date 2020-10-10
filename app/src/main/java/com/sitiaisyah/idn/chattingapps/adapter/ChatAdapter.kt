package com.sitiaisyah.idn.chattingapps.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.sitiaisyah.idn.chattingapps.R
import com.sitiaisyah.idn.chattingapps.activity.ViewFullImageActivity
import com.sitiaisyah.idn.chattingapps.model.Chat
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(mContext: Context, mChatList: List<Chat>, imageUrl: String) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder?>() {

    private val mContext: Context
    private val mChatList: List<Chat>
    private val imageUrl: String
    val firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mContext = mContext
        this.mChatList = mChatList
        this.imageUrl = imageUrl
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        return if (viewType == 1) {
            val view: View = LayoutInflater.from(mContext)
                .inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(mContext)
                .inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        val chat = mChatList[position]
        Glide.with(mContext).load(imageUrl).into(holder.imageProfile!!)
        if (chat.getMessage().equals("sent an image") && chat.getUrl().equals("")) {
            //right
            if (chat.getSender().equals(firebaseUser!!.uid)) {
                holder.textMessageShow!!.visibility = View.GONE
                holder.rightImage!!.visibility = View.GONE
                Glide.with(mContext).load(chat.getUrl()).into(holder.rightImage!!)
                holder.rightImage!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Full View",
                        "Delete Image",
                        "Cancel"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want ?")

                    builder.setItems(options, DialogInterface.OnClickListener { dialog, wich ->
                        if (wich == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getUrl())
                            mContext.startActivity(intent)

                        } else if (wich == 1) {
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }
            //left
            else if (chat.getSender().equals(firebaseUser!!.uid))
                holder.textMessageShow!!.visibility = View.GONE
            holder.leftImage!!.visibility = View.GONE
            Glide.with(mContext).load(chat.getUrl()).into(holder.leftImage!!)
            holder.leftImage!!.setOnClickListener {
                val options = arrayOf<CharSequence>(
                    "Full View",
                    "Cancel"
                )
                var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                builder.setTitle("What do you want ?")
                builder.setItems(options, DialogInterface.OnClickListener { dialog, wich ->
                    if (wich == 0) {
                        val intent = Intent(mContext, ViewFullImageActivity::class.java)
                        intent.putExtra("url", chat.getUrl())
                        mContext.startActivity(intent)
                    }
                })
                builder.show()
            }

        }
        //text
        else {
            holder.textMessageShow!!.text = chat.getMessage()
            if (firebaseUser!!.uid == chat.getSender()) {
                holder.textMessageShow!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Delete Message",
                        "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want ?")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, wich ->
                        if (wich == 0) {
                            deleteSentMessage(position, holder)
                        }
                    })
                    builder.show()
                }
            }
        }
        //sent
        if (position == mChatList.size - 1) {
            if (chat.isSeen()) {
                holder.textSeen!!.text = "Seen"
                if (chat.getMessage().equals("sent you image") && !chat
                        .getUrl().equals("")
                ) {
                    val lp: RelativeLayout.LayoutParams? =
                        holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            }else {
                holder.textSeen!!.text = "Sent"
                if (chat.getMessage().equals("sent you image")&& !chat.getUrl().equals("")){
                    val lp: RelativeLayout.LayoutParams? =
                        holder.textSeen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.textSeen!!.layoutParams = lp
                }
            }
        }else {
            holder.textSeen!!.visibility = View.GONE
        }

    }

    private fun deleteSentMessage(position: Int, holder: ViewHolder) {
        val reff = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).getMessageId()!!)
            .removeValue()
            .addOnCompleteListener {
                task->
                if(task.isSuccessful) {
                    Toast.makeText(holder.itemView.context, "Deleted",
                    Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(holder.itemView.context, "Failed Not Deleted !",
                    Toast.LENGTH_SHORT).show()
                }
            }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageProfile: CircleImageView? = null
        var textMessageShow: TextView? = null
        var leftImage: ImageView? = null
        var rightImage: ImageView? = null
        var textSeen: TextView? = null

        init {
            imageProfile = itemView.findViewById(R.id.iv_profile_chat)
            textMessageShow = itemView.findViewById(R.id.tv_show_message)
            leftImage = itemView.findViewById(R.id.iv_image_left)
            rightImage = itemView.findViewById(R.id.iv_image_right)
            textSeen = itemView.findViewById(R.id.tv_seen)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if(mChatList[position].getSender().equals(firebaseUser!!.uid)){
            1
        }else {
            0
        }
    }
}