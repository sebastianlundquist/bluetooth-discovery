package com.sebastianlundquist.bluetoothapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	ListView deviceListView;
	TextView statusTextView;
	Button searchButton;
	ArrayList<String> bluetoothDevices = new ArrayList<>();
	ArrayList<String> addresses = new ArrayList<>();
	ArrayAdapter adapter;

	BluetoothAdapter bluetoothAdapter;

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i("INFO", action);

			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				statusTextView.setText(R.string.finished);
				searchButton.setEnabled(true);
			}
			else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String name = device.getName();
				String address = device.getAddress();
				String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
				Log.i("Device Found", "Name: " + name + ", Address: " + address + ", RSSI: " + rssi);
				if (!addresses.contains(address)) {
					addresses.add(address);
					String deviceString = "";
					if (name == null || name.equals("")) {
						deviceString = address + " - RSSI = " + rssi + "dBm";
					}
					else {
						deviceString = name + " - RSSI = " + rssi + "dBm";
					}
					bluetoothDevices.add(deviceString);
					adapter.notifyDataSetChanged();
				}
			}
		}
	};

	public void search(View view) {
		if (bluetoothAdapter == null) {
			new AlertDialog.Builder(MainActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Bluetooth Not Supported")
					.setMessage("This device does not support Bluetooth. Try another device.")
					.setNegativeButton("Cancel", null)
					.show();
			return;
		}
		else if (!bluetoothAdapter.isEnabled()) {
			new AlertDialog.Builder(MainActivity.this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Bluetooth Disabled")
					.setMessage("Bluetooth is disabled. Please enable Bluetooth.")
					.setPositiveButton("OK", null)
					.show();
			return;
		}
		statusTextView.setText(R.string.searching);
		searchButton.setEnabled(false);
		bluetoothDevices.clear();
		addresses.clear();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
			switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
				case PackageManager.PERMISSION_DENIED:
					if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(MainActivity.this,
								new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
					}
					break;
				case PackageManager.PERMISSION_GRANTED:
					break;
			}
		}
		bluetoothAdapter.startDiscovery();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		deviceListView = findViewById(R.id.deviceListView);
		statusTextView = findViewById(R.id.statusTextView);
		searchButton = findViewById(R.id.searchButton);
		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, bluetoothDevices);
		deviceListView.setAdapter(adapter);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(broadcastReceiver, intentFilter);
	}
}
