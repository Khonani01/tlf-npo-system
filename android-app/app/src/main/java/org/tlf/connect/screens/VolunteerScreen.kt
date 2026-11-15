package org.tlf.connect.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import org.tlf.connect.AppStore
import org.tlf.connect.TlfOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val isDark = AppStore.darkMode.value
    val bg = if (isDark) Color(0xFF121212) else Color(0xFFF6F6F6)
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textC = if (isDark) Color.White else Color.Black
    val subC = if (isDark) Color(0xFFAAAAAA) else Color.Gray
    val borderC = if (isDark) Color(0xFF333333) else Color(0xFFE0E0E0)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = cardBg, focusedContainerColor = cardBg,
        unfocusedTextColor = textC, focusedTextColor = textC,
        unfocusedPlaceholderColor = subC, focusedPlaceholderColor = subC,
        focusedBorderColor = TlfOrange, unfocusedBorderColor = borderC
    )

    var fullName by remember { mutableStateOf(AppStore.userName.value.ifBlank { auth.currentUser?.displayName ?: "" }) }
    var email by remember { mutableStateOf(auth.currentUser?.email ?: "") }
    var phone by remember { mutableStateOf("") }
    var why by remember { mutableStateOf("") }
    var event by remember { mutableStateOf("") }
    var agreed by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var eventExpanded by remember { mutableStateOf(false) }

    val allSkills = listOf("Teaching", "Mentoring", "Events", "IT")
    val selectedSkills = remember { mutableStateListOf<String>() }
    val events = listOf("Youth Mentorship Programme", "Tutoring Programme", "Community Clean-up", "Fundraising Event", "Sports Day", "Holiday Camp")

    fun saveToFirestore(onDone: () -> Unit) {
        loading = true; error = ""
        val data = hashMapOf(
            "fullName" to fullName.trim(),
            "email" to email.trim(),
            "phone" to phone.trim(),
            "skillArea" to selectedSkills.toList(),
            "event" to event,
            "whyVolunteer" to why.trim(),
            "agreedToTerms" to agreed,
            "status" to "submitted",
            "uid" to (auth.currentUser?.uid ?: ""),
            "createdAt" to FieldValue.serverTimestamp()
        )
        // FIX: Never spin forever - stop after 10 seconds if Firebase has no connection
        Handler(Looper.getMainLooper()).postDelayed({
            if (loading) {
                loading = false
                error = "No connection to Firebase - check internet / WiFi and try again."
                Toast.makeText(context, "No connection - check internet", Toast.LENGTH_LONG).show()
            }
        }, 10000)
        firestore.collection("volunteers").add(data)
            .addOnSuccessListener {
                loading = false
                Toast.makeText(context, "Application submitted!", Toast.LENGTH_LONG).show()
                onDone()
            }
            .addOnFailureListener { e ->
                loading = false
                error = "FAILED: ${e.message}"
                Log.e("VOLUNTEER", "Failed", e)
                Toast.makeText(context, "FAILED: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    Column(
        Modifier.fillMaxSize().background(bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("< Back", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textC, modifier = Modifier.clickable { onBack() }.padding(bottom = 4.dp))
        Text("Volunteer with us", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textC)
        Text("Tell us a bit about yourself", fontSize = 13.sp, color = subC)
        Spacer(Modifier.height(16.dp))

        Text("Full name", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textC)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, placeholder = { Text("e.g. Thabo Nkosi", color = subC) }, leadingIcon = { Icon(Icons.Filled.Person, null, tint = subC) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = fieldColors)
        Spacer(Modifier.height(12.dp))

        Text("Email address", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textC)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, placeholder = { Text("name@example.com", color = subC) }, leadingIcon = { Icon(Icons.Filled.Person, null, tint = subC) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = fieldColors)
        Spacer(Modifier.height(12.dp))

        Text("Phone number", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textC)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, placeholder = { Text("071 234 5678", color = subC) }, leadingIcon = { Icon(Icons.Filled.Person, null, tint = subC) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = fieldColors)
        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Skill area", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textC)
            Text(if (selectedSkills.size == allSkills.size) "Clear All" else "Select All", color = TlfOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                if (selectedSkills.size == allSkills.size) selectedSkills.clear() else { selectedSkills.clear(); selectedSkills.addAll(allSkills) }
            })
        }
        Text("Optional - pick as many as you want, or leave empty", color = subC, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            allSkills.forEach { skill ->
                val selected = selectedSkills.contains(skill)
                Box(
                    Modifier.background(if (selected) TlfOrange else cardBg, RoundedCornerShape(8.dp))
                        .border(1.dp, if (selected) TlfOrange else borderC, RoundedCornerShape(8.dp))
                        .clickable { if (selected) selectedSkills.remove(skill) else selectedSkills.add(skill) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) { Text(skill, color = if (selected) Color.White else textC, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text("Event / programme", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textC)
        Spacer(Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = eventExpanded, onExpandedChange = { eventExpanded = !eventExpanded }) {
            OutlinedTextField(value = event, onValueChange = {}, readOnly = true, placeholder = { Text("Select an event", color = subC) }, trailingIcon = { Icon(Icons.Filled.ArrowForwardIos, null, modifier = Modifier.size(12.dp), tint = subC) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(8.dp), colors = fieldColors)
            ExposedDropdownMenu(expanded = eventExpanded, onDismissRequest = { eventExpanded = false }, modifier = Modifier.background(cardBg)) {
                events.forEach { e ->
                    DropdownMenuItem(text = { Text(e, color = textC) }, onClick = { event = e; eventExpanded = false }, colors = MenuDefaults.itemColors(textColor = textC))
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Text("Why do you want to volunteer?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textC)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(value = why, onValueChange = { why = it }, placeholder = { Text("Tell us what inspires you...", color = subC) }, modifier = Modifier.fillMaxWidth().height(110.dp), shape = RoundedCornerShape(8.dp), colors = fieldColors)
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { agreed = !agreed }) {
            Checkbox(checked = agreed, onCheckedChange = { agreed = it }, colors = CheckboxDefaults.colors(checkedColor = TlfOrange))
            Text("I agree to the terms and conditions", fontSize = 13.sp, color = textC)
        }
        if (!agreed) Text("Tick the box above to enable Apply now", color = subC, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))

        if (error.isNotEmpty()) Text(error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            if (fullName.isBlank() || email.isBlank() || phone.isBlank() || event.isBlank()) { error = "Please fill name, email, phone and event. Skills are optional."; return@Button }
            if (!agreed) { error = "Please agree to the terms and conditions to apply."; return@Button }
            saveToFirestore { onBack() }
        }, modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TlfOrange, disabledContainerColor = if (isDark) Color(0xFF333333) else Color(0xFFCCCCCC), disabledContentColor = if (isDark) Color(0xFF888888) else Color.White),
            shape = RoundedCornerShape(8.dp),
            enabled = !loading && agreed
        ) {
            if (loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) else Text("Apply now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}