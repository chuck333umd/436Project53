package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.widget.Toast



class MyJobs : Activity() {

    private var numJobs = 0
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myjobs)

        username = intent.getStringExtra("username").toString()

        // TODO - IMPLEMENT THIS CLASS
        // TODO   Search all jobs currently in DB and display all jobs created by the current user

        if (numJobs == 0) Toast.makeText(applicationContext, "No Jobs Currently", Toast.LENGTH_SHORT).show()

    }
}