package com.example.kolhapurcivic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

class IssueDetailActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val issueId =
            intent.getStringExtra("issueId") ?: ""

        setContent {

            var issue by remember {
                mutableStateOf<Issue?>(null)
            }

            var messages by remember {
                mutableStateOf(listOf<String>())
            }

            var newMessage by remember {
                mutableStateOf("")
            }

            LaunchedEffect(Unit) {

                db.collection("issues")
                    .document(issueId)
                    .get()
                    .addOnSuccessListener {

                        issue = Issue(

                            id = it.id,

                            issueType =
                                it.getString("issueType")
                                    ?: "",

                            description =
                                it.getString("description")
                                    ?: "",

                            department =
                                it.getString("department")
                                    ?: "",

                            status =
                                it.getString("status")
                                    ?: "",

                            priority =
                                it.getString("priority")
                                    ?: "",

                            timeline =
                                it.getString("timeline")
                                    ?: "",

                            votes =
                                it.getLong("votes")
                                    ?.toInt() ?: 0,

                            progress =
                                it.getLong("progress")
                                    ?.toInt() ?: 0
                        )
                    }

                db.collection("issues")
                    .document(issueId)
                    .collection("chats")
                    .addSnapshotListener { value, _ ->

                        val temp = mutableListOf<String>()

                        value?.documents?.forEach {

                            temp.add(
                                it.getString("message")
                                    ?: ""
                            )
                        }

                        messages = temp
                    }
            }

            issue?.let { currentIssue ->

                LazyColumn(

                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF4F6FF))
                        .padding(16.dp)

                ) {

                    item {

                        Text(
                            text = currentIssue.issueType,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(16.dp))

                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    Color.White
                            )
                        ) {

                            Column(
                                modifier = Modifier
                                    .padding(18.dp)
                            ) {

                                Text(
                                    "Description",
                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(currentIssue.description)

                                Spacer(
                                    Modifier.height(14.dp)
                                )

                                Text(
                                    "Department: ${
                                        currentIssue.department
                                    }"
                                )

                                Text(
                                    "Priority: ${
                                        currentIssue.priority
                                    }"
                                )

                                Text(
                                    "Timeline: ${
                                        currentIssue.timeline
                                    }"
                                )

                                Text(
                                    "Votes: ${
                                        currentIssue.votes
                                    }"
                                )

                                Text(
                                    "Status: ${
                                        currentIssue.status
                                    }"
                                )

                                Spacer(
                                    Modifier.height(14.dp)
                                )

                                LinearProgressIndicator(
                                    progress =
                                        currentIssue.progress / 100f,
                                    modifier =
                                        Modifier.fillMaxWidth()
                                )

                                Spacer(
                                    Modifier.height(8.dp)
                                )

                                Text(
                                    "Progress: ${
                                        currentIssue.progress
                                    }%"
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            "Interdepartmental Coordination",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(14.dp))
                    }

                    items(messages) { msg ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    Color(0xFFE8EAF6)
                            )
                        ) {

                            Text(
                                text = msg,
                                modifier =
                                    Modifier.padding(16.dp)
                            )
                        }
                    }

                    item {

                        Spacer(Modifier.height(18.dp))

                        OutlinedTextField(
                            value = newMessage,
                            onValueChange = {
                                newMessage = it
                            },
                            label = {
                                Text("Coordination Message")
                            },
                            modifier =
                                Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = {

                                val chatData =
                                    hashMapOf(

                                        "message" to newMessage,

                                        "timestamp" to
                                                System.currentTimeMillis()
                                    )

                                db.collection("issues")
                                    .document(issueId)
                                    .collection("chats")
                                    .add(chatData)

                                newMessage = ""
                            },
                            modifier =
                                Modifier.fillMaxWidth(),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        Color(0xFF5E35B1)
                                )
                        ) {

                            Text("Send Message")
                        }
                    }
                }
            }
        }
    }
}
