package com.sitiaisyah.idn.chattingapps.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.sitiaisyah.idn.chattingapps.R
import com.sitiaisyah.idn.chattingapps.adapter.UserAdapter
import com.sitiaisyah.idn.chattingapps.model.ChatList
import com.sitiaisyah.idn.chattingapps.notification.Token
import com.sitiaisyah.idn.chattingapps.model.Users

class ChatFragment : Fragment() {

    private var userAdapter : UserAdapter? = null
    private var mUser : List<Users>? = null
    private var userChatList : List<ChatList>? = null
    private var firebaseUser: FirebaseUser? = null
    lateinit var recycler_chat_list : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_chat, container, false)

        recycler_chat_list = view.findViewById(R.id.rv_chat_list)
        recycler_chat_list.setHasFixedSize(true)
        recycler_chat_list.layoutManager = LinearLayoutManager (context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        // kita deklarasi klo dia array list
        userChatList = ArrayList()


        val ref = FirebaseDatabase.getInstance().reference.child("ChatList")
            .child(firebaseUser!!.uid)

        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                //
            }

            override fun onDataChange(snapshots: DataSnapshot) {
                (userChatList as ArrayList).clear()

                for (dataSnapshot in snapshots.children){
                    val chatList = dataSnapshot.getValue(ChatList::class.java)
                    (userChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

        })

        updateToken(FirebaseInstanceId.getInstance().token)

        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val firstToken =
            Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(firstToken)
    }

    private fun retrieveChatList() {
        mUser = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshots: DataSnapshot) {
                (mUser as ArrayList).clear()

                for (dataSnapshot in snapshots.children){
                    val user = dataSnapshot.getValue(Users::class.java)
                    for (chatList in userChatList!!){
                        if (user!!.getUID().equals(chatList.getId())) {
                            (mUser as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUser as ArrayList<Users>), true)
                recycler_chat_list.adapter = userAdapter
            }

        })
    }

}