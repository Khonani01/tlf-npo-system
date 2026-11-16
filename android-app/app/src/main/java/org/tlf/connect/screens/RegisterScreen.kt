// ==========================================================
// TLF CONNECT APP - RegisterScreen.kt
// This is the Register screen - creates a new user
// Creates user in Firebase Auth with email + password
// Saves name, email and phone to Firestore "users" collection
// Also saves displayName in Auth so Login can greet by real name
// ==========================================================
package org.tlf.connect.screens
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import org.tlf.connect.AppStore
import org.tlf.connect.TlfOrange
import kotlinx.coroutines.*

@Composable
fun RegisterScreen(onRegistered: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(180.dp).background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(64.dp).background(TlfOrange, CircleShape), contentAlignment = Alignment.Center) { Text("TLF", color = Color.White, fontWeight = FontWeight.Bold) }
                Text("Create account", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column(Modifier.padding(20.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = pass, onValueChange = { pass = it }, visualTransformation = PasswordVisualTransformation(), label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))

            if (error.isNotEmpty()) Text(error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                val cleanEmail = email.trim()
                val cleanName = name.trim()
                if (cleanName.isBlank() || cleanEmail.isBlank() || pass.isBlank()) { error = "Fill all fields"; return@Button }
                if (pass.length < 6) { error = "Password must be at least 6 characters"; return@Button }

                loading = true; error = ""
                scope.launch {
                    delay(15000)
                    if (loading) {
                        loading = false
                        error = "Connection timed out. Check internet / Firebase setup, then try again. Check Logcat for error."
                    }
                }

                auth.createUserWithEmailAndPassword(cleanEmail, pass)
                    .addOnSuccessListener { res ->
                        val user = res.user
                        val uid = user?.uid
                        if (uid == null) { loading = false; error = "Registration failed, try again"; return@addOnSuccessListener }

                        // SAVE NAME TO AUTH PROFILE
                        val profile = userProfileChangeRequest { displayName = cleanName }
                        user.updateProfile(profile).addOnCompleteListener {
                            // FIXED: WAIT for Firestore to actually save - this is what shows in Console
                            val userData = mapOf(
                                "fullName" to cleanName,
                                "name" to cleanName,
                                "email" to cleanEmail,
                                "uid" to uid,
                                "createdAt" to FieldValue.serverTimestamp()
                            )
                            firestore.collection("users").document(uid).set(userData)
                                .addOnSuccessListener {
                                    AppStore.userName.value = cleanName
                                    loading = false
                                    Toast.makeText(context, "Registered! Now please Log in", Toast.LENGTH_LONG).show()
                                    auth.signOut()
                                    onRegistered()
                                }
                                .addOnFailureListener { e ->
                                    loading = false
                                    error = "Registered but failed to save to database: ${e.message}. Check Firestore Rules."
                                    Toast.makeText(context, "Firestore error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        error = when {
                            e.message?.contains("already in use", ignoreCase = true) == true -> "This email is already registered. Please Log in."
                            e.message?.contains("network", ignoreCase = true) == true -> "No internet connection. Turn on WiFi/Data and try again."
                            e.message?.contains("badly formatted", ignoreCase = true) == true -> "Invalid email format"
                            else -> e.message ?: "Register failed"
                        }
                    }
            }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = TlfOrange), shape = RoundedCornerShape(8.dp), enabled = !loading) {
                if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) else Text("Register")
            }

            Spacer(Modifier.height(12.dp))
            Text("Already have account? Log in", color = TlfOrange, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { onBack() }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp)) {
                Text("Back to Login")
            }
        }
    }
}