package tn.azizexample.healthlink;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import tn.azizexample.healthlink.database.DatabaseHelper;
import tn.azizexample.healthlink.model.rappel;

public class ReminderUpdateFragment extends Fragment {
    private EditText editTextReminderTitle;
    private EditText editTextReminderTime;
    private Button buttonSaveChanges;
    private DatabaseHelper db;
    private int reminderId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_modify_reminder, container, false);

        // Initialize UI elements
        editTextReminderTitle = view.findViewById(R.id.editTextReminderTitle);
        editTextReminderTime = view.findViewById(R.id.editTextReminderTime);
        buttonSaveChanges = view.findViewById(R.id.button_save_reminder);

        // Initialize the database helper
        db = new DatabaseHelper(requireContext());

        // Retrieve data passed via arguments
        if (getArguments() != null) {
            reminderId = getArguments().getInt("reminderId", -1);
            String reminderTitle = getArguments().getString("reminderTitle", "");
            String reminderTime = getArguments().getString("reminderTime", "");

            // Pre-fill fields with the reminder's data
            editTextReminderTitle.setText(reminderTitle);
            editTextReminderTime.setText(reminderTime);
        }

        // Set up Time Picker on editTextReminderTime
        editTextReminderTime.setOnClickListener(v -> showTimePicker());

        // Set up Save Changes button click action
        buttonSaveChanges.setOnClickListener(v -> {
            Log.d("ReminderUpdateFragment", "Save button clicked");

            String newNomMedicament = editTextReminderTitle.getText().toString().trim();
            String newHeureRappel = editTextReminderTime.getText().toString().trim();

            if (newNomMedicament.isEmpty() || newHeureRappel.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Run database update in a background thread
            new Thread(() -> {
                try {
                    rappel reminder = db.getReminderById(reminderId);
                    if (reminder != null) {
                        reminder.setNomMedicament(newNomMedicament);
                        reminder.setHeureRappel(newHeureRappel);

                        // Update reminder in the database
                        db.mettreAJourRappel(reminder);

                        // Schedule updated alarm
                        scheduleUpdatedAlarm(reminder);

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Reminder updated successfully", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Error: Reminder not found", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    Log.e("ReminderUpdateFragment", "Error updating reminder", e);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        return view;
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    editTextReminderTime.setText(time);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    /**
     * Schedule updated alarm for the reminder
     */
    @SuppressLint("ScheduleExactAlarm")
    private void scheduleUpdatedAlarm(rappel reminder) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        // Cancel existing alarm with the same requestCode (reminder ID)
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        // Set up new time for the updated alarm
        String[] timeParts = reminder.getHeureRappel().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Pass updated details to AlarmReceiver
        intent.putExtra("medicationName", reminder.getNomMedicament());
        intent.putExtra("reminderTime", reminder.getHeureRappel());

        // Create new PendingIntent with updated details
        PendingIntent newPendingIntent = PendingIntent.getBroadcast(requireContext(), reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set up calendar for the specified time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the specified time is before the current time, set it for the next day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Schedule the alarm with the updated time
        if (alarmManager != null) {
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), newPendingIntent);
            alarmManager.setAlarmClock(alarmClockInfo, newPendingIntent);
            Log.d("ReminderUpdateFragment", "Updated alarm set for: " + calendar.getTime().toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
