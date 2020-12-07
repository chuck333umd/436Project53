package com.example.project53


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class MyJobsAdapter(private val mContext: Context, jobsCreated: MutableList<String>, userName: String) : BaseAdapter(){
    private var jobsList = jobsCreated
    private var name = userName

    override fun getItem(pos: Int): Any {

        return jobsList[pos]

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return jobsList.size
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val curJob = jobsList[position]
        
        var mLayoutInflater : LayoutInflater = LayoutInflater.from(mContext)
        val rowView = mLayoutInflater.inflate(R.layout.list_item,null,true)
        val descView = rowView.findViewById(R.id.listitem_desc) as TextView
        val dateView = rowView.findViewById(R.id.listitem_date) as TextView
        val timeView = rowView.findViewById(R.id.listitem_location) as TextView
        val payoutView = rowView.findViewById(R.id.listitem_dollar) as TextView
        val createdView = rowView.findViewById(R.id.listitem_username) as TextView

        descView.text = curJob
        createdView.text = name

        return rowView
    }



}