package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.ExamKraftApp
import com.example.ui.theme.ExamKraftTheme
import com.example.ui.viewmodel.ExamViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ExamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamKraftTheme {
                ExamKraftApp(viewModel = viewModel)
            }
        }
    }
}
