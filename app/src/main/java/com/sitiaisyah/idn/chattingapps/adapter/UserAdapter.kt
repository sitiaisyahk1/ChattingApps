package com.sitiaisyah.idn.chattingapps.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sitiaisyah.idn.chattingapps.R
import com.sitiaisyah.idn.chattingapps.activity.MessageChatActivity
import com.sitiaisyah.idn.chattingapps.activity.VisitProfileActivity
import com.sitiaisyah.idn.chattingapps.model.Chat
import com.sitiaisyah.idn.chattingapps.model.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    mContext: Context, mUsers: List<Users>,
    isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {
    private val mContext: Context
    private val mUsers: List<Users>
    private val isChatCheck: Boolean
    var lastMsg: String = ""

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view: View = LayoutInflater.from(mContext)
            .inflate(R.layout.user_search_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user: Users = mUsers[position]
        holder.userName.text = user!!.getUserName()
        Glide.with(mContext).load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profile)

        if (isChatCheck) {
            retrieveLastMessage(user.getUID(), holder.lastMessage)
        } else {
            holder.lastMessage.visibility = View.GONE
        }
        if (isChatCheck) {
            if (user.getStatus() == "online") {
                holder.onlineStatus.visibility = View.VISIBLE
                holder.offlineStatus.visibility = View.GONE
            } else {
                holder.onlineStatus.visibility = View.GONE
                holder.offlineStatus.visibility = View.VISIBLE
            }
        } else {
            holder.onlineStatus.visibility = View.GONE
            holder.offlineStatus.visibility = View.GONE
        }


        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message", "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0) {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }
                if (position == 1) {
                    val intent = Intent(mContext, VisitProfileActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }
            })
            builder.show()

        }
    }

    private fun retrieveLastMessage(chatUid: String?, lastMessage: TextView) {
        lastMsg = "defaultMsg"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshots: DataSnapshot) {
                for (dataSnapshot in snapshots.children) {
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver() == firebaseUser!!.uid &&
                            chat.getSender() == chatUid ||
                            chat.getReceiver() == chatUid &&
                            chat.getSender() == firebaseUser!!.uid
                        ) {
                            lastMsg = chat.getMessage()!!
                        }
                    }
                }

                when (lastMsg){
                    "defaultMsg" -> lastMessage.text = mContext.getString(R.string.no_message)
                    "sent you an image" -> lastMessage.text = mContext.getString(R.string.image_sent)
                    else -> lastMessage.text = lastMsg
                }
                lastMsg = "defaultMsg"
            }

        })

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView
        var profile: CircleImageView
        var onlineStatus: CircleImageView
        var offlineStatus: CircleImageView
        var lastMessage: TextView

        init {
            userName = itemView.findViewById(R.id.tv_username_search)
            profile = itemView.findViewById(R.id.iv_profile_search)
            onlineStatus = itemView.findViewById(R.id.iv_online)
            offlineStatus = itemView.findViewById(R.id.iv_offline)
            lastMessage = itemView.findViewById(R.id.tv_message_last)
        }
    }

}