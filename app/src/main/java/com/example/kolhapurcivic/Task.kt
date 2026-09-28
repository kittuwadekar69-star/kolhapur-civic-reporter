package com.example.kolhapurcivic

data class Task(

    val id: String = "",

    val taskName: String = "",

    val department: String = "",

    val assignedOfficer: String = "",

    val date: String = "",

    val time: String = "",

    val status: String = "Pending",

    val priority: String = "Medium"
)
