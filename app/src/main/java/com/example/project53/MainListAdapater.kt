package com.example.project53

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.recyclerview.widget.RecyclerView

class MainListAdapater(private val mContext: Context,private val jobs: MutableList<String>) : BaseAdapter(){
    private var jobsList = jobs

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
        val rowView = mLayoutInflater.inflate(R.layout.custom_list,null,true)
        val titleText = rowView.findViewById(R.id.title) as TextView

        titleText.text = curJob
        return rowView
    }


}