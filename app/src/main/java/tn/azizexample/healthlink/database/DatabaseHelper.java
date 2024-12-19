package tn.azizexample.healthlink.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tn.azizexample.healthlink.model.rappel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rappels.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RAPPELS = "rappels";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOM = "nomMedicament";
    private static final String COLUMN_HEURE = "heureRappel";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_RAPPELS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NOM + " TEXT, "
                + COLUMN_HEURE + " TEXT)";
        db.execSQL(createTable);
        Log.d("DatabaseHelper", "Table " + TABLE_RAPPELS + " created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAPPELS);
        Log.d("DatabaseHelper", "Table " + TABLE_RAPPELS + " upgraded.");
        onCreate(db);
    }

    // Check if the database has been created
    public boolean isDatabaseCreated() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_RAPPELS + "'", null);
        boolean isCreated = cursor.getCount() > 0;
        cursor.close();
        return isCreated;
    }

    // CRUD (Create, Read, Update, Delete) Methods
    public boolean ajouterRappel(rappel rappel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM, rappel.getNomMedicament());
        values.put(COLUMN_HEURE, rappel.getHeureRappel());

        long result = db.insert(TABLE_RAPPELS, null, values);
        Log.d("DatabaseHelper", "Insert result: " + result);
        return result != -1;
    }

    public List<rappel> getTousLesRappels() {
        List<rappel> rappels = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RAPPELS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String nomMedicament = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
                String heureRappel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEURE));
                rappels.add(new rappel(id, nomMedicament, heureRappel));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("DatabaseHelper", "Reminders retrieved: " + rappels.size());
        return rappels;
    }

    public boolean mettreAJourRappel(rappel rappel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOM, rappel.getNomMedicament());
        values.put(COLUMN_HEURE, rappel.getHeureRappel());

        int result = db.update(TABLE_RAPPELS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(rappel.getId())});
        Log.d("DatabaseHelper", "Reminder updated: " + (result > 0));
        return result > 0;
    }

    public boolean supprimerRappel(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_RAPPELS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        Log.d("DatabaseHelper", "Reminder deleted: " + (result > 0));
        return result > 0;
    }

    public rappel getReminderById(int reminderId) {
        rappel reminder = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RAPPELS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(reminderId)});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String nomMedicament = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOM));
            String heureRappel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEURE));
            reminder = new rappel(id, nomMedicament, heureRappel);
        }
        cursor.close();
        return reminder;
    }
}
