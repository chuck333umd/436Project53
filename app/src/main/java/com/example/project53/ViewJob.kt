package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


// TODO - Implement a way for the view to show a different button depending on the case:
// TODO   Case 1 - Job is not currently assigned to any tasker (shows ACCEPT JOB button)
// TODO   Case 2 - Job is currently assigned to a tasker and is being viewed by the tasker (shows IN PROGRESS notation as well as QUIT JOB button)
// TODO   Case 3 - Job is currently assigned to a tasker and is being viewed by the creator (shows IN PROGRESS notation as well as FIRE TASKER button)
// TODO   Case 4 - Job is completed (shows COMPLETED notation)

// TODO - Implement functionality for the above listed cases.

class ViewJob : Activity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mViewJob_User: TextView
    private lateinit var mViewJob_Tasker: TextView
    private lateinit var mAcceptJobButton: Button
    private lateinit var mViewJob_DateView: TextView
    private lateinit var mViewJob_TimeView: TextView
    private lateinit var mViewJob_PayoutView: TextView
    private lateinit var mViewJob_MinPayoutView: TextView
    private lateinit var mViewJob_DescriptionView: TextView

    private var username: String? = null
    private var jid: Long = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createjob)

        mViewJob_User = findViewById(R.id.viewjob_username)
        mViewJob_Tasker = findViewById(R.id.viewjob_taskername)
        mAcceptJobButton = findViewById(R.id.viewjob_acceptbutton)
        mViewJob_DateView = findViewById(R.id.viewjob_editTextDate)
        mViewJob_TimeView = findViewById(R.id.viewjob_editTextTime)
        mViewJob_PayoutView = findViewById(R.id.viewjob_editTextPayout)
        mViewJob_DescriptionView = findViewById(R.id.viewjob_editTextDescription)

        mDatabase = FirebaseDatabase.getInstance().getReference("Jobs")

        username = intent.getStringExtra("username")
        jid = intent.getLongExtra("jid", -1L)

        Log.i(TAG, "ViewJob()")
        Log.i(TAG, "username = $username")
        Log.i(TAG, "jid = $jid")

        mViewJob_User.text = username

        // TODO - retrieve job from database using jid and populate TextViews

        setupViewButtons()

    }

    private fun setupViewButtons(){
        //TODO - Hide/Show buttons depending on which case in above todo ^^^


    }

    fun acceptJobButtonClick(view: View){



        //TODO - edit job in "Jobs" database to show isStarted = true
        //TODO - edit job in "Jobs" database to show tasker = username of <person who accepted>

        //TODO - edit user record in "Users" database to show tasksAccepted includes this job ID

    }

    fun quitJobButtonClick(view: View){

        //TODO - edit job in "Jobs" database to show isStarted = false
        //TODO - edit job in "Jobs" database to show tasker = null

        //TODO - edit user record in "Users" database to show tasksAccepted excludes this job ID

    }

    fun fireTaskerButtonClick(view: View){

        //TODO - edit job in "Jobs" database to show isStarted = false
        //TODO - edit job in "Jobs" database to show tasker = null

        //TODO - edit user record in "Users" database to show tasksAccepted excludes this job ID

    }

    fun completeJobButtonClick(view: View){

        //TODO - edit job in "Jobs" database to show isDone = false

    }



    companion object{
        val TAG = "main"
    }
}