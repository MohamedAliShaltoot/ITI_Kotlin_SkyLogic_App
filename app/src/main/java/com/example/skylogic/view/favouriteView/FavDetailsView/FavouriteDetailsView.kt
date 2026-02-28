package com.example.skylogic.view.favouriteView.FavDetailsView

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.skylogic.R
import com.example.skylogic.view.favouriteView.FavDetailsUiState
import com.example.skylogic.view.favouriteView.FavoriteViewModel
import com.example.skylogic.utils.NetworkState
import com.example.skylogic.utils.OfflineBanner
import com.example.skylogic.view.weatherView.reusable.formatDay
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailsScreen(
    lat: Double,
    lon: Double,
    name: String,
    onBack: () -> Unit = {},
    viewModel: FavoriteViewModel = viewModel()
) {
    val networkState    by viewModel.networkState.collectAsState()
    val favDetailsState by viewModel.favDetailsState.collectAsState()

    LaunchedEffect(lat, lon) {
        viewModel.loadDetailsFor(lat, lon)
    }

    val backgroundGradient = Brush.radialGradient(
        colors = listOf(FavoriteDetailsViewColors.SkyMid, FavoriteDetailsViewColors.SkyDeep),
        center = Offset(0f, 0f),
        radius = 1800f
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier
                            .padding(start = 4.dp, end = 10.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(FavoriteDetailsViewColors.SkyAccent.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = FavoriteDetailsViewColors.SkyAccent
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = FavoriteDetailsViewColors.SkyAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = name,
                            color = FavoriteDetailsViewColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            letterSpacing = 0.3.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FavoriteDetailsViewColors.SkyDeep,
                    titleContentColor = FavoriteDetailsViewColors.TextPrimary
                ),
                modifier = Modifier.shadow(8.dp, ambientColor = FavoriteDetailsViewColors.SkyAccent.copy(alpha = 0.3f))
            )
        },
        containerColor = Color.Transparent
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AnimatedVisibility(
                    visible = networkState is NetworkState.Unavailable,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    OfflineBanner()
                }

                when (val state = favDetailsState) {

                    is FavDetailsUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(52.dp),
                                    color = FavoriteDetailsViewColors.SkyAccent,
                                    strokeWidth = 3.dp,
                                    trackColor = FavoriteDetailsViewColors.SkyAccent.copy(alpha = 0.2f)
                                )
                                Text(
                                    text = stringResource(R.string.Fetchingweather),
                                    color = FavoriteDetailsViewColors.TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    is FavDetailsUiState.NoCache -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = null,
                                    tint = FavoriteDetailsViewColors.SkyAccent,
                                    modifier = Modifier.size(52.dp)
                                )
                                Text(
                                    text = stringResource(R.string.noCachedDataAvailable),
                                    color = FavoriteDetailsViewColors.TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    is FavDetailsUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = state.message,
                                color = FavoriteDetailsViewColors.TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }

                    is FavDetailsUiState.Success -> {
                        val weather  = state.weather
                        val forecast = state.forecast
                        val today    = LocalDate.now().toString()

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            // Weather card
                            item {
                                Spacer(Modifier.height(20.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    FavoriteDetailsViewColors.SkyAccent.copy(alpha = 0.25f),
                                                    FavoriteDetailsViewColors.CardBg
                                                ),
                                                start = Offset(0f, 0f),
                                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                            )
                                        )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 28.dp, horizontal = 20.dp)
                                    ) {
                                        AsyncImage(
                                            model = "https://openweathermap.org/img/wn/${weather.icon}@4x.png",
                                            contentDescription = null,
                                            modifier = Modifier.size(130.dp)
                                        )
                                        Text(
                                            text = "${weather.temp}°C",
                                            color = FavoriteDetailsViewColors.TempGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 64.sp,
                                            letterSpacing = (-2).sp,
                                            lineHeight = 70.sp
                                        )
                                        Text(
                                            text = weather.description.replaceFirstChar { it.uppercase() },
                                            color = FavoriteDetailsViewColors.TextSecondary,
                                            fontSize = 18.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(Modifier.height(24.dp))
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            StatChip(label = stringResource(R.string.FeelsLike), value = "${weather.feelsLike}°C", modifier = Modifier.weight(1f))
                                            StatChip(label = stringResource(R.string.Humidity), value = "${weather.humidity}${stringResource(R.string.percentage)}", modifier = Modifier.weight(1f))
                                        }
                                        Spacer(Modifier.height(10.dp))
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            StatChip(label = stringResource(R.string.Wind), value = "${weather.windSpeed} ${stringResource(R.string.MeterPerSecond)}", modifier = Modifier.weight(1f))
                                            StatChip(label = stringResource(R.string.Pressure), value = "${weather.pressure} ${stringResource(R.string.hPa)}", modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                                Spacer(Modifier.height(28.dp))
                            }

                            // Today section
                            val todayList = forecast.filter { it.dt_txt.startsWith(today) }
                            if (todayList.isNotEmpty()) {
                                item { SectionHeader(title = stringResource(R.string.Today)) }
                                items(todayList) { ForecastCard(it) }
                                item { Spacer(Modifier.height(8.dp)) }
                            }

                            // Upcoming days
                            forecast.groupBy { it.dt_txt.substring(0, 10) }
                                .filterKeys { it != today }
                                .forEach { (date, list) ->
                                    item { SectionHeader(title = formatDay(date)) }
                                    items(list) { ForecastCard(it) }
                                    item { Spacer(Modifier.height(8.dp)) }
                                }
                        }
                    }
                }
            }
        }
    }
}



