package com.example.project53

import android.location.Location
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import java.lang.NumberFormatException
import java.net.URL

class DistFromZip {

    fun getDist(zip1: String, zip2: String): Float{

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val apiResponse1 = URL("https://maps.googleapis.com/maps/api/geocode/json?components=postal_code:$zip1&key=AIzaSyByaBb5GYYIoBo7lk1odivoK-q-ZfclmIQ").readText()
        //Log.i(ViewJob.TAG, "apiResponse1 = $apiResponse1 ")

        val lat1 = apiResponse1.substringAfter("\"lat\" : ").substring(0, 9)
        val lon1 = apiResponse1.substringAfter("\"lng\" : ").substring(0, 9)
        Log.i(ViewJob.TAG, "zip1 = $zip1 ")
        Log.i(ViewJob.TAG, "lat = $lat1 ")
        Log.i(ViewJob.TAG, "lon = $lon1 ")

        val apiResponse2 = URL("https://maps.googleapis.com/maps/api/geocode/json?components=postal_code:$zip2&key=AIzaSyByaBb5GYYIoBo7lk1odivoK-q-ZfclmIQ").readText()
        //Log.i(ViewJob.TAG, "apiResponse1 = $apiResponse1 ")

        val lat2 = apiResponse2.substringAfter("\"lat\" : ").substring(0, 9)
        val lon2 = apiResponse2.substringAfter("\"lng\" : ").substring(0, 9)
        Log.i(ViewJob.TAG, "zip2 = $zip2 ")
        Log.i(ViewJob.TAG, "lat = $lat2 ")
        Log.i(ViewJob.TAG, "lon = $lon2 ")

        try {
            val loc1 = Location("")
            loc1.latitude = lat1.toDouble()
            loc1.longitude = lon1.toDouble()

            val loc2 = Location("")
            loc2.latitude = lat2.toDouble()
            loc2.longitude = lon2.toDouble()

            val distanceInkM: Float = (loc1.distanceTo(loc2)/ 1000)

            Log.i(ViewJob.TAG, "distanceInkM = $distanceInkM ")

            return distanceInkM

        }catch (e: NumberFormatException){

        }

        return -1F
    }
}