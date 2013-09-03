package com.discover.mobile.common.ui.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * The DynamicMultiImageViewLayout class allows a user to add multiple image views to a single layout easily repeatedly.
 * The primary use for this class is for the View Check Images feature.
 * 
 * @author stephenfarr
 *
 */

public class DynamicMultiImageViewLayout extends LinearLayout {

	/** List of added image views */
	private final ArrayList<ImageView> multiImageList;
	
	private final int imageViewBottomMargin = 20;
	
	
	//------------------------------ Constructors -------------------------
	public DynamicMultiImageViewLayout(Context context) {
		super(context);
		
		multiImageList = new ArrayList<ImageView>();
		setupLinearLayout();
	}
	
	public DynamicMultiImageViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		multiImageList = new ArrayList<ImageView>();
		setupLinearLayout();
	}
	
	//------------------------------ Add New Image Views To Layout ------------------------------
	/**
	 * Adds New Image View in the "Retrieving Image" state
	 * 
	 * @return returns the new image view
	 */
	public ImageView addNewImageView() {
		ImageView newImageView = buildImageView();
		addImageViewToLayout(newImageView);
		
		return newImageView;
	}
	
	/**
	 * Adds New Image View with a supplied image.  The DynamicImageViewLayout will immediately show the image and skip
	 * having the loading image shown.
	 * 
	 * @param image - image to add to the new image view
	 * @return returns the new image view
	 */
	public ImageView addNewImageView(final byte[] image) {
		ImageView newImageLayout = new ImageView(getContext());
		
		//TODO: -sfarr Change this to an async task
		newImageLayout.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
		
		addImageViewToLayout(newImageLayout);
		
		return newImageLayout;
	}
	
	//------------------------------ Show Layout ------------------------------
	
	/**
	 * Shows the DynamicMultiImageViewLayout
	 */
	public void show() {
		for (ImageView imageView : multiImageList) {
			imageView.setVisibility(View.VISIBLE);
		}
		setVisibility(View.VISIBLE);
	}
	
	//------------------------------ Getters ------------------------------
	
	/**
	 * Gets the list of image views
	 * 
	 * @return image view list
	 */
	public ArrayList<ImageView> getImageViewList() {
		return multiImageList;
	}
	
	/**
	 * Gets a specific image view
	 * 
	 * @param position - position of image view to remove
	 * @return requested image view
	 */
	public ImageView getImageView(final int position) {
		return multiImageList.get(position);
	}
	
	//------------------------------ Private Helper Methods ------------------------------
	
	/**
	 * Adds the image view
	 * 
	 * @param imageLayout
	 */
	private void addImageViewToLayout(final ImageView imageLayout) {
		addView(imageLayout);
		multiImageList.add(imageLayout);
	}
	
	
	/**
	 * Sets up the linear layout
	 */
	private void setupLinearLayout() {
		setOrientation(LinearLayout.VERTICAL);
	}
	
	/**
	 * Builds a new image view
	 * 
	 * @return newly built image view
	 */
	private ImageView buildImageView() {
		ImageView newImageLayout = new ImageView(getContext());
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
		params.setMargins(0, 0, 0, imageViewBottomMargin);
		newImageLayout.setLayoutParams(params);
		
		return newImageLayout;
	}
}
