package com.discover.mobile.card.common.uiwidget;

import java.text.NumberFormat;
import java.text.ParseException;

import android.content.Context;
import android.content.res.Resources;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;

import com.google.common.base.Strings;

import com.discover.mobile.card.common.utils.Utils;

import com.discover.mobile.card.R;

/**
 * Custom edit text field for the manage push alerts edit text items
 * 
 * @author jthornton
 * 
 */
public class PushManageEditText extends ValidatedInputField {

    /** Static string to replace the minimum amount */
    private static String MINIMUM_AMT = "MINIMUM_AMT";

    /** Static string to replace the maximum amount */
    private static String MAXIMUM_AMT = "MAXIMUM_AMT";

    /** Amount in the text box */
    private int amount;

    /** Max amount allowed in the text box */
    private int maxAmount;

    /** Min amount allowed in the text box */
    private int minAmount;

    /** application context */
    private final Context context;

    /** Defined amount from server */
    private int definedAmount;

    /** Tag for error logging */
    private static final String TAG = PushManageEditText.class.getSimpleName();

    /**
     * Class constructor
     * 
     * @param context
     *            - activity context
     */
    public PushManageEditText(final Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Class constructor
     * 
     * @param context
     *            - activity context
     * @param attrs
     *            - attribute set
     */
    public PushManageEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * Class constructor
     * 
     * @param context
     *            - activity context
     * @param attrs
     *            - attribute set
     * @param defStyle
     *            - style of the edit text
     */
    public PushManageEditText(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    /**
     * Return true if the edit text contains a valid value
     * 
     * @return if the edit text contains a valid value
     */
    @Override
    public boolean isValid() {
        amount = Integer.parseInt(getAmountFromEditText());
        boolean isValid;
        if (maxAmount == 0) {
            isValid = amount >= minAmount;
        } else {
            isValid = amount <= maxAmount && amount >= minAmount;
        }

        if (null == errorLabel) {
            return isValid;
        }

        if (!isValid) {
            errorLabel.setText(getErrorCodeText());
            errorLabel.setVisibility(View.VISIBLE);
        } else {
            errorLabel.setVisibility(View.GONE);
        }
        return isValid;
    }

    /**
     * Set the default appearance so that we dont have to do it in XML.
     */
    @Override
    public void setupDefaultAppearance() {
        this.setBackgroundResource(FIELD_DEFAULT_APPEARANCE);
        this.setTextColor(getResources().getColor(R.color.field_copy));
        this.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    /**
     * Get the amount the user put in the box
     * 
     * @return - the amount put in the box
     */
    private String getAmountFromEditText() {
        String amountString = this.getText().toString();
        String number = Integer.toString(0);

        if (!Strings.isNullOrEmpty(amountString)) {
            if (!amountString.contains("$")) {
                amountString = "$" + amountString;
            }
            try {
                number = NumberFormat.getCurrencyInstance().parse(amountString)
                        .toString();
            } catch (final ParseException e) {
                Utils.log(TAG, "Error parsing string " + amountString
                        + " , reason: " + e.getMessage());
            }
        }
        return number;
    }

    /**
     * Check to see if the edit text has changed from its defined amount
     * 
     * @return if the edit text has changed from its defined amount
     */
    public boolean hasChanged() {
        return !(Integer.parseInt(getAmountFromEditText()) == definedAmount);
    }

    /**
     * Get the error code to display
     * 
     * @return the error code to display
     */
    public String getErrorCodeText() {
        final Resources res = context.getResources();
        amount = Integer.parseInt(getAmountFromEditText());
        String string = "";
        if (amount <= maxAmount || maxAmount == 0) {
            string = res.getString(R.string.push_manage_minimum_error_text);
            string = string.replace(MINIMUM_AMT, Integer.toString(minAmount));
        } else if (amount >= minAmount) {
            string = res.getString(R.string.push_manage_maximum_error_text);
            string = string.replace(MAXIMUM_AMT, Integer.toString(maxAmount));
        }
        return string;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount
     *            the amount to set
     */
    public void setAmount(final int amount) {
        this.amount = amount;
        this.setText(NumberFormat.getCurrencyInstance().format(amount));
    }

    /**
     * @return the maxAmount
     */
    public int getMaxAmount() {
        return maxAmount;
    }

    /**
     * @param maxAmount
     *            the maxAmount to set
     */
    public void setMaxAmount(final int maxAmount) {
        this.maxAmount = maxAmount;
    }

    /**
     * @return the minAmount
     */
    public int getMinAmount() {
        return minAmount;
    }

    /**
     * @param minAmount
     *            the minAmount to set
     */
    public void setMinAmount(final int minAmount) {
        this.minAmount = minAmount;
    }

    /**
     * @return the definedAmount
     */
    public int getDefinedAmount() {
        return definedAmount;
    }

    /**
     * @param definedAmount
     *            the definedAmount to set
     */
    public void setDefinedAmount(final int definedAmount) {
        this.definedAmount = definedAmount;
    }

}
