package bassamalim.hidaya.features.bookSearcher

import android.content.Context
import android.util.Log
import bassamalim.hidaya.R
import bassamalim.hidaya.core.data.database.AppDatabase
import bassamalim.hidaya.core.data.preferences.Preference
import bassamalim.hidaya.core.data.preferences.PreferencesDataSource
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.models.Book
import bassamalim.hidaya.core.other.Global
import bassamalim.hidaya.core.utils.FileUtils
import com.google.gson.Gson
import java.io.File
import javax.inject.Inject

class BookSearcherRepo @Inject constructor(
    private val ctx: Context,
    private val preferencesDS: PreferencesDataSource,
    private val db: AppDatabase,
    private val gson: Gson
) {

    private val prefix = "/Books/"

    fun getLanguage() = preferencesDS.getLanguage()

    fun getNumeralsLanguage() = preferencesDS.getNumeralsLanguage()

    private fun getBooks() = db.booksDao().getAll()

    fun getBookContents(): List<Book> {
        val dir = File(ctx.getExternalFilesDir(null).toString() + prefix)
        if (!dir.exists()) return emptyList()

        val books = getBooks()

        val bookContents = ArrayList<Book>()
        for (i in books.indices) {
            val jsonStr = FileUtils.getJsonFromDownloads(
                ctx.getExternalFilesDir(null).toString() + prefix + i + ".json"
            )

            try {
                val bookContent = gson.fromJson(jsonStr, Book::class.java)
                if (bookContent != null) bookContents.add(bookContent)
            } catch (e: Exception) {
                Log.e(Global.TAG, "Error in json read in BookSearcher")
                e.printStackTrace()
                continue
            }
        }

        return bookContents
    }

    fun getBookSelections(): Array<Boolean> {
        val books = getBooks()
        val selections = Array(books.size) { true }

        val json = preferencesDS.getString(Preference.SelectedSearchBooks)
        if (json.isNotEmpty()) {
            val boolArr =  gson.fromJson(json, BooleanArray::class.java)
            boolArr.forEachIndexed { index, bool ->
                selections[index] = bool
            }
        }

        return selections
    }

    fun getMaxMatchesIndex() =
        preferencesDS.getInt(Preference.BookSearcherMaxMatchesIndex)

    fun getMaxMatchesItems(): Array<String> =
        ctx.resources.getStringArray(R.array.searcher_matches_en)

    fun setMaxMatchesIndex(index: Int) {
        preferencesDS.setInt(Preference.BookSearcherMaxMatchesIndex, index)
    }

    fun getBookTitles(language: Language): List<String> =
        if (language == Language.ENGLISH) db.booksDao().getTitlesEn()
        else db.booksDao().getTitles()

    fun isDownloaded(id: Int): Boolean {
        val dir = File(ctx.getExternalFilesDir(null).toString() + prefix)
        if (!dir.exists()) return false

        val files = dir.listFiles()
        for (element in files!!) {
            val name = element.name
            val n = name.substring(0, name.length - 5)
            try {
                val num = n.toInt()
                if (num == id) return true
            } catch (ignored: NumberFormatException) {}
        }
        return false
    }

}