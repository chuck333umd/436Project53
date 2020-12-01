package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



class CreateJob : Activity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mButton: Button
    private lateinit var mTextOne: TextView
    private lateinit var mTextTwo: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myjobs)

        mButton = findViewById(R.id.createjob_button)
        mTextOne = findViewById(R.id.createjob_textone)
        mTextTwo = findViewById(R.id.createjob_texttwo)

        mDatabase = FirebaseDatabase.getInstance().getReference("Jobs")




    }

    fun buttonClick(view: View){
        mDatabase.child(mTextOne.text.toString()).child(mTextTwo.text.toString()).setValue("placeholder")
    }












    private fun addUser() {

        val mDatabase = FirebaseDatabase.getInstance().reference.root
        val mUsers = FirebaseDatabase.getInstance().getReference("Users")
        val mUserss = FirebaseDatabase.getInstance().getReference("authors")
        mUserss.child("User2").setValue(User("asshile", "shithead")).addOnCompleteListener(OnCompleteListener<Void?> { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Song Uploaded", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "OK")
            }
        }).addOnFailureListener(OnFailureListener { e ->
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            Log.i(TAG, "FUCK NO " + e.message)
        })




        Log.i(TAG, "mDatabase = $mDatabase ")
        Log.i(TAG, "mUsers = $mUsers ")
        var task1 = mDatabase!!.push().setValue("fuck")
        Log.i(UserAuth.TAG, "task1.isSuccessful = " + task1.isSuccessful)

        val id = (mUsers!!.push()).key.toString()
        Log.i(UserAuth.TAG, "addUser: id = $id")
        //creating an Author Object
        val user = User("dickhead", "dickhead@dicksRus.com")

        //Saving the Author
        val task = mUsers!!.child("dickhead").child(id).setValue(user)
        Log.i(UserAuth.TAG, "task.isSuccessful = " + task.isSuccessful)

        if (task.isSuccessful) Toast.makeText(this, "User added", Toast.LENGTH_LONG).show()
        else Toast.makeText(this, "User NOT added", Toast.LENGTH_LONG).show()
    }

    companion object{
        val TAG = "main"
    }
}