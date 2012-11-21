package com.discover.mobile;

import java.util.Timer;
import java.util.TimerTask;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import android.content.Intent;
import android.os.Bundle;

import com.discover.mobile.login.StartActivity;
@ContentView(R.layout.splash)
public class SplashScreenActivity extends RoboActivity{
	
	@Override
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		
		final Intent mainActivity = new Intent(this, StartActivity.class);
				
		new Timer().schedule(new TimerTask(){
		    public void run() { 
		        startActivity(mainActivity);
		    }
		}, 2500 );
	}

}
