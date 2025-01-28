package com.example.ocr.ui

import android.util.Patterns
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.example.ocr.ui.theme.CameraPreview
import com.google.mlkit.vision.text.Text
import java.util.regex.Pattern

@OptIn(ExperimentalGetImage::class)
@Composable
fun OcrScreen(navController: NavController) {
    var recognizedText by remember { mutableStateOf("") }

    var isProcessing by remember { mutableStateOf(false) }

    val textRecognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    CameraPreview { imageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            textRecognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    recognizedText = visionText.text

                    val zipCode = extractZipCode(recognizedText)
                    if (zipCode != null){
                        navController.navigate("resultScreen/$zipCode")
                    }else{
                        recognizedText = "No se detectó un código postal válido"
                    }
                }
                .addOnFailureListener { e ->
                    recognizedText = "Error: ${e.message}"
                }
                .addOnCompleteListener {
                    imageProxy.close()
                    isProcessing = false
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = recognizedText,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}


fun extractZipCode(text: String): String? {
    val zipCodePattern = Pattern.compile("\\b\\d{5}\\b")
    val matcher = zipCodePattern.matcher(text)
    return if (matcher.find()) matcher.group() else null
}