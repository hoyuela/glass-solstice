package com.discover.mobile.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;

/**
 * This is the base activity for any activity that wants to use the Action bar
 * that is not logged in. This will show the back button with the Discover logo.
 * 
 * @author jthornton
 * 
 */
public abstract class NotLoggedInRoboActivity extends SherlockActivity implements ErrorHandlerUi, AlertDialogParent {
	protected boolean modalIsPresent = false;
	/**
	 * Reference to the dialog currently being displayed on top of this activity. Is set using setDialog();
	 */
	private AlertDialog mActiveDialog;

	/**
	 * Create the activity and show the action bar
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showActionBar();
	}

	/**
	 * Child class must supply proper error handler
	 */
	@Override
	public abstract ErrorHandler getErrorHandler();


	@Override
	public void onResume(){
		super.onResume();

		DiscoverActivityManager.setActiveActivity(this);
		
		//If a modal was showing show the modal
		if(DiscoverModalManager.isAlertShowing() && null != DiscoverModalManager.getActiveModal()){
			DiscoverModalManager.getActiveModal().show();
			DiscoverModalManager.setAlertShowing(true);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		closeDialog();
		
		//Close the modal if it is showing
		if(null != DiscoverModalManager.getActiveModal() && DiscoverModalManager.getActiveModal().isShowing()){
			DiscoverModalManager.getActiveModal().dismiss();
			DiscoverModalManager.setAlertShowing(true);
		}else{
			DiscoverModalManager.setAlertShowing(false);
		}
	}

	/**
	 * Show the action bar with the custom layout
	 */
	public void showActionBar() {
		final ActionBar actionBar = getSupportActionBar();

		actionBar.setCustomView(getLayoutInflater().inflate(
				R.layout.action_bar_menu_layout, null));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		final ImageView logo = (ImageView) this
				.findViewById(R.id.action_bar_discover_logo);
		final ImageView back = (ImageView) this
				.findViewById(R.id.navigation_back_button);

		back.setVisibility(View.INVISIBLE);
		logo.setVisibility(View.VISIBLE);

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				goBack();
			}
		});
	}

	/**
	 * Set the title in the action bar to display text instead of the default discover image
	 */
	public void setActionBarTitle(final int stringResource) {
		//Hide the title image in the action bar.
		((ImageView)this.findViewById(R.id.action_bar_discover_logo)).setVisibility(View.GONE);

		//Show title text with string resource.
		final TextView titleText = (TextView)findViewById(R.id.logged_out_title_view);
		titleText.setText(this.getString(stringResource));
		titleText.setVisibility(View.VISIBLE);

	}

	/**
	 * Set the title in the action bar to display the title image.
	 */
	public void setActionBarTitleImageVisible() {
		//Hide the title image in the action bar.
		((ImageView)this.findViewById(R.id.action_bar_discover_logo)).setVisibility(View.VISIBLE);

		//Hide title text and reset text value.
		final TextView titleText = (TextView)findViewById(R.id.title_view);
		titleText.setText(this.getString(R.string.empty));
		titleText.setVisibility(View.GONE);
	}

	/**
	 * Present a modal error dialog over the current activity with a given title and body text. Can also close the
	 * current activity on close if needed.
	 * 
	 * @param titleText - the String resource to present in the title of the modal dialog.
	 * @param bodyText - the String resource to present in the body of the modal dialog.
	 * @param finishActivityOnClose - if passed as true, the activity that displays the modal 
	 * error will be finished when the modal is closed.
	 */
	protected void showErrorModal(final int titleText, final int bodyText, final boolean finishActivityOnClose) {
		final Activity activity = this;

		final ModalDefaultTopView titleAndContentForDialog = new ModalDefaultTopView(activity, null);
		final ModalDefaultOneButtonBottomView oneButtonBottomView = new ModalDefaultOneButtonBottomView(activity, null);

		titleAndContentForDialog.setTitle(activity.getResources().getString(titleText));
		titleAndContentForDialog.setContent(activity.getResources().getString(bodyText));

		titleAndContentForDialog.showErrorIcon(true);
		oneButtonBottomView.setButtonText(R.string.close_text);

		titleAndContentForDialog.getHelpFooter().setToDialNumberOnClick(R.string.need_help_number_text);
		final ModalAlertWithOneButton errorModal = 
				new ModalAlertWithOneButton(activity, titleAndContentForDialog, oneButtonBottomView);

		if(finishActivityOnClose)
			errorModal.finishActivityOnClose(activity);

		oneButtonBottomView.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				modalIsPresent = false;
				errorModal.dismiss();
				if(finishActivityOnClose)
					goBack();
			}
		});

		errorModal.setOnCancelListener(new OnCancelListener() {			
			@Override
			public void onCancel(final DialogInterface dialog) {
				if(finishActivityOnClose)
					goBack();
				else
					errorModal.dismiss();			
			}
		});

		modalIsPresent = true;
		showCustomAlert(errorModal);

	}

	/**
	 * Present a modal error dialog over the current activity with a given title and body text. Can also close the
	 * current activity on close if needed.
	 * 
	 * @param titleText - the String resource to present in the title of the modal dialog.
	 * @param bodyText - the String resource to present in the body of the modal dialog.
	 * @param finishActivityOnClose - if passed as true, the activity that displays the modal 
	 * error will be finished when the modal is closed.
	 */
	protected void showErrorModalForRegistration(final int titleText, final int bodyText, final boolean finishActivityOnClose) {
		final Activity activity = this;

		final ModalDefaultTopView titleAndContentForDialog = new ModalDefaultTopView(activity, null);
		final ModalDefaultOneButtonBottomView oneButtonBottomView = new ModalDefaultOneButtonBottomView(activity, null);

		titleAndContentForDialog.setTitle(activity.getResources().getString(titleText));
		titleAndContentForDialog.setContent(activity.getResources().getString(bodyText));

		titleAndContentForDialog.showErrorIcon(true);
		oneButtonBottomView.setButtonText(R.string.close_text);

		titleAndContentForDialog.getHelpFooter().setToDialNumberOnClick(R.string.need_help_number_text);
		final ModalAlertWithOneButton errorModal = 
				new ModalAlertWithOneButton(activity, titleAndContentForDialog, oneButtonBottomView);

		if(finishActivityOnClose)
			errorModal.finishActivityOnClose(activity);

		oneButtonBottomView.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				modalIsPresent = false;
				errorModal.dismiss();
				if(finishActivityOnClose)
					goBack();
			}
		});

		errorModal.setOnCancelListener(new OnCancelListener() {			
			@Override
			public void onCancel(final DialogInterface dialog) {
				if(finishActivityOnClose)
					goBack();
				else
					errorModal.dismiss();
			}
		});

		modalIsPresent = true;
		errorModal.show();

	}
	/**
	 * Function to be implemented by subclasses to return to previous screen that opened
	 * the currently displayed screen.
	 * 
	 */
	public abstract void goBack();


	@Override
	public void showCustomAlert(final AlertDialog alert) {
		DiscoverModalManager.setActiveModal(alert);
		DiscoverModalManager.setAlertShowing(true);
		
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.show();
		alert.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	@Override
	public void showOneButtonAlert(final int title, final int content, final int buttonText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showDynamicOneButtonAlert(final int title, final String content,
			final int buttonText) {
		// TODO Auto-generated method stub

	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastError(final int errorCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * @return Return a reference to the current dialog being displayed over this activity.
	 */
	@Override
	public AlertDialog getDialog() {
		return mActiveDialog;
	}
	/**
	 * Allows to set the current dialog that is being displayed over this activity.
	 */
	@Override
	public void setDialog(final AlertDialog dialog) {
		mActiveDialog = dialog;
	}
	/**
	 * Closes the current dialog this is being displayed over this activity. Requires
	 * a call to setDialog to be able to use this function.
	 */
	@Override
	public void closeDialog() {
		if( mActiveDialog != null && mActiveDialog.isShowing()) {
			mActiveDialog.dismiss();
			mActiveDialog = null;
		}
	}
	/**
	 * Starts a Progress dialog using this activity as the context. The ProgressDialog created
	 * will be set at the active dialog.
	 */
	@Override
	public void startProgressDialog() {		
		if( mActiveDialog == null ) {
			mActiveDialog = ProgressDialog.show(this,"Discover", "Loading...", true);	
			setDialog(mActiveDialog);
		}
	}

}
