package com.example.laser;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class GameActivity extends Activity {
	Player player;
	Drawable back;
	Resources res;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		res = getResources();

		Bundle bundle = getIntent().getExtras();
		player = (Player) getIntent().getSerializableExtra("player");
		setContentView(R.layout.activity_game);

		//	setContentView(R.layout.activity_game);
		// Show the Up button in the action bar.
		if(player.getTeam().matches("Blue")){
			back = res.getDrawable(R.drawable.blue_back); 



		}
		else if(player.getTeam().matches("Red")){
			back = res.getDrawable(R.drawable.red_back); 



		}
		else{
		//	back = res.getDrawable(R.drawable.neut_back); 

			
		}
		TextView name = (TextView)findViewById(R.id.player_name);
		name.setText(player.getName());
		name.setTextColor(Color.WHITE);

		LinearLayout linearLayout =  (LinearLayout)findViewById(R.id.GameLayout); 
		linearLayout.setBackgroundDrawable(back);

			}





}
