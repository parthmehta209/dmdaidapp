package com.example.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.util.AppConstants;
import com.example.util.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Environment;
import android.util.Log;

public class MedicationDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] medicationTableColumns = { MySQLiteHelper.MEDICATION_ID,
			MySQLiteHelper.MEDICATION_NAME, 
			MySQLiteHelper.MEDICATION_DOSE,
			MySQLiteHelper.MEDICATION_UNITS, 
			MySQLiteHelper.MEDICATION_TIMES,
			MySQLiteHelper.MEDICATION_TIMESPER,
			MySQLiteHelper.MEDICATION_STARTMONTH,
			MySQLiteHelper.MEDICATION_ENDMONTH,
			MySQLiteHelper.MEDICATION_TYPE,
			MySQLiteHelper.MEDICATION_DIRTY};


	public MedicationDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
		dbHelper.createDataBase();
	}
	
	public void deleteDatabase() {
		dbHelper.deleteDatabase();
	}

	public void open() throws SQLException {
		//database = dbHelper.getWritableDatabase();
		database = dbHelper.openDataBase();
	}

	public void close() {
		dbHelper.close();
	}

	public long createNewMedication(Medication medication) {

		ContentValues values = new ContentValues();
		if(medication._id != null)
			values.put(MySQLiteHelper.MEDICATION_ID, medication._id);
		values.put(MySQLiteHelper.MEDICATION_NAME, medication.medicationName);
		values.put(MySQLiteHelper.MEDICATION_DOSE, medication.dose);
		values.put(MySQLiteHelper.MEDICATION_UNITS, medication.units);
		values.put(MySQLiteHelper.MEDICATION_TIMES, medication.times);
		values.put(MySQLiteHelper.MEDICATION_TIMESPER, medication.timesPer);
		values.put(MySQLiteHelper.MEDICATION_STARTMONTH, medication.startMonth);
		values.put(MySQLiteHelper.MEDICATION_ENDMONTH, medication.endMonth);
		values.put(MySQLiteHelper.MEDICATION_TYPE, medication.type);
		values.put(MySQLiteHelper.MEDICATION_DIRTY, medication.dirty);
		long insertId = database.insert(MySQLiteHelper.MEDICATION_TABLE, null,
				values);
		if(insertId == -1)
			Log.d(AppConstants.TAG,"Could not create new medication");
		else
			Log.d(AppConstants.TAG,"Inserted medication in database:" + medication.medicationName +":"+ medication._id);
		return insertId;
	}

	public Cursor getMedicationsForType(String type) {
		Cursor cursor = database.query(MySQLiteHelper.MEDICATION_TABLE,
				medicationTableColumns, MySQLiteHelper.MEDICATION_TYPE + "=?", new String[]{type}, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}
	
	public void deleteMedication(long medicationId) {
		
		database.delete(MySQLiteHelper.MEDICATION_TABLE, MySQLiteHelper.MEDICATION_ID
				+ " = " + medicationId, null);
		Log.d(AppConstants.TAG,"Deleting medicationId:" +medicationId);
		
	}
	
	public Medication getMedication(long id) {
		Cursor cursor = database.query(MySQLiteHelper.MEDICATION_TABLE,
				medicationTableColumns, MySQLiteHelper.MEDICATION_ID + "=?", new String[]{""+id}, null, null, null);
		cursor.moveToFirst();
		
		return cursortoMedication(cursor);
	}
	
	public void cleanMedication(int id) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.MEDICATION_DIRTY, 0);
		database.update(MySQLiteHelper.MEDICATION_TABLE, values, MySQLiteHelper.MEDICATION_ID + "=?", new String[]{""+id});
		Log.d(Utils.TAG, "Updating medication: " + id + " to clean");
	}
	
	public List<Medication> getDirtyMedications() {
		List<Medication> medications = new ArrayList<Medication>();
		Cursor cursor = database.query(MySQLiteHelper.MEDICATION_TABLE,
				medicationTableColumns, MySQLiteHelper.MEDICATION_DIRTY + "=?", new String[]{""+1}, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Medication medication = cursortoMedication(cursor);
			medications.add(medication);
			cursor.moveToNext();
			Log.d(Utils.TAG, "Dirty Medication id :" + medication._id);
		}
		// make sure to close the cursor
		cursor.close();
		return medications;	

	}
	
	private Medication cursortoMedication(Cursor cursor) {
		Medication medication = new Medication();
		medication._id = cursor.getInt(0);
		medication.medicationName = cursor.getString(1);
		medication.dose = cursor.getInt(2);
		medication.units = cursor.getString(3);
		medication.times = cursor.getInt(4);
		medication.timesPer = cursor.getString(5);
		medication.startMonth = cursor.getString(6);
		medication.endMonth = cursor.getString(7);
		medication.type = cursor.getString(8);
		medication.dirty = cursor.getInt(9);
		return medication;
	}
} 