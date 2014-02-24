package com.example.laser;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.laser.JoinActivity.LoadPlayers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

public class HostServerActivity extends Activity {
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();
	Player player;
	VideoView countDown;
	Timer timer;
	int counter;
	TextView counter1, loadtitle;
	int change = 4;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_host_server);
		// 2. get person object from intent
		player = (Player) getIntent().getSerializableExtra("player");
		
		counter1 = (TextView)findViewById(R.id.count1);
		loadtitle = (TextView)findViewById(R.id.loadingtitle);
		counter1.setTextColor(Color.WHITE);
		loadtitle.setTextColor(Color.WHITE);
		loadtitle.setText("   LOAD");

		new CountDownTimer(4000, 1000) {

			public void onTick(long millisUntilFinished) {
				counter1.setText(String.valueOf(millisUntilFinished / 1000));
				if ((millisUntilFinished /1000) != change){
					change = (int)(millisUntilFinished / 1000);
					if (change == 3){
						loadtitle.setText("   LOAD .");	
					}
					if (change == 2){
						loadtitle.setText("   LOAD . .");	
					}
					if (change == 1){
						loadtitle.setText("   LOAD . . .");	
					}
				}

			}
			public void onFinish() {

				Intent in = new Intent(HostServerActivity.this,
						GameActivity.class);
				in.putExtra("player", player );
				// sending pid to next activity


				// starting new activity and expecting some response back
				startActivity(in);

			}
		}.start();	}

}