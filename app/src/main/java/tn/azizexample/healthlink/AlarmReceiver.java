package tn.azizexample.healthlink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get medication details from the intent
        String medicationName = intent.getStringExtra("medicationName");
        String reminderTime = intent.getStringExtra("reminderTime");

        // Debug message to confirm the receiver was triggered
        Toast.makeText(context, "Alarm triggered for: " + medicationName, Toast.LENGTH_LONG).show();

        // Start AlarmActivity with medication details
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra("medicationName", medicationName);
        alarmIntent.putExtra("reminderTime", reminderTime);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(alarmIntent);
    }
}
