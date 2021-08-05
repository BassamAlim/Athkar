package com.bassamalim.athkar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.bassamalim.athkar.receivers.DailyUpdateReceiver;

import java.util.Calendar;

public class DailyUpdate {

    private final Context CONTEXT = MainActivity.getInstance();
    private int hourOfTheDay = 0;

    public DailyUpdate() {
        Intent myIntent = new Intent(CONTEXT.getApplicationContext(), DailyUpdateReceiver.class);

        PendingIntent pendIntent = PendingIntent.getBroadcast(CONTEXT.getApplicationContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager myAlarm = (AlarmManager) CONTEXT.getSystemService(Context.ALARM_SERVICE);

        myAlarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time().getTimeInMillis(), pendIntent);
    }

    public Calendar time() {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        time.set(Calendar.HOUR_OF_DAY, hourOfTheDay);

        return time;
    }

}
