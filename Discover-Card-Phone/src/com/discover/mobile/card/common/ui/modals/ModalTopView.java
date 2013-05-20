package com.discover.mobile.card.common.ui.modals;

/**
 * Interface to bind to the top views with a title and content view that will be
 * placed in an alert modal
 * 
 * @author jthornton
 * 
 */
public interface ModalTopView {

    /**
     * Set the text in the title view
     * 
     * @param resource
     *            - resource id to be shown
     */
    public void setTitle(final int resource);

    /**
     * Set the text in the title view
     * 
     * @param text
     *            - String with text to be displayed as the title
     */
    public void setTitle(final String text);

    /**
     * Set the text in the content view
     * 
     * @param resource
     *            - resource id to be shown
     */
    public void setContent(final int resouce);

    /**
     * Set the text in the content view
     * 
     * @param content
     *            - String with text to be displayed as the message
     */
    public void setContent(final String content);
}
