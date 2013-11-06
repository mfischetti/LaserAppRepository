package com.example.laser;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.laser.HostServerActivity.CreateNewGame;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class HostActivity extends Activity implements OnClickListener {

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static String url_create_product = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/create_product.php";
	private static String url_all_products = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_all_products.php";
	private static String url_post_gameinfo = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/create_game_info.php";

	//10.0.0.16
	//udel - 128.4.202.239

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GAMES = "games";
	private static final String TAG_PID = "pid";
	private static final String TAG_SERVER_NAME = "server_name";
	private static final String TAG_MAX_PLAYERS = "max_players";
	private static final String TAG_CURRENT_PLAYERS = "current_players";
	private static final String TAG_GAME_MODE = "game_mode";
	private static final String TAG_GAME_INFO = "game_info";
	JSONParser jParser = new JSONParser();


	// products JSONArray
	JSONArray products = null;

	EditText HostName;
	EditText PasswordName;
	Spinner Items;
	TextView test;
	Button start;
	TextView servername;
	TextView playername;
	TextView passwordtext;
	RadioGroup mode;
	RadioButton tempRadio;
	public String GameInfo;
	public String game_name;
	public String password;
	public String players;
	public String game_mode;
	public String current_players;

	public String gamepid = "test";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host);
		setupWidget();
		addItemsOnSpinner();
		//test sync

	}
	public void setupWidget() {
		servername = (TextView)findViewById(R.id.servername);
		servername.setTextColor(Color.WHITE);
		playername = (TextView)findViewById(R.id.playersname);
		playername.setTextColor(Color.WHITE);
		passwordtext = (TextView)findViewById(R.id.passwordname);
		passwordtext.setTextColor(Color.WHITE);
		mode = (RadioGroup)findViewById(R.id.radioGroup1);

		HostName = (EditText)findViewById(R.id.name1);
		HostName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});	//sets max characters length
		PasswordName = (EditText)findViewById(R.id.password1);
		PasswordName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});	//sets max characters length
		//test = (TextView)findViewById(R.id.text1);
		TextView title = (TextView)findViewById(R.id.findtitle);
		title.setTextColor(Color.RED);

		start = (Button)findViewById(R.id.buttonhost);
		start.setOnClickListener(this);
	}
	public void addItemsOnSpinner() {
		List<String> SpinnerArray =  new ArrayList<String>();
		SpinnerArray.add("2");
		SpinnerArray.add("4");
		SpinnerArray.add("8");
		SpinnerArray.add("16");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SpinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Items = (Spinner) findViewById(R.id.players);
		Items.setAdapter(adapter);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host, menu);
		return true;
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.buttonhost){
			GetRadioSelected();
			//test
			game_name = HostName.getText().toString();
			password = PasswordName.getText().toString();
			players = Items.getSelectedItem().toString();
			game_mode = tempRadio.getText().toString();
			current_players = "1";
			/*GameInfo = HostName.getText().toString()+ "~"
					+ PasswordName.getText().toString()+"~"+Items.getSelectedItem().toString()+
					"~"+tempRadio.getText().toString();
			Editable texttest = HostName.getText();
			Intent next = new Intent(this, HostServerActivity.class);	
			next.putExtra("Game Name", game_name.toString());
			next.putExtra("Password",password.toString()); 
			next.putExtra("Players", players); 
			next.putExtra("Game Mode", game_mode);
			startActivity(next);*/
			new CreateNewGame().execute();



			// updating UI from Background Thread
			//servername.setText(gamepid);


		}
	}
	public void GetRadioSelected() {
		int selectedId = mode.getCheckedRadioButtonId();
		tempRadio = (RadioButton) findViewById(selectedId);
	}
	class CreateNewGame extends AsyncTask<String, String, String>{

		protected void onPreExecute(){

			super.onPreExecute();
			pDialog = new ProgressDialog(HostActivity.this);
			pDialog.setMessage("Creating Game..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... arg0) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//	params.add(new BasicNameValuePair("pid","1"));
			params.add(new BasicNameValuePair("server_name", game_name));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("current_players", current_players));
			params.add(new BasicNameValuePair("max_players", players));


			params.add(new BasicNameValuePair("game_mode", game_mode));

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

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			new LoadTheGame().execute();

		}



	}
	class LoadTheGame extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(HostActivity.this);
			pDialog.setMessage("Loading Game. Please wait...");
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
						gamepid = id;
						/*
						if (game_name == name && players == maxPlayers && game_mode == gameMode){
						 */

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
			// Starting new intent
			new CreateGameInfo().execute();

		}

	}
	class CreateGameInfo extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(HostActivity.this);
			pDialog.setMessage("Loading Game. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... arg0) {
			String playernumber = ""+(1 + (Math.random() * 8));
			String color;
			if (game_mode.charAt(0) == 'T'){
				color = pickcolor();
			}
			else{
				color = "Neutral";
			}
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("game_id", gamepid));
			params2.add(new BasicNameValuePair("player1", playernumber));
			params2.add(new BasicNameValuePair("playerTeam", color));

			JSONObject json = jsonParser.makeHttpRequest(url_post_gameinfo,
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

		private String pickcolor() {
			int num = (int)(Math.random() * 2);
			if (num == 0){
				return "Blue";
			}
			else{
				return "Red";
			}
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// Starting new intent
			Intent in = new Intent(getApplicationContext(),
					JoinActivity.class);
			// sending pid to next activity
			in.putExtra("gamepid", gamepid);
			in.putExtra("current_players", current_players);
			in.putExtra("gamemode",game_mode);
			// starting new activity and expecting some response back
			startActivity(in);

		}

	}



}
