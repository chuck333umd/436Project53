package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.widget.Toast



class MyTasks : Activity() {

    private var numTasks = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mytasks)

        if (numTasks == 0) Toast.makeText(applicationContext, "No Tasks Currently", Toast.LENGTH_SHORT).show()
    }
}