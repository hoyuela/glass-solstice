package com.discover.mobile.common.ui.widgets;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DynamicMultiImageViewLayout extends LinearLayout {

	private final ArrayList<ImageView> multiImageList;
	
	
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
	 * @return
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
	 * @param image
	 * @return
	 */
	public ImageView addNewImageView(final byte[] image) {
		ImageView newImageLayout = new ImageView(getContext());
		
		//TODO: -sfarr Change this to an async task
		newImageLayout.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
		
		addImageViewToLayout(newImageLayout);
		
		return newImageLayout;
	}
	
	//------------------------------ Show Layout ------------------------------
	public void show() {
		for (ImageView imageView : multiImageList) {
			imageView.setVisibility(View.VISIBLE);
		}
		setVisibility(View.VISIBLE);
	}
	
	//------------------------------ Getters ------------------------------
	public ArrayList<ImageView> getImageViewList() {
		return multiImageList;
	}
	
	public ImageView getImageView(final int position) {
		return multiImageList.get(position);
	}
	
	//------------------------------ Private Helper Methods ------------------------------
	private void addImageViewToLayout(final ImageView imageLayout) {
		addView(imageLayout);
		multiImageList.add(imageLayout);
	}
	
	private void setupLinearLayout() {
		setOrientation(LinearLayout.VERTICAL);
	}
	
	private ImageView buildImageView() {
		ImageView newImageLayout = new ImageView(getContext());
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
		params.setMargins(0, 0, 0, 20);
		newImageLayout.setLayoutParams(params);
		
		return newImageLayout;
	}
}
