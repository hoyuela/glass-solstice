package com.discover.mobile.bank.deposit;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera.Size;

/**
 * Common class to put utility methods for the mobile deposit capture feature.
 * @author jthornton
 *
 */
public final class MCDUtils {

	private MCDUtils(){
		//Intentionally left blank and made private
	}

	/**
	 * Get the best image size that should be taken.  This method may return null if the bestSize is not set.
	 * If designed width is larger than 1600, the application will set the shouldResizeImage to true.
	 * Note this method will sort the items.
	 * @param sizes - list of possible sizes
	 * @param maxImageWidth - int representing the maximum width that the image can be
	 * @return the best size
	 */
	public static Size getBestImageSize(final List<Size> sizes, final int maxImageWidth){
		Size bestSize = null;

		//Sort the list in order from highest width to lowest
		Collections.sort(sizes, new Comparator<Size>(){

			@Override
			public int compare(final Size lhs, final Size rhs) {
				if (lhs.width == rhs.width) {
					return 0;
				} else if (lhs.width > rhs.width) {
					return -1;
				} else {
					return 1;
				}
			}

		});

		final int arrayLength = sizes.size();
		for(int i = 0; i < arrayLength; i++){
			final Size size = sizes.get(i);

			//If there is only one item in the array use that item
			if(arrayLength == 1){
				bestSize = size;
				//If the length is equal to max length (1600) set the image size
			}else if(size.width == maxImageWidth){
				bestSize = sizes.get(i);
				break;
				//If the length dipped below the max length (1600) take the next size up
			}else if(size.width < maxImageWidth){
				//If all the lengths are below the max value and 
				//its the first in the list take that item.
				if(i == 0){
					bestSize = size;
					//If all the lengths are below the max value and 
					//its the first in the list take that item.
				}else{
					bestSize = sizes.get(i-1);
				}
				break;
			}else{
				bestSize = size;
			}
		}

		return bestSize;
	}

	/**
	 * Get the adjusted height of an image
	 * @param origHeight - original height of the image
	 * @param origWidth - original width of the image
	 * @param newWidth - the new width of the image
	 * @return the adjusted height
	 */
	public static int getAdjustedImageHeight(final int origHeight, final int origWidth, final int newWidth){
		return origHeight*newWidth/origWidth;
	}

}
