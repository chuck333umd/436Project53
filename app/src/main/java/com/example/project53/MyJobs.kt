package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.widget.Toast



class MyJobs : Activity() {

    private var numJobs = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myjobs)


        if (numJobs == 0) Toast.makeText(applicationContext, "No Jobs Currently", Toast.LENGTH_SHORT).show()

    }
}