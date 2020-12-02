package com.example.project53

class User(var name: String = "", var email: String = "", var jobsCreated: ArrayList<Long> = arrayListOf(), var tasksAccepted: ArrayList<Long> = arrayListOf()) {




        fun addJob(jobID: Long): Boolean{
                return jobsCreated.add(jobID)
        }

        fun addTask(jobID: Long): Boolean{
                return tasksAccepted.add(jobID)
        }


}