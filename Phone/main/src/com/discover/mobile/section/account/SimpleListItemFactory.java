package com.discover.mobile.section.account;

import android.content.Context;
import android.view.View.OnClickListener;

public final class SimpleListItemFactory {
	
	private SimpleListItemFactory(){}

	/**
	 * Create a list item with no action button
	 */
	public static SimpleListItem createItem(final Context context, final String label, final String value, 
											final String action){
		final SimpleListItem item = new SimpleListItem(context, null);
		item.setLabel(label);
		item.setValue(value);
		item.setAction(action);
		return item;
	}
	
	/**
	 * Create a list item with no action button
	 */
	public static SimpleListItem createItem(final Context context, final String label, final String value){
		final SimpleListItem item = new SimpleListItem(context, null);
		item.setLabel(label);
		item.setValue(value);
		item.hideAction();
		return item;
	}
	
	/**
	 * Create a list item with no action button
	 */
	public static SimpleListItem createItem(final Context context, final String label, final String value, 
								  			final String action, final OnClickListener listener){
		final SimpleListItem item = new SimpleListItem(context, null);
		item.setLabel(label);
		item.setValue(value);
		item.setAction(action);
		item.setActionHandler(listener);
		return item;
	}
}
