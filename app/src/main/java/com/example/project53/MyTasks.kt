package com.example.project53

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*


class MyTasks : Activity() {

    private var numJobs = 0
    private lateinit var username: String
    private lateinit var useremail: String
    private lateinit var mZip: String


    internal lateinit var listView: ListView

    internal lateinit var jobsCreated: MutableList<String>
    internal lateinit var description: MutableList<String>
    internal lateinit var createdBy: MutableList<String>
    internal lateinit var dueDate: MutableList<String>
    internal lateinit var location: MutableList<String>
    internal lateinit var dollar: MutableList<String>

    var job : Job? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myjobs)
        listView = findViewById<ListView>(R.id.myjobs_listview)


        username = intent.getStringExtra("username").toString()
        useremail = intent.getStringExtra("email").toString()

        // TODO - IMPLEMENT THIS CLASS
        // TODO   Search all jobs currently in DB and display all jobs created by the current user

        setTitle("My Tasks ($username)");

    }
    override fun onStart() {
        super.onStart()

        jobsCreated = mutableListOf()
        description  = mutableListOf()
        createdBy = mutableListOf()
        dueDate = mutableListOf()
        location = mutableListOf()
        dollar = mutableListOf()

        //TODO - When displaying an individual job with ViewJob, send an intent containing username
        //TODO   as "username" StringExtra and job ID as "jid" LongExtra

        var mJobs = FirebaseDatabase.getInstance().getReference("Jobs")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jid: String = ""


                for (postSnap in snapshot.children) {

                    try {
                        job = postSnap.getValue(Job::class.java)

                        Log.d("myjobs", "jobs?" + job!!.jid)
                    } catch (e: Exception) {
                        Log.e("myjobs", e.toString())
                    } finally {

                        if(username == job!!.tasker) {
                            numJobs++

                            jobsCreated!!.add(job!!.jid)
                            description!!.add(job!!.description)
                            dollar!!.add(job!!.payout.toString())
                            dueDate!!.add(job!!.date.toString())
                            location!!.add(job!!.zip.toString())
                            createdBy!!.add(job!!.creator)
                        }

                    }
                }
                Log.d("myjobs", "jobsCreated?" + jobsCreated + job)
                val mAdapter = MyJobsAdapter(this@MyTasks, jobsCreated!!,description!!,dollar!!,dueDate!!,location!!,createdBy!!)
                listView.adapter = mAdapter;



            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        mJobs.addValueEventListener(postListener)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, item, _ ->


            val ref = mJobs.child(jobsCreated[item].toString())
            var job: Job? = null
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    job = dataSnapshot.getValue(Job::class.java)
                    Log.i(ViewJob.TAG, "The read succeeded: " + job.toString())

                    val intent = Intent(this@MyTasks, ViewJob::class.java)


                    intent.putExtra("jid", jobsCreated[item])
                    intent.putExtra("username", username)
                    intent.putExtra("email", useremail)
                    intent.putExtra("zip", job!!.zip)

                    startActivity(intent)

                }
                override fun onCancelled(error: DatabaseError) {}
            })




        }

/*

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
        listView.adapter = mAdapter; */
    }


}