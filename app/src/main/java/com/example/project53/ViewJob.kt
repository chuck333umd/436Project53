package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.google.firebase.database.*


// TODO - Implement a way for the view to show a different button depending on the case:
// TODO   Case 1 - Job is not currently assigned to any tasker (shows ACCEPT JOB button)
// TODO   Case 2 - Job is currently assigned to a tasker and is being viewed by the tasker (shows IN PROGRESS notation as well as QUIT JOB button)
// TODO   Case 3 - Job is currently assigned to a tasker and is being viewed by the creator (shows IN PROGRESS notation as well as FIRE TASKER button)
// TODO   Case 4 - Job is completed (shows COMPLETED notation)

// TODO - Implement functionality for the above listed cases.

class ViewJob : Activity() {

    private lateinit var mJobs: DatabaseReference
    private lateinit var mViewJob_Creator: TextView
    private lateinit var mViewJob_Tasker: TextView
    private lateinit var mAcceptJobButton: Button
    private lateinit var mViewJob_DateView: TextView
    private lateinit var mViewJob_TimeView: TextView
    private lateinit var mViewJob_PayoutView: TextView
    private lateinit var mViewJob_MinPayoutView: TextView
    private lateinit var mViewJob_DescriptionView: TextView
    private lateinit var mViewJob_CompletedCheckBox: CheckBox
    private lateinit var mViewJob_StartedCheckBox: CheckBox


    private var username: String? = null
    private var jid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewjob)

        mViewJob_Creator = findViewById(R.id.viewjob_username)
        mViewJob_Tasker = findViewById(R.id.viewjob_taskername)
        mAcceptJobButton = findViewById(R.id.viewjob_acceptbutton)
        mViewJob_DateView = findViewById(R.id.viewjob_editTextDate)
        mViewJob_TimeView = findViewById(R.id.viewjob_editTextTime)
        mViewJob_PayoutView = findViewById(R.id.viewjob_editTextPayout)
        mViewJob_DescriptionView = findViewById(R.id.viewjob_editTextDescription)
        mViewJob_CompletedCheckBox = findViewById(R.id.viewjob_completedCheckBox)
        mViewJob_StartedCheckBox = findViewById(R.id.viewjob_startedCheckBox)


        mJobs = FirebaseDatabase.getInstance().getReference("Jobs")

        username = intent.getStringExtra("username")
        jid = intent.getStringExtra("jid")

        Log.i(TAG, "ViewJob()")
        Log.i(TAG, "username = $username")
        Log.i(TAG, "jid = $jid")



        // TODO - retrieve job from database using jid and populate TextViews


        val ref = mJobs.child(jid.toString())
        var job: Job? = null

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                job = dataSnapshot.getValue(Job::class.java)
                Log.i(TAG, "The read succeeded: " + job.toString())
                mViewJob_Creator.text = job!!.creator
                mViewJob_Tasker.text = job!!.tasker
                mViewJob_DateView.text = job!!.date.toString()
                // mViewJob_TimeView.text = job!!.date.toString()
                mViewJob_PayoutView.text = job!!.payout.toString()
                mViewJob_CompletedCheckBox.isChecked = job!!.isDone
                mViewJob_StartedCheckBox.isChecked = job!!.isStarted
                mViewJob_DescriptionView.text = job!!.description
                /*
                for (childSnapshot in dataSnapshot.children) {
                    val user = childSnapshot.getValue(Job::class.java)
                }

                 */
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i(TAG, "The read failed: " + databaseError.code)
            }
        })

        Log.i(TAG, "job = $job")
        if (job != null) {
            /*
            mViewJob_Creator.text = job!!.creator
            mViewJob_Tasker.text = job!!.tasker
            mViewJob_DateView.text = job!!.date.toString()
            // mViewJob_TimeView.text = job!!.date.toString()
            mViewJob_PayoutView.text = job!!.payout.toString()
            mViewJob_CompletedCheckBox.isChecked = job!!.isDone
            mViewJob_StartedCheckBox.isChecked = job!!.isStarted
            */



        }else{
            Log.i(TAG, "job = $job")
        }
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