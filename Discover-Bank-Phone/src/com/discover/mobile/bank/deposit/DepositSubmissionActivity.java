package com.discover.mobile.bank.deposit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;

/**
 * This activity handles the submission of checks to be deposited into a users account.
 * 
 * @author scottseward
 *
 */
public class DepositSubmissionActivity extends BaseActivity {
	/**The Debug TAG for this activity*/
	final String TAG = DepositSubmissionActivity.class.getSimpleName();
	/**An AsyncTask that handles changing the loading image every second.*/
	private SecondTimer timerAnimator = new SecondTimer();

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.deposit_submission);
	}
	
	@Override
	public void onBackPressed() {
		//Do nothing, this activity cannot be canceled by the user.
	}
	
	//Start the animator task.
	@Override
	public void onResume() {
		timerAnimator = new SecondTimer();
		timerAnimator.execute();
	}
	
	//Stop the animator task.
	@Override
	public void onPause() {
		super.onPause();
		timerAnimator.cancel(true);
	}
	
	@Override
	public ErrorHandler getErrorHandler() {
		return null;
	}
	
	/**An index value used for looping through all possible loading images */
	private int count = 0;
	protected void cycleBankImage() {
		/**The image to update with the AsyncTask*/
		final ImageView bankImage = (ImageView)findViewById(R.id.sending_deposit_loading_image);
		/**An array of drawable resources to be used for updating the loading image*/
		final int bankImages[] = {R.drawable.bank_fill_1, R.drawable.bank_fill_2, R.drawable.bank_fill_3};

		count++;
		if(count >= bankImages.length)
			count = 0;
		
		bankImage.setImageDrawable(getResources().getDrawable(bankImages[count]));
	}
	
	/**
	 * An infinitely running AsyncTask that will animate the loading image in this Activity.
	 * @author scottseward
	 *
	 */
	public class SecondTimer extends AsyncTask<Void, Void, Void> {
		final long oneSecondInMilliSeconds = 1000;
		@Override
		protected Void doInBackground(final Void... params) {
			try {
				Thread.sleep(oneSecondInMilliSeconds);
			} catch (final InterruptedException e) {
				Log.d(TAG, "Error sleeping thread for animation " + e);
			}
			return null;
		}
		
		/**
		 * Restart the timer and animation every time one finishes.
		 */
		@Override
		protected void onPostExecute(final Void result) {
			cycleBankImage();
			timerAnimator = new SecondTimer();
			timerAnimator.execute();
		}
		
	}


}
