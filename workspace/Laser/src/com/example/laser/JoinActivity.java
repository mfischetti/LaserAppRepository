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
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JoinActivity extends Activity implements OnClickListener {

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
	private static String url_update_delete_gameinfo = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/delete_update_game_info.php";
	private static String url_start_game = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/create_started_game.php";
	private static String url_erase_game = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/erase_game.php";
	private static String url_update_ready = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/update_ready_gameinfo.php";
	private static String url_update_time = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/update_gameinfo_time.php";
	// url to delete product


	// JSON Node names
	private static final String TAG_GAME_INFO = "game_info";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GAMES = "games";
	private static final String TAG_PID = "game_id";
	private static final String TAG_CURRENT_PLAYERS = "current_players";
	String tempCurrentPlayers = "";
	String MaxPlayers = "";
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
	TextView test;
	String GameMode = "";
	int newCurrentPlayers;
	Button start;
	Player player;
	ArrayList<String> AllColors = new ArrayList<String>();		
	ArrayList<String> AllPlayers = new ArrayList<String>();
	ArrayList<String> AllReady = new ArrayList<String>();		

	Timer timer;
	Drawable back;
	Resources res;

	long time;


	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		res = getResources();
		setContentView(R.layout.activity_join);
		Bundle bundle = getIntent().getExtras();

		// 2. get person object from intent
		player = (Player) getIntent().getSerializableExtra("player");



		start = (Button)findViewById(R.id.startButton);
		start.setOnClickListener(this);
		pid = bundle.getString("gamepid");
		currentPlayers = bundle.getString("current_players");
		//String player = bundle.getString("player1");
		GameMode = bundle.getString("gamemode");
		JoinTitle = (TextView)findViewById(R.id.JoinTitle);
		//JoinTitle.setText(GameMode);
		//JoinTitle.setTextColor(Color.RED);
		player1 = (TextView)findViewById(R.id.TextView001);
		player1.setTextColor(Color.RED);
		player2 = (TextView)findViewById(R.id.TextView002);
		player2.setTextColor(Color.BLUE);
		player3 = (TextView)findViewById(R.id.TextView003);
		player3.setTextColor(Color.WHITE);
		player4 = (TextView)findViewById(R.id.TextView004);
		player4.setTextColor(Color.GREEN);
		player5 = (TextView)findViewById(R.id.TextView005);
		player5.setTextColor(Color.DKGRAY);
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
		player.setName(getName(player.getPlayerID()+""));
		GetCurrentPlayers();
		View view = this.findViewById(R.id.joinLayout);

		if(GameMode.charAt(0) == 'F'){
			//back = res.getDrawable(R.drawable.freeforallback); 
			view.setBackgroundResource(R.drawable.freeforallback);
		}
		else{
			//back = res.getDrawable(R.drawable.joinggamebackground); 
			view.setBackgroundResource(R.drawable.joinggamebackground);
			JoinTitle.setText("Team");
			JoinTitle.setTextSize(50);
			JoinTitle.setTextColor(Color.WHITE);
		}








		callAsynchronousTask();
		// Getting complete product details in background thread
		//new LoadPlayers().execute();

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


	public void callAsynchronousTask() {
		final Handler handler = new Handler();
		timer = new Timer();
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
		timer.schedule(doAsynchronousTask, 0, 2000); //execute in every 5000 ms
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

			AllPlayers.clear();
			AllColors.clear();
			AllReady.clear();
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
					AllPlayers.add(c.getString("player1"));
					AllPlayers.add(c.getString("player2"));
					AllPlayers.add(c.getString("player3"));
					AllPlayers.add(c.getString("player4"));
					AllPlayers.add(c.getString("player5"));
					AllPlayers.add(c.getString("player6"));
					AllPlayers.add(c.getString("player7"));
					AllPlayers.add(c.getString("player8"));
					AllColors.add(c.getString("player1Team"));
					AllColors.add(c.getString("player2Team"));
					AllColors.add(c.getString("player3Team"));
					AllColors.add(c.getString("player4Team"));
					AllColors.add(c.getString("player5Team"));
					AllColors.add(c.getString("player6Team"));
					AllColors.add(c.getString("player7Team"));
					AllColors.add(c.getString("player8Team"));
					AllReady.add(c.getString("player1Ready"));
					AllReady.add(c.getString("player2Ready"));
					AllReady.add(c.getString("player3Ready"));
					AllReady.add(c.getString("player4Ready"));
					AllReady.add(c.getString("player5Ready"));
					AllReady.add(c.getString("player6Ready"));
					AllReady.add(c.getString("player7Ready"));
					AllReady.add(c.getString("player8Ready"));
					time =  Long.valueOf(c.getString("time"));
					//updates the player spot if a player quits

					if (Integer.parseInt(AllPlayers.get((player.getPlayerSpot()-1))) != player.getPlayerID()){
						for(int i=0; i<AllPlayers.size(); i++){
							if (Integer.parseInt(AllPlayers.get(i)) == player.getPlayerID()){
								player.setPlayerSpot(i+1);
							}
						}
					}
					if (time != 0){
						timer.cancel();

						/*
						int countdown = (int) (time - System.currentTimeMillis());
						countdown = countdown - 3000;
						try {
							Thread.sleep(countdown);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						 */
						Intent in = new Intent(JoinActivity.this,
								GameActivity.class);
						in.putExtra("player", player );
						// sending pid to next activity


						// starting new activity and expecting some response back
						startActivity(in);

					}

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

			if (GameMode.charAt(0) == 'F'){
				player1.setText(getName(AllPlayers.get(0)));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(0))){
					player1.setBackgroundColor(Color.YELLOW);
				}
				player2.setText(getName(AllPlayers.get(1)));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(1))){
					player2.setBackgroundColor(Color.YELLOW);
				}
				player3.setText(getName(AllPlayers.get(2)));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(2))){
					player3.setBackgroundColor(Color.YELLOW);
				}
				player4.setText(getName(AllPlayers.get(3)));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(3))){
					player4.setBackgroundColor(Color.YELLOW);
				}
				player5.setText(getName(AllPlayers.get(4)));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(4))){
					player5.setBackgroundColor(Color.YELLOW);
				}
				player6.setText(getName(AllPlayers.get(5)));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(5))){
					player6.setBackgroundColor(Color.YELLOW);
				}
				player7.setText(getName(AllPlayers.get(6)));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(6))){
					player7.setBackgroundColor(Color.YELLOW);
				}
				player8.setText(getName(AllPlayers.get(7)));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(7))){
					player8.setBackgroundColor(Color.YELLOW);
				}
			}
			if (GameMode.charAt(0) == 'T'){
				if (!AllPlayers.isEmpty()&&!AllColors.isEmpty()&&!AllReady.isEmpty()){
					setDisplay();
				}
			}
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
			//SET TEAM MEMBERS
			if (player.getTeam().contains("Blue")){
				int[] members = new int[4];	
				members[0] = 0; members[1] = 0; members[2] = 0; members[3] = 0;
				for (int count = 0; count < 4; count ++){
					if  (blue[count] != 9){
						members[count] = blue[count]+1;
					}

				}
				Log.d("Recieved", "team members are: "+members);
				player.setTeamMembers(members);
			}
			else if (player.getTeam().contains("Red")){
				int[] members = new int[4];	
				members[0] = 0; members[1] = 0; members[2] = 0; members[3] = 0;
				for (int count = 0; count < 4; count ++){
					if  (red[count] != 9){
						members[count] = red[count]+1;
					}

				}
				Log.d("Recieved", "team members are: "+members);
				player.setTeamMembers(members);
			}
			//SET BLUE TEAM DISPLAY
			if (blue[0] != 9){
				team11.setText(getName(AllPlayers.get(blue[0])));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(blue[0]))){
					team11.setBackgroundColor(Color.YELLOW);
				}

			}
			else{
				team11.setText("");
			}
			if (blue[1] != 9){
				team12.setText(getName(AllPlayers.get(blue[1])));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(blue[1]))){
					team12.setBackgroundColor(Color.YELLOW);
				}
			}
			else{
				team12.setText("");
			}
			if (blue[2] != 9){
				team13.setText(getName(AllPlayers.get(blue[2])));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(blue[2]))){
					team13.setBackgroundColor(Color.YELLOW);
				}
			}
			else{
				team13.setText("");
			}
			if (blue[3] != 9){
				team14.setText(getName(AllPlayers.get(blue[3])));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(blue[3]))){
					team14.setBackgroundColor(Color.YELLOW);
				}

			}
			else{
				team14.setText("");
			}
			//SET RED TEAM DISPLAY
			if (red[0] != 9){
				team21.setText(getName(AllPlayers.get(red[0])));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(red[0]))){
					team21.setBackgroundColor(Color.YELLOW);
				}
			}
			else{
				team21.setText("");
			}
			if (red[1] != 9){
				team22.setText(getName(AllPlayers.get(red[1])));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(red[1]))){
					team22.setBackgroundColor(Color.YELLOW);
				}
			}
			else{
				team22.setText("");
			}
			if (red[2] != 9){
				team23.setText(getName(AllPlayers.get(red[2])));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(red[2]))){
					team23.setBackgroundColor(Color.YELLOW);
				}
			}
			else{
				team23.setText("");
			}
			if (red[3] != 9){
				team24.setText(getName(AllPlayers.get(red[3])));
				if(player.getPlayerID() == Integer.parseInt(AllPlayers.get(red[3]))){
					team24.setBackgroundColor(Color.YELLOW);
				}
			}
			else{
				team24.setText("");
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			timer.cancel();
			GetCurrentPlayers();
			UpdateCurrentPlayers();
			updateGameInfo();
			if (newCurrentPlayers == 0){
				EraseGame();
			}
			//player.earseAll();

			Intent in = new Intent(getApplicationContext(),
					MainActivity.class);
			// starting new activity and expecting some response back
			startActivity(in);	    
		}
		return super.onKeyDown(keyCode, event);
	}

	private void EraseGame() {	//first get the current players
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("game_id", pid));

		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_erase_game, "POST", params);


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
				tempCurrentPlayers = c.getString("current_players");




				// Starting new intent


			}
			else {
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//Process char

	}

	private void GetCurrentPlayers() {	//first get the current players
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("game_id", pid));

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
				tempCurrentPlayers = c.getString("current_players");
				MaxPlayers= c.getString("max_players");



				// Starting new intent


			}
			else {
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//Process char

	}
	private void UpdateCurrentPlayers(){
		// Building Parameters
		//int temp = Integer.parseInt(tempCurrentPlayers);
		newCurrentPlayers = Integer.valueOf(tempCurrentPlayers)-1;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("game_id", pid));
		params.add(new BasicNameValuePair("current_players", (Integer.valueOf(tempCurrentPlayers)-1)+""));

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
	public void updateGameInfo(){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		AllPlayers.remove((player.getPlayerSpot()-1));
		AllColors.remove((player.getPlayerSpot()-1));
		AllReady.remove((player.getPlayerSpot()-1));
		params.add(new BasicNameValuePair("game_id", pid));

		if (AllPlayers.size()>= 1){
			params.add(new BasicNameValuePair("player1", AllPlayers.get(0).toString()));
			params.add(new BasicNameValuePair("playerTeam1", AllColors.get(0).toString()));
			params.add(new BasicNameValuePair("player1Ready", AllReady.get(0).toString()));

		}
		else{
			params.add(new BasicNameValuePair("player1", "0"));
			params.add(new BasicNameValuePair("playerTeam1", ""));
			params.add(new BasicNameValuePair("player1Ready", ""));
		}
		if (AllPlayers.size()>= 2){
			params.add(new BasicNameValuePair("player2", AllPlayers.get(1).toString()));
			params.add(new BasicNameValuePair("playerTeam2", AllColors.get(1).toString()));
			params.add(new BasicNameValuePair("player2Ready", AllReady.get(1).toString()));


		}
		else{
			params.add(new BasicNameValuePair("player2", "0"));
			params.add(new BasicNameValuePair("playerTeam2", ""));
			params.add(new BasicNameValuePair("player2Ready", ""));


		}
		if (AllPlayers.size()>= 3){
			params.add(new BasicNameValuePair("player3", AllPlayers.get(2).toString()));
			params.add(new BasicNameValuePair("playerTeam3", AllColors.get(2).toString()));
			params.add(new BasicNameValuePair("player3Ready", AllReady.get(2).toString()));

		}
		else{
			params.add(new BasicNameValuePair("player3", "0"));
			params.add(new BasicNameValuePair("playerTeam3", ""));
			params.add(new BasicNameValuePair("player3Ready", ""));
		}
		if (AllPlayers.size()>= 4){
			params.add(new BasicNameValuePair("player4", AllPlayers.get(3).toString()));
			params.add(new BasicNameValuePair("playerTeam4", AllColors.get(3).toString()));
			params.add(new BasicNameValuePair("player4Ready", AllReady.get(3).toString()));
		}
		else{
			params.add(new BasicNameValuePair("player4", "0"));
			params.add(new BasicNameValuePair("playerTeam4", ""));
			params.add(new BasicNameValuePair("player4Ready", ""));

		}
		if (AllPlayers.size()>= 5){
			params.add(new BasicNameValuePair("player5", AllPlayers.get(4).toString()));
			params.add(new BasicNameValuePair("playerTeam5", AllColors.get(4).toString()));
			params.add(new BasicNameValuePair("player5Ready", AllReady.get(4).toString()));
		}
		else{
			params.add(new BasicNameValuePair("player5", "0"));
			params.add(new BasicNameValuePair("playerTeam5", ""));
			params.add(new BasicNameValuePair("player5Ready", ""));
		}
		if (AllPlayers.size()>= 6){
			params.add(new BasicNameValuePair("player6", AllPlayers.get(5).toString()));
			params.add(new BasicNameValuePair("playerTeam6", AllColors.get(5).toString()));
			params.add(new BasicNameValuePair("player6Ready", AllReady.get(5).toString()));
		}
		else{
			params.add(new BasicNameValuePair("player6", "0"));
			params.add(new BasicNameValuePair("playerTeam6", ""));
			params.add(new BasicNameValuePair("player6Ready", ""));
		}
		if (AllPlayers.size()>= 7){
			params.add(new BasicNameValuePair("player7", AllPlayers.get(6).toString()));
			params.add(new BasicNameValuePair("playerTeam7", AllColors.get(6).toString()));
			params.add(new BasicNameValuePair("player7Ready", AllReady.get(6).toString()));
		}
		else{
			params.add(new BasicNameValuePair("player7", "0"));
			params.add(new BasicNameValuePair("playerTeam7", ""));
			params.add(new BasicNameValuePair("player7Ready", AllReady.get(6).toString()));
		}


		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_update_delete_gameinfo, "POST", params);


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





				// Starting new intent


			}
			else {
				// failed to update product
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}
	public void StartGame(){

		List<NameValuePair> params2 = new ArrayList<NameValuePair>();

		params2.add(new BasicNameValuePair("gamenumber","game"+pid));
		params2.add(new BasicNameValuePair("pid",pid));

		JSONObject json = jsonParser.makeHttpRequest(url_start_game,
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


	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.startButton){
			try {
				getReady();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int counter = 0;
			if(player.getReady() != "Yes"){
				for (int j = 0; j<AllReady.size(); j++){
					if (AllReady.get(j).contains("Yes")){
						counter++;
					}
				}
				if (counter+1 == Integer.parseInt(MaxPlayers)){
					setReady();
					StartGame();
					setTime();
					//new StartGame().execute();
				}
				else{
					setReady();
				}
			}



			// updating UI from Background Thread
			//servername.setText(gamepid);


		}
	}
	private void getReady() throws JSONException {
		AllReady.clear();
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("game_id", pid));

		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_get_gameinfo, "GET", params);


		// Check your log cat for JSON reponse
		Log.d("All Products: ", json.toString());

		// Checking for SUCCESS TAG
		int success = json.getInt(TAG_SUCCESS);

		if (success == 1) {
			// products found
			// Getting Array of Products
			products = json.getJSONArray(TAG_GAME_INFO);


			// looping through All Products

			JSONObject c = products.getJSONObject(0);
			AllReady.clear();

			AllReady.add(c.getString("player1Ready"));
			AllReady.add(c.getString("player2Ready"));
			AllReady.add(c.getString("player3Ready"));
			AllReady.add(c.getString("player4Ready"));
			AllReady.add(c.getString("player5Ready"));
			AllReady.add(c.getString("player6Ready"));
			AllReady.add(c.getString("player7Ready"));
			AllReady.add(c.getString("player8Ready"));
		}

	}
	//NEED TO CHANGE!
	private void setTime(){
		// Building Parameters
		time = System.currentTimeMillis()+8000;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("game_id", pid));
		params.add(new BasicNameValuePair("time", time+""));

		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_update_time, "POST", params);


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


			}
			else {
				// failed to update product
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}
	private void setReady(){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("game_id", pid));
		params.add(new BasicNameValuePair("playerid", player.getPlayerSpot()+""));

		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_update_ready, "POST", params);


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


			}
			else {
				// failed to update product
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		player.setReady("Yes");


	}
}

