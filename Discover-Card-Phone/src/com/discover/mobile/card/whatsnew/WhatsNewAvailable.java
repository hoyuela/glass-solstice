
/* 13.3  Changes */

package com.discover.mobile.card.whatsnew;

import com.discover.mobile.card.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WhatsNewAvailable extends LinearLayout implements WhatsNewConstants{

	Context context;
	int pageID;
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
		switch (pageID) {
		case QUICK_VIEW_INFO:
			View v = vi.inflate(R.layout.whats_new_layout1, null);		
			setGravity(Gravity.CENTER);
			addView(v);
			setPageOneContent();
			break;
		default:
			break;
		}

	}

	private void setPageOneContent() {
		TextView title;
		ImageView imageView;
		TextView contentDescription;
		TextView linkLabel;
		title = (TextView) findViewById(R.id.whats_new_content_heading);
		imageView = (ImageView) findViewById(R.id.whats_new_image);
		contentDescription = (TextView) findViewById(R.id.whats_new_content_description_label);
		linkLabel = (TextView) findViewById(R.id.whats_new_label);

		title.setText(R.string.quickview_content_heading);
		linkLabel.setText(R.string.quickview_gotolink);
		if(cardType.equalsIgnoreCase("CBB") || cardType.equalsIgnoreCase("SBC")){
			contentDescription.setText(R.string.quickview_content_description_cbb);			
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.quick_view));
		}else if (cardType.equalsIgnoreCase("NOR")){			
			contentDescription.setText(R.string.quickview_content_description_dbc_corp);			
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.quickview_dbc_corp));

		}else{
			contentDescription.setText(R.string.quickview_content_description_mi);			
			imageView.setImageDrawable(getResources().getDrawable(
					R.drawable.quick_view_miles));
		}
	}
	
}
