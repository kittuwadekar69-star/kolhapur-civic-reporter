package com.example.kolhapurcivic

import androidx.compose.runtime.*
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MapScreen(db: FirebaseFirestore) {

    var issues by remember { mutableStateOf(listOf<LatLng>()) }

    LaunchedEffect(Unit) {
        db.collection("issues")
            .get()
            .addOnSuccessListener { result ->

                val list = result.mapNotNull {
                    val lat = it.getDouble("latitude")
                    val lon = it.getDouble("longitude")

                    if (lat != null && lon != null) {
                        LatLng(lat, lon)
                    } else null
                }

                issues = list
            }
    }

    AndroidView(factory = { context ->

        val mapView = MapView(context)
        mapView.onCreate(null)
        mapView.onResume()

        mapView.getMapAsync { googleMap ->

            val kolhapur = LatLng(16.7050, 74.2433)

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kolhapur, 12f))

            // Add markers
            issues.forEach {
                googleMap.addMarker(
                    MarkerOptions().position(it).title("Reported Issue")
                )
            }
        }

        mapView
    })
}