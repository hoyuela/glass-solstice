package com.discover.mobile.bank.ui.modals;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.ui.modals.ModalTopView;
/**
 * The top view of the how it works modal.
 * This class exists because the normal default top view would not accept a layout as the
 * content, so this was created so that an entire layout can be used as the top view.
 * 
 * @author scottseward
 *
 */
public class HowItWorksModalTop extends ScrollView implements ModalTopView {

	public HowItWorksModalTop(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final View view = inflate(context, R.layout.how_it_works_modal_content, null);
		addView(view);
	}

	@Override
	public void setTitle(final int resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTitle(final String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContent(final int resouce) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContent(final String content) {
		// TODO Auto-generated method stub
		
	}
	

	
}
