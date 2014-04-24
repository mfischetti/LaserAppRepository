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


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

public class GameActivity extends Activity {
	private static final String TAG = "bluetooth2";

	private static String url_update_shooter = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/update_shooter.php";
	private static String url_get_gamedata = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_ingame_info.php";

	JSONParser jParser = new JSONParser();
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> productsList;
	
	JSONArray products = null;
	JSONArray products1 = null;
	JSONArray products2 = null;
	JSONArray products3 = null;
	
	ArrayList<GamePlayer> AllPlayerInfo = new ArrayList<GamePlayer>();		
	TextView txtArduino, player_name;
	TextView Blue1, Blue2, Blue3, Blue4;
	TextView Red1, Red2, Red3, Red4;
	TextView BlueScore, RedScore;
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

	private ConnectedThread mConnectedThread;

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

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		//	btnOn = (Button) findViewById(R.id.btnOn);                  // button LED ON
		//btnOff = (Button) findViewById(R.id.btnOff);                // button LED OFF
		txtArduino = (TextView) findViewById(R.id.txtArduino);      // for display the received data from the Arduino
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
		BlueScore = (TextView) findViewById(R.id.BlueScore);
		RedScore = (TextView) findViewById(R.id.RedScore);
		callAsynchronousTask();
		h = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case RECIEVE_MESSAGE:                                                   // if receive massage
					byte[] readBuf = (byte[]) msg.obj;
					String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
					//	txtArduino.setText(strIncom);
					UpdateShooter(strIncom);
					//Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
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
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
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
		timer.schedule(doAsynchronousTask, 0, 2000); //execute in every 5000 ms
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
				int success = json.getInt(TAG_SUCCESS);
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
			int red = 1;
			//int loop = 0;
			for (int loop = 0; loop <4;loop++){
				if (ScoreSort.get(loop).Team.contains("Blue")){
					if (blue == 1){
						Blue1.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
						Blue2.setText("");
						Blue3.setText("");
						Blue4.setText("");
						blue++;
					}
					else if (blue == 2){
						Blue2.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
						Blue3.setText("");
						Blue4.setText("");
						blue++;
					}
					else if (blue == 3){
						Blue3.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
						Blue4.setText("");
						blue++;
					}
					else if (blue == 4){
						Blue4.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
						blue= 1;
					}
				}

				else{
					if (red == 1){
						Red1.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
						Red2.setText("");
						Red3.setText("");
						Red4.setText("");
						red++;
					}
					else if (red == 2){
						Red2.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
						Red3.setText("");
						Red4.setText("");
						red++;
					}
					else if (red == 3){
						Red3.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
						Red4.setText("");
						red++;
					}
					else if (red == 4){
						Red4.setText(ScoreSort.get(loop).NameID+ "\t"+ ScoreSort.get(loop).Score);
						red=1;
					}
				}
			}


		}
	}
}