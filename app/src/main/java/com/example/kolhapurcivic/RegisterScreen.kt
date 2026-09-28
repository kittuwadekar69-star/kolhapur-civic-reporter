package com.example.kolhapurcivic

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
@Composable
fun RegisterScreen(
    navController: NavController
) {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE5E0),   // light peach
                        Color(0xFFFFD6D6),   // soft red
                        Color(0xFFFFF3F0)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // TITLE
            Text(
                text = "ADMIN REGISTER",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B0000)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Create your civic admin account",
                color = Color.DarkGray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(35.dp))

            // CARD
            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(30.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),

                elevation = CardDefaults.cardElevation(10.dp)
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    // NAME
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },

                        label = {
                            Text("Full Name")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(18.dp),

                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFFB22222)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // EMAIL
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },

                        label = {
                            Text("Email")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(18.dp),

                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFFB22222)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // PHONE
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },

                        label = {
                            Text("Phone Number")
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(18.dp),

                        leadingIcon = {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color(0xFFB22222)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // PASSWORD
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },

                        label = {
                            Text("Password")
                        },

                        visualTransformation = PasswordVisualTransformation(),

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(18.dp),

                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFB22222)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // REGISTER BUTTON
                    Button(

                        onClick = {

                            if (
                                name.isNotEmpty() &&
                                email.isNotEmpty() &&
                                password.isNotEmpty()
                            ) {

                                FirebaseAuth.getInstance()
                                    .createUserWithEmailAndPassword(
                                        email,
                                        password
                                    )
                                    .addOnSuccessListener {

                                        val db = FirebaseFirestore.getInstance()

                                        val admin = hashMapOf(

                                            "name" to name,
                                            "email" to email,
                                            "phone" to phone,
                                            "role" to "admin"
                                        )

                                        db.collection("admins")
                                            .document(
                                                FirebaseAuth
                                                    .getInstance()
                                                    .currentUser!!
                                                    .uid
                                            )
                                            .set(admin)

                                        Toast.makeText(
                                            context,
                                            "Admin Registered Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        navController.navigate("login")
                                    }

                                    .addOnFailureListener {

                                        Toast.makeText(
                                            context,
                                            it.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                            } else {

                                Toast.makeText(
                                    context,
                                    "Fill all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),

                        shape = RoundedCornerShape(20.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB22222)
                        )

                    ) {

                        Text(
                            "REGISTER",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // LOGIN
                    TextButton(
                        onClick = {
                            navController.navigate("login")
                        },

                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {

                        Text(
                            "Already have an account? Login",
                            color = Color(0xFF8B0000)
                        )
                    }
                }
            }
        }
    }
}