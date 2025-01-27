package com.example.ocr.ui

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.example.ocr.ui.theme.CameraPreview

@OptIn(ExperimentalGetImage::class)
@Composable
fun OcrScreen() {
    var recognizedText by remember { mutableStateOf("") }

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
                }
                .addOnFailureListener { e ->
                    recognizedText = "Error: ${e.message}"
                }
                .addOnCompleteListener {
                    imageProxy.close()
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
