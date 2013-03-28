package com.discover.mobile.bank.deposit;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalBottomOneButtonView;
import com.discover.mobile.common.ui.modals.ModalTopView;
/**
 * A modal dialog that is used during the check deposit capture activity.
 * It's a subclass of ModalAlertWithOneButton.
 * 
 * @author scottseward
 *
 */
public class CheckDepositModal extends ModalAlertWithOneButton {

	public CheckDepositModal(final Context context, final ModalTopView top,
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
		final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.modal_linear_layout);
		
		linearLayout.removeAllViews();
		
		final float topWeight = 6f;
		final float bottomWeight = 4f;
		final ModalTopView top = getTop();
		final ModalBottomOneButtonView bottom = getBottom();


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
