package com.example.imagegalleryproject.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

const val SINGLE_IMAGE_PAGE_ARG = "imgId"

sealed class Pages(val route: String, val title: String, val icon: ImageVector) {
    object Gallery : Pages("GalleryPage", "Gallery", Icons.Default.PhotoAlbum)

    object SignIn : Pages("SignInPage", "Sign In", Icons.Default.Login)
    object SignUp : Pages("SignUpPage", "Sign Up", Icons.Default.Person)
    object Favorites : Pages("Favorites", "Favorites", Icons.Default.Favorite)
    object ProfileManagement :
        Pages("ProfileManagement", "Profile Management", Icons.Default.Person)

    object ProfileEdit : Pages("ProfileEdit", "Edit Profile", Icons.Default.Edit)
    object SingleImagePage :
        Pages("SingleImagePage/{$SINGLE_IMAGE_PAGE_ARG}", "Image Viewer", Icons.Default.Image) {
        fun passImgId(imgId: String): String {
            return this.route.replace(oldValue = SINGLE_IMAGE_PAGE_ARG, newValue = imgId)
        }
    }
}