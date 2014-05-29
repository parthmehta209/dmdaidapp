package com.example.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.example.data.ReportsDataSource;
import com.example.dmdaid.R;
import com.example.dmdaid.R.id;
import com.example.dmdaid.R.layout;
import com.example.dmdaid.R.menu;
import com.example.util.AppConstants;
import com.example.util.Utils;

import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

public class ReportDisplay extends Activity {

	protected static final int SELECT_FILE = 200;
	Button button;
	//ImageAdapter adapter; 
	protected static final int REQUEST_CAMERA = 100; 
	String tempFileName;
	private GridViewImageAdapter adapter;
	private GridView gridView;
	private int columnWidth;
	private Utils utils; 
	private ReportsDataSource datasource; 
	long reportId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health_report);
		Intent intent = this.getIntent(); 
		reportId = intent.getLongExtra("REPORTID", reportId);
		Log.d(AppConstants.TAG,"The report id is " + reportId);

		gridView = (GridView) findViewById(R.id.gridView1);
		utils = new Utils(getApplicationContext());

		datasource = new ReportsDataSource(ReportDisplay.this);
		datasource.open();
		List<String> files = datasource.getImagesForReport(reportId);
		datasource.close();
		// Initialise grid layout
		initializeGridLayout();

		// Instance of ImageAdapter Class
		adapter = new GridViewImageAdapter(ReportDisplay.this, files, 
				reportId,columnWidth);

		gridView.setAdapter(adapter);

		// create a directory
		File folder = new File(Environment.getExternalStorageDirectory() + File.separator+AppConstants.PHOTO_ALBUM);
		boolean success = true;
		if (!folder.exists()) 
			success = folder.mkdir();
		if (success) 
			Log.d("DMD", "Created DMD Reprts Folder successfully");
		else
			Log.d("DMD","Could not creaate New Folder");


		button = (Button) findViewById(R.id.button1);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("DMD","Button pressed adding new image");
				selectImage();
			}
		});


	}

	private void initializeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				AppConstants.GRID_PADDING, r.getDisplayMetrics());

		columnWidth = (int) ((utils.getScreenWidth() - ((AppConstants.NUM_OF_COLUMNS + 1) * padding)) / AppConstants.NUM_OF_COLUMNS);

		gridView.setNumColumns(AppConstants.NUM_OF_COLUMNS);
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);
		gridView.setHorizontalSpacing((int) padding);
		gridView.setVerticalSpacing((int) padding);

	}

	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
		"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(ReportDisplay.this);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					tempFileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
					File f = new File(android.os.Environment
							.getExternalStorageDirectory() + File.separator+ AppConstants.PHOTO_ALBUM, tempFileName);
					try {
						f.createNewFile();
						
						Log.d("DMD","Created new file:"+tempFileName);
					} catch (IOException e) {
						Log.d("DMD","Could not create temp file");
						e.printStackTrace();
					} 
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Library")) {
					dialog.dismiss();
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri = null;
		File f = null;
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CAMERA) {
				Log.d("DMD","Picture retrned now show");
				f = new File(Environment.getExternalStorageDirectory() + File.separator + AppConstants.PHOTO_ALBUM ,tempFileName);
				uri = Uri.fromFile(f);
				

			} else if (requestCode == SELECT_FILE) {
				uri = data.getData();
			}

			datasource.open();
			datasource.addImageToReport(tempFileName, reportId);
			datasource.close();
			adapter.addImage(tempFileName);
			adapter.notifyDataSetChanged();

		} else if (resultCode == RESULT_CANCELED) {
			
			Log.d(Utils.TAG, "The user cancelled the image capture deleting file " + tempFileName);
			f = new File(Environment.getExternalStorageDirectory() + File.separator + AppConstants.PHOTO_ALBUM ,tempFileName);
			f.delete();
		}
            
	}

	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first
		Log.d(AppConstants.TAG,"On pause files list is " + adapter.getFiles().toString());
		List<String> gridfiles = adapter.getFiles();
		datasource.open();

		int size = gridfiles.size();
		for (int i = size - 1; i >= 0; i--) {
		    if(datasource.FileExists(gridfiles.get(i)) == false ){
		        gridfiles.remove(i);
		    }
		}
		datasource.close();
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload_record, menu);
		return true;
	}

}
