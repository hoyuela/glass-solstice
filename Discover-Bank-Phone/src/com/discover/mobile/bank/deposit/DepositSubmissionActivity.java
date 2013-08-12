package com.discover.mobile.bank.deposit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Size;
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
import com.discover.mobile.bank.services.deposit.SubmitCheckDepositCall;
import com.discover.mobile.bank.services.error.BankErrorResponse;
import com.discover.mobile.bank.services.json.Money;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.utils.CommonUtils;
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

	/**Standard compression that will be used*/
	private static final int COMPRESSION = 30;

	/**Maximum compression that will be used if the images do not need to be compresses*/
	private static final int MAX_COMPRESSION = 100;

	/**Conversion to kb for the length*/
	private static final long KB_CONVERSION = 1000;

	/**Conversion to decimal for the compression*/
	private static final long PERCENTAGE_CONVERSION = 100;

	/**Thresh hold value that the check needs to be for compression*/
	private static final int HEIGHT_THRESH = 1600;
	private static final int WIDTH_THRESH = 1200;

	/**Analytics values*/
	private int frontImageHeight = 0;
	private int frontImageWidth = 0;
	private int frontImageCompressedSize = 0;
	private boolean isEqualOrAboveThresh = true;
	private int compression = -1;


	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.deposit_submission);
		CommonUtils.fixBackgroundRepeat(findViewById(R.id.main_layout));
		callingActivity = DiscoverActivityManager.getActiveActivity();
		DiscoverActivityManager.setActiveActivity(this);
		BankTrackingHelper.forceTrackPage(R.string.bank_capture_sending);

		submit();
	}

	/**
	 * Create a time stamp string
	 * @return the time stamp as a string value
	 */
	private String createTimeStamp() {
		final Date date = new Date();
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault() );
		return formatter.format(date).toString();
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
		setImageParams();
		final DepositDetail data = getDepositDetails();
		final SubmitCheckDepositCall call = BankServiceCallFactory.createSubmitCheckDepositCall(data, this);
		final Bundle bundle = new Bundle();
		bundle.putInt(BankTrackingHelper.TRACKING_IMAGE_HEIGHT, frontImageHeight);
		bundle.putInt(BankTrackingHelper.TRACKING_IMAGE_WIDTH, frontImageWidth);

		bundle.putString(BankTrackingHelper.TRACKING_IMAGE_SIZE, 
				String.valueOf(frontImageCompressedSize/KB_CONVERSION));
		bundle.putString(BankTrackingHelper.TRACKING_IMAGE_COMPRESSION, 
				String.valueOf(compression/PERCENTAGE_CONVERSION));

		bundle.putInt(BankTrackingHelper.TRACKING_IMAGE_AMOUNT, data.amount.value);
		bundle.putInt(BankTrackingHelper.TRACKING_IMAGE_ACCOUNT, data.account);
		bundle.putString(BankTrackingHelper.TRACKING_SUBMIT_TIME, createTimeStamp());
		call.setExtras(bundle);

		call.submit();
	}

	/**
	 * Setup the parameters of the images
	 */
	private void setImageParams() {
		final Camera camera = Camera.open();
		final Camera.Parameters parameters = camera.getParameters();
		final int maxImageWidth = 1600;
		final List<Size> sizes = parameters.getSupportedPictureSizes();

		Size smallCaptureSize = null;

		for(final Size size : sizes) {
			if(size.width <= maxImageWidth && smallCaptureSize == null) {
				smallCaptureSize = size;
			}
		}

		frontImageHeight = smallCaptureSize.height;
		frontImageWidth = smallCaptureSize.width;

		if(frontImageHeight < HEIGHT_THRESH && frontImageWidth < WIDTH_THRESH){
			isEqualOrAboveThresh = false;
		}else{
			isEqualOrAboveThresh = true;
		}

		camera.release();
	}

	/**
	 * Get the deposit details that will be sent to the server
	 * @return the deposit details
	 */
	private DepositDetail getDepositDetails() {
		final Bundle extras = getIntent().getExtras();
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
				compression = isEqualOrAboveThresh ? COMPRESSION : MAX_COMPRESSION;
				decodedImage.compress(Bitmap.CompressFormat.JPEG, compression, imageBitStream);
			} else{
				Log.e(TAG, "Error : Could not compress decoded image!");
			}

			//If this was the front check get the size of the image
			if(path.equals(CheckDepositCaptureActivity.FRONT_PICTURE)){
				frontImageCompressedSize = imageBitStream.toByteArray().length;
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
		//If the sender was a check deposit call make sure it was tracked
		if(sender instanceof SubmitCheckDepositCall){
			//If the call was a success, get the right info
			if(result instanceof DepositDetail){
				final DepositDetail data = (DepositDetail) result;
				final Bundle extras = ((SubmitCheckDepositCall) sender).getExtras();
				extras.putInt(BankTrackingHelper.TRACKING_IMAGE_RESPONSE_CODE, data.responseCode);

				//If the call was a failure, get the right info
			}else if (result instanceof BankErrorResponse){
				final Bundle bundle = ((SubmitCheckDepositCall) sender).getExtras();
				bundle.putInt(BankTrackingHelper.TRACKING_IMAGE_RESPONSE_CODE, 
						((BankErrorResponse)result).getHttpStatusCode());
				bundle.putString(BankTrackingHelper.TRACKING_IMAGE_RESPONSE_JSON, 
						((BankErrorResponse)result).getErrorCode());
			}
			BankTrackingHelper.trackDepositSubmission(((SubmitCheckDepositCall) sender).getExtras());
		}

		DiscoverActivityManager.setActiveActivity(callingActivity);
		finish();

		final Bundle extras = getIntent().getExtras();
		final Account account = (Account)extras.getSerializable(BankExtraKeys.DATA_LIST_ITEM);
		BankServiceCallFactory.createGetAccountLimits(account, true).submit();
	}

	@Override
	public void startProgressDialog() {		
		//do nothing
	} 

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Reload the XML layout to switch between portrait/landscape version.
		setContentView(R.layout.deposit_submission);
	}
}
