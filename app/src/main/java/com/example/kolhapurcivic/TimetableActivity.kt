package com.example.kolhapurcivic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Brush

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*


import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

class TimetableActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            val navController = rememberNavController()
            TimetableScreen(db, navController)

        }
    }
}
@Composable
fun TimetableScreen(
    db: FirebaseFirestore,
    navController: NavController
) {

    val tasks = remember {
        mutableStateListOf<Task>()
    }

    var selectedFilter by remember {
        mutableStateOf("All")
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    // REALTIME FIRESTORE
    LaunchedEffect(Unit) {

        db.collection("tasks")
            .addSnapshotListener { result, _ ->

                tasks.clear()

                result?.forEach { document ->

                    val task = Task(

                        id = document.id,

                        taskName =
                            document.getString("taskName") ?: "",

                        department =
                            document.getString("department") ?: "",

                        assignedOfficer =
                            document.getString("assignedOfficer") ?: "",

                        date =
                            document.getString("date") ?: "",

                        time =
                            document.getString("time") ?: "",

                        status =
                            document.getString("status") ?: "Pending",

                        priority =
                            document.getString("priority") ?: "Medium"
                    )

                    tasks.add(task)
                }
            }
    }

    // FILTERED LIST
    val filteredTasks = tasks.filter {

        val statusMatch =
            selectedFilter == "All" ||
                    it.status == selectedFilter

        val searchMatch =
            it.taskName.contains(searchQuery, true)

        statusMatch && searchMatch
    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEDE7F6),
                        Color.White
                    )
                )
            )
            .padding(16.dp)

    ) {

        // TITLE
        item {

            Text(
                text = "📅 Smart Civic Timetable",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A148C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Municipal workflow monitoring system",
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // ANALYTICS CARDS
        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                MiniStatCard(
                    "Pending",
                    tasks.count {
                        it.status == "Pending"
                    }.toString(),
                    Color.Red
                )

                MiniStatCard(
                    "Resolved",
                    tasks.count {
                        it.status == "Resolved"
                    }.toString(),
                    Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // SEARCH
        item {

            OutlinedTextField(

                value = searchQuery,

                onValueChange = {
                    searchQuery = it
                },

                label = {
                    Text("🔍 Search Task")
                },

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(18.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // FILTER BUTTONS
        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                listOf(
                    "All",
                    "Pending",
                    "In Progress",
                    "Resolved"
                ).forEach { filter ->

                    FilterChip(
                        selected =
                            selectedFilter == filter,

                        onClick = {
                            selectedFilter = filter
                        },

                        label = {
                            Text(filter)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // TASK LIST
        items(filteredTasks) { task ->

            AdvancedTaskCard(
                task = task,
                db = db
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // BACK BUTTON
        item {

            Spacer(modifier = Modifier.height(20.dp))

            Button(

                onClick = {
                    navController.popBackStack()
                },

                modifier = Modifier.fillMaxWidth(),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A1B9A)
                ),

                shape = RoundedCornerShape(18.dp)
            ) {

                Text(
                    "⬅ Back",
                    color = Color.White
                )
            }
        }
    }
}

/* ---------------- TASK CARD ---------------- */

@Composable
fun AdvancedTaskCard(
    task: Task,
    db: FirebaseFirestore
) {

    val priorityColor = when(task.priority) {

        "High" -> Color.Red

        "Medium" -> Color(0xFFFF9800)

        else -> Color(0xFF43A047)
    }

    val statusColor = when(task.status) {

        "Resolved" -> Color(0xFF2E7D32)

        "In Progress" -> Color(0xFF1565C0)

        else -> Color.Red
    }

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )

    ) {

        Column(

            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color.White,
                            Color(0xFFF3E5F5)
                        )
                    )
                )
                .padding(18.dp)

        ) {

            // TITLE
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = task.taskName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor =
                            priorityColor.copy(alpha = 0.2f)
                    )
                ) {

                    Text(
                        text = task.priority,
                        color = priorityColor,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "🏢 Department: ${task.department}",
                fontSize = 17.sp
            )

            Text(
                "👮 Officer: ${task.assignedOfficer}",
                fontSize = 17.sp
            )

            Text(
                "📅 Date: ${task.date}",
                fontSize = 17.sp
            )

            Text(
                "⏰ Time: ${task.time}",
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // STATUS CARD
            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        statusColor.copy(alpha = 0.15f)
                )
            ) {

                Text(
                    text = "Status: ${task.status}",
                    color = statusColor,
                    modifier = Modifier.padding(10.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PROGRESS BAR
            val progress = when(task.status) {

                "Pending" -> 0.2f

                "In Progress" -> 0.6f

                "Resolved" -> 1f

                else -> 0.1f
            }

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = statusColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ADMIN ACTIONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                Button(

                    onClick = {

                        db.collection("tasks")
                            .document(task.id)
                            .update(
                                "status",
                                "In Progress"
                            )
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    )
                ) {

                    Text(
                        "Start",
                        color = Color.White
                    )
                }

                Button(

                    onClick = {

                        db.collection("tasks")
                            .document(task.id)
                            .update(
                                "status",
                                "Resolved"
                            )
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {

                    Text(
                        "Resolve",
                        color = Color.White
                    )
                }

                Button(

                    onClick = {

                        db.collection("tasks")
                            .document(task.id)
                            .delete()
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {

                    Text(
                        "Delete",
                        color = Color.White
                    )
                }
            }
        }
    }
}

/* ---------------- MINI STAT CARD ---------------- */

@Composable
fun MiniStatCard(
    title: String,
    value: String,
    color: Color
) {

    Card(

        modifier = Modifier
            .width(150.dp)
            .height(100.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                color.copy(alpha = 0.15f)
        )

    ) {

        Column(

            modifier = Modifier.padding(14.dp),

            verticalArrangement =
                Arrangement.Center

        ) {

            Text(
                text = title,
                color = color,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TaskCard(task: Task) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = task.taskName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("Department: ${task.department}")

            Text("Officer: ${task.assignedOfficer}")

            Text("Date: ${task.date}")

            Text("Time: ${task.time}")

            Text("Priority: ${task.priority}")

            Text("Status: ${task.status}")

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {

                    FirebaseFirestore.getInstance()
                        .collection("tasks")
                        .document(task.id)
                        .update("status", "Completed")
                }
            ) {

                Text("Mark Completed")
            }
        }
    }
}

@Composable
fun TimelineCard(
    title: String,
    desc: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),

        shape = RoundedCornerShape(18.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier
                .padding(18.dp)
        ) {

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = desc,
                color = Color.DarkGray
            )
        }
    }
}