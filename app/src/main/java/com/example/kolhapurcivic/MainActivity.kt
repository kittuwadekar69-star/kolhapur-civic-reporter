package com.example.kolhapurcivic

import androidx.compose.runtime.*

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import coil.compose.AsyncImage
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.airbnb.lottie.compose.*

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.Query
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController

import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FieldValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf

import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.*
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.location.Location

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.io.ByteArrayOutputStream


class MainActivity : ComponentActivity() {

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        db = FirebaseFirestore.getInstance()

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        setContent {

            val navController = rememberNavController()

            var latitude by remember {
                mutableStateOf(0.0)
            }

            var longitude by remember {
                mutableStateOf(0.0)
            }

            var issueList by remember {
                mutableStateOf(listOf<Issue>())
            }

            val context = LocalContext.current

            // LOCATION PERMISSION
            val permissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->

                    if (isGranted) {

                        try {

                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location ->

                                    location?.let {

                                        latitude = it.latitude
                                        longitude = it.longitude
                                    }
                                }

                        } catch (e: SecurityException) {

                            e.printStackTrace()
                        }

                    } else {

                        Toast.makeText(
                            context,
                            "Location Permission Denied",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            // ASK LOCATION PERMISSION
            LaunchedEffect(Unit) {

                permissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }

            // ---------------- NAVIGATION ----------------

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {

                // LOGIN
                composable("login") {

                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = {

                            navController.navigate("home") {

                                popUpTo("login") {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                // REGISTER
                composable("register") {

                    RegisterScreen(navController)
                }

                // HOME
                composable("home") {

                    HomeScreen(navController)
                }

                // REPORT ISSUE
                composable("report_issue") {

                    IssueScreen(
                        db,
                        latitude,
                        longitude,
                        navController
                    )
                }

                // VIEW ISSUES
                composable("issues") {

                    ViewIssuesScreen(
                        db,
                        false,
                        navController
                    )
                }

                // ADMIN
                composable("admin") {

                    AdminScreen(db)
                }

                // CHATBOT
                composable("chatbot") {

                    ChatbotScreen(
                        db,
                        navController
                    )
                }

                // CONTACT
                composable("contact") {

                    ContactScreen(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                // TIMETABLE
                composable("timetable") {

                    TimetableScreen(
                        db,
                        navController
                    )
                }

                // MAP
                composable("map") {

                    MapScreen(db)
                }

                // DASHBOARD
                composable("dashboard") {

                    DashboardSection(issueList)
                }
            }
        }
    }
}

//HOME SCREEN
@Composable
fun HomeScreen(navController: NavController) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD9CFE6)) // same theme
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Kolhapur Civic Reporter",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(30.dp))

                // REPORT ISSUE
                Button(
                    onClick = {
                        navController.navigate("report_issue")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B3B7A))
                ) {
                    Text("📢 Report Issue", color = Color.White)
                }

                Spacer(modifier = Modifier.height(15.dp))

                // VIEW ISSUES

                Button(
                    onClick = { navController.navigate("issues") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30)
                ) {
                    Text("📋 View Issues")
                }

                Spacer(modifier = Modifier.height(15.dp))

                // MAP
                Button(
                    onClick = {
                        navController.navigate("map")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1DB954)
                    )
                ) {
                    Text("View Map")
                }


                // AI CHATBOT
                Button(
                    onClick = {
                        navController.navigate("chatbot")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1565C0)
                    )
                ) {
                    Text("AI Chatbot")
                }

                //CHAT ACTIVITY
                Button(
                    onClick = {
                        navController.navigate("chat")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6d4c41)
                    )
                ) {
                    Text("YouChat")
                }


                //TIMETABLE
                Button(
                    onClick = {
                        navController.navigate("timetable")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFffdf00)
                    )
                ) {
                    Text("Admin Timetable")
                }

                //CONTACT
                Button(
                    onClick = {
                        navController.navigate("contact")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFff1493)
                    )
                ) {
                    Text("Contact")
                }

                // ADMIN
                Button(
                    onClick = { navController.navigate("admin") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("🛠 Admin Dashboard", color = Color.White)
                }
            }
        }
    }

@Composable
fun ViewIssuesScreen(
    db: FirebaseFirestore,
    isAdmin: Boolean,
    navController: NavController
) {

    var issueList by remember { mutableStateOf(listOf<Issue>()) }

    val department: String = ""
    val votes: Long = 0
    val emergency: Boolean = false

    LaunchedEffect(Unit) {
        db.collection("issues")
            .addSnapshotListener { snapshot, _ ->

                snapshot?.let {
                    issueList = it.map { doc ->
                        Issue(
                            issueType = doc.getString("type") ?: "",
                            description = doc.getString("description") ?: "",
                            area = doc.getString("area") ?: "",
                            status = doc.getString("status") ?: "Pending"
                        )
                    }
                }
            }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("📋 Reported Issues", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(10.dp))

        issueList.forEach { issue ->

            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)) {

                Column(modifier = Modifier.padding(10.dp)) {

                    Text("🚨 ${issue.issueType}")
                    Text("📍 ${issue.area}")
                    Text("📝 ${issue.description}")
                    Text("📌 Status: ${issue.status}")

                    Text(
                        text = "Department: ${issue.department}"
                    )

                    Text(
                        text = "Votes: ${issue.votes}"
                    )

                    //VOTE BUTTON
                    val context = LocalContext.current

                    Button(
                        onClick = {

                            FirebaseFirestore.getInstance()
                                .collection("issues")
                                .document(issue.id)
                                .update(
                                    "votes",
                                    FieldValue.increment(1)
                                )
                        }
                    ) {
                        Text("Upvote")
                    }


                    //EMERGENCY ISSUES
                    if (issue.emergency == true) {

                        Text(
                            text = "EMERGENCY",
                            color = Color.Red
                        )
                    }

                    if (isAdmin) {

                            Text("📊 Admin Dashboard")

                            Text("Total Issues: ${issueList.size}")
                            Text("Pending: ${issueList.count { it.status == "Pending" }}")
                            Text("Resolved: ${issueList.count { it.status == "Resolved" }}")

                            Spacer(modifier = Modifier.height(10.dp))

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(onClick = {
                            db.collection("issues")
                                .whereEqualTo("area", issue.area)
                                .get()
                                .addOnSuccessListener { docs ->
                                    for (doc in docs) {
                                        db.collection("issues")
                                            .document(doc.id)
                                            .update("status", "Resolved")
                                    }
                                }
                        }) {
                            Text("✔ Resolve Issue")
                        }

                        Button(onClick = {
                            navController.navigate("coordination")
                        }) {
                            Text("📞 Coordinate Dept")
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun IssueScreen(
    db: FirebaseFirestore,
    latitude: Double,
    longitude: Double,
    navController: NavController
) {

    // ---------------- STATES (UNCHANGED LOGIC) ----------------

    val departments = listOf(
        "Road Department",
        "Water Department",
        "Electricity Department",
        "Garbage Management",
        "Drainage Department",
        "Public Health Department"
    )

    var expanded by remember { mutableStateOf(false) }

    var selectedDepartment by remember {
        mutableStateOf("Select Department")
    }

    var description by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var base64Image by remember { mutableStateOf<String?>(null) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    var selectedFilter by remember { mutableStateOf("All") }

    var issueList by remember { mutableStateOf(listOf<Issue>()) }
    var selectedIssueType by remember { mutableStateOf("Garbage") }


    val context = LocalContext.current

    //SPLASH ACTIVITY
    var showAnimation by remember {
        mutableStateOf(false)
    }
    showAnimation = true
    if (showAnimation) {

        val composition by rememberLottieComposition(
            LottieCompositionSpec.Url(
                "https://assets2.lottiefiles.com/packages/lf20_jbrw3hcz.json"
            )
        )

        LottieAnimation(
            composition = composition,
            iterations = 1,
            modifier = Modifier.size(200.dp)
        )
    }

    // ---------------- FIRESTORE (UNCHANGED) ----------------
    DisposableEffect(Unit) {

        val listener = db.collection("issues")
            .addSnapshotListener { snapshot, _ ->

                snapshot?.let {
                    issueList = it.map { doc ->
                        Issue(
                            issueType  = doc.getString("type") ?: "",
                            description = doc.getString("description") ?: "",
                            area = doc.getString("area") ?: "",
                            priority=doc.getString("priority") ?: "MEDIUM",
                            status = doc.getString("status") ?: "Pending",
                            imageBase64 = doc.getString("imageBase64") ?: "",
                            progress = (doc.getLong("progress") ?: 0).toInt(),
                            department = doc.getString("department") ?: "",
                            votes = (doc.getLong("votes") ?: 0L).toInt(),
                            emergency = doc.getBoolean("emergency") ?: false
                        )
                    }
                }
            }

        onDispose { listener.remove() }
    }

    // ---------------- CAMERA (UNCHANGED) ----------------
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->

        bitmap?.let {
            capturedImage = it

            val baos = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            base64Image = Base64.encodeToString(
                baos.toByteArray(),
                Base64.DEFAULT
            )
        }
    }

    // ================= UI START =================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD9CFE6)) // light purple
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------------- BACK ----------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))
                ) {
                    Text("BACK", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---------------- TITLE ----------------
            Text(
                text = "REPORT CIVIC ISSUE",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------- CAMERA ----------------
            Button(
                onClick = { cameraLauncher.launch(null) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text("📸 Capture Photo")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---------------- IMAGE PREVIEW ----------------
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                capturedImage?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Text("No Image")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------- INPUT ----------------
            OutlinedTextField(
                value = locationText,
                onValueChange = { locationText = it },
                label = { Text("📍 Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = locationText,
                onValueChange = {
                    locationText = it
                },
                label = {
                    Text("📍 Enter Location")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("📝 Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))


            Spacer(modifier = Modifier.height(20.dp))

            // ---------------- ISSUE TYPES (CLEAN GRID) ----------------
            Text("Select Issue Type", style = MaterialTheme.typography.titleMedium)

            val issueTypes = listOf(
                "Garbage",
                "Pothole",
                "Water Problem",
                "Electricity",
                "Road Damage",
                "Environmental",
                "Others"
            )

            issueTypes.forEach { type ->

                Button(
                    onClick = {
                        selectedIssueType = type
                    }
                ) {
                    Text(type)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(

                onClick = {

                    val locationPair =
                        getLatLngFromAddress(
                            context,
                            locationText
                        )

                    if(locationPair != null) {

                        reportIssue(
                            db = db,
                            issueType = selectedIssueType,
                            location = locationText,
                            description = description,
                            latitude = locationPair.first,
                            longitude = locationPair.second,
                            base64Image = base64Image
                        )

                        Toast.makeText(
                            context,
                            "Issue Submitted Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            context,
                            "Invalid location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                shape = RoundedCornerShape(18.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43A047)
                )

            ) {

                Text(
                    "🚀 Submit Issue",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            issueTypes.chunked(3).forEach { row ->

                Row(modifier = Modifier.fillMaxWidth()) {

                    row.forEach { type ->

                        Button(
                            onClick = {

                                val locationPair = getLatLngFromAddress(
                                    context,
                                    locationText
                                )

                                if (locationPair != null) {

                                    val lat = locationPair.first
                                    val lng = locationPair.second

                                    reportIssue(
                                        db,
                                        type,
                                        locationText,
                                        description,
                                        lat,
                                        lng,
                                        base64Image
                                    )

                                    Toast.makeText(
                                        context,
                                        "Issue Reported Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {

                                    Toast.makeText(
                                        context,
                                        "Location not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },





                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5B3B7A)
                            )
                        ) {
                            Text(type, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------- SEARCH ----------------
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("🔍 Search Issues") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            //SUBMIT BUTTON
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {

                    reportIssue(
                        db,
                        "General",
                        locationText,
                        description,
                        latitude,
                        longitude,
                        base64Image

                    )

                    Toast.makeText(
                        context,
                        "Issue Submitted",
                        Toast.LENGTH_SHORT
                    ).show()
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),

                shape = RoundedCornerShape(20.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )

            ) {

                Text(
                    "SUBMIT ISSUE",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            // ---------------- FILTERS ----------------
            Row {
                listOf("All", "Pending", "Resolved").forEach {
                    OutlinedButton(onClick = { selectedStatus = it }) {
                        Text(it)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //DEPARTMENT CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),

                shape = RoundedCornerShape(16.dp),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFE0B2),
                                    Color(0xFFFFF3E0)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {

                    Text(
                        text = "Assigned Department",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box {

                        Button(

                            onClick = {
                                expanded = true
                            },

                            modifier = Modifier.fillMaxWidth(),

                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF9800)
                            )
                        ) {

                            Text(
                                text = selectedDepartment,
                                color = Color.White
                            )

                            Spacer(
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,

                            onDismissRequest = {
                                expanded = false
                            }
                        ) {

                            departments.forEach { dept ->

                                DropdownMenuItem(

                                    text = {
                                        Text(dept)
                                    },

                                    onClick = {
                                        selectedDepartment = dept
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ---------------- ISSUE LIST ----------------
            Text("Reported Issues")

            val filteredList = issueList.filter {

                val typeMatch = selectedFilter == "All" || it.issueType == selectedFilter
                val statusMatch = selectedStatus == "All" || it.status == selectedStatus
                val searchMatch = it.description.contains(searchQuery, true)

                typeMatch && statusMatch && searchMatch
            }

            filteredList.forEach { issue ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(modifier = Modifier.padding(10.dp)) {

                        Text("🚨 ${issue.issueType}")
                        Text("📍 ${issue.area}")
                        Text("📝 ${issue.description}")

                        // PRIORITY (UNCHANGED)
                        val priority = calculatePriority(issue.issueType, issue.area)

                        Text("⚡ ${priority.level}")

                        // HOTSPOT
                        val count = getHotspotScore(issueList, issue.area)
                        if (count >= 3) Text("🔥 HOTSPOT", color = Color.Red)

                        // IMAGE
                        issue.imageBase64?.takeIf { it.isNotEmpty() }?.let {

                            val bytes = Base64.decode(it, Base64.DEFAULT)
                            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp)
                            )
                        }

                        // STATUS
                        Text("Status: ${issue.status}")

                        Text(
                            "🏢 Department: ${issue.department}",
                            color = Color(0xFF1565C0)
                        )
                    }
                }
            }
        }
    }
}

//SELECT ROLE (USER/ADMIN)
@Composable
fun RoleSelectionScreen(onRoleSelected: (String) -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Select Role", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { onRoleSelected("user") }) {
            Text("👤 User")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { onRoleSelected("admin") }) {
            Text("🛠️ Admin")
        }
    }
}

//ADMIN SCREEN
@Composable
fun AdminScreen(db: FirebaseFirestore) {

    val issues = remember { mutableStateListOf<Issue>() }

    LaunchedEffect(Unit) {
        db.collection("issues")
            .addSnapshotListener { result, _ ->

                issues.clear()

                result?.forEach { document ->

                    val issue = Issue(
                        id = document.id,
                        issueType = document.getString("issueType") ?: "",
                        description = document.getString("description") ?: "",
                        department = document.getString("department") ?: "",
                        status = document.getString("status") ?: "Pending",
                        priority = document.getString("priority") ?: "",
                        votes = (document.getLong("votes") ?: 0L).toInt(),
                        assignedOfficer = document.getString("assignedOfficer") ?: "",
                        coordinationRequired = document.getBoolean("coordinationRequired") ?: false,
                        departmentsInvolved = document.get("departmentsInvolved") as? List<String> ?: emptyList()
                    )

                    issues.add(issue)
                }
            }
    }

    val pending = issues.count { it.status == "Pending" }
    val resolved = issues.count { it.status == "Resolved" }

    val deptMap = issues
        .groupingBy { it.department }   // ✅ FIXED HERE
        .eachCount()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            "📊 Admin Dashboard",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatCard("Pending", pending.toString(), Color.Red)
            StatCard("Resolved", resolved.toString(), Color(0xFF2E7D32))
        }

        Spacer(Modifier.height(20.dp))

        Text("Department Distribution", fontWeight = FontWeight.Bold)

        deptMap.forEach { (dept, count) ->

            AnalyticsBar(
                label = dept,
                value = count,
                color = Color(0xFF1976D2)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text("All Issues", fontWeight = FontWeight.Bold)

        issues.forEach { issue ->
            IssueCard(issue)
            Spacer(Modifier.height(10.dp))
        }
    }
}

//ISSUE CARD
@Composable
fun IssueCard(issue: Issue) {

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {

                val intent = Intent(
                    context,
                    IssueDetailActivity::class.java
                )

                intent.putExtra(
                    "issueId",
                    issue.id
                )

                context.startActivity(intent)
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Text("📌 Timeline")

        Text("1. Report Submitted")
        Text("2. Department Assigned")
        Text("3. Officer Visit")
        Text("4. Work In Progress")
        Text("5. Resolution")

        Text(
            text = "Officer: ${issue.assignedOfficer}"
        )

        if(issue.votes >= 5){

            Text(
                text = "🔥 HIGH PUBLIC CONCERN",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = issue.issueType,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor =
                            Color(0xFFFFE082)
                    )
                ) {

                    Text(
                        issue.priority,
                        modifier =
                            Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                issue.description,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Department: ${issue.department}"
            )

            Text(
                "Status: ${issue.status}"
            )

            Text(
                "Votes: ${issue.votes}"
            )

            Text(
                "Timeline: ${issue.timeline}"
            )

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = issue.progress / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF5E35B1)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Progress: ${issue.progress}%"
            )
            if(SessionManager.role == "admin") {

                Spacer(modifier = Modifier.height(10.dp))

                Button(

                    onClick = {

                        val newProgress =
                            (issue.progress + 20).coerceAtMost(100)

                        val newStatus = when {

                            newProgress >= 100 -> "Resolved"

                            newProgress >= 50 -> "In Progress"

                            else -> "Pending"
                        }

                        FirebaseFirestore.getInstance()
                            .collection("issues")
                            .document(issue.id)
                            .update(
                                mapOf(
                                    "progress" to newProgress,
                                    "status" to newStatus
                                )
                            )

                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    )

                ) {

                    Text(
                        "⬆ Update Progress",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // RESOLVE BUTTON
                    Button(
                        onClick = {

                            FirebaseFirestore.getInstance()
                                .collection("issues")
                                .document(issue.id)
                                .update(
                                    mapOf(
                                        "status" to "Resolved",
                                        "progress" to 100
                                    )
                                )
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {

                        Text(
                            "✔ Resolve",
                            color = Color.White
                        )
                    }

                    // DELETE BUTTON
                    Button(
                        onClick = {

                            FirebaseFirestore.getInstance()
                                .collection("issues")
                                .document(issue.id)
                                .delete()
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {

                        Text(
                            "🗑 Delete",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapScreen(db: FirebaseFirestore) {
    val context = LocalContext.current
    Toast.makeText(context, "Issue Reported!", Toast.LENGTH_SHORT).show()
    var issueList by remember { mutableStateOf(listOf<Issue>()) }

    // 📡 Fetch data from Firestore
    LaunchedEffect(Unit) {
        db.collection("issues")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { doc ->
                    Issue(
                        issueType = doc.getString("type") ?: "",
                        description = doc.getString("description") ?: "",
                        area = doc.getString("area") ?: ""
                    )
                }
                issueList = list
            }
    }

    // 📍 Default location (Kolhapur)
    val kolhapur = com.google.android.gms.maps.model.LatLng(16.7050, 74.2433)

    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(kolhapur, 12f)
    }
    var selectedFilter by remember { mutableStateOf("All") }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        val color = if (issueList.size > 5)
            BitmapDescriptorFactory.HUE_RED
        else
            BitmapDescriptorFactory.HUE_GREEN

        // 📌 Add markers for each issue
        val filteredList = if (selectedFilter == "All") issueList
        else issueList.filter { it.issueType == selectedFilter }
        val areaCount = issueList.groupingBy { it.area }.eachCount()

        filteredList.forEach { issue ->

            // If location stored as "lat,lon"
            val parts = issue.area.split(",")

            if (parts.size == 2) {
                val lat = parts[0].toDoubleOrNull()
                val lon = parts[1].toDoubleOrNull()

                if (lat != null && lon != null) {

                    val count = areaCount[issue.area] ?: 1

                    val color = when {
                        count >= 5 -> BitmapDescriptorFactory.HUE_RED
                        count >= 3 -> BitmapDescriptorFactory.HUE_ORANGE
                        else -> BitmapDescriptorFactory.HUE_GREEN
                    }


                    Marker(
                        state = MarkerState(position = LatLng(lat, lon)),
                        title = issue.issueType,
                        snippet = issue.description,
                        icon = BitmapDescriptorFactory.defaultMarker(color)

                    )
                }
            }
        }
    }
}

//RECORDING TYPED LOCATIONS INTO REAL-WORLD MAP LOCATIONS
fun getLatLngFromAddress(
    context: Context,
    address: String
): Pair<Double, Double>? {

    return try {

        val coder = Geocoder(context)
        val list = coder.getFromLocationName(address, 1)

        if (!list.isNullOrEmpty()) {

            Pair(
                list[0].latitude,
                list[0].longitude
            )

        } else null

    } catch (e: Exception) {
        null
    }
}

// chatbot
@Composable
fun ChatbotScreen(db: FirebaseFirestore, navController: NavController){

    var userInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf<String>()) }
    var issueList by remember {mutableStateOf(listOf<Issue>())}

    Column(modifier = Modifier.padding(16.dp)) {

        Text("🤖 Civic AI Assistant", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(10.dp))

        // Chat history
        chatHistory.forEach {
            Text(it)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Describe your problem") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {

            val response = smartAI(userInput,userArea="Kolhapur",pastIssues=issueList)

            chatHistory = chatHistory + "You: $userInput"
            chatHistory = chatHistory + "AI: $response"

            // SAVE TO FIRESTORE
            val chatData = hashMapOf(
                "query" to userInput,
                "response" to response,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("chatHistory").add(chatData)

            userInput = ""

        }) {
            Text("Ask AI")
        }
        Spacer(modifier = Modifier.height(10.dp))

        LaunchedEffect(Unit) {
            db.collection("issues").addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    issueList = it.map { doc ->
                        Issue(
                            issueType= doc.getString("type") ?: "",
                            description =  doc.getString("description") ?: "",
                            area= doc.getString("area") ?: "",
                            status= doc.getString("status") ?: "Pending",
                            department = doc.getString("department") ?: ""
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}

data class PriorityResult(
    val level: String,
    val color: Long,
    val reason: String
)
//CALCULATE PRIORITY
fun calculatePriority(type: String, area: String): PriorityResult {

    val score = when (type) {
        "Pothole" -> 90
        "Water Problem" -> 85
        "Garbage" -> 60
        else -> 40
    }

    val adjustedScore = score + if (area.contains("main", true)) 10 else 0

    return when {
        adjustedScore >= 85 -> PriorityResult(
            "CRITICAL 🔴",
            0xFFFF5252,
            "Immediate safety risk"
        )

        adjustedScore >= 70 -> PriorityResult(
            "HIGH 🟠",
            0xFFFFA726,
            "Needs urgent attention"
        )

        adjustedScore >= 50 -> PriorityResult(
            "MEDIUM 🟡",
            0xFFFFEB3B,
            "Should be resolved soon"
        )

        else -> PriorityResult(
            "LOW 🟢",
            0xFF66BB6A,
            "Routine maintenance"
        )
    }
}
@Composable
fun DashboardSection(issueList: List<Issue>) {

    val total = issueList.size
    val pending = issueList.count { it.status == "Pending" }
    val inProgress = issueList.count { it.status == "In Progress" }
    val resolved = issueList.count { it.status == "Resolved" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text(
            text = "📊 Civic Analytics Dashboard",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        // STATS ROW 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("Total", total.toString(), Color(0xFF1976D2))
            StatCard("Pending", pending.toString(), Color.Red)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // STATS ROW 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("In Progress", inProgress.toString(), Color(0xFFFFA000))
            StatCard("Resolved", resolved.toString(), Color(0xFF388E3C))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BAR CHART SECTION
        Text(
            text = "📈 Issue Analytics",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        AnalyticsBar("Pending", pending, Color.Red)
        AnalyticsBar("In Progress", inProgress, Color(0xFFFFA000))
        AnalyticsBar("Resolved", resolved, Color(0xFF388E3C))

        Spacer(modifier = Modifier.height(20.dp))

        // PIE CHART SECTION
        Text(
            text = "🥧 Department Distribution",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        DepartmentPieChart(issueList)
    }
}

/* ---------------- STAT CARD ---------------- */

@Composable
fun StatCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = color)
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/* ---------------- BAR CHART ---------------- */

@Composable
fun AnalyticsBar(label: String, value: Int, color: Color) {

    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {

        Text(text = "$label: $value")

        Spacer(Modifier.height(6.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(18.dp)
                .background(Color.LightGray)
        ) {

            Box(
                Modifier
                    .fillMaxWidth(if (value == 0) 0f else value / 10f)
                    .height(18.dp)
                    .background(color)
            )
        }
    }
}

/* ---------------- PIE CHART (SIMPLE SAFE VERSION) ---------------- */

@Composable
fun DepartmentPieChart(issueList: List<Issue>) {

    val deptCounts = issueList
        .groupingBy { it.department }
        .eachCount()

    val total = deptCounts.values.sum()

    val colors = listOf(
        Color(0xFFE53935),
        Color(0xFF43A047),
        Color(0xFF1E88E5),
        Color(0xFFFFB300),
        Color(0xFF8E24AA),
        Color(0xFF00897B)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🥧 Department Analytics",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Canvas(
            modifier = Modifier.size(260.dp)
        ) {

            var startAngle = -90f

            deptCounts.entries.forEachIndexed { index, entry ->

                val sweep =
                    (entry.value.toFloat() / total.toFloat()) * 360f

                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height)
                )

                startAngle += sweep
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        deptCounts.entries.forEachIndexed { index, entry ->

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(colors[index % colors.size])
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "${entry.key}: ${entry.value} issues",
                    fontSize = 16.sp
                )
            }
        }
    }
}

fun smartAI(
    input: String,
    userArea: String,
    pastIssues: List<Issue>
): String {

    val text = input.lowercase()

    // 🔍 Context awareness (user history)
    val similarIssues = pastIssues.count {
        it.area == userArea || it.issueType.contains(text, true)
    }

    val urgencyTag = when {
        similarIssues >= 5 -> "🔥 CRITICAL AREA ALERT"
        similarIssues >= 3 -> "⚠️ REPEATED ISSUE ZONE"
        else -> "📍 NORMAL AREA"
    }

    return when {

        "pothole" in text -> """
🚧 Pothole Issue Report

$urgencyTag

👤 Personal:
- Avoid damaged road sections
- Drive carefully in affected zone

👥 Community:
- Report repeatedly via app (your area has $similarIssues cases)
- Share issue in local groups

🏛 Government:
- Immediate road repair required
- Use asphalt resurfacing for durability
- Install road monitoring system

📊 AI Priority: HIGH
""".trimIndent()

        "garbage" in text || "waste" in text -> """
🗑 Waste Management Issue

$urgencyTag

👤 Personal:
- Avoid littering
- Use dustbins properly

👥 Community:
- Organize cleanliness drive
- Report repeated dumping ($similarIssues cases found)

🏛 Government:
- Increase garbage pickup frequency
- Install public dustbins

📊 AI Priority: MEDIUM
""".trimIndent()

        "water" in text || "leak" in text -> """
💧 Water Supply Issue

$urgencyTag

👤 Personal:
- Conserve water usage
- Report leakage immediately

👥 Community:
- Coordinate reporting of pipeline leaks
- Share water during shortage

🏛 Government:
- Repair pipeline infrastructure
- Monitor water pressure systems

"flood" in text -> ""${'"'}
🌊 Flood Situation

👤 Personal:
Move to higher ground, avoid waterlogged roads

👥 Community:
Coordinate rescue and support

🏛 Government:
Deploy emergency teams, drainage clearing

📊 Priority: CRITICAL
""${'"'}.trimIndent()

📊 AI Priority: HIGH
""".trimIndent()

        else -> """
         
🤖 Smart Civic Assistant

Please describe your issue clearly:
Example:
- pothole
- garbage
- water problem

📍 Your Area: $userArea
📊 Similar past issues: $similarIssues
"""
    }
}

//HOTSPOT SCORE
fun getHotspotScore(issueList: List<Issue>, area: String): Int {
    return issueList.count { it.area == area }
}

fun getProgressFromStatus(status: String): Int {
    return when (status) {
        "Pending" -> 0
        "In Progress" -> 50
        "Resolved" -> 100
        else -> 0
    }
}
//INTERDEPARTMENTAL COORDINATION FEATURE
fun sendToDepartment(issue: Issue, department: String) {

    val coord = hashMapOf(
        "issueType" to issue.issueType,
        "area" to issue.area,
        "department" to department,
        "status" to "Forwarded",
        "timestamp" to System.currentTimeMillis()
    )

    FirebaseFirestore.getInstance()
        .collection("coordination")
        .add(coord)
}


fun reportIssue(
    db: FirebaseFirestore,
    issueType: String,
    location: String,
    description: String,
    latitude: Double,
    longitude: Double,
    base64Image: String?
) {

    val priority = calculatePriority(issueType, location)

    val department = when(issueType) {

        "Garbage" -> "Sanitation Department"

        "Pothole" -> "Road Department"

        "Water Problem" -> "Water Supply Department"

        "Flood" -> "Disaster Management"

        "Traffic" -> "Traffic Department"

        else -> "General Department"
    }
    val assignedDepartment = getDepartment(issueType)

    val issue = hashMapOf(
        "type" to issueType,
        "city" to "Kolhapur",
        "area" to location,
        "description" to description,
        "latitude" to latitude,
        "longitude" to longitude,
        "imageBase64" to (base64Image ?: ""),
        "priority" to priority.level,
        "status" to "Pending",
        "progress" to 0,
        "votes" to 0,
        "description" to description,
        "department" to assignedDepartment
    )

    FirebaseFirestore.getInstance()
        .collection("issues")
        .add(issue)
        .addOnSuccessListener {
            Log.e("Firestore", "Successful Reporting!")
        }
        .addOnFailureListener {
            Log.e("Firestore", "Error")

        }
}