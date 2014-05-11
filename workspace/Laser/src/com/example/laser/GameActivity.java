package com.example.laser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener, OnClickListener, SensorEventListener  {
	private static final String TAG = "bluetooth";

	private static String url_update_shooter = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/update_shooter.php";
	private static String url_get_gamedata = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_ingame_info.php";
	private static String url_create_product = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/testCords.php";
	private static String url_get_cords = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_cords.php";
	String playerLat="0";
	String playerLon="0";
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private MyImageView myImageView;

	
	double enemy1x=0;
	double enemy1y=0;
	double enemy2x=0;
	double enemy2y=0;
	
	double teamx=0;
	double teamy=0;
	
	JSONArray playerCords = null;
	float x, y;
	float [] loc;
	float radius = 50;
	double myLat=0, myLon=0;
	Button randomCords;
	// record the compass picture angle turned
	private float currentDegree = 0f;

	// device sensor manager
	private SensorManager mSensorManager;

	public Timer timer2;
	// A request to connect to Location Services
	private LocationRequest mLocationRequest;
	// Stores the current instantiation of the location client in this object
	private LocationClient mLocationClient;
	TextView cords;
	TextView degrees;
	private Location currentLocation;

	boolean mUpdatesRequested;

	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;

	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;
	// Global variable to hold the current location
	//Location mCurrentLocation;
	JSONParser jParser = new JSONParser();
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> productsList;

	JSONArray products = null;
	JSONArray products1 = null;
	JSONArray products2 = null;
	JSONArray products3 = null;

	ArrayList<GamePlayer> AllPlayerInfo = new ArrayList<GamePlayer>();		
	TextView game_time, player_name;
	TextView Blue1, Blue2, Blue3, Blue4;
	TextView Red1, Red2, Red3, Red4;
	TextView ScoreBlue, ScoreRed;
	//Button btnOn, btnOff;
	//TextView txtArduino;
	//	EditText write;
	Timer timer;
	Handler h;
	Player player;
	final int RECIEVE_MESSAGE = 1;        // Status  for Handler
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private StringBuilder sb = new StringBuilder();
	int gameTime = 10;//THIS CONTROLS HOW LONG THE GAME IS
	private ConnectedThread mConnectedThread;
	int success = 0;

	// SPP UUID service
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GAME_INFO = "game_info";
	// MAC-address of Bluetooth module (you must edit this line)
	private String address;
	//HC-05: 00:13:12:23:43:19
	//firefly: 00:12:02:01:03:59
	//second hc-05: 00:13:12:23:53:43
	Drawable back;
	Resources res;
	int bluescore, redscore;
	Vibrator v;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_game);
		Bundle bundle = getIntent().getExtras();
		player = (Player) getIntent().getSerializableExtra("player");
		address = player.getBluetoothMac();
		res = getResources();
		if (player.getTeam().matches("Blue")){
			back = res.getDrawable(R.drawable.blue_back);
		}
		else if (player.getTeam().matches("Red")){
			back = res.getDrawable(R.drawable.red_back);
		}
		else{
			//back = res.getDrawable(R.drawable.neut_back);
		}
		RelativeLayout rellayout = (RelativeLayout) findViewById(R.id.GameLayout);
		rellayout.setBackgroundDrawable(back);
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		//	btnOn = (Button) findViewById(R.id.btnOn);                  // button LED ON
		//btnOff = (Button) findViewById(R.id.btnOff);                // button LED OFF
		game_time = (TextView) findViewById(R.id.txtArduino);      // for display the received data from the Arduino
		//	write = (EditText) findViewById(R.id.write);
		player_name = (TextView) findViewById(R.id.playerName);      // for display name
		Blue1 = (TextView) findViewById(R.id.Blue1);
		Blue2 = (TextView) findViewById(R.id.Blue2);
		Blue3 = (TextView) findViewById(R.id.Blue3);
		Blue4 = (TextView) findViewById(R.id.Blue4);
		Red1 = (TextView) findViewById(R.id.Red1);
		Red2 = (TextView) findViewById(R.id.Red2);
		Red3 = (TextView) findViewById(R.id.Red3);
		Red4 = (TextView) findViewById(R.id.Red4);
		ScoreBlue = (TextView) findViewById(R.id.BlueScore);
		ScoreRed = (TextView) findViewById(R.id.RedScore);
		player_name.setText(player.getName());
	//	degrees = (TextView)findViewById(R.id.degree);
	//	cords = (TextView)findViewById(R.id.cords);
		myImageView = (MyImageView) findViewById(R.id.radar);
		myImageView.setImageResource(R.drawable.radar);
	//	randomCords = (Button)findViewById(R.id.randomLoc);
	//	randomCords.setOnClickListener(this);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		/// Open the shared preferences
		mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		mEditor = mPrefs.edit();
		JSONParser jParser = new JSONParser();
		/*
		 * Create a new location client, using the enclosing class to
		 * handle callbacks.
		 */
		mLocationClient = new LocationClient(this, this, this);
		// Start with updates turned off
		mUpdatesRequested = false;

		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_MSECS);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_MSECS);

		callAsynchronousTask();
		h = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case RECIEVE_MESSAGE:                                                   // if receive massage
					byte[] readBuf = (byte[]) msg.obj;
					String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
					Log.d("Recieved", "Recieve a message of: "+strIncom);
					//	txtArduino.setText(strIncom);
					boolean hit = true;
					int[] temp = new int[4];
					temp=player.getTeamMembers();
					Log.d("Recieved", "team members are: "+temp);
					for (int i = 0; i < temp.length; i++){
						if (Integer.parseInt(strIncom)==temp[i]){
							hit = false;
						}
					}
					if (hit){
						// Vibrate for 400 milliseconds
						v.vibrate(1000);
						UpdateShooter(strIncom);
					}
					Log.d(TAG, "Shot by:"+ strIncom);
					break;
				}
			};
		};

		btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
		checkBTState();


		/*btnOn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//btnOn.setEnabled(false);
				//mConnectedThread.write("1");    // Send "1" via Bluetooth
				mConnectedThread.write(write.getText().toString());
				//Toast.makeText(getBaseContext(), "Turn on LED", Toast.LENGTH_SHORT).show();
			}
		});*/

		/*	btnOff.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				btnOff.setEnabled(false); 
				mConnectedThread.write("0");    // Send "0" via Bluetooth
				//Toast.makeText(getBaseContext(), "Turn off LED", Toast.LENGTH_SHORT).show();
			}
		});*/
	}
	public void UpdateShooter(String killer) {

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("game_id", "game"+player.getGameID()));
		params.add(new BasicNameValuePair("player", ""+player.getPlayerSpot()));
		params.add(new BasicNameValuePair("killer", killer));

		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_update_shooter, "POST", params);



		// Check your log cat for JSON reponse
		Log.d("All Products: ", json.toString());

		try {
			// Checking for SUCCESS TAG
			int success1 = json.getInt(TAG_SUCCESS);

			if (success1 == 1) {
				// products found
				// Getting Array of Products
				products = json.getJSONArray("games"+player.getGameID());


				// looping through All Products

				JSONObject c = products.getJSONObject(0);





				// Starting new intent


			}
			else {
				// failed to update product
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
		if(Build.VERSION.SDK_INT >= 10){
			try {
				final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
				return (BluetoothSocket) m.invoke(device, MY_UUID);
			} catch (Exception e) {
				Log.e(TAG, "Could not create Insecure RFComm Connection",e);
			}
		}
		return  device.createRfcommSocketToServiceRecord(MY_UUID);
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d(TAG, "...onResume - try connect...");

		// Set up a pointer to the remote node using it's address.
		BluetoothDevice device = btAdapter.getRemoteDevice(address);

		// Two things are needed to make a connection:
		//   A MAC address, which we got above.
		//   A Service ID or UUID.  In this case we are using the
		//     UUID for SPP.

		try {
			btSocket = createBluetoothSocket(device);
		} catch (IOException e) {
			errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
		}

		// Discovery is resource intensive.  Make sure it isn't going on
		// when you attempt to connect and pass your message.
		btAdapter.cancelDiscovery();

		// Establish the connection.  This will block until it connects.
		Log.d(TAG, "...Connecting...");
		try {
			btSocket.connect();
			Log.d(TAG, "....Connection ok...");
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
			}
		}

		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Create Socket...");

		mConnectedThread = new ConnectedThread(btSocket);
		mConnectedThread.start();
		mConnectedThread.write(""+player.getPlayerSpot());

		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);

		/*
		 * Get any previous setting for location updates
		 * Gets "false" if an error occurs
		 */
		if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
			mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

			// Otherwise, turn off location updates
		} else {
			mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
			mEditor.commit();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.d(TAG, "...In onPause()...");

		try     {
			btSocket.close();
		} catch (IOException e2) {
			errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
		}
		mSensorManager.unregisterListener(this);

		// Save the current setting for updates
		mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
		mEditor.commit();
	}

	private void checkBTState() {
		// Check for Bluetooth support and then check to make sure it is turned on
		// Emulator doesn't support Bluetooth and will return null
		if(btAdapter==null) {
			errorExit("Fatal Error", "Bluetooth not support");
		} else {
			if (btAdapter.isEnabled()) {
				Log.d(TAG, "...Bluetooth ON...");
			} else {
				//Prompt user to turn on Bluetooth
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 1);
			}
		}
	}

	private void errorExit(String title, String message){
		Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
		finish();
	}

	private class ConnectedThread extends Thread {
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) { }

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			byte[] buffer = new byte[256];  // buffer store for the stream
			int bytes; // bytes returned from read()

			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
					h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
				} catch (IOException e) {
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(String message) {
			Log.d(TAG, "...Data to send: " + message + "...");
			byte[] msgBuffer = message.getBytes();
			try {
				mmOutStream.write(msgBuffer);
			} catch (IOException e) {
				Log.d(TAG, "...Error data send: " + e.getMessage() + "...");    
			}
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			timer.cancel();
			try {
				btSocket.close();
			} catch (IOException e) {
				errorExit("Fatal Error", "In OnKeyDown() and failed to close socket." + e.getMessage() + ".");	
			}
			Intent in = new Intent(getApplicationContext(),
					MainActivity.class);
			// starting new activity and expecting some response back
			startActivity(in);	    
		}
		return super.onKeyDown(keyCode, event);
	}
	public void callAsynchronousTask() {
		final Handler handler = new Handler();
		timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {       
						try {
							GetScores performBackgroundTask = new GetScores();
							// PerformBackgroundTask this class is the class that extends AsynchTask 
							performBackgroundTask.execute();
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 5000 ms
	}
	class GetScores extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
		pDialog = new ProgressDialog(JoinActivity.this);
		pDialog.setMessage("Loading. Please wait...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
			 */
			//test++;
			//BlueScore.setText(test+"");
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			AllPlayerInfo.clear();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("game_id", "game"+player.getGameID()));

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_get_gamedata, "GET", params);


			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());


			try {
				// Checking for SUCCESS TAG
				success = json.getInt(TAG_SUCCESS);
				//RedScore.setText(success+"");
				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_GAME_INFO);
					products1 = json.getJSONArray("game_info1");
					products2 = json.getJSONArray("game_info2");
					products3 = json.getJSONArray("game_info3");



					// looping through All Products

					JSONObject c = products.getJSONObject(0);
					GamePlayer temp = new GamePlayer(""+1, c.getString("Player1"),
							c.getString("Player2"),c.getString("Player3"),c.getString("Player4"),
							c.getString("Player5"),c.getString("Player6"),c.getString("Player7"),
							c.getString("Player8"),c.getString("Team"),c.getString("xLoc"),
							c.getString("yLoc"),c.getString("Score"),getName(c.getString("NameID")));
					AllPlayerInfo.add(temp);
					JSONObject c1 = products1.getJSONObject(0);
					GamePlayer temp1 = new GamePlayer(""+1, c1.getString("Player1"),
							c1.getString("Player2"),c1.getString("Player3"),c1.getString("Player4"),
							c1.getString("Player5"),c1.getString("Player6"),c1.getString("Player7"),
							c1.getString("Player8"),c1.getString("Team"),c1.getString("xLoc"),
							c1.getString("yLoc"),c1.getString("Score"),getName(c1.getString("NameID")));
					AllPlayerInfo.add(temp1);
					JSONObject c2 = products2.getJSONObject(0);
					GamePlayer temp2 = new GamePlayer(""+1, c2.getString("Player1"),
							c2.getString("Player2"),c2.getString("Player3"),c2.getString("Player4"),
							c2.getString("Player5"),c2.getString("Player6"),c2.getString("Player7"),
							c2.getString("Player8"),c2.getString("Team"),c2.getString("xLoc"),
							c2.getString("yLoc"),c2.getString("Score"),getName(c2.getString("NameID")));
					AllPlayerInfo.add(temp2);
					JSONObject c3 = products3.getJSONObject(0);
					GamePlayer temp3 = new GamePlayer(""+1, c3.getString("Player1"),
							c3.getString("Player2"),c3.getString("Player3"),c3.getString("Player4"),
							c3.getString("Player5"),c3.getString("Player6"),c3.getString("Player7"),
							c3.getString("Player8"),c3.getString("Team"),c3.getString("xLoc"),
							c3.getString("yLoc"),c3.getString("Score"),getName(c3.getString("NameID")));
					AllPlayerInfo.add(temp3);
					if(player.getName().contains(temp.NameID)){
						teamx =Double.parseDouble(temp2.xLoc);
						teamy =Double.parseDouble(temp2.yLoc);
						enemy1x =Double.parseDouble(temp1.xLoc);
						enemy1y =Double.parseDouble(temp1.yLoc);
						enemy2x =Double.parseDouble(temp3.xLoc);
						enemy2y =Double.parseDouble(temp3.yLoc);
						/*
						if(temp1.Team.contains(player.getTeam())){
							teamx =Double.parseDouble(temp1.xLoc);
							teamy =Double.parseDouble(temp1.yLoc);
							enemy1x =Double.parseDouble(temp2.xLoc);
							enemy1y =Double.parseDouble(temp2.yLoc);
							enemy2x =Double.parseDouble(temp3.xLoc);
							enemy2y =Double.parseDouble(temp3.yLoc);
						}
						else if(temp2.Team.contains(player.getTeam())){
							teamx =Double.parseDouble(temp2.xLoc);
							teamy =Double.parseDouble(temp2.yLoc);
							enemy1x =Double.parseDouble(temp1.xLoc);
							enemy1y =Double.parseDouble(temp1.yLoc);
							enemy2x =Double.parseDouble(temp3.xLoc);
							enemy2y =Double.parseDouble(temp3.yLoc);
						}
						else{
							teamx =Double.parseDouble(temp3.xLoc);
							teamy =Double.parseDouble(temp3.yLoc);
							enemy1x =Double.parseDouble(temp1.xLoc);
							enemy1y =Double.parseDouble(temp1.yLoc);
							enemy2x =Double.parseDouble(temp2.xLoc);
							enemy2y =Double.parseDouble(temp2.yLoc);
							
						}
						*/
					}
					if(player.getName().contains(temp1.NameID)){
						teamx =Double.parseDouble(temp3.xLoc);
						teamy =Double.parseDouble(temp3.yLoc);
						enemy1x =Double.parseDouble(temp.xLoc);
						enemy1y =Double.parseDouble(temp.yLoc);
						enemy2x =Double.parseDouble(temp2.xLoc);
						enemy2y =Double.parseDouble(temp2.yLoc);
						/*
						if(temp.Team.contains(player.getTeam())){
							teamx =Double.parseDouble(temp.xLoc);
							teamy =Double.parseDouble(temp.yLoc);
							enemy1x =Double.parseDouble(temp2.xLoc);
							enemy1y =Double.parseDouble(temp2.yLoc);
							enemy2x =Double.parseDouble(temp3.xLoc);
							enemy2y =Double.parseDouble(temp3.yLoc);
						}
						else if(temp2.Team.contains(player.getTeam())){
							teamx =Double.parseDouble(temp2.xLoc);
							teamy =Double.parseDouble(temp2.yLoc);
							enemy1x =Double.parseDouble(temp.xLoc);
							enemy1y =Double.parseDouble(temp.yLoc);
							enemy2x =Double.parseDouble(temp3.xLoc);
							enemy2y =Double.parseDouble(temp3.yLoc);
						}
						else{
							teamx =Double.parseDouble(temp3.xLoc);
							teamy =Double.parseDouble(temp3.yLoc);
							enemy1x =Double.parseDouble(temp.xLoc);
							enemy1y =Double.parseDouble(temp.yLoc);
							enemy2x =Double.parseDouble(temp2.xLoc);
							enemy2y =Double.parseDouble(temp2.yLoc);
							
						}
						*/
					}
					if(player.getName().contains(temp2.NameID)){
						/*
						if(temp1.Team.contains(player.getTeam())){
							teamx =Double.parseDouble(temp1.xLoc);
							teamy =Double.parseDouble(temp1.yLoc);
							enemy1x =Double.parseDouble(temp.xLoc);
							enemy1y =Double.parseDouble(temp.yLoc);
							enemy2x =Double.parseDouble(temp3.xLoc);
							enemy2y =Double.parseDouble(temp3.yLoc);
						}
						else if(temp2.Team.contains(player.getTeam())){
							teamx =Double.parseDouble(temp.xLoc);
							teamy =Double.parseDouble(temp.yLoc);
							enemy1x =Double.parseDouble(temp1.xLoc);
							enemy1y =Double.parseDouble(temp1.yLoc);
							enemy2x =Double.parseDouble(temp3.xLoc);
							enemy2y =Double.parseDouble(temp3.yLoc);
						}
						else{
							teamx =Double.parseDouble(temp3.xLoc);
							teamy =Double.parseDouble(temp3.yLoc);
							enemy1x =Double.parseDouble(temp1.xLoc);
							enemy1y =Double.parseDouble(temp1.yLoc);
							enemy2x =Double.parseDouble(temp.xLoc);
							enemy2y =Double.parseDouble(temp.yLoc);
							
						}
						*/
						teamx =Double.parseDouble(temp.xLoc);
						teamy =Double.parseDouble(temp.yLoc);
						enemy1x =Double.parseDouble(temp1.xLoc);
						enemy1y =Double.parseDouble(temp1.yLoc);
						enemy2x =Double.parseDouble(temp3.xLoc);
						enemy2y =Double.parseDouble(temp3.yLoc);
					}
					if(player.getName().contains(temp3.NameID)){
						/*
						if(temp1.Team.contains(player.getTeam())){
							teamx =Double.parseDouble(temp1.xLoc);
							teamy =Double.parseDouble(temp1.yLoc);
							enemy1x =Double.parseDouble(temp2.xLoc);
							enemy1y =Double.parseDouble(temp2.yLoc);
							enemy2x =Double.parseDouble(temp.xLoc);
							enemy2y =Double.parseDouble(temp.yLoc);
						}
						else if(temp2.Team.contains(player.getTeam())){
							teamx =Double.parseDouble(temp2.xLoc);
							teamy =Double.parseDouble(temp2.yLoc);
							enemy1x =Double.parseDouble(temp1.xLoc);
							enemy1y =Double.parseDouble(temp1.yLoc);
							enemy2x =Double.parseDouble(temp.xLoc);
							enemy2y =Double.parseDouble(temp.yLoc);
						}
						else{
							teamx =Double.parseDouble(temp.xLoc);
							teamy =Double.parseDouble(temp.yLoc);
							enemy1x =Double.parseDouble(temp1.xLoc);
							enemy1y =Double.parseDouble(temp1.yLoc);
							enemy2x =Double.parseDouble(temp2.xLoc);
							enemy2y =Double.parseDouble(temp2.yLoc);
							
						}
						*/
						teamx =Double.parseDouble(temp1.xLoc);
						teamy =Double.parseDouble(temp1.yLoc);
						enemy1x =Double.parseDouble(temp.xLoc);
						enemy1y =Double.parseDouble(temp.yLoc);
						enemy2x =Double.parseDouble(temp2.xLoc);
						enemy2y =Double.parseDouble(temp2.yLoc);
					}
				

					// Storing each json item in variable


					// Starting new intent

				}
				else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//publishProgress(args);

			return null;
		}


		public String getName(String number) {
			if (number.charAt(0)=='1'){
				return "Matt";
			}
			if (number.charAt(0)=='2'){
				return "Mike";
			}
			if (number.charAt(0)=='3'){
				return "Alyssa";
			}
			if (number.charAt(0)=='4'){
				return "Theo";
			}
			if (number.charAt(0)=='5'){
				return "Angelo";
			}
			if (number.charAt(0)=='6'){
				return "Eric";
			}
			if (number.charAt(0)=='7'){
				return "DR";
			}
			if (number.charAt(0)=='8'){
				return "Jimmer";
			}
			else{
				return "";
			}
		}
		/*
		protected void onProgressUpdate(String... values) {

		}

		 */

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			////////////////////////////
			///////////////////////////
			//////////////////GAME TIME////////////////////////////
			///////////////////////////////////////////////////////
			int hold_time = gameTime;
			int mins = hold_time/60;
			int secs = hold_time % 60;
			if (secs >= 10){
				if (mins==0){
					game_time.setText("0"+mins+":"+secs);
				}
				else{
					game_time.setText(mins+":"+secs);
				}
			}
			else{
				if(mins==0){
					game_time.setText("0"+mins+":0"+secs);
				}
				else{
					game_time.setText(mins+":0"+secs);
				}
			}
			if (gameTime == 0){
				timer.cancel();
				try {
					btSocket.close();
				} catch (IOException e) {
					errorExit("Fatal Error", "In changing activities, failed to close socket" + e.getMessage() + ".");	
				}
				Intent in = new Intent(getApplicationContext(),
						ResultActivity.class);
				in.putExtra("player", player);
				startActivity(in);	

			}
			else{
				gameTime = gameTime - 1;
			}
			///////////////////////////////////////////////////////
			///////////////Score Display///////////////////////////
			///////////////////////////////////////////////////////
			if (!AllPlayerInfo.isEmpty() && success == 1){
				ArrayList<GamePlayer> AllPlayerInfoTemp = new ArrayList<GamePlayer>();
				AllPlayerInfoTemp.addAll(AllPlayerInfo);
				ArrayList<GamePlayer> ScoreSort = new ArrayList<GamePlayer>();
				int highestscore = -1;
				int position = 0;
				for (int i = 0; i < AllPlayerInfo.size(); i++){
					for (int j = 0; j < AllPlayerInfoTemp.size(); j++){
						if (Integer.parseInt(AllPlayerInfoTemp.get(j).Score) > highestscore){
							highestscore = Integer.parseInt(AllPlayerInfoTemp.get(j).Score);
							position = j;
						}
					}
					ScoreSort.add(AllPlayerInfoTemp.get(position));
					AllPlayerInfoTemp.remove(position);
					highestscore = -1;
					position = 0;
				}

				int blue = 1;
				bluescore = 0;
				int red = 1;
				redscore = 0;
				//int loop = 0;

				for (int loop = 0; loop <ScoreSort.size();loop++){
					if (ScoreSort.get(loop).Team.contains("Blue")){
						if (blue == 1){
							Blue1.setText(ScoreSort.get(loop).NameID+ "\t\t"+ ScoreSort.get(loop).Score);
							Blue2.setText("");
							Blue3.setText("");
							Blue4.setText("");
							blue++;
							bluescore = bluescore + Integer.parseInt(ScoreSort.get(loop).Score);
						}
						else if (blue == 2){
							Blue2.setText(ScoreSort.get(loop).NameID+ "\t\t"+ ScoreSort.get(loop).Score);
							Blue3.setText("");
							Blue4.setText("");
							blue++;
							bluescore = bluescore + Integer.parseInt(ScoreSort.get(loop).Score);

						}
						else if (blue == 3){
							Blue3.setText(ScoreSort.get(loop).NameID+ "\t\t"+ ScoreSort.get(loop).Score);
							Blue4.setText("");
							blue++;
							bluescore = bluescore + Integer.parseInt(ScoreSort.get(loop).Score);

						}
						else if (blue == 4){
							Blue4.setText(ScoreSort.get(loop).NameID+ "\t\t"+ ScoreSort.get(loop).Score);
							blue= 1;
							bluescore = bluescore + Integer.parseInt(ScoreSort.get(loop).Score);
						}
					}

					else if (ScoreSort.get(loop).Team.contains("Red")){
						if (red == 1){
							Red1.setText(ScoreSort.get(loop).NameID+ "\t\t"+ ScoreSort.get(loop).Score);
							Red2.setText("");
							Red3.setText("");
							Red4.setText("");
							red++;
							redscore = redscore + Integer.parseInt(ScoreSort.get(loop).Score);
						}
						else if (red == 2){
							Red2.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
							Red3.setText("");
							Red4.setText("");
							red++;
							redscore = redscore + Integer.parseInt(ScoreSort.get(loop).Score);
						}
						else if (red == 3){
							Red3.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
							Red4.setText("");
							red++;
							redscore = redscore + Integer.parseInt(ScoreSort.get(loop).Score);
						}
						else if (red == 4){
							Red4.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
							red=1;
							redscore = redscore + Integer.parseInt(ScoreSort.get(loop).Score);
						}
					}
				}

				//update scores here
				ScoreBlue.setText("Blue: "+bluescore);
				ScoreRed.setText("Red: "+redscore);
			}

		}
	}
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is no longer visible.
	 * Stop updating location and disconnect
	 */
	@Override
	protected void onStop() {
		// If the client is connected
		if (mLocationClient.isConnected()) {
			/*
			 * Remove location updates for a listener.
			 * The current Activity is the listener, so
			 * the argument is "this".
			 */

			// Unsure if this is correct
			mLocationClient.removeLocationUpdates(this);
		}
		/*
		 * After disconnect() is called, the client is
		 * considered "dead".
		 */

		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}
	/*
	 * Handle results returned to the FragmentActivity
	 * by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST :
			/*
			 * If the result code is Activity.RESULT_OK, try
			 * to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK :
				/*
				 * Try the request again
				 */

				//maybe???
				//servicesConnected();
				break;
			}
		}
	}
	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates",
					"Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			//int errorCode = connectionResult.getErrorCode();
			// Get the error dialog from Google Play services

			//	Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
			//		resultCode,
			//	this,
			//CONNECTION_FAILURE_RESOLUTION_REQUEST);



		}
		return false;
		//	}
	}
	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		mUpdatesRequested = true;
		//Toast.makeText(this, LocationUtils.getLatLng(this, currentLocation), Toast.LENGTH_SHORT).show();

		//if already requested, start periodic updates
		if (mUpdatesRequested) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
			Log.v("Location: onConnected()", LocationUtils.getLatLng(this, currentLocation));

		}


		// If Google Play Services is available
		if (servicesConnected()) {

			// Get the current location
			currentLocation = mLocationClient.getLastLocation();

			// Display the current location in the Log
			Log.v("Location: servicesConnected()", LocationUtils.getLatLng(this, currentLocation));

			if (currentLocation != null) {
				Log.v("Initial Location", "Entered If Statement");
				//	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),
				//		currentLocation.getLongitude()),14));
				Log.v("Initial Location", "Should Be On Current Location");
			}
		}
	}
	// ...
	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}
	// ...
	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			//	showErrorDialog(connectionResult.getErrorCode());
		}
	}

	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float degree = Math.round(event.values[0]);
		currentDegree = -degree;
		myImageView.setMyCords(myLat, myLon,teamx,teamy,enemy1x,enemy1y,enemy2x,enemy2y, currentDegree);

	//	myImageView.setMyCords(myLat, myLon, currentDegree);

	//	degrees.setText(""+currentDegree);


		
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		String msg = "Updated Location: " +
				Double.toString(location.getLatitude()) + "," +
				Double.toString(location.getLongitude());
		//Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		myLat = location.getLatitude();
		myLon = location.getLongitude();
		Log.v("Location: onLocationChanged()", msg);
		String lat = Double.toString(location.getLatitude());
		String lon = Double.toString(location.getLongitude());
	//	myImageView.setMyCords(myLat, myLon, currentDegree);
		Log.d("call updateCords","Mylat: "+myLat+" myLon: "+myLon);
		updateCords();
		//cords.setText(lat+"/"+lon);
		
	}
	public void updateCords(){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//	params.add(new BasicNameValuePair("pid","1"));
		//params.add(new BasicNameValuePair("player", "1"));
		params.add(new BasicNameValuePair("game_id", "game"+player.getGameID()));

		params.add(new BasicNameValuePair("player", ""+player.getPlayerSpot()));
		params.add(new BasicNameValuePair("myLat", ""+myLat));
		params.add(new BasicNameValuePair("myLon", ""+myLon));
		JSONObject json = jsonParser.makeHttpRequest(url_create_product,
				"POST", params);

		

		// check log cat fro response
		Log.d("Create Response", json.toString());

		// check for success tag
		try {
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {


				// closing this screen
			} else {
				// failed to create product
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	

}