package com.discover.mobile.card.common.uiwidget;

import android.content.Context;
import android.util.AttributeSet;

import com.discover.mobile.card.R;
import com.discover.mobile.common.utils.CommonUtils;

/**
 * The card expiration date picker. This is a stylized edit text field. It is
 * attached to a date picker dialog that is responsible for setting the selected
 * month and year values.
 * 
 * @author scottseward
 * 
 */
public class CardExpirationDateEditText extends CustomDatePickerElement {

    // advances the year by 2 years into the future.
    private static final int YEAR_OFFSET = -2;

    public CardExpirationDateEditText(final Context context) {
        super(context);
    }

    public CardExpirationDateEditText(final Context context,
            final AttributeSet attrs) {
        super(context, attrs);
    }

    public CardExpirationDateEditText(final Context context,
            final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Checks to see if the saved expiration date is valid.
     * 
     * @return true if the month and year member variables are valid.
     */
    @Override
    public boolean isValid() {
        return isMonthValid() && isYearValid();
    }

    /**
     * Hide the day picker from the date picker dialog so we can only choose a
     * month and year.
     * 
     * @param context
     */
    @Override
    protected void defaultSetup(final Context context) {
        super.defaultSetup(context);
        attachedDatePickerDialog.hideDayPicker();
        // To set the date picker title for Card Expiration Date
        final String dobPickerTitle = getResources().getString(
                R.string.card_expiration_date_text);
        attachedDatePickerDialog.setTitle(dobPickerTitle);
    }

    /**
     * If the date picker has valid values, update its label to a formatted date
     * with those values.
     */
    @Override
    public void updateLabelWithSavedDate() {
        if (this.isValid()) {
            this.setText(CommonUtils.getFormattedDate(getMonth(), getYear()));
        }
    }

    /**
     * Return the placeholder text to use in the date picker field.
     * 
     * @return
     */
    @Override
    protected int getPlaceholderText() {
        return R.string.exp_date_placeholder;
    }

    /**
     * Return the year offset that will be subtracted from the current year.
     * 
     * @return
     */
    @Override
    protected int getYearOffset() {
        return YEAR_OFFSET;
    }

}
