package com.example.laser;



import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class HostActivity extends Activity implements OnClickListener {
	
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	
	private static String url_create_product = "http://128.4.204.51/laserDatabase/android_connect/create_product.php";
    private static String url_all_products = "http://128.4.204.51/laserDatabase/android_connect/get_all_products.php";

	//192.168.1.15
	//udel - 128.4.222.193
	//128.4.223.234
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	
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
			current_players = "0";
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
					json = jsonParser.makeHttpRequest(url_all_products,
							"GET", params);
					String gamepid = json.getString("pid");
					// successfully created product
					Intent i = new Intent(getApplicationContext(), JoinActivity.class);
					i.putExtra("gamepid", gamepid);
					i.putExtra("current_players", current_players);
					startActivity(i);
					
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
		}
		
		
		
	}

}
