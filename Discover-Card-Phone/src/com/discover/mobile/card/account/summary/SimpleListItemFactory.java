package com.discover.mobile.card.account.summary;

import android.content.Context;
import android.text.Spanned;
import android.view.View.OnClickListener;

/**
 * Factory used to create simple list items
 * 
 * @author jthornton
 * 
 */
public final class SimpleListItemFactory {

    /**
     * Default constructor
     */
    private SimpleListItemFactory() {
    }

    /**
     * Creates a simple list item
     * 
     * @param context
     *            - activity context
     * @param label
     *            - label to be displayed
     * @param value
     *            - value to be displayed
     * @param action
     *            - action text to be displayed
     * @return the populated list item
     */
    public static SimpleListItem createItem(final Context context,
            final String label, final String value, final String action) {
        final SimpleListItem item = new SimpleListItem(context, null);
        item.setLabel(label);
        item.setValue(value);
        item.setAction(action);
        return item;
    }

    /**
     * Creates a simple list item
     * 
     * @param context
     *            - activity context
     * @param label
     *            - label to be displayed
     * @param value
     *            - value to be displayed
     * @return the populated list item
     */
    public static SimpleListItem createItem(final Context context,
            final String label, final String value) {
        final SimpleListItem item = new SimpleListItem(context, null);
        item.setLabel(label);
        item.setValue(value);
        item.hideAction();
        return item;
    }

    public static SimpleListItem createItem(final Context context,
            final Spanned label, final String value) {
        final SimpleListItem item = new SimpleListItem(context, null);
        item.setLabel(label);
        item.setValue(value);
        item.hideAction();
        return item;
    }

    /**
     * Creates a simple list item
     * 
     * @param context
     *            - activity context
     * @param label
     *            - label to be displayed
     * @param value
     *            - value to be displayed
     * @param action
     *            - action text to be displayed
     * @param listener
     *            - click listener to be placed with the action button
     * @return the populated list item
     */
    public static SimpleListItem createItem(final Context context,
            final String label, final String value, final String action,
            final OnClickListener listener) {
        final SimpleListItem item = new SimpleListItem(context, null);
        item.setLabel(label);
        item.setValue(value);
        item.setAction(action);
        item.setActionHandler(listener);
        return item;
    }
}
