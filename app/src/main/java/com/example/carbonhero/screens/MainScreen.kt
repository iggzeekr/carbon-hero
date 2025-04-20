package com.example.carbonhero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.carbonhero.ui.theme.CarbonHeroBackground
import kotlinx.coroutines.tasks.await

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
                                contentDescription = "Çıkış Yap",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF1B5E20)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Görevler") },
                        label = { Text("Görevler") },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = Color(0xFF4CAF50)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
                        label = { Text("Ana Sayfa") },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = Color(0xFF4CAF50)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                        label = { Text("Profil") },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = Color(0xFF4CAF50)
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
    val challenges = listOf(
        "Bir hafta boyunca toplu taşıma kullan",
        "Evdeki tüm ampulleri LED ile değiştir",
        "Bu hafta et tüketimini azalt",
        "Alışverişte plastik poşet kullanmamaya çalış",
        "Elektronik cihazları bekleme modunda bırakma"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(challenges) { challenge ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = challenge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTab(userData: Map<String, Any>?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Günlük Karbon Ayak İziniz",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "12.5 kg CO₂",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Karbon Ayak İzi Dağılımı",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CarbonSourceItem("Ulaşım", 45)
                    CarbonSourceItem("Ev Enerjisi", 30)
                    CarbonSourceItem("Beslenme", 15)
                    CarbonSourceItem("Diğer", 10)
                }
            }
        }
    }
}

@Composable
fun CarbonSourceItem(source: String, percentage: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = source,
            color = Color.White
        )
        Text(
            text = "%$percentage",
            color = Color.White
        )
    }
}

@Composable
fun ProfileTab(userData: Map<String, Any>?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ProfileItem("Araç", "${userData?.get("carMake")} ${userData?.get("carModel")}")
                    ProfileItem("Yakıt Tipi", userData?.get("fuelType")?.toString() ?: "-")
                    ProfileItem("Aylık Sürüş", "${userData?.get("monthlyDrivingDistance")} km")
                    ProfileItem("Beslenme Tercihi", userData?.get("dietPreference")?.toString() ?: "-")
                    ProfileItem("Ev Tipi", userData?.get("houseType")?.toString() ?: "-")
                }
            }
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White
        )
        Text(
            text = value,
            color = Color.White
        )
    }
}