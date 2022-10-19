package bassamalim.hidaya.activities

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.preference.PreferenceManager
import bassamalim.hidaya.R
import bassamalim.hidaya.database.AppDatabase
import bassamalim.hidaya.database.dbs.TelawatDB
import bassamalim.hidaya.enums.ListType
import bassamalim.hidaya.helpers.Keeper
import bassamalim.hidaya.models.Reciter
import bassamalim.hidaya.models.Reciter.RecitationVersion
import bassamalim.hidaya.ui.components.*
import bassamalim.hidaya.ui.theme.AppTheme
import bassamalim.hidaya.utils.ActivityUtils
import bassamalim.hidaya.utils.DBUtils
import bassamalim.hidaya.utils.FileUtils
import bassamalim.hidaya.utils.PrefUtils
import com.google.gson.Gson
import java.io.File
import java.util.*
import java.util.concurrent.Executors

@RequiresApi(api = Build.VERSION_CODES.O)
class TelawatActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private lateinit var pref: SharedPreferences
    private var gson: Gson = Gson()
    private val prefix = "/Telawat/"
    private val continueListeningMediaId = mutableStateOf("")
    private var continueListeningText = ""
    private val filteredState = mutableStateOf(false)
    private lateinit var rewayat: Array<String>
    private var favs = mutableStateListOf<Int>()
    private val downloadStates = mutableStateListOf(mutableStateListOf<String>())
    private lateinit var selectedVersions: BooleanArray
    private val downloading = HashMap<Long, Pair<Int, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.onActivityCreateSetLocale(this)

        init()

        setupFavs()

        setContent {
            AppTheme {
                UI()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        try {
            unregisterReceiver(onComplete)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()

        setupContinue()

        clean()
        initDownloadStates()

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun onBack() {
        val intent = Intent(this, MainActivity::class.java)
        val location = Keeper(this).retrieveLocation()
        intent.putExtra("located", location != null)
        intent.putExtra("location", location)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        onBack()
    }

    private fun init() {
        db = DBUtils.getDB(this)
        pref = PreferenceManager.getDefaultSharedPreferences(this)

        rewayat = resources.getStringArray(R.array.rewayat)

        initFilterIb()
        initDownloadStates()
    }

    private fun initFilterIb() {
        selectedVersions = getSelectedVersions()
        for (bool in selectedVersions) {
            if (!bool) {
                filteredState.value = true
                break
            }
        }
    }

    private fun setupFavs() {
        for (fav in db.telawatRecitersDao().getFavs()) favs.add(fav)
    }

    private fun initDownloadStates() {
        for (telawa in db.telawatDao().all) {  // all versions
            val state =
                if (isDownloaded("${telawa.getReciterId()}/${telawa.getVersionId()}"))
                    "downloaded"
                else "not downloaded"

            if (telawa.getReciterId() == downloadStates.size)
                downloadStates.add(mutableStateListOf(state))
            else
                downloadStates[telawa.getReciterId()].add(state)
        }
    }

    private fun getSelectedVersions(): BooleanArray {
        val defArr = BooleanArray(rewayat.size)
        Arrays.fill(defArr, true)
        val defStr = gson.toJson(defArr)
        return gson.fromJson(pref.getString("selected_rewayat", defStr), BooleanArray::class.java)
    }

    private fun setupContinue() {
        continueListeningMediaId.value = pref.getString("last_played_media_id", "")!!

        if (continueListeningMediaId.value.isEmpty())
            continueListeningText = getString(R.string.no_last_play)
        else {
            val reciterId = continueListeningMediaId.value.substring(0, 3).toInt()
            val versionId = continueListeningMediaId.value.substring(3, 5).toInt()
            val suraIndex = continueListeningMediaId.value.substring(5).toInt()

            val suraName =
                if (PrefUtils.getLanguage(this, pref) == "en")
                    db.suarDao().getNameEn(suraIndex)
                else db.suarDao().getName(suraIndex)
            val reciterName = db.telawatRecitersDao().getName(reciterId)
            val rewaya = db.telawatVersionsDao().getVersion(reciterId, versionId).getRewaya()

            val text = "${getString(R.string.last_play)}: " +
                    "${getString(R.string.sura)} $suraName " +
                    "${getString(R.string.for_reciter)} $reciterName " +
                    "${getString(R.string.in_rewaya_of)} $rewaya"
            continueListeningText = text
        }
    }

    private fun getItems(type: ListType): List<Reciter> {
        val reciters = db.telawatRecitersDao().getAll()

        val items = ArrayList<Reciter>()
        for (i in reciters.indices) {
            val reciter = reciters[i]

            if ((type == ListType.Favorite && favs[i] == 0) ||
                (type == ListType.Downloaded && !isDownloaded("${reciter.id}")))
                continue

            val versions = filterSelectedVersions(db.telawatDao().getReciterTelawat(reciter.id))
            val versionsList = ArrayList<RecitationVersion>()

            for (j in versions.indices) {
                val telawa = versions[j]
                versionsList.add(
                    RecitationVersion(
                        telawa.getVersionId(), telawa.getUrl(), telawa.getRewaya(),
                        telawa.getCount(), telawa.getSuras()
                    )
                )
            }
            items.add(Reciter(reciter.id, reciter.name!!, versionsList))
        }
        return items
    }

    private fun filterSelectedVersions(versions: List<TelawatDB>): List<TelawatDB> {
        if (!filteredState.value) return versions

        val selected = mutableListOf<TelawatDB>()
        for (i in versions.indices) {
            for (j in rewayat.indices) {
                if (selectedVersions[j] && versions[i].getRewaya().startsWith(rewayat[j])) {
                    selected.add(versions[i])
                    break
                }
            }
        }

        return selected
    }

    private fun updateFavorites() {
        val favReciters = db.telawatRecitersDao().getFavs()
        val recitersJson = gson.toJson(favReciters)
        pref.edit()
            .putString("favorite_reciters", recitersJson)
            .apply()
    }

    private fun isDownloaded(suffix: String): Boolean {
        return File(
            "${getExternalFilesDir(null)}$prefix$suffix"
        ).exists()
    }

    private fun downloadVer(reciterId: Int, ver: RecitationVersion) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var request: DownloadManager.Request
        var posted = false
        for (i in 0..113) {
            if (ver.getSuras().contains("," + (i + 1) + ",")) {
                val link = String.format(Locale.US, "%s/%03d.mp3", ver.getServer(), i + 1)
                val uri = Uri.parse(link)

                request = DownloadManager.Request(uri)
                request.setTitle("${db.telawatRecitersDao().getName(reciterId)} ${ver.getRewaya()}")
                val suffix = "$prefix$reciterId/${ver.getVersionId()}"
                FileUtils.createDir(this, suffix)
                request.setDestinationInExternalFilesDir(this, suffix, "$i.mp3")
                request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )

                val downloadId = downloadManager.enqueue(request)

                if (!posted) {
                    downloadStates[reciterId][ver.getVersionId()] = "downloading"
                    downloading[downloadId] = Pair(reciterId, ver.getVersionId())
                    posted = true
                }
            }
        }
    }

    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            try {
                val ids = downloading[downloadId]!!
                downloadStates[ids.first][ids.second] = "downloaded"
                downloading.remove(downloadId)
            } catch (e: RuntimeException) {
                for (i in downloadStates.indices) downloadStates[i] = downloadStates[i]
            }
        }
    }

    private fun clean() {
        val mainDir = File("${getExternalFilesDir(null)}/Telawat/")
        FileUtils.deleteDirRecursive(mainDir)
    }

    @Composable
    private fun UI() {
        MyScaffold(
            stringResource(R.string.recitations),
            onBack = { onBack() }
        ) {
            val textState = remember { mutableStateOf(TextFieldValue("")) }

            Column {
                MyButton(
                    text = continueListeningText,
                    fontSize = 18.sp,
                    textColor = AppTheme.colors.accent,
                    modifier = Modifier.fillMaxWidth(),
                    innerPadding = PaddingValues(vertical = 4.dp)
                ) {
                    if (continueListeningMediaId.value.isNotEmpty()) {
                        val intent = Intent(
                            this@TelawatActivity, TelawatClient::class.java
                        )
                        intent.action = "continue"
                        intent.putExtra("media_id", continueListeningMediaId.value)
                        startActivity(intent)
                    }
                }

                TabLayout(
                    pageNames = listOf(getString(R.string.all), getString(R.string.favorite), getString(R.string.downloaded)),
                    searchComponent = {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SearchComponent(
                                state = textState,
                                hint = stringResource(id = R.string.search)
                            )

                            MyIconBtn(
                                iconId = R.drawable.ic_filter,
                                description = stringResource(id = R.string.filter_search_description),
                                tint =
                                if (filteredState.value) AppTheme.colors.secondary
                                else AppTheme.colors.weakText
                            ) {
                                /*FilterDialog(
                                    requireContext(), v, resources.getString(R.string.choose_rewaya),
                                    rewayat, selectedRewayat, adapter!!, binding!!.filterIb,
                                    "selected_rewayat"
                                )*/
                            }
                        }
                    }
                ) { page ->
                    Tab(items = getItems(ActivityUtils.getListType(page)), textState)
                }
            }
        }
    }

    @Composable
    private fun Tab(
        items: List<Reciter>,
        textState: MutableState<TextFieldValue>
    ) {
        MyLazyColumn(
            lazyList = {
                items(
                    items = items.filter { item ->
                        item.name.contains(textState.value.text, ignoreCase = true)
                    }
                ) { item ->
                    ReciterCard(reciter = item)
                }
            }
        )
    }

    @Composable
    private fun ReciterCard(reciter: Reciter) {
        Surface(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp),
            elevation = 10.dp,
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
            color = AppTheme.colors.primary
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 3.dp, start = 10.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MyText(reciter.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    MyFavBtn(favs[reciter.id]) {
                        if (favs[reciter.id] == 0) {
                            favs[reciter.id] = 1
                            db.telawatRecitersDao().setFav(reciter.id, 1)
                        }
                        else if (favs[reciter.id] == 1) {
                            favs[reciter.id] = 0
                            db.telawatRecitersDao().setFav(reciter.id, 0)
                        }
                        updateFavorites()
                    }
                }

                MyHorizontalDivider(thickness = 2.dp)

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    for (version in reciter.versions)
                        VersionCard(reciterId = reciter.id, version = version)
                }
            }
        }
    }

    @Composable
    private fun VersionCard(reciterId: Int, version: RecitationVersion) {
        if (version.getVersionId() != 0) MyHorizontalDivider()

        Box(
            Modifier.clickable {
                val intent = Intent(
                    this@TelawatActivity, TelawatSuarActivity::class.java
                )
                intent.putExtra("reciter_id", reciterId)
                intent.putExtra("version_id", version.getVersionId())
                startActivity(intent)
            }
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp, start = 15.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MyText(text = version.getRewaya(), fontSize = 18.sp)

                Box(
                    Modifier.size(25.dp)
                ) {
                    val downloadState = downloadStates[reciterId][version.getVersionId()]
                    if (downloadState == "downloading") MyCircularProgressIndicator()
                    else {
                        MyIconBtn(
                            iconId =
                            if (downloadState == "downloaded") R.drawable.ic_downloaded
                            else R.drawable.ic_download,
                            description = stringResource(id = R.string.download_description),
                            tint = AppTheme.colors.accent
                        ) {
                            if (downloading.containsValue(Pair(reciterId, version.getVersionId())))
                                FileUtils.showWaitMassage(this@TelawatActivity)
                            else if (isDownloaded("${reciterId}/${version.getVersionId()}")) {
                                FileUtils.deleteFile(
                                    this@TelawatActivity,
                                    "$prefix$reciterId/${version.getVersionId()}"
                                )
                                downloadStates[reciterId][version.getVersionId()] = "not downloaded"
                            }
                            else {
                                Executors.newSingleThreadExecutor().execute {
                                    downloadVer(reciterId, version)
                                }
                                downloadStates[reciterId][version.getVersionId()] = "downloading"
                            }
                        }
                    }
                }
            }
        }
    }

}