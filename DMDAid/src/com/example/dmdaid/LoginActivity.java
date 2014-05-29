package com.example.dmdaid;


import com.example.restbackend.MyResultReceiver;
import com.example.restbackend.RestIntentService;
import com.example.restbackend.MyResultReceiver.Receiver;
import com.example.util.Utils;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity  implements Receiver{

	EditText userNameEditText;
	EditText passwordEditText;
	Button button;
	ProgressDialog progress;
	MyResultReceiver mReceiver;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		userNameEditText = (EditText)findViewById(R.id.editText1);
		passwordEditText = (EditText)findViewById(R.id.editText2);
		button = (Button)findViewById(R.id.button1);
		mReceiver = new MyResultReceiver(new Handler());
		mReceiver.setReceiver(this);

		progress = new ProgressDialog(this);
		progress.setTitle("Logging in");
		progress.setMessage("Verifying Credentials");


		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(userNameEditText.getText().equals("") ||
						passwordEditText.getText().equals("")){
					Log.d(Utils.TAG, "The user name or pass is blank");
					return;
				}

				Intent intent = new Intent(getApplicationContext(), RestIntentService.class);

				intent.putExtra("username", userNameEditText.getText().toString());
				intent.putExtra("password", passwordEditText.getText().toString());
				intent.putExtra(Utils.ACTION_TAG,"login" );
				intent.putExtra(Utils.RECEIVER_TAG, mReceiver);

				startService(intent);
				progress.show();
			}
		});


		SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
		String username = prefs.getString("username", null);
		if(username != null)
			userNameEditText.setText(username);
		String password = prefs.getString("password", null);
		if(password != null)
			passwordEditText.setText(password);
		
		if(username != null && password != null) {
			Intent intent = new Intent(getApplicationContext(), RestIntentService.class);

			intent.putExtra("username", username);
			intent.putExtra("password", password);
			intent.putExtra(Utils.ACTION_TAG,"login" );
			intent.putExtra(Utils.RECEIVER_TAG, mReceiver);

			startService(intent);
			progress.show();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		String result = resultData.getString(Utils.RESULT_TAG);
		Log.d(Utils.TAG, "Result tag is " + result);

		if(resultCode == 200)
		{
			progress.dismiss();
			SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
			editor.putString("username",userNameEditText.getText().toString());
			editor.putString("password",passwordEditText.getText().toString());
			editor.commit();

			startActivity(new Intent(this,MenuList.class));
		} else if (resultCode == 400) {
			progress.setMessage(resultData.getString(Utils.RESULT_TAG));
		}

	}




}
