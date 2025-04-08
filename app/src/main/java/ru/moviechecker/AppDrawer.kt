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

package ru.moviechecker

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.moviechecker.ui.theme.CheckerTheme

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    currentRoute: Any,
    navigateToSites: () -> Unit,
    navigateToMovies: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        drawerState = drawerState,
        modifier = modifier,
    ) {
        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.sites_title)) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
            selected = currentRoute is SitesRoute,
            onClick = { navigateToSites(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(id = R.string.movies_title)) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
            selected = currentRoute is MoviesRoute,
            onClick = { navigateToMovies(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    CheckerTheme {
        AppDrawer(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            currentRoute = MoviesRoute,
            navigateToSites = { },
            navigateToMovies = { },
            closeDrawer = { }
        )
    }
}
