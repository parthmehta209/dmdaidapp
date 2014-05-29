package com.example.util;

import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.text.StaticLayout;
import android.view.Display;
import android.view.WindowManager; 

public class Utils {
	
	public static DefaultHttpClient httpClient;
	
	public static final String SERVER_URL = "http://192.168.43.219:9001/"; 
	
	public static final String TAG="DMD";

	public static final String ACTION_TAG = "action";

	public static final String RECEIVER_TAG = "receiver";

	public static final String RESULT_TAG = "result";
	
	public static final String IMG_REPORT = "img";
	public static final String PDF_REPORT = "pdf";
	
	public static Cookie cookie;
	
	private Context _context;
	  
	    // constructor
	public Utils(Context context) {
	        this._context = context;
	}
	
	public static DefaultHttpClient getHttpClient() {
		if(httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}
	
	/*
     * getting screen width
     */
    @SuppressLint("NewApi")
	public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
 
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

}
