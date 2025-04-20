package com.example.carbonhero.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.carbonhero.ui.theme.CarbonHeroBackground
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataScreen(navController: NavController) {
    val auth = Firebase.auth
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            navController.navigate("login_screen") {
                popUpTo("user_data_screen") { inclusive = true }
            }
            return@LaunchedEffect
        }
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userData by remember { mutableStateOf(mutableMapOf<String, Any>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Firebase.firestore
                .collection("user_data")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        navController.navigate("main_screen") {
                            popUpTo("user_data_screen") { inclusive = true }
                        }
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    val questions = listOf(
        Question.SingleChoice("What is your gender?", listOf("Female", "Male"), "gender"),
        Question.SingleChoice("What is your diet preference?", listOf("Vegan", "Vegetarian", "Omnivore", "Meat-heavy"), "dietPreference"),
        Question.SingleChoice("What type of fuel do you use?", listOf("Gasoline", "Diesel", "Electric", "Hybrid", "LPG"), "fuelType"),
        Question.SingleChoice("What is your car brand?", listOf(
            "Toyota", "Honda", "Volkswagen", "Ford", "BMW", 
            "Mercedes", "Audi", "Hyundai", "Kia", "Nissan", 
            "Chevrolet", "Other"
        ), "carMake"),
        Question.SingleChoice("What is your car model type?", listOf(
            "Sedan", "Hatchback", "SUV", "Crossover", 
            "Station Wagon", "Pickup", "Minivan", "Sports Car", "Other"
        ), "carModel"),
        Question.SingleChoice("How many kilometers do you drive monthly?", listOf(
            "0-500 km", "501-1000 km", "1001-2000 km", 
            "2001-3000 km", "3000+ km"
        ), "monthlyDrivingDistance"),
        Question.SingleChoice("What type of home do you live in?", listOf(
            "Apartment", "Detached House", "Villa", "Studio", 
            "Townhouse", "Duplex"
        ), "houseType"),
        Question.SingleChoice("What is your heating system?", listOf(
            "Natural Gas", "Electric Heater", "Heat Pump", 
            "Central Heating", "Solar Heating", "Wood/Pellet Stove"
        ), "heatingSystem"),
        Question.YesNo("Do you recycle?", "recycling")
    )

    CarbonHeroBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF4CAF50)
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .verticalScroll(scrollState),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = (currentQuestionIndex + 1).toFloat() / questions.size,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFF4CAF50)
                        )

                        when (val question = questions[currentQuestionIndex]) {
                            is Question.SingleChoice -> {
                                Text(question.text, style = MaterialTheme.typography.headlineSmall, color = Color.Black)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    question.options.forEach { option ->
                                        OutlinedButton(
                                            onClick = {
                                                focusManager.clearFocus()
                                                userData[question.key] = option
                                                if (currentQuestionIndex < questions.size - 1) currentQuestionIndex++ else submitData(userData, navController)
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (userData[question.key] == option) Color(0xFF4CAF50) else Color.Transparent
                                            )
                                        ) {
                                            Text(option, color = if (userData[question.key] == option) Color.White else Color.Black)
                                        }
                                    }
                                }
                            }
                            is Question.YesNo -> {
                                Text(question.text, style = MaterialTheme.typography.headlineSmall, color = Color.Black)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Button(
                                        onClick = {
                                            focusManager.clearFocus()
                                            userData[question.key] = true
                                            if (currentQuestionIndex < questions.size - 1) currentQuestionIndex++ else submitData(userData, navController)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                    ) {
                                        Text("Yes")
                                    }
                                    Button(
                                        onClick = {
                                            focusManager.clearFocus()
                                            userData[question.key] = false
                                            if (currentQuestionIndex < questions.size - 1) currentQuestionIndex++ else submitData(userData, navController)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                    ) {
                                        Text("No")
                                    }
                                }
                            }
                            is Question.TextInput -> {
                                Text(question.text, style = MaterialTheme.typography.headlineSmall, color = Color.Black)
                                OutlinedTextField(
                                    value = userData[question.key]?.toString() ?: "",
                                    onValueChange = { 
                                        userData[question.key] = it
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4CAF50),
                                        unfocusedBorderColor = Color.Gray
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            if (userData[question.key]?.toString()?.isNotBlank() == true) {
                                                if (currentQuestionIndex < questions.size - 1) currentQuestionIndex++
                                            }
                                        }
                                    )
                                )
                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (userData[question.key]?.toString()?.isNotBlank() == true) {
                                            if (currentQuestionIndex < questions.size - 1) currentQuestionIndex++
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Text("Next")
                                }
                            }
                        }
                    }
                }
            }

            if (currentQuestionIndex > 0) {
                Button(
                    onClick = { currentQuestionIndex-- },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Back")
                }
            }
        }
    }
}

sealed class Question {
    data class SingleChoice(val text: String, val options: List<String>, val key: String) : Question()
    data class YesNo(val text: String, val key: String) : Question()
    data class TextInput(val text: String, val key: String) : Question()
}

private fun submitData(userData: Map<String, Any>, navController: NavController) {
    val currentUser = Firebase.auth.currentUser
    val db = Firebase.firestore

    val userId = currentUser?.uid ?: return

    val finalData = userData.toMutableMap().apply {
        put("userId", userId)
        put("timestamp", System.currentTimeMillis())
    }

    db.collection("user_data")
        .add(finalData)
        .addOnSuccessListener {
            Log.d("FirestoreSubmit", "Veri eklendi: $finalData")
            navController.navigate("main_screen") {
                popUpTo("login_screen") { inclusive = true }
            }
        }
        .addOnFailureListener { e ->
            Log.e("FirestoreSubmit", "Veri eklenemedi: ${e.message}", e)
        }
}