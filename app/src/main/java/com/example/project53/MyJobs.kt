package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*


class MyJobs : Activity() {

    private var numJobs = 0
    private lateinit var username: String
    internal lateinit var jobsCreated: MutableList<String>
    internal lateinit var listView: ListView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainlist)
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
        var mUsers1 = FirebaseDatabase.getInstance().getReference("Jobs")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var userData = snapshot.getValue(User::class.java)
                username = userData!!.name
                jobsCreated = userData!!.jobsCreated

                val mAdapter = MyJobsAdapter(this@MyJobs, jobsCreated!!,username!!)
                listView.adapter = mAdapter;
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        mUsers.addValueEventListener(postListener)/*
        var dueDate: MutableList<String>? = ArrayList()
        var payout: MutableList<String>? = ArrayList()
        var location: MutableList<String>? = ArrayList()
        var job: Job? = null
        val postListener1 = object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot1: DataSnapshot) {
                var i = 0;

                for (postSnap in snapshot1.children) {

                    try {
                        job = postSnap.getValue(Job::class.java)

                    } catch (e: Exception) {
                    } finally {
                        if(job!!.jid == jobsCreated[i]){
                            dueDate!!.add(job!!.date.toString())
                            payout!!.add(job!!.payout.toString())
                            location!!.add(job!!.zip.toString())
                            i++
                        }
                    }
                }
            }
        }
        mUsers1.addValueEventListener(postListener1)
        val mAdapter = MyJobsAdapter(this@MyJobs, jobsCreated!!, location!!, dueDate!!, payout!!)
        listView.adapter = mAdapter;*/
    }
}