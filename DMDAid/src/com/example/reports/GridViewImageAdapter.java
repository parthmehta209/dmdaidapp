package com.example.reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.example.util.AppConstants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class GridViewImageAdapter extends BaseAdapter {

	private Activity activity;
	private List<String> filePaths; 
	private int imageWidth;
	private long reportId;

	public GridViewImageAdapter(Activity activity, List<String> filePaths, 
			long reportId, int imageWidth) {
		this.activity = activity;
		this.filePaths = filePaths;
		this.imageWidth = imageWidth;
		this.reportId = reportId; 
		
	}


	public void addImage(String filename) {
		filePaths.add(filename);
	}

	@Override
	public int getCount() {
		return this.filePaths.size();
	}

	@Override
	public Object getItem(int position) {
		return this.filePaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView; 
		//if (convertView == null) {
			imageView = new ImageView(activity);
		//} else {
		//	imageView = (ImageView) convertView;
		//}

		imageView.setClickable(true);
		// get screen dimensions
		Bitmap image = decodeFile( android.os.Environment.getExternalStorageDirectory()
				+File.separator+AppConstants.PHOTO_ALBUM+File.separator
				+filePaths.get(position), imageWidth,
				imageWidth);

		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
				imageWidth));
		imageView.setImageBitmap(image);
		// image view click listener
		imageView.setOnClickListener((OnClickListener) 
				new OnImageClickListener(filePaths.get(position),reportId));

		return imageView;
	}

	class OnImageClickListener implements OnClickListener {

		String _filename;
		long _reportId;

		// constructor
		public OnImageClickListener(String filename, long reportId) {
			this._filename = filename;
			this._reportId = reportId;
		}

		@Override
		public void onClick(View v) {
			// on selecting grid view image
			// launch full screen activity
			Log.d(AppConstants.TAG,"Launching full screen activity for " + _filename + "  and reportid " + reportId  );
			Toast.makeText(activity.getApplicationContext(), "Opening Image", Toast.LENGTH_SHORT).show();
			Intent i = new Intent(activity, FullScreenViewActivity.class);
			i.putExtra("filename", _filename);
			i.putExtra("reportid", _reportId);
			activity.startActivity(i);
		}

	}

	/*
	 * Resizing image size
	 */
	public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
		try {

			File f = new File(filePath);

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}


	public List<String> getFiles() {
		return filePaths;
	}


}