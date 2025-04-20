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
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Carbon Hero",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Email", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Şifre", color = Color.White) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    singleLine = true
                )

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Lütfen tüm alanları doldurun"
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
                                                errorMessage = "Veri kontrolü sırasında hata oluştu"
                                            }
                                    }
                                } else {
                                    errorMessage = "Giriş başarısız. Lütfen bilgileri kontrol edin."
                                }
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Giriş Yap", color = Color.White)
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        navController.navigate("register_screen") {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        "Hesabınız yok mu? Kayıt olun",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
