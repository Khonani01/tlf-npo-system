package org.tlf.connect

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

val TlfOrange = Color(0xFFFF8A00)

object AppStore {
    var userName = mutableStateOf("WELCOME TO TLF")
    var userPhotoUri = mutableStateOf<Uri?>(null)
    var darkMode = mutableStateOf(false)
    var language = mutableStateOf("English")
    var draftName = mutableStateOf("")
    var draftEmail = mutableStateOf("")
    var draftPhone = mutableStateOf("")
}