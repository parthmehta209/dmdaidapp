package com.example.restbackend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.example.data.Medication;
import com.example.data.MedicationDataSource;
import com.example.data.Report;
import com.example.data.ReportsDataSource;
import com.example.util.AppConstants;
import com.example.util.Utils;

import android.R.integer;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.os.storage.OnObbStateChangeListener;
import android.util.JsonWriter;
import android.util.Log;

public class RestIntentService extends IntentService {

	ResultReceiver rec;
	ReportsDataSource dataSource;

	public RestIntentService() {
		super("RestIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		rec = intent.getParcelableExtra(Utils.RECEIVER_TAG);
		String action = intent.getStringExtra(Utils.ACTION_TAG);
		Log.d(Utils.TAG, "Action is " + action);

		if (action.equals("login")) {
			handleLogin(intent);
		} else if (action.equals("syncReports")) {
			fetchDirtyReports();
			uploadDirtyReports();
			fetchDirtyMeds();
			uploadDirtyMeds();
		} else if(action.equals("getesm")) {
			fetchEsm();
		}

		Log.d(Utils.TAG, "Activity completed");
		if(rec != null) {
			Bundle bundle = new Bundle();
			bundle.putString("message", "finish");
			rec.send(200, bundle);
		}
	}

	private void uploadDirtyMeds() {
		Log.d(Utils.TAG, "Uploading Dirty Meds");
		Bundle bundle = new Bundle();
		bundle.putString("message", "Uploading Medications");
		rec.send(200, bundle);

		MedicationDataSource dataSource = new MedicationDataSource(this);
		dataSource.open();
		List<Medication> meds = dataSource.getDirtyMedications();
		dataSource.close();
		for(Medication med:meds) {
			int ret = uploadMedication(med);
			if(ret == 200) {
				dataSource.open();
				dataSource.cleanMedication(med._id);
				dataSource.close();
			}

		}


	}

	private int uploadMedication(Medication med) {

		DefaultHttpClient httpClient = Utils.getHttpClient();

		HttpPost httpost = new HttpPost(Utils.SERVER_URL + "medications/" + med.type);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("_id",med._id + ""));
		nvps.add(new BasicNameValuePair("medicationName",""+ med.medicationName));
		nvps.add(new BasicNameValuePair("dose",""+ med.dose));
		nvps.add(new BasicNameValuePair("units",med.units));
		nvps.add(new BasicNameValuePair("times",""+ med.times));
		nvps.add(new BasicNameValuePair("timesPer", med.timesPer));
		nvps.add(new BasicNameValuePair("startMonth",med.startMonth));
		nvps.add(new BasicNameValuePair("endMonth",med.endMonth));
		nvps.add(new BasicNameValuePair("type",med.type));

		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpResponse response = null;
		try {
			response = httpClient.execute(httpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (response == null)
			return 0;
		HttpEntity entity = response.getEntity();
		String responseStatus = response.getStatusLine().toString();
		Log.d(Utils.TAG, "Med upload response: " + responseStatus);

		int resultCode;
		String resultMsg;
		if (responseStatus.contains("400")) {
			resultCode = 400;
			resultMsg = "Connot Log in";
		} else if (responseStatus.contains("200")) {
			resultCode = 200;
			resultMsg = "Uploaded medication";
		} else {
			resultCode = 0;
			resultMsg = "Unknown";
		}

		if (entity != null) {
			try {
				entity.consumeContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resultCode;
	}

	private void fetchDirtyMeds() {
		Log.d(Utils.TAG, "Fetching Dirty Meds");
		Bundle bundle = new Bundle();
		bundle.putString("message", "Fetching Medications");
		rec.send(200, bundle);

		DefaultHttpClient httpClient = Utils.getHttpClient();
		HttpGet httpGet = new HttpGet(Utils.SERVER_URL + "medication/dirty");
		HttpResponse response = null;
		int resultCode = 0;
		try {
			response = httpClient.execute(httpGet);
			if (response == null) {
				Log.d(Utils.TAG, "Could not get a response");
			}
			HttpEntity entity = response.getEntity();
			String responseStatus = response.getStatusLine().toString();
			Log.d(Utils.TAG, "Login form get: " + responseStatus);

			resultCode = 0;
			String resultMsg;
			if (responseStatus.contains("400")) {
				resultCode = 400;
				resultMsg = "Bad request";
			} else if (responseStatus.contains("200")) {
				resultCode = 200;
				resultMsg = "Received reports Successfully";
			} else {
				resultCode = 0;
				resultMsg = "Unknown";
			}
			InputStream inputStream = null;
			String result = null;

			inputStream = entity.getContent();
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();


			if (entity != null) 
				entity.consumeContent();

			Log.d(Utils.TAG, result);
			List<Medication> medications = parseMedicationsArray(result);
			MedicationDataSource dataSource = new MedicationDataSource(this);
			dataSource.open();

			for (Medication medication : medications) {
				medication.dirty = 0; 
				dataSource.createNewMedication(medication);
			}
			dataSource.close();

		} catch (Exception e) {
			Log.d(Utils.TAG, "Exception "+e.toString());
		}

		if(resultCode != 200)
			return;


		Log.d(Utils.TAG, "Marking meds as clean");
		DefaultHttpClient httpClient1 = Utils.getHttpClient();
		HttpGet httpGet1 = new HttpGet(Utils.SERVER_URL + "medication/clean");
		HttpResponse response1 = null;
		try {
			response1 = httpClient1.execute(httpGet1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d(Utils.TAG,"Meds cleaned status: " + response1.getStatusLine());
		HttpEntity entity = response1.getEntity();
		if (entity != null) {
			try {
				entity.consumeContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private List<Medication> parseMedicationsArray(String result) {

		List<Medication> medications = new LinkedList<Medication>();
		JSONArray jArray = null;
		try {
			jArray = new JSONArray(result);

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject oneObject = jArray.getJSONObject(i);
				Medication medication = new Medication();
				medication._id = oneObject.getInt("_id");
				medication.medicationName = oneObject.getString("medicationName");
				medication.dose = oneObject.getInt("dose");
				medication.units = oneObject.getString("units");
				medication.times = oneObject.getInt("times");
				medication.timesPer = oneObject.getString("timesPer");
				medication.startMonth = oneObject.getString("startMonth");
				medication.endMonth = oneObject.getString("endMonth");
				medication.type = oneObject.getString("type");
				medication.dirty = oneObject.getInt("dirty");
				medications.add(medication);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return medications;


	}

	private void fetchEsm() {
		Log.d(Utils.TAG, "Fetching Esm");
		DefaultHttpClient httpClient = Utils.getHttpClient();
		HttpGet httpGet = new HttpGet(Utils.SERVER_URL + "esm");
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			if (response == null) {
				Log.d(Utils.TAG, "Could not get a response");
			}
			HttpEntity entity = response.getEntity();
			String responseStatus = response.getStatusLine().toString();
			Log.d(Utils.TAG, "Login form get: " + responseStatus);

			InputStream inputStream = null;
			String result = null;
			inputStream = entity.getContent();
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
			Log.d(Utils.TAG, "The esm is: " + result);
			if (entity != null) {
				entity.consumeContent();
			}
			if(result.equals("none"))
			{
				Log.d(Utils.TAG, "No esm");
			} else 
				displayEsm(result);

		} catch (Exception e) {
			Log.d(Utils.TAG, "Exception " + e.toString());
		}
	}

	private void displayEsm(String esmString) {
		Aware.setSetting(getContentResolver(), Aware_Preferences.STATUS_ESM, true);
		//Queue the ESM to be displayed when possible
		Intent esm = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
		esm.putExtra(ESM.EXTRA_ESM, esmString);
		sendBroadcast(esm);
	}

	private void uploadDirtyReports() {
		dataSource.open();
		List<Report> reports = dataSource.getDirtyReports();
		dataSource.close();

		for (Report report : reports) {
			Bundle bundle = new Bundle();
			bundle.putString("message", "Uploading " + report.getReportName());
			rec.send(200, bundle);
			uploadReport(report);
			return;
		}

	}

	private void uploadReport(Report report) {
		try {
			DefaultHttpClient client = Utils.getHttpClient();
			HttpPost post = new HttpPost(Utils.SERVER_URL
					+ "reports/imgsave");
			MultipartEntity reqEntity = new MultipartEntity();
			int i=0;
			for (String fileName : report.getImages()) {
				File file = new File(
						Environment.getExternalStorageDirectory() + File.separator
						+ AppConstants.PHOTO_ALBUM, fileName);
				FileBody bin = new FileBody(file,"image/jpeg");
				reqEntity.addPart("reportFile"+i, bin);
				i++;
			}

			reqEntity.addPart("category", new StringBody(report.getCategory()));
			reqEntity.addPart("reporttype", new StringBody(report.getReportType()));
			reqEntity.addPart("username", new StringBody("sampleusername"));
			reqEntity.addPart("reportname", new StringBody(report.getReportName()));
			reqEntity.addPart("date", new StringBody(report.getReportDate()));
			reqEntity.addPart("id", new StringBody(""+report.getReportId()));
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			Log.d(Utils.TAG, "Uploading report " + report.getReportId() + " response:" + response.getStatusLine());

			if(response.getStatusLine().toString().contains("200")) {
				dataSource.cleanReport(report.getReportId());
			}

		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		}
	}

	private void fetchDirtyReports() {
		DefaultHttpClient httpClient = Utils.getHttpClient();
		HttpGet httpGet = new HttpGet(Utils.SERVER_URL + "reports/dirty");
		HttpResponse response = null;
		dataSource = new ReportsDataSource(this);
		try {
			response = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (response == null) {
			Log.d(Utils.TAG, "Could not get a response");
			return;
		}
		HttpEntity entity = response.getEntity();
		String responseStatus = response.getStatusLine().toString();
		Log.d(Utils.TAG, "Login form get: " + responseStatus);

		int resultCode = 0;
		String resultMsg;
		if (responseStatus.contains("400")) {
			resultCode = 400;
			resultMsg = "Bad request";
		} else if (responseStatus.contains("200")) {
			resultCode = 200;
			resultMsg = "Received reports Successfully";
		} else {
			resultCode = 0;
			resultMsg = "Unknown";
		}
		InputStream inputStream = null;
		String result = null;
		try {
			inputStream = entity.getContent();
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			result = sb.toString();
		} catch (Exception e) {

		}

		if (entity != null) {
			try {
				entity.consumeContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Log.d(Utils.TAG, result);
		List<Report> reports = parseReportsArray(result);

		int res = 0;

		for (Report report : reports) {
			Bundle bundle = new Bundle();
			bundle.putString("message", "Fetching " + report.getReportName());
			rec.send(200, bundle);
			if (fetchReport(report) != 200) {
				rec.send(resultCode, null);
				return;
			}
		}

	}

	private int fetchReport(Report report) {
		int ret = 0;
		Log.d(Utils.TAG, "Fetching Report id:" + report.getReportId());
		if (report.getReportType().equals(Utils.PDF_REPORT)) {
			ret = fetchFile(report.getPdfPath());
			if (ret == 200) {
				dataSource.open();
				dataSource.createNewReport((int) report.getReportId(),
						report.getReportName(), report.getCategory(),
						report.getReportDate(), report.getReportType(),
						report.getPdfPath(), 0);
				dataSource.close();
			}
		} else if (report.getReportType().equals(Utils.IMG_REPORT)) {
			for (String image : report.getImages()) {
				ret = fetchFile(image);
				if (ret != 200)
					break;
			}

			if (ret == 200) {
				dataSource.open();
				dataSource.createNewReport((int) report.getReportId(),
						report.getReportName(), report.getCategory(),
						report.getReportDate(), report.getReportType(),
						report.getPdfPath(), 0);
				for (String image : report.getImages()) {
					dataSource.addImageToReport(image, report.getReportId());
				}
				dataSource.close();
			}
		}

		if (ret == 200) {
			Log.d(Utils.TAG, "Marking report" + report.getReportId()
					+ " as clean");
			DefaultHttpClient httpClient = Utils.getHttpClient();
			HttpGet httpGet = new HttpGet(Utils.SERVER_URL + "reports/clean/"
					+ report.getReportId());
			HttpResponse response = null;
			try {
				response = httpClient.execute(httpGet);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d(Utils.TAG,
					"Report cleaned status: " + response.getStatusLine());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return ret;
	}

	private int fetchFile(String filename) {
		Log.d(Utils.TAG, "Fetching file:" + filename + " for report");
		DefaultHttpClient httpClient = Utils.getHttpClient();
		String filenameUrl = filename.replaceAll(" ", "%20");
		HttpGet httpGet = new HttpGet(Utils.SERVER_URL
				+ "assets/healthReports/" + filenameUrl);
		HttpResponse response = null;
		dataSource = new ReportsDataSource(this);
		try {
			response = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (response == null) {
			Log.d(Utils.TAG, "Could not get a response");
		}
		HttpEntity entity = response.getEntity();
		String responseStatus = response.getStatusLine().toString();
		Log.d(Utils.TAG, "Repsponse is: " + responseStatus);

		int resultCode = 0;
		String resultMsg;
		if (responseStatus.contains("400")) {
			resultCode = 400;
			resultMsg = "Bad request";
		} else if (responseStatus.contains("200")) {
			resultCode = 200;
			resultMsg = "Received reports Successfully";
		} else {
			resultCode = 0;
			resultMsg = "Unknown";
		}

		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			inputStream = entity.getContent();
			outputStream = new FileOutputStream(new File(
					Environment.getExternalStorageDirectory() + File.separator
					+ AppConstants.PHOTO_ALBUM, filename));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			Log.d(Utils.TAG, "Saved file :" + filename);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		return resultCode;
	}

	private List<Report> parseReportsArray(String result) {
		List<Report> reports = new LinkedList<Report>();
		JSONArray jArray = null;
		try {
			jArray = new JSONArray(result);

			for (int i = 0; i < jArray.length(); i++) {

				JSONObject oneObject = jArray.getJSONObject(i);
				Report report = new Report();

				report.setReportId(oneObject.getInt("id"));
				Log.d(Utils.TAG, "Report Id:" + report.getReportId()
						+ " is dirty");
				report.setCategory(oneObject.getString("category"));
				report.setReportDate(oneObject.getString("date"));
				report.setReportType(oneObject.getString("reporttype"));
				report.setReportName(oneObject.getString("reportname"));
				report.setPdfPath(oneObject.getString("pdfPath"));
				JSONArray jImages = oneObject.getJSONArray("imageList");
				if (jImages != null) {
					List<String> images = new LinkedList<String>();
					for (int j = 0; j < jImages.length(); j++) {
						String imageName = jImages.getJSONObject(j).getString(
								"imagePath");
						images.add(imageName);
					}
					report.setImages(images);
				}
				reports.add(report);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return reports;
	}

	private void handleLogin(Intent i) {
		String userName = i.getStringExtra("username");
		String password = i.getStringExtra("password");
		DefaultHttpClient httpClient = Utils.getHttpClient();

		HttpPost httpost = new HttpPost(Utils.SERVER_URL + "login");

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("email", userName));
		nvps.add(new BasicNameValuePair("password", password));

		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpResponse response = null;
		try {
			response = httpClient.execute(httpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (response == null)
			return;
		HttpEntity entity = response.getEntity();
		String responseStatus = response.getStatusLine().toString();
		Log.d(Utils.TAG, "Login form get: " + responseStatus);

		int resultCode;
		String resultMsg;
		if (responseStatus.contains("400")) {
			resultCode = 400;
			resultMsg = "Connot Log in";
		} else if (responseStatus.contains("200")) {
			resultCode = 200;
			resultMsg = "Logged In Successfully";
		} else {
			resultCode = 0;
			resultMsg = "Unknown";
		}

		if (entity != null) {
			try {
				entity.consumeContent();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Log.d(Utils.TAG, "Post logon cookies:");
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			Log.d(Utils.TAG, "None");
		} else {
			for (int i1 = 0; i1 < cookies.size(); i1++) {
				Log.d(Utils.TAG, "- " + cookies.get(i1).toString());
			}
			Utils.cookie = cookies.get(0);
		}

		Bundle b = new Bundle();
		b.putString(Utils.RESULT_TAG, resultMsg);
		rec.send(resultCode, b);

	}

}