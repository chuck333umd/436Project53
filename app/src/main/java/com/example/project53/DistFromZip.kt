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
       // Log.i("dist", "apiResponse1 = $apiResponse1 ")

        if (apiResponse1.contains("ZERO_RESULTS")){
            return -2F
        }

        val lat1 = apiResponse1.substringAfter("\"lat\" : ").substring(0, 9)
        val lon1 = apiResponse1.substringAfter("\"lng\" : ").substring(0, 9)
        Log.i("dist", "zip1 = $zip1 ")
        Log.i("dist", "lat = $lat1 ")
        Log.i("dist", "lon = $lon1 ")

        val apiResponse2 = URL("https://maps.googleapis.com/maps/api/geocode/json?components=postal_code:$zip2&key=AIzaSyByaBb5GYYIoBo7lk1odivoK-q-ZfclmIQ").readText()
        //Log.i("dist", "apiResponse1 = $apiResponse1 ")
        if (apiResponse2.contains("ZERO_RESULTS")){
            return -3F
        }

        val lat2 = apiResponse2.substringAfter("\"lat\" : ").substring(0, 9)
        val lon2 = apiResponse2.substringAfter("\"lng\" : ").substring(0, 9)
        Log.i("dist", "zip2 = $zip2 ")
        Log.i("dist", "lat = $lat2 ")
        Log.i("dist", "lon = $lon2 ")

        try {
            val loc1 = Location("")
            loc1.latitude = lat1.toDouble()
            loc1.longitude = lon1.toDouble()

            val loc2 = Location("")
            loc2.latitude = lat2.toDouble()
            loc2.longitude = lon2.toDouble()

            val distanceInkM: Float = (loc1.distanceTo(loc2)/ 1000)

            Log.i("dist", "distanceInkM = $distanceInkM ")

            return distanceInkM

        }catch (e: NumberFormatException){

        }

        return -1F
    }
}