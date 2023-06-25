package bassamalim.hidaya.core.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import bassamalim.hidaya.core.data.Prefs
import bassamalim.hidaya.core.data.database.AppDatabase
import bassamalim.hidaya.core.other.Global
import com.google.gson.Gson
import java.util.*

object DBUtils {

    fun getDB(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "HidayaDB")
            .createFromAsset("databases/HidayaDB.db").allowMainThreadQueries().build()
    }

    fun testDB(
        ctx: Context,
        sp: SharedPreferences,
        db: AppDatabase
    ) {
        val lastVer = PrefUtils.getInt(sp, Prefs.LastDBVersion)
        if (Global.dbVer > lastVer) reviveDB(ctx, sp, db)

        try {  // if there is a problem in the db it will cause an error
            db.suarDao().getFavs()
        } catch (e: Exception) {
            reviveDB(ctx, sp, db)
        }
    }

    fun reviveDB(
        ctx: Context,
        sp: SharedPreferences = PrefUtils.getPreferences(ctx),
        db: AppDatabase = getDB(ctx)
    ) {
        ctx.deleteDatabase("HidayaDB")

        val surasJson = PrefUtils.getString(sp, Prefs.FavoriteSuras)
        val recitersJson = PrefUtils.getString(sp, Prefs.FavoriteReciters)
        val athkarJson = PrefUtils.getString(sp, Prefs.FavoriteAthkar)

        val gson = Gson()

        if (surasJson.isNotEmpty()) {
            val favSuras = gson.fromJson(surasJson, IntArray::class.java)
            for (i in favSuras.indices) db.suarDao().setFav(i, favSuras[i])
        }

        if (recitersJson.isNotEmpty()) {
            val favReciters: Array<Any> = gson.fromJson(recitersJson, Array<Any>::class.java)
            for (i in favReciters.indices) {
                val d = favReciters[i] as Double
                db.telawatRecitersDao().setFav(i, d.toInt())
            }
        }

        if (athkarJson.isNotEmpty()) {
            val favAthkar: Array<Any> = gson.fromJson(athkarJson, Array<Any>::class.java)
            for (i in favAthkar.indices) {
                val d = favAthkar[i] as Double
                db.athkarDao().setFav(i, d.toInt())
            }
        }

        sp.edit()
            .putInt(Prefs.LastDBVersion.key, Global.dbVer)
            .apply()

        Log.i(Global.TAG, "Database Revived")
    }

}