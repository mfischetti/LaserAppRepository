package com.example.laser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	TextView mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        setupWidget();
    }
//Test commit

    private void setupWidget() {
    	Button host = (Button)findViewById(R.id.host);
    	host.setOnClickListener(this);
    	Button find = (Button)findViewById(R.id.find);
    	find.setOnClickListener(this);
    	Button help = (Button)findViewById(R.id.help);
    	help.setOnClickListener(this);
    	mTitle = (TextView)findViewById(R.id.findtitle);
    	mTitle.setTextColor(Color.RED);
		// TODO Auto-generated method stub
		
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.host){
			StartHost();
		}
		if(v.getId() == R.id.find){
			StartFind();
		}
		if(v.getId() == R.id.help){
			StartHelp();
		}
	}


	private void StartHelp() {
		Intent shelp = new Intent(this, HelpActivity.class);	
		startActivity(shelp);		
	}


	private void StartFind() {
		Intent sfind = new Intent(this, FindActivity.class);	
		startActivity(sfind);		
	}


	private void StartHost() {
		Intent shost = new Intent(this, HostActivity.class);	
		startActivity(shost);
	}
    
}
