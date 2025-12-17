/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.moviechecker.ui.drawer

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.moviechecker.MoviesRoute
import ru.moviechecker.R
import ru.moviechecker.SitesRoute
import ru.moviechecker.ui.theme.CheckerTheme

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    currentRoute: Any,
    navigateToSites: () -> Unit,
    navigateToMovies: (siteId: Int?) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppDrawerViewModel
) {
    val sites by viewModel.sites.collectAsStateWithLifecycle()

    AppDrawerScreen(drawerState = drawerState,
        currentRoute = currentRoute,
        sitesProvider = { sites },
        navigateToSites = navigateToSites,
        navigateToMovies = navigateToMovies,
        closeDrawer = closeDrawer,
        modifier = modifier
    )
}

@Composable
fun AppDrawerScreen(
    drawerState: DrawerState,
    currentRoute: Any,
    sitesProvider: () -> List<SiteModel>,
    navigateToSites: () -> Unit,
    navigateToMovies: (siteId: Int?) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,

) {
    ModalDrawerSheet(
        drawerState = drawerState,
        modifier = modifier,
    ) {
        LazyColumn {
            item {
                Text(stringResource(id = R.string.driwer_item_menu_title), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.driwer_item_sites_title)) },
                    icon = { Icon(imageVector = ImageVector.vectorResource(R.drawable.list_24px), contentDescription = null) },
                    selected = currentRoute is SitesRoute,
                    onClick = { navigateToSites(); closeDrawer() },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(stringResource(R.string.driwer_item_movies_title), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.driwer_item_all_sites_title)) },
                    icon = { Icon(imageVector = ImageVector.vectorResource(R.drawable.list_24px), contentDescription = null) },
                    selected = currentRoute is MoviesRoute,
                    onClick = { navigateToMovies(null); closeDrawer() },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
            items(items = sitesProvider(), key = { it.id }) { site ->
                NavigationDrawerItem(
                    label = { Text(site.title) },
                    icon = { Icon(imageVector = ImageVector.vectorResource(R.drawable.list_24px), contentDescription = null) },
                    selected = currentRoute is MoviesRoute,
                    onClick = { navigateToMovies(site.id); closeDrawer() },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }
}

@Preview(name = "Drawer contents", locale = "ru")
@Preview(name = "Drawer contents (dark)", locale = "ru", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    CheckerTheme {
        AppDrawerScreen(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            currentRoute = MoviesRoute,
            sitesProvider = { listOf(
                SiteModel(id = 1, title = "Site 1"),
                SiteModel(id = 2, title = "Site 2")
            ) },
            navigateToSites = { },
            navigateToMovies = { },
            closeDrawer = { }
        )
    }
}
