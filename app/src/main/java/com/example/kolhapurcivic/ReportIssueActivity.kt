package com.example.kolhapurcivic

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalLayoutApi::class)
class ReportIssueActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val context = LocalContext.current

            var issueType by remember {
                mutableStateOf("")
            }

            var description by remember {
                mutableStateOf("")
            }

            var selectedImageUri by remember {
                mutableStateOf<Uri?>(null)
            }

            val galleryLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    selectedImageUri = uri
                }

            var issueTitle by remember { mutableStateOf("") }
            var locationText by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF3ECFF))
                    .padding(16.dp)
            ) {

                Text(
                    text = "REPORT CIVIC ISSUE",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = issueTitle,
                    onValueChange = {
                        issueTitle = it
                    },

                    label = {
                        Text("🚨 Issue Title")
                    },

                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = locationText,
                    onValueChange = {
                        locationText = it
                    },

                    label = {
                        Text("📍 Enter Location")
                    },

                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        "📸 Upload / Select Image",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {

                    if (selectedImageUri != null) {

                        val inputStream =
                            context.contentResolver
                                .openInputStream(selectedImageUri!!)

                        val bitmap =
                            BitmapFactory.decodeStream(inputStream)

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            contentScale = ContentScale.Crop
                        )

                    } else {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                "No Image Selected",
                                fontSize = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = issueType,
                    onValueChange = {
                        issueType = it
                    },
                    label = {
                        Text("Issue Type")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Description")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Select Issue Type",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                val categories = listOf(
                    "Garbage",
                    "Pothole",
                    "Water",
                    "Electric",
                    "Road",
                    "Others"
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    categories.forEach { category ->

                        Button(
                            onClick = {
                                issueType = category
                            },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6A3EA1)
                            )
                        ) {

                            Text(category)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = {

                        if (
                            issueType.isBlank() ||
                            description.isBlank()
                        ) {

                            Toast.makeText(
                                context,
                                "Fill all fields",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@Button
                        }

                        val priority = getPriority(issueType)

                        val issueData = hashMapOf(

                            "issueType" to issueType,

                            "description" to description,

                            "timeline" to
                                    generateTimeline(priority),

                            "status" to "Pending",

                            "votes" to 0,

                            "progress" to 0,

                            "assignedOfficer" to
                                    "Officer A",

                            "coordinationRequired" to true,

                            "departmentsInvolved" to
                                    getDepartmentsInvolved(issueType)
                        )

                        db.collection("issues")
                            .add(issueData)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    context,
                                    "Issue Submitted",
                                    Toast.LENGTH_SHORT
                                ).show()

                                issueType = ""
                                description = ""
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A3EA1)
                    )
                ) {

                    Text(
                        "Submit Issue",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
