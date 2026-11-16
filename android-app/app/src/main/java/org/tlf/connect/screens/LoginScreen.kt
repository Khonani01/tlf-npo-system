// ==========================================================
// TLF CONNECT APP - LoginScreen.kt
// ----------------------------------------------------------
// WHAT IS THIS FILE?
// This is the Login screen for the TLF NPO app.
// The user can log in in 2 ways:
//  1. With Email + Password
//  2. With Google Sign-In
//
// HOW IT WORKS:
// - Uses Firebase Auth to check email/password or Google token
// - If login is successful, it goes to the Home screen
// - If login fails, it shows an error message on the screen
// - It also saves the user's real NAME (not email) to AppStore
//   so Home can say "Hi, Peterson" instead of "Hi, peterson@gmail.com"
// ==========================================================
package org.tlf.connect.screens
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import org.tlf.connect.AppStore
import org.tlf.connect.TlfOrange

@Composable
fun LoginScreen(onLogin: () -> Unit, onGoogleLogin: () -> Unit, onGoRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

    // Safe Google setup - won't crash if web client id is missing
    // FIXED: Hard-coded WEB Client ID - no more guessing
    val gso = remember {
        val webClientId = "950169352647-j1m968qqlufs9k81cei5q1uaqit58erk.apps.googleusercontent.com"
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail().build()
    }
    val googleClient = remember { GoogleSignIn.getClient(context, gso) }
    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account.idToken == null) { error = "Google sign-in failed: No ID token. Check SHA-1 in Firebase."; return@rememberLauncherForActivityResult }
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            loading = true
            auth.signInWithCredential(credential).addOnCompleteListener { task2 ->
                loading = false
                if (task2.isSuccessful) {
                    // *** FIXED: Save name correctly + set AppStore to NAME not email ***
                    val googleName = account.displayName ?: account.givenName ?: "there"
                    AppStore.userName.value = googleName
                    firestore.collection("users").document(auth.currentUser!!.uid).set(mapOf("fullName" to googleName, "name" to googleName, "email" to account.email, "type" to "google", "createdAt" to FieldValue.serverTimestamp())).addOnCompleteListener { onGoogleLogin() }
                } else {
                    error = task2.exception?.message ?: "Google auth failed"
                }
            }
        } catch (e: Exception) { error = "Google failed: ${e.message}"; loading = false }
    }
    Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(180.dp).background(Color(0xFF121212)), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(Modifier.size(64.dp).background(TlfOrange, CircleShape), contentAlignment = Alignment.Center) { Text("TLF", color = Color.White, fontWeight = FontWeight.Bold) }; Text("Welcome back", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold) } }
        Column(Modifier.padding(20.dp)) {
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = pass, onValueChange = { pass = it }, visualTransformation = PasswordVisualTransformation(), label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            if (error.isNotEmpty()) Text(error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top=8.dp))
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                if (email.isBlank() || pass.isBlank()) { error = "Fill all fields"; return@Button }
                loading = true; error = ""
                auth.signInWithEmailAndPassword(email.trim(), pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result.user
                        // *** FIXED: Get REAL name, not email ***
                        // 1. Try Auth displayName first (saved at Register)
                        val authName = user?.displayName?.takeIf { it.isNotBlank() }
                        if (authName != null) {
                            AppStore.userName.value = authName
                            loading = false
                            onLogin()
                        } else {
                            // 2. If not in Auth, try Firestore fullName
                            firestore.collection("users").document(user!!.uid).get()
                                .addOnSuccessListener { doc ->
                                    val firestoreName = doc.getString("fullName") ?: doc.getString("name")
                                    AppStore.userName.value = firestoreName ?: email.trim().substringBefore("@")
                                    loading = false
                                    onLogin()
                                }
                                .addOnFailureListener {
                                    AppStore.userName.value = email.trim().substringBefore("@")
                                    loading = false
                                    onLogin()
                                }
                        }
                    }
                    else { loading = false; error = task.exception?.message ?: "Login failed" }
                }
            }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = TlfOrange), shape = RoundedCornerShape(8.dp), enabled = !loading) { if(loading) CircularProgressIndicator(color=Color.White, modifier=Modifier.size(20.dp)) else Text("Login") }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onClick = { googleLauncher.launch(googleClient.signInIntent) }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp)) { Text("Continue with Google") }
            Spacer(Modifier.height(12.dp)); Text("Don't have account? Register", color = TlfOrange, modifier = Modifier.clickable { onGoRegister() })
        }
    }
}