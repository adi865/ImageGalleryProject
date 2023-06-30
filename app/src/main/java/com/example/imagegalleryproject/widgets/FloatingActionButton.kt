package com.example.imagegalleryproject.widgets

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.imagegalleryproject.R
import com.example.imagegalleryproject.ui.MainActivity

@Composable
fun FAB() {
    val context = LocalContext.current
    androidx.compose.material.FloatingActionButton(
        onClick = {
            (context as? MainActivity)?.requestCameraPermission()
        },
        backgroundColor = Color(0xFFFFAC5F),
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_photo_camera_24),
            contentDescription = "fab"
        )
    }
}
