package com.aristidevs.masterclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.room.Room
import com.aristidevs.masterclass.notes.data.NotesDatabase
import com.aristidevs.masterclass.notes.data.RoomNotesRepository
import com.aristidevs.masterclass.notes.ui.NotesScreen
import com.aristidevs.masterclass.notes.ui.NotesViewModel
import com.aristidevs.masterclass.ui.theme.MasterclassTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Wiring muy simple para la masterclass:
        // aquí creamos la BD, el repositorio y el ViewModel sin DI.
        val db = Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java,
            "notes-db"
        ).build()

        val repository = RoomNotesRepository(db.noteDao())
        val viewModel = NotesViewModel(repository)

        setContent {
            MasterclassTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NotesScreen(viewModel = viewModel)
                }
            }
        }
    }
}