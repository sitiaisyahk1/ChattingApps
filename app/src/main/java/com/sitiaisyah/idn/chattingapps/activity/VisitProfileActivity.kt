package com.sitiaisyah.idn.chattingapps.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sitiaisyah.idn.chattingapps.R
import com.sitiaisyah.idn.chattingapps.model.Users
import kotlinx.android.synthetic.main.activity_visit_profile.*

class VisitProfileActivity : AppCompatActivity() {

    private var userVisitId: String? = ""
    var user: Users? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_profile)

        userVisitId = intent.getStringExtra("visit_id")

        val ref = FirebaseDatabase.getInstance().reference.child(userVisitId!!)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(Users::class.java)
                tv_username_visit.text = user!!.getUserName()
                Glide.with(this@VisitProfileActivity).load(user!!.getProfile()).into(iv_profile_visit)
                Glide.with(this@VisitProfileActivity).load(user!!.getCover()).into(iv_cover_visit)
            }

        })
        iv_fb_visit.setOnClickListener {
            val uri = Uri.parse(user!!.getFacebook())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        iv_instagram_visit.setOnClickListener {
            val uri = Uri.parse(user!!.getInstagram())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        iv_website_visit.setOnClickListener {
            val uri = Uri.parse(user!!.getWebsite())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        btn_send_message_visit.setOnClickListener {
            val intent = Intent(this, MessageChatActivity::class.java)
            intent.putExtra("visit_id", user!!.getUID())
            startActivity(intent)
        }


    }
}