package com.example.project53

class User(var name: String, var email: String) {


        private val jobsCreated: ArrayList<Integer> = ArrayList()
        private val tasksAccepted: ArrayList<Integer> = ArrayList()

        fun addJob(jobID: Integer): Boolean{
                return jobsCreated.add(jobID)
        }

        fun addTask(jobID: Integer): Boolean{
                return tasksAccepted.add(jobID)
        }


}