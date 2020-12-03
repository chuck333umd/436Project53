package com.example.project53

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainListAdapater(private val mContext: Context) : BaseAdapter(){
    private val jobsList = ArrayList<Job>()

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
        var viewHolder: ViewHolder
        if(null == convertView){
            viewHolder = ViewHolder()
            viewHolder.title = viewHolder!!.mLayout!!.findViewById(R.id.listitem_desc) as TextView
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.title!!.text = "Test";
        return viewHolder.mLayout
    }
    internal class ViewHolder{
        var position: Int = 0
        var mLayout: RelativeLayout? = null
        var title: TextView? = null

    }

}