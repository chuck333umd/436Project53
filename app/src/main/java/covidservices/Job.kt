package covidservices

import java.util.*

class Job{

    var creator: String = ""
    var tasker: String? = null
    var date: Date? = null
    var payout: Int = -1
    var acceptLowerOffer: Boolean = false
    var minpayout: Int = -1
    var isDone: Boolean = false

    /** Use when job creator will not accept a lower offer initially*/
    constructor (creator: String, date: Date, payout: Int) {
        this.creator = creator
        this.date = date
        this.payout = payout
    }

    /** Use when job creator will auto-accept a lower offer without negotiation (like ebay)*/
    constructor (creator: String, date: Date, payout: Int, minPayout: Int) {

        this.creator = creator
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
    }


}
