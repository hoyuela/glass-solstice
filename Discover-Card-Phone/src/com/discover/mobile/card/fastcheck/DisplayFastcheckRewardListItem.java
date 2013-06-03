package com.discover.mobile.card.fastcheck;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.account.summary.SimpleListItem;

public class DisplayFastcheckRewardListItem extends SimpleListItem {

	private final ImageView errorImage;
	
	public DisplayFastcheckRewardListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final RelativeLayout layout = 
				(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.fastcheck_display_reward_list_item, null);
		errorImage = (ImageView)layout.findViewById(R.id.error_img);
		
		label = (TextView)layout.findViewById(R.id.balance_label);
		value = (TextView)layout.findViewById(R.id.balance_value);
		action = (TextView)layout.findViewById(R.id.action_text);
		line = (ImageView)layout.findViewById(R.id.divider_line);

		addView(layout);
	}
	
	public void hideValue(){
		value.setVisibility(View.INVISIBLE);
	}
}
