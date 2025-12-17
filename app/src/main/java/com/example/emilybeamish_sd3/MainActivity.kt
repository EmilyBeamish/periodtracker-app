package com.example.emilybeamish_sd3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Favorite
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



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PeriodTrackerApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodTrackerApp() {
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
                composable("Settings") { SettingsScreen() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    androidx.compose.material3.TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "App Icon",
                    tint = Color.White
                )
                Text("Period Tracker")
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("Settings") }) {
                Text("⚙️")
            }
            IconButton(onClick = { /* Notifications */ }) {
                Text("🔔")
            }
        }

        )

}

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
    ) {
        NavigationBarItem(
            selected = currentRoute == "Home",
            onClick = { navController.navigate("Home") },
            icon = { Text("H")},
            label = { Text("Home") }
            )

        NavigationBarItem(
            selected = currentRoute == "AddPeriod",
            onClick = { navController.navigate("AddPeriod") },
            icon = { Text("+") },
            label = { Text("Add Period") },

            )

        NavigationBarItem(
            selected = currentRoute == "PeriodHistory",
            onClick = { navController.navigate("PeriodHistory") },
            icon = { Text("#") },
            label = { Text("History") },

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

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Welcome!",

                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Track your menstrual cycle with ease")
            }
        }

        // Current Status Card
        CurrentStatusCard()

        // Add Period Button
        Button(
            onClick = { navController.navigate("AddPeriod") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

        ) {
            Text("+", color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Period", color = Color.White)
        }

        // Last Period Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Last Period",

                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("10/12/2025")
            }
        }
    }
}

@Composable
fun CurrentStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Current Status",

            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Days until next period: 14")
            Text("Average Cycle Length: 28 days")
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

        )

        // Start Date Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Select Dates (start date)")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("DD/MM/YYYY") }
                )
            }
        }

        // Flow Type Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Flow Type")
                Spacer(modifier = Modifier.height(8.dp))
                FlowSelector()
            }
        }

        // Notes Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Notes")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text("Enter any additional notes here") }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

        ) {
            Text("Save Period", color = Color.White)
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

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Start Date: ${period.startDate}")
            Spacer(modifier = Modifier.height(4.dp))
            Text("End Date: ${period.endDate}")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Notes: ${period.notes}")
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
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Settings",

        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Notification Preferences")
                Text("Data Backup")
                Text("Theme Selection")
            }
        }
    }
}