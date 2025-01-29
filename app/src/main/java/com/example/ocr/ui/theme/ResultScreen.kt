package com.example.ocr.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.example.ocr.api.ApiResponse
import com.example.ocr.api.RetrofitClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(backStackEntry: NavBackStackEntry) {
    val zipCode = backStackEntry.arguments?.getString("zipcode") ?: ""
    var response by remember { mutableStateOf<ApiResponse?>(null) }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 5f)
    }

    LaunchedEffect(zipCode) {
        scope.launch {
            try {
                response = RetrofitClient.apiService.getZipCodeInfo("us", zipCode)
                response?.places?.firstOrNull()?.let { place ->
                    val latitude = place.latitude.toDoubleOrNull()
                    val longitude = place.longitude.toDoubleOrNull()

                    if (latitude != null && longitude != null) {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 10f)
                        )
                    }
                }
            } catch (e: Exception) {
                error = "Error al obtener datos: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Información del Código Postal") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (response != null) {
                val place = response!!.places.firstOrNull()
                if (place != null) {
                    Text("País: ${response!!.country}")
                    Text("Código Postal: ${response!!.postCode}")
                    Text("Lugar: ${place.placeName}")
                    Text("Estado: ${place.state}")
                    Text("Latitud: ${place.latitude}")
                    Text("Longitud: ${place.longitude}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    cameraPositionState = cameraPositionState
                ) {
                    place?.let {
                        val latitude = it.latitude.toDoubleOrNull()
                        val longitude = it.longitude.toDoubleOrNull()
                        if (latitude != null && longitude != null) {
                            val markerState = rememberMarkerState(position = LatLng(latitude, longitude))

                            Marker(
                                state = markerState,
                                title = it.placeName,
                                snippet = "Estado: ${it.state}"
                            )
                        }
                    }
                }
            } else if (error.isNotEmpty()) {
                Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}
