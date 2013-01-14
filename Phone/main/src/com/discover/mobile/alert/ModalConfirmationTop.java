package com.discover.mobile.alert;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.discover.mobile.R;

public class ModalConfirmationTop extends RelativeLayout implements ModalTopView {
		
	private TextView userIdLabel;
	
	private TextView userEmailLabel;
	
	private TextView userAcctNbrLabel;
	
	private TextView firstParagraph;
	
	private TextView noteLabel;
	
	/**
	 * Constructor for the view
	 * @param context - activity context
	 * @param attrs - attributes to apply to the layout
	 */
	public ModalConfirmationTop(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.register_confirm, null);
		
		userIdLabel = (TextView) mainView.findViewById(R.id.account_info_confirm_id_label);
		userEmailLabel = (TextView) mainView.findViewById(R.id.account_info_confirm_email_label);
		userAcctNbrLabel = (TextView) mainView.findViewById(R.id.account_info_confirm_account_label);
		firstParagraph = (TextView) mainView.findViewById(R.id.account_info_confirm_first_paragraph_label);
		noteLabel = (TextView) mainView.findViewById(R.id.account_info_confirm_note_label);
		
		addView(mainView);
	}
	
//	@Override
//	public void onCreate(final Bundle savedInstanceState){
//		super.onCreate(savedInstanceState);
//		final Bundle extras = getIntent().getExtras();
//    	if(extras != null) {
//    		userIdLabel.setText(extras.getString(IntentExtraKey.UID));
//    		userEmailLabel.setText(extras.getString(IntentExtraKey.EMAIL));
//    		userAcctNbrLabel.setText(extras.getString(IntentExtraKey.ACCOUNT_LAST4));
//    		if("forgotPass".equals(extras.getString("ScreenType"))){
//    			firstParagraph.setText(R.string.password_confirmation_changed_text);
//    		} else if("forgotBoth".equals(extras.getString("ScreenType"))){
//    			firstParagraph.setText(R.string.forgot_both_changed_text);
//    		} else if("forgotId".equals(extras.getString("ScreenType"))){
//    			firstParagraph.setVisibility(View.GONE);
//    			noteLabel.setVisibility(View.INVISIBLE);
//    		}
//    	}
//	}
	
	/**
	 * Set the user id text
	 */
	public void setUserId(final String userId){
		userIdLabel.setText(userId);
	}
	
	/**
	 * Set the email address of the user
	 */
	public void setEmail(final String email){
		userEmailLabel.setText(email);
	}
	
	/**
	 * Set the last 4 digits of the users account number
	 */
	public void setLastFour(final String lastFour){
		userAcctNbrLabel.setText(lastFour);
	}
	
	/**
	 * Set the text in the first paragraph
	 */
	public void setConfirmationText(final String text){
		firstParagraph.setText(text);
	}
	
	/**
	 * Set the title view text
	 */
	@Override
	public void setTitle(final int resource) {}

	/**
	 * Set the content view text 
	 */
	@Override
	public void setContent(final int resouce) {}

}