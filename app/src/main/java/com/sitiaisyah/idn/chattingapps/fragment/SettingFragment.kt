package com.sitiaisyah.idn.chattingapps.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.sitiaisyah.idn.chattingapps.R
import com.sitiaisyah.idn.chattingapps.model.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_setting.view.*


class SettingFragment : Fragment() {

    var userReference : DatabaseReference? = null
    var firebaseUser : FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri : Uri? = null
    private var storageRef : StorageReference? = null
    private var coverCheck : String? = ""
    private var socialMediaCheck : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        userReference!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshots: DataSnapshot) {
                if (snapshots.exists()){
                    val user: Users? = snapshots.getValue(Users::class.java)

                    if(context != null){
                        view.tv_username_setting.text = user!!.getUserName()
                        Glide.with(this@SettingFragment).load(user.getProfile()).into(view.iv_profile_setting)
                        Glide.with(this@SettingFragment).load(user.getCover()).into(view.iv_cover)
                    }
                }
            }
        })

        view.iv_profile_setting.setOnClickListener{
            pickImage()
        }

        view.iv_cover.setOnClickListener{
            coverCheck = "cover"
            pickImage()
        }

        view.iv_facebook.setOnClickListener{
            socialMediaCheck = "facebook"
            setSocialMediaLink()
        }

        view.iv_instagram.setOnClickListener{
            socialMediaCheck = "instagram"
            setSocialMediaLink()
        }

        view.iv_website.setOnClickListener{
            socialMediaCheck = "website"
            setSocialMediaLink()
        }

        return view
    }

    private fun setSocialMediaLink() {
        val builder : AlertDialog.Builder = AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        if (socialMediaCheck == "website"){
            builder.setTitle("Write URL :")
        }else{
            builder.setTitle("Write username :")
        }

        val editText = EditText(context)
        if (socialMediaCheck == "website") {
            editText.hint = " e.g.www.google.com"
        }else{
            editText.hint = "e.g sitiaisyah"
        }
        builder.setView(editText)
        builder.setPositiveButton("create", DialogInterface.OnClickListener{
            dialog, wich ->
            val textString = editText.text.toString()
            if (textString == ""){
                Toast.makeText(context, getString(R.string.write_something), Toast.LENGTH_LONG).show()
            }else{
                saveSocialMediaLin(textString)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{
            dialog, wich ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialMediaLin(textString: String) {
        val mapSocialMedia = HashMap<String, Any>()

        when(socialMediaCheck){
            "facebook" -> {
                mapSocialMedia["facebook"] = "https://m.facebook.com/$textString"
            }
            "instagram" -> {
                mapSocialMedia["instagram"] = "https://m.instagram.com/$textString"
            }
            "website" -> {
                mapSocialMedia["website"] = "https://$textString"
            }
        }

        userReference!!.updateChildren(mapSocialMedia).addOnCompleteListener{
            task ->
            if (task.isSuccessful){
                Toast.makeText(context, getString(R.string.updated), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data
            Toast.makeText(context, getString(R.string.upload), Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getString(R.string.wait_for_upload))
        progressDialog.show()

        val fileRef = storageRef!!.child(System.currentTimeMillis().toString()+"jpg")
        var uploadTask : StorageTask<*>
        uploadTask = fileRef.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if(!task.isSuccessful){
                task.exception?.let{
                    throw it
                }
            }

            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener{ task ->
            if (task.isSuccessful){
                val downloadUrl = task.result
                val url = downloadUrl.toString()

                if (coverCheck == "cover"){
                    val mapCoverImage = HashMap<String, Any>()
                    mapCoverImage["cover"] = url
                    userReference!!.updateChildren(mapCoverImage)
                    coverCheck = ""
                }else{
                    val mapProfileImage = HashMap<String, Any>()
                    mapProfileImage["profile"] = url
                    userReference!!.updateChildren(mapProfileImage)
                    coverCheck = ""
                }

                progressDialog.dismiss()
            }
        }

    }

}