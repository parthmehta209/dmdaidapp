package com.example.dmdaid;

import com.example.data.Medication;
import com.example.data.MedicationDataSource;
import com.example.util.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewMedication extends Activity {
	
	EditText medicationNameEditText;
	EditText doseEditText;
	Spinner unitsSpinner;
	EditText timesEditText;
	Spinner timesperSpinner;
	EditText monthStarEditText;
	EditText monthEndEditText;
	Button saveButton;
	String type;
	
	MedicationDataSource dataSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_medication);
		type = getIntent().getStringExtra("type");
		dataSource = new MedicationDataSource(this);
		medicationNameEditText = (EditText)findViewById(R.id.editText1);
		doseEditText = (EditText)findViewById(R.id.editText2);
		unitsSpinner = (Spinner)findViewById(R.id.spinner1);
		timesEditText = (EditText)findViewById(R.id.editText4);
		timesperSpinner = (Spinner)findViewById(R.id.spinner2);
		monthStarEditText = (EditText)findViewById(R.id.editText3);
		monthEndEditText = (EditText)findViewById(R.id.EditText01);
		saveButton = (Button)findViewById(R.id.button1);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Medication medication = new Medication();
				
				if(isEmpty(medicationNameEditText)) {
					Toast toast = new Toast(getApplicationContext());
					toast.setText("Name not entered");
					toast.show();
					return;
				}
				medication.medicationName = medicationNameEditText.getText().toString();
				
				if(isEmpty(doseEditText)) {
					Toast toast = new Toast(getApplicationContext());
					toast.setText("Dose not entered");
					toast.show();
					return;
				}
				medication.dose = Integer.parseInt(doseEditText.getText().toString());
				
				medication.units = (String) unitsSpinner.getSelectedItem();
				
				if(isEmpty(timesEditText)) {
					Toast toast = new Toast(getApplicationContext());
					toast.setText("Times not entered");
					toast.show();
					return;
				}
				medication.times = Integer.parseInt(timesEditText.getText().toString());
				
				medication.timesPer = (String) unitsSpinner.getSelectedItem();
				
				if(isEmpty(monthStarEditText)) {
					Toast toast = new Toast(getApplicationContext());
					toast.setText("Start not entered");
					toast.show();
					return;
				}
				medication.startMonth = monthStarEditText.getText().toString();
				
				if(isEmpty(monthEndEditText)) {
					Toast toast = new Toast(getApplicationContext());
					toast.setText("Start not entered");
					toast.show();
					return;
				}
				medication.endMonth = monthEndEditText.getText().toString();
				medication.dirty = 1; 
				medication.type = type;
				
				Log.d(Utils.TAG, "Saving medication : " + medication);
				dataSource.open();
				dataSource.createNewMedication(medication);
				dataSource.close();
				NewMedication.this.finish();
			}
		});
		
	}
	
	private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_medication, menu);
		return true;
	}

}
