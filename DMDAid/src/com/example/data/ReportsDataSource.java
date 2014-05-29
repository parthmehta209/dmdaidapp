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

public class ReportsDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] reportTableColumns = { MySQLiteHelper.REPORTS_ID,
			MySQLiteHelper.REPORTS_CATEGORY, 
			MySQLiteHelper.REPORTS_NAME,
			MySQLiteHelper.REPORTS_DATE, 
			MySQLiteHelper.REPORTS_TYPE,
			MySQLiteHelper.REPORTS_PDFPATH,
			MySQLiteHelper.REPORTS_DIRTY};

	private String[] imageTableColumns = { MySQLiteHelper.IMAGES_ID, 
			MySQLiteHelper.IMAGES_NAME, 
			MySQLiteHelper.IMAGES_REPORT_ID };

	public ReportsDataSource(Context context) {
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

	public long createNewReport(Integer id,String reportName, String category, String date,
			String reportType,String pdfPath,int dirty) {

		ContentValues values = new ContentValues();
		if(id!= null)
			values.put(MySQLiteHelper.REPORTS_ID, id);
		values.put(MySQLiteHelper.REPORTS_NAME, reportName);
		values.put(MySQLiteHelper.REPORTS_CATEGORY, category);
		values.put(MySQLiteHelper.REPORTS_DATE, date);
		values.put(MySQLiteHelper.REPORTS_TYPE, reportType);
		if(pdfPath != null)
			values.put(MySQLiteHelper.REPORTS_PDFPATH,pdfPath);
		values.put(MySQLiteHelper.REPORTS_DIRTY, dirty);

		long insertId = database.insert(MySQLiteHelper.REPORTS_TABLE, null,
				values);
		if(insertId == -1)
			Log.d(AppConstants.TAG,"Could not create new report");
		else
			Log.d(AppConstants.TAG,"Inserted report in database:" + reportName +":"+ category + ":" + date);
		return insertId;
	}

	
	public long addImageToReport(String filename, long reportId) {

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.IMAGES_NAME, filename);
		values.put(MySQLiteHelper.IMAGES_REPORT_ID, reportId);

		long insertId = database.insert(MySQLiteHelper.IMAGES_TABLE, null,
				values);
		if(insertId == -1)
			Log.d(AppConstants.TAG,"Could not insert image");
		else
			Log.d(AppConstants.TAG,"Inserted report image:" + filename + " report id:"+ reportId);
		return insertId;

	}


	public Cursor getReportsForCategory(String category) {

		Cursor cursor = database.query(MySQLiteHelper.REPORTS_TABLE,
				reportTableColumns, MySQLiteHelper.REPORTS_CATEGORY + "=?", new String[]{category}, null, null, null);

		cursor.moveToFirst();
		//			    while (!cursor.isAfterLast()) {
		//			    	Report report = new Report();
		//			      report.setReportId(cursor.getLong(0));
		//			      report.setCategory(cursor.getString(1));
		//			      report.setReportName(cursor.getString(2));
		//			      report.setReportDate(cursor.getString(3));
		//			      reports.add(report);
		//			      cursor.moveToNext();
		//			    }
		//			    // make sure to close the cursor
		//			    cursor.close();
		//			    
		//			    Log.d(AppConstants.TAG,"Files for category "+category+" are "+ reports.toString());

		return cursor;


	}


	public List<String> getImagesForReport(long reportId) {
		List<String> files = new ArrayList<String>();

		Cursor cursor = database.query(MySQLiteHelper.IMAGES_TABLE,
				imageTableColumns, MySQLiteHelper.IMAGES_REPORT_ID + "=?", new String[]{Long.toString(reportId)}, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			files.add(cursor.getString(1));
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		Log.d(AppConstants.TAG,"Files for report "+reportId+" are "+files.toString());

		return files;
	}



	public void removeImage(String filename) {
		database.delete(MySQLiteHelper.IMAGES_TABLE, MySQLiteHelper.IMAGES_NAME+"=?", new String[]{filename});
		File f = new File(android.os.Environment
				.getExternalStorageDirectory() + File.separator+ AppConstants.PHOTO_ALBUM, filename);
		f.delete();
	}

	public boolean FileExists(String filename) {
		boolean ret = false;
		Cursor cursor = database.query(MySQLiteHelper.IMAGES_TABLE,
				imageTableColumns, MySQLiteHelper.IMAGES_NAME + "=?", new String[]{filename}, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ret = true;
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		return ret;
	}

	public List<Report> getReportListForCategory(String category) {
		List<Report> reports = new ArrayList<Report>();
		Cursor cursor = database.query(MySQLiteHelper.REPORTS_TABLE,
				reportTableColumns, MySQLiteHelper.REPORTS_CATEGORY + "=?", new String[]{category}, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Report report = cursorToReport(cursor);
			reports.add(report);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		Log.d(AppConstants.TAG,"Files for category "+category+" are "+ reports.toString());

		return reports;	
	}
	
	public void cleanReport(long reportId) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.REPORTS_DIRTY, 0);
		database.update(MySQLiteHelper.REPORTS_TABLE, values, MySQLiteHelper.REPORTS_ID + "=?", new String[]{""+reportId});
		Log.d(Utils.TAG, "Updating report: " + reportId + " to clean");
	}
	
	public void deleteReport(long reportId) {
		
		Report report = getReport(reportId);
		if(report.getReportType().equals(Utils.IMG_REPORT)) {
			List<String> images = getImagesForReport(reportId);
			for(String image:images) {
				removeImage(image);
			}	
		} else if(report.getReportType().equals(Utils.PDF_REPORT)) {
			File pdfFile = new File(Environment.getExternalStorageDirectory()+File.separator + 
					AppConstants.PHOTO_ALBUM + File.separator + report.getPdfPath());
			pdfFile.delete();
		}
		
		
		database.delete(MySQLiteHelper.REPORTS_TABLE, MySQLiteHelper.REPORTS_ID
				+ " = " + reportId, null);
		Log.d(AppConstants.TAG,"Deleting reportId:" +reportId);
		
	}
	
	public Report getReport(long id) {
		Cursor cursor = database.query(MySQLiteHelper.REPORTS_TABLE,
				reportTableColumns, MySQLiteHelper.REPORTS_ID + "=?", new String[]{""+id}, null, null, null);
		cursor.moveToFirst();
		
		return cursorToReport(cursor);
	}
	
	
	public List<Report> getDirtyReports() {
		List<Report> reports = new ArrayList<Report>();
		Cursor cursor = database.query(MySQLiteHelper.REPORTS_TABLE,
				reportTableColumns, MySQLiteHelper.REPORTS_DIRTY + "=?", new String[]{""+1}, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Report report = cursorToReport(cursor);
			report.setImages(getImagesForReport(report.getReportId()));
			reports.add(report);
			cursor.moveToNext();
			Log.d(Utils.TAG, "Dirty report id :" + report.getReportId());
		}
		// make sure to close the cursor
		cursor.close();

		

		return reports;	

	}
	
	private Report cursorToReport(Cursor cursor) {
		Report report = new Report();
		report.setReportId(cursor.getLong(0));
		report.setCategory(cursor.getString(1));
		report.setReportName(cursor.getString(2));
		report.setReportDate(cursor.getString(3));
		report.setReportType(cursor.getString(4));
		report.setPdfPath(cursor.getString(5));
		report.setDirty(cursor.getInt(6));
		return report;
	}

	

	

} 