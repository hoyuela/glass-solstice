package com.discover.mobile.bank.deposit;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.ScalableImage;
import com.discover.mobile.common.DiscoverActivityManager;
import com.google.common.base.Strings;
/**
 * This is a table cell that is added to LinearLayouts to display saved
 * check images and provide buttons to let a user retake the image.
 * @author scottseward
 *
 */
public class ReviewCheckDepositTableCell extends RelativeLayout {
	
	View mainView = null;
	
	public ReviewCheckDepositTableCell(final Context context) {
		super(context);
		doSetup(context);
	}
	
	public ReviewCheckDepositTableCell(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup(context);
	}
	
	public ReviewCheckDepositTableCell(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
		doSetup(context);
	}
	
	/**
	 * Add the inflated layout to this RelativeLayout, then load the saved check
	 * images into the cell.
	 * @param context
	 */
	private void doSetup(final Context context) {
		addView(getInflatedLayout(context));
		loadImages(context);
	}
	
	/**
	 * Return the inflated layout for this table cell.
	 * @param context the calling context
	 * @return the inflated view for this cell.
	 */
	private View getInflatedLayout(final Context context) {
		if(mainView == null)
			mainView = LayoutInflater.from(context).inflate(R.layout.review_check_deposit_cell, null);
		return mainView;
	}
	
	/**
	 * Load the check images into the image views that are presented on this table cell.
	 */
	public void loadImages(final Context context) {		
		loadImageToView(context, R.id.check_front_image, CheckDepositCaptureActivity.FRONT_PICTURE);
		loadImageToView(context, R.id.check_back_image, CheckDepositCaptureActivity.BACK_PICTURE);
	}
	
	/**
	 * Load a saved image to an ImageView.
	 * @param context the calling context.
	 * @param imageViewResource the ImageView resource id in the layout file.
	 * @param filename the file name of the image to load.
	 */
	private void loadImageToView(final Context context, final int imageViewResource, final String filename) {
		if(context != null && imageViewResource != 0 && !Strings.isNullOrEmpty(filename)) {
			Bitmap decodedImage = null;
			final ScalableImage checkImageView = (ScalableImage)findViewById(imageViewResource);
			final File savedImage = context.getFileStreamPath(filename);
			
			decodedImage = BitmapFactory.decodeFile(savedImage.getAbsolutePath());
			
			if(savedImage != null)
				decodedImage = BitmapFactory.decodeFile(savedImage.getAbsolutePath());

			if(decodedImage != null && checkImageView != null){
				final Drawable image = new BitmapDrawable(getResources(), decodedImage);
				checkImageView.setBackgroundDrawable(image);
			}
		}
	}
	
	/**
	 * Restarts the check deposit capture activity with a specified retake value.
	 * @param type
	 */
	public void retake(final int checkToRetake) {
		final Activity currentActivity = DiscoverActivityManager.getActiveActivity();
		
		final Intent retakePic = new Intent(currentActivity, CheckDepositCaptureActivity.class);
		retakePic.putExtra(BankExtraKeys.RETAKE_PICTURE, checkToRetake);
		currentActivity.startActivity(retakePic);
	}
	
	/**
	 * Method show the error label with the text provided.
	 * 
	 * @param text - Reference to a string that holds the error to be displayed to the user
	 */
	public void showErrorLabel(final String text) {
		final TextView errorLabel = (TextView)findViewById(R.id.error_label);
		if( errorLabel != null ) {
			errorLabel.setText(text);
			errorLabel.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 
	 * @return Returns reference to TextView that shows inline error in widget.
	 * 
	 */
	public TextView getErrorLabel() {
		final TextView errorLabel = (TextView)findViewById(R.id.error_label);
		return errorLabel;
	}
	
	/**
	 * Method used to clear in-line error shown under widget
	 */
	public void clearError() {
		final TextView errorLabel = (TextView)findViewById(R.id.error_label);
		errorLabel.setVisibility(View.GONE);
	}
}
