package com.example.kolhapurcivic
 fun isEmergency(issue: String): Boolean {

    return issue.contains("fire", true) ||
            issue.contains("accident", true) ||
            issue.contains("gas", true) ||
            issue.contains("flood", true)
}
