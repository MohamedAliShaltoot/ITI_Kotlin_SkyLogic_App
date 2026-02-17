package com.example.skylogic

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.skylogic.models.CurrentWeatherResponse
import com.example.skylogic.models.ForecastItem
import com.example.skylogic.ui.theme.SkyLogicTheme
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkyLogicTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen()
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {

    val navController = rememberNavController()

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                locationHelper.getCurrentLocation { location ->
                    location?.let {
                        viewModel.fetchWeather(it.latitude, it.longitude)
                    }
                }
            }
        }

    val savedLat = settingsViewModel.lat.collectAsState().value
    val savedLon = settingsViewModel.lon.collectAsState().value
    val locationMode = settingsViewModel.locationMode.collectAsState().value

    LaunchedEffect(savedLat, savedLon, locationMode) {

        if (locationMode == "Map" && savedLat != null && savedLon != null) {

            viewModel.fetchWeather(savedLat, savedLon)

        } else if (locationMode == "GPS") {

            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                WeatherContent(
                    viewModel = viewModel,
                    settingsViewModel = settingsViewModel
                )
            }
            composable(Screen.MapSelection.route) {
                MapSelectionScreen(
                    navController = navController,
                    settingsViewModel = settingsViewModel,
                    viewModel = viewModel

                )
            }

            composable(Screen.Radar.route) {
                RadarScreen()
            }

            composable(Screen.Alerts.route) {
                AlertsScreen()
            }
            composable(Screen.Settings.route) {
            SettingsScreen(
                weatherViewModel = viewModel,
                viewModel = settingsViewModel,
                navController = navController


            )
        }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherContent(
    viewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel
)

{

    val weather = viewModel.currentWeather
    val hourly = viewModel.getHourlyData()
    val daily = viewModel.getDailyData()
    val condition = weather?.weather?.firstOrNull()?.description
    val targetGradient = getWeatherGradient(condition)
    val windUnit = settingsViewModel.windUnit.collectAsState().value

    val animatedColors = targetGradient.map { targetColor ->
        animateColorAsState(targetColor, label = "").value
    }

    Scaffold(
        containerColor = Color.Transparent,

    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(animatedColors)
                )

                .padding(innerPadding)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Spacer(Modifier.height(16.dp))
            }

            weather?.let {

                item {
                    HeaderSection(it)


                    Spacer(Modifier.height(24.dp))
                }

                item {
                   // CurrentWeatherSection(it)
                    CurrentWeatherSection(it, windUnit)

                    Spacer(Modifier.height(28.dp))
                }
            }
//            items(hourly) { item ->
//                ExpandableHourlyCard(item)
//            }
            item {

                Text(
                    "HOURLY FORECAST",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                )

                Spacer(Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(hourly) { item ->
                        ExpandableHourlyCard(item)
                    }
                }

                Spacer(Modifier.height(28.dp))
            }
            item {
                SevenDayForecastSection(daily)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
@Composable
fun RotatingWindArrow(degrees: Int) {

    val rotation by animateFloatAsState(
        targetValue = degrees.toFloat(),
        label = ""
    )

    Icon(
        imageVector = Icons.Default.Navigation,
        contentDescription = "Wind Direction",
        tint = Color(0xFF4DA3FF),
        modifier = Modifier
            .size(24.dp)
            .rotate(rotation)
    )
}

@Composable
fun ExpandableHourlyCard(item: ForecastItem) {

    var expanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .width(200.dp)
            .animateContentSize()
            .clickable { expanded = !expanded }
    ) {

        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                item.dt_txt.substringAfter(" ").substring(0,5),
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            AsyncImage(
                model = "https://openweathermap.org/img/wn/${item.weather.first().icon}@2x.png",
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Text(
                "${item.main.temp.toInt()}°",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Feels ${item.main.feels_like.toInt()}°",
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "Rain ${(item.pop * 100).toInt()}%",
                color = Color.Cyan,
                fontSize = 12.sp
            )

            if (expanded) {

                Spacer(Modifier.height(12.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))

                WeatherInfoItem("Humidity", "${item.main.humidity}%")
                WeatherInfoItem("Pressure", "${item.main.pressure} hPa")
                WeatherInfoItem("Clouds", "${item.clouds.all}%")
                WeatherInfoItem("Visibility", "${item.visibility / 1000} km")
                WeatherInfoItem("Wind Speed", "${item.wind.speed} m/s")
                WeatherInfoItem("Wind Direction", "${item.wind.deg}°")
                item.wind.gust?.let {
                    WeatherInfoItem("Wind Gust", "$it m/s")
                }
            }
        }
    }
}
@Composable
fun HeaderSection(weather: CurrentWeatherResponse) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(Modifier.width(6.dp))

            Text(
                "${weather.name}, ${weather.sys.country}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = formatFullDateTime(weather.dt, weather.timezone),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = "https://openweathermap.org/img/wn/${weather.weather.first().icon}@2x.png",
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                weather.weather.first().description.replaceFirstChar { it.uppercase() },
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        Divider(color = Color.White.copy(alpha = 0.2f))

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            MiniHeaderInfo("Visibility", "${weather.visibility / 1000} km")
            MiniHeaderInfo("Clouds", weather.weather.first().main)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    "Wind",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {

                    RotatingWindArrow(weather.wind.deg)

                    Spacer(Modifier.width(4.dp))

                    Text(
                        "${weather.wind.deg}°",
                        color = Color(0xFF4DA3FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
fun formatFullDateTime(timestamp: Long, timezone: Int): String {
    val date = Date((timestamp + timezone) * 1000)
    val sdf = SimpleDateFormat("EEE, dd MMM • hh:mm a", Locale.getDefault())
    return sdf.format(date)
}
@Composable
fun MiniHeaderInfo(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp
        )
        Text(
            value,
            color = Color(0xFF4DA3FF),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@Composable
fun CurrentWeatherSection(
    weather: CurrentWeatherResponse,
    windUnit: String,
) {

    val windSpeed = convertWindSpeed(weather.wind.speed, windUnit)

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "${weather.main.temp.toInt()}°",
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.width(16.dp))

                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${weather.weather.first().icon}@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                weather.weather.first().description.replaceFirstChar { it.uppercase() },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp
            )

            Text(
                "Feels like ${weather.main.feels_like.toInt()}°",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            Divider(color = Color.White.copy(alpha = 0.2f))

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherInfoItem("High", "${weather.main.temp_max.toInt()}°")
                WeatherInfoItem("Low", "${weather.main.temp_min.toInt()}°")
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherInfoItem("Humidity", "${weather.main.humidity}%")
                WeatherInfoItem(
                    "Wind",
                    "${windSpeed.toInt()} ${if (windUnit == "miles/hour") "mph" else "m/s"}"
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherInfoItem("Pressure", "${weather.main.pressure} hPa")
                WeatherInfoItem("Sunrise", formatUnixTime(weather.sys.sunrise))
            }

            Spacer(Modifier.height(12.dp))

            WeatherInfoItem("Sunset", formatUnixTime(weather.sys.sunset))
        }
    }
}
@Composable
fun WeatherInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Color(0xFF4DA3FF),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun formatUnixTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpandableDailyCard(
    date: String,
    dayItems: List<ForecastItem>
) {

    var expanded by remember { mutableStateOf(false) }

    val maxTemp = dayItems.maxOf { it.main.temp }
    val minTemp = dayItems.minOf { it.main.temp }
    val avgHumidity = dayItems.map { it.main.humidity }.average()
    val avgWind = dayItems.map { it.wind.speed }.average()
    val maxRain = dayItems.maxOf { it.pop } * 100
    val icon = dayItems.first().weather.first().icon

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded }
    ) {

        Column(Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    formatDay(date),
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold
                )

                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${icon}@2x.png",
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "H ${maxTemp.toInt()}°  L ${minTemp.toInt()}°",
                        color = Color.White
                    )
                    Text(
                        "Rain ${maxRain.toInt()}%",
                        color = Color.Cyan,
                        fontSize = 12.sp
                    )
                }
            }

            if (expanded) {

                Spacer(Modifier.height(12.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))

                WeatherInfoItem("Avg Humidity", "${avgHumidity.toInt()}%")
                WeatherInfoItem("Avg Wind", "${avgWind.toInt()} m/s")
                WeatherInfoItem("Pressure Range",
                    "${dayItems.minOf { it.main.pressure }} - ${dayItems.maxOf { it.main.pressure }} hPa"
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SevenDayForecastSection(
    daily: Map<String, List<ForecastItem>>
) {

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            "5-DAY FORECAST",
            color = Color.Gray,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )

        daily.entries.take(5).forEach { entry ->

            val dayItems = entry.value
            ExpandableDailyCard(entry.key, dayItems)
        }
    }
}


@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        Screen.Home,
        Screen.Radar,
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
                label = screen.route.replaceFirstChar { it.uppercase() },
                icon = when (screen) {
                    Screen.Home -> Icons.Default.Home
                    Screen.Radar -> Icons.Default.Search
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



@RequiresApi(Build.VERSION_CODES.O)
fun formatDay(dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(dateString, formatter)
    return date.dayOfWeek.getDisplayName(
        TextStyle.SHORT,
        Locale.getDefault()
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun getWeatherGradient(condition: String?): List<Color> {

    return when (condition?.lowercase()) {

        "clear sky" -> listOf(
//            Color(0xFF4DA3FF),
//            Color(0xFF1E88E5),
//            Color(0xFF1565C0)
            Color(0xFF0B1C2D),
            Color(0xFF0E2236),
            Color(0xFF0A1A2A)
        )

        "few clouds", "scattered clouds", "broken clouds" -> listOf(
            Color(0xFF78909C),
            Color(0xFF546E7A),
            Color(0xFF37474F)
        )

        "overcast clouds" -> listOf(
            Color(0xFF616161),
            Color(0xFF424242),
            Color(0xFF303030)
        )

        "light rain", "moderate rain", "heavy intensity rain" -> listOf(
            Color(0xFF263238),
            Color(0xFF37474F),
            Color(0xFF102027)
        )

        "thunderstorm" -> listOf(
            Color(0xFF2C2C54),
            Color(0xFF1B1464),
            Color(0xFF0F0F3E)
        )

        "snow" -> listOf(
            Color(0xFFE3F2FD),
            Color(0xFFBBDEFB),
            Color(0xFF90CAF9)
        )

        "mist", "haze", "fog" -> listOf(
            Color(0xFFCFD8DC),
            Color(0xFFB0BEC5),
            Color(0xFF90A4AE)
        )

        else -> listOf(
            Color(0xFF0B1C2D),
            Color(0xFF0E2236),
            Color(0xFF0A1A2A)
        )
    }
}
@Composable
fun RadarScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Radar Screen", color = Color.White)
    }
}

@Composable
fun AlertsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Alerts Screen", color = Color.White)
    }
}
@Composable
fun SettingSectionTitle(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 12.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4DA3FF),
            modifier = Modifier.size(22.dp)
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun RadioGroup(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {

    Column {
        options.forEach { option ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(option) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF4DA3FF),
                        unselectedColor = Color.White.copy(alpha = 0.6f)
                    )
                )


                Spacer(Modifier.width(8.dp))
                Text(
                    option,
                    color = if (option == selected)
                        Color(0xFF4DA3FF)
                    else
                        Color.White
                )

            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    weatherViewModel: WeatherViewModel,
    viewModel: SettingsViewModel = viewModel(),
    navController: NavController
) {

    val locationMode = viewModel.locationMode.collectAsState().value
    val tempUnit = viewModel.tempUnit.collectAsState().value
    val windUnit = viewModel.windUnit.collectAsState().value
    val language = viewModel.language.collectAsState().value
    val condition = weatherViewModel.currentWeather
        ?.weather?.firstOrNull()?.description

    val targetGradient = getWeatherGradient(condition)

    val animatedColors = targetGradient.map { targetColor ->
        animateColorAsState(targetColor, label = "").value
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(animatedColors))
            .padding(16.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            ScreenTitle("Settings")
        }

        // 🔹 LOCATION
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = "Location",
                    icon = Icons.Default.LocationOn
                )
                RadioGroup(
                    options = listOf("GPS", "Map"),
                    selected = locationMode,
                    onSelect = { selected ->
                        viewModel.setLocationMode(selected)

                        if (selected == "Map") {
                            navController.navigate(Screen.MapSelection.route)
                        }

                    }
                )


            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = "Temperature Unit",
                    icon = Icons.Default.Cloud
                )


                RadioGroup(
                    options = listOf("Kelvin", "Celsius", "Fahrenheit"),
                    selected = tempUnit,
                    onSelect = { selected ->

                        viewModel.setTempUnit(selected)

                        val apiUnit = when (selected) {
                            "Celsius" -> "metric"
                            "Fahrenheit" -> "imperial"
                            "Kelvin" -> "standard"
                            else -> "metric"
                        }

                        val currentWeather = weatherViewModel.currentWeather

                        currentWeather?.let {
                            weatherViewModel.fetchWeather(
                                lat = it.coord.lat,
                                lon = it.coord.lon,
                                units = apiUnit,
                                lang = if (language == "Arabic") "ar" else "en"
                            )
                        }
                    }
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = "Wind Speed Unit",
                    icon = Icons.Default.Air
                )


                RadioGroup(
                    options = listOf("meter/sec", "miles/hour"),
                    selected = windUnit,
                    onSelect = { viewModel.setWindUnit(it) }
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {

                SettingSectionTitle(
                    title = "Language",
                    icon = Icons.Default.Language
                )


                RadioGroup(
                    options = listOf("English", "Arabic"),
                    selected = language,
                    onSelect = { selected ->

                        viewModel.setLanguage(selected)

                        val apiLang =
                            if (selected == "Arabic") "ar" else "en"

                        val apiUnit = when (tempUnit) {
                            "Celsius" -> "metric"
                            "Fahrenheit" -> "imperial"
                            "Kelvin" -> "standard"
                            else -> "metric"
                        }

                        val currentWeather =
                            weatherViewModel.currentWeather

                        currentWeather?.let {
                            weatherViewModel.fetchWeather(
                                lat = it.coord.lat,
                                lon = it.coord.lon,
                                units = apiUnit,
                                lang = apiLang
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ScreenTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

fun convertWindSpeed(speed: Double, unit: String): Double {
    return when (unit) {
        "miles/hour" -> speed * 2.237
        else -> speed // meter/sec
    }
}
