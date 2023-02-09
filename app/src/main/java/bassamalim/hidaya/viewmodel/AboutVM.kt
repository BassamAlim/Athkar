package bassamalim.hidaya.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import bassamalim.hidaya.other.Global
import bassamalim.hidaya.repository.AboutRepo
import bassamalim.hidaya.state.AboutState
import bassamalim.hidaya.utils.DBUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AboutVM @Inject constructor(
    private val app: Application,
    repo: AboutRepo
): AndroidViewModel(app) {

    private var counter by mutableStateOf(0)

    private val _uiState = MutableStateFlow(AboutState(
        lastDailyUpdate = repo.getLastUpdate()
    ))
    val uiState = _uiState.asStateFlow()

    private fun enableDevMode() {
        _uiState.update { it.copy(
            isDevModeOn = true
        )}
    }

    fun rebuildDatabase() {
        app.deleteDatabase("HidayaDB")

        Log.i(Global.TAG, "Database Rebuilt")

        DBUtils.reviveDB(app)

        _uiState.update { it.copy(
            shouldShowRebuilt = _uiState.value.shouldShowRebuilt + 1
        )}
    }

    /*fun quickUpdate() {
        val url = repo.getUpdateURL()
        val intent = Intent(Intent.ACTION_VIEW)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setData(Uri.parse(url))

        app.applicationContext.startActivity(intent)
    }*/

    fun onTitleClick() {
        if (++counter == 5) enableDevMode()
    }

}