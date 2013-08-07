package com.discover.mobile.bank.ui.modals;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.ui.help.NeedHelpFooter;

/**
 * This class creates a modal dialog fragment for when a user is navigating out of the app.
 * @author rconner
 */
public class LeavingThisAppModal extends DialogFragment {
	
	private int modalTitle = R.string.bank_open_browser_title;
	private int modalText = R.string.bank_open_browser_text;
	private int buttonText = R.string.continue_text;
	
	/** View that holds the footer text */
	private NeedHelpFooter helpFooter;
	
	/**the url to navigate to upon "continue" click*/
	private String url;
	
	/**the view for the dialog*/
	private View view;
	
	public LeavingThisAppModal() {

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_FRAME, R.style.AppTheme);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {                
    	view = inflater.inflate(R.layout.simple_content_modal, container); 
		helpFooter = new NeedHelpFooter((ViewGroup) view);
		
        final Bundle bundle = getArguments();
        setURL(bundle.getString("URL"));
        
        setUpModal();
        
        return view;
    }
    
    public void setUpModal() {
    	setTitle(modalTitle);
        setContent(modalText);
        setButtonText(buttonText);
        
    	setClickListener();
        
    	//set the dialog to have no title bar and match_parent for width and height   	
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        
        //get rid of the default black DialogFragment border
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        
        hideNeedHelpFooter();
    }
    
    public void setClickListener() {
    	((Button) view.findViewById(R.id.button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				DiscoverActivityManager.getActiveActivity().startActivity(i);
				dismiss();
			}
		});
    }
	
	/**
	 * Set the text in the title view
	 * 
	 * @param text
	 *            - int with text to be displayed as the title
	 */
	public void setTitle(final int title){
		((TextView) view.findViewById(R.id.modal_alert_title)).setText(title);
	}
	
	/**
	 * Set the text in the content view
	 * 
	 * @param content
	 *            - int with text to be displayed as the message
	 */
	public void setContent(final int content){
		((TextView) view.findViewById(R.id.modal_alert_text)).setText(content);
	}
	
	/**
	 * Set the text in the button
	 * @param text - text to display
	 */
	public void setButtonText(final int text){
		((Button) view.findViewById(R.id.button)).setText(text);
	}
	
	/**
	 * Set the url to navigate to upon continue click
	 * @param newUrl - the url for navigation
	 */
	public void setURL(final String newUrl){
		this.url = newUrl;
	}
	
	/**
	 * Hide the help footer
	 */
	public void hideNeedHelpFooter(){
		helpFooter.show(false);
	}
}
