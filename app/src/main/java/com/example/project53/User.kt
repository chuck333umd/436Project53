package com.example.project53

class User(var name: String, var email: String, var jobsCreated: ArrayList<Long>, var tasksAccepted: ArrayList<Long>) {




        fun addJob(jobID: Long): Boolean{
                return jobsCreated.add(jobID)
        }

        fun addTask(jobID: Long): Boolean{
                return tasksAccepted.add(jobID)
        }


}