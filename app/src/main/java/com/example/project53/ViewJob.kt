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
import java.util.Date
import java.text.SimpleDateFormat


class ViewJob : Activity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mViewJob_User: TextView
    private lateinit var mAcceptJobButton: Button
    private lateinit var mViewJob_DateView: TextView
    private lateinit var mViewJob_TimeView: TextView
    private lateinit var mViewJob_PayoutView: TextView
    private lateinit var mViewJob_MinPayoutView: TextView
    private lateinit var mViewJob_DescriptionView: TextView

    private var username: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createjob)

        mViewJob_User = findViewById(R.id.viewjob_username)
        mAcceptJobButton = findViewById(R.id.viewjob_button)
        mViewJob_DateView = findViewById(R.id.viewjob_editTextDate)
        mViewJob_TimeView = findViewById(R.id.viewjob_editTextTime)
        mViewJob_PayoutView = findViewById(R.id.viewjob_editTextPayout)
        mViewJob_DescriptionView = findViewById(R.id.viewjob_editTextDescription)

        mDatabase = FirebaseDatabase.getInstance().getReference("Jobs")

        username = intent.getStringExtra("username")
        Log.i(TAG, "username = $username")
        mViewJob_User.text = username

    }

    fun buttonClick(view: View){

        //TODO - IMPLEMENT acceptJob()

    }

    fun acceptJob(){

        //TODO - edit job in "Jobs" database to show isStarted = true
        //TODO - edit job in "Jobs" database to show tasker = username of <person who accepted>

        //TODO - edit user record in "Users" databse to show tasksAccepted includes this job ID

    }



    companion object{
        val TAG = "main"
    }
}