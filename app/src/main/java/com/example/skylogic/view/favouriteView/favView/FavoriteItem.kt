package com.example.skylogic.view.favouriteView.favView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skylogic.data.local.CachedWeatherEntity
import com.example.skylogic.data.local.FavoriteEntity

@Composable
fun FavoriteItem(
    favorite: FavoriteEntity,
    weather: CachedWeatherEntity?,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onAppear: () -> Unit
) {
    LaunchedEffect(favorite.lat, favorite.lon) { onAppear() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = FavouriteViewColors.SkyAccent.copy(alpha = 0.2f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(FavouriteViewColors.CardBg, FavouriteViewColors.SkyDeep),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                )
                // subtle left accent stripe
                .then(
                    Modifier.background(
                        Brush.horizontalGradient(
                            colors = listOf(FavouriteViewColors.SkyAccent.copy(alpha = 0.15f), Color.Transparent),
                            startX = 0f,
                            endX = 80f
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left accent dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(FavouriteViewColors.SkyAccent, FavouriteViewColors.SkyLight))
                        )
                )

                Spacer(Modifier.width(14.dp))

                // Text info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = FavouriteViewColors.SkyAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = favorite.name,
                            color = FavouriteViewColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    if (weather != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "${weather.temp}°C",
                                color = FavouriteViewColors.TempGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "·",
                                color = FavouriteViewColors.TextSecondary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = weather.description.replaceFirstChar { it.uppercase() },
                                color = FavouriteViewColors.TextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Loading weather…",
                            color = FavouriteViewColors.TextSecondary.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                }

                // Weather icon
                if (weather != null) {
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${weather.icon}@2x.png",
                        contentDescription = null,
                        modifier = Modifier.size(56.dp)
                    )
                }

                // Delete button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FavouriteViewColors.DeleteRed.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = FavouriteViewColors.DeleteRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}