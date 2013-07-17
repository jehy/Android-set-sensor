package com.sma.setsensor;

import java.util.ArrayList;
import java.util.Date;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  implements NumberPicker.OnValueChangeListener{

	private static final String PREFS_NAME = "SETSENSOR";
	int sensor_id = 0;
	int retr_id = 0;
	int host_id = 1;
	String phone = "+79037162494";//nagios

	private static long minTimeMillis = 2000;
	private static long minDistanceMeters = 10;
	// private static float minAccuracyMeters = 35;
	private int lastStatus = 0;
	private static boolean showingDebugToast = false;
	// private static boolean showingRecToast = false;

	private LocationManager lm;
	private LocationListener locationListener;
	Location lastloc = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new MyLocationListener();
		setListener(null);

		NumberPicker np = (NumberPicker) findViewById(R.id.host_picker);
		np.setMaxValue(100);
		np.setMinValue(0);
		np.setOnValueChangedListener(this);
		/*{

		        @Override
		        public void onClick(View v) {
		            change_host(v);
		        }
		    });*/

		NumberPicker np2 = (NumberPicker) findViewById(R.id.retr_picker);
		np2.setMaxValue(100);
		np2.setMinValue(0);
		np2.setOnValueChangedListener(this);
		/*   np2.setOnClickListener(new View.OnClickListener() {

		        @Override
		        public void onClick(View v) {
		            change_retr(v);
		        }
		    });*/

		NumberPicker np3 = (NumberPicker) findViewById(R.id.sensor_picker);
		np3.setMaxValue(100);
		np3.setMinValue(0);
		np3.setOnValueChangedListener(this);
		/*   np3.setOnClickListener(new View.OnClickListener() {

		        @Override
		        public void onClick(View v) {
		            change_sensor(v);
		        }
		    });*/
	}

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
    	if(picker.getId()==R.id.host_picker)
    		host_id=newVal;
    	if(picker.getId()==R.id.sensor_picker)
    		sensor_id=newVal;
    	if(picker.getId()==R.id.retr_picker)
    		retr_id=newVal;
        //Toast.makeText(this, "change", Toast.LENGTH_SHORT).show();
    }
    
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		lm.removeUpdates(locationListener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setListener(null);
		load_ids();
	}

	public void setListener(View view) {

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeMillis,
				minDistanceMeters, locationListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
/*
	public void change_host(View view) 
	{
		NumberPicker np = (NumberPicker) findViewById(R.id.host_picker);
		host_id=np.getValue();
	}

	public void change_retr(View view) 
	{
		NumberPicker np = (NumberPicker) findViewById(R.id.retr_picker);
		retr_id=np.getValue();
	}
	

	public void change_sensor(View view) 
	{
		NumberPicker np = (NumberPicker) findViewById(R.id.sensor_picker);
		sensor_id=np.getValue();
	}*/
	/*
	public void increment_sid(View view) {
		sensor_id++;
		String f = "SID " + String.valueOf(sensor_id);
		TextView c = (TextView) findViewById(R.id.sensor_id);
		c.setText(f);
	}
*/
	public void load_ids() {

		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		sensor_id = settings.getInt("sensor_id", 0);
		((NumberPicker) findViewById(R.id.sensor_picker)).setValue(sensor_id);
		host_id = settings.getInt("host_id", 1);
		((NumberPicker) findViewById(R.id.host_picker)).setValue(host_id);
		retr_id = settings.getInt("retr_id", 0);
		((NumberPicker) findViewById(R.id.retr_picker)).setValue(retr_id);
	}

	public void save_ids() {
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("sensor_id", sensor_id);
		editor.putInt("host_id", host_id);
		editor.putInt("retr_id", retr_id);
		editor.commit();
	}
/*
	public void decrement_sid(View view) {
		if (sensor_id == 0)
			return;
		sensor_id--;
		String f = "SID " + String.valueOf(sensor_id);
		TextView c = (TextView) findViewById(R.id.sensor_id);
		c.setText(f);
	}

	public void increment_hid(View view) {
		host_id++;
		String f = "HID " + String.valueOf(host_id);
		TextView c = (TextView) findViewById(R.id.host_id);
		c.setText(f);
	}

	public void decrement_hid(View view) {
		if (host_id == 0)
			return;
		host_id--;
		String f = "HID " + String.valueOf(host_id);
		TextView c = (TextView) findViewById(R.id.host_id);
		c.setText(f);
	}

	public void increment_rid(View view) {
		retr_id++;
		String f = "RID " + String.valueOf(retr_id);
		TextView c = (TextView) findViewById(R.id.retr_id);
		c.setText(f);
	}

	public void decrement_rid(View view) {
		if (retr_id == 0)
			return;
		retr_id--;
		String f = "RID " + String.valueOf(retr_id);
		TextView c = (TextView) findViewById(R.id.retr_id);
		c.setText(f);
	}*/

	public class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location loc) {
			if (loc != null) {
				// boolean pointIsRecorded = false;
				try {
					lastloc = loc;
					String f = loc.getLongitude() + " " + loc.getLatitude()
							+ " " + loc.getAltitude();
					EditText c = (EditText) findViewById(R.id.sensor_coords);
					c.setText(f);
					lm.removeUpdates(locationListener);
					ProgressBar p = (ProgressBar) findViewById(R.id.progressBar1);
					p.setVisibility(View.INVISIBLE);
				} catch (Exception e) {
					Log.e("Set Sensor error", e.getMessage());
				}
			}
		}

		public void onProviderDisabled(String provider) {
			if (showingDebugToast)
				Toast.makeText(getBaseContext(),
						"onProviderDisabled: " + provider, Toast.LENGTH_SHORT)
						.show();

		}

		public void onProviderEnabled(String provider) {
			if (showingDebugToast)
				Toast.makeText(getBaseContext(),
						"onProviderEnabled: " + provider, Toast.LENGTH_SHORT)
						.show();

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			String showStatus = null;
			if (status == LocationProvider.AVAILABLE)
				showStatus = "Available";
			if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
				showStatus = "Temporarily Unavailable";
			if (status == LocationProvider.OUT_OF_SERVICE)
				showStatus = "Out of Service";
			if (status != lastStatus && showingDebugToast) {
				Toast.makeText(getBaseContext(), "new status: " + showStatus,
						Toast.LENGTH_SHORT).show();
			}
			lastStatus = status;
		}

	}

	public void ShowErrorMessage(String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		// Add the buttons
		builder.setMessage(text).setTitle("Ошибка");
		builder.setPositiveButton("Выйти",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						MainActivity.this.finish();
						// User clicked OK button
					}
				});
		builder.setNegativeButton("Игнорировать",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
					}
				});
		builder.show();
	}

	public void sendMessage(View view) {
		Date dt = new java.util.Date();
		if (lastloc == null) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			// Add the buttons
			builder.setMessage("Координаты ещё не определены!").setTitle(
					"Ошибка");
			builder.setPositiveButton("Ок",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							// MainActivity.this.finish();
							// User clicked OK button
						}
					});
			builder.show();
			return;
		}

		if (dt.getTime() - lastloc.getTime() > 180 * 1000)// three minutes
		{

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			// Add the buttons
			builder.setMessage("Координаты устарели! Обновить?").setTitle(
					"Ошибка");
			builder.setPositiveButton("Да",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							// MainActivity.this.finish();
							// User clicked OK button
							setListener(null);
						}

					});
			builder.setNegativeButton("Игнорировать",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
						}
					});
			builder.show();
		}

		// EditText c = (EditText) findViewById(R.id.sensor_descr);
		// String descr = c.getText().toString();
		// c = (EditText) findViewById(R.id.sensor_name);
		// String name = c.getText().toString();
		String message = "{\"action\": \"add_sensor\"," + "\"sensor_id\":\""
				+ this.sensor_id + "\"," + "\"retr_id\":\"" + this.retr_id
				+ "\"," + "\"host_id\":\"" + this.host_id + "\","
				+ "\"time\":\"" + String.valueOf(dt.getTime() / 1000)+ "\","+
				//+
				// + name + "\",\"" + "description\": \"" + descr + "\"," +
				"\"long\":\"" + lastloc.getLongitude() + "\"," + "\"lat\":\""
				+ lastloc.getLatitude() + "\"," + "\"alt\":\""
				+ lastloc.getAltitude() + "\"}";

		// SmsManager smsManager = SmsManager.getDefault();
		Log.v("Thread service", "Sending sms to " + phone);
		Log.v("Thread service", "SMS text:  " + message);
		// smsManager.sendTextMessage(phone, null, message, null, null);

		SmsManager sms = SmsManager.getDefault();
		ArrayList<String> parts = sms.divideMessage(message);
		sms.sendMultipartTextMessage(phone, null, parts, null, null);
		save_ids();
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		// Add the buttons
		builder.setMessage("Сообщение о датчике отправлено!")
				.setTitle("Статус");
		builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				// MainActivity.this.finish();
				// User clicked OK button
			}
		});
		builder.show();
		return;
	}

}
