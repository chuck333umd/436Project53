package com.example.project53

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.*
import java.lang.Double.parseDouble
import java.lang.NumberFormatException
import java.util.Date
import java.text.SimpleDateFormat


class CreateJob : Activity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mCreateJobUser: TextView
    private lateinit var mCreateJobButton: Button
    private lateinit var mEditZipButton: Button
    private lateinit var mEditZipTextView: TextView
    private lateinit var mCreateJobDateView: TextView
    private lateinit var mCreateJobTimeView: TextView
    private lateinit var mCreateJobPayoutView: TextView
    private lateinit var mCreateJobMinPayoutView: TextView
    private lateinit var mCreateJobDescriptionView: TextView

    private var username: String? = null
    private var email: String? = null
    private var zip: String? = null
    private var jobsCreated: ArrayList<String>? = null
    private var tasksAccepted: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createjob)

        mCreateJobUser = findViewById(R.id.createjob_username)
        mEditZipButton = findViewById(R.id.createjob_changeZipButton)
        mCreateJobButton = findViewById(R.id.createjob_button)
        mEditZipTextView = findViewById(R.id.createjob_editZip)
        mCreateJobDateView = findViewById(R.id.createjob_editTextDate)
        mCreateJobTimeView = findViewById(R.id.createjob_editTextTime)
        mCreateJobPayoutView = findViewById(R.id.createjob_editTextPayout)
        mCreateJobMinPayoutView = findViewById(R.id.createjob_editTextMinPayout)
        mCreateJobDescriptionView = findViewById(R.id.createjob_editTextDescription)

        mDatabase = FirebaseDatabase.getInstance().getReference("Jobs")


        username = intent.getStringExtra("username")
        zip = intent.getStringExtra("zip")


        Log.i(TAG, "username = $username")
        Log.i(TAG, "testest = $username")


        mCreateJobUser.text = username
        mEditZipTextView.text = zip

    }

    override fun onStart() {
        super.onStart()
        var mUsers = FirebaseDatabase.getInstance().getReference("Users").child(username!!)

        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var userData = snapshot.getValue(User::class.java)
                email = userData!!.email
                username = userData!!.name
                jobsCreated = userData!!.jobsCreated
                tasksAccepted = userData!!.tasksAccepted


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        mUsers.addListenerForSingleValueEvent(postListener)
        Log.i(TAG, "userData = $mUsers")

    }

    fun buttonClick(view: View){
        var mUsers = FirebaseDatabase.getInstance().getReference("Users").child(username!!)

        validateFields()
        val jid = generateUniqueJobID().toString()
        Log.i(TAG, "jid = $jid")
        val duedate: Date


        val formatter = SimpleDateFormat("yyyy/MM/ddHH:mm")
        duedate = formatter.parse(mCreateJobDateView.text.toString()+mCreateJobTimeView.text.toString())
        Log.i(TAG, "duedate = $duedate")


        val job = Job(jid, username!!, duedate, mCreateJobDescriptionView.text.toString(), zip.toString(), mCreateJobPayoutView.text.toString().toInt() )
        val task = mDatabase.child(jid).setValue(job)

        Log.i(TAG, "task isComplete= " + task.isComplete)

        jobsCreated!!.add(jid)
        var newUser = User(username!!, email!!, jobsCreated!!,tasksAccepted!! )
        //TODO - add job ID to jobsCreated List in User object in "Users" database corresponding to this user
        mUsers.setValue(newUser)
        mCreateJobDateView.text = null;
        mCreateJobTimeView.text = null;
        mCreateJobPayoutView.text = null;
        mCreateJobMinPayoutView.text = null;
        mCreateJobDescriptionView.text = null;
        finish()
    }



    fun buttonClickChangeZip(view: View){

        //hideKeyboard(this)

        if (zipValidator(mEditZipTextView.text.toString())){
            zip = mEditZipTextView.text.toString()
            mEditZipTextView.setTextColor(Color.parseColor("#000000"))
            Toast.makeText(applicationContext, "Location Updated", Toast.LENGTH_SHORT).show()
        }else{
            mEditZipTextView.setTextColor(Color.parseColor("#FF0000"))
            Toast.makeText(applicationContext, "Invalid Zip", Toast.LENGTH_SHORT).show()
        }
    }

    fun zipValidator(s: String): Boolean {
        val regex = "^[0-9]{5}$".toRegex()
        return regex.containsMatchIn(s)
    }

    private fun validateFields(){

        //TODO - CREATE VALIDATOR CODE TO MAKE SURE USER CANNOT ENTER IMPROPERLY FORMATTED DATA
        //TODO - MAKE SURE PAYOUT IS IN WHOLE DOLLAR AMOUNT AND IS A VALID INT (NO CENTS)

    }

    @Suppress("UnnecessaryVariable")
    fun generateUniqueJobID(): Long{


        var randjid = (0..Long.MAX_VALUE).random()

        //TODO (OPTIONAL) - CHECK ALL "Jobs" KEYS IN DB TO MAKE SURE THIS ID IS UNIQUE

        return randjid
    }










    companion object{
        val TAG = "main"
    }
}