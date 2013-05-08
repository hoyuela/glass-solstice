package com.discover.mobile.bank.deposit;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
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
		
	public ReviewCheckDepositTableCell(final Context context) {
		super(context);
		doSetup();
	}
	
	public ReviewCheckDepositTableCell(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup();
	}
	
	public ReviewCheckDepositTableCell(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
		doSetup();
	}
	
	/**
	 * Add the inflated layout to this RelativeLayout, then load the saved check
	 * images into the cell.
	 * @param context
	 */
	private void doSetup() {
		addView(getInflatedLayout());
		loadImages();
	}
	
	/**
	 * Return the inflated layout for this table cell.
	 * @param context the calling context
	 * @return the inflated view for this cell.
	 */
	private View getInflatedLayout() {
		return LayoutInflater.from(getContext()).inflate(R.layout.review_check_deposit_cell, null);
	}
	
	/**
	 * Load the check images into the image views that are presented on this table cell.
	 */
	public final void loadImages() {		
		loadImageToView(R.id.check_front_image, CheckDepositCaptureActivity.FRONT_PICTURE);
		loadImageToView(R.id.check_back_image, CheckDepositCaptureActivity.BACK_PICTURE);
	}
	
	/**
	 * Load a saved image to an ImageView.
	 * @param context the calling context.
	 * @param imageViewResource the ImageView resource id in the layout file.
	 * @param filename the file name of the image to load.
	 */
	private void loadImageToView(final int imageViewResource, final String filename) {
		if(imageViewResource != 0 && !Strings.isNullOrEmpty(filename)) {
			Bitmap decodedImage = null;
			final ScalableImage checkImageView = (ScalableImage)findViewById(imageViewResource);
			final File savedImage = getContext().getFileStreamPath(filename);
			
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			if(savedImage != null) {
				decodedImage = BitmapFactory.decodeFile(savedImage.getAbsolutePath(), options);
			}
			
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
			errorLabel.setText(Html.fromHtml(text).toString());
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
