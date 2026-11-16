// ==========================================================
// TLF CONNECT APP - DonateScreen.kt
// This is the Donate screen
// User can choose amount: R50, R100, R200 or Other amount
// User can choose Once-off or Monthly donation
// Saves donation to Firestore collection "donations"
// ==========================================================
package org.tlf.connect.screens
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import org.tlf.connect.AppStore
import org.tlf.connect.TlfOrange

@Composable
fun DonateScreen() {
    val isDark = AppStore.darkMode.value; val bg = if(isDark) Color(0xFF121212) else Color(0xFFF8F8F8); val cardBg = if(isDark) Color(0xFF1E1E1E) else Color.White; val textC = if(isDark) Color.White else Color.Black
    var selectedAmount by remember { mutableStateOf("R100") }; var otherAmount by remember { mutableStateOf("") }; var showOtherInput by remember { mutableStateOf(false) }; var fullName by remember { mutableStateOf("") }; var email by remember { mutableStateOf("") }; var donationType by remember { mutableStateOf("Once-off") }; var showSuccessBox by remember { mutableStateOf(false) }; var isLoading by remember { mutableStateOf(false) }; val context = LocalContext.current
    Box(Modifier.fillMaxSize().background(bg)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(Modifier.fillMaxWidth().height(110.dp).background(TlfOrange).padding(20.dp)) { Text("Support our mission\nEvery contribution builds stronger communities", color = Color.White) }
            Column(Modifier.padding(16.dp)) {
                Text("Choose amount", fontWeight = FontWeight.Bold, color = textC)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    listOf("R50", "R100", "R200", "Other").forEach { amt -> val sel = selectedAmount == amt; Box(Modifier.border(1.dp, if (sel) TlfOrange else Color.LightGray, RoundedCornerShape(8.dp)).background(if (sel) TlfOrange else cardBg, RoundedCornerShape(8.dp)).clickable { selectedAmount = amt; showOtherInput = amt == "Other" }.padding(12.dp)) { Text(amt, color = if (sel) Color.White else textC) } }
                }
                if (showOtherInput) { Spacer(Modifier.height(10.dp)); OutlinedTextField(value = otherAmount, onValueChange = { otherAmount = it.filter { c -> c.isDigit() } }, label = { Text("Enter any amount") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textC, unfocusedTextColor = textC)) }
                Spacer(Modifier.height(12.dp)); OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textC, unfocusedTextColor = textC))
                Spacer(Modifier.height(8.dp)); OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = textC, unfocusedTextColor = textC))
                Spacer(Modifier.height(12.dp)); Text("Donation type", fontWeight = FontWeight.SemiBold, color = textC)
                Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = donationType == "Once-off", onClick = { donationType = "Once-off" }); Text("Once-off", color = textC, modifier = Modifier.clickable { donationType = "Once-off" }); Spacer(Modifier.width(16.dp)); RadioButton(selected = donationType == "Monthly", onClick = { donationType = "Monthly" }); Text("Monthly", color = textC, modifier = Modifier.clickable { donationType = "Monthly" }) }
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    if (fullName.isBlank() || email.isBlank() || !email.contains("@")) { Toast.makeText(context, "Enter valid name and email", Toast.LENGTH_SHORT).show(); return@Button }
                    if (selectedAmount == "Other" && otherAmount.isBlank()) { Toast.makeText(context, "Enter amount", Toast.LENGTH_SHORT).show(); return@Button }
                    isLoading = true; val amt = if (selectedAmount == "Other") otherAmount else selectedAmount.replace("R", ""); val db = FirebaseFirestore.getInstance()
                    val donation = hashMapOf("full_name" to fullName, "email" to email, "amount" to amt, "type" to donationType, "createdAt" to FieldValue.serverTimestamp())
                    // FIX: Never spin forever - stop after 10 seconds if Firebase has no connection
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isLoading) {
                            isLoading = false
                            Toast.makeText(context, "No connection - check internet / WiFi and try again", Toast.LENGTH_LONG).show()
                        }
                    }, 10000)
                    db.collection("donations").add(donation).addOnSuccessListener { isLoading = false; showSuccessBox = true }.addOnFailureListener { e -> isLoading = false; Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show() }
                }, modifier = Modifier.fillMaxWidth().height(50.dp), enabled = !isLoading, colors = ButtonDefaults.buttonColors(containerColor = TlfOrange), shape = RoundedCornerShape(8.dp)) { if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) else Text("Donate now") }
                Spacer(Modifier.height(80.dp))
            }
        }
        if (showSuccessBox) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Card(Modifier.padding(24.dp).fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(80.dp).background(Color(0xFF4CAF50), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp)) }
                        Spacer(Modifier.height(16.dp)); Text("Thank You!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black); Spacer(Modifier.height(8.dp)); Text("Your donation was successful.", fontSize = 14.sp, color = Color.Gray); Spacer(Modifier.height(16.dp))
                        Button(onClick = { showSuccessBox = false; fullName = ""; email = ""; otherAmount = "" }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), shape = RoundedCornerShape(8.dp)) { Text("Done") }
                    }
                }
            }
        }
    }
}