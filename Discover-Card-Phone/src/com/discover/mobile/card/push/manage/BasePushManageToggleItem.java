package com.discover.mobile.card.push.manage;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.card.R;

/**
 * Base class for all of the toggle items
 * 
 * @author jthornton
 * 
 */
public abstract class BasePushManageToggleItem extends RelativeLayout implements
        PushManageCategoryItem {

    /** Header view of the item */
    private TextView headerView;

    /** ImageView of the enable text alert check box */
    private ImageView textAlert;

    /** ImageView of the push enable alert check box */
    private ImageView pushAlert;

    /** Boolean holding the state of the text alert box */
    private boolean isTextChecked = false;

    /** Boolean holding the state of the push alert box */
    private boolean isPushChecked = false;

    /** Boolean representing if the text alert was already set in the prefs */
    private boolean wasTextAlreadySet = false;

    /** Fragment holding these items */
    private PushManageFragment fragment;

    /** Resources of the app */
    private final Resources res;

    /**
     * Constructor for the class
     * 
     * @param context
     *            - activity context
     * @param attrs
     *            - attributes to give to the layout
     */
    public BasePushManageToggleItem(final Context context,
            final AttributeSet attrs) {
        super(context, attrs);
        res = context.getResources();
    }

    /**
     * Get the on click listeners for the toggle items
     * 
     * @return the on click listener for the toggle items
     */
    protected View.OnClickListener getToggleListener() {
        return new OnClickListener() {

            @Override
            public void onClick(final View v) {
                final ImageView toggleImage = (ImageView) v;
                fragment.showSaveBar();
                if (toggleImage.getId() == textAlert.getId()) {
                    toggleTextBox(!isTextChecked);
                } else {
                    togglePushBox(!isPushChecked);
                }
            }

        };
    }

    /**
     * Toggle the text enable box
     * 
     * @param isChecked
     *            - if the toggle box should be set
     */
    protected void toggleTextBox(final boolean isChecked) {
        toggleBox(textAlert, isChecked);
        isTextChecked = isChecked;
    }

    /**
     * Toggle the push enable box
     * 
     * @param isChecked
     *            - if the toggle box should be set
     */
    protected void togglePushBox(final boolean isChecked) {
        toggleBox(pushAlert, isChecked);
        isPushChecked = isChecked;
    }

    /**
     * Toggle a box with the blank image or image with a check mark
     * 
     * @param toggleImage
     *            - image to be toggled
     * @param isChecked
     *            - if the item should be checked
     */
    protected void toggleBox(final ImageView toggleImage,
            final boolean isChecked) {
        if (!isChecked) {
            toggleImage.setBackgroundDrawable(res
                    .getDrawable(R.drawable.gray_gradient_square));
            toggleImage.setImageDrawable(res
                    .getDrawable(R.drawable.transparent_square));
        } else {
            toggleImage.setBackgroundDrawable(res
                    .getDrawable(R.drawable.black_gradient_square));
            toggleImage.setImageDrawable(res
                    .getDrawable(R.drawable.white_check_mark));
        }
    }

    /**
     * Get if the text alert option is enabled
     * 
     * @return if the text alert option is enabled
     */
    public boolean isTextAlertEnabled() {
        return isTextChecked;
    }

    /**
     * Get if the push alert option is enabled
     * 
     * @return if the push alert option is enabled
     */
    public boolean isPushAlertEnabled() {
        return isPushChecked;
    }

    /**
     * Set the text enable box active or not active
     * 
     * @param isEnabled
     *            - if the the text enable box active or not active
     */
    public void setTextAlertBox(final boolean isEnabled) {
        isTextChecked = isEnabled;
        toggleBox(textAlert, isEnabled);
    }

    /**
     * Set the push enable box active or not active
     * 
     * @param isEnabled
     *            - if the the push enable box active or not active
     */
    public void setPushAlertBox(final boolean isEnabled) {
        isPushChecked = isEnabled;
        toggleBox(pushAlert, isEnabled);
    }

    /**
     * Get the text toggle view
     * 
     * @return the text toggle view
     */
    protected ImageView getTextToggleView() {
        return textAlert;
    }

    /**
     * Get the push toggle view
     * 
     * @return push toggle view
     */
    protected ImageView getPushToggleView() {
        return pushAlert;
    }

    /**
     * Set the text toggle view
     * 
     * @param textToggle
     *            - the text toggle view
     */
    protected void setTextToggleView(final ImageView textToggle) {
        textAlert = textToggle;
    }

    /**
     * Set the push toggle view
     * 
     * @param pushToggle
     *            - the push toggle view
     */
    protected void setPushToggleView(final ImageView pushToggle) {
        pushAlert = pushToggle;
    }

    /**
     * Get the header view of the item
     * 
     * @return the header view of the item
     */
    public TextView getHeaderView() {
        return headerView;
    }

    /**
     * Set the header view of the item
     * 
     * @param headerView
     *            - the header view of the item
     */
    public void setHeaderView(final TextView headerView) {
        this.headerView = headerView;
    }

    /**
     * Get if the text preference was already set active by what was received
     * from the server
     * 
     * @return if the text preference was already set active by what was
     *         received from the server
     */
    public boolean isWasTextAlreadySet() {
        return wasTextAlreadySet;
    }

    /**
     * Set if the text preference was already set active by what was received
     * from the server
     * 
     * @param isAlreadySet
     *            - if the text preference was already set active by what was
     *            received from the server
     */
    @Override
    public void setWasTextAlreadySet(final boolean isAlreadySet) {
        wasTextAlreadySet = isAlreadySet;
    }

    /**
     * Set the fragment using these items
     * 
     * @param fragment
     *            - the fragment using these items
     */
    public void setFragment(final PushManageFragment fragment) {
        this.fragment = fragment;
    }
}
