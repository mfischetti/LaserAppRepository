package com.example.laser;

import java.util.ArrayList;
import java.util.Set;



import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;

public class BluetoothActivity extends Activity {
	ListView listViewPaired;
	ArrayList<String> arrayListpaired;
	ArrayAdapter<String> adapter,detectedAdapter;
	BluetoothDevice bdDevice;
	BluetoothClass bdClass;
	ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
	ListItemClickedonPaired listItemClickedonPaired;
	BluetoothAdapter bluetoothAdapter = null;
	ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;
	BluetoothAdapter mBluetoothAdapter;
	Player player;

	String pid;
	String currentPlayers;
	String GameMode;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		Bundle bundle = getIntent().getExtras();
		pid = bundle.getString("gamepid");
		currentPlayers = bundle.getString("current_players");
		//String player = bundle.getString("player1");
		GameMode = bundle.getString("gamemode");
		player = (Player) getIntent().getSerializableExtra("player");

		
		listViewPaired = (ListView) findViewById(R.id.listViewPaired);
		arrayListpaired = new ArrayList<String>();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();

		listItemClickedonPaired = new ListItemClickedonPaired();

		arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();
		adapter= new ArrayAdapter<String>(BluetoothActivity.this, android.R.layout.simple_list_item_1, arrayListpaired);
		detectedAdapter = new ArrayAdapter<String>(BluetoothActivity.this, android.R.layout.simple_list_item_single_choice);
		//	listViewDetected.setAdapter(detectedAdapter);

		detectedAdapter.notifyDataSetChanged();
		listViewPaired.setAdapter(adapter);
		// Show the Up button in the action bar.
	//	setupActionBar();
	}


	protected void onStart(){
		super.onStart();
		getPairedDevices();

		listViewPaired.setOnItemClickListener(listItemClickedonPaired);
	}

	private void getPairedDevices() {
		Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();           
		if(pairedDevice.size()>0)
		{
			for(BluetoothDevice device : pairedDevice)
			{
				arrayListpaired.add(device.getName()+"\n"+device.getAddress());
				arrayListPairedBluetoothDevices.add(device);
			}
		}
		adapter.notifyDataSetChanged();
	}
	class ListItemClickedonPaired implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			bdDevice = arrayListPairedBluetoothDevices.get(position);
			player.setBluetoothMac(bdDevice.getAddress());

			Intent in = new Intent(getApplicationContext(),
					JoinActivity.class);
			// sending pid to next activity
			in.putExtra("player", player);
			in.putExtra("gamepid", pid);
			in.putExtra("current_players", currentPlayers);
			in.putExtra("gamemode",GameMode);
			// starting new activity and expecting some response back
			startActivity(in);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
/*	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bluetooth, menu);
		return true;
	}

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
*/
}
