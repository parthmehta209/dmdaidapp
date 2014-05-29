package com.example.dmdaid;

import com.example.data.Medication;
import com.example.data.MedicationDataSource;
import com.example.util.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MedicationView extends Activity {

	MedicationDataSource dataSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medication_view);
		dataSource = new MedicationDataSource(this);
		Integer id = getIntent().getIntExtra("id", -1);
		if(id == -1) {
			Log.d(Utils.TAG, "No id specified");
		}
		
		dataSource.open();
		Medication medication = dataSource.getMedication(id);
		dataSource.close();
		
		TextView tv = (TextView)findViewById(R.id.medicationName);
		tv.setText(medication.medicationName);
		
		tv = (TextView)findViewById(R.id.dose1);
		tv.setText(""+medication.dose);
		tv = (TextView)findViewById(R.id.units1);
		tv.setText(medication.units);
		tv = (TextView)findViewById(R.id.times1);
		tv.setText(""+medication.times);
		tv = (TextView)findViewById(R.id.timesper1);
		tv.setText(medication.timesPer);
		tv = (TextView)findViewById(R.id.startMonth1);
		tv.setText(medication.startMonth);
		tv = (TextView)findViewById(R.id.endMonth1);
		tv.setText(medication.endMonth);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.medication_view, menu);
		return true;
	}

}
