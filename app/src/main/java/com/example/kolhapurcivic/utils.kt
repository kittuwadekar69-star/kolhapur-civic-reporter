package com.example.kolhapurcivic

fun getDepartment(issueType: String): String {

    return when(issueType.lowercase()) {

        "garbage" -> "Sanitation Department"

        "water" -> "Water Department"

        "electric" -> "Electric Department"

        "road" -> "Road Department"

        "pothole" -> "Road Department"

        else -> "General Department"
    }
}

fun getPriority(issueType: String): String {

    return when(issueType.lowercase()) {

        "electric" -> "Critical"

        "water" -> "High"

        "road" -> "Medium"

        "pothole" -> "Medium"

        else -> "Low"
    }
}

fun generateTimeline(priority: String): String {

    return when(priority) {

        "Critical" ->
            "Resolve within 24 Hours"

        "High" ->
            "Resolve within 3 Days"

        "Medium" ->
            "Resolve within 7 Days"

        else ->
            "Resolve within 14 Days"
    }
}

fun getDepartmentsInvolved(
    issueType: String
): List<String> {

    return when(issueType.lowercase()) {

        "road" -> listOf(
            "Road Department",
            "Water Department"
        )

        "electric" -> listOf(
            "Electric Department",
            "Emergency Cell"
        )

        "water" -> listOf(
            "Water Department",
            "Road Department"
        )

        else -> listOf(
            "General Department"
        )
    }
}
