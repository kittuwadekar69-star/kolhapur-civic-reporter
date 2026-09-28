package com.example.kolhapurcivic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactScreen(onBack: () -> Unit) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {

        item {

            Text(
                text = "📞 Civic Emergency Contacts",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            ContactCard(
                title = "Kolhapur Civic",
                email = "support@kolhapurcivic.in",
                phone = "+91 9607077713"
            )
        }

        item {
            ContactCard(
                title = "Kolhapur Municipal Corporation",
                email = "commissionerkmc@rediffmail.com",
                phone = "0231-2540291"
            )
        }

        item {
            ContactCard(
                title = "Zilla Parishad Kolhapur",
                email = "ceozp.kolhapur@maharashtra.gov.in",
                phone = "0231-2655411 / 2655412"
            )
        }

        item {

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onBack()
                },

                modifier = Modifier.fillMaxWidth(),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A1B9A)
                )
            ) {

                Text(
                    "Back",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ContactCard(
    title: String,
    email: String,
    phone: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),

        shape = RoundedCornerShape(22.dp),

        elevation = CardDefaults.cardElevation(10.dp)
    ) {

        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFF3E0),
                            Color(0xFFFFE0B2)
                        )
                    )
                )
                .padding(18.dp)
        ) {

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "📧 $email",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "📞 $phone",
                fontSize = 16.sp
            )
        }
    }
}
