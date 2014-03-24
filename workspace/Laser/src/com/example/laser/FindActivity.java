package com.example.laser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.laser.JoinActivity.LoadPlayers;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FindActivity extends ListActivity {
	Player player;
	// Progress Dialog
	private ProgressDialog pDialog;
Timer timer;
	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> productsList;

	// url to get all products list
	private static String url_all_products = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_all_products.php";
	private static String url_get_gameinfo = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_game_info.php";
	private static String url_update_gameinfo = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/update_game_info.php";
	private static String url_get_game = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_game.php";
	private static String url_update_game =  "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/update_game.php";
	//192.168.1.15
	//udel 128.4.201.0
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GAMES = "games";
	private static final String TAG_PID = "pid";
	private static final String TAG_SERVER_NAME = "server_name";
	private static final String TAG_MAX_PLAYERS = "max_players";
	private static final String TAG_CURRENT_PLAYERS = "current_players";
	private static final String TAG_GAME_MODE = "game_mode";
	private static final String TAG_GAME_INFO = "game_info";

	String Micheal;
	String currentplayers;
	String pickedpid;
	String myplayer;
	String player1;
	String player2;
	Boolean ContinueOn = true;//false;
	int left;
	String GameMode = "";

	// products JSONArray
	JSONArray products = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.find_game);

		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		callAsynchronousTask();
		// Get listview
		ListView lv = getListView();

		// on seleting single product
		// launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				pickedpid = ((TextView) view.findViewById(R.id.pid)).getText()
						.toString();

				if(checkiffull()){
					new getGameInfo().execute();
				}
			}

			private Boolean checkiffull() {
				String MaxPlayers = null;
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("game_id", pickedpid));

				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_get_game, "GET", params);


				// Check your log cat for JSON reponse
				Log.d("All Products: ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// products found
						// Getting Array of Products
						products = json.getJSONArray(TAG_GAMES);


						// looping through All Products

						JSONObject c = products.getJSONObject(0);

						// Storing each json item in variable
						currentplayers = c.getString("current_players");
						MaxPlayers = c.getString("max_players");
						GameMode = c.getString("game_mode");



						// Starting new intent


					}
					else {
						// failed to update product
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (Integer.valueOf(currentplayers)<Integer.valueOf(MaxPlayers)){
					UpdateCurrentPlayers();
					return true;
				}
				else{
					return false;
				}
				//Process char

			}

			private void UpdateCurrentPlayers() {

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("game_id", pickedpid));
				params.add(new BasicNameValuePair("current_players", ""+(Integer.valueOf(currentplayers)+1)));

				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_update_game, "POST", params);


				// Check your log cat for JSON reponse
				Log.d("All Products: ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// products found
						// Getting Array of Products
						products = json.getJSONArray(TAG_GAMES);


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
		});


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
							LoadAllProducts performBackgroundTask = new LoadAllProducts();
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			timer.cancel();
		
			//player.earseAll();

			Intent in = new Intent(getApplicationContext(),
					MainActivity.class);
			// starting new activity and expecting some response back
			startActivity(in);	    
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllProducts extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			pDialog = new ProgressDialog(FindActivity.this);
			pDialog.setMessage("Loading Games. Please wait...");
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
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);

			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					products = json.getJSONArray(TAG_GAMES);
					productsList.clear();

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						String id = c.getString(TAG_PID);
						String name = c.getString(TAG_SERVER_NAME);
						String maxPlayers = c.getString(TAG_MAX_PLAYERS);
						String current_players = c.getString(TAG_CURRENT_PLAYERS);
						String gameMode = c.getString(TAG_GAME_MODE);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						String gameInfo = (name+" "+current_players+"/"+maxPlayers+" "+gameMode); 
						// adding each child node to HashMap key => value
						map.put(TAG_PID, id);
						map.put(TAG_GAME_INFO, gameInfo);
						// map.put(TAG_SERVER_NAME, name);
						map.put(TAG_CURRENT_PLAYERS, current_players);
						map.put(TAG_MAX_PLAYERS, maxPlayers);
						map.put(TAG_GAME_MODE, gameMode);

						// adding HashList to ArrayList
						productsList.add(map);
					}
				} else {
					// no products found
					// Launch Add New product Activity
					//  Intent i = new Intent(getApplicationContext(),
					//            NewProductActivity.class);
					// Closing all previous activities
					//   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					//    startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			//pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							FindActivity.this, productsList,
							R.layout.list_item, new String[] { TAG_PID, TAG_GAME_INFO,
									TAG_SERVER_NAME, TAG_CURRENT_PLAYERS, TAG_MAX_PLAYERS, TAG_GAME_MODE},
									new int[] { R.id.pid, R.id.gameinfo});
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
	class getGameInfo extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FindActivity.this);
			pDialog.setMessage("Joining Game. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {


			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("game_id", pickedpid));

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
					int[] players;
					players = new int[8];

					// looping through All Products

					JSONObject c = products.getJSONObject(0);

					// Storing each json item in variable
					player1 = c.getString("player1");
					player2 = c.getString("player2");
					String player3 = c.getString("player3");
					String player4 = c.getString("player4");
					String player5 = c.getString("player5");
					String player6 = c.getString("player6");
					String player7 = c.getString("player7");
					String player8 = c.getString("player8");

					players[0]=Integer.valueOf(player1);
					players[1]=Integer.valueOf(player2);
					players[2]=Integer.valueOf(player3);
					players[3]=Integer.valueOf(player4);
					players[4]=Integer.valueOf(player5);
					players[5]=Integer.valueOf(player6);
					players[6]=Integer.valueOf(player7);
					players[7]=Integer.valueOf(player8);


					// Starting new intent


					myplayer= pickRandomPlayer(players);
					player = new Player(Integer.valueOf(myplayer),Integer.valueOf(pickedpid));

					if (GameMode.charAt(0) == 'T'){ //find out what team to be added too
						int blue = 0;
						int red = 0;
						//String[] redteam = new String[3];
						//String[] blueteam = new String[3];

						for (int i=1; i<9;i++){
							String temp = "player"+i+"Team";
							String temp1 = c.getString(temp);
							if(temp1.contains("Red")){
								//redteam[red] = "player" + i; //what players are on the red team
								red++;
							}
							if(temp1.contains("Blue")){
								//blueteam[blue] = "player" + i; // what players are on the blue team
								blue++;
								Micheal = "here";
							}
						}

						Micheal = c.getString("player1Team");
						if(blue < red){
							player.setTeam("Blue");
						}
						// if blue equals red, then pick a random color
						/*
						if(blue == red){

						}
						 */
						else{
							player.setTeam("Red");
						}
					}
					else{
						player.setTeam("Neutral");
					}
				}
				else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}
		String pickRandomPlayer(int[] players){
			int[] playersLeft;
			int[] randoms;
			left = 0;
			playersLeft = new int[8];

			for(int i = 1; i<=8; i++){
				playersLeft[i-1]=i;
			}
			for(int i =0; i<8;i++){
				for(int j =0; j<8;j++){
					if(playersLeft[i]==players[j] && playersLeft[i]!=0 && players[j] != 0){
						playersLeft[i]=0;
						left++;
					}
				}

			}
			randoms = new int[8-left];
			int count = 0;
			for( int j =0; j<8;j++){
				if(playersLeft[j] != 0){
					randoms[count]=playersLeft[j];
					count++;
				}
			}
			Random random = new Random();

			return  ""+randoms[random.nextInt(randoms.length)];





		}
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();

			new JoiningGame().execute();

		}

	}
	class JoiningGame extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(FindActivity.this);
			pDialog.setMessage("Joining Game. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			left = left +1;
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("game_id", pickedpid));
			params2.add(new BasicNameValuePair("player", myplayer));
			params2.add(new BasicNameValuePair("left",""+left));
			params2.add(new BasicNameValuePair("playerTeam",player.getTeam())); //add player to red or blue

			JSONObject json = jsonParser.makeHttpRequest(url_update_gameinfo,
					"POST", params2);


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

			return null;
		}




		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			player.setPlayerSpot(left);

			Intent in = new Intent(FindActivity.this,
					JoinActivity.class);
			// sending pid to next activity
			in.putExtra("player", player);
			in.putExtra("gamepid", pickedpid);
			in.putExtra("current_players", Micheal);
			in.putExtra("player1", ""+left);
			in.putExtra("gamemode", GameMode);
			timer.cancel();
			// starting new activity and expecting some response back
			startActivity(in);

		}

	}
}
