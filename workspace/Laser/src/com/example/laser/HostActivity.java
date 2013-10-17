package com.example.laser;



import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
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
			GameInfo = HostName.getText().toString()+ "~"
					+ PasswordName.getText().toString()+"~"+Items.getSelectedItem().toString()+
					"~"+tempRadio.getText().toString();
			Editable texttest = HostName.getText();
			Intent next = new Intent(this, HostServerActivity.class);	
			next.putExtra("Game Name", game_name.toString());
			next.putExtra("Password",password.toString()); 
			next.putExtra("Players", players); 
			next.putExtra("Game Mode", game_mode);
			startActivity(next);
		
			
			
		}
	}
	public void GetRadioSelected() {
        int selectedId = mode.getCheckedRadioButtonId();
        tempRadio = (RadioButton) findViewById(selectedId);
	}

}
