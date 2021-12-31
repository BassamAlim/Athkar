package bassamalim.hidaya.other;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

import bassamalim.hidaya.interfaces.AthkarCategoryDao;
import bassamalim.hidaya.interfaces.AthkarDao;
import bassamalim.hidaya.interfaces.AyahDao;
import bassamalim.hidaya.interfaces.AyatRecitersDao;
import bassamalim.hidaya.interfaces.AyatTelawaDao;
import bassamalim.hidaya.interfaces.SuraDao;
import bassamalim.hidaya.interfaces.ThikrsDao;
import bassamalim.hidaya.models.AthkarCategoryDB;
import bassamalim.hidaya.models.AthkarDB;
import bassamalim.hidaya.models.AyatRecitersDB;
import bassamalim.hidaya.models.AyatTelawaDB;
import bassamalim.hidaya.models.JAyah;
import bassamalim.hidaya.models.SuraDB;
import bassamalim.hidaya.models.ThikrsDB;

@Database(
        entities = {JAyah.class, SuraDB.class, AthkarCategoryDB.class, AthkarDB.class,
        ThikrsDB.class, AyatRecitersDB.class, AyatTelawaDB.class},
        version = 1
        /*, autoMigrations = {@AutoMigration(from = 1, to = 2), @AutoMigration(from = 2, to = 3)}*/)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AyahDao ayahDao();

    public abstract SuraDao suraDao();

    public abstract AthkarCategoryDao athkarCategoryDao();

    public abstract AthkarDao athkarDao();

    public abstract ThikrsDao thikrsDao();

    public abstract AyatRecitersDao ayatRecitersDao();

    public abstract AyatTelawaDao ayatTelawaDao();
}
