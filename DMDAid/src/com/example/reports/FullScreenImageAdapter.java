package com.example.reports;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import com.example.data.ReportsDataSource;
import com.example.dmdaid.R;
import com.example.dmdaid.R.id;
import com.example.dmdaid.R.layout;
import com.example.util.AppConstants;
import com.example.util.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FullScreenImageAdapter extends PagerAdapter {

	private Activity _activity;
	private List<String> _imagePaths;
	private LayoutInflater inflater;
	private Utils util; 
	private int width; 
	private ReportsDataSource datasource; 
	
	// constructor
	public FullScreenImageAdapter(Activity activity, List<String> imagePaths) {
		this._activity = activity;
		this._imagePaths = imagePaths;
		util = new Utils(_activity.getApplicationContext());
		width = util.getScreenWidth();
		datasource = new ReportsDataSource(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		return this._imagePaths.size();
	}

	public void deleteImage(int position)  {
		datasource.open();
		datasource.removeImage(_imagePaths.get(position));
		datasource.close();
		_imagePaths.remove(position);
		
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imgDisplay;
		

		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
				false);

		imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
		

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Log.d(AppConstants.TAG,"FullScreenImageAdapter creating bitmap from file");
		Bitmap bitmap = decodeFile(Environment.getExternalStorageDirectory() + File.separator+
				AppConstants.PHOTO_ALBUM+File.separator+ _imagePaths.get(position), width,width);
		Log.d(AppConstants.TAG,"FullScreenImageAdapter Bitmap created");
		imgDisplay.setImageBitmap(bitmap);

		
		((ViewPager) container).addView(viewLayout);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}
	
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
	
	public class DeleteButtonListner implements OnClickListener {

		int position; 
		FullScreenImageAdapter adapter; 
		public DeleteButtonListner(int position, FullScreenImageAdapter adapter) {
			this.position = position;
			this.adapter = adapter;
		}
		
		@Override
		public void onClick(View v) {
			Log.d(AppConstants.TAG,"Removing image on position:" + position);
			adapter.deleteImage(position);
		}
		
	}
}

