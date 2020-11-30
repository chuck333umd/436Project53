package covidservices

import android.content.Intent
import android.os.Bundle
import android.os.Debug.waitForDebugger
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/** User authentication via Firebase. Takes a username, email and password and saves them in firebase
 * after validating them. Va;lidation specs are listed in Validators.kt
 *
 * The user can register and log in via the same screen. Logging in does not require the username.
 *
 * For testing purposes the fields are populated with "asd" (password is asdasd1)
 *
 * Author: Chuck Daniels
 */

class UserAuth : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var userName: EditText? = null
    private var userEmail: EditText? = null
    private var userPassword: EditText? = null
    private var loginButton: Button? = null
    private var progressBar: ProgressBar? = null
    private var regButton: Button? = null

    private var validator = Validators()

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userauth)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        userName = findViewById(R.id.userauth_username)
        userEmail = findViewById(R.id.userauth_email)
        userPassword = findViewById(R.id.userauth_password)
        loginButton = findViewById(R.id.userauth_login)
        regButton = findViewById(R.id.userauth_register)
        progressBar = findViewById(R.id.progressBar)

        loginButton!!.setOnClickListener { loginUserAccount() }
        regButton!!.setOnClickListener { registerNewUser() }
    }

    // TODO: Allow the user to log into their account
    // If the email and password are not empty, try to log in
    // If the login is successful, store info into intent and launch DashboardActivity
    private fun loginUserAccount() {

        progressBar?.visibility = View.VISIBLE

        val email: String = userEmail!!.text.toString()
        val password: String = userPassword!!.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task -> progressBar!!.visibility = View.GONE
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_LONG).show()

                Log.i("main", mAuth!!.uid!!)
                Log.i("main", "mAuth!!.currentUser!!.displayName = " + mAuth!!.currentUser!!.displayName)
                val intent = Intent(this, MainListScreen::class.java)
                intent.putExtra("userID", mAuth!!.uid)
                intent.putExtra("username", mAuth!!.currentUser!!.displayName)
                intent.putExtra("user", mAuth!!.currentUser)
                intent.putExtra("loggedIn", true)

                startActivity(intent)



            } else {
                Toast.makeText(applicationContext, "Login failed! Please try again later", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun registerNewUser() {
        progressBar!!.visibility = View.VISIBLE

        val username: String = userName!!.text.toString()
        val email: String = userEmail!!.text.toString()
        val password: String = userPassword!!.text.toString()


        if (!validator.validUsername(username)) {
            Toast.makeText(applicationContext, "Enter a valid username!", Toast.LENGTH_LONG).show()
            return
        }

        if (!validator.validEmail(email)) {
            Toast.makeText(applicationContext, "Enter a valid email!", Toast.LENGTH_LONG).show()
            return
        }
        if (!validator.validPassword(password)) {
            Toast.makeText(applicationContext, "Enter a valid password!", Toast.LENGTH_LONG).show()
            return
        }
/*
        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Log.d("main", "Authentication successful")
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "Authentication failed.",  Toast.LENGTH_SHORT).show();
                    }
                }
*/
        var task = mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
            Log.i("main", "registerNewUser: ")
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
                progressBar!!.visibility = View.GONE


                val user = mAuth!!.currentUser
                user!!.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(username).build())

                loginUserAccount()
            /*
                Log.i("main", mAuth!!.uid!!)
                Log.i("main", "mAuth!!.currentUser!!.displayName = " + mAuth!!.currentUser!!.displayName)

                val intent = Intent(this, MainListScreen::class.java)
                intent.putExtra("loggedIn", true)
                intent.putExtra("userID", mAuth!!.uid)
                intent.putExtra("username", mAuth!!.currentUser!!.displayName)
                intent.putExtra("user", user)


                startActivity(intent)*/

            } else {
                Toast.makeText(applicationContext, "Registration failed! Please try again later", Toast.LENGTH_LONG).show()
                progressBar!!.visibility = View.GONE
            }
        })



    }



}