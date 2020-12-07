package com.example.project53


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

class MyTasksAdapter(private val mContext: Context, tasks: MutableList<String>, userName: String) : BaseAdapter(){
    private var mytasks = tasks
    private var name = userName

    override fun getItem(pos: Int): Any {

        return mytasks[pos]

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mytasks.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val curTask = mytasks[position]



        var mLayoutInflater : LayoutInflater = LayoutInflater.from(mContext)
        val rowView = mLayoutInflater.inflate(R.layout.list_item,null,true)
        val descView = rowView.findViewById(R.id.listitem_desc) as TextView
        val dateView = rowView.findViewById(R.id.listitem_date) as TextView
        val timeView = rowView.findViewById(R.id.listitem_location) as TextView
        val payoutView = rowView.findViewById(R.id.listitem_dollar) as TextView
        val createdView = rowView.findViewById(R.id.listitem_username) as TextView

        descView.text = curTask
        createdView.text = name

        return rowView
    }



}