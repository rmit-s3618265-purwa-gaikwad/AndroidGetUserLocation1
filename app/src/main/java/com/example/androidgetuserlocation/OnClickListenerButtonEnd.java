package com.example.androidgetuserlocation;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickListenerButtonEnd implements OnClickListener {

	final String TAG = "OnClickListenerButtonEnd.java";
	
	MainActivity mainActivity;
	Context context;
	
	@Override
	public void onClick(View view) {
		
		Log.e(TAG, "Ended getting user location.");
		
		// to get the context and main activity
		this.context = view.getContext();
		this.mainActivity = ((MainActivity) context);

		// enable the START button, disable the STOP button
		mainActivity.buttonStart.setEnabled(true);
		mainActivity.buttonStop.setEnabled(false);
		
		// stop the listener
		mainActivity.locationHelper.stopGettingLocationUpdates();
		
	}

}
