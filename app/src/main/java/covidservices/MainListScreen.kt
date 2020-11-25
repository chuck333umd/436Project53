package covidservices

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import covidservices.R

class MainListScreen : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.mainlist)
        Log.i("SplashScreen", "Starting Main List Screen")

        Toast.makeText(applicationContext, "No Jobs Currently", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {

        //change back to finishaffinity for final build

        finish()
        //finishAffinity()
    }
}