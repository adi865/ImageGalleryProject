package com.example.imagegalleryproject.BottomBar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.imagegalleryproject.screens.Pages

@Composable
fun BottomBar(navController: NavController) {
    val bottomNavigationItems = listOf(
        Pages.Gallery,
        Pages.Favorites,
        Pages.ProfileEdit,
        Pages.ProfileManagement
    )
    androidx.compose.material.BottomAppBar(
        elevation = 16.dp,
        backgroundColor = Color.White,
        cutoutShape = CircleShape,
        modifier = Modifier
            .height(48.dp)
            .padding(start = 3.dp, end = 3.dp, bottom = 5.dp)
            .clip(RoundedCornerShape(15.dp))
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.arguments?.getString(Pages.Gallery.route)
        BottomNavigation(
            backgroundColor = Color.Transparent, elevation = 0.dp
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val weightLeft = 1f / 2 // Two items on the left side
                val weightRight = 1f / 2 // Two items on the right side
                bottomNavigationItems.subList(0, 2).forEach { screen ->
                    BottomNavigationItem(selected = (currentRoute == screen.route),
                        icon = {
                            Icon(
                                screen.icon, screen.route, tint = Color.DarkGray
                            )
                        },
                        modifier = Modifier.weight(weightLeft),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo = navController.graph.getStartDestination()
                                launchSingleTop = true
                            }
                        })
                }

                Spacer(Modifier.weight(0.5f))

                bottomNavigationItems.subList(2, bottomNavigationItems.size)
                    .forEach { screen ->
                        BottomNavigationItem(selected = (currentRoute == screen.route),
                            icon = {
                                Icon(
                                    screen.icon, screen.route, tint = Color.DarkGray
                                )
                            },
                            modifier = Modifier.weight(weightRight),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo = navController.graph.getStartDestination()
                                    launchSingleTop = true
                                }
                            })
                    }
            }
        }
    }
}