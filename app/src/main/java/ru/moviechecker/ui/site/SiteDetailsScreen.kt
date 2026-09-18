package ru.moviechecker.ui.site

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.moviechecker.R
import ru.moviechecker.ui.theme.MoviecheckerTheme

@Composable
fun SiteDetailsScreen(
    siteId: Int,
    viewModel: SiteDetailsViewModel = viewModel(factory = SiteDetailsViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Загружаем данные один раз при входе на экран
    LaunchedEffect(siteId) {
        viewModel.loadData(siteId)
    }

    SiteDetailsScreenContent(
        uiState = state,
        onDataChanged = { form -> viewModel.updateForm(form) },
        onSaveClick = { viewModel.save() }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteDetailsScreenContent(
    uiState: SiteDetailsUiState,
    onDataChanged: (SiteFormState) -> Unit,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
    ) {
        uiState.data.poster?.let {
            Poster(
                it,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
        }

        OutlinedTextField(
            value = uiState.form.mnemonic,
            onValueChange = { onDataChanged(uiState.form.copy(mnemonic = it)) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleMedium,
            label = { Text(stringResource(R.string.site_mnemonic_label)) }
        )

        OutlinedTextField(
            value = uiState.form.title,
            onValueChange = { onDataChanged(uiState.form.copy(title = it)) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleMedium,
            label = { Text(stringResource(R.string.site_title_label)) }
        )

        OutlinedTextField(
            value = uiState.form.address,
            onValueChange = { onDataChanged(uiState.form.copy(address = it)) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.titleMedium,
            label = { Text(stringResource(R.string.site_address_label)) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_small)),
//                horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.use_mirror))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.form.useMirror,
                onCheckedChange = { onDataChanged(uiState.form.copy(useMirror = it)) }
            )
        }

        OutlinedTextField(
            value = uiState.form.mirror,
            onValueChange = { onDataChanged(uiState.form.copy(mirror = it)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.form.useMirror,
            label = { Text("Зеркало") },
            placeholder = { Text("Введите адрес зеркала") }
        )

        Button(
            onClick = onSaveClick,
            enabled = uiState.form.isValid && (!uiState.isLoading && !uiState.isSaving),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading || uiState.isSaving) {
                CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                )
            } else {
                Text("Сохранить")
            }
        }
    }
}

@Composable
private fun Poster(
    data: ByteArray,
    modifier: Modifier = Modifier
) {
    val image = remember(data) {
        try {
            BitmapFactory.decodeByteArray(data, 0, data.size).asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
@Preview("Подробности сайта")
@Preview("Подробности сайта (тёмная тема)", uiMode = UI_MODE_NIGHT_YES)
fun PreviewSiteDetailsScreen() {
    MoviecheckerTheme {
        SiteDetailsScreenContent(
            uiState = SiteDetailsUiState(
                data = SiteData(
                    id = 1
                ),
                form = SiteFormState(
                    mnemonic = "мнемоника",
                    title = "Сайт №1",
                    address = "https://site.one"
                )
            ),
            onDataChanged = {},
            onSaveClick = {}
        )
    }
}
