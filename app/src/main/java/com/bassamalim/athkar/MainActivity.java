package com.bassamalim.athkar;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.bassamalim.athkar.models.DataSaver;
import com.bassamalim.athkar.receivers.DeviceBootReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.bassamalim.athkar.databinding.ActivityMainBinding;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;
    private ActivityMainBinding binding;
    public final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    public static Location location;
    public static ArrayList<String> times;
    public Calendar[] formattedTimes;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_prayers, R.id.navigation_alathkar, R.id.navigation_qibla).build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600*12).build();

        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        Intent intent = getIntent();
        location = intent.getParcelableExtra("location");

        try {
            double test = location.getLatitude();
            new Keeper(this, location);
            Log.i(Constants.TAG, "went");
        }
        catch (Exception e) {
            Keeper keeper = new Keeper(this);
            location = keeper.retrieveLocation();
            Log.i(Constants.TAG, "didnt");
        }

        times = getTimes();

        //formattedTimes = formatTimes(times);
        formattedTimes = test();

        new Keeper(this, formattedTimes);

        if (getPreferences(MODE_PRIVATE).getBoolean(getString(R.string.athan_enable_key), true)) {
            new Alarms(this, formattedTimes);
            new DailyUpdate();
            receiver();
        }

        new Update();
    }

    public Calendar[] test() {
        Calendar[] tester = new Calendar[7];

        tester[0] = Calendar.getInstance();
        tester[0].setTimeInMillis(System.currentTimeMillis());
        tester[0].set(Calendar.HOUR_OF_DAY, 4);
        tester[0].set(Calendar.MINUTE, 4);

        tester[1] = Calendar.getInstance();
        tester[1].setTimeInMillis(System.currentTimeMillis());
        tester[1].set(Calendar.HOUR_OF_DAY, 4);
        tester[1].set(Calendar.MINUTE, 48);

        tester[2] = Calendar.getInstance();
        tester[2].setTimeInMillis(System.currentTimeMillis());
        tester[2].set(Calendar.HOUR_OF_DAY, 10);
        tester[2].set(Calendar.MINUTE, 41);

        tester[3] = Calendar.getInstance();
        tester[3].setTimeInMillis(System.currentTimeMillis());
        tester[3].set(Calendar.HOUR_OF_DAY, 21);
        tester[3].set(Calendar.MINUTE, 41);

        tester[4] = Calendar.getInstance();
        tester[4].setTimeInMillis(System.currentTimeMillis());
        tester[4].set(Calendar.HOUR_OF_DAY, 16);
        tester[4].set(Calendar.MINUTE, 42);

        tester[5] = Calendar.getInstance();
        tester[5].setTimeInMillis(System.currentTimeMillis());
        tester[5].set(Calendar.HOUR_OF_DAY, 2);
        tester[5].set(Calendar.MINUTE, 43);

        tester[6] = Calendar.getInstance();
        tester[6].setTimeInMillis(System.currentTimeMillis());
        tester[6].set(Calendar.HOUR_OF_DAY, 11);
        tester[6].set(Calendar.MINUTE, 54);

        return tester;
    }

    public ArrayList<String> getTimes() {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);

        return new PrayTimes().getPrayerTimes(calendar, location.getLatitude(),
                location.getLongitude(), Constants.TIME_ZONE);
    }

    private Calendar[] formatTimes(ArrayList<String> givenTimes) {
        Calendar[] formattedTimes = new Calendar[givenTimes.size()];

        for (int i = 0; i < givenTimes.size(); i++) {
            char m = givenTimes.get(i).charAt(6);
            int hour = Integer.parseInt(givenTimes.get(i).substring(0, 2));
            if (m == 'P')
                hour += 12;

            formattedTimes[i] = Calendar.getInstance();
            formattedTimes[i].setTimeInMillis(System.currentTimeMillis());
            formattedTimes[i].set(Calendar.HOUR_OF_DAY, hour);
            formattedTimes[i].set(Calendar.MINUTE, Integer.parseInt(givenTimes.get(i).substring(3, 5)));
        }
        return formattedTimes;
    }

    public void receiver() {
        ComponentName receiver = new ComponentName(getApplicationContext(), DeviceBootReceiver.class);
        PackageManager pm = getApplicationContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(
                R.color.primary, getTheme())));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.whatsapp) {
            String myNumber = "+966553145230";
            String url = "https://api.whatsapp.com/send?phone=" + myNumber;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

}