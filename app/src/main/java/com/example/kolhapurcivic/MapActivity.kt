package com.example.kolhapurcivic

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition

class MapActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val context = LocalContext.current

            var searchText by remember {
                mutableStateOf("")
            }

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    LatLng(16.7050, 74.2433),
                    12f
                )
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },
                    label = {
                        Text("Search Location")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )

                Button(
                    onClick = {

                        val geocoder = Geocoder(context)

                        val list = geocoder.getFromLocationName(
                            searchText,
                            1
                        )

                        if (!list.isNullOrEmpty()) {

                            val latLng = LatLng(
                                list[0].latitude,
                                list[0].longitude
                            )

                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng,
                                    15f
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Text("Search")
                }

                Spacer(modifier = Modifier.height(10.dp))

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {

                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                16.7050,
                                74.2433
                            )
                        ),
                        title = "Kolhapur"
                    )
                }
            }
        }
    }
}