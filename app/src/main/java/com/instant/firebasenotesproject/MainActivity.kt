package com.instant.firebasenotesproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.instant.firebasenotesproject.navigation.AppNavHost
import com.instant.firebasenotesproject.ui.theme.FirebaseNotesProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            FirebaseNotesProjectTheme {
                Column {
                    AppNavHost(modifier = Modifier)
                }
            }
        }
    }
}


