package ru.moviechecker

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ru.moviechecker.R.string
import ru.moviechecker.ui.home.HomeDestination
import ru.moviechecker.ui.navigation.CheckerNavHost
import ru.moviechecker.ui.theme.MoviecheckerTheme

/**
 * Top level composable that represents screens for the application.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CheckerApp(navController: NavHostController = rememberNavController()) {
    CheckerNavHost(navController = navController)
}

/**
 * App bar to display title and conditionally display the back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckerTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {},
    onFavoritesClicked: () -> Unit = {},
    onViewedClicked: () -> Unit = {}
) {
    val nonFavoritesVisibilityState = remember {
        mutableStateOf(true)
    }
    val viewedVisibilityState = remember {
        mutableStateOf(true)
    }
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(onClick = {
                onFavoritesClicked()
                nonFavoritesVisibilityState.value = !nonFavoritesVisibilityState.value
            }) {
                Icon(
                    imageVector = if (nonFavoritesVisibilityState.value) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                    contentDescription = null,
                    tint = if (nonFavoritesVisibilityState.value) Color.Gray else Color.Yellow
                )
            }
            IconButton(onClick = {
                onViewedClicked()
                viewedVisibilityState.value = !viewedVisibilityState.value
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (viewedVisibilityState.value) Color.Green else Color.Gray
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CheckerTopAppBarPreview() {
    MoviecheckerTheme {
        CheckerTopAppBar(title = stringResource(HomeDestination.titleRes),
            canNavigateBack = false,
            onFavoritesClicked = {},
            onViewedClicked = {}
        )
    }
}