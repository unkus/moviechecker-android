package ru.moviechecker.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import ru.moviechecker.R
import ru.moviechecker.model.Identifiable

@Composable
fun <T : Identifiable> CommonList(
    items: ListItems<T>,
    itemContent: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: (T) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(count = items.itemCount, key = { items[it]?.id ?: it }) { index ->
            val item = items[index] ?: return@items
            CommonListItem(
                item = item,
                content = itemContent,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .animateItem()
                    .clickable { onItemClick(item) })
        }
    }
}

@Composable
private fun <T : Identifiable> CommonListItem(
    item: T,
    content: @Composable (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            content(item)
        }
    }
}

interface ListItems<T> {
    val itemCount: Int
    operator fun get(index: Int): T?
}

fun <T> List<T>.asListItems() = object : ListItems<T> {
    override val itemCount = size
    override fun get(index: Int) = getOrNull(index)
}
