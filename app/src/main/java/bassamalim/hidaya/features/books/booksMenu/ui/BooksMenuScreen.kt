package bassamalim.hidaya.features.books.booksMenu.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bassamalim.hidaya.R
import bassamalim.hidaya.core.data.room.models.Book
import bassamalim.hidaya.core.enums.DownloadState
import bassamalim.hidaya.core.ui.components.MyButtonSurface
import bassamalim.hidaya.core.ui.components.MyCircularProgressIndicator
import bassamalim.hidaya.core.ui.components.MyFloatingActionButton
import bassamalim.hidaya.core.ui.components.MyIconButton
import bassamalim.hidaya.core.ui.components.MyLazyColumn
import bassamalim.hidaya.core.ui.components.MyScaffold
import bassamalim.hidaya.core.ui.components.TutorialDialog
import bassamalim.hidaya.core.ui.theme.AppTheme
import bassamalim.hidaya.core.utils.FileUtils

@Composable
fun BooksMenuScreen(
    viewModel: BooksMenuViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(key1 = viewModel) {
        viewModel.onStart()
        onDispose {}
    }

    MyScaffold(
        title = stringResource(R.string.hadeeth_books),
        fab = {
            MyFloatingActionButton(
                iconId = R.drawable.ic_quran_search,
                description = stringResource(R.string.search_in_books),
                onClick = { viewModel.onFabClick() }
            )
        }
    ) {
        // books list
        MyLazyColumn(
            Modifier.padding(vertical = 5.dp),
            lazyList = {
                items(state.books) { item ->
                    BookCard(
                        item = item,
                        downloadState = state.downloadStates[item.id]!!,
                        onItemClick = viewModel::onItemClick,
                        onDownloadButtonClick = viewModel::onDownloadButtonClick
                    )
                }
            }
        )

        // tutorial dialog
        TutorialDialog(
            textResId = R.string.books_activity_tips,
            shown = state.tutorialDialogShown,
            onDismiss = viewModel::onTutorialDialogDismiss
        )

        if (state.shouldShowWait != 0) {
            WaitMessage(state.shouldShowWait)
        }
    }
}

@Composable
private fun BookCard(
    item: Book,
    downloadState: DownloadState,
    onItemClick: (Book) -> Unit,
    onDownloadButtonClick: (Book) -> Unit,
) {
    MyButtonSurface(
        text = item.titleAr,
        innerVPadding = 15.dp,
        fontSize = 22.sp,
        modifier = Modifier.padding(vertical = 2.dp),
        iconButton = {
            DownloadBtn(
                downloadState = downloadState,
                onClick = { onDownloadButtonClick(item) }
            )
        },
        onClick = { onItemClick(item) }
    )
}

@Composable
private fun DownloadBtn(
    downloadState: DownloadState,
    onClick: () -> Unit,
) {
    Box(
        Modifier.padding(end = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (downloadState == DownloadState.DOWNLOADING)
            MyCircularProgressIndicator(Modifier.size(32.dp))
        else {
            MyIconButton(
                iconId =
                    if (downloadState == DownloadState.DOWNLOADED) R.drawable.ic_downloaded
                    else R.drawable.ic_download,
                description = stringResource(R.string.download_description),
                size = 32.dp,
                innerPadding = 6.dp,
                tint = AppTheme.colors.accent,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun WaitMessage(
    shouldShowWait: Int
) {
    val ctx = LocalContext.current
    LaunchedEffect(shouldShowWait) {
        FileUtils.showWaitMassage(ctx)
    }
}