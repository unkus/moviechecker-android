package ru.moviechecker.ui.site

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.moviechecker.R
import ru.moviechecker.ui.theme.CheckerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteDetailsScreen(
    uiState: SiteDetailsUiState?,
    onUseMirrorToggle: (Boolean) -> Unit = {},
    onMirrorChanged: (String) -> Unit = {},
    navigateBack: () -> Unit = {}
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SiteDetailsTopAppBar(
                topAppBarState = topAppBarState,
                navigateBack = navigateBack
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            uiState?.let { site ->
                site.poster?.let {
                    Poster(
                        it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(CenterHorizontally)
                    )
                }
                OutlinedTextField(
                    value = site.mnemonic,
                    label = { Text(stringResource(R.string.site_mnemonic_label)) },
                    textStyle = MaterialTheme.typography.titleMedium,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = site.title ?: "",
                    label = { Text(stringResource(R.string.site_title_label)) },
                    textStyle = MaterialTheme.typography.titleMedium,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = site.address,
                    label = { Text(stringResource(R.string.site_address_label)) },
                    textStyle = MaterialTheme.typography.titleMedium,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth()
                )
                Card(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.site_use_mirror_label),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Checkbox(checked = site.useMirror, onCheckedChange = onUseMirrorToggle)
                    }
                }
                OutlinedTextField(
                    value = site.mirror ?: "",
                    label = { Text(stringResource(R.string.site_mirror_label)) },
                    textStyle = MaterialTheme.typography.titleMedium,
                    onValueChange = onMirrorChanged,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SiteDetailsTopAppBar(
    navigateBack: () -> Unit = {},
    topAppBarState: TopAppBarState = rememberTopAppBarState(),
    scrollBehavior: TopAppBarScrollBehavior? =
        TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
) {
    val title = stringResource(id = R.string.movie_details_title)
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(title)
        },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.arrow_back_24px),
                    contentDescription = stringResource(R.string.cd_back)
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun Poster(
    data: ByteArray,
    modifier: Modifier = Modifier
) {
    val image = try {
        BitmapFactory.decodeByteArray(
            data,
            0,
            data.size
        )
            .asImageBitmap()
    } catch (exception: Exception) {
        return
    }
    Image(
        bitmap = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
    )
}

@Preview("Site details")
@Preview("Site details (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewSiteDetailsScreen() {
    CheckerTheme {
        SiteDetailsScreen(
            uiState = SiteDetailsUiState(
                id = 1,
                mnemonic = "site",
                address = "address",
                mirror = "mirror"
            )
        )
    }
}
