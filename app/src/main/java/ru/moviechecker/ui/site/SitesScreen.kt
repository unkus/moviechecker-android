package ru.moviechecker.ui.site

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.moviechecker.R
import ru.moviechecker.ui.theme.MoviecheckerTheme

@Composable
fun SitesScreen(
    sitesProvider: () -> List<SiteModel>,
    onClickOnItem: (SiteModel) -> Unit = {}
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = sitesProvider(), key = { it.id }) { site ->
            SiteCard(site = site, onClick = onClickOnItem)
        }
    }
}

@Composable
fun SiteCard(site: SiteModel, onClick: (SiteModel) -> Unit = {}) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick(site) }
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = site.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            site.address.toUri()
                        )
                        context.startActivity(browserIntent)
                    }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.open_in_new_24px),
                        contentDescription = stringResource(R.string.cd_open_navigation_drawer)
                    )
                }
            }
        }
    }
}

@Composable
@Preview("Sites", locale = "ru-RU")
@Preview("Sites (dark)", locale = "ru-RU", uiMode = UI_MODE_NIGHT_YES)
fun SitesScreenPreview() {
    MoviecheckerTheme {
        SitesScreen(
            sitesProvider = {
                listOf(
                    SiteModel(
                        id = 1,
                        mnemonic = "site_1",
                        title = "Site 1",
                        address = "http://site1.ru",
                    ),
                    SiteModel(
                        id = 2,
                        mnemonic = "site_2",
                        title = "Site 2",
                        address = "http://site2.ru",
                    )
                )
            }
        )
    }
}