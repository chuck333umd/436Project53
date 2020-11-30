package covidservices

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color.parseColor
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
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
 * If the zip or radius in invalid, the textview text color will turn red and a toast will appear
 * The user must enter a correct value for it to turn back to black and update the private vars
 *
 * Author: Chuck Daniels
 */

class MainListScreen : Activity() {


    private var loggedIn: Boolean = true /** Change to false for final build */
    private lateinit var mZipView: TextView
    private lateinit var mRadiusView: TextView
    private var mZip = ""
    private var mRadius = 50


    // Current best location estimate
    private var mBestReading: Location? = null

    // Reference to the LocationManager and LocationListener
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    private var mCancelHandle: ScheduledFuture<*>? = null
    private var mIsRequestingUpdates = false
    private var mShouldResume = false

    @TargetApi(Build.VERSION_CODES.M)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var numJobs = 0

        setContentView(R.layout.mainlist)
        mZipView = findViewById(R.id.location)
        mRadiusView = findViewById(R.id.radius)

        Log.i("SplashScreen", "Starting Main List Screen")


        /** START IMPORT */
        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (null != mLocationManager) {
            Log.i(TAG, "Couldn't find the LocationManager")
            // Return a LocationListener
        }
        mLocationListener = makeLocationListener()
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQUEST_FINE_LOC_PERM_ONCREATE)
        } else {
            getAndDisplayLastKnownLocation()
            installLocationListeners()
        }
        /** FINISH IMPORT */



        if (numJobs == 0) Toast.makeText(applicationContext, "No Jobs Currently", Toast.LENGTH_SHORT).show()



    }

    fun locationButton(view: View){

        hideKeyboard(this)


        if (zipValidator(mZipView.text.toString())){
            mZip = mZipView.text.toString()
            mZipView.setTextColor(parseColor("#000000"))
            Toast.makeText(applicationContext, "Location Updated", Toast.LENGTH_SHORT).show()
        }else{
            mZipView.setTextColor(parseColor("#FF0000"))
            Toast.makeText(applicationContext, "Invalid Zip", Toast.LENGTH_SHORT).show()
        }
    }

    fun radiusButton(view: View){

        hideKeyboard(this)


        if (mRadiusView.text.toString().length < 9 && radiusValidator(mRadiusView.text.toString()) && mRadiusView.text.toString().toInt() > 0){
            mRadius = mRadiusView.text.toString().toInt()
            mRadiusView.setTextColor(parseColor("#000000"))
            Toast.makeText(applicationContext, "Radius Updated", Toast.LENGTH_SHORT).show()
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
        val regex = "[0-9]+".toRegex()
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


    companion object {
        private const val ONE_MIN = 1000 * 60.toLong()
        private const val TWO_MIN = ONE_MIN * 2
        private const val MEASURE_TIME = TWO_MIN
        private const val POLLING_FREQ = 1000 * 10.toLong()
        private const val MIN_ACCURACY = 5.0f
        private const val MIN_DISTANCE = 10.0f
        private const val REQUEST_FINE_LOC_PERM_ONCREATE = 200
        private const val REQUEST_FINE_LOC_PERM_ONRESUME = 201
        private var mFirstUpdate = true
        private const val TAG = "LocationGetLocation"
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
        if (null == mBestReading ||
                mBestReading!!.accuracy > MIN_ACCURACY ||
                mBestReading!!.time < System.currentTimeMillis() - TWO_MIN
        ) {

            if (checkSelfPermission(ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED
            ) {
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
                Log.i(TAG, "Network location updates requested")
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        POLLING_FREQ, MIN_DISTANCE, mLocationListener)
                mIsRequestingUpdates = true
            }

            // Register for GPS location updates
            if (null != mLocationManager.getProvider(LocationManager.GPS_PROVIDER)) {
                Log.i(TAG, "GPS location updates requested")
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        POLLING_FREQ,
                        MIN_DISTANCE,
                        mLocationListener
                )
                mIsRequestingUpdates = true
            }

            // Schedule a runnable to unregister LocationListeners after MEASURE_TIME
            mCancelHandle = Executors.newScheduledThreadPool(1).schedule(
                    {
                        Log.i(TAG, "location updates cancelled")
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
            Toast.makeText(
                    this,
                    "This app requires ACCESS_FINE_LOCATION permission",
                    Toast.LENGTH_LONG
            ).show()
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

    // Update display
    private fun  updateDisplay(location: Location) {

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

        return when (item.itemId) {
            R.id.menu_login -> {
                loggedIn = true
                invalidateOptionsMenu()
                true
            }
            R.id.menu_logout -> {
                loggedIn = false
                invalidateOptionsMenu()
                true
            }
            R.id.menu_myjobs -> {
                startActivity(intentMyJobs)
                true
            }
            R.id.menu_createjob -> {
                startActivity(intentCreateJob)
                true
            }
            R.id.menu_mytasks -> {
                startActivity(intentMyTasks)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }



}