package tn.azizexample.healthlink;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import tn.azizexample.healthlink.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private EditText reminderTimeEditText; // Champ de texte pour l'heure du rappel
    private DatabaseHelper dbHelper; // Instance de DatabaseHelper pour gérer la base de données

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Utilisez un fichier XML propre à l'activité

        if (savedInstanceState == null) {
            // Charger ReminderFormFragment dans le conteneur de fragments
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ReminderFormFragment());
            transaction.commit();
        }
    }

    private void showTimePicker() {
        // Obtenir l'heure actuelle
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // Heure actuelle
        int minute = calendar.get(Calendar.MINUTE); // Minute actuelle

        // Création du sélecteur d'heure
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Formatage de l'heure sélectionnée et mise à jour du champ de texte
                        String time = String.format("%02d:%02d", hourOfDay, minute);
                        reminderTimeEditText.setText(time);
                    }
                },
                hour, // Heure par défaut
                minute, // Minute par défaut
                true // true pour le format 24 heures
        );

        // Afficher le sélecteur
        timePickerDialog.show();
    }
}
