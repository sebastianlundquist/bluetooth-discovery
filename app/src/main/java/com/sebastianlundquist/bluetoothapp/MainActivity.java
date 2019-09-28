package com.sebastianlundquist.bluetoothapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

	ListView deviceListView;
	TextView statusTextView;
	Button searchButton;

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
		}
	};

	public void search(View view) {
		statusTextView.setText(R.string.searching);
		searchButton.setEnabled(false);
		bluetoothAdapter.startDiscovery();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		deviceListView = findViewById(R.id.deviceListView);
		statusTextView = findViewById(R.id.statusTextView);
		searchButton = findViewById(R.id.searchButton);

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(broadcastReceiver, intentFilter);
	}
}