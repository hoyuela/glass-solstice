package com.discover.mobile.bank.deposit;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.BankPhoneAsyncCallbackBuilder;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.account.Account;
import com.discover.mobile.bank.services.deposit.DepositDetail;
import com.discover.mobile.bank.services.deposit.SubmitCheckDepositCall;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.error.ErrorHandler;
import com.google.common.base.Strings;

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
		submit();
	}
	
	@Override
	public void onBackPressed() {
		Toast.makeText(this, "Be Sure to Disable the Back Button!", Toast.LENGTH_LONG).show();  
		super.onBackPressed();
		finish();
	}
		
	/**
	 * Submit the check images and the information about the checks and what account to deposit to.
	 */
	private void submit() {
		final AsyncCallback<DepositDetail> callback = 
				BankPhoneAsyncCallbackBuilder.createDefaultCallbackBuilder(DepositDetail.class, this, this).build();
		final Bundle extras = this.getIntent().getExtras();
		if(extras != null){
			final Account account = (Account)extras.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
			final DepositDetail detail = new DepositDetail();
		
			detail.amount = extras.getInt(BankExtraKeys.AMOUNT);
			detail.account = Integer.parseInt(account.id);

			final TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			
			detail.deviceUUID = telephonyManager.getDeviceId();
			detail.deviceType = "Android";
		
			detail.frontImage = getCompressedImageFromPath(CheckDepositCaptureActivity.FRONT_PICTURE);
			detail.backImage = getCompressedImageFromPath(CheckDepositCaptureActivity.BACK_PICTURE);
			
			final SubmitCheckDepositCall call = new SubmitCheckDepositCall(this, callback, detail);
			call.submit();
		}
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

			if(pictureFile != null)
				decodedImage = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
			
			decodedImage.compress(Bitmap.CompressFormat.JPEG, jpegCompressionQuality, imageBitStream);
			base64Image.append(Base64.encodeToString(imageBitStream.toByteArray(), Base64.NO_WRAP));
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
		if(count >= bankImages.length)
			count = 0;
		
		bankImage.setImageDrawable(getResources().getDrawable(bankImages[count]));
	}
	
	/**
	 * An infinitely running AsyncTask that will animate the loading image in this Activity.
	 * @author scottseward
	 */
	protected class SecondTimer extends AsyncTask<Void, Void, Void> {
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
