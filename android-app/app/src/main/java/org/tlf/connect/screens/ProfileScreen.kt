// ==========================================================
// TLF CONNECT APP - ProfileScreen.kt
// This shows the logged-in user's profile details
// Shows name, email, and options to Edit Profile, Logout
// Also has Dark Mode, Language and Privacy options
// ==========================================================
package org.tlf.connect.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import org.tlf.connect.AppStore
import org.tlf.connect.TlfOrange

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onEditProfile: () -> Unit = {},
    onPrivacy: () -> Unit = {}
) {
    val isDark = AppStore.darkMode.value
    val bg = if (isDark) Color(0xFF121212) else Color(0xFFF6F6F6)
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textC = if (isDark) Color.White else Color.Black
    val subC = if (isDark) Color(0xFFAAAAAA) else Color.Gray
    val dividerC = if (isDark) Color(0xFF333333) else Color(0xFFF0F0F0)
    val context = LocalContext.current

    // NAME ONLY - NO EMAIL
    val user = FirebaseAuth.getInstance().currentUser
    val rawName = AppStore.userName.value.takeIf { it.isNotBlank() && !it.contains("@") }
        ?: user?.displayName ?: "Thabo Nkosi"
    val parts = rawName.trim().split(" ").filter { it.isNotBlank() }
    val initials = if (parts.size >= 2) "${parts[0].first().uppercase()}${parts[1].first().uppercase()}" else parts.firstOrNull()?.firstOrNull()?.uppercase() ?: "T"
    val displayName = parts.joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

    // PROFILE PICTURE - GALLERY PICKER - WORKS ON ANY PHONE
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            // Load bitmap from gallery
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    imageBitmap = BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) { }
        }
    }

    // LANGUAGE POPUP
    val currentLang = try { AppStore.language.value } catch (e: Exception) { "English" }
    var showLangDialog by remember { mutableStateOf(false) }
    var tempLang by remember { mutableStateOf(currentLang) }

    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text("Choose Language", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Khetha ulimi / Select language", color = Color.Gray, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    listOf("English", "Zulu").forEach { lang ->
                        Row(Modifier.fillMaxWidth().clickable { tempLang = lang }.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = tempLang == lang, onClick = { tempLang = lang })
                            Spacer(Modifier.width(8.dp))
                            Text(if (lang == "Zulu") "Zulu - isiZulu" else "English", fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { try { AppStore.language.value = tempLang } catch (e: Exception) {}; showLangDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = TlfOrange)) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showLangDialog = false }) { Text("Cancel") } }
        )
    }

    Column(Modifier.fillMaxSize().background(bg)) {
        Box(Modifier.fillMaxWidth().background(Color.Black), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 32.dp, bottom = 20.dp)) {
                Box {
                    // CLICK TO OPEN GALLERY
                    Box(
                        Modifier.size(84.dp).clip(CircleShape).background(TlfOrange, CircleShape)
                            .clickable { picker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageBitmap != null) {
                            Image(bitmap = imageBitmap!!.asImageBitmap(), contentDescription = "Profile photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Text(initials, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    // Small camera button bottom-right
                    Box(
                        Modifier.size(26.dp).background(Color.White, CircleShape).align(Alignment.BottomEnd)
                            .clickable { picker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Filled.CameraAlt, null, tint = Color.Black, modifier = Modifier.size(14.dp)) }
                    Box(Modifier.size(14.dp).background(Color(0xFF2ECC71), CircleShape).align(Alignment.BottomStart))
                }
                Spacer(Modifier.height(10.dp))
                Text(displayName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Tap photo to change • Volunteer since 2024", color = Color.Gray, fontSize = 12.sp)
            }
        }

        Column(Modifier.padding(16.dp)) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = cardBg), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    ProfileRow(icon = Icons.Filled.Person, title = "Edit profile", textC = textC, onClick = onEditProfile)
                    Divider(color = dividerC)
                    ProfileRow(icon = Icons.Filled.Lock, title = "Privacy & security", textC = textC, onClick = onPrivacy)
                    Divider(color = dividerC)
                    // Language - opens popup
                    Row(Modifier.fillMaxWidth().clickable { tempLang = currentLang; showLangDialog = true }.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(32.dp).background(Color(0xFFFFF3E0), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Language, null, tint = TlfOrange, modifier = Modifier.size(18.dp)) }
                        Spacer(Modifier.width(12.dp))
                        Column { Text("Language", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textC); Text(currentLang, color = subC, fontSize = 12.sp) }
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Filled.ChevronRight, null, tint = Color.Gray)
                    }
                    Divider(color = dividerC)
                    // Dark Mode - inside same card, same style
                    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(32.dp).background(Color(0xFFFFF3E0), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Filled.DarkMode, null, tint = TlfOrange, modifier = Modifier.size(18.dp)) }
                        Spacer(Modifier.width(12.dp))
                        Text("Dark Mode", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textC)
                        Spacer(Modifier.weight(1f))
                        Switch(checked = isDark, onCheckedChange = { AppStore.darkMode.value = it })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = { onLogout() }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB00020))) {
                Icon(Icons.Filled.Logout, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Log out", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun ProfileRow(icon: ImageVector, title: String, textC: Color, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(32.dp).background(Color(0xFFFFF3E0), CircleShape), contentAlignment = Alignment.Center) { Icon(icon, null, tint = TlfOrange, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(12.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textC)
        Spacer(Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, null, tint = Color.Gray)
    }
}