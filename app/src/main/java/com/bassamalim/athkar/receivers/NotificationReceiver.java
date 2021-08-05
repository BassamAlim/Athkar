package com.bassamalim.athkar.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.bassamalim.athkar.services.NotificationService;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("myself", "hi");
        long time = intent.getLongExtra("time", 0);
        int prayer = intent.getIntExtra("prayer", 0);

        Intent intent1 = new Intent(context, NotificationService.class);

        float min = time - 1000 * 60 * 5;
        float max = time + 1000 * 60 * 5;

        if (System.currentTimeMillis() >= min && System.currentTimeMillis() <= max) {
            intent1.putExtra("prayer", prayer);
            context.startService(intent1);
        }
        else
            Toast.makeText(context, "Not the time for " + prayer, Toast.LENGTH_SHORT).show();

        /*if (Build.VERSION.SDK_INT >= 26)
            context.startForegroundService(intent1);
        else
            context.startService(intent1);*/
    }

}
