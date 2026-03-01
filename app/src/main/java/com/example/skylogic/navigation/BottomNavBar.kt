package com.example.skylogic.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.skylogic.models.Screen

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        Screen.Home,
        Screen.Favourite,
        Screen.Alerts,
        Screen.Settings
    )

    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        items.forEach { screen ->

            val selected = currentRoute == screen.route

            BottomItem(
                label = stringResource(screen.labelRes),
                icon = when (screen) {
                    Screen.Home -> Icons.Default.Home
                    Screen.Favourite -> Icons.Default.Favorite
                    Screen.Alerts -> Icons.Default.Notifications
                    Screen.Settings -> Icons.Default.Settings
                    else -> Icons.Default.Home
                },
                selected = selected
            ) {
                navController.navigate(screen.route) {
                    popUpTo(Screen.Home.route)
                    launchSingleTop = true
                }
            }
        }
    }
}
@Composable
fun BottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {

    val tint by animateColorAsState(
        if (selected) Color(0xFF4DA3FF) else Color.Gray,
        label = ""
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Icon(icon, null, tint = tint)
        Text(label.uppercase(), color = tint, fontSize = 12.sp)
    }
}