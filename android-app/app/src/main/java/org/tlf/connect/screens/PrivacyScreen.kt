package org.tlf.connect.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tlf.connect.AppStore

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    val isDark = AppStore.darkMode.value
    val bg = if (isDark) Color(0xFF121212) else Color(0xFFF6F6F6)
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textC = if (isDark) Color.White else Color.Black
    val subC = if (isDark) Color(0xFFAAAAAA) else Color.Gray

    val tips = listOf(
        "Keep your password private" to "Never share your TLF password with anyone, not even friends or volunteers. TLF will never ask for your password.",
        "Don't share OTP codes" to "If someone asks for a code sent to your phone or email, don't send it. That code lets them into your account.",
        "Log out on shared phones" to "If you use someone else's phone, always press Log out on Profile when you are done.",
        "Check before you donate" to "Only donate inside this app. Don't send money to personal numbers claiming to be TLF.",
        "Keep your phone locked" to "Use a PIN, fingerprint or pattern so no one can open TLF if they pick up your phone.",
        "Update your details" to "If your number or email changes, update it in Edit Profile so we can reach you safely."
    )

    Column(Modifier.fillMaxSize().background(bg).verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("< Back", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textC, modifier = Modifier.clickable { onBack() }.padding(bottom = 16.dp))
        Text("Privacy & security", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textC)
        Text("How to keep your TLF account safe", fontSize = 13.sp, color = subC)
        Spacer(Modifier.height(16.dp))

        tips.forEach { (title, body) ->
            Card(Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = cardBg), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textC)
                    Spacer(Modifier.height(4.dp))
                    Text(body, fontSize = 13.sp, color = subC)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Your name and volunteer applications are only used by The Leadership Foundation to run programmes. We do not sell your data.", fontSize = 12.sp, color = subC)
    }
}