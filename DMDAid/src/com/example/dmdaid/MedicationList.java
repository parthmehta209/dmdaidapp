package com.example.dmdaid;

import com.example.data.MedicationDataSource;
import com.example.data.MySQLiteHelper;
import com.example.dmdaid.MenuList.TimePickerFragment;
import com.example.restbackend.RestIntentService;
import com.example.util.AppConstants;
import com.example.util.Utils;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MedicationList extends Activity {

	MedicationDataSource dataSource;
	String type;
	SimpleCursorAdapter adapter;
	TextView tvTextView;
	ListView listView;

	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medications);
		listView = (ListView)findViewById(R.id.listView1);
		type = getIntent().getStringExtra("type");
		// based on type="med" or "otc" do stuff
		tvTextView = (TextView)findViewById(R.id.textView1);
		if(type.equals("med"))
			tvTextView.setText("Medications");
		else 
			tvTextView.setText("OTC Meds");
		
		dataSource = new MedicationDataSource(this);
		dataSource.open();
		Cursor cursor = dataSource.getMedicationsForType(type);
		dataSource.close();

		adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.rowlayout2,
				cursor,
				new String[] {MySQLiteHelper.MEDICATION_NAME,MySQLiteHelper.MEDICATION_STARTMONTH,MySQLiteHelper.MEDICATION_ENDMONTH},
				new int[]{R.id.title,R.id.startMonth,R.id.endMonth}, 
				SimpleCursorAdapter.FLAG_AUTO_REQUERY);
		listView.setAdapter(adapter);

		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				Intent intent = new Intent(getApplicationContext(),MedicationView.class);
				intent.putExtra("id", (int)id);
				startActivity(intent);
				
			}
		});

		listView.setLongClickable(true);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long medicationId) {

				AlertDialog.Builder builder = new AlertDialog.Builder(MedicationList.this);
				builder.setMessage("Delete Medication ?")
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dataSource.open();
						dataSource.deleteMedication(medicationId);
						Cursor cursor = dataSource.getMedicationsForType(type);
						dataSource.close();
						adapter.changeCursor(cursor);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				builder.show();

				return true;
			}

		});
		
	}

	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    
	    dataSource.open();
	    Cursor cursor = dataSource.getMedicationsForType(type);
	    dataSource.close();
	    adapter.changeCursor(cursor);
	   
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.medications, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_new:
			Intent intent = new Intent(this,NewMedication.class);
			intent.putExtra("type", type);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
}
