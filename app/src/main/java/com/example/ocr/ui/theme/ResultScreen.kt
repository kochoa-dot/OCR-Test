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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(backStackEntry: NavBackStackEntry) {
    val zipCode = backStackEntry.arguments?.getString("zipcode") ?: ""
    var response by remember { mutableStateOf<ApiResponse?>(null) }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(zipCode) {
        scope.launch {
            try {
                // Llama a la API con el país "us" y el código postal proporcionado
                response = RetrofitClient.apiService.getZipCodeInfo("us", zipCode)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (response != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("País: ${response!!.country}")
                    Text("Código Postal: ${response!!.postCode}")
                    Spacer(modifier = Modifier.height(16.dp))

                    response!!.places.forEach { place ->
                        Text("Lugar: ${place.placeName}")
                        Text("Estado: ${place.state}")
                        Text("Latitud: ${place.latitude}")
                        Text("Longitud: ${place.longitude}")
                    }
                }
            } else if (error.isNotEmpty()) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            } else {
                CircularProgressIndicator()
            }
        }
    }
}
