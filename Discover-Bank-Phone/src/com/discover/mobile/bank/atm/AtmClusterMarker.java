package com.discover.mobile.bank.atm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;

import com.discover.mobile.bank.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twotoasters.clusterkraf.ClusterPoint;
import com.twotoasters.clusterkraf.MarkerOptionsChooser;


/**
 * This class is used to draw the cluster icon on the map and perform actions when the cluster marker is selected.
 * 
 * @author stephenfarr
 *
 */
public class AtmClusterMarker extends MarkerOptionsChooser{

	private Context context;
	private Resources resources;
	
	private final int ALPHA_VALUE = 255;
	
	/* This int is used to compare the size of the cluster size and see if it is a double digit or single digit value */
	private final int MINIMUM_DOUBLE_DIGIT_VALUE = 10;
	
	/* These offsets shift the X and Y position of the text within the cluster icons */
	private final float DOUBLE_DIGIT_WIDTH_OFFSET = 0.2f;
	private final float SINGLE_DIGIT_WIDTH_OFFSET = 0.32f;
	private final float SINGLE_DIGIT_HEIGHT_OFFSET = 0.7f;
	private final float DOUBLE_DIGIT_HEIGHT_OFFSET = 0.68f;
	
	public AtmClusterMarker(Context context) {
		this.context = context;
		this.resources = context.getResources();
	}
	
	/**
	 * Chooses whether to use the cluster marker or the single ATM marker on the map.
	 */
	@Override
	public void choose(MarkerOptions markerOptions, ClusterPoint clusterPoint) {
		BitmapDescriptor icon;
		if (clusterPoint.size() > 1) {
			//cluster, draw seperate image
			icon = BitmapDescriptorFactory.fromBitmap(getClusterMarker(clusterPoint.size()));
		} else {
			//not a cluster, just render the single pin
			icon = BitmapDescriptorFactory.fromResource(R.drawable.atm_pin_orange_map_view);
		}
		markerOptions.icon(icon);
	}

	/**
	 * Returns the icon representing the the cluster of ATMs
	 * 
	 * @param clusterSize - Number of ATMs represented by the grouping icon
	 * @return Bitmap of the group icon
	 */
	@SuppressLint("NewApi")
	public Bitmap getClusterMarker(int clusterSize){
		BitmapFactory.Options options = new BitmapFactory.Options();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			options.inMutable = true;
		}
		
		//Gets the grouping icon and adds it to the canvas.
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.atm_pin_grouping_icon, options);
		if(!bitmap.isMutable()) {
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		}
		Canvas canvas = new Canvas(bitmap);
		
		//The paint creates the value drawn into the grouping icon representing the number of ATMS.
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAlpha(ALPHA_VALUE);
		paint.setAntiAlias(true);
		float offsetWidth = 0f;
		float offsetHeight = 0f;
		if(clusterSize >= MINIMUM_DOUBLE_DIGIT_VALUE) {
			offsetWidth = bitmap.getWidth() * DOUBLE_DIGIT_WIDTH_OFFSET;
			offsetHeight = bitmap.getHeight() * DOUBLE_DIGIT_HEIGHT_OFFSET;
			paint.setTextSize(resources.getDimension(R.dimen.map_double_digit_text_size));
		} else {
			offsetWidth = bitmap.getWidth() * SINGLE_DIGIT_WIDTH_OFFSET;
			offsetHeight = bitmap.getHeight() * SINGLE_DIGIT_HEIGHT_OFFSET;
			paint.setTextSize(resources.getDimension(R.dimen.map_single_digit_text_size));
		}
		
		//Draws the text onto the canvas with the specific offsets on the icon
		canvas.drawText(String.valueOf(clusterSize), offsetWidth, offsetHeight, paint);
		return bitmap;
	}
}
