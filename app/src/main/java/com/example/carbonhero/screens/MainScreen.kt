package com.example.carbonhero.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.carbonhero.ui.theme.CarbonHeroBackground
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import com.example.carbonhero.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val currentUser = Firebase.auth.currentUser
    val db = Firebase.firestore

    LaunchedEffect(currentUser?.uid) {
        try {
            val userDoc = currentUser?.uid?.let { uid ->
                db.collection("user_data")
                    .whereEqualTo("userId", uid)
                    .get()
                    .await()
            }
            userData = userDoc?.documents?.firstOrNull()?.data
            isLoading = false
        } catch (e: Exception) {
            error = "Veri yüklenemedi: ${e.message}"
            isLoading = false
        }
    }

    CarbonHeroBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            topBar = {
                TopAppBar(
                    title = { Text("Carbon Hero", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(onClick = {
                            Firebase.auth.signOut()
                            navController.navigate("login_screen") {
                                popUpTo("main_screen") { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF1B5E20).copy(alpha = 0.9f)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = "Challenges") },
                        label = { Text("Challenges") },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = Color(0xFF4CAF50).copy(alpha = 0.3f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = Color(0xFF4CAF50).copy(alpha = 0.3f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = Color(0xFF4CAF50).copy(alpha = 0.3f)
                        )
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }
                    error != null -> {
                        Text(
                            text = error!!,
                            color = Color.Red,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }
                    else -> {
                        when (selectedTab) {
                            0 -> ChallengesTab()
                            1 -> HomeTab(userData)
                            2 -> ProfileTab(userData)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengesTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Challenges",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Daily Challenges",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Complete challenges to reduce your carbon footprint",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 0.4f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE8F5E9)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "2/5 challenges completed today",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1B5E20)
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(getDailyChallenges()) { challenge ->
                ChallengeCard(challenge)
            }
        }
    }
}

private data class Challenge(
    val title: String,
    val description: String,
    val points: Int,
    val icon: ImageVector
)

private fun getDailyChallenges(): List<Challenge> {
    return listOf(
        Challenge(
            "Walk or Bike",
            "Choose walking or biking instead of driving for short trips",
            50,
            Icons.Default.DirectionsWalk
        ),
        Challenge(
            "Reduce Water Usage",
            "Take shorter showers and turn off taps when not in use",
            30,
            Icons.Default.WaterDrop
        ),
        Challenge(
            "Energy Saver",
            "Turn off lights and unplug devices when not in use",
            40,
            Icons.Default.Lightbulb
        ),
        Challenge(
            "Zero Waste Meal",
            "Prepare a meal using all ingredients with no waste",
            60,
            Icons.Default.Restaurant
        )
    )
}

@Composable
private fun ChallengeCard(challenge: Challenge) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = challenge.icon,
                    contentDescription = challenge.title,
                    tint = Color(0xFF1B5E20),
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${challenge.points}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "points",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun HomeTab(userData: Map<String, Any>?) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Your Carbon Footprint",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Calculate total carbon footprint based on user data
                val totalFootprint = calculateTotalCarbonFootprint(userData)
                
                Text(
                    text = "Total: ${String.format("%.1f", totalFootprint)} tons CO₂/year",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1B5E20)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Carbon footprint breakdown
                CarbonFootprintBreakdown(userData)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Reduction Tips",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1B5E20)
                )
                Spacer(modifier = Modifier.height(16.dp))
                ReductionTipsList()
            }
        }
    }
}

@Composable
private fun CarbonFootprintBreakdown(userData: Map<String, Any>?) {
    val categories = listOf(
        FootprintCategory("Buildings", 0.64f, Icons.Default.Home),
        FootprintCategory("Industry", 0.20f, Icons.Default.Factory),
        FootprintCategory("Transportation", 0.14f, Icons.Default.DirectionsCar),
        FootprintCategory("Agriculture", 0.15f, Icons.Default.Grass),
        FootprintCategory("Electricity", 0.18f, Icons.Default.ElectricBolt),
        FootprintCategory("Waste", 0.14f, Icons.Default.Delete)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            FootprintCategoryItem(category)
        }
    }
}

@Composable
private fun FootprintCategoryItem(category: FootprintCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = category.name,
            tint = Color(0xFF1B5E20),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1B5E20)
            )
            LinearProgressIndicator(
                progress = category.percentage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFFE8F5E9)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${(category.percentage * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1B5E20)
        )
    }
}

@Composable
private fun ReductionTipsList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReductionTip(
            "Use Public Transport",
            "Reduce your transportation emissions by using public transit",
            Icons.Default.DirectionsTransit
        )
        ReductionTip(
            "Energy Efficient Appliances",
            "Switch to energy-efficient appliances to reduce electricity consumption",
            Icons.Default.Power
        )
        ReductionTip(
            "Reduce Waste",
            "Practice recycling and composting to minimize waste",
            Icons.Default.Delete
        )
    }
}

@Composable
private fun ReductionTip(title: String, description: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF1B5E20),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF1B5E20)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

private data class FootprintCategory(
    val name: String,
    val percentage: Float,
    val icon: ImageVector
)

private fun calculateTotalCarbonFootprint(userData: Map<String, Any>?): Double {
    if (userData == null) return 0.0
    
    var total = 0.0
    
    // Transportation calculation
    when (userData["monthlyDrivingDistance"]) {
        "0-500 km" -> total += 0.5
        "501-1000 km" -> total += 1.0
        "1001-2000 km" -> total += 2.0
        "2001-3000 km" -> total += 3.0
        "3000+ km" -> total += 4.0
    }
    
    // Diet calculation
    when (userData["dietPreference"]) {
        "Meat-heavy" -> total += 3.3
        "Omnivore" -> total += 2.5
        "Vegetarian" -> total += 1.7
        "Vegan" -> total += 1.5
    }
    
    // Housing calculation
    when (userData["houseType"]) {
        "Apartment" -> total += 1.5
        "Detached House" -> total += 2.5
        "Villa" -> total += 3.0
        "Studio" -> total += 1.0
    }
    
    // Heating system calculation
    when (userData["heatingSystem"]) {
        "Natural Gas" -> total += 2.0
        "Electric Heater" -> total += 1.5
        "Stove" -> total += 2.5
        "Central Heating" -> total += 2.2
    }
    
    // Recycling impact
    if (userData["recycling"] == true) {
        total *= 0.9 // 10% reduction for recycling
    }
    
    return total
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileTab(userData: Map<String, Any>?) {
    var username by remember { mutableStateOf<String?>(null) }
    val currentUser = Firebase.auth.currentUser
    val db = Firebase.firestore
    var showEditDialog by remember { mutableStateOf<ProfileItem?>(null) }
    var editedValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userDataState by remember { mutableStateOf(userData) }

    // Fetch username from Firestore
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    username = document.getString("username")
                }
        }
    }

    if (showEditDialog != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = {
                Text(
                    text = "Edit ${showEditDialog?.title}",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                when (showEditDialog?.title) {
                    "Gender" -> {
                        Column {
                            listOf("Female", "Male").forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { editedValue = option }
                                ) {
                                    RadioButton(
                                        selected = editedValue == option,
                                        onClick = { editedValue = option }
                                    )
                                    Text(option)
                                }
                            }
                        }
                    }
                    "Diet Type" -> {
                        Column {
                            listOf("Vegan", "Vegetarian", "Omnivore", "Meat-heavy").forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { editedValue = option }
                                ) {
                                    RadioButton(
                                        selected = editedValue == option,
                                        onClick = { editedValue = option }
                                    )
                                    Text(option)
                                }
                            }
                        }
                    }
                    "Vehicle Type" -> {
                        Column {
                            listOf(
                                "Sedan", "Hatchback", "SUV", "Crossover",
                                "Station Wagon", "Pickup", "Minivan", "Sports Car", "Other"
                            ).forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { editedValue = option }
                                ) {
                                    RadioButton(
                                        selected = editedValue == option,
                                        onClick = { editedValue = option }
                                    )
                                    Text(option)
                                }
                            }
                        }
                    }
                    "House Type" -> {
                        Column {
                            listOf(
                                "Apartment", "Detached House", "Villa", "Studio",
                                "Townhouse", "Duplex"
                            ).forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { editedValue = option }
                                ) {
                                    RadioButton(
                                        selected = editedValue == option,
                                        onClick = { editedValue = option }
                                    )
                                    Text(option)
                                }
                            }
                        }
                    }
                    "Heating System" -> {
                        Column {
                            listOf(
                                "Natural Gas", "Electric Heater", "Heat Pump",
                                "Central Heating", "Solar Heating", "Wood/Pellet Stove"
                            ).forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { editedValue = option }
                                ) {
                                    RadioButton(
                                        selected = editedValue == option,
                                        onClick = { editedValue = option }
                                    )
                                    Text(option)
                                }
                            }
                        }
                    }
                    "Monthly Distance" -> {
                        Column {
                            listOf(
                                "0-500 km", "501-1000 km", "1001-2000 km",
                                "2001-3000 km", "3000+ km"
                            ).forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { editedValue = option }
                                ) {
                                    RadioButton(
                                        selected = editedValue == option,
                                        onClick = { editedValue = option }
                                    )
                                    Text(option)
                                }
                            }
                        }
                    }
                    "Recycling" -> {
                        Column {
                            listOf("Yes", "No").forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { 
                                        editedValue = (option == "Yes").toString()
                                    }
                                ) {
                                    RadioButton(
                                        selected = editedValue == (option == "Yes").toString(),
                                        onClick = { editedValue = (option == "Yes").toString() }
                                    )
                                    Text(option)
                                }
                            }
                        }
                    }
                    else -> {
                        OutlinedTextField(
                            value = editedValue,
                            onValueChange = { editedValue = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLoading = true
                        val updatedData = userDataState?.toMutableMap() ?: mutableMapOf()
                        
                        when (showEditDialog?.title) {
                            "Recycling" -> updatedData["recycling"] = editedValue.toBoolean()
                            else -> updatedData[getKeyForTitle(showEditDialog?.title ?: "")] = editedValue
                        }

                        currentUser?.uid?.let { uid ->
                            db.collection("user_data")
                                .whereEqualTo("userId", uid)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (!documents.isEmpty) {
                                        val doc = documents.documents[0]
                                        doc.reference.update(updatedData)
                                            .addOnSuccessListener {
                                                userDataState = updatedData
                                                isLoading = false
                                                showEditDialog = null
                                            }
                                            .addOnFailureListener {
                                                errorMessage = "Failed to update data"
                                                isLoading = false
                                            }
                                    }
                                }
                                .addOnFailureListener {
                                    errorMessage = "Failed to fetch user data"
                                    isLoading = false
                                }
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) {
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
        // Profile Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1B5E20).copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    ) {
                        val isFemale = userDataState?.get("gender") == "Female"
                        Image(
                            painter = painterResource(
                                id = if (isFemale) R.drawable.female else R.drawable.male
                            ),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Welcome,",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    
                    Text(
                        text = "${username ?: "User"}!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    
                    Text(
                        text = "Carbon Hero Profile",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        
        // Error message
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Stats Cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(getProfileItems(userDataState)) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                        .clickable {
                            editedValue = item.value
                            showEditDialog = item
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (item.title == "Gender") {
                                Icon(
                                    imageVector = if (userDataState?.get("gender") == "Female") 
                                        Icons.Default.Face 
                                    else 
                                        Icons.Default.AccountCircle,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color(0xFF1B5E20)
                                )
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = Color(0xFF1B5E20)
                                )
                            }
                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = item.value,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF1B5E20)
                        )
                    }
                }
            }
        }
    }
}

private fun getKeyForTitle(title: String): String {
    return when (title) {
        "Gender" -> "gender"
        "Diet Type" -> "dietPreference"
        "Vehicle Type" -> "carModel"
        "Monthly Distance" -> "monthlyDrivingDistance"
        "House Type" -> "houseType"
        "Heating System" -> "heatingSystem"
        "Recycling" -> "recycling"
        else -> title.lowercase().replace(" ", "_")
    }
}

private data class ProfileItem(
    val title: String,
    val value: String,
    val icon: ImageVector
)

private fun getProfileItems(userData: Map<String, Any>?): List<ProfileItem> {
    return listOf(
        ProfileItem(
            "Gender",
            userData?.get("gender")?.toString() ?: "Not specified",
            Icons.Default.Face
        ),
        ProfileItem(
            "Carbon Footprint",
            "10 lbs/day",
            Icons.Default.Co2
        ),
        ProfileItem(
            "Total Reduction",
            "17 lbs",
            Icons.Default.TrendingDown
        ),
        ProfileItem(
            "Diet Type",
            userData?.get("dietPreference")?.toString() ?: "Not specified",
            Icons.Default.Restaurant
        ),
        ProfileItem(
            "Vehicle Type",
            "${userData?.get("carMake")} - ${userData?.get("carModel")}",
            Icons.Default.DirectionsCar
        ),
        ProfileItem(
            "Monthly Distance",
            userData?.get("monthlyDrivingDistance")?.toString() ?: "Not specified",
            Icons.Default.Timeline
        ),
        ProfileItem(
            "House Type",
            userData?.get("houseType")?.toString() ?: "Not specified",
            Icons.Default.Home
        ),
        ProfileItem(
            "Heating System",
            userData?.get("heatingSystem")?.toString() ?: "Not specified",
            Icons.Default.Thermostat
        ),
        ProfileItem(
            "Recycling",
            if (userData?.get("recycling") == true) "Yes" else "No",
            Icons.Default.Recycling
        )
    )
}