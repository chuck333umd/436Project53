package com.example.project53

class User(var name: String = "", var email: String = "", var jobsCreated: ArrayList<String> = arrayListOf(), var tasksAccepted: ArrayList<String> = arrayListOf()) {




        fun addJob(jobID: Long): Boolean{
                return jobsCreated.add(jobID.toString())
        }

        fun addTask(jobID: Long): Boolean{
                return tasksAccepted.add(jobID.toString())
        }


}