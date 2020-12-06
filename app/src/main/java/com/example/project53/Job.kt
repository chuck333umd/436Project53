package com.example.project53

import java.sql.Time
import java.util.*

class Job{

    public var jid: String = ""
    public var creator: String = ""
    public var tasker: String? = null
    public var date: Date? = null
    public var payout: Int = -1
    public var acceptLowerOffer: Boolean = false
    public var minpayout: Int = -1
    public var isStarted: Boolean = false
    public var isDone: Boolean = false
    public var description: String = ""
    public var zip: String = ""


    /** Use when job creator will not accept a lower offer initially*/


    constructor (jid: String, creator: String, date: Date, desc: String, zip: String, payout: Int) {
        this.jid = jid
        this.creator = creator
        this.date = date
        this.zip = zip
        this.payout = payout
        this.acceptLowerOffer = false
        this.minpayout = -1
        this.description = desc

    }

    /** Use when job creator will auto-accept a lower offer without negotiation (like ebay)*/
    constructor (jid: String, creator: String, date: Date, desc: String, zip: String,payout: Int, minPayout: Int) {
        this.jid = jid
        this.creator = creator
        this.date = date
        this.zip = zip
        this.payout = payout
        this.description = desc
        this.acceptLowerOffer = true
        this.minpayout = minPayout
    }
    constructor (jid: String, creator: String, date: Date, desc: String, zip: String,payout: Int, minPayout: Int, done:Boolean, started:Boolean,acceptLowerOffer:Boolean, tasker:String) {
        this.jid = jid
        this.creator = creator
        this.date = date
        this.zip = zip
        this.payout = payout
        this.description = desc
        this.acceptLowerOffer = acceptLowerOffer
        this.minpayout = minPayout
        this.isStarted = done
        this.isDone = started
        this.tasker = tasker
    }

    constructor()


    fun completeJob(){
        isDone = true
    }


    fun assignTasker(user: String){
        this.tasker = user
        isStarted = true
    }


}
