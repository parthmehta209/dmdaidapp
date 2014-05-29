package com.example.reports;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.data.MySQLiteHelper;
import com.example.data.Report;
import com.example.data.ReportsDataSource;
import com.example.dmdaid.R;
import com.example.dmdaid.R.id;
import com.example.dmdaid.R.layout;
import com.example.dmdaid.R.menu;
import com.example.util.AppConstants;
import com.example.util.Utils;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ReportList extends Activity {

	ListView listView; 
	EditText editText;
	Button addReportButton; 
	ReportsDataSource dataSource; 
	String category; 
	SimpleCursorAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_list);

		category = getIntent().getStringExtra("category");
		if(category == null) category = "Neurology";
		setTitle(category + " Reports");
		Log.d(AppConstants.TAG,"The reports for category: " + category + " are shown now");
		listView = (ListView) findViewById(R.id.listView1);
		editText = (EditText) findViewById(R.id.editText1);
		addReportButton = (Button) findViewById(R.id.button1);

		dataSource = new ReportsDataSource(ReportList.this);
		dataSource.open();
		Cursor cursor = dataSource.getReportsForCategory(category);
		dataSource.close();

		editText.setSelected(false);

		adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.rowlayout,
				cursor,
				new String[] {MySQLiteHelper.REPORTS_NAME,MySQLiteHelper.REPORTS_DATE},
				new int[]{R.id.textView1,R.id.textView2}, 
				SimpleCursorAdapter.FLAG_AUTO_REQUERY);

		addReportButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(AppConstants.TAG,"The text in the edit text is " + editText.getText().toString());


				Calendar c = Calendar.getInstance();
				int mYear = c.get(Calendar.YEAR);
				int mMonth = c.get(Calendar.MONTH);
				int mDay = c.get(Calendar.DAY_OF_MONTH);
				System.out.println("the selected " + mDay);
				DatePickerDialog dialog = new DatePickerDialog(ReportList.this,
						new mDateSetListener(), mYear, mMonth, mDay);
				dialog.show();


			}
		});

		listView.setAdapter(adapter);

		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {

				Log.d(AppConstants.TAG, "The report id is "+ id);
				dataSource.open();
				Report report = dataSource.getReport(id);
				dataSource.close();

				if(report.getReportType().equals(Utils.IMG_REPORT)) {
					Intent intent = new Intent(getApplicationContext(), ReportDisplay.class);
					intent.putExtra("REPORTID", id);
					startActivity(intent);
				} else if(report.getReportType().equals(Utils.PDF_REPORT)) {
					File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+
							File.separator+AppConstants.PHOTO_ALBUM + File.separator + report.getPdfPath());
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
				}

			}
		});

		listView.setLongClickable(true);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long reportId) {

				AlertDialog.Builder builder = new AlertDialog.Builder(ReportList.this);
				builder.setMessage("Delete Report ?")
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dataSource.open();
						dataSource.deleteReport(reportId);
						Cursor cursor = dataSource.getReportsForCategory(category);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report_list, menu);
		return true;
	}
	@SuppressLint("NewApi")
	class mDateSetListener implements DatePickerDialog.OnDateSetListener {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			// getCalender();
			int mYear = year;
			int mMonth = monthOfYear;
			int mDay = dayOfMonth;
			String report = editText.getText().toString(); 
			if(report == null | report.equals("")) return; 
			dataSource.open();
			Log.d(AppConstants.TAG,"The text in the edit text is " + report );
			dataSource.createNewReport(null,editText.getText().toString(), category,
					String.format("%d-%d-%d", mMonth,mDay,mYear),Utils.IMG_REPORT,null,1);
			Cursor cursor = dataSource.getReportsForCategory(category);
			editText.setText("");
			dataSource.close();
			adapter.changeCursor(cursor);


		}
	}


}
