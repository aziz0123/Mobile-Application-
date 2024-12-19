package tn.azizexample.healthlink;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends Activity {

    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        TextView medicationNameTextView = findViewById(R.id.medicationNameTextView);
        TextView reminderTimeTextView = findViewById(R.id.reminderTimeTextView);
        Button stopAlarmButton = findViewById(R.id.stopAlarmButton);

        // Get medication details from the intent
        String medicationName = getIntent().getStringExtra("medicationName");
        String reminderTime = getIntent().getStringExtra("reminderTime");

        // Set text for medication name and reminder time
        medicationNameTextView.setText("Médicament: " + medicationName);
        reminderTimeTextView.setText("Heure: " + reminderTime);

        // Play default alarm sound
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.play();

        // Stop the alarm sound and close the activity on button click
        stopAlarmButton.setOnClickListener(v -> stopAlarm());
    }

    private void stopAlarm() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }
}
