package com.discover.mobile.bank.deposit;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalBottomOneButtonView;
import com.discover.mobile.common.ui.modals.ModalTopView;

/**
 * The modal dialog used for the how it works content.
 * This exists because the modal needed to be larger than the standard modal in both landscape and portrait.
 * 
 * @author scottseward
 * 
 */
public class HowItWorksModal extends ModalAlertWithOneButton {
	
	
	public HowItWorksModal(final Context context, final ModalTopView top,
			final ModalBottomOneButtonView bottom) {
		super(context, top, bottom);
		
	}
	
	/**
	 * Returns the layout to be used for the modal.
	 */
	@Override
	protected int getMainLayout() {
		return R.layout.check_deposit_modal;
	}
	
	/**
	 * Here the display method is overridden to that the weight of the top and bottom parts
	 * of the modal can be adjusted so that it appears properly in landscape mode.
	 */
	@Override
	public void display() {
		final Activity activeActivity = DiscoverActivityManager.getActiveActivity();

		final float absentBottomWeight = 10f;
		final float landscapeTopWeight = 6.2f;
		final float landscapeBottomWeight = 3.8f;
		final float portraitTopWeight = 7.4f;
		final float portraitBottomWeight = 2.6f;
		
		final RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.modal_dialog);
		final Resources res = activeActivity.getResources();
		
		final int betweenReleatedElementsPadding = (int)res.getDimension(R.dimen.between_related_elements_padding);
		final int modalTopPadding = (int)res.getDimension(R.dimen.modal_top_padding);
		
		final ModalBottomOneButtonView bottom = getBottom();
		final ModalTopView top = getTop();
		
		final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.modal_linear_layout);
		linearLayout.removeAllViews();
		
		final int orientation = activeActivity.getResources().getConfiguration().orientation; 
		float topWeight;
		float bottomWeight;
		
		if (null == bottom){
			topWeight = absentBottomWeight;
			bottomWeight = 0;
		}else if (Configuration.ORIENTATION_LANDSCAPE == orientation) { 
			topWeight = landscapeTopWeight;
			bottomWeight = landscapeBottomWeight;
			mainLayout.setPadding(betweenReleatedElementsPadding, betweenReleatedElementsPadding, 
									betweenReleatedElementsPadding, modalTopPadding);
		} else { 
			mainLayout.setPadding(betweenReleatedElementsPadding, betweenReleatedElementsPadding,
									betweenReleatedElementsPadding, modalTopPadding);
			topWeight = portraitTopWeight;
			bottomWeight = portraitBottomWeight;
		} 

		final LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, topWeight);

		final LinearLayout.LayoutParams p2 = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0, bottomWeight);

		
		linearLayout.removeAllViews();

		if(null != top){
			linearLayout.addView((View)top, p1);
		}
		if(null != bottom){
			linearLayout.addView((View)bottom, p2);
		}

	}

}
