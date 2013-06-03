package com.discover.mobile.card.fastcheck;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.account.summary.SimpleListItem;

public class DisplayFastcheckTitleListItem extends SimpleListItem {
	private Button backButton;
	private ImageButton refreshButton;
	
	public ImageView getRefreshButton() {
		return refreshButton;
	}

	public void setRefreshButton(ImageButton refreshButton) {
		this.refreshButton = refreshButton;
	}

	public Button getBackButton() {
		return backButton;
	}

	public void setBackButton(Button backButton) {
		this.backButton = backButton;
	}

	public DisplayFastcheckTitleListItem(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final RelativeLayout layout = 
				(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.fastcheck_display_title_list_item, null);
		
		backButton = (Button)layout.findViewById(R.id.fastcheck_display_back_button);
		refreshButton = (ImageButton)layout.findViewById(R.id.refresh_button);	
		label = (TextView)layout.findViewById(R.id.balance_label);
		value = (TextView)layout.findViewById(R.id.balance_value);
		action = (TextView)layout.findViewById(R.id.action_text);
		line = (ImageView)layout.findViewById(R.id.divider_line);

		addView(layout);
	}
	
}
