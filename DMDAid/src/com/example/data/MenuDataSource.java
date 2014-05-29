package com.example.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.util.Log;

public class MenuDataSource {
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	private String[] menusTableColumns = { MySQLiteHelper.MENUS_ID, 
			MySQLiteHelper.MENUS_LIST_ITEM, 
			MySQLiteHelper.MENUS_LIST_TYPE,
			MySQLiteHelper.MENUS_NEXT_TYPE,
			MySQLiteHelper.MENUS_NEXT};

	public MenuDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	    dbHelper.createDataBase();
	}
	
	public void deleteDatabase() {
		dbHelper.deleteDatabase();
	}

	public void open() throws SQLException {
		database = dbHelper.openDataBase();
	}

	public void close() {
		dbHelper.close();
	}

	public Cursor getList(String listType) {

		//List<String> listItems = new ArrayList<String>();
		Cursor cursor = database.query(MySQLiteHelper.MENUS_TABLE,
				menusTableColumns, MySQLiteHelper.MENUS_LIST_TYPE + "=?", new String[]{listType}, null, null, null);

		cursor.moveToFirst();
//		
//		while (!cursor.isAfterLast()) {
//
//			listItems.add(cursor.getString(1));
//			cursor.moveToNext();
//		}
//		// make sure to close the cursor
//		cursor.close();
//
//		Log.d(AppConstants.TAG,"List for type "+listType+" is "+ listItems.toString());
		return cursor;
	}

	public ListItem getlistItem(long id) {
		Cursor cursor = database.query(MySQLiteHelper.MENUS_TABLE,
				menusTableColumns, MySQLiteHelper.MENUS_ID + "=?", new String[]{Long.toString(id)}, null, null, null);

		cursor.moveToFirst();
		ListItem item = new ListItem();
		while(!cursor.isAfterLast()) {
			item.listItem = cursor.getString(1);
			item.listType = cursor.getString(2);
			item.nextType = cursor.getString(3);
			item.next = cursor.getString(4);
			cursor.moveToNext();
		}
		cursor.close();
		return item;
	}


} 