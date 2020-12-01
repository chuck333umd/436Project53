package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.widget.Toast



class MyTasks : Activity() {

    private var numTasks = 0
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mytasks)

        username = intent.getStringExtra("username").toString()

        // TODO - IMPLEMENT THIS CLASS
        // TODO   Search all jobs currently in DB and display all jobs with current user as tasker


        if (numTasks == 0) Toast.makeText(applicationContext, "No Tasks Currently", Toast.LENGTH_SHORT).show()
    }
}