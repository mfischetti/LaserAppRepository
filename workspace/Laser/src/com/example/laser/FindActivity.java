package com.example.laser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FindActivity extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> productsList;

	// url to get all products list
	private static String url_all_products = "http://10.0.0.16/laserDatabase/android_connect/get_all_products.php";
	private static String url_get_gameinfo = "http://10.0.0.16/laserDatabase/android_connect/get_game_info.php";

	//192.168.1.15
	//udel 128.4.222.193
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GAMES = "games";
	private static final String TAG_PID = "pid";
	private static final String TAG_SERVER_NAME = "server_name";
	private static final String TAG_MAX_PLAYERS = "max_players";
	private static final String TAG_CURRENT_PLAYERS = "current_players";
	private static final String TAG_GAME_MODE = "game_mode";
	private static final String TAG_GAME_INFO = "game_info";
	String currentplayers;
	String pickedpid;
	String player1;
	String player2;
	Boolean ContinueOn = true;//false;

	// products JSONArray
	JSONArray products = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_game);

		// Hashmap for ListView
		productsList = new ArrayList<HashMap<String, String>>();

		// Loading products in Background Thread
		new LoadAllProducts().execute();

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
				currentplayers = ((TextView) view.findViewById(R.id.gameinfo)).getText()
						.toString();
				if(checkiffull(currentplayers)){
					new getGameInfo().execute();
				}
			}

			private Boolean checkiffull(String s) {
				char tempcurrent = 0;
				char tempmax = 0;
				for (int i = 0; i < s.length(); i++){
					char c = s.charAt(i);
					if (c == '/'){
						tempcurrent = s.charAt(i-1);
						tempmax = s.charAt(i+1);
						break;
					}

					//Process char
				}

				if (Character.getNumericValue(tempcurrent) < Character.getNumericValue(tempmax)){
					currentplayers =  Character.toString(tempcurrent);
					return true;
				}
				else{
					return false;
				}
			}
		});

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
			pDialog = new ProgressDialog(FindActivity.this);
			pDialog.setMessage("Loading Games. Please wait...");
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
			pDialog.dismiss();
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

					// looping through All Products
					for (int i = 0; i < products.length(); i++) {
						JSONObject c = products.getJSONObject(i);

						// Storing each json item in variable
						player1 = c.getString("player1");
						player2 = c.getString("player2");
						//String player3 = c.getString("player3");
						//String player4 = c.getString("player4");
					}
					// Starting new intent
					Intent in = new Intent(getApplicationContext(),
							JoinActivity.class);
					// sending pid to next activity
					in.putExtra("gamepid", pickedpid);
					in.putExtra("current_players", currentplayers);
					in.putExtra("player1", player1);


					// starting new activity and expecting some response back
					startActivity(in);
				}
				 else {
					// failed to update product
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
			
			/*
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("game_id", pickedpid));
			params.add(new BasicNameValuePair("player", number));

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_product,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			*/
			return null;
			
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
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
}
