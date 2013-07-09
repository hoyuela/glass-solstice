package com.discover.mobile.card.ui.modals;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.discover.mobile.card.R;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ui.modals.ModalTopView;

/**
 * Top view of the confirmation modal
 * @author jthornton
 *
 */
public class ModalConfirmationTop extends RelativeLayout implements ModalTopView {
	
	/**Application Resources*/
	private final Resources res;
	
	/**Text View that holds the user id*/
	private final TextView userIdLabel;
	
	/**Text View holding the email*/
	private final TextView userEmailLabel;
	
	/**Text view holding the last for digits of the account number*/
	private final TextView userAcctNbrLabel;
	
	/**Text View First paragraph text*/
	private final TextView dialogTitle;
	
    
    /**Text View First paragraph text*/
    private final TextView firstParagraph;
    
    /**Text View First paragraph text*/
    private final TextView secondParagraph , securityLabelAbove , securityLabelBelow;
    
	/**Button to close the modal*/
	private final Button home;
	
	/**
	 * Constructor for the view
	 * @param context - activity context
	 * @param attrs - attributes to apply to the layout
	 */
	public ModalConfirmationTop(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		final RelativeLayout mainView = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.register_confirm, null);
		final  TextView noteLabel = (TextView) mainView.findViewById(R.id.account_info_confirm_note_label);
		
		res = context.getResources();
		userIdLabel = (TextView) mainView.findViewById(R.id.account_info_confirm_id_label);
		userEmailLabel = (TextView) mainView.findViewById(R.id.account_info_confirm_email_label);
		userAcctNbrLabel = (TextView) mainView.findViewById(R.id.account_info_confirm_account_label);
		
		dialogTitle = (TextView) mainView.findViewById(R.id.account_info_confirm_first_paragraph_label);		
		firstParagraph = (TextView)mainView.findViewById(R.id.account_info_confirm_message_first);
		secondParagraph = (TextView)mainView.findViewById(R.id.account_info_confirm_message_second);
		
		securityLabelAbove = (TextView)mainView.findViewById(R.id.account_info_confirm_security_precaution_label);
		securityLabelBelow = (TextView)mainView.findViewById(R.id.account_info_confirm_security_precaution);
		        
		home = (Button) mainView.findViewById(R.id.home);
		
		noteLabel.setText(Html.fromHtml(context.getString(R.string.account_info_confirm_note_text)));
		addView(mainView);
	}

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
	    dialogTitle.setText(text);
	}
	
	/**
	 * Set the screenType based on what is passed in
	 * @param screenType - screen type to show
	 *//*
	public void setScreenType(final String screenType){
		if(IntentExtraKey.SCREEN_FORGOT_BOTH.equals(screenType)){
		    firstParagraph.setText(res.getString(R.string.forgot_both_changed_text));
		} else if(IntentExtraKey.SCREEN_FORGOT_PASS.equals(screenType)){
		    firstParagraph.setText(res.getString(R.string.password_confirmation_changed_text));
		} else if(IntentExtraKey.SCREEN_FOROGT_USER.equals(screenType)){
		    firstParagraph.setText(res.getString(R.string.user_confirmation_changed_text));
		} else if(IntentExtraKey.SCREEN_REGISTRATION.equals(screenType)){
		    firstParagraph.setText(res.getString(R.string.account_info_confirm_first_paragraph_text));
		}
	}
	*/
	
	
    
    /**
     * Set the screenType based on what is passed in
     * @param screenType - screen type to show
     */
    public void setDialog(final String screenType){
        if(IntentExtraKey.SCREEN_FORGOT_BOTH.equals(screenType)){
            dialogTitle.setText("Success!");
            dialogTitle.setTextColor(res.getColor(R.color.orange_link_idicator));
            firstParagraph.setVisibility(View.VISIBLE);
            secondParagraph.setVisibility(View.VISIBLE);
            firstParagraph.setText(res.getString(R.string.forgot_both_changed_text));
            secondParagraph.setText(res.getString(R.string.second_paragraph_information));
            securityLabelAbove.setVisibility(View.GONE);
            securityLabelBelow.setVisibility(View.VISIBLE);
        } else if(IntentExtraKey.SCREEN_FORGOT_PASS.equals(screenType)){
            dialogTitle.setText("Success!");
            dialogTitle.setTextColor(res.getColor(R.color.orange_link_idicator));
            firstParagraph.setVisibility(View.VISIBLE);
            secondParagraph.setVisibility(View.VISIBLE);
            firstParagraph.setText(res.getString(R.string.password_confirmation_changed_text));
            secondParagraph.setText(res.getString(R.string.second_paragraph_information));
            securityLabelAbove.setVisibility(View.GONE);
            securityLabelBelow.setVisibility(View.VISIBLE);
        } else if(IntentExtraKey.SCREEN_FOROGT_USER.equals(screenType)){
            
        } else if(IntentExtraKey.SCREEN_REGISTRATION.equals(screenType)){
            dialogTitle.setText("Success!");
            dialogTitle.setTextColor(res.getColor(R.color.orange_link_idicator));
            firstParagraph.setVisibility(View.VISIBLE);
            secondParagraph.setVisibility(View.VISIBLE);
            firstParagraph.setText(res.getString(R.string.account_info_confirm_registration));
            secondParagraph.setText(res.getString(R.string.second_paragraph_information));
            securityLabelAbove.setVisibility(View.GONE);
            securityLabelBelow.setVisibility(View.VISIBLE);
        }
            
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

	/**
	 * Set the title view text. Not Used in this class
	 */
	@Override
	public void setTitle(final String text) {}

	/**
	 * Set the content text. Not Used in this class
	 */
	@Override
	public void setContent(final String content) {}


	public Button getButton(){
		return home;
	}
}