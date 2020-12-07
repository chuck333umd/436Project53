package com.example.project53

import java.util.*

class Job{

    public var jid: String = ""
    public var creator: String = ""
    public var cemail: String = ""
    public var temail: String? = ""
    public var tasker: String? = null
    public var date: Date? = null
    public var payout: Int = -1
    public var isStarted: Boolean = false
    public var isDone: Boolean = false
    public var description: String = ""
    public var zip: String = ""


    /** Use when job creator will not accept a lower offer initially*/


    constructor (jid: String, creator: String, cemail: String, date: Date, desc: String, zip: String, payout: Int) {
        this.jid = jid
        this.creator = creator
        this.cemail = cemail
        this.date = date
        this.zip = zip
        this.payout = payout
        this.description = desc
        this.tasker = null

    }


    constructor (jid: String, creator: String, cemail: String, date: Date, desc: String, zip: String, payout: Int, done:Boolean, started:Boolean, tasker: String?, temail: String?) {
        this.jid = jid
        this.creator = creator
        this.cemail = cemail
        this.date = date
        this.zip = zip
        this.payout = payout
        this.description = desc
        this.isStarted = started
        this.isDone = done
        this.tasker = tasker
        this.temail = temail
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
