package com.example.reports;

import java.util.List;

import com.example.data.ReportsDataSource;
import com.example.dmdaid.R;
import com.example.dmdaid.R.id;
import com.example.dmdaid.R.layout;
import com.example.util.AppConstants;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class FullScreenViewActivity extends Activity{


	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	private ReportsDataSource datasource;
	private ImageButton deleteButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.activity_fullscreen);

		Log.d(AppConstants.TAG,"Activity Full screen started");
		viewPager = (ViewPager)findViewById(R.id.pager);
		deleteButton = (ImageButton)findViewById(R.id.imageButton1);
		
		Intent i = getIntent();
		String filename = i.getStringExtra("filename");
		Long reportId = i.getLongExtra("reportid",-1);

		datasource = new ReportsDataSource(FullScreenViewActivity.this);
        datasource.open();
        List<String> files = datasource.getImagesForReport(reportId);
        datasource.close(); 
		if(files == null)
			Log.d(AppConstants.TAG,"FIles are null");
		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,files);
		Log.d(AppConstants.TAG,"Setting Adapter");
		viewPager.setAdapter(adapter);

		Log.d(AppConstants.TAG,"Setting position");
		// displaying selected image first
		int position = files.indexOf(filename);
		viewPager.setCurrentItem(position);
		deleteButton.setOnClickListener(new DeleteButtonListner(viewPager, adapter, FullScreenViewActivity.this));
		
	}
	
	public class DeleteButtonListner implements OnClickListener {

		ViewPager viewPager;
		FullScreenImageAdapter adapter;
		Activity activity;
		public DeleteButtonListner(ViewPager viewPager, FullScreenImageAdapter adapter, Activity activity) {
			this.viewPager = viewPager; 
			this.adapter = adapter; 
			this.activity = activity; 
		}
		
		@Override
		public void onClick(View v) {
			int position = viewPager.getCurrentItem();
			int size = adapter.getCount();
			
			
			//TODO fugure out the reason for not being able to scroll to all images if one from middle is deletedif(size == 1)
			if(true)
			{
				Log.d(AppConstants.TAG,"Removing 1st image on position:" + position);

				activity.finish();
				adapter.deleteImage(position);
				adapter.notifyDataSetChanged();
				return;
			}
			
			if(position == size - 1)
			{
				Log.d(AppConstants.TAG,"Removing the last image on position:" + position);
				viewPager.setCurrentItem(0);
				adapter.deleteImage(position);
				adapter.notifyDataSetChanged();
				return;
			}
			
			Log.d(AppConstants.TAG,"Removing image on position:" + position);

			viewPager.setCurrentItem(position+1);
			adapter.deleteImage(position);
			adapter.notifyDataSetChanged();
			
		}
		
	}
}

