package com.example.project53

import java.sql.Time
import java.util.*

class Job{

    var jid: String = ""
    var creator: String = ""
    var tasker: String? = null
    var date: Date? = null
    var time: Time? = null
    var payout: Int = -1
    var acceptLowerOffer: Boolean = false
    var minpayout: Int = -1
    var isStarted: Boolean = false
    var isDone: Boolean = false

    /** Use when job creator will not accept a lower offer initially*/
    constructor (jid: String, creator: String, date: Date, payout: Int) {
        this.jid = jid
        this.creator = creator
        this.date = date

        this.payout = payout
    }

    /** Use when job creator will auto-accept a lower offer without negotiation (like ebay)*/
    constructor (jid: String, creator: String, date: Date, time: Time, payout: Int, minPayout: Int) {
        this.jid = jid
        this.creator = creator
        this.date = date
        this.date = date
        this.payout = payout
        this.acceptLowerOffer = true
        this.minpayout = minPayout
    }

    fun completeJob(){
        isDone = true
    }


    fun assignTasker(user: String){
        this.tasker = user
        isStarted = true
    }


}
