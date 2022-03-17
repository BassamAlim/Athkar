package bassamalim.hidaya.receivers;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

import bassamalim.hidaya.helpers.Alarms;
import bassamalim.hidaya.helpers.Keeper;
import bassamalim.hidaya.other.Const;
import bassamalim.hidaya.other.PrayersWidget;
import bassamalim.hidaya.other.Utils;

public class DailyUpdateReceiver extends BroadcastReceiver {

    private Context context;
    private SharedPreferences pref;
    private int time;
    private Calendar now;

    @Override
    public void onReceive(Context gContext, Intent intent) {
        Log.i(Const.TAG, "in daily update receiver");
        context = gContext;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        now = Calendar.getInstance();

        if (intent.getAction().equals("daily")) {
            time = intent.getIntExtra("time", 0);

            if (needed())
                locate();
            else
                Log.i(Const.TAG, "dead intent walking in daily update receiver");
        }
        else if (intent.getAction().equals("boot"))
            locate();
    }

    private boolean needed() {
        int day = pref.getInt("last_day", 0);

        int today = now.get(Calendar.DATE);
        int hour = now.get(Calendar.HOUR_OF_DAY);

        return day != today && time <= hour;
    }

    private void locate() {
        FusedLocationProviderClient fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(this::update);
        }
    }

    private void update(Location location) {
        if (location == null) {
            location = new Keeper(context).retrieveLocation();
            if (location == null) {
                Log.e(Const.TAG, "No available location in DailyUpdate");
                return;
            }
        }

        new Keeper(context, location);

        Calendar[] times = Utils.getTimes(location);

        new Alarms(context, times);

        updateWidget();

        updated();
    }

    private void updateWidget() {
        Intent intent = new Intent(context, PrayersWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetIds(
                new ComponentName(context.getApplicationContext(), PrayersWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void updated() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("last_day", now.get(Calendar.DATE));
        editor.apply();
    }
}
