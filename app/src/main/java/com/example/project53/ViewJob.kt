package com.example.project53

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.database.*
import java.util.*


// TODO (DONE) - Implement a way for the view to show a different button depending on the case:
// TODO (DONE)  Case 1 - Job is not currently assigned to any tasker (shows ACCEPT JOB button)
// TODO (DONE)  Case 2 - Job is currently assigned to a tasker and is being viewed by the tasker (shows IN PROGRESS notation as well as QUIT JOB button)
// TODO (DONE)  Case 3 - Job is currently assigned to a tasker and is being viewed by the creator (shows IN PROGRESS notation as well as FIRE TASKER button)
// TODO (DONE)  Case 4 - Job is completed (shows COMPLETED notation)

// TODO - Implement functionality for the above listed cases.

class ViewJob : Activity() {

    private lateinit var mJobs: DatabaseReference
    private lateinit var mViewJob_Creator: TextView
    private lateinit var mViewJob_Tasker: TextView

    private lateinit var mAcceptJobButton: Button
    private lateinit var mQuitJobButton: Button
    private lateinit var mFireTaskerButton: Button
    private lateinit var mCompleteJobButton: Button
    private lateinit var mCancelJobButton: Button

    private lateinit var mViewJob_DateView: TextView
    private lateinit var mViewJob_ZipView: TextView
    private lateinit var mViewJob_DistView: TextView
    private lateinit var mViewJob_PayoutView: TextView
    private lateinit var mViewJob_ContactInfo: TextView
    private lateinit var mViewJob_ContactInfoTV: TextView
    private lateinit var mViewJob_DescriptionView: TextView
    private lateinit var mViewJob_CompletedCheckBox: CheckBox
    private lateinit var mViewJob_StartedCheckBox: CheckBox

    private var creator:String? = null
    private var tasker:String? = null
    private var date: Date? = null
    private var description: String? = null

    private var payout: Int? = -1
    private var started: Boolean = false
    private var done: Boolean = false;
    private var username: String? = null
    private var jid: String? = null
    private var cemail: String? = null
    private var temail: String? = null
    private var userZip: String? = null
    private var email: String? = null
    private var useremail: String? = null
    private var jobsCreated: ArrayList<String>? = null
    private var tasksAccepted: ArrayList<String>? = null

    private var creatorContactInfo: String? = null
    private var taskerContactInfo: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewjob)

        mViewJob_Creator = findViewById(R.id.viewjob_username)
        mViewJob_Tasker = findViewById(R.id.viewjob_taskername)

        mAcceptJobButton = findViewById(R.id.viewjob_acceptbutton)
        mQuitJobButton = findViewById(R.id.viewjob_quitbutton)
        mFireTaskerButton = findViewById(R.id.viewjob_firebutton)
        mCompleteJobButton = findViewById(R.id.viewjob_completejob)
        mCancelJobButton = findViewById(R.id.viewjob_cancelbutton)

        mViewJob_DateView = findViewById(R.id.viewjob_editTextDate)
        mViewJob_ZipView = findViewById(R.id.viewjob_location)
        mViewJob_DistView = findViewById(R.id.viewjob_distance)
        mViewJob_PayoutView = findViewById(R.id.viewjob_editTextPayout)
        mViewJob_ContactInfo = findViewById(R.id.viewjob_contactInfo)
        mViewJob_ContactInfoTV = findViewById(R.id.viewjob_contactInfoTV)
        mViewJob_DescriptionView = findViewById(R.id.viewjob_editTextDescription)
        mViewJob_CompletedCheckBox = findViewById(R.id.viewjob_completedCheckBox)
        mViewJob_StartedCheckBox = findViewById(R.id.viewjob_startedCheckBox)


        mJobs = FirebaseDatabase.getInstance().getReference("Jobs")

        username = intent.getStringExtra("username")
        jid = intent.getStringExtra("jid")
        userZip = intent.getStringExtra("zip")
        useremail = intent.getStringExtra("email")

        Log.i(TAG, "ViewJob()")
        Log.i(TAG, "username = $username")
        Log.i(TAG, "jid = $jid")
        Log.i(TAG, "useremail = $useremail")



        setTitle("View Job #"+ jid?.substring(0,4)+" by: $username");

        // TODO - retrieve job from database using jid and populate TextViews


        val ref = mJobs.child(jid.toString())
        var job: Job? = null

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                job = dataSnapshot.getValue(Job::class.java)
                Log.i(TAG, "The read succeeded: " + job.toString())
                mViewJob_Creator.text = job!!.creator
                mViewJob_Tasker.text = job!!.tasker
                mViewJob_DateView.text = job!!.date.toString()
                creatorContactInfo = job!!.cemail.toString()
                taskerContactInfo = job!!.temail.toString()
                mViewJob_PayoutView.text = job!!.payout.toString()
                mViewJob_CompletedCheckBox.isChecked = job!!.isDone
                mViewJob_StartedCheckBox.isChecked = job!!.isStarted
                mViewJob_DescriptionView.text = job!!.description
                mViewJob_ZipView.text = job!!.zip
                mViewJob_DistView.text = getDistance(job!!.zip, userZip.toString())

                creator = job!!.creator
                date = job!!.date
                description = job!!.description
                tasker = job!!.tasker
                payout = job!!.payout
                done = job!!.isDone
                started = job!!.isStarted
                cemail = job!!.cemail
                temail = job!!.temail

                creatorContactInfo = job!!.temail
                taskerContactInfo = job!!.temail

                Log.d(TAG, "job = snapshot.getValue(Job::class.java)")
                Log.d(TAG, "creator = $creator")
                Log.d(TAG, "tasker = $tasker")
                Log.d(TAG, "date = $date")
                Log.d(TAG, "cemail = $cemail")
                Log.d(TAG, "temail = $temail")

                /** ALL FUNCTIONS TO BE CALLED MUST BE CALLED FROM HERE IF THEY RELY ON THE DATA
                 * PULLED FROM THE DATABASE. THERE IS A DELAY!
                 */

                setupViewButtons()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i(TAG, "The read failed: " + databaseError.code)
            }
        })
    }

    override fun onStart() {
        super.onStart()

    }

    private fun getDistance(zip1: String, zip2: String): String{

        var ret = DistFromZip().getDist(zip1, zip2)

        when (ret){
            -1F ->  Toast.makeText(this, "API Error", Toast.LENGTH_SHORT).show()
            -2F ->  Toast.makeText(this, "ZIP Bad", Toast.LENGTH_SHORT).show()
            -3F ->  Toast.makeText(this, "ZIP2 Bad", Toast.LENGTH_SHORT).show()
        }


        return ret.toString()
    }

    private fun setupViewButtons(){

        //TODO - Hide/Show buttons depending on which case in above todo ^^^

        /** You did not create the job and it is up for grabs
          *  Your options are - accept the job*/

        mViewJob_ContactInfo.isVisible = false
        mViewJob_ContactInfoTV.isVisible = false

        if (username == mViewJob_Tasker.text.toString()){
            Log.i("main", " useremail = $cemail")
            mViewJob_ContactInfo.text = "User: "+ creator + ": " + cemail
            mViewJob_ContactInfo.isVisible = true
            mViewJob_ContactInfoTV.isVisible = true
        }

        if (username == mViewJob_Creator.text.toString() && mViewJob_StartedCheckBox.isChecked){
            Log.i("main", " useremail = $temail")
            mViewJob_ContactInfo.text = "User: "+ tasker + ": " + temail
            mViewJob_ContactInfo.isVisible = true
            mViewJob_ContactInfoTV.isVisible = true
        }


        if (mViewJob_Creator.text.toString() == ""){
            Toast.makeText(this, "Task Creator = NULL... ERROR!", Toast.LENGTH_SHORT).show()

        }
        if (username != mViewJob_Creator.text.toString() && !mViewJob_StartedCheckBox.isChecked){

            Log.i(TAG, "3)mViewJob_Creator.text = " + mViewJob_Creator.text.toString())
            Log.i(TAG, " username != mViewJob_Creator.text && mViewJob_StartedCheckBox.isChecked != true")

            mAcceptJobButton.isVisible = true
            mAcceptJobButton.isClickable = true

            mFireTaskerButton.isVisible = false
            mFireTaskerButton.isClickable= false

            mCompleteJobButton.isVisible = false
            mCompleteJobButton.isClickable= false

            mCancelJobButton.isVisible = false
            mCancelJobButton.isClickable= false

            mQuitJobButton.isVisible = false
            mQuitJobButton.isClickable= false
        }
        /** You are the tasker and you have accepted the job
         *  Your options are - quit the job*/
        if (username == mViewJob_Tasker.text && mViewJob_StartedCheckBox.isChecked == true){
            mAcceptJobButton.isVisible = false
            mAcceptJobButton.isClickable = false

            mFireTaskerButton.isVisible = false
            mFireTaskerButton.isClickable= false

            mCompleteJobButton.isVisible = false
            mCompleteJobButton.isClickable= false

            mCancelJobButton.isVisible = false
            mCancelJobButton.isClickable= false

            mQuitJobButton.isVisible = true
            mQuitJobButton.isClickable= true
        }
        /** You created the job and it is already assigned to someone
         *  Your options are - fire the tasker*/
        if (username == mViewJob_Creator.text && mViewJob_StartedCheckBox.isChecked == true){
            Log.i(TAG, "username == mViewJob_Creator.text && mViewJob_StartedCheckBox.isChecked == true")

            mAcceptJobButton.isVisible = false
            mAcceptJobButton.isClickable = false

            mFireTaskerButton.isVisible = true
            mFireTaskerButton.isClickable= true

            mCompleteJobButton.isVisible = true
            mCompleteJobButton.isClickable= true

            mCancelJobButton.isVisible = false
            mCancelJobButton.isClickable= false

            mQuitJobButton.isVisible = false
            mQuitJobButton.isClickable= false
        }

        /** You created the job and it is not assigned
         * Your options are - cancel the job*/
        if (username == mViewJob_Creator.text && mViewJob_StartedCheckBox.isChecked == false){

            mAcceptJobButton.isVisible = false
            mAcceptJobButton.isClickable = false

            mFireTaskerButton.isVisible = false
            mFireTaskerButton.isClickable= false

            mCompleteJobButton.isVisible = false
            mCompleteJobButton.isClickable= false

            mCancelJobButton.isVisible = true
            mCancelJobButton.isClickable= true

            mQuitJobButton.isVisible = false
            mQuitJobButton.isClickable= false
        }

        /** You didnt create the job and its in progress and you are not the tasker
         * Your options are - NOTHING! You shouldn't be here*/
        if (username != mViewJob_Creator.text && username != mViewJob_Tasker.text && mViewJob_StartedCheckBox.isChecked == true){

            mAcceptJobButton.isVisible = false
            mAcceptJobButton.isClickable = false

            mFireTaskerButton.isVisible = false
            mFireTaskerButton.isClickable= false

            mCompleteJobButton.isVisible = false
            mCompleteJobButton.isClickable= false

            mCancelJobButton.isVisible = false
            mCancelJobButton.isClickable= false

            mQuitJobButton.isVisible = false
            mQuitJobButton.isClickable= false
        }

    }

    fun acceptJobButtonClick(view: View){

        var mUsers = FirebaseDatabase.getInstance().getReference("Jobs").child(jid!!)
        var mUsers1 = FirebaseDatabase.getInstance().getReference("Users").child(username!!)
        Log.d(TAG, "acceptJobButtonClick = $jid")
        Log.d(TAG, "creator = $creator")
        Log.d(TAG, "username = $username")
        Log.d(TAG, "cemail = $cemail")
        Log.d(TAG, "temail = $temail")

        var newJob = Job(jid!!, creator!!, cemail!!, date!!, description!!, userZip!!, payout!!,false,true, username!!, useremail!!)
        mUsers.setValue(newJob)


        //TODO - edit job in "Jobs" database to show isStarted = true
        //TODO - edit job in "Jobs" database to show tasker = username of <person who accepted>


        //TODO - edit user record in "Users" database to show tasksAccepted includes this job ID
        this.finish()
    }

    fun quitJobButtonClick(view: View){

        //TODO - edit job in "Jobs" database to show isStarted = false
        //TODO - edit job in "Jobs" database to show tasker = null
        var mUsers = FirebaseDatabase.getInstance().getReference("Jobs").child(jid!!)
        var mUsers1 = FirebaseDatabase.getInstance().getReference("Users").child(username!!)
        Log.d(TAG, "jidtest" + jid)
        var newJob = Job(jid!!, creator!!,cemail!!, date!!, description!!, userZip!!, payout!!,false,false,null, null)
        mUsers.setValue(newJob)


        this.finish()

    }

    fun fireTaskerButtonClick(view: View){

        var mUsers = FirebaseDatabase.getInstance().getReference("Jobs").child(jid!!)
        var newJob = Job(jid!!, creator!!,cemail!!, date!!, description!!, userZip!!, payout!!,false,false,null, null)
        mUsers.setValue(newJob)



        //DONE - edit job in "Jobs" database to show isStarted = false
        //DONE - edit job in "Jobs" database to show tasker = null

        //TODO - edit user record in "Users" database to show tasksAccepted excludes this job ID
        this.finish()

    }

    fun completeJobButtonClick(view: View){

        //TODO - edit job in "Jobs" database to show isDone = false
        Log.d(TAG, "completeJobButtonClick" + jid)
        var mUsers = FirebaseDatabase.getInstance().getReference("Jobs").child(jid!!)

        var newJob = Job(jid!!, creator!!, cemail!!, date!!, description!!, userZip!!, payout!!,true,true,username!!, temail!!)
        mUsers.setValue(newJob)
        this.finish()
    }
    fun cancelJobButtonClick(view: View){

        //TODO - delete from "Jobs" database
        //TODO - remove from "Users" database under your username
        var mUsers = FirebaseDatabase.getInstance().getReference("Jobs").child(jid!!)
        mUsers.removeValue()
        this.finish()

    }

    companion object{
        val TAG = "main"
    }
}