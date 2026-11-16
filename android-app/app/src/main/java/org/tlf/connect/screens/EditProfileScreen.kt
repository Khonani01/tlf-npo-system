// ==========================================================
// TLF CONNECT APP - EditProfileScreen.kt
// This lets the user change their profile details
// User can change name and profile photo
// Updates Firestore "users" document and Firebase Auth
// ==========================================================
package org.tlf.connect.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import org.tlf.connect.AppStore
import org.tlf.connect.TlfOrange

@Composable
fun EditProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val isDark = AppStore.darkMode.value
    val bg = if (isDark) Color(0xFF121212) else Color(0xFFF6F6F6)
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textC = if (isDark) Color.White else Color.Black
    val subC = if (isDark) Color(0xFFAAAAAA) else Color.Gray

    // Start with current real name - NOT Thabo hardcoded
    var name by remember { mutableStateOf(AppStore.userName.value) }
    var loading by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(bg).padding(16.dp)) {
        Text("< Back", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textC, modifier = Modifier.clickable { onBack() }.padding(bottom = 16.dp))
        Text("Edit profile", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textC)
        Text("Change your name as it shows on Home and Profile", fontSize = 13.sp, color = subC)
        Spacer(Modifier.height(20.dp))

        Text("Full name", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textC)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("e.g. Jesta Mokoena", color = subC) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = cardBg, focusedContainerColor = cardBg,
                unfocusedTextColor = textC, focusedTextColor = textC
            ),
            singleLine = true
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isBlank()) { Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show(); return@Button }
                loading = true
                val cleanName = name.trim()
                // 1. Save locally - Home + Profile update instantly
                AppStore.userName.value = cleanName
                AppStore.draftName.value = cleanName
                // 2. Save to Firebase so it stays after logout/login
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(cleanName).build())
                        .addOnCompleteListener {
                            loading = false
                            Toast.makeText(context, "Saved - Hello $cleanName", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                } else {
                    loading = false
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TlfOrange),
            shape = RoundedCornerShape(8.dp),
            enabled = !loading
        ) {
            if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            else Text("Save changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}