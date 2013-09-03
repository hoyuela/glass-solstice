package com.discover.mobile.bank.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.DiscoverOrangeSpinner;
import com.discover.mobile.bank.ui.table.ViewPagerListItem;
import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.StartListener;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;
import com.discover.mobile.common.ui.widgets.DynamicMultiImageViewLayout;

/**
 * Creates a list item specific to the View Check Images feature.
 * 
 * @author stephenfarr
 *
 */

public class BankCheckImagesListItem extends ViewPagerListItem implements StartListener, CompletionListener, 
																		  ErrorResponseHandler{
	
	private final DynamicMultiImageViewLayout multiImageViewLayout;
	private final Button viewCheckImagesButton;
	private final DiscoverOrangeSpinner orangeSpinner;
	private final TextView checkImagesTextView;

	//------------------------------ Constructors Methods ------------------------------
	public BankCheckImagesListItem(Context context) {
		super(context);
		
		viewCheckImagesButton = (Button)findViewById(R.id.view_checks_button);
		viewCheckImagesButton.setOnClickListener(hideViewCheckButton());
		
		multiImageViewLayout = (DynamicMultiImageViewLayout)findViewById(R.id.dynamic_image_widget);
		orangeSpinner = (DiscoverOrangeSpinner)findViewById(R.id.load_images);
		checkImagesTextView = (TextView)findViewById(R.id.check_images_text_view);
	}

	//------------------------------ ViewPagerListItem Overridden Methods ------------------------------
	
	/**
	 * Returns the BankCheckImagesListItem layout
	 * 
	 * @return layout for BankCheckImagesListItem
	 */
	@Override
	protected RelativeLayout getInflatedLayout() {
		
		return (RelativeLayout)LayoutInflater.from(getContext()).inflate(
															   R.layout.bank_account_activity_check_images_list_item, null); 
	}

	//------------------------------ Private Helper Methods ------------------------------
	
	/**
	 * Click Listener to hide the view check button and show the orange spinner.
	 * 
	 * @return
	 */
	private OnClickListener hideViewCheckButton() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewCheckImagesButton.setVisibility(View.GONE);
				
				orangeSpinner.setVisibility(View.VISIBLE);
				orangeSpinner.startAnimation();
			}
		};
	}

	//------------------------------ Start Listener Overridden Methods ------------------------------
	
	/**
	 * Code to run at service call start
	 */
	@Override
	public void start(NetworkServiceCall<?> sender) {
		//Place holder for when the service call is implemented
	}

	/**
	 * Returns the priority of the call back.
	 * 
	 * @return Callback priority
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.FIRST;
	}
	
	//------------------------------ Completion Listener Overridden Methods ------------------------------
	
	/**
	 * Handles service call completion
	 */
	@Override
	public void complete(NetworkServiceCall<?> sender, Object result) {
		orangeSpinner.stopAnimation();
	}

	//------------------------------ Error Response Handler Overridden Methods ------------------------------
	
	/**
	 * Handles the service call failure
	 */
	@Override
	public boolean handleFailure(NetworkServiceCall<?> sender, ErrorResponse<?> errorResponse) {
		// TODO Auto-generated method stub
		return false;
	}
}
