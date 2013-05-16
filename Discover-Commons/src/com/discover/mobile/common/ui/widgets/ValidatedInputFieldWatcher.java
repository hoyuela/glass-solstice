package com.discover.mobile.common.ui.widgets;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Base {@link TextWatcher} used for all {@link ValidatedInputField} instances.
 * Use one per ValidatedInputField to better optimize input performance.
 * <br><br>
 * When extending ValidatedInputField and overriding {@link ValidatedInputFieldWatcher#afterTextChanged(Editable)},
 * make a call to this superclass to ensure the normal error appearance reset 
 * (namely, calls {@link ValidatedInputField#clearErrors()}) 
 * (Used to be part of a separate TextWatcher contained in ValidatedInputField).
 * @author alliecurry
 */
public class ValidatedInputFieldWatcher implements TextWatcher {
	
	/** The ValidatedInputField being watched by this TextWatcher. */
	private ValidatedInputField inputField;
	
	public ValidatedInputFieldWatcher(ValidatedInputField inputField) {
		this.inputField = inputField;
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(inputField.isInErrorState && inputField.isValid()) {
			inputField.clearErrors();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// Not needed in base implementation. Override in a subclass when necessary.
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// Not needed in base implementation. Override in a subclass when necessary.
	}

	protected ValidatedInputField getInputField() {
		return inputField;
	}
}
