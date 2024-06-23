package bassamalim.hidaya.features.books.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bassamalim.hidaya.core.data.database.dbs.BooksDB
import bassamalim.hidaya.core.enums.DownloadState
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.nav.Navigator
import bassamalim.hidaya.core.nav.Screen
import bassamalim.hidaya.features.books.domain.BooksDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    app: Application,
    private val domain: BooksDomain,
    private val navigator: Navigator
): AndroidViewModel(app) {

    private lateinit var language: Language

    private val _uiState = MutableStateFlow(BooksUiState(
        items = domain.getBooks()
    ))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            language = domain.getLanguage()

            _uiState.update { it.copy(
                tutorialDialogShown = domain.getShowTutorial()
            )}
        }
    }

    fun onStart() {
        _uiState.update { it.copy(
            downloadStates = domain.getDownloadStates(books = it.items)
        )}
    }

    fun onFabClick() {
        navigator.navigate(Screen.BookSearcher)
    }

    fun onItemClick(item: BooksDB) {
        when (_uiState.value.downloadStates[item.id]!!) {
            DownloadState.NotDownloaded -> {
                _uiState.update { it.copy(
                    downloadStates = it.downloadStates.toMutableMap().apply {
                        this[item.id] = DownloadState.Downloading
                    }
                )}

                domain.download(
                    bookId = item.id,
                    onDownloadedCallback = {
                        _uiState.update { it.copy(
                            downloadStates = it.downloadStates.toMutableMap().apply {
                                this[item.id] = DownloadState.Downloaded
                            }
                        )}
                    }
                )
            }
            DownloadState.Downloaded -> {
                navigator.navigate(
                    Screen.BookChapters(
                        bookId = item.id.toString(),
                        bookTitle =
                            if (language == Language.ENGLISH) item.titleEn
                            else item.title
                    )
                )
            }
            DownloadState.Downloading -> {
                _uiState.update { it.copy(
                    shouldShowWait = it.shouldShowWait + 1
                )}
            }
        }
    }

    fun onDownloadButtonClick(item: BooksDB) {
        when (_uiState.value.downloadStates[item.id]!!) {
            DownloadState.NotDownloaded -> {
                _uiState.update { it.copy(
                    downloadStates = it.downloadStates.toMutableMap().apply {
                        this[item.id] = DownloadState.Downloading
                    }
                )}

                domain.download(
                    bookId = item.id,
                    onDownloadedCallback = {
                        _uiState.update { it.copy(
                            downloadStates = it.downloadStates.toMutableMap().apply {
                                this[item.id] = DownloadState.Downloaded
                            }
                        )}
                    }
                )
            }
            DownloadState.Downloaded -> {
                _uiState.update { it.copy(
                    downloadStates = it.downloadStates.toMutableMap().apply {
                        this[item.id] = DownloadState.NotDownloaded
                    }
                )}

                domain.deleteBook(item.id)
            }
            DownloadState.Downloading -> {
                _uiState.update { it.copy(
                    shouldShowWait = it.shouldShowWait + 1
                )}
            }
        }
    }

    fun onTutorialDialogDismiss(doNotShowAgain: Boolean) {
        _uiState.update { it.copy(
            tutorialDialogShown = false
        )}

        viewModelScope.launch {
            domain.handleTutorialDialogDismiss(doNotShowAgain)
        }
    }

}