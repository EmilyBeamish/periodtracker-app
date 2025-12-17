package com.example.emilybeamish_sd3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.emilybeamish_sd3.ui.theme.DarkPurple1
import com.example.emilybeamish_sd3.ui.theme.Emilybeamish_SD3Theme
import com.example.emilybeamish_sd3.ui.theme.ThemeState
import com.example.emilybeamish_sd3.ui.theme.rememberThemeState
import com.example.emilybeamish_sd3.ui.theme.DarkPurple1
import com.example.emilybeamish_sd3.ui.theme.Emilybeamish_SD3Theme
import com.example.emilybeamish_sd3.ui.theme.LightPurple1
import com.example.emilybeamish_sd3.ui.theme.Pink1
import com.example.emilybeamish_sd3.ui.theme.ThemeType
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeState = rememberThemeState()
            Emilybeamish_SD3Theme(themeType = themeState.currentTheme) {
                PeriodTrackerApp(themeState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodTrackerApp(themeState: ThemeState) {
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar(navController) },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = "Home"
            ) {
                composable("Home") { HomeScreen(navController) }
                composable("AddPeriod") { AddPeriodScreen(navController) }
                composable("PeriodHistory") { PeriodHistoryScreen() }
                composable("Statistics") { StatisticsScreen() }
                composable("Settings") { SettingsScreen(themeState) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "App Icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text("Period Tracker")

            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("Settings") }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings Icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            IconButton(onClick = { /* Notifications */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications Icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        )
}

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        NavigationBarItem(
            selected = currentRoute == "Home",
            onClick = { navController.navigate("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home Icon",
                tint = if (currentRoute == "Home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            ) },
            label = { Text("Home",
                color = if (currentRoute == "Home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

            )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )

        NavigationBarItem(
            selected = currentRoute == "AddPeriod",
            onClick = { navController.navigate("AddPeriod") },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Add Period Icon",
                tint = if (currentRoute == "AddPeriod") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                ) },
            label = { Text("Add Period",
                color = if (currentRoute == "AddPeriod") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

                ) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )

            )

        NavigationBarItem(
            selected = currentRoute == "PeriodHistory",
            onClick = { navController.navigate("PeriodHistory") },
            icon = { Icon(Icons.Default.List, contentDescription = "History Icon",
                tint = if (currentRoute == "PeriodHistory") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                ) },
            label = { Text("History",
                color = if (currentRoute == "PeriodHistory") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                ) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )

        )
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Welcome!",
                    color = MaterialTheme.colorScheme.onSurface

                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Track your menstrual cycle with ease",
                color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Current Status Card
        CurrentStatusCard()

        // Add Period Button
        Button(
            onClick = { navController.navigate("AddPeriod") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Period Icon",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Period", color = Color.White)
        }

        // Last Period Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Last Period",
                    color = MaterialTheme.colorScheme.onSurfaceVariant

                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("10/12/2025",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
        }
    }
}

@Composable
fun CurrentStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Current Status",
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Days until next period: 14",
                color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            Text("Average Cycle Length: 28 days",
                color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
    }
}

@Composable
fun AddPeriodScreen(navController: NavController) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Add New Period",
            color = MaterialTheme.colorScheme.onSurface
        )

        // Start Date Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Select Dates (start date)",
                    color = MaterialTheme.colorScheme.onSurface
                    )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("DD/MM/YYYY") },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Flow Type Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Flow Type",
                    color = MaterialTheme.colorScheme.onSurface
                    )
                Spacer(modifier = Modifier.height(8.dp))
                FlowSelector()
            }
        }

        // Notes Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Notes",
                    color = MaterialTheme.colorScheme.onSurface
                    )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text("Enter any additional notes here") },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Save Period")
        }
    }
}

@Composable
fun FlowSelector() {
    var selectedType by remember { mutableStateOf(2) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 1..5) {
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { selectedType = i },


            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "*".repeat(i),
                    )
                }
            }
        }
    }
}

@Composable
fun PeriodHistoryScreen() {
    val periods = listOf(
        Period("10/12/2025", "15/12/2025", "Normal flow"),
        Period("15/11/2025", "20/11/2025", "Heavy flow with cramps"),
        Period("20/10/2025", "25/10/2025", "Light flow"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Period History",
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(periods) { period ->
                PeriodCard(period)
            }
        }
    }
}

@Composable
fun PeriodCard(period: Period) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Start Date: ${period.startDate}",
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text("End Date: ${period.endDate}",
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Notes: ${period.notes}",
                color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

data class Period(
    val startDate: String,
    val endDate: String,
    val notes: String
)

@Composable
fun StatisticsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Statistics",

        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Average Cycle Length: 28 days")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Average Period Duration: 5 days")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Most Common Flow Type: Normal")
            }
        }
    }
}

@Composable
fun SettingsScreen(themeState: ThemeState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Theme Settings",
                    style = MaterialTheme.typography.titleMedium
                )

                // Default Theme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { themeState.currentTheme = ThemeType.DEFAULT }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                MaterialTheme.colorScheme.primary
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Default Theme")
                    Spacer(modifier = Modifier.weight(1f))
                    if (themeState.currentTheme == ThemeType.DEFAULT) {
                        Icon(Icons.Default.Check, contentDescription = "Selected")
                    }
                }

                // Pink Theme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { themeState.currentTheme = ThemeType.PINK }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Pink1)  // Use Pink1 from theme
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pink Theme")
                    Spacer(modifier = Modifier.weight(1f))
                    if (themeState.currentTheme == ThemeType.PINK) {
                        Icon(Icons.Default.Check, contentDescription = "Selected")
                    }
                }

                // Light Purple Theme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { themeState.currentTheme = ThemeType.LIGHT_PURPLE }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(LightPurple1)  // Use LightPurple1 from theme
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Light Purple Theme")
                    Spacer(modifier = Modifier.weight(1f))
                    if (themeState.currentTheme == ThemeType.LIGHT_PURPLE) {
                        Icon(Icons.Default.Check, contentDescription = "Selected")
                    }
                }

                // Dark Purple Theme
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { themeState.currentTheme = ThemeType.DARK_PURPLE }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(DarkPurple1)  // Use DarkPurple1 from theme
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dark Purple Theme")
                    Spacer(modifier = Modifier.weight(1f))
                    if (themeState.currentTheme == ThemeType.DARK_PURPLE) {
                        Icon(Icons.Default.Check, contentDescription = "Selected")
                    }
                }
            }
        }
    }
}