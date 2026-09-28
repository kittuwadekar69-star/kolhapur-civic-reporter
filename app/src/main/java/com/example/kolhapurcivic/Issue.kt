package com.example.kolhapurcivic

data class Issue(
    val id: String = "",
    val issueType: String = "",
    val description: String = "",
    val area: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val status: String = "Pending",
    val priority: String = "Medium",
    val votes: Int = 0,
    val progress: Int = 0,
    val department: String = "",
    val imageBase64: String = "",
    val emergency: Boolean = false,
    val departmentsInvolved: List<String> = emptyList(),
    val coordinationRequired: Boolean = false,
    val assignedOfficer: String = "",
    val timeline: String = ""
)