package com.example.imagegalleryproject.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

const val SINGLE_IMAGE_PAGE_ARG = "imgId"

sealed class PagesWithIconAndTitles(val route: String, val title: String, val icon: ImageVector ){
    object Gallery: PagesWithIconAndTitles("GalleryPage", "Gallery", Icons.Default.PhotoAlbum)
    object Favorites: PagesWithIconAndTitles("Favorites", "Favorites", Icons.Default.Favorite)
    object ProfileManagement: PagesWithIconAndTitles("ProfileManagement","Profile Management", Icons.Default.Person)
    object ProfileEdit: PagesWithIconAndTitles("ProfileEdit", "Edit Profile", Icons.Default.Edit)
    object SingleImagePage: PagesWithIconAndTitles("SingleImagePage/{$SINGLE_IMAGE_PAGE_ARG}", "Image Viewer", Icons.Default.Image) {
        fun passImgId(imgId: String): String {
            return this.route.replace(oldValue = SINGLE_IMAGE_PAGE_ARG, newValue = imgId)
        }
    }
}