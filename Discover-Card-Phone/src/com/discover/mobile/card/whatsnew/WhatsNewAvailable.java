
/* 13.3  Changes */

package com.discover.mobile.card.whatsnew;

import com.discover.mobile.card.R;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;


import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;

public class WhatsNewAvailable extends LinearLayout {

	Context context;
	int pageID;

	TextView title;
	ImageView imageView;
	TextView contentDescription;
	TextView linkLabel;
	 String cardType;

	public WhatsNewAvailable(Context context, int pageID, String cardType) {
		super(context);
		this.context = context;
		this.pageID = pageID;
		this.cardType = cardType;
		init();
	}

	public WhatsNewAvailable(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		/*
		 * setBackgroundColor(Color.WHITE); Button button = new
		 * Button(getContext()); button.setText("Click Me..");
		 */
		LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		View v = vi.inflate(R.layout.whats_new, null);		
		setGravity(Gravity.CENTER);
		addView(v);

		title = (TextView) findViewById(R.id.whats_new_content_heading);
		imageView = (ImageView) findViewById(R.id.whats_new_image);
		contentDescription = (TextView) findViewById(R.id.whats_new_content_description_label);
		linkLabel = (TextView) findViewById(R.id.whats_new_label);

		switch (pageID) {
		case 1:
			setpageoneContent();
			break;
		case 2:
			//setpagetwoContent();
			break;		
		default:
			
			break;
		}

	}
	
	private void setWhatsNewContent(){
		title.setText(R.string.quickview_content_heading);
		
		if(cardType.equalsIgnoreCase("CBB")){
			contentDescription.setText(R.string.quickview_content_description_cbb);
			linkLabel.setText(R.string.quickview_gotolink);
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.quick_view));
		}
		else{

			contentDescription.setText(R.string.quickview_content_description_mi);
			linkLabel.setText(R.string.quickview_gotolink);
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.quick_view_miles));
			
		}
		
	}
	
	private void setpagetwoContent() {
		title.setText(R.string.fastview_content_heading);
		contentDescription.setText(R.string.fastview_content_description);
		linkLabel.setText(R.string.fastview_gotolink);
		imageView.setImageDrawable(getResources().getDrawable(
				R.drawable.quick_view));
		// TODO Auto-generated method stub

	}

	private void setpageoneContent() {
		setWhatsNewContent();
		// TODO Auto-generated method stub

	}

}
