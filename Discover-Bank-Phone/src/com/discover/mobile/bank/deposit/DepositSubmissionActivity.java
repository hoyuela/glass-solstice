package com.discover.mobile.bank.deposit;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.discover.mobile.analytics.BankTrackingHelper;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.framework.BankServiceCallFactory;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.deposit.DepositDetail;
import com.discover.mobile.bank.services.json.Money;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.google.common.base.Strings;


/**
 * This activity handles the submission of checks to be deposited into a users account.
 * 
 * @author scottseward
 *
 */
public class DepositSubmissionActivity extends BaseActivity implements CompletionListener {
	/**The Debug TAG for this activity*/
	private static final String TAG = DepositSubmissionActivity.class.getSimpleName();

	/**An AsyncTask that handles changing the loading image every second.*/
	private SecondTimer timerAnimator = new SecondTimer();

	/**A reference to the Activity that launched this Activity */
	private Activity callingActivity = null;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.deposit_submission);
		callingActivity = DiscoverActivityManager.getActiveActivity();
		DiscoverActivityManager.setActiveActivity(this);
		BankTrackingHelper.forceTrackPage(R.string.bank_capture_sending);
		submit();
	}

	@Override
	public void onBackPressed() {
		/**
		 * Do nothing because we want the back button to be disabled for this activity.
		 */
	}

	/**
	 * Submit the check images and the information about the checks and what account to deposit to.
	 */
	private void submit() {
		BankServiceCallFactory.createSubmitCheckDepositCall(getDepositDetails(), this).submit();
	}

	private DepositDetail getDepositDetails() {
		final Bundle extras = this.getIntent().getExtras();
		Account account = null;
		DepositDetail detail = null;

		if(extras != null){
			account = (Account)extras.getSerializable(BankExtraKeys.DATA_LIST_ITEM);

			if(account != null){
				detail = new DepositDetail();
				detail.amount = new Money();
				detail.amount.value = extras.getInt(BankExtraKeys.AMOUNT);
				detail.account = Integer.parseInt(account.id);		

				detail.frontImage = getCompressedImageFromPath(CheckDepositCaptureActivity.FRONT_PICTURE);
				detail.backImage = getCompressedImageFromPath(CheckDepositCaptureActivity.BACK_PICTURE);
			}else {
				Log.e(TAG, "Cannot create check deposit server call, Account is missing!");
			}
		}else {
			Log.e(TAG, "Cannot create check deposit server call, Bundle was null!");
		}
		return detail;
	}

	/**
	 * Returns a compressed JPEG image that is base64 encoded given a path name to an image.
	 * @param path the path of an image to decode and compress.
	 * @return a base64 encoded image that has been JPEG encoded and compressed.
	 */
	private String getCompressedImageFromPath(final String path) {
		final StringBuilder base64Image = new StringBuilder();
		base64Image.append("");
		final int jpegCompressionQuality = 40;

		if(!Strings.isNullOrEmpty(path)) {
			final ByteArrayOutputStream imageBitStream = new ByteArrayOutputStream();
			Bitmap decodedImage = null;

			final File pictureFile = getFileStreamPath(path);

			if(pictureFile != null) {
				decodedImage = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
			} else {
				Log.e(TAG, "Error : Could Not Decode image from file path!");
			}

			if(decodedImage != null) {
				decodedImage.compress(Bitmap.CompressFormat.JPEG, jpegCompressionQuality, imageBitStream);
			} else {
				Log.e(TAG, "Error : Could not compress decoded image!");
			}

			base64Image.append(Base64.encodeToString(imageBitStream.toByteArray(), Base64.NO_WRAP));

			if(Strings.isNullOrEmpty(base64Image.toString())) {
				Log.e(TAG, "Error : Compressed Image is Empty!");
			}
		}

		return base64Image.toString();
	}

	//Start the animator task.
	@Override
	public void onResume() {
		super.onResume();
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
		if(count >= bankImages.length) {
			count = 0;
		}

		bankImage.setImageDrawable(getResources().getDrawable(bankImages[count]));
	}

	/**
	 * An infinitely running AsyncTask that will animate the loading image in this Activity.
	 * @author scottseward
	 */
	protected class SecondTimer extends AsyncTask<Void, Void, Void> {
		private static final long ONE_SECOND_IN_MILLISECONDS = 1000;
		@Override
		protected Void doInBackground(final Void... params) {
			try {
				Thread.sleep(ONE_SECOND_IN_MILLISECONDS);
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

	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.FIRST;
	}

	@Override
	public void complete(final NetworkServiceCall<?> sender, final Object result) {
		DiscoverActivityManager.setActiveActivity(callingActivity);
		finish();

		final Bundle extras = this.getIntent().getExtras();
		final Account account = (Account)extras.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		BankServiceCallFactory.createGetAccountLimits(account, true).submit();
	}

	@Override
	public void startProgressDialog(boolean isProgressDialogCancelable) {		
		//do nothing
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Reload the XML layout to switch between portrait/landscape version.
        setContentView(R.layout.deposit_submission);
    }

}
