package com.example.project53


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MyTasksAdapter(private val mContext: Context, private val jobs: MutableList<String>, description: MutableList<String>, dollar: MutableList<String>, dueDate: MutableList<String>, location: MutableList<String>, createdBy: MutableList<String>) : BaseAdapter(){
    private var jobsList = jobs
    private var desc = description
    private var dollar = dollar
    private var date = dueDate
    private var location = location
    private var created = createdBy

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
        val curLoc = location[position]
        val curDate = date[position]
        val curCreated = created[position]
        val curPayout = dollar[position]
        val curDesc = desc[position]



        var mLayoutInflater : LayoutInflater = LayoutInflater.from(mContext)
        val rowView = mLayoutInflater.inflate(R.layout.list_item,null,true)
        val descView = rowView.findViewById(R.id.listitem_desc) as TextView
        val dateView = rowView.findViewById(R.id.listitem_date) as TextView
        val curLocView = rowView.findViewById(R.id.listitem_location) as TextView
        val payoutView = rowView.findViewById(R.id.listitem_dollar) as TextView
        val createdView = rowView.findViewById(R.id.listitem_username) as TextView

        descView.text = curDesc
        dateView.text = curDate
        payoutView.text = curPayout
        createdView.text = curCreated
        curLocView.text = curLoc


        return rowView
    }



}