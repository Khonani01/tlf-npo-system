package org.tlf.connect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import org.tlf.connect.screens.DonateScreen
import org.tlf.connect.screens.EditProfileScreen
import org.tlf.connect.screens.HomeScreen
import org.tlf.connect.screens.LoginScreen
import org.tlf.connect.screens.PrivacyScreen
import org.tlf.connect.screens.ProfileScreen
import org.tlf.connect.screens.RegisterScreen
import org.tlf.connect.screens.VolunteerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var loggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
            var showRegister by remember { mutableStateOf(false) }
            var tab by remember { mutableStateOf(0) }
            var showEdit by remember { mutableStateOf(false) }
            var showPrivacy by remember { mutableStateOf(false) }

            // Language from AppStore - this drives bottom bar
            val lang = AppStore.language.value
            val isZulu = lang == "Zulu" || lang == "isiZulu"
            val navLabels = if (isZulu) listOf("Ikhaya", "Nikela", "Volontiya", "Iphrofayela")
            else listOf("Home", "Donate", "Volunteer", "Profile")

            if (!loggedIn) {
                if (showRegister) {
                    RegisterScreen(onRegistered = { showRegister = false }, onBack = { showRegister = false })
                } else {
                    LoginScreen(onLogin = { loggedIn = true }, onGoogleLogin = { loggedIn = true }, onGoRegister = { showRegister = true })
                }
            } else if (showEdit) {
                // Edit Profile screen - can change name + save
                EditProfileScreen(onBack = { showEdit = false })
            } else if (showPrivacy) {
                // Privacy & Security screen - safety tips inside
                PrivacyScreen(onBack = { showPrivacy = false })
            } else {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            listOf(navLabels[0] to Icons.Filled.Home, navLabels[1] to Icons.Filled.Favorite, navLabels[2] to Icons.Filled.People, navLabels[3] to Icons.Filled.Person).forEachIndexed { index, (label, icon) ->
                                NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = { Icon(icon, null) }, label = { Text(label, fontSize = 11.sp) })
                            }
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        when (tab) {
                            0 -> HomeScreen(onVolunteer = { tab = 2 }, onDonate = { tab = 1 })
                            1 -> DonateScreen()
                            2 -> VolunteerScreen(onBack = { tab = 0 })
                            3 -> ProfileScreen(
                                onLogout = { FirebaseAuth.getInstance().signOut(); loggedIn = false },
                                onEditProfile = { showEdit = true }, // FIXED: was {}
                                onPrivacy = { showPrivacy = true }   // FIXED: was {}
                            )
                        }
                    }
                }
            }
        }
    }
}