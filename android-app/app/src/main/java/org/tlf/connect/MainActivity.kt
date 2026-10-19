package org.tlf.connect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tlf.connect.ui.theme.TLFConnectTheme

val TlfOrange = Color(0xFFFF8C00)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TLFConnectTheme { TLFApp() } }
    }
}

@Composable
fun TLFApp() {
    var loggedIn by remember { mutableStateOf(false) }
    var tab by remember { mutableStateOf(0) }
    var showVolunteer by remember { mutableStateOf(false) }

    if (!loggedIn) {
        LoginScreen { loggedIn = true }
    } else {
        Scaffold(
            bottomBar = {
                if (!showVolunteer) {
                    NavigationBar {
                        NavigationBarItem(selected = tab==0, onClick = { tab=0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                        NavigationBarItem(selected = tab==1, onClick = { tab=1 }, icon = { Icon(Icons.Default.Event, null) }, label = { Text("Events") })
                        NavigationBarItem(selected = tab==2, onClick = { tab=2 }, icon = { Icon(Icons.Default.VolunteerActivism, null) }, label = { Text("Give") })
                        NavigationBarItem(selected = tab==3, onClick = { tab=3 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
                    }
                }
            }
        ) { p ->
            Box(Modifier.padding(p)) {
                if (showVolunteer) VolunteerScreen { showVolunteer = false }
                else when(tab) {
                    0 -> HomeScreen(onVolunteer = { showVolunteer=true }, onDonate = { tab=2 })
                    1 -> NewsEventsScreen()
                    2 -> DonateScreen()
                    3 -> ProfileScreen { loggedIn=false }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(Color(0xFF121212)).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(70.dp).clip(CircleShape).background(TlfOrange), contentAlignment = Alignment.Center) { Text("TLF", color = Color.White, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp)); Text("Welcome back", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp); Text("Sign in to continue making an impact", color = Color.Gray, fontSize = 12.sp)
        }
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Email address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Password") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = TlfOrange)) { Text("Log in") }
            OutlinedButton(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("Continue with Google") }
        }
    }
}

@Composable
fun HomeScreen(onVolunteer: () -> Unit, onDonate: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Column(Modifier.fillMaxWidth().background(Color(0xFF121212), RoundedCornerShape(12.dp)).padding(16.dp)) { Text("Good afternoon,", color = Color.Gray); Text("Thabo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp) } }
        item { Box(Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(12.dp)).background(Brush.horizontalGradient(listOf(Color(0xFFFFD180), TlfOrange))).padding(16.dp)) { Column { Text("Our impact this year", color = Color.White, fontSize = 12.sp); Text("500+", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold) } } }
        item { Text("Quick actions", fontWeight = FontWeight.Bold) }
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) { Card(Modifier.weight(1f).clickable { onVolunteer() }) { Column(Modifier.padding(16.dp)) { Icon(Icons.Default.VolunteerActivism, null, tint = TlfOrange); Text("Volunteer", fontWeight = FontWeight.Bold) } }; Card(Modifier.weight(1f).clickable { onDonate() }) { Column(Modifier.padding(16.dp)) { Icon(Icons.Default.Favorite, null, tint = TlfOrange); Text("Donate", fontWeight = FontWeight.Bold) } } } }
        item { Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(12.dp)) { Box(Modifier.size(45.dp).clip(RoundedCornerShape(8.dp)).background(TlfOrange), contentAlignment = Alignment.Center) { Text("APR\n12", color = Color.White, fontSize = 12.sp) }; Spacer(Modifier.width(12.dp)); Column { Text("Youth Leadership Summit", fontWeight = FontWeight.Bold); Text("09:00 · TLF Community Hall", fontSize = 11.sp, color = Color.Gray) } } } }
    }
}

@Composable
fun NewsEventsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("News & Events", fontWeight = FontWeight.Bold, fontSize = 20.sp); Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth().padding(bottom=12.dp)) { Column { Box(Modifier.fillMaxWidth().height(80.dp).background(Color(0xFFFFCC80))); Column(Modifier.padding(12.dp)) { Text("Youth Leadership Workshop", fontWeight=FontWeight.Bold); Text("15 Mar 2026", fontSize=11.sp, color=Color.Gray) } } }
        Card(Modifier.fillMaxWidth().padding(bottom=12.dp)) { Column { Box(Modifier.fillMaxWidth().height(80.dp).background(Color(0xFFA5D6A7))); Column(Modifier.padding(12.dp)) { Text("Community Development Program", fontWeight=FontWeight.Bold); Text("10 Mar 2026", fontSize=11.sp, color=Color.Gray) } } }
    }
}

@Composable
fun DonateScreen() {
    LazyColumn(Modifier.fillMaxSize()) {
        item { Box(Modifier.fillMaxWidth().background(TlfOrange).padding(16.dp)) { Column { Text("Support our mission", color=Color.White, fontWeight=FontWeight.Bold, fontSize=18.sp); Text("Every contribution builds stronger communities", color=Color.White, fontSize=11.sp) } } }
        item { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { listOf("R50","R100","R200").forEach { Text(it, modifier=Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFE0B2)).padding(12.dp), color=TlfOrange, fontWeight=FontWeight.Bold) } }; OutlinedTextField(value="", onValueChange={}, placeholder={Text("Full name")}, modifier=Modifier.fillMaxWidth()); OutlinedTextField(value="", onValueChange={}, placeholder={Text("Email address")}, modifier=Modifier.fillMaxWidth()); Button(onClick={}, modifier=Modifier.fillMaxWidth().height(50.dp), colors=ButtonDefaults.buttonColors(containerColor=TlfOrange)) { Text("Donate now") } } }
    }
}

@Composable
fun VolunteerScreen(onBack: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { Text("Volunteer with us", fontWeight=FontWeight.Bold, fontSize=20.sp) }
        item { OutlinedTextField(value="", onValueChange={}, placeholder={Text("Full name")}, modifier=Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value="", onValueChange={}, placeholder={Text("Email address")}, modifier=Modifier.fillMaxWidth()) }
        item { OutlinedTextField(value="", onValueChange={}, placeholder={Text("Phone number")}, modifier=Modifier.fillMaxWidth()) }
        item { Text("Skill area: Teaching, Mentoring, Events, IT", fontSize=12.sp, color=Color.Gray) }
        item { OutlinedTextField(value="", onValueChange={}, placeholder={Text("Why do you want to volunteer?")}, modifier=Modifier.fillMaxWidth().height(90.dp)) }
        item { Button(onClick=onBack, modifier=Modifier.fillMaxWidth().height(50.dp), colors=ButtonDefaults.buttonColors(containerColor=TlfOrange)) { Text("Apply now") } }
    }
}

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(Color(0xFF121212)).padding(24.dp), horizontalAlignment=Alignment.CenterHorizontally) {
            Box(Modifier.size(70.dp).clip(CircleShape).background(TlfOrange), contentAlignment=Alignment.Center) { Text("TN", color=Color.White, fontWeight=FontWeight.Bold, fontSize=22.sp) }
            Spacer(Modifier.height(8.dp)); Text("Thabo Nkosi", color=Color.White, fontWeight=FontWeight.Bold); Text("Volunteer since 2024", color=Color.Gray, fontSize=11.sp)
        }
        Column(Modifier.padding(16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
            listOf("Edit profile","Notification preferences","Privacy & security","Language").forEach { title -> Card(Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween) { Text(title, fontWeight=FontWeight.Bold, fontSize=13.sp); Text(">", color=Color.Gray) } } }
            Spacer(Modifier.height(16.dp)); OutlinedButton(onClick=onLogout, modifier=Modifier.fillMaxWidth()) { Text("Log out", color=Color.Red) }
        }
    }
}