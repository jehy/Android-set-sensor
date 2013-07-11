package com.sma.setsensor;

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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	int sensor_id = 1;

	private static long minTimeMillis = 2000;
	private static long minDistanceMeters = 10;
	private static float minAccuracyMeters = 35;
	private int lastStatus = 0;
	private static boolean showingDebugToast = false;
	private static boolean showingRecToast = false;

	private LocationManager lm;
	private LocationListener locationListener;
	Location lastloc = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new MyLocationListener();
		setListener(null);
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

	public void increment_id(View view) {
		sensor_id++;
		String f = "ID " + String.valueOf(sensor_id);
		TextView c = (TextView) findViewById(R.id.sensor_id);
		c.setText(f);
	}

	public void decrement_id(View view) {
		if (sensor_id == 0)
			return;
		sensor_id--;
		String f = "ID " + String.valueOf(sensor_id);
		TextView c = (TextView) findViewById(R.id.sensor_id);
		c.setText(f);
	}

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
							MainActivity.this.finish();
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

		/*
		 * {"widget": { "debug": "on", "window": { "title":
		 * "Sample Konfabulator Widget", "name": "main_window", "width": 500,
		 * "height": 500 }, "image": { "src": "Images/Sun.png", "name": "sun1",
		 * "hOffset": 250, "vOffset": 250, "alignment": "center" }, "text": {
		 * "data": "Click Here", "size": 36, "style": "bold", "name": "text1",
		 * "hOffset": 250, "vOffset": 100, "alignment": "center", "onMouseUp":
		 * "sun1.opacity = (sun1.opacity / 100) * 90;" } }
		 */

		EditText c = (EditText) findViewById(R.id.sensor_descr);
		String descr = c.getText().toString();
		c = (EditText) findViewById(R.id.sensor_name);
		String name = c.getText().toString();
		String t = "{action: add_sensor," + "sensor_id:" + this.sensor_id + ","
				+ "time:" + String.valueOf(dt.getTime() / 1000) + "," + "name:"
				+ name + "," + "description" + descr + "," + "long:"
				+ lastloc.getLongitude() + "," + "lat:" + lastloc.getLatitude()
				+ "," + "alt:" + lastloc.getAltitude() + "}";
	}

}
