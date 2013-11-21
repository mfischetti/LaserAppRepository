package com.example.laser;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class GameActivity extends Activity {
	Player player;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		player = (Player) getIntent().getSerializableExtra("player");
	//	setContentView(R.layout.activity_game);
		// Show the Up button in the action bar.
		if(player.getTeam().matches("Blue")){
			setContentView(R.layout.activity_game_blue);
			TextView team = (TextView)findViewById(R.id.textView111);
			team.setText(player.getTeam());
		}
		else if(player.getTeam().matches("Red")){
			setContentView(R.layout.activity_game_red);
			TextView team = (TextView)findViewById(R.id.textView3);
			team.setText(player.getTeam());


		}
		else{
			setContentView(R.layout.activity_game_neut);
			TextView team = (TextView)findViewById(R.id.textView2);
			team.setText(player.getTeam());



		}
	}






}
