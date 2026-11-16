// ==========================================================
// TLF CONNECT APP - EventsScreen.kt
// This shows TLF events / activities
// User can view events and choose to volunteer for one
// ==========================================================
package org.tlf.connect.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import org.tlf.connect.AppStore

data class EventItem(val title: String = "", val date: String = "", val location: String = "")

@Composable
fun EventsScreen() {
    val isDark = AppStore.darkMode.value; val bg = if(isDark) Color(0xFF121212) else Color(0xFFF8F8F8); val cardBg = if(isDark) Color(0xFF1E1E1E) else Color.White; val textC = if(isDark) Color.White else Color.Black
    var events by remember { mutableStateOf(listOf<EventItem>()) }
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("events").get().addOnSuccessListener { snap -> events = snap.documents.map { EventItem(it.getString("title") ?: "", it.getString("date") ?: "", it.getString("location") ?: "") } }
    }
    if (events.isEmpty()) events = listOf(EventItem("Youth Mentorship Day", "15 Dec 2025", "Soshanguve Campus"), EventItem("Community Cleanup", "20 Dec 2025", "Tshwane"), EventItem("Food Drive", "22 Dec 2025", "Pretoria"))
    LazyColumn(Modifier.fillMaxSize().background(bg).padding(16.dp)) {
        item { Text("Upcoming Events", fontWeight = FontWeight.Bold, color = textC, modifier = Modifier.padding(bottom = 12.dp)) }
        items(events) { ev -> Card(Modifier.fillMaxWidth().padding(bottom = 10.dp), colors = CardDefaults.cardColors(containerColor = cardBg), shape = RoundedCornerShape(12.dp)) { Column(Modifier.padding(16.dp)) { Text(ev.title, fontWeight = FontWeight.Bold, color = textC); Text(ev.date, color = Color.Gray); Text(ev.location, color = Color.Gray) } } }
    }
}