package com.example.project53

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color.parseColor
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/** This screen serves as the main activity for the app. It will display a listview of jobs and UI elements
 * for the user to set his location (if the current location is not suitable) and radius (default is set to 50km)
 *
 * Currently I have implemented:
 * - populate the current location by using network / gps (or show "noloc" if no location)
 * - validate entered zip codes (checks against regex for 5 digit numeric)
 * - validate entered radius values (user can enter anything >0 and <999999999)
 *
 *
 * If the zip or radius in invalid, the textview text color will turn red and a toast will appear
 * The user must enter a correct value for it to turn back to black and update the private vars
 *
 * Currently the bottom-most textview displays username and ID but in the final build it will show
 * only the username.
 *
 * Author: Chuck Daniels
 */


// TODO - Implement displayJobsByRadius() to sort jobs by radius distance from current zipcode <mainlist_location.text>

// TODO - Add the listview <mainlist_listview> that shows all jobs within radius <mainlist_radius.text> using list_item.xml layout
// TODO   Clicking on one of the list items will open it in ViewJob.kt

// TODO MAYBE - Change list_item.xml layout so that it better fits into the main listview.

// TODO OPTIONAL - Clean up all the unnecessary crap from this activity pertaining to location,
// TODO OPTIONAL   since we do not need to update the location once we obtain the initial ZIP,
// TODO OPTIONAL   some of this can be probably be removed.

class MainListScreen : Activity() {


    private var loggedIn: Boolean = false
    private lateinit var mZipView: TextView
    private lateinit var mRadiusView: TextView
    private lateinit var mCurrentUser: TextView
    private lateinit var mCurrentUserTV: TextView
    private var mZip = ""
    private var mRadius = 5000
    private var username: String? = null
    private var useremail: String? = null



    // Current best location estimate
    private var mBestReading: Location? = null

    // Reference to the LocationManager and LocationListener
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    private var mCancelHandle: ScheduledFuture<*>? = null
    private var mIsRequestingUpdates = false
    private var mShouldResume = false
    internal lateinit var jobsCreated: MutableList<String>
    internal lateinit var description: MutableList<String>
    internal lateinit var createdBy: MutableList<String>
    internal lateinit var dueDate: MutableList<String>
    internal lateinit var location: MutableList<String>
    internal lateinit var dollar: MutableList<String>

    internal lateinit var listView: ListView

    internal lateinit var mAdapter: MainListAdapater

    var numJobs = 0

    @TargetApi(Build.VERSION_CODES.M)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.mainlist)

        mZipView = findViewById(R.id.mainlist_location)
        mRadiusView = findViewById(R.id.mainlist_radius)
        mCurrentUser = findViewById(R.id.mainlist_currentUser)
        mCurrentUserTV = findViewById(R.id.textView)
        mCurrentUser.isVisible = false
        mCurrentUserTV.isVisible = false
        listView = findViewById<ListView>(R.id.mainlist_listview)
        val logIn = intent.getBooleanExtra("loggedIn", false)
        if (logIn) {


            invalidateOptionsMenu()
            username = intent.getStringExtra("username")
            useremail = intent.getStringExtra("email")
            val userID = intent.getStringExtra("userID")
            val user = intent.getParcelableExtra<Parcelable>("user")
            loggedIn = true

            //mCurrentUser.text = username + "   ID:   " + userID
            setTitle("Jobs List - Hello $username!");
        }


        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (null != mLocationManager) {
           // Log.i(TAG, "Couldn't find the LocationManager")
            // Return a LocationListener
        }
        mLocationListener = makeLocationListener()
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQUEST_FINE_LOC_PERM_ONCREATE)
        } else {
            getAndDisplayLastKnownLocation()
            installLocationListeners()
        }


    }

    override fun onStart() {
        super.onStart()

        displayJobsByRadius()


    }

    private fun displayJobsByRadius(){

        jobsCreated = mutableListOf()
        description  = mutableListOf()
        createdBy = mutableListOf()
        dueDate = mutableListOf()
        location = mutableListOf()
        dollar = mutableListOf()

        //TODO - When displaying an individual job with ViewJob, send an intent containing username
        //TODO   as "username" StringExtra and job ID as "jid" LongExtra

        var mUsers = FirebaseDatabase.getInstance().getReference("Jobs")
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jid: String = ""
                var job : Job? = null
                jobsCreated.clear()
                description.clear()
                dollar.clear()
                dueDate.clear()
                location.clear()
                createdBy.clear()
                for (postSnap in snapshot.children) {

                    try {
                        job = postSnap.getValue(Job::class.java)

                        //Log.d(TAG, "jobs?" + job!!.jid)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    } finally {

                        val dist = DistFromZip().getDist(job!!.zip.toString(), mZip ).toInt()
                        Log.i("dfz", "jid: " + job!!.jid + ", dist: $dist")
                        if ( dist < mRadius && job!!.isStarted == false && job!!.isDone == false) {
                            numJobs++
                            Log.i("dfz", "adding jid: " + job!!.jid + ", zip: " + job!!.zip + ", dist: $dist")
                            ////Log.d(TAG, "we are getting here right?" + job)
                            jobsCreated!!.add(job!!.jid)
                            description!!.add(job!!.description)
                            dollar!!.add(job!!.payout.toString())
                            dueDate!!.add(job!!.date.toString())
                            location!!.add(job!!.zip.toString())
                            createdBy!!.add(job!!.creator)
                        }

                    }
                }
                //Log.d(TAG, "jobsCreated?" + jobsCreated + job)
                val mAdapter = MainListAdapater(this@MainListScreen, jobsCreated!!,description!!,dollar!!,dueDate!!,location!!,createdBy!!)
                listView.adapter = mAdapter;

                if (numJobs == 0) Toast.makeText(applicationContext, "No Jobs Currently", Toast.LENGTH_SHORT).show()
                //if (numJobs > 0) Toast.makeText(applicationContext, "Search Returned $numJobs Jobs", Toast.LENGTH_SHORT).show()
                numJobs = 0

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        mUsers.addValueEventListener(postListener)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, item, _ ->
            //getting the selected artist
           // val job = jobsCreated[item]


            if (!loggedIn) {
                val intentLogin = Intent(this, UserAuth::class.java)
                startActivity(intentLogin)
            }
            //creating an intent
            val intent = Intent(this, ViewJob::class.java)

            //putting artist name and id to intent
            intent.putExtra("jid", jobsCreated[item])
            intent.putExtra("username", username)
            intent.putExtra("zip", mZip)
            intent.putExtra("email", useremail)

            //starting the activity with intent
            startActivity(intent)
        }

    }

    fun locationButton(view: View){

        hideKeyboard(this)

        /** Dont bother wasting a call to Geocoder API unless it is properly formatted */
        if (!zipValidator(mZipView.text.toString())){
            Toast.makeText(this, "Location is not a ZIP!", Toast.LENGTH_SHORT).show()
            mZipView.setTextColor(parseColor("#FF0000"))
            return
        }

        var dfz =  DistFromZip().getDist(mZipView.text.toString(), 21012.toString())

        when (dfz){
            -1F ->  Toast.makeText(this, "API Error", Toast.LENGTH_SHORT).show()
            -2F ->  Toast.makeText(this, "ZIP Bad", Toast.LENGTH_SHORT).show()
            -3F ->  Toast.makeText(this, "ZIP2 Bad", Toast.LENGTH_SHORT).show()
        }


        Log.i(TAG, "validZip (per USPS): " + (dfz > -1))


        if (zipValidator(mZipView.text.toString()) && dfz > -1){
            mZip = mZipView.text.toString()
            mZipView.setTextColor(parseColor("#000000"))
            Toast.makeText(applicationContext, "Location Updated", Toast.LENGTH_SHORT).show()
        }else{
            mZipView.setTextColor(parseColor("#FF0000"))
            //Toast.makeText(applicationContext, "Bad Zip / Geocoder API Error!", Toast.LENGTH_SHORT).show()
        }
    }

    fun radiusButton(view: View){

        hideKeyboard(this)


        if (mRadiusView.text.toString().length < 9 && radiusValidator(mRadiusView.text.toString()) && mRadiusView.text.toString().toInt() > 0){
            Log.i(TAG, "radiusButton")
            mRadius = mRadiusView.text.toString().toInt()
            mRadiusView.setTextColor(parseColor("#000000"))
            Toast.makeText(applicationContext, "Radius Updated", Toast.LENGTH_SHORT).show()
            displayJobsByRadius()
        }else{
            mRadiusView.setTextColor(parseColor("#FF0000"))
            Toast.makeText(applicationContext, "Invalid Radius", Toast.LENGTH_SHORT).show()
        }
    }

    fun zipValidator(s: String): Boolean {
        val regex = "^[0-9]{5}$".toRegex()
        return regex.containsMatchIn(s)
    }

    fun radiusValidator(s: String): Boolean {

        val regex = "^[0-9]+$".toRegex()
        Log.i(TAG, "radiusValidator = " + regex.containsMatchIn(s))
        return regex.containsMatchIn(s)
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {

        //change back to finishaffinity for final build

        finish()
        //finishAffinity()
    }

    // Update display
    private fun updateDisplay(location: Location) {

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        Log.i("main", "addresses: $addresses")
        addresses.forEach { a ->
            Log.i("main", "zip: " + a.postalCode)
            mZipView.text = a.postalCode
            mZip = a.postalCode
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (loggedIn) {
            menuInflater.inflate(R.menu.loggedin, menu)
        }else{
            menuInflater.inflate(R.menu.notloggedin, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val intentMyJobs = Intent(this, MyJobs::class.java)
        val intentMyTasks = Intent(this, MyTasks::class.java)
        val intentCreateJob = Intent(this, CreateJob::class.java)
        val intentLogin = Intent(this, UserAuth::class.java)

        return when (item.itemId) {
            R.id.menu_login -> {

                startActivity(intentLogin)
                true
            }
            R.id.menu_logout -> {
                loggedIn = false 
                mCurrentUser.text = "None"
                invalidateOptionsMenu()
                true
            }
            R.id.menu_myjobs -> {

                /*/* TEST */
                val testViewJobIntent = Intent(this, ViewJob::class.java)
                testViewJobIntent.putExtra("username", username)
                testViewJobIntent.putExtra("jid", "1580387569798154325")
                testViewJobIntent.putExtra("zip", mZip)
                startActivity(testViewJobIntent)
*/
                /**CHANGE BACK TO THIS WHEN DONE TESTING: */

                val intentMyJobs = Intent(this, MyJobs::class.java)
                intentMyJobs.putExtra("username", username)
                intentMyJobs.putExtra("zip", mZip)
                intentMyJobs.putExtra("email", useremail)

                startActivity(intentMyJobs)


                true
            }
            R.id.menu_createjob -> {

                val intentCreateJob = Intent(this, CreateJob::class.java)
                intentCreateJob.putExtra("username", username)
                intentCreateJob.putExtra("zip", mZip)
                intentCreateJob.putExtra("email", useremail)

                startActivity(intentCreateJob)
                true
            }
            R.id.menu_mytasks -> {

                val intentMyTasks = Intent(this, MyTasks::class.java)
                intentMyTasks.putExtra("username", username)
                intentMyTasks.putExtra("zip", mZip)
                intentMyTasks.putExtra("email", useremail)

                startActivity(intentMyTasks)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    companion object {
        private const val ONE_MIN = 1000 * 60.toLong()
        private const val TWO_MIN = ONE_MIN * 2
        private const val MEASURE_TIME = TWO_MIN
        private const val POLLING_FREQ = 1000 * 10.toLong()
        private const val MIN_ACCURACY = 5.0f
        private const val MIN_DISTANCE = 10.0f
        private const val REQUEST_FINE_LOC_PERM_ONCREATE = 200
        private const val REQUEST_FINE_LOC_PERM_ONRESUME = 201
        private const val TAG = "main"
    }





    private fun makeLocationListener(): LocationListener {

        return object : LocationListener {
            // Called back when location changes
            override fun onLocationChanged(location: Location) {
                Log.i(TAG, "Received new location$location")


                // Determine whether new location is better than current best estimate
                if (null == mBestReading || location.accuracy <= mBestReading!!.accuracy) {

                    // Update best estimate
                    mBestReading = location

                    // Update display
                    updateDisplay(location)

                    // If location is accurate enough stop listening
                    if (mBestReading!!.accuracy < MIN_ACCURACY) {
                        mLocationManager.removeUpdates(mLocationListener)
                        Log.i(TAG, "location updates cancelled")
                        mIsRequestingUpdates = false
                    }
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                /* Not implemented */
            }

            override fun onProviderEnabled(provider: String) { /* Not implemented */
            }

            override fun onProviderDisabled(provider: String) { /* Not implemented */
            }
        }
    }


    private fun installLocationListeners() {

        // Determine whether initial reading is "good enough".
        // If not, register for further location updates
        if (null == mBestReading || mBestReading!!.accuracy > MIN_ACCURACY || mBestReading!!.time < System.currentTimeMillis() - TWO_MIN) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQUEST_FINE_LOC_PERM_ONRESUME)
            } else {
                continueInstallLocationListeners()
            }
        }
    }

    private fun continueInstallLocationListeners() {
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Register for network location updates
            if (null != mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER)) {
                //Log.i(TAG, "Network location updates requested")
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, POLLING_FREQ, MIN_DISTANCE, mLocationListener)
                mIsRequestingUpdates = true
            }

            // Register for GPS location updates
            if (null != mLocationManager.getProvider(LocationManager.GPS_PROVIDER)) {
                //Log.i(TAG, "GPS location updates requested")
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, POLLING_FREQ, MIN_DISTANCE, mLocationListener)
                mIsRequestingUpdates = true
            }

            // Schedule a runnable to unregister LocationListeners after MEASURE_TIME
            mCancelHandle = Executors.newScheduledThreadPool(1).schedule(
                    {
                        //Log.i(TAG, "location updates cancelled")
                        mLocationManager.removeUpdates(mLocationListener)
                        mIsRequestingUpdates = false
                    },
                    MEASURE_TIME,
                    TimeUnit.MILLISECONDS
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mIsRequestingUpdates && mShouldResume) {
            installLocationListeners()
        }
    }

    // Unregister LocationListeners
    override fun onPause() {
        super.onPause()
        // Stop updates
        if (mIsRequestingUpdates) {
            mLocationManager.removeUpdates(mLocationListener)
            if (null != mCancelHandle && !mCancelHandle!!.isDone) {
                mCancelHandle!!.cancel(false)
            }
            mIsRequestingUpdates = false
            mShouldResume = true
            Log.i(TAG, "Removing location updates in onPause()")
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (REQUEST_FINE_LOC_PERM_ONCREATE == requestCode) {
                getAndDisplayLastKnownLocation()
                installLocationListeners()
            } else if (REQUEST_FINE_LOC_PERM_ONRESUME == requestCode) {
                continueInstallLocationListeners()
            }
        } else {
            Toast.makeText(this, "This app requires ACCESS_FINE_LOCATION permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAndDisplayLastKnownLocation(): Location? {

        // Get best last location measurement
        mBestReading = bestLastKnownLocation()

        // Display last reading information
        if (null != mBestReading) {
            updateDisplay(mBestReading!!)
        } else {
            mZipView.text = "noloc"
        }

        // Return best reading or null
        return mBestReading
    }

    // Get the last known location from all providers return best reading that is as accurate
    // as minAccuracy and was taken no longer then minAge milliseconds ago. If none, return null.
    private fun bestLastKnownLocation(): Location? {
        var bestResult: Location? = null
        var bestAccuracy = Float.MAX_VALUE
        var bestAge = Long.MIN_VALUE
        val matchingProviders =
                mLocationManager.allProviders
        for (provider in matchingProviders) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val location = mLocationManager.getLastKnownLocation(provider)
                if (location != null) {
                    val accuracy = location.accuracy
                    val time = location.time
                    if (accuracy < bestAccuracy) {
                        bestResult = location
                        bestAccuracy = accuracy
                        bestAge = time
                    }
                }
            }
        }

        // Return best reading or null
        return if (bestAccuracy > MIN_ACCURACY || System.currentTimeMillis() - bestAge > TWO_MIN) {
            null
        } else {
            bestResult
        }
    }





}