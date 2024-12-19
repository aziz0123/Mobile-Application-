package tn.azizexample.healthlink;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import tn.azizexample.healthlink.database.DatabaseHelper;
import tn.azizexample.healthlink.model.rappel;

public class ReminderFormFragment extends Fragment {

    private EditText medNameEditText, reminderTimeEditText;
    private Button saveReminderButton, viewReminderListButton;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_form, container, false);

        medNameEditText = view.findViewById(R.id.medNameEditText);
        reminderTimeEditText = view.findViewById(R.id.reminderTimeEditText);
        saveReminderButton = view.findViewById(R.id.saveReminderButton);
        viewReminderListButton = view.findViewById(R.id.viewReminderListButton);
        databaseHelper = new DatabaseHelper(getContext());

        // Configure TimePicker for reminderTimeEditText
        reminderTimeEditText.setOnClickListener(v -> showTimePicker());

        saveReminderButton.setOnClickListener(v -> {
            String nomMedicament = medNameEditText.getText().toString();
            String heureRappel = reminderTimeEditText.getText().toString();

            if (!nomMedicament.isEmpty() && !heureRappel.isEmpty()) {
                // Extract hour and minute from heureRappel
                String[] timeParts = heureRappel.split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);

                // Save reminder in the database
                rappel rappel = new rappel(0, nomMedicament, heureRappel);
                boolean isAdded = databaseHelper.ajouterRappel(rappel);

                if (isAdded) {
                    // Schedule the alarm with medication name and reminder time
                    setAlarm(hour, minute, nomMedicament, heureRappel);
                    Toast.makeText(getContext(), "Rappel enregistré avec succès", Toast.LENGTH_SHORT).show();
                    medNameEditText.setText("");
                    reminderTimeEditText.setText("");
                } else {
                    Toast.makeText(getContext(), "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        });

        viewReminderListButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ReminderListFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    reminderTimeEditText.setText(time);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(int hour, int minute, String medicationName, String reminderTime) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);

        // Pass medication details to AlarmReceiver
        intent.putExtra("medicationName", medicationName);
        intent.putExtra("reminderTime", reminderTime);

        // Generate a unique request code to avoid conflicts
        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set up the calendar to the specified time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the specified time is before the current time, set it for the next day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Debug message to confirm the correct time is being set
        Toast.makeText(requireContext(), "Alarm set for: " + calendar.getTime().toString(), Toast.LENGTH_LONG).show();

        if (alarmManager != null) {
            // Use setAlarmClock to ensure the alarm is treated as a high-priority alarm
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
