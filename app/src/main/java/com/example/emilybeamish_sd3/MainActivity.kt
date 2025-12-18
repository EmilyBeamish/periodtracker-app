package com.example.emilybeamish_sd3

import android.content.Context
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.emilybeamish_sd3.ui.theme.DarkPurple1
import com.example.emilybeamish_sd3.ui.theme.Emilybeamish_SD3Theme
import com.example.emilybeamish_sd3.ui.theme.LightPurple1
import com.example.emilybeamish_sd3.ui.theme.Pink1
import com.example.emilybeamish_sd3.ui.theme.ThemeState
import com.example.emilybeamish_sd3.ui.theme.ThemeType
import com.example.emilybeamish_sd3.ui.theme.rememberThemeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

// DATABASE
@Entity(tableName = "periods")
data class PeriodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: String,
    val endDate: String,
    val flowType: Int = 2,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface PeriodDao {
    @Query("SELECT * FROM periods ORDER BY createdAt DESC")
    fun getAllPeriods(): Flow<List<PeriodEntity>>

    @Insert
    suspend fun insertPeriod(period: PeriodEntity)

    @Delete
    suspend fun deletePeriod(period: PeriodEntity)

    @Query("SELECT * FROM periods WHERE :currentDate BETWEEN startDate AND endDate")
    suspend fun getCurrentPeriod(currentDate: String): PeriodEntity?

    @Query("SELECT * FROM periods ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastPeriod(): PeriodEntity?

    @Query("SELECT AVG(julianday(endDate) - julianday(startDate)) FROM periods")
    suspend fun getAveragePeriodDuration(): Double?

    @Query("SELECT flowType FROM periods GROUP BY flowType ORDER BY COUNT(*) DESC LIMIT 1")
    suspend fun getMostCommonFlowType(): Int?

    @Query("SELECT COUNT(*) FROM periods WHERE flowType = :flowType")
    suspend fun getCountForFlowType(flowType: Int): Int

    @Query("DELETE FROM periods")
    suspend fun clearAllPeriods()
}

// EVERY DATA CLASS USED
data class FlowTypeCount(
    val flowType: Int,
    val count: Int
)

data class Period(
    val startDate: String,
    val endDate: String,
    val notes: String
)

@Database(entities = [PeriodEntity::class], version = 1)
abstract class PeriodDatabase : RoomDatabase() {
    abstract fun periodDao(): PeriodDao

    companion object {
        @Volatile
        private var INSTANCE: PeriodDatabase? = null

        fun getDatabase(context: Context): PeriodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PeriodDatabase::class.java,
                    "period_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// REPOSITORY
class PeriodRepository(private val periodDao: PeriodDao) {
    val allPeriods: Flow<List<PeriodEntity>> = periodDao.getAllPeriods()

    suspend fun insert(period: PeriodEntity) {
        periodDao.insertPeriod(period)
    }

    suspend fun delete(period: PeriodEntity) {
        periodDao.deletePeriod(period)
    }

    suspend fun getLastPeriod(): PeriodEntity? {
        return periodDao.getLastPeriod()
    }

    suspend fun getAveragePeriodDuration(): Double? {
        return periodDao.getAveragePeriodDuration()
    }

    suspend fun getMostCommonFlowType(): Int? {
        return periodDao.getMostCommonFlowType()
    }

    suspend fun clearAllPeriods() {
        periodDao.clearAllPeriods()
    }
}

// VIEWMODEL
class PeriodViewModel(private val repository: PeriodRepository) : ViewModel() {
    val allPeriods: Flow<List<PeriodEntity>> = repository.allPeriods

    suspend fun addPeriod(period: PeriodEntity) = withContext(Dispatchers.IO) {
        repository.insert(period)
    }

    suspend fun deletePeriod(period: PeriodEntity) = withContext(Dispatchers.IO) {
        repository.delete(period)
    }

    suspend fun getLatestPeriod(): PeriodEntity? = withContext(Dispatchers.IO) {
        repository.getLastPeriod()
    }

    suspend fun getAveragePeriodDuration(): Double? = withContext(Dispatchers.IO) {
        repository.getAveragePeriodDuration()
    }

    suspend fun getMostCommonFlowType(): Int? = withContext(Dispatchers.IO) {
        repository.getMostCommonFlowType()
    }

    suspend fun clearAllPeriods() = withContext(Dispatchers.IO) {
        repository.clearAllPeriods()
    }
}

//Factory for ViewModel
class ViewModelFactory(private val repository: PeriodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PeriodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PeriodViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

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
                Spacer(modifier = Modifier.width(8.dp))
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
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home Icon",
                    tint = if (currentRoute == "Home") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    "Home",
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
            icon = {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = "Add Period Icon",
                    tint = if (currentRoute == "AddPeriod") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    "Add Period",
                    color = if (currentRoute == "AddPeriod") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
            selected = currentRoute == "PeriodHistory",
            onClick = { navController.navigate("PeriodHistory") },
            icon = {
                Icon(
                    Icons.Default.List,
                    contentDescription = "History Icon",
                    tint = if (currentRoute == "PeriodHistory") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    "History",
                    color = if (currentRoute == "PeriodHistory") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { PeriodRepository(PeriodDatabase.getDatabase(context).periodDao()) }
    val factory = remember { ViewModelFactory(repository) }
    val viewModel: PeriodViewModel = viewModel(factory = factory)

    val periods by viewModel.allPeriods.collectAsState(initial = emptyList())
    var currentPeriod by remember { mutableStateOf<PeriodEntity?>(null) }
    var currentDayofPeriod by remember { mutableStateOf<Int?>(null) }
    var latestPeriod by remember { mutableStateOf<PeriodEntity?>(null) }

    LaunchedEffect(periods) {
        val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        currentPeriod = viewModel.getCurrentPeriod(todayDate)
        latestPeriod = viewModel.getLatestPeriod()
    }

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
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Track your menstrual cycle with ease",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Current Status Card
        CurrentStatusCard(periods)

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
        if (latestPeriod != null) {
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
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${latestPeriod!!.startDate} - ${latestPeriod!!.endDate}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (latestPeriod!!.notes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Notes: ${latestPeriod!!.notes}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
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
                        "No periods logged yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
fun calculateEndDate(startDate: String, periodLength: Int): String {
    // Simple date calculation assuming format "DD/MM/YYYY"
    val parts = startDate.split("/").map { it.toInt() }
    var day = parts[0]
    var month = parts[1]
    var year = parts[2]

    day += periodLength
    val daysInMonth = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0) 29 else 28
        else -> 30
    }

    if (day > daysInMonth) {
        day -= daysInMonth
        month += 1
        if (month > 12) {
            month = 1
            year += 1
        }
    }

    return String.format("%02d/%02d/%04d", day, month, year)
}

@Composable
fun CurrentStatusCard(periods: List<PeriodEntity>) {
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (periods.isEmpty()) {
                Text(
                    "No periods logged yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    "You have logged ${periods.size} periods.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddPeriodScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { PeriodRepository(PeriodDatabase.getDatabase(context).periodDao()) }
    val factory = remember { ViewModelFactory(repository) }
    val viewModel: PeriodViewModel = viewModel(factory = factory)

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedFlowType by remember { mutableStateOf(3) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Add New Period",
            style = MaterialTheme.typography.headlineSmall,
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
                Text(
                    "Select Dates",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date (DD/MM/YYYY)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("DD/MM/YYYY") },
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date (DD/MM/YYYY)") },
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
                Text(
                    "Flow Type",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowSelector(
                    selectedType = selectedFlowType,
                    onTypeSelected = { selectedFlowType = it }
                )
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
                Text(
                    "Notes",
                    style = MaterialTheme.typography.titleMedium,
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
            onClick = {
                if (startDate.isNotBlank() && endDate.isNotBlank()) {
                    // Create new period
                    val newPeriod = PeriodEntity(
                        startDate = startDate,
                        endDate = endDate,
                        flowType = selectedFlowType,
                        notes = notes
                    )
                    viewModel.viewModelScope.launch {
                        viewModel.addPeriod(newPeriod)
                        // Navigate back
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = startDate.isNotBlank() && endDate.isNotBlank(),
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
fun FlowSelector(
    selectedType: Int,
    onTypeSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 1..5) {
            Card(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onTypeSelected(i) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedType == i) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "*".repeat(i),
                        color = if (selectedType == i) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PeriodHistoryScreen() {
    val context = LocalContext.current
    val repository = remember { PeriodRepository(PeriodDatabase.getDatabase(context).periodDao()) }
    val factory = remember { ViewModelFactory(repository) }
    val viewModel: PeriodViewModel = viewModel(factory = factory)

    val periods by viewModel.allPeriods.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Period History",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (periods.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No periods recorded yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(periods) { period ->
                    PeriodCard(
                        period = period,
                        onDelete = {
                            viewModel.viewModelScope.launch {
                                viewModel.deletePeriod(period)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PeriodCard(
    period: PeriodEntity,
    onDelete: () -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "${period.startDate} - ${period.endDate}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Flow: ${"*".repeat(period.flowType)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (period.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Notes: ${period.notes}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val repository = remember { PeriodRepository(PeriodDatabase.getDatabase(context).periodDao()) }
    val factory = remember { ViewModelFactory(repository) }
    val viewModel: PeriodViewModel = viewModel(factory = factory)

    val periods by viewModel.allPeriods.collectAsState(initial = emptyList())

    // Calculate statistics
    var averageDuration by remember { mutableStateOf<Double?>(null) }
    var mostCommonFlow by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(periods) {
        if (periods.isNotEmpty()) {
            averageDuration = viewModel.getAveragePeriodDuration()
            mostCommonFlow = viewModel.getMostCommonFlowType()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Statistics",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

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
                if (periods.isEmpty()) {
                    Text(
                        "Add periods to see statistics",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        "Total periods: ${periods.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (averageDuration != null) {
                        Text(
                            "Average duration: ${String.format("%.1f", averageDuration)} days",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (mostCommonFlow != null) {
                        Text(
                            "Most common flow: ${"*".repeat(mostCommonFlow!!)}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (periods.size >= 2) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Keep tracking for cycle predictions!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(themeState: ThemeState) {
    val context = LocalContext.current
    val repository = remember { PeriodRepository(PeriodDatabase.getDatabase(context).periodDao()) }
    val factory = remember { ViewModelFactory(repository) }
    val viewModel: PeriodViewModel = viewModel(factory = factory)

    var showClearDialog by remember { mutableStateOf(false) }

    // Add confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Data") },
            text = { Text("Are you sure you want to delete all period data? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        viewModel.viewModelScope.launch {
                            viewModel.clearAllPeriods()
                        }
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Theme Settings Card
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
                            .background(Pink1)
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
                            .background(LightPurple1)
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
                            .background(DarkPurple1)
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

        Spacer(modifier = Modifier.height(16.dp))

        // Clear Data Button
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
                    "Data Management",
                    style = MaterialTheme.typography.titleMedium
                )

                Button(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Clear All Data")
                }
            }
        }
    }
}

