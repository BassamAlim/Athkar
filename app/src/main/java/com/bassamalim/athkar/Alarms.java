package com.bassamalim.athkar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.bassamalim.athkar.receivers.NotificationReceiver;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Alarms extends AppCompatActivity {

    private final Context context;
    private Context appContext;
    private Calendar[] times;
    private SharedPreferences pref;
    private final String action;

    public Alarms(Context gContext, Calendar[] gTimes) {
        context = gContext;
        times = gTimes;
        action = "all";

        setup();
    }

    public Alarms(Context gContext, String gAction) {
        context = gContext;
        action = gAction;

        setup();
    }

    private void setup() {
        appContext = context.getApplicationContext();
        pref = PreferenceManager.getDefaultSharedPreferences(context);

        switch (action) {
            case "all":
                setPrayerAlarms();
                setExtraAlarms();
                break;
            case "prayers":
                setPrayerAlarms();
                break;
            case "extra":
                setExtraAlarms();
                break;
        }
    }

    private void setPrayerAlarms() {
        if (!pref.getBoolean(context.getString(R.string.athan_enable_key), true))
            return;

        Log.i(Constants.TAG, "in set alarms");
        for (int i = 1; i <= times.length; i++) {
            if (i != 2 && i != 5 && System.currentTimeMillis() <= times[i-1].getTimeInMillis()) {
                Intent intent = new Intent(appContext, NotificationReceiver.class);
                intent.setAction("prayer");
                intent.putExtra("id", i);
                intent.putExtra("time", times[i-1].getTimeInMillis());
                PendingIntent pendingIntent;
                AlarmManager myAlarm = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pendingIntent = PendingIntent.getBroadcast(appContext, i,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    myAlarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                            times[i-1].getTimeInMillis(), pendingIntent);
                }
                else {
                    pendingIntent = PendingIntent.getBroadcast(appContext, i,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    myAlarm.setExact(AlarmManager.RTC_WAKEUP,
                            times[i-1].getTimeInMillis(), pendingIntent);
                }
                Log.i(Constants.TAG, "alarm " + i + " set");
            }
            else
                Log.i(Constants.TAG, i + " Passed");
        }
    }

    private void setExtraAlarms() {
        Log.i(Constants.TAG, "in set extra alarms");

        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());

        if (pref.getBoolean(context.getString(R.string.morning_athkar_key), true))
            setExtraAlarm(8);
        if (pref.getBoolean(context.getString(R.string.night_athkar_key), true))
            setExtraAlarm(9);
        if (pref.getBoolean(context.getString(R.string.daily_page_key), true))
            setExtraAlarm(10);
        if (pref.getBoolean(context.getString(R.string.friday_kahf_key), true)
                && today.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
            setExtraAlarm(11);
    }

    private void setExtraAlarm(int id) {
        String key = "";
        int defHour = 0;
        int defMinute = 0;
        switch (id) {
            case 8:
                key = context.getString(R.string.morning_athkar_key);
                defHour = 5;
                break;
            case 9:
                key = context.getString(R.string.night_athkar_key);
                defHour = 16;
                break;
            case 10:
                key = context.getString(R.string.daily_page_key);
                defHour = 21;
                break;
            case 11:
                key = context.getString(R.string.friday_kahf_key);
                Location loc = new Keeper(appContext).retrieveLocation();
                Calendar[] times = getTimes(loc);
                Calendar duhr = times[2];
                defHour = duhr.get(Calendar.HOUR_OF_DAY)+1;
                defMinute = duhr.get(Calendar.MINUTE);
                break;
        }
        int hour = pref.getInt(key + "hour", defHour);
        int minute = pref.getInt(key + "minute", defMinute);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        time.set(Calendar.SECOND, 0);

        if (System.currentTimeMillis() <= time.getTimeInMillis()) {
            Intent intent = new Intent(appContext, NotificationReceiver.class);
            intent.setAction("extra");
            intent.putExtra("id", id);
            intent.putExtra("time", time.getTimeInMillis());
            PendingIntent pendIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                pendIntent = PendingIntent.getBroadcast(appContext, id, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            }
            else {
                pendIntent = PendingIntent.getBroadcast(appContext, id, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }

            AlarmManager myAlarm = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
            myAlarm.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendIntent);

            Log.i(Constants.TAG, "alarm " + id + " set");
        }
        else
            Log.i(Constants.TAG, id + " Passed");
    }

    private Calendar[] getTimes(Location loc) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        TimeZone timeZoneObj = TimeZone.getDefault();
        long millis = timeZoneObj.getOffset(date.getTime());
        double timezone = millis / 3600000.0;

        return new PrayTimes().getPrayerTimesArray(calendar, loc.getLatitude(),
                loc.getLongitude(), timezone);
    }

}
