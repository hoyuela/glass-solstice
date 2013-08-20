package com.discover.mobile.card.fastcheck;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.card.account.summary.SimpleListItem;

/**
 * ©2013 Discover Bank
 * 
 * Fast check listview items
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class DisplayFastcheckRewardListItem extends SimpleListItem {

    public DisplayFastcheckRewardListItem(final Context context,
            final AttributeSet attrs) {
        super(context, attrs);
        final RelativeLayout layout = (RelativeLayout) LayoutInflater.from(
                context).inflate(R.layout.fastcheck_display_reward_list_item,
                null);

        label = (TextView) layout.findViewById(R.id.balance_label);
        value = (TextView) layout.findViewById(R.id.balance_value);
        action = (TextView) layout.findViewById(R.id.action_text);
        line = (ImageView) layout.findViewById(R.id.divider_line);

        addView(layout);
    }

    public void hideValue() {
        value.setVisibility(View.INVISIBLE);
    }
}
