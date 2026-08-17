package ru.moviechecker.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    label: Int,
    errorViewModel: ErrorViewModel = viewModel(),
    actions: @Composable () -> Unit,
    content: @Composable (innerPadding: PaddingValues) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        errorViewModel.errorEvent.collect { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(label)) },
                actions = { actions() }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
