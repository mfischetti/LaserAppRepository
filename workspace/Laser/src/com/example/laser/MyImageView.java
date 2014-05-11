package com.example.laser;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class MyImageView extends ImageView implements SensorEventListener
{
	float x, y,tX,tY, eX1, eY1, eX2, eY2;
	float [] loc;
	float radius = 5;
	float lat1, lon1, lat2, lon2;
	double player2lat = 39.680400;
	double player2lon = -75.7507500;
	
	double player3lat = 39.6803620;
	double player3lon = -75.7507033;
	
	double myLat, myLon;
	int deg = 90;
	double latMult = 759109.3112;
	double lonMult = 150996.5774;
	// record the compass picture angle turned
	private float currentDegree = 0f;

	// device sensor manager
	private SensorManager mSensorManager;


	public MyImageView(Context context) {
		super(context);    

	}
	// Constructor for inflating via XML
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);   


		x = 50;
		y = 50;
	}
	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		x = 50;
		y = 50;

	}
	public void setMyCords(double myLat, double myLon, double teamx, double teamy, double enemy1x, double enemy1y, double enemy2x, double enemy2y, float degree){
		currentDegree = degree;
		
/////Code for calculating distance between dots on the 150x150 image
		double teamDifLat = myLat - teamx;
		double teamDifLon = myLon - teamy;

		double teamDistx = teamDifLat*latMult;
		double teamDisty = teamDifLon*lonMult;

		tX = (float) (teamDistx+75);
		tY = (float)(teamDisty+75);
		
		
		double teamE1Lat = myLat - enemy1x;
		double teamE1Lon = myLon - enemy1y;

		double EDistx = teamE1Lat*latMult;
		double EDisty = teamE1Lon*lonMult;

		eX1 = (float) (EDistx+75);
		eY1 = (float)(EDisty+75);

		
		double teamE2Lat = myLat - enemy1x;
		double teamE2Lon = myLon - enemy1y;

		double EDist2x = teamE2Lat*latMult;
		double EDist2y = teamE2Lon*lonMult;

		eX2 = (float) (EDist2x+75);
		eY2 = (float)(EDist2y+75);

	Log.d("Cords","teamX: "+tX+" teamY: " +tY+" Enemy1: "+eX1+" Enemy1y: "+eY1+" En2x: "+eX2+" En2Y: "+eY2);

		invalidate();

	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint p = new Paint();
		Paint p2 = new Paint();
		p.setColor(Color.YELLOW);
		p2.setColor(Color.RED);
		p2.setStrokeWidth(2);
		p.setStrokeWidth(2); 
		canvas.rotate(currentDegree, 75,75);
		canvas.drawCircle(tX, tY, radius, p);
		canvas.drawCircle(eX1, eY1, radius, p2);
		canvas.drawCircle(eX2, eY2, radius, p2);
	



	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}       
}