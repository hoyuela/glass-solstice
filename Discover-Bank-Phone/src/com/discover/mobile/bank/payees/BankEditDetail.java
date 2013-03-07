package com.discover.mobile.bank.payees;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.discover.mobile.bank.R;

/**
 * This class is used to display the view in the layout res/bank_edit_deail_view. It provides methods
 * which allows other objects to change how this class is rendered. 
 * 
 * @author henryoyuela
 *
 */
public class BankEditDetail extends RelativeLayout implements OnClickListener, OnFocusChangeListener, OnEditorActionListener{
	private TextView topLabel;
	private TextView middleLabel;
	private PayeeValidatedEditField editableField;
	private TextView errorLabel;
	private View dividerLine;
	private View caret;
	private View view;
	
	public BankEditDetail(final Context context) {
		super(context);
		doSetup(context);
	}
	
	public BankEditDetail(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		doSetup(context);
	}
	public BankEditDetail(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		doSetup(context);
	}

	private void doSetup(final Context context) {
		addView(getInflatedLayout(context));
		loadViews();
	}
	
	private RelativeLayout getInflatedLayout(final Context context) {
		return (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.bank_edit_detail_view, null);
	}
	
	private void loadViews() {
		view = findViewById(R.id.list_item);
		caret = findViewById(R.id.caret);
		topLabel = (TextView)findViewById(R.id.top_label);
		middleLabel = (TextView)findViewById(R.id.middle_label);
		errorLabel = (TextView)findViewById(R.id.error_label);	
		editableField = (PayeeValidatedEditField)findViewById(R.id.editable_field);
		editableField.attachErrorLabel(errorLabel);
		editableField.setOnFocusChangeListener(this);
		editableField.setOnEditorActionListener(this);
	
		
		view.setOnClickListener(this);
		
		dividerLine = findViewById(R.id.divider_line);
		
		/**User will have to tap edit to edit the text field, by default is not visible*/
		this.setEditMode(false);
	}
	
	/**
	 * @return Returns a reference to the View displaying the divider line.
	 */
	public View getDividerLine() {
		return dividerLine;
	}
	
	/** 
	 * @return Returns a reference to the text view displayed at the top of the layout
	 */
	public TextView getTopLabel() {
		return topLabel;
	}
	
	/** 
	 * @return Returns a reference to the text view displayed in the middle of the layout
	 */
	public TextView getMiddleLabel() {
		return middleLabel;
	}
	
	/**
	 * 
	 * @return Returns the text both the editable and textview fields have been set to.
	 */
	public String getText() {
		return middleLabel.getText().toString();
	}
	
	/**
	 * Method used to set the text for the middle label and editable field at once.
	 * 
	 * @param text Reference to string to use to set the editable field and middle label
	 */
	public void setText(final String text) {
		middleLabel.setText(text);
		editableField.setText(text);
	}
	
	/** 
	 * @return Returns a reference to the editable view within the layout
	 */
	public PayeeValidatedEditField getEditableField() {
		return editableField;
	}
	
	/**
	 * Method used to enable or disable the editing capability of this view. If
	 * false hides the caret and edit label.
	 * 
	 * @param value True to allow the user to edit the editableField, false otherwise.
	 */
	public void enableEditing(final boolean value) {
		if( value ) {
			caret.setVisibility(View.VISIBLE);
		} else {
			caret.setVisibility(View.GONE);
		}
		
	}
	
	/**
	 * Method used to show the error the label hosted by this view with the text
	 * provided.
	 * 
	 * @param text String Resource Identifier that points to the string to be displayed by the error label.
	 */
	public void showErrorLabel(final int text) {
		this.editableField.showErrorLabel(text);
	}
	
	/**
	 * Method used to toggle between showing an editable text field or showing a label.
	 * 
	 * @param value Set to true if want to make editable, otherwise false.
	 */
	public void setEditMode( final boolean value ) {
		final InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		
		if( value ) {
			if( !editableField.hasFocus() ) {
				editableField.setText(middleLabel.getText());
				editableField.setVisibility(View.VISIBLE);
				middleLabel.setVisibility(View.GONE);
				editableField.setFocusable(true);
				editableField.requestFocus();
				
				imm.showSoftInput(editableField, InputMethodManager.SHOW_FORCED);
			}
		} else {
			middleLabel.setText(editableField.getText());
			editableField.setVisibility(View.GONE);
			middleLabel.setVisibility(View.VISIBLE);
			
			imm.hideSoftInputFromWindow(editableField.getWindowToken(), 0);
		}
	}

	/**
	 * Click handler for any view hosted by this layout.
	 */
	@Override
	public void onClick(final View sender) {
		/**
		 * If any part of the view is clicked and the view is editable
		 * then toggle the edit mode for this view.
		 */
		if( sender == view  && caret.getVisibility() == View.VISIBLE) {
			setEditMode(!(editableField.getVisibility() == View.VISIBLE));		
		}
	}

	/**
	 * Event handler for when focus is gained or lost from the editableField in this layout. 
	 * On a focus change it toggles the edit mode for this view.
	 */
	@Override
	public void onFocusChange(final View arg0, final boolean arg1) {
		setEditMode(arg1);	
	}

	/**
	 * Method used to detect if user has pressed done on the soft keyboard. This callback will only be called
	 * if the ime option for the editable field has been set to EditorInfo.IME_ACTION_DONE.
	 * 
	 * @param v	The view that was clicked.
	 * @param actionId	Identifier of the action. This will be either the identifier you supplied, or EditorInfo.IME_NULL if being called due to the enter key being pressed.
	 * @param event	If triggered by an enter key, this is the event; otherwise, this is null.
	 * 
	 * @return Return true if you have consumed the action, else false.
	 */
	@Override
	public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			setEditMode(false);
        }
        return false;
	}
	

}
