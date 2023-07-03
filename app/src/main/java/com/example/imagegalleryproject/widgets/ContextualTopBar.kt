package com.example.imagegalleryproject.ui.AppBar

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.viewmodel.FavoriteViewModel

@Composable
fun ContextualTopBar(countOfSelectedItems: MutableState<Int>, imageVector: ImageVector,performAction: () -> Unit) {
    androidx.compose.material.TopAppBar(
        title = { Text(text = "${countOfSelectedItems.value} selected") },
        actions = {
            IconButton(onClick = { /* Handle action */ }) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        performAction()
                    }
                )
            }
        }
    )
}