package com.discover.mobile.card.common.uiwidget;

import java.util.Locale;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.google.common.base.Strings;

import com.discover.mobile.common.utils.CommonUtils;

import com.discover.mobile.card.common.InputValidator;

import com.discover.mobile.card.R;

public class ConfirmationAnswerEditText extends ValidatedInputField {
    private EditText editTextToMatch;

    public ConfirmationAnswerEditText(final Context context) {
        super(context);
    }

    public ConfirmationAnswerEditText(final Context context,
            final AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfirmationAnswerEditText(final Context context,
            final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setErrorLabelText(String text)

    {
        if (errorLabel != null)
            errorLabel.setText(text);
    }

    public String getErrorLabelText() {
        String errorText = "";
        if (errorLabel != null)
            errorText = errorLabel.getText().toString();
        return errorText;

    }

    /**
     * Returns true if the current text matches the attached text field text.
     */
    @Override
    public boolean isValid() {
        boolean isValid = true;

        if (isNull()) {
            setErrorLabelText(this.getResources().getString(
                    R.string.confirm_answer_nottyped));
            isValid = false;
        } else if (isSpaceEntered()) {
            setErrorLabelText(this.getResources().getString(
                    R.string.space_entered));
            isValid = false;
        } else if (isSpecialCharacterEntered()) {
            setErrorLabelText(this.getResources().getString(
                    R.string.special_characters_entered));
            isValid = false;
        } else if (!isMatchedWithAnswer()) {
            setErrorLabelText(this.getResources().getString(
                    R.string.answer_not_match));
            isValid = false;
        }

        return isValid;
    }

    /**
     * When an edit text field is attached to the confirmation field, start
     * listening to text changes on that attached field. Then update the
     * confirmation field based on the attached field. So we have a two way real
     * time validation system going on.
     * 
     * @param matchTo
     *            An EditText that we want our confirmation field to match to.
     */
    public void attachEditTextToMatch(final EditText matchTo) {
        if (matchTo != null) {
            editTextToMatch = matchTo;

            this.matchInputRestrictionsTo(editTextToMatch);

            editTextToMatch.addTextChangedListener(getMatcherTextWatcher());
        }
    }

    /**
     * Returns a TextWatcher that should be added to the EditText that this
     * Confirmation EditText is watching. This will cause the attached EditText
     * field to clear errors or set the confirmation field to matched (Green)
     * when it gets edited.
     * 
     * @return - A TextWatcher that is intended to be attached to the EditText
     *         that a ConfirmationEditText is matching itself to. This allows
     *         the attached EditText to update the appearance of the
     *         ConfirmationEditText.
     */
    private TextWatcher getMatcherTextWatcher() {
        return new TextWatcher() {

            @Override
            public void afterTextChanged(final Editable s) {
            }

            @Override
            public void beforeTextChanged(final CharSequence s,
                    final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start,
                    final int before, final int count) {
                if (!isValid()) {
                    clearErrors();
                    clearRightDrawable();
                } else {
                    setAppearanceMatched();
                }
            }

        };
    }

    /**
     * Sets the input restrictions of the confirmation field to the input
     * restrictions of the attached EditText.
     * 
     * @param attachedField
     */
    private void matchInputRestrictionsTo(final EditText attachedField) {
        this.setFilters(attachedField.getFilters());
        this.setInputType(attachedField.getInputType());
    }

    /**
     * When a user navigates away from the field, do a validation and graphic
     * update again. If the field is yet to be matched it will still be gray,
     * but when a user navigates away it will turn red to notify them that they
     * need to correct it.
     */
    @Override
    protected void setupFocusChangedListener() {

        this.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (!hasFocus && !isValid()) {
                    setErrors();
                } else if (!hasFocus && isValid()) {
                    setAppearanceMatched();
                } else {
                    setRightDrawableGrayX();
                    if (isInErrorState) {
                        setRightDrawableRedX();
                    }
                }

            }
        });
    }

    /**
     * Setup a text changed listener to the confirmation field (this). During a
     * user editing the field it will stay gray until it matches, then it will
     * turn green and place a green check mark in the right drawable location of
     * the input field.
     */
    @Override
    protected void setupTextChangedListener() {
        final ConfirmationAnswerEditText self = this;

        this.addTextChangedListener(new TextWatcher() {
            String beforeText;
            String afterText;

            @Override
            public void afterTextChanged(final Editable s) {
                afterText = s.toString();// defect id 95350

                if (beforeText.equals(afterText) && !beforeText.equals("")
                        && !afterText.equals("")) {
                    if (isValid()) {
                        setAppearanceMatched();
                    } else {
                        updateAppearanceForInput();
                    }
                }
            }

            // defect id 95350

            @Override
            public void beforeTextChanged(final CharSequence s,
                    final int start, final int count, final int after) {
                beforeText = s.toString();
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start,
                    final int before, final int count) {

                if (count > 0) {
                    isInDefaultState = false;
                }

                if (!isInDefaultState && isValid()) {
                    clearErrors();
                    setAppearanceMatched();
                } else {
                    clearErrors();
                }

            }

        });
    }

    /**
     * The appearance of the text field when it is matching an attached
     * EditText's input.
     */
    private void setAppearanceMatched() {
        this.setBackgroundResource(R.drawable.card_textfield_valid_holo_light);
        this.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getGreenCheck(), null);
    }

    public boolean isMatchedWithAnswer() {
        boolean valueMatched = false;

        if (editTextToMatch != null && editTextToMatch.length() > 0) {
            valueMatched = this.getText().toString()
                    .equals(editTextToMatch.getText().toString());
        }

        return valueMatched;
    }

   /* public boolean isNull() {

        return Strings.isNullOrEmpty(this.getText().toString());
    }*/

    /**
     * Update the error state of the field. If the input field is valid - hide
     * the error state and show as valid. If not, show the error state.
     */
    /*
     * @Override public void updateAppearanceForInput() { if (isValid()) {
     * clearErrors(); setAppearanceMatched(); } else { setErrors(); } }
     */

    public boolean isSpecialCharacterEntered() {
        final String currentInput = this.getText().toString();
        boolean isValid = false;
        isValid = InputValidator.validateAnswerField(currentInput);
        return isValid;
    }

    /**
     * This method validates the current input for space entered
     */
    public boolean isSpaceEntered() {
        final String currentInput = this.getText().toString();
        boolean isSpaceEntered = false;

        /* 13.4 Defect ID 105360 start */
        /* if (currentInput.contains(" ")) {
             isSpaceEntered = true;
         } else {
             isSpaceEntered = false;
         }*/
         if(currentInput.startsWith(" ")||currentInput.endsWith(" ")){
         	isSpaceEntered = true;
         }else
         {
         	isSpaceEntered = false;
         }
         /* 13.4 Defect ID 105360 End */	
         
        return isSpaceEntered;
    }

    @Override
    protected void showErrorLabel() {
        // TODO Auto-generated method stub
        AnswerEditText answerEditText = (AnswerEditText) editTextToMatch;
        if (errorLabel != null) {
            if (!isNull())
                errorLabel.setVisibility(View.VISIBLE);
            if (!(answerEditText.isValid())) {
                if (answerEditText.isNull() && !isNull()) {
                    errorLabel.setVisibility(View.VISIBLE);
                } else {
                    errorLabel.setVisibility(View.GONE);
                }
            }
        }
    }

}