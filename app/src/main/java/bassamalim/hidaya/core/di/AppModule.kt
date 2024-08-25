package bassamalim.hidaya.core.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.room.Room
import bassamalim.hidaya.core.data.database.AppDatabase
import bassamalim.hidaya.core.data.preferences.migrations.AppSettingsPreferencesMigration
import bassamalim.hidaya.core.data.preferences.migrations.AppStatePreferencesMigration
import bassamalim.hidaya.core.data.preferences.migrations.BooksPreferencesMigration
import bassamalim.hidaya.core.data.preferences.migrations.NotificationsPreferencesMigration
import bassamalim.hidaya.core.data.preferences.migrations.PrayersPreferencesMigration
import bassamalim.hidaya.core.data.preferences.migrations.QuranPreferencesMigration
import bassamalim.hidaya.core.data.preferences.migrations.RecitationsPreferencesMigration
import bassamalim.hidaya.core.data.preferences.migrations.RemembrancesPreferencesMigration
import bassamalim.hidaya.core.data.preferences.migrations.UserPreferencesMigration
import bassamalim.hidaya.core.data.preferences.objects.AppSettingsPreferences
import bassamalim.hidaya.core.data.preferences.objects.AppStatePreferences
import bassamalim.hidaya.core.data.preferences.objects.BooksPreferences
import bassamalim.hidaya.core.data.preferences.objects.NotificationsPreferences
import bassamalim.hidaya.core.data.preferences.objects.PrayersPreferences
import bassamalim.hidaya.core.data.preferences.objects.QuranPreferences
import bassamalim.hidaya.core.data.preferences.objects.RecitationsPreferences
import bassamalim.hidaya.core.data.preferences.objects.RemembrancesPreferences
import bassamalim.hidaya.core.data.preferences.objects.UserPreferences
import bassamalim.hidaya.core.data.preferences.dataSources.AppSettingsPreferencesDataSource
import bassamalim.hidaya.core.data.preferences.dataSources.AppStatePreferencesDataSource
import bassamalim.hidaya.core.data.preferences.dataSources.BooksPreferencesDataSource
import bassamalim.hidaya.core.data.preferences.dataSources.NotificationsPreferencesDataSource
import bassamalim.hidaya.core.data.preferences.dataSources.PrayersPreferencesDataSource
import bassamalim.hidaya.core.data.preferences.dataSources.QuranPreferencesDataSource
import bassamalim.hidaya.core.data.preferences.dataSources.RecitationsPreferencesDataSource
import bassamalim.hidaya.core.data.preferences.dataSources.RemembrancePreferencesDataSource
import bassamalim.hidaya.core.data.preferences.dataSources.UserPreferencesDataSource
import bassamalim.hidaya.core.data.preferences.serializers.AppSettingsPreferencesSerializer
import bassamalim.hidaya.core.data.preferences.serializers.AppStatePreferencesSerializer
import bassamalim.hidaya.core.data.preferences.serializers.BooksPreferencesSerializer
import bassamalim.hidaya.core.data.preferences.serializers.NotificationsPreferencesSerializer
import bassamalim.hidaya.core.data.preferences.serializers.PrayersPreferencesSerializer
import bassamalim.hidaya.core.data.preferences.serializers.QuranPreferencesSerializer
import bassamalim.hidaya.core.data.preferences.serializers.RecitationsPreferencesSerializer
import bassamalim.hidaya.core.data.preferences.serializers.RemembrancesPreferencesSerializer
import bassamalim.hidaya.core.data.preferences.serializers.UserPreferencesSerializer
import bassamalim.hidaya.core.nav.Navigator
import bassamalim.hidaya.features.quranSearcher.QuranSearcherRepository
import bassamalim.hidaya.features.quran.quranSettings.QuranSettingsRepository
import bassamalim.hidaya.features.radio.RadioClientRepository
import bassamalim.hidaya.features.settings.SettingsRepository
import bassamalim.hidaya.features.recitationRecitersMenu.RecitationsRecitersMenuRepository
import bassamalim.hidaya.features.recitationsPlayer.RecitationsPlayerClientRepository
import bassamalim.hidaya.features.recitationSurasMenu.RecitationsSurasRepository
import bassamalim.hidaya.features.tv.TvRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)  // Sets how long does the dependencies live
object AppModule {

    @Provides @Singleton  // Sets how many instances of this dependency can be created
    fun provideApplicationContext(application: Application) =
        application.applicationContext!!

    @Provides @Singleton
    fun provideResources(application: Application): Resources =
        application.resources

    @Provides @Singleton
    fun provideDatabase(application: Application) =
        Room.databaseBuilder(
            application, AppDatabase::class.java, "HidayaDB"
        ).createFromAsset("databases/HidayaDB.db")
            .allowMainThreadQueries()
            .build()

    @Provides @Singleton
    fun provideAppSettingsPreferencesRepository(@ApplicationContext appContext: Context) =
        AppSettingsPreferencesDataSource(
            DataStoreFactory.create(
                serializer = AppSettingsPreferencesSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { AppSettingsPreferences() }
                ),
                migrations = listOf(AppSettingsPreferencesMigration.getMigration(appContext)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("app_settings_preferences") }
            )
        )

    @Provides @Singleton
    fun provideAppStatePreferencesRepository(@ApplicationContext appContext: Context) =
        AppStatePreferencesDataSource(
            DataStoreFactory.create(
                serializer = AppStatePreferencesSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { AppStatePreferences() }
                ),
                migrations = listOf(AppStatePreferencesMigration.getMigration(appContext)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("app_state_preferences") }
            )
        )

    @Provides @Singleton
    fun provideBooksPreferencesRepository(@ApplicationContext appContext: Context) =
        BooksPreferencesDataSource(
            DataStoreFactory.create(
                serializer = BooksPreferencesSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { BooksPreferences() }
                ),
                migrations = listOf(BooksPreferencesMigration.getMigration(appContext)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("books_preferences") }
            )
        )

    @Provides @Singleton
    fun provideNotificationsPreferencesRepository(@ApplicationContext appContext: Context) =
        NotificationsPreferencesDataSource(
            DataStoreFactory.create(
                serializer = NotificationsPreferencesSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { NotificationsPreferences() }
                ),
                migrations = listOf(NotificationsPreferencesMigration.getMigration(appContext)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("notifications_preferences") }
            )
        )

    @Provides @Singleton
    fun providePrayersPreferencesRepository(@ApplicationContext appContext: Context) =
        PrayersPreferencesDataSource(
            DataStoreFactory.create(
                serializer = PrayersPreferencesSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { PrayersPreferences() }
                ),
                migrations = listOf(PrayersPreferencesMigration.getMigration(appContext)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("prayers_preferences") }
            )
        )

    @Provides @Singleton
    fun provideQuranPreferencesRepository(@ApplicationContext appContext: Context) =
        QuranPreferencesDataSource(
            DataStoreFactory.create(
                serializer = QuranPreferencesSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { QuranPreferences() }
                ),
                migrations = listOf(QuranPreferencesMigration.getMigration(appContext)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("quran_preferences") }
            )
        )

    @Provides @Singleton
    fun provideRecitationsPreferencesRepository(@ApplicationContext appContext: Context) =
        RecitationsPreferencesDataSource(
            DataStoreFactory.create(
                serializer = RecitationsPreferencesSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { RecitationsPreferences() }
                ),
                migrations = listOf(RecitationsPreferencesMigration.getMigration(appContext)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("recitations_preferences") }
            )
        )

    @Provides @Singleton
    fun provideRemembrancesPreferencesRepository(@ApplicationContext appContext: Context) =
        RemembrancePreferencesDataSource(
            DataStoreFactory.create(
                serializer = RemembrancesPreferencesSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { RemembrancesPreferences() }
                ),
                migrations = listOf(RemembrancesPreferencesMigration.getMigration(appContext)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("remembrances_preferences") }
            )
        )

    @Provides @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext appContext: Context) =
        UserPreferencesDataSource(
            DataStoreFactory.create(
                serializer = UserPreferencesSerializer,
                corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { UserPreferences() }
                ),
                migrations = listOf(UserPreferencesMigration.getMigration(appContext)),
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                produceFile = { appContext.dataStoreFile("user_preferences") }
            )
        )

    @Provides @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides @Singleton
    fun provideRemoteConfig() = FirebaseRemoteConfig.getInstance()

    @Provides @Singleton
    fun provideGson() = Gson()

    @Provides @Singleton
    fun provideNavigator() = Navigator()


    @Provides @Singleton
    fun provideAboutRepository(
        appStatePreferencesDataSource: AppStatePreferencesDataSource
    ) = AboutRepository(appStatePreferencesDataSource)

    @Provides @Singleton
    fun provideBookChaptersRepository(
        application: Application,
        booksPreferencesDataSource: BooksPreferencesDataSource,
        gson: Gson
    ) = BookChaptersRepository(application, booksPreferencesDataSource, gson)

    @Provides @Singleton
    fun provideBookReaderRepository(
        application: Application,
        gson: Gson,
        booksPreferencesDataSource: BooksPreferencesDataSource
    ) = BookReaderRepository(application, booksPreferencesDataSource, gson)

    @Provides @Singleton
    fun provideBooksRepository(
        application: Application,
        database: AppDatabase,
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource,
        booksPreferencesDataSource: BooksPreferencesDataSource,
        gson: Gson
    ) = BooksRepository(
        application,
        database,
        appSettingsPreferencesDataSource,
        booksPreferencesDataSource,
        gson
    )

    @Provides @Singleton
    fun provideBookSearcherRepository(
        application: Application,
        resources: Resources,
        database: AppDatabase,
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource,
        booksPreferencesDataSource: BooksPreferencesDataSource,
        gson: Gson
    ) = BookSearcherRepository(
        application,
        resources,
        database,
        appSettingsPreferencesDataSource,
        booksPreferencesDataSource,
        gson
    )

    @Provides @Singleton
    fun provideDateConverterRepository(
        resources: Resources,
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource
    ) = DateConverterRepository(resources, appSettingsPreferencesDataSource)

    @Provides @Singleton
    fun provideDateEditorRepository(
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource
    ) = DateEditorRepository(appSettingsPreferencesDataSource)

    @Provides @Singleton
    fun provideHijriDatePickerRepository(
        resources: Resources,
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource
    ) = HijriDatePickerRepository(resources, appSettingsPreferencesDataSource)

    @Provides @Singleton
    fun provideHomeRepository(
        resources: Resources,
        database: AppDatabase,
        firestore: FirebaseFirestore,
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource,
        prayersPreferencesDataSource: PrayersPreferencesDataSource,
        quranPreferencesDataSource: QuranPreferencesDataSource,
        userPreferencesDataSource: UserPreferencesDataSource
    ) = HomeRepository(
        resources,
        database,
        firestore,
        appSettingsPreferencesDataSource,
        prayersPreferencesDataSource,
        quranPreferencesDataSource,
        userPreferencesDataSource
    )

    @Provides @Singleton
    fun provideLeaderboardRepository(
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource,
        firestore: FirebaseFirestore
    ) = LeaderboardRepository(appSettingsPreferencesDataSource, firestore)

    @Provides @Singleton
    fun provideLocationPickerRepository(
        database: AppDatabase,
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource,
        userPreferencesDataSource: UserPreferencesDataSource
    ) = LocationPickerRepository(
        database,
        appSettingsPreferencesDataSource,
        userPreferencesDataSource
    )

    @Provides @Singleton
    fun provideLocatorRepository(
        database: AppDatabase,
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource,
        userPreferencesDataSource: UserPreferencesDataSource
    ) = LocatorRepository(
        database,
        appSettingsPreferencesDataSource,
        userPreferencesDataSource
    )

    @Provides @Singleton
    fun provideMainRepository(
        resources: Resources,
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource
    ) = MainRepository(resources, appSettingsPreferencesDataSource)

    @Provides @Singleton
    fun provideOnboardingRepository(
        appStatePreferencesDataSource: AppStatePreferencesDataSource
    ) = OnboardingRepository(appStatePreferencesDataSource)

    @Provides @Singleton
    fun providePrayerReminderRepository(
        resources: Resources,
        appSettingsPreferencesDataSource: AppSettingsPreferencesDataSource,
        prayersPreferencesDataSource: PrayersPreferencesDataSource
    ) = PrayerReminderRepository(
        resources,
        appSettingsPreferencesDataSource,
        prayersPreferencesDataSource
    )

    @Provides @Singleton
    fun providePrayersRepository(
        resources: Resources,
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = PrayersRepository(resources, preferencesDataSource, database)

    @Provides @Singleton
    fun providePrayerSettingsRepository(
        resources: Resources,
        preferencesDataSource: PreferencesDataSource
    ) = PrayerSettingsRepository(resources, preferencesDataSource)

    @Provides @Singleton
    fun provideQiblaRepository(
        preferencesDataSource: PreferencesDataSource
    ) = QiblaRepository(preferencesDataSource)

    @Provides @Singleton
    fun provideQuizRepository(
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = QuizRepository(preferencesDataSource, database)

    @Provides @Singleton
    fun provideQuizResultRepository(
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = QuizResultRepository(preferencesDataSource, database)

    @Provides @Singleton
    fun provideQuranRepository(
        resources: Resources,
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = QuranRepository(resources, preferencesDataSource, database)

    @Provides @Singleton
    fun provideQuranReaderRepository(
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = QuranReaderRepository(preferencesDataSource, database)

    @Provides @Singleton
    fun provideQuranSearcherRepository(
        resources: Resources,
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = QuranSearcherRepository(resources, preferencesDataSource, database)

    @Provides @Singleton
    fun provideQuranSettingsRepository(
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = QuranSettingsRepository(preferencesDataSource, database)

    @Provides @Singleton
    fun provideRadioClientRepository(
        remoteConfig: FirebaseRemoteConfig
    ) = RadioClientRepository(remoteConfig)

    @Provides @Singleton
    fun provideRecitationsPlayerClientRepository(
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = RecitationsPlayerClientRepository(preferencesDataSource, database)

    @Provides @Singleton
    fun provideRecitationsRecitersMenuRepository(
        resources: Resources,
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = RecitationsRecitersMenuRepository(resources, preferencesDataSource, database)

    @Provides @Singleton
    fun provideRecitationsSurasRepository(
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = RecitationsSurasRepository(preferencesDataSource, database)

    @Provides @Singleton
    fun provideSettingsRepository(
        resources: Resources,
        preferencesDataSource: PreferencesDataSource
    ) = SettingsRepository(resources, preferencesDataSource)

    @Provides @Singleton
    fun provideRemembrancesMenuRepository(
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase,
    ) = RemembrancesMenuRepository(preferencesDataSource, database)

    @Provides @Singleton
    fun provideRemembranceReaderRepository(
        preferencesDataSource: PreferencesDataSource,
        database: AppDatabase
    ) = RemembranceReaderRepository(preferencesDataSource, database)

    @Provides @Singleton
    fun provideTvRepository(
        remoteConfig: FirebaseRemoteConfig
    ) = TvRepository(remoteConfig)

}