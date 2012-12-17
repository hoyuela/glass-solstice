package com.discover.mobile.alert;

/**
* Interface to bind to the top views with a title and content view that will be placed in an 
* alert modal
* 
* @author jthornton
*
*/
public interface ModalTopView{

	/**
	 * Set the text in the title view
	 * @param resource - resource id to be shown
	 */
	public void setTitle(final int resource);
	
	/**
	 * Set the text in the content view
	 * @param resource - resource id to be shown
	 */
	public void setContent(final int resouce);
}
