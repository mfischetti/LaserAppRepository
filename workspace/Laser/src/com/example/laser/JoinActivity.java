package com.example.laser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.laser.FindActivity.JoiningGame;
import com.example.laser.HostActivity.CreateGameInfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class JoinActivity extends Activity {

	TextView playerslist;
	EditText txtPrice;
	EditText txtDesc;
	EditText txtCreatedAt;
	Button btnSave;
	Button btnDelete;

	String pid;
	String currentPlayers;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jParser = new JSONParser();
	JSONParser jsonParser = new JSONParser();
	JSONArray products = null;


	// single product url
	private static String url_get_gameinfo = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_game_info.php";
	// url to update product
	private static String url_update_game =  "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/update_game.php";
	private static String url_update_player = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/add_player.php";
	private static String url_get_game = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_game.php";

	//10.0.0.16
	// url to delete product


	// JSON Node names
	private static final String TAG_GAME_INFO = "game_info";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GAMES = "gameinfo";
	private static final String TAG_PID = "game_id";
	private static final String TAG_CURRENT_PLAYERS = "current_players";
	private static String play1 = "";
	private static String play2 = "";
	private static String play3 = "";
	private static String play4 = "";
	private static String play5 = "";
	private static String play6 = "";
	private static String play7 = "";
	private static String play8 = "";
	private static String color1 = "";
	private static String color2 = "";
	private static String color3 = "";
	private static String color4 = "";
	private static String color5 = "";
	private static String color6 = "";
	private static String color7 = "";
	private static String color8 = "";
	String tempCurrentPlayers = "";

	boolean temp = true;
	TextView JoinTitle;
	TextView player1;
	TextView player2;
	TextView player3;
	TextView player4;
	TextView player5;
	TextView player6;
	TextView player7;
	TextView player8;
	TextView team11;
	TextView team12;
	TextView team13;
	TextView team14;
	TextView team21;
	TextView team22;
	TextView team23;
	TextView team24;

	String GameMode = "";

	Player player;
	ArrayList<String> AllColors = new ArrayList<String>();		
	ArrayList<String> AllPlayers = new ArrayList<String>();
	ArrayList<String> AllPlayersTemp = new ArrayList<String>();		





	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);
		Bundle bundle = getIntent().getExtras();

		// 2. get person object from intent
		player = (Player) getIntent().getSerializableExtra("player");




		pid = bundle.getString("gamepid");
		currentPlayers = bundle.getString("current_players");
		//String player = bundle.getString("player1");
		GameMode = bundle.getString("gamemode");
		JoinTitle = (TextView)findViewById(R.id.JoinTitle);
		JoinTitle.setText(GameMode);
		JoinTitle.setTextColor(Color.RED);
		player1 = (TextView)findViewById(R.id.TextView001);
		player1.setTextColor(Color.RED);
		player2 = (TextView)findViewById(R.id.TextView002);
		player2.setTextColor(Color.BLUE);
		player3 = (TextView)findViewById(R.id.TextView003);
		player3.setTextColor(Color.WHITE);
		player4 = (TextView)findViewById(R.id.TextView004);
		player4.setTextColor(Color.GREEN);
		player5 = (TextView)findViewById(R.id.TextView005);
		player5.setTextColor(Color.YELLOW);
		player6 = (TextView)findViewById(R.id.TextView006);
		player6.setTextColor(Color.MAGENTA);
		player7 = (TextView)findViewById(R.id.TextView007);
		player7.setTextColor(Color.CYAN);
		player8 = (TextView)findViewById(R.id.TextView008);
		player8.setTextColor(Color.GRAY);

		team11 = (TextView)findViewById(R.id.team11);
		team11.setTextColor(Color.BLUE);
		team12 = (TextView)findViewById(R.id.team12);
		team12.setTextColor(Color.BLUE);
		team13 = (TextView)findViewById(R.id.team13);
		team13.setTextColor(Color.BLUE);
		team14 = (TextView)findViewById(R.id.team14);
		team14.setTextColor(Color.BLUE);
		team21 = (TextView)findViewById(R.id.team21);
		team21.setTextColor(Color.RED);
		team22 = (TextView)findViewById(R.id.team22);
		team22.setTextColor(Color.RED);
		team23 = (TextView)findViewById(R.id.team23);
		team23.setTextColor(Color.RED);
		team24 = (TextView)findViewById(R.id.team24);
		team24.setTextColor(Color.RED);
		//this is how you get it so it doesnt crash
		/*
		TextView test = (TextView)findViewById(R.id.testView);
		test.setText(player.getPlayerID()+"");
		*/
		//players.setText(Allplayers);
		//new LoadPlayers().execute();



		callAsynchronousTask();
		// Getting complete product details in background thread
		//new LoadPlayers().execute();

	}



	public void callAsynchronousTask() {
		final Handler handler = new Handler();
		Timer timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {       
						try {
							LoadPlayers performBackgroundTask = new LoadPlayers();
							// PerformBackgroundTask this class is the class that extends AsynchTask 
							performBackgroundTask.execute();
						} catch (Exception e) {
							// TODO Auto-generated catch block
						}
					}
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 1000 ms
	}


	/**
	 * Background Async Task to Get complete product details
	 * */

	class LoadPlayers extends AsyncTask<String, String, String> {

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
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {


			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("game_id", pid));

			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_get_gameinfo, "GET", params);


			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_GAME_INFO);


					// looping through All Products

					JSONObject c = products.getJSONObject(0);

					// Storing each json item in variable
					AllPlayersTemp.add(getName(c.getString("player1")));
					AllPlayersTemp.add(getName(c.getString("player2")));
					AllPlayersTemp.add(getName(c.getString("player3")));
					AllPlayersTemp.add(getName(c.getString("player4")));
					AllPlayersTemp.add(getName(c.getString("player5")));
					AllPlayersTemp.add(getName(c.getString("player6")));
					AllPlayersTemp.add(getName(c.getString("player7")));
					AllPlayersTemp.add(getName(c.getString("player8")));
					AllColors.add(c.getString("player1Team"));
					AllColors.add(c.getString("player2Team"));
					AllColors.add(c.getString("player3Team"));
					AllColors.add(c.getString("player4Team"));
					AllColors.add(c.getString("player5Team"));
					AllColors.add(c.getString("player6Team"));
					AllColors.add(c.getString("player7Team"));
					AllColors.add(c.getString("player8Team"));

					// Starting new intent

				}
				else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			publishProgress(args);

			return null;
		}


		private String getName(String number) {
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
				return "DaveRamos";
			}
			if (number.charAt(0)=='8'){
				return "Jimmer";
			}
			else{
				return "";
			}
		}

		protected void onProgressUpdate(String... values) {
			if(AllPlayers != AllPlayersTemp){
				AllPlayers = AllPlayersTemp;
				if (GameMode.charAt(0) == 'F'){
					player1.setText(AllPlayers.get(0));
					player2.setText(AllPlayers.get(1));
					player3.setText(AllPlayers.get(2));
					player4.setText(AllPlayers.get(3));
					player5.setText(AllPlayers.get(4));
					player6.setText(AllPlayers.get(5));
					player7.setText(AllPlayers.get(6));
					player8.setText(AllPlayers.get(7));
				}
				if (GameMode.charAt(0) == 'T'){
					setDisplay();
				}
			}
		}



		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			//pDialog.dismiss();

		}
		private void setDisplay() {
			int[] red = new int[4];	
			red[0] = 9; red[1] = 9; red[2] = 9; red[3] = 9;
			int[] blue = new int[4];
			blue[0] = 9; blue[1] = 9; blue[2] = 9; blue[3] = 9;
			int redcounter = 0;
			int bluecounter = 0;
			for (int i = 0; i < 8; i++){
				if(AllColors.get(i).contains("Red")){
					red[redcounter] = i;
					redcounter++;
				}
				if(AllColors.get(i).contains("Blue")){
					blue[bluecounter] = i;
					bluecounter++;
				}
			}
			//SET BLUE TEAM DISPLAY
			if (blue[0] != 9){
				team11.setText(AllPlayers.get(blue[0]));
			}
			if (blue[1] != 9){
				team12.setText(AllPlayers.get(blue[1]));
			}
			if (blue[2] != 9){
				team13.setText(AllPlayers.get(blue[2]));
			}
			if (blue[3] != 9){
				team14.setText(AllPlayers.get(blue[3]));

			}
			//SET RED TEAM DISPLAY
			if (red[0] != 9){
				team21.setText(AllPlayers.get(red[0]));
			}
			if (red[1] != 9){
				team22.setText(AllPlayers.get(red[1]));
			}
			if (red[2] != 9){
				team23.setText(AllPlayers.get(red[2]));
			}
			if (red[3] != 9){
				team24.setText(AllPlayers.get(red[3]));
			}

		}
	}
	//	@Override
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	//			removePlayer();
	//			/*
	//			Intent in = new Intent(getApplicationContext(),
	//					MainActivity.class);
	//			// starting new activity and expecting some response back
	//			startActivity(in);	    
	//			*/}
	//		return super.onKeyDown(keyCode, event);
	//	}
	//	/**
	//	 * Background Async Task to  Save product Details
	//	 * */
	//	private void removePlayer() {	//first get the current players
	//		String MaxPlayers = null;
	//		List<NameValuePair> params = new ArrayList<NameValuePair>();
	//		params.add(new BasicNameValuePair("game_id", pid));
	//
	//		// getting JSON string from URL
	//		JSONObject json = jParser.makeHttpRequest(url_get_game, "GET", params);
	//
	//
	//		// Check your log cat for JSON reponse
	//		Log.d("All Products: ", json.toString());
	//
	//		try {
	//			// Checking for SUCCESS TAG
	//			int success = json.getInt(TAG_SUCCESS);
	//
	//			if (success == 1) {
	//				// products found
	//				// Getting Array of Products
	//				products = json.getJSONArray(TAG_GAMES);
	//
	//
	//				// looping through All Products
	//
	//				JSONObject c = products.getJSONObject(0);
	//
	//				// Storing each json item in variable
	//				tempCurrentPlayers = c.getString("current_players");
	//
	//
	//
	//				// Starting new intent
	//
	//
	//			}
	//			else {
	//				// failed to update product
	//			}
	//		} catch (JSONException e) {
	//			e.printStackTrace();
	//		}
	//
	//		new UpdateGameInfo().execute();
	//		//Process char
	//
	//	}
	//	class UpdateGameInfo extends AsyncTask<String, String, String> {	//updates current players to current players - 1
	//
	//		/**
	//		 * Before starting background thread Show Progress Dialog
	//		 * */
	//		@Override
	//		protected void onPreExecute() {
	//			super.onPreExecute();
	//			pDialog = new ProgressDialog(JoinActivity.this);
	//			pDialog.setMessage("Leaving Game");
	//			pDialog.setIndeterminate(false);
	//			pDialog.setCancelable(true);
	//			pDialog.show();
	//		}
	//
	//		/**
	//		 * Saving product
	//		 * */
	//		protected String doInBackground(String... args) {
	//
	//			// getting updated data from EditTexts
	//
	//			// Building Parameters
	//			
	//			List<NameValuePair> params = new ArrayList<NameValuePair>();
	//			params.add(new BasicNameValuePair("game_id", pid));
	//			params.add(new BasicNameValuePair("current_players", ""+(Integer.valueOf(tempCurrentPlayers)-1)));
	//
	//			// getting JSON string from URL
	//			JSONObject json = jParser.makeHttpRequest(url_update_game, "POST", params);
	//
	//
	//			// Check your log cat for JSON reponse
	//			Log.d("All Products: ", json.toString());
	//
	//			try {
	//				// Checking for SUCCESS TAG
	//				int success = json.getInt(TAG_SUCCESS);
	//
	//				if (success == 1) {
	//					// products found
	//					// Getting Array of Products
	//					products = json.getJSONArray(TAG_GAMES);
	//
	//
	//					// looping through All Products
	//
	//					JSONObject c = products.getJSONObject(0);
	//
	//
	//
	//
	//
	//					// Starting new intent
	//
	//
	//				}
	//				else {
	//					// failed to update product
	//				}
	//			} catch (JSONException e) {
	//				e.printStackTrace();
	//			}
	//
	//
	//
	//			return null;
	//		}
	//
	//
	//		/**
	//		 * After completing background task Dismiss the progress dialog
	//		 * **/
	//		protected void onPostExecute(String file_url) {
	//			// dismiss the dialog once product uupdated
	//			pDialog.dismiss();
	//		}
	//	}
	/* still need to update all other players by sliding them over
	 * 	along with removing all player object info the player holds */

}
