package com.example.dmdaid;

import java.util.Calendar;

import com.example.data.ListItem;
import com.example.data.MenuDataSource;
import com.example.data.MySQLiteHelper;
import com.example.dmdaid.R;
import com.example.dmdaid.R.id;
import com.example.dmdaid.R.layout;
import com.example.dmdaid.R.menu;
import com.example.reports.ReportList;
import com.example.restbackend.MyResultReceiver;
import com.example.restbackend.MyResultReceiver.Receiver;
import com.example.restbackend.RestIntentService;
import com.example.util.AppConstants;
import com.example.util.Utils;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MenuList extends Activity implements Receiver{

	String listType; 
	ListView listView; 
	MenuDataSource dataSource;
	ProgressDialog progress;
	MyResultReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		listView = (ListView)findViewById(R.id.listView2);
		listView.setTextFilterEnabled(true);
		mReceiver = new MyResultReceiver(new Handler());
		mReceiver.setReceiver(this);
		progress = new ProgressDialog(this);

		listType = getIntent().getStringExtra("listtype");
		if(listType == null) 
			listType = "DMDAid";
		setTitle(listType);

		dataSource = new MenuDataSource(getApplicationContext());
		dataSource.open();
		Cursor cursor = dataSource.getList(listType);
		dataSource.close();

		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list, RECORD_TYPES);


		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.general_rowlayout,
				cursor,
				new String[] {MySQLiteHelper.MENUS_LIST_ITEM},
				new int[]{R.id.textView1}, 
				SimpleCursorAdapter.FLAG_AUTO_REQUERY);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {

				dataSource.open();
				ListItem item = dataSource.getlistItem(id);
				dataSource.close();
				if(item.nextType.equals("list"))
				{
					Intent intent = new Intent(getApplicationContext(), MenuList.class);
					intent.putExtra("listtype", item.next);
					startActivity(intent);
				}
				else if(item.nextType.equals("report"))
				{
					Intent intent = new Intent(getApplicationContext(), ReportList.class);
					intent.putExtra("category", item.next);
					startActivity(intent);
				} 
				else if(item.nextType.equals("Medications")) {
					Intent intent = new Intent(getApplicationContext(),MedicationList.class);
					intent.putExtra("type", item.next);
					startActivity(intent);
				}
				else {
					Log.d(AppConstants.TAG,"Any other type is not supported yet");
				}

			}
		});




	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_list, menu);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.sync:
			Intent intent = new Intent(this,RestIntentService.class);
			intent.putExtra(Utils.ACTION_TAG, "syncReports");
			intent.putExtra(Utils.RECEIVER_TAG, mReceiver);
			startService(intent);
			progress.setTitle("Downloading Reports");
			progress.setMessage("Please wait while reports are downloaded");
			progress.show();

			return true;
			
		case R.id.alarm:
			DialogFragment newFragment = new TimePickerFragment();
		    newFragment.show(getFragmentManager(), "timePicker");
		    return true;
		case R.id.cleardb:
			dataSource.deleteDatabase();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		if(resultCode == 200) {
			String message = resultData.getString("message", null);
			if(message.equals("finish"))
				progress.dismiss();
			else 
				progress.setMessage(message);
		} else {
			progress.dismiss();
			Toast toast = new Toast(this);
			toast.setText("Could not download reports");
			toast.show();
		}

	}


	@SuppressLint("NewApi")
	public static class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			Dialog dialog = new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
			dialog.setTitle("Set the time for your daily questionaire");
			// Create a new instance of TimePickerDialog and return it
			return dialog;
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			AlarmManager alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(getActivity(), RestIntentService.class);
			intent.putExtra(Utils.ACTION_TAG, "getesm");
			PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarmMgr.cancel(pendingIntent);
			Calendar alarmTime = Calendar.getInstance();
			alarmTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			alarmTime.set(Calendar.MINUTE, minute);
			alarmTime.set(Calendar.SECOND, 0);
			alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent); 
		}
	}

}
