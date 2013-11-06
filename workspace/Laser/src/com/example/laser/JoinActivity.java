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
	private static final String url_update_player = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/add_player.php";
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





	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);
		Bundle bundle = getIntent().getExtras();
		pid = bundle.getString("gamepid");
		currentPlayers = bundle.getString("current_players");
		String player = bundle.getString("player1");
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
		//players.setText(Allplayers);
		//new LoadPlayers().execute();



		callAsynchronousTask();
		// Getting complete product details in background thread
		//new UpdateGameInfo().execute();
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
					play1 = getName(c.getString("player1"));
					play2 = getName(c.getString("player2"));
					play3 = getName(c.getString("player3"));
					play4 = getName(c.getString("player4"));
					play5 = getName(c.getString("player5"));
					play6 = getName(c.getString("player6"));
					play7 = getName(c.getString("player7"));
					play8 = getName(c.getString("player8"));
					color1 = c.getString("player1Team");
					color2 = c.getString("player2Team");
					color3 = c.getString("player3Team");
					color4 = c.getString("player4Team");
					color5 = c.getString("player5Team");
					color6 = c.getString("player6Team");
					color7 = c.getString("player7Team");
					color8 = c.getString("player8Team");

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
			if (GameMode.charAt(0) == 'F'){
				player1.setText(play1);
				player2.setText(play2);
				player3.setText(play3);
				player4.setText(play4);
				player5.setText(play5);
				player6.setText(play6);
				player7.setText(play7);
				player8.setText(play8);
			}
			if (GameMode.charAt(0) == 'T'){
				team11.setText(play1);
			}
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			//pDialog.dismiss();

		}

	}

	/**
	 * Background Async Task to  Save product Details
	 * */
	class UpdateGameInfo extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(JoinActivity.this);
			pDialog.setMessage("Update Info");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_PID, pid));
			params.add(new BasicNameValuePair(TAG_CURRENT_PLAYERS, currentPlayers));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_player,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				/*
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
				}
				 */
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
		}
	}


}
