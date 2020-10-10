package com.sitiaisyah.idn.chattingapps.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sitiaisyah.idn.chattingapps.R

class ViewFullImageActivity : AppCompatActivity() {
    
    private var imageViewer : ImageView? = null
    private var imageUrl : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)

        imageUrl = intent.getStringExtra("url")
        imageViewer = findViewById(R.id.iv_full_image)

        Glide.with(this).load(imageUrl).into(imageViewer!!)
    }
}