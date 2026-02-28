package com.example.skylogic.view.favouriteView.FavDetailsView

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skylogic.models.ForecastItem
import com.example.skylogic.utils.UnitConverter
import com.example.skylogic.utils.UnitSymbol
import com.example.skylogic.view.weatherView.reusable.formatUnixTime

@Composable
fun ForecastCard(item: ForecastItem , tempUnit: String = "Celsius") {
    val formattedTime = formatUnixTime(item.dt)
    val tSymbol = UnitSymbol.temp(tempUnit)
    val displayTemp = UnitConverter.convertTemp(item.main.temp, tempUnit)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(FavoriteDetailsViewColors.CardBg, FavoriteDetailsViewColors. SkyDeep),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, 0f)
                )
            )
    ) {
        // subtle left accent bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(4.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                .background(
                    Brush.linearGradient(listOf(FavoriteDetailsViewColors.SkyAccent, FavoriteDetailsViewColors.SkyLight))
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Time + description
            Column {
                Text(
                    text = formattedTime,
                    color = FavoriteDetailsViewColors.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = item.weather[0].description.replaceFirstChar { it.uppercase() },
                    color = FavoriteDetailsViewColors.TextSecondary,
                    fontSize = 12.sp
                )
            }

            // Icon + temp
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${item.weather[0].icon}@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(46.dp)
                )
                Text(
                    text = "${displayTemp.toInt()}$tSymbol",
                    color = FavoriteDetailsViewColors. TempGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = (-0.5).sp
                )
            }
        }
    }
}