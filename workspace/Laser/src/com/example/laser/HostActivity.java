package com.example.laser;



import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.Menu;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class HostActivity extends Activity {
	EditText HostName;
	EditText PasswordName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host);
		setupWidget();
		addItemsOnSpinner();
		
	}
    public void setupWidget() {
    	HostName = (EditText)findViewById(R.id.name1);
    	HostName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});	//sets max characters length
    	PasswordName = (EditText)findViewById(R.id.editText1);
    	PasswordName.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});	//sets max characters length
    	
    }
    public void addItemsOnSpinner() {
        List<String> SpinnerArray =  new ArrayList<String>();
        SpinnerArray.add("2");
        SpinnerArray.add("4");
        SpinnerArray.add("8");
        SpinnerArray.add("16");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SpinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner Items = (Spinner) findViewById(R.id.players);
        Items.setAdapter(adapter);
      }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host, menu);
		return true;
	}

}
