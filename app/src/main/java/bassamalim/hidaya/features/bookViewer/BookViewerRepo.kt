package bassamalim.hidaya.features.bookViewer

import android.content.Context
import bassamalim.hidaya.core.data.preferences.Preference
import bassamalim.hidaya.core.data.preferences.PreferencesDataSource
import bassamalim.hidaya.core.models.Book
import bassamalim.hidaya.core.utils.FileUtils
import com.google.gson.Gson
import javax.inject.Inject

class BookViewerRepo @Inject constructor(
    private val ctx: Context,
    private val preferencesDS: PreferencesDataSource,
    private val gson: Gson
) {

    fun getTextSize() = preferencesDS.getFloat(Preference.BooksTextSize)

    fun updateTextSize(textSize: Float) {
        preferencesDS.setFloat(Preference.BooksTextSize, textSize)
    }

    fun getDoors(bookId: Int, chapterId: Int): List<Book.BookChapter.BookDoor> {
        val path = ctx.getExternalFilesDir(null).toString() + "/Books/" + bookId + ".json"
        val jsonStr = FileUtils.getJsonFromDownloads(path)
        val book = gson.fromJson(jsonStr, Book::class.java)
        return book.chapters[chapterId].doors.toList()
    }

}