package com.example.kolhapurcivic

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        // BUTTONS
        val btnReport = findViewById<Button>(R.id.btnReport)
        val btnMap = findViewById<Button>(R.id.btnMap)
        val btnBot = findViewById<Button>(R.id.btnBot)
        val btnIssues = findViewById<Button>(R.id.btnIssues)
        val btnTimetable = findViewById<Button>(R.id.btnTimetable)
        val btnChat = findViewById<Button>(R.id.btnChat)

        // CONTACT CARD
        val cardContact =
            findViewById<CardView>(R.id.cardContact)

        // REPORT ISSUE
        btnReport.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ReportIssueActivity::class.java
                )
            )
        }

        // MAP
        btnMap.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MapActivity::class.java
                )
            )
        }

        // CHATBOT
        btnBot.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ChatbotActivity::class.java
                )
            )
        }

        // ISSUES
        btnIssues.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ViewIssuesActivity::class.java
                )
            )
        }

        // TIMETABLE
        btnTimetable.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    TimetableActivity::class.java
                )
            )
        }

        // CHAT
        btnChat.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ChatActivity::class.java
                )
            )
        }

        // CONTACT
        cardContact.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ContactActivity::class.java
                )
            )
        }
    }
}

