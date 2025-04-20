package com.example.carbonhero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.carbonhero.ui.theme.CarbonHeroBackground
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun LoginScreen(navController: NavController) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = Firebase.auth
    val db = Firebase.firestore
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    CarbonHeroBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(64.dp))

                // Stylish Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(
                                color = Color.White,
                                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            )) {
                                append("Carbon")
                            }
                            append(" ")
                            withStyle(SpanStyle(
                                color = Color(0xFF4CAF50),
                                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                                fontWeight = FontWeight.Bold
                            )) {
                                append("Hero")
                            }
                        }
                    )
                    
                    Text(
                        text = "Your path to sustainable living",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Login Form Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Welcome Back",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                errorMessage = null
                            },
                            label = { Text("Email", color = Color.White) },
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = Color.White
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                                cursorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                errorMessage = null
                            },
                            label = { Text("Password", color = Color.White) },
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = Color.White
                                )
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                                cursorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please fill in all fields"
                                    return@Button
                                }

                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val userId = auth.currentUser?.uid
                                            if (userId != null) {
                                                db.collection("user_data")
                                                    .whereEqualTo("userId", userId)
                                                    .get()
                                                    .addOnSuccessListener { documents ->
                                                        if (!documents.isEmpty) {
                                                            navController.navigate("main_screen") {
                                                                popUpTo("login_screen") { inclusive = true }
                                                            }
                                                        } else {
                                                            navController.navigate("user_data_screen") {
                                                                popUpTo("login_screen") { inclusive = true }
                                                            }
                                                        }
                                                    }
                                                    .addOnFailureListener {
                                                        errorMessage = "Error during data verification"
                                                    }
                                            }
                                        } else {
                                            errorMessage = "Login failed. Please check your credentials."
                                        }
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Login",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        errorMessage?.let {
                            Text(
                                text = it,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        navController.navigate("register_screen") {
                            launchSingleTop = true
                        }
                    }
                ) {
                    Text(
                        "Don't have an account? Register",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
