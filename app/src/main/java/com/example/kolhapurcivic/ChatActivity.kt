package com.example.kolhapurcivic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var messages by remember {
                mutableStateOf(listOf<String>())
            }

            var text by remember {
                mutableStateOf("")
            }

            LaunchedEffect(Unit) {

                db.collection("globalChat")
                    .addSnapshotListener { value, _ ->

                        val temp =
                            mutableListOf<String>()

                        value?.documents?.forEach {

                            temp.add(
                                it.getString("message")
                                    ?: ""
                            )
                        }

                        messages = temp
                    }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF4F1EE))
                    .padding(16.dp)
            ) {

                Text(
                    "Interdepartmental Chat",
                    style =
                        MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {

                    items(messages) { msg ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    Color(0xFFD7CCC8)
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {

                            Text(
                                msg,
                                modifier =
                                    Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    label = {
                        Text("Send Message")
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {

                        val data = hashMapOf(

                            "message" to text
                        )

                        db.collection("globalChat")
                            .add(data)

                        text = ""
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFF6D4C41)
                        )
                ) {

                    Text("Send")
                }
            }
        }
    }
}