package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList


class MyJobs : Activity() {

    private var numJobs = 0
    private lateinit var username: String
    internal lateinit var jobsCreated: MutableList<String>
    internal lateinit var listView: ListView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myjobs)
        listView = findViewById<ListView>(R.id.mainlist_listview)
        jobsCreated = ArrayList()

        username = intent.getStringExtra("username").toString()

        // TODO - IMPLEMENT THIS CLASS
        // TODO   Search all jobs currently in DB and display all jobs created by the current user

        if (numJobs == 0) Toast.makeText(applicationContext, "No Jobs Currently", Toast.LENGTH_SHORT).show()

    }
    override fun onStart() {
        super.onStart()
        var mUsers = FirebaseDatabase.getInstance().getReference("Users").child(username!!)
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var userData = snapshot.getValue(User::class.java)
                username = userData!!.name
                jobsCreated = userData!!.jobsCreated
                val mAdapter = MyJobsAdapter(this@MyJobs, jobsCreated!!)
                listView.adapter = mAdapter;
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        mUsers.addValueEventListener(postListener)
    }
}