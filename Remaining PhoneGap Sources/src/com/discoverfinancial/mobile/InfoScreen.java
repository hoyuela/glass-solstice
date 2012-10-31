package com.discoverfinancial.mobile;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoScreen extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		LinearLayout linearLayout = new LinearLayout(this);
		Drawable drawable = getResources().getDrawable(R.drawable.loading);
		linearLayout.setBackgroundDrawable(drawable);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		TextView tv1 = new TextView(this);
		PackageManager packageManager = this.getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		int versionCode = packageInfo.versionCode;
		String versionName = packageInfo.versionName;		
		String infoText = "Version name: " + versionName + " Version code: " + String.valueOf(versionCode);
		tv1.setText(infoText);
		linearLayout.addView(tv1);
		setContentView(linearLayout);
	}
}
