// ==========================================================
// TLF CONNECT APP - HomeScreen.kt
// This is the Home screen after login
// Shows greeting with user's name from AppStore
// User can navigate to Donate, Volunteer, Events and Profile
// ==========================================================
package org.tlf.connect.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import org.tlf.connect.AppStore
import org.tlf.connect.TlfOrange

@Composable
fun HomeScreen(onVolunteer: () -> Unit, onDonate: () -> Unit) {
    val isDark = AppStore.darkMode.value; val bg = if(isDark) Color(0xFF121212) else Color(0xFFF8F8F8); val cardBg = if(isDark) Color(0xFF1E1E1E) else Color.White; val textC = if(isDark) Color.White else Color.Black
    val rawName = AppStore.userName.value.takeIf { it.isNotBlank() && !it.contains("@") }
        ?: FirebaseAuth.getInstance().currentUser?.displayName
        ?: "there"
    val firstName = rawName.split(" ").firstOrNull()?.takeIf { it.isNotBlank() } ?: "there"
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val greetingText = when (hour) {
        in 5..11 -> "Good Morning,"
        in 12..16 -> "Good Afternoon,"
        else -> "Good Evening,"
    }
    Column(Modifier.fillMaxSize().background(bg).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(130.dp).background(Color(0xFF121212)).padding(20.dp)) { Column { Text(greetingText, color = Color.Gray, fontSize = 12.sp); Text(firstName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold) } }
        Column(Modifier.padding(16.dp)) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB347))) { Box(Modifier.padding(20.dp)) { Text("500+ youth empowered across Tshwane", color = Color.White, fontWeight = FontWeight.Bold) } }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(Modifier.weight(1f).clickable { onVolunteer() }, colors = CardDefaults.cardColors(containerColor = cardBg)) { Column(Modifier.padding(16.dp)) { Icon(Icons.Filled.FavoriteBorder, null, tint = TlfOrange); Text("Volunteer", color = textC) } }
                Card(Modifier.weight(1f).clickable { onDonate() }, colors = CardDefaults.cardColors(containerColor = cardBg)) { Column(Modifier.padding(16.dp)) { Icon(Icons.Filled.FavoriteBorder, null, tint = TlfOrange); Text("Donate", color = textC) } }
            }
        }
    }
}