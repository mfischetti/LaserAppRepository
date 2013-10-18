package com.example.laser;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HostServerActivity extends Activity {
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	
	private static String url_create_product = "http://128.4.204.51/laserDatabase/android_connect/create_product.php";
	//192.168.1.15
	//udel - 128.4.222.193
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	
	private Socket client;
	private PrintWriter printwriter;
	private EditText textField;
	private Button button;
	private String messsage;
	String value;
	String game_name;
	String password;
	String players;
	String game_type;
	String ipadress;
	String current_players;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_server);

		textField = (EditText) findViewById(R.id.textout); //reference to the text field
		button = (Button) findViewById(R.id.send);   //reference to the send button
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		   // value = extras.getString("GameInfo");
			game_name = extras.getString("Game Name");
			password = extras.getString("Password");
			players = extras.getString("Players");
			game_type = extras.getString("Game Mode");
			current_players = "1"; //set to one since the host player counts as one.
			


			
		    
		}
		//Button press event listener
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
			//	messsage= textField.getText().toString();
			//	ipadress = textField.getText().toString();
				//messsage = value.toString(); //get the text message on the text field
				//textField.setText("");      //Reset the text field to blank
				new CreateNewGame().execute();
			/*	try {

					client = new Socket(ipadress, 4444);  //connect to server
					printwriter = new PrintWriter(client.getOutputStream(),true);
					printwriter.write(messsage);  //write the message to output stream

					printwriter.flush();
					printwriter.close();
					client.close();   //closing the connection

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
		});

	}


class CreateNewGame extends AsyncTask<String, String, String>{

	protected void onPreExecute(){
		
		super.onPreExecute();
		pDialog = new ProgressDialog(HostServerActivity.this);
		pDialog.setMessage("Creating Product..");
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
		
		
		params.add(new BasicNameValuePair("game_mode", game_type));

		JSONObject json = jsonParser.makeHttpRequest(url_create_product,
				"POST", params);
		
		// check log cat fro response
		Log.d("Create Response", json.toString());

		// check for success tag
		try {
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				// successfully created product
				Intent i = new Intent(getApplicationContext(), FindActivity.class);
				startActivity(i);
				
				// closing this screen
				finish();
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
