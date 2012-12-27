package com.discover.mobile.push.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discover.mobile.R;
import com.discover.mobile.RoboSherlockFragment;

public class PushHistoryFragment extends RoboSherlockFragment{
	
	public static final int ALERT_AMOUNT_TO_GET = 10;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		final View mainView = inflater.inflate(R.layout.push_history_layout, null);
	
		return mainView;
	}

	@Override
	public int getActionBarTitle() {
		return R.string.push_alert_history_title;
	}

}
