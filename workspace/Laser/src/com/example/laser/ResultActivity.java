package com.example.laser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;




public class ResultActivity extends Activity {
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GAME_INFO = "game_info";
	private static String url_get_gamedata = "http://lasertagapp.no-ip.biz/laserDatabase/android_connect/get_ingame_info.php";

	JSONParser jParser = new JSONParser();
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> productsList;

	JSONArray products = null;
	JSONArray products1 = null;
	JSONArray products2 = null;
	JSONArray products3 = null;
	ArrayList<GamePlayer> AllPlayerInfo = new ArrayList<GamePlayer>();	
	ArrayList<GamePlayer> ScoreSort = new ArrayList<GamePlayer>();

	Player player;
	int success = 0;
	TextView info0,info1,info2,info3,info4,Scores;
	TextView place0,place1,place2,place3,place4;
	Button Home;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		player = (Player) getIntent().getSerializableExtra("player");
		place0 = (TextView) findViewById(R.id.place0);
		place1 = (TextView) findViewById(R.id.place1);
		place2 = (TextView) findViewById(R.id.place2);
		place3 = (TextView) findViewById(R.id.place3);
		place4 = (TextView) findViewById(R.id.place4);
		info0 = (TextView) findViewById(R.id.info0);
		info1 = (TextView) findViewById(R.id.info1);
		info2 = (TextView) findViewById(R.id.info2);
		info3 = (TextView) findViewById(R.id.info3);
		info4 = (TextView) findViewById(R.id.info4);
		Scores = (TextView) findViewById(R.id.scores);
		Home = (Button) findViewById(R.id.home);
		getFinalScores();
		Sort();
		setDisplay();
		Home.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(in);	
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}
	public void getFinalScores(){

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
	public void Sort(){
		ArrayList<GamePlayer> AllPlayerInfoTemp = new ArrayList<GamePlayer>();
		AllPlayerInfoTemp.addAll(AllPlayerInfo);
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
	}
	public void setDisplay(){
		int placed = 1;
		int bluescore = 0;
		int redscore = 0;
		if (AllPlayerInfo.get(2).NameID==""){
			info0.setText("\t\t"+AllPlayerInfo.get(0).NameID+"\t\t\t"+AllPlayerInfo.get(1).NameID+"\t\t\t\t\t\t\t\t\t\t"+"Score");
		}
		else{
			info0.setText("\t\t"+AllPlayerInfo.get(0).NameID+"\t\t"+AllPlayerInfo.get(1).NameID+"\t\t"+AllPlayerInfo.get(2).NameID+"\t\t"+ AllPlayerInfo.get(3).NameID+"\t\t"+"Score");
		}
		
		info0.setTextColor(Color.WHITE);
		info0.setTextSize(12);
		place0.setText("Place\t\t"+"Name");
		place0.setTextColor(Color.WHITE);
		place0.setTextSize(12);
		for (int loop = 0; loop <ScoreSort.size();loop++){
			if(ScoreSort.get(loop).NameID!=""){
				if (placed == 1){
					place1.setText("1st\t\t"+ScoreSort.get(loop).NameID);
					info1.setText("\t\t\t"+ScoreSort.get(loop).Player1+"\t\t\t\t"+ScoreSort.get(loop).Player2+"\t\t\t\t"+ScoreSort.get(loop).Player3+"\t\t\t\t"+ScoreSort.get(loop).Player4+"\t\t\t\t"+ ScoreSort.get(loop).Score);
					info2.setText("");
					info3.setText("");
					info4.setText("");
					place2.setText("");
					place3.setText("");
					place4.setText("");
					placed++;
					if (ScoreSort.get(loop).Team.contains("Blue")){
						bluescore = bluescore + Integer.parseInt(ScoreSort.get(loop).Score);
						info1.setTextColor(Color.BLUE);
						place1.setTextColor(Color.BLUE);
					}
					else{
						redscore = redscore + Integer.parseInt(ScoreSort.get(loop).Score);
						info1.setTextColor(Color.RED);
						place1.setTextColor(Color.RED);
					}
					if (ScoreSort.get(loop).NameID.contains(player.playerName)){
						info1.setBackgroundColor(Color.YELLOW);
						place1.setBackgroundColor(Color.YELLOW);
					}
				}
				else if (placed == 2){
					place2.setText("2nd\t\t"+ScoreSort.get(loop).NameID);
					info2.setText("\t\t\t"+ScoreSort.get(loop).Player1+"\t\t\t\t"+ScoreSort.get(loop).Player2+"\t\t\t\t"+ScoreSort.get(loop).Player3+"\t\t\t\t"+ScoreSort.get(loop).Player4+"\t\t\t\t"+ ScoreSort.get(loop).Score);
					info3.setText("");
					info4.setText("");
					placed++;
					if (ScoreSort.get(loop).Team.contains("Blue")){
						bluescore = bluescore + Integer.parseInt(ScoreSort.get(loop).Score);
						info2.setTextColor(Color.BLUE);
						place2.setTextColor(Color.BLUE);
					}
					else{
						redscore = redscore + Integer.parseInt(ScoreSort.get(loop).Score);
						info2.setTextColor(Color.RED);
						place2.setTextColor(Color.RED);
					}
					if (ScoreSort.get(loop).NameID.contains(player.playerName)){
						info2.setBackgroundColor(Color.YELLOW);
						place2.setBackgroundColor(Color.YELLOW);
					}
				}
				else if (placed == 3){
					place3.setText("3rd\t\t"+ScoreSort.get(loop).NameID);
					info3.setText("\t\t\t"+ScoreSort.get(loop).Player1+"\t\t\t\t"+ScoreSort.get(loop).Player2+"\t\t\t\t"+ScoreSort.get(loop).Player3+"\t\t\t\t"+ScoreSort.get(loop).Player4+"\t\t\t\t"+ ScoreSort.get(loop).Score);
					info4.setText("");
					placed++;
					if (ScoreSort.get(loop).Team.contains("Blue")){
						bluescore = bluescore + Integer.parseInt(ScoreSort.get(loop).Score);
						info3.setTextColor(Color.BLUE);
						place3.setTextColor(Color.BLUE);
					}
					else{
						redscore = redscore + Integer.parseInt(ScoreSort.get(loop).Score);
						info3.setTextColor(Color.RED);
						place3.setTextColor(Color.RED);
					}
					if (ScoreSort.get(loop).NameID.contains(player.playerName)){
						info3.setBackgroundColor(Color.YELLOW);
						place3.setBackgroundColor(Color.YELLOW);
					}
				}
				else if (placed == 4){
					place4.setText("4th\t\t"+ScoreSort.get(loop).NameID);
					info4.setText("\t\t\t"+ScoreSort.get(loop).Player1+"\t\t\t\t"+ScoreSort.get(loop).Player2+"\t\t\t\t"+ScoreSort.get(loop).Player3+"\t\t\t\t"+ScoreSort.get(loop).Player4+"\t\t\t\t"+ ScoreSort.get(loop).Score);
					if (ScoreSort.get(loop).Team.contains("Blue")){
						bluescore = bluescore + Integer.parseInt(ScoreSort.get(loop).Score);
						info4.setTextColor(Color.BLUE);
						place4.setTextColor(Color.BLUE);
					}
					else{
						redscore = redscore + Integer.parseInt(ScoreSort.get(loop).Score);
						info4.setTextColor(Color.RED);
						place4.setTextColor(Color.RED);
					}	
					if (ScoreSort.get(loop).NameID.contains(player.playerName)){
						info4.setBackgroundColor(Color.YELLOW);
						place4.setBackgroundColor(Color.YELLOW);
					}
				}
			}
		}

		//update scores here
		if(bluescore>redscore){
			Scores.setText("Blue Team Wins!\tBlue: "+bluescore+"\t\tRed: "+redscore);
			Scores.setTextColor(Color.BLUE);
		}
		else if(bluescore<redscore){
			Scores.setText("Red Team Wins!\tRed: "+redscore+"\t\tBlue: "+bluescore);
			Scores.setTextColor(Color.RED);
		}
		else{
			Scores.setText("Its a Tie!\tRed: "+redscore+"\t\tBlue: "+bluescore);
			Scores.setTextColor(Color.WHITE);
		}
	}
}