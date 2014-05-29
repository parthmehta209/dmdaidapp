package com.example.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.example.util.AppConstants;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "awareclient.db";
	private static final int DATABASE_VERSION = 4;
	private static String DATABASE_PATH = "/data/data/com.example.dmdaid/databases/";


	private SQLiteDatabase database; 
	private Context myContext;

	public static final String MEDICATION_TABLE = "medication";
	public static final String MEDICATION_ID = "_id";
	public static final String MEDICATION_NAME = "name";
	public static final String MEDICATION_DOSE = "dose";
	public static final String MEDICATION_UNITS = "units";
	public static final String MEDICATION_TIMES = "times";
	public static final String MEDICATION_TIMESPER = "timesper";
	public static final String MEDICATION_STARTMONTH = "startmonth";
	public static final String MEDICATION_ENDMONTH = "endmonth";
	public static final String MEDICATION_TYPE = "type";
	public static final String MEDICATION_DIRTY = "dirty";
	

	
	public static final String REPORTS_TABLE = "reports";
	public static final String REPORTS_ID = "_id";
	public static final String REPORTS_CATEGORY = "category";

	public static final String REPORTS_NAME = "report_name";
	public static final String REPORTS_DATE = "report_date";
	public static final String REPORTS_TYPE = "type";
	public static final String REPORTS_PDFPATH = "pdfpath";
	public static final String REPORTS_DIRTY = "dirty";

	// Database creation sql statement
	private static final String REPORTS_TABLE_CREATE = "create table "
			+ REPORTS_TABLE + "(" 
			+REPORTS_ID + " integer primary key autoincrement not null , "
			+ REPORTS_CATEGORY + " text not null, "
			+REPORTS_NAME + " text not null, " 
			+REPORTS_DATE+ " text not null, "
			+REPORTS_TYPE+ " text not null, "
			+REPORTS_PDFPATH+" text, "
			+REPORTS_DIRTY + " integer );";


	public static final String IMAGES_TABLE = "report_images";
	public static final String IMAGES_ID = "_id";
	public static final String IMAGES_NAME = "image_name";
	public static final String IMAGES_REPORT_ID = "report_id";
	
	// Database creation sql statement
	private static final String IMAGES_TABLE_CREATE = "create table "
			+ IMAGES_TABLE + "("  
			+ IMAGES_ID+" integer primary key autoincrement not null, " 
			+ IMAGES_NAME	+ " text not null, "
			+ IMAGES_REPORT_ID+ " integer not null );";


	public static final String MENUS_TABLE = "menus"; 
	public static final String MENUS_ID = "_id";
	public static final String MENUS_LIST_ITEM = "list_item";
	public static final String MENUS_LIST_TYPE = "list_type";
	public static final String MENUS_NEXT_TYPE = "next_type";
	public static final String MENUS_NEXT = "next";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		myContext = context;
	}

	public void deleteDatabase() {
		myContext.deleteDatabase(DATABASE_NAME);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		this.database = database;
		//this.database.execSQL(REPORTS_TABLE_CREATE);
		//this.database.execSQL(IMAGES_TABLE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + REPORTS_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + IMAGES_TABLE);
		onCreate(db);
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() {

		boolean dbExist = checkDataBase();

		if(dbExist){
			Log.d(AppConstants.TAG, "The data base already exists");

		}else{

			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {
				e.printStackTrace();
				Log.d(AppConstants.TAG,"Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){

		SQLiteDatabase checkDB = null;

		try{
			String myPath = DATABASE_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

		}catch(SQLiteException e){
			e.printStackTrace();
			
			Log.d(AppConstants.TAG,"Database does not exist yet");
		}

		if(checkDB != null){

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException{


		//Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

		// Path to the just created empty db
		String outFileName = DATABASE_PATH + DATABASE_NAME;

		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

		public SQLiteDatabase openDataBase() throws SQLException{
	
			//Open the database
			String myPath = DATABASE_PATH + DATABASE_NAME;
			database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
			return database;
		}


} 