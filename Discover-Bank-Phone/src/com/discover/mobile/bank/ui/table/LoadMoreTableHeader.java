package com.discover.mobile.bank.ui.table;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.widgets.StatusMessageView;
import com.discover.mobile.common.utils.StringUtility;

public class LoadMoreTableHeader extends RelativeLayout {

	final List<ToggleButton> toggleButtons = new ArrayList<ToggleButton>();
	private int currentSelectedButtonIndex = -1;

	public LoadMoreTableHeader(final Context context) {
		super(context);
		inflateLayout();
	}
	
	public LoadMoreTableHeader(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		inflateLayout();
	}
	
	public LoadMoreTableHeader(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		inflateLayout();
	}
	
	private void inflateLayout() {
		final View inflatedLayout = LayoutInflater.from(getContext()).inflate(R.layout.load_more_header, null);
		
		this.addView(inflatedLayout);
	}
	
	public final int getSelectedButtonIndex() {
		return currentSelectedButtonIndex;
	}
	
	public List<ToggleButton> getButtons() {
		return toggleButtons;
	}
	
	public void setCustomMessage(final String message) {
		final StatusMessageView statusView = (StatusMessageView)this.findViewById(R.id.status);
		if(statusView != null) {
			statusView.setText(message);
			statusView.setTextBold(false);
		}
	}
	
	public void hideSecondaryMessage() {
		final TableTitles titles = (TableTitles)findViewById(R.id.table_titles);
		if(titles != null) {
			titles.hideMessage();
		}
	}
	
	public void showMessage() {
		final StatusMessageView statusView = (StatusMessageView)this.findViewById(R.id.status);
		if(statusView != null) {
			statusView.setVisibility(View.VISIBLE);
			hideErrorIcon();
		}
	}
	
	private void hideErrorIcon() {
		final StatusMessageView statusView = (StatusMessageView)this.findViewById(R.id.status);
		if(statusView != null) {
			statusView.hideErrorIcon();
		}
	}
	
	public void hideMessage() {
		final StatusMessageView statusView = (StatusMessageView)this.findViewById(R.id.status);
		if(statusView != null) {
			statusView.setVisibility(View.GONE);
		}
	}
	
	public final ToggleButton getSelectedButton() {
		ToggleButton selectedButton = null;
		if(toggleButtons != null && isIndexValid(currentSelectedButtonIndex)) {
			selectedButton = toggleButtons.get(currentSelectedButtonIndex);
		}
		return selectedButton;
	}
	
	public final void addButtonWithClickListener(final String buttonText, final OnClickListener clickListener) {
		new ButtonAdder(buttonText, clickListener).execute();
	}
	
	public final void addButton(final String buttonText) {
		addToggleButton(buttonText);
	}
	
	public final void addToggleButton(final String buttonText) {
		new ButtonAdder(buttonText, null).execute();
	}
	
	public final void setSelectedButton(final int index) {
		if(toggleButtons != null && isIndexValid(index)) {
			//Disable any currently selected buttons
			unpressCurrentButton();
			pressNewButton(index);
		}
	}
	
	private void pressNewButton(final int index) {
		final ToggleButton newlySelectedButton = toggleButtons.get(index);
		if(newlySelectedButton != null) {
			pressButton(newlySelectedButton);
			currentSelectedButtonIndex = index;
		}
	}
	
	private void unpressCurrentButton() {
		if(isIndexValid(currentSelectedButtonIndex)) {
			final ToggleButton oldButton = toggleButtons.get(currentSelectedButtonIndex);
			if(oldButton != null) {
				unpressButton(oldButton);				
			}else { //uncheck all buttons.
				for(final ToggleButton toggle : toggleButtons) {
					if(toggle.isChecked()) {
						unpressButton(toggle);
					}
				}
			}
		}
	}
	
	private void pressButton(final ToggleButton button) {
		button.setChecked(true);
		button.setPressed(true);
		button.setSelected(true);
		button.setTextColor(getResources().getColor(R.color.white));
	}
	
	private void unpressButton(final ToggleButton button) {
		button.setChecked(false);
		button.setPressed(false);
		button.setSelected(false);
		button.setTextColor(getResources().getColor(R.color.body_copy));
	}
	
	/**
	 * 
	 * @param index
	 * @return if the given index value will likely map to a valid button.
	 */
	private boolean isIndexValid(final int index) {
		return toggleButtons != null &&  toggleButtons.size() > 0 && index >= 0 && index < toggleButtons.size();
	}
	
	private void distributeButtonWeights() {
		final float weightSum = 1.0f;
		final int numOfButtons = toggleButtons.size();
		
		final float buttonWeight = weightSum/numOfButtons;
		
		final LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, buttonWeight);
		
		for(final ToggleButton button : toggleButtons) {
			button.setLayoutParams(params);
		}
	}
	
	private final class ButtonAdder extends AsyncTask<Void,Void,Void> {
		private String buttonText = StringUtility.EMPTY;
		private OnClickListener userProvidedClickListener = null;
		
		public ButtonAdder(final String buttonText, final OnClickListener clickListener) {
			this.buttonText = buttonText;
			this.userProvidedClickListener = clickListener;
		}
		
		@Override
		protected Void doInBackground(final Void... params) {
			final ToggleButton button = new ToggleButton(getContext());
			
			button.setTextOn(buttonText);
			button.setTextOff(buttonText);
			button.setText(buttonText);
			button.setTextColor(getResources().getColor(R.color.body_copy));
			
			final int currentButtonIndex = toggleButtons.size();
			toggleButtons.add(button);

			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(final View v) {
					if(v instanceof ToggleButton) {
						final boolean didNotSelectSameButton = getSelectedButtonIndex() != currentButtonIndex;
						if(didNotSelectSameButton) {
							setSelectedButton(currentButtonIndex);
							toggleTextColor((ToggleButton)v);
							if(userProvidedClickListener != null) {
								userProvidedClickListener.onClick(v);
							}
						}else {
							pressButton(button);
						}
					}
				}
			});

			configureButtonAppearances();
			distributeButtonWeights();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(final Void result) {
			final LinearLayout buttonRow = (LinearLayout)findViewById(R.id.button_row);
			buttonRow.removeAllViews();
			for(final ToggleButton button : toggleButtons) {
				buttonRow.addView(button);
			}
			
			this.cancel(true);
		}
		
		private void configureButtonAppearances() {
			final int numOfButtons = toggleButtons.size();
			
			//Set the default text color and background Drawable for every button.
			for(int i = 0; i < numOfButtons; ++i) {
				final ToggleButton currentButton = toggleButtons.get(i);
				setButtonImage(currentButton, i);								
			}
		}
		
		private void setButtonImage(final ToggleButton button, final int position) {
			final Resources res = getResources();
			final int maxPosition = toggleButtons.size() - 1;
			
			//Handle one button.
			if(maxPosition == 0) {
				button.setBackgroundDrawable(res.getDrawable(R.drawable.toggle_button_single_selector));
			} else { //Handle more than one button.
				if(position == 0) {
					button.setBackgroundDrawable(res.getDrawable(R.drawable.toggle_button_left_selector));
				}else if (position == maxPosition) {
					button.setBackgroundDrawable(res.getDrawable(R.drawable.toggle_button_right_selector));
				}else {
					button.setBackgroundDrawable(res.getDrawable(R.drawable.toggle_button_middle_selector));
				}
			}
		}
		
		private void toggleTextColor(final ToggleButton button) {
			if(button.isChecked()) {
				button.setTextColor(getResources().getColor(R.color.white));
			}else {
				button.setTextColor(getResources().getColor(R.color.body_copy));
			}
		}
	}

}
