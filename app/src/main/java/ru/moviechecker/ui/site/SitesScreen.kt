package ru.moviechecker.ui.site

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.core.net.toUri
import ru.moviechecker.R
import ru.moviechecker.ui.common.CommonList
import ru.moviechecker.ui.common.openInBrowser
import ru.moviechecker.ui.theme.MoviecheckerTheme

@Composable
fun SitesScreen(
    sitesProvider: () -> List<SiteModel>,
    onClickOnItem: (SiteModel) -> Unit = {}
) {
    CommonList(
        items = sitesProvider(),
        itemContent = { site ->
            SiteCardContent(
                dataProvider = { site }
            )
        },
        modifier = Modifier.fillMaxSize(),
        onItemClick = onClickOnItem
    )
}

@Composable
fun SiteCardContent(
    dataProvider: () -> SiteModel
) {
    val context = LocalContext.current
    val site = dataProvider()

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
                onClick = { openInBrowser(context, site.address.toUri()) }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.open_in_new_24px),
                    contentDescription = stringResource(R.string.cd_open_navigation_drawer)
                )
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