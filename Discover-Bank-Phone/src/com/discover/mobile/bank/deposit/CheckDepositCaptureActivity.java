package com.discover.mobile.bank.deposit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.analytics.BankTrackingHelper;
import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.bank.ui.Animator;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.error.ErrorHandler;

public class CheckDepositCaptureActivity extends BaseActivity implements SurfaceHolder.Callback, AnimationListener {
	private static final String TAG = CheckDepositCaptureActivity.class.getSimpleName();

	public static final int RETAKE_FRONT = 1;
	public static final int RETAKE_BACK  = 2;
	public static final int THREE = 3;
	public static final String FRONT_PICTURE = "frontCheckImage";
	public static final String BACK_PICTURE = "backCheckImage";

	//BUTTONS
	private Button captureButton;
	private Button retakeButton;
	private ImageView closeButton;
	private ImageView helpButton;

	//BOUNDARY INDICATORS
	private ImageView bracketTopRight;
	private ImageView bracketBottomRight;
	private ImageView bracketBottomLeft;
	private ImageView bracketTopLeft;

	//COUNTDOWN IMAGE
	private ImageView countdownLogo;

	//TEXT LABELS
	private TextView frontLabel;
	private TextView backLabel;
	private TextView captureHelpTextView;

	//BREADCRUMB CHECKMARKS
	private ImageView stepOneCheck;
	private ImageView stepTwoCheck;

	//CAMERA
	private SurfaceView cameraPreview;
	private SurfaceHolder previewHolder;
	private Camera camera;
	private CameraCountdownTask timerTask;

	//OTHER
	private Drawable countdownThree;
	private Drawable countdownTwo;
	private Drawable countdownOne;

	//AUTO CAPTURE TIPS
	private ImageView tips;

	/** Length of time the tips will appear */
	private final int LIFESPAN = 5000;

	/** Animation Duration */
	private final int ANIMATIONDURATION = 1000;

	private int count = THREE;

	private boolean shouldResizeImage = false;
	private Size bestCameraSize;

	private boolean cameraConfigured = false;
	private boolean isPaused = false;
	private OrientationEventListener orientationListener = null;

	/**
	 * Setup the Activity. Loads all UI elements to local references and starts camera setup.
	 * @param savedInstanceState
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.check_deposit_capture);

		loadViews();
		getWindow().setFormat(PixelFormat.UNKNOWN);

		//This deprecated call is needed to support API 10 devices.
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		loadDrawables();
		setupButtons();
		setupCameraForRetake();
		cameraPreview.setOnClickListener(autoFocusClickListener);
		orientationListener = createOrientationListener();
	}

	/**
	 * Show the capture tips layout
	 */
	private void showCaptureTips() {
		tips.setVisibility(View.VISIBLE);

		//After causing the view to appear count out 5 seconds than disappear.
		final Handler fadeThread = new Handler();
		fadeThread.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (View.VISIBLE == tips.getVisibility()) {
					fadeTipsLayout();
				}	
			}
		}, LIFESPAN);
	}

	/**
	 * Fade out the tips layout1
	 */
	protected void fadeTipsLayout(){
		final Animation fade = Animator.createFadeOutAnimation(ANIMATIONDURATION);
		fade.setAnimationListener(CheckDepositCaptureActivity.this);
		tips.startAnimation(fade);
	}

	/**
	 * Hide a view that was clicked on
	 * @param v - view to hide
	 */
	public void closeLayout(final View v){
		v.setVisibility(View.GONE);
	}

	/**
	 * Create the orientation changed listener, this will attempt to force the orientation into landscape mode
	 * if the orientation of the activity is changed.
	 * @return the orientation changed listener
	 */
	public OrientationEventListener createOrientationListener() {
		final OrientationEventListener ret = 
				new OrientationEventListener(getContext(), SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(final int arg0) {
				final int requestedOrientation = 
						CheckDepositCaptureActivity.this.getResources().getConfiguration().orientation;

				if(!(requestedOrientation == 
						Configuration.ORIENTATION_LANDSCAPE)) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

				}
			}
		};

		return ret;  
	}

	/**
	 * Start the listener for orientation change.
	 */
	private void startOrientationListener() {
		if(orientationListener == null) {
			orientationListener = createOrientationListener();
		}
		orientationListener.enable();
	}

	/**
	 * Stop the listener for orientation change.
	 */
	private void stopOrientationListener() {
		if(orientationListener != null) {
			orientationListener.disable();
		}
	}
	/**
	 * On resume of the Activity, get the camera ready to use.
	 */
	@Override
	public void onResume() {
		isPaused = false;
		super.onResume();
		startOrientationListener();

		//Check to see if this is the users first time in the capture activity
		if(!Globals.isUsersFirstTimeInDepositCapture(this)){
			showCaptureTips();
			//Update the settings so that they reflect that the user has been here
			Globals.setUserHasBeenInDepositCapture(this);
		}
		//make sure the close button is visible
		if(closeButton.getVisibility() == View.INVISIBLE) {
			closeButton.setVisibility(View.VISIBLE);
			setupButtons();
		}
	}

	/**
	 * When this Activity gets paused, release control of the camera.
	 */
	@Override
	public void onPause() {
		stopOrientationListener();
		orientationListener.disable();

		isPaused = true;	

		//Check to see if onPause was called because the activity is being finished
		if( !isFinishing() ) {
			//Reset the capture image if the user pauses the fragment during countdown or 
			//before they press confirm capture.
			if(retakeButton.getVisibility() == View.VISIBLE) {
				retakeClickListener.onClick(null);
				captureHelpTextView.setVisibility(View.GONE);
			}else if (!captureButton.isClickable()) {
				setupButtons();
			}
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onPause();
	}


	/**
	 * If the async task is not finished when the activity stops, we need to cancel it.
	 */
	@Override
	public void onStop() {
		super.onStop();

		if(timerTask != null) {
			timerTask.cancel(true);
		}

		resetCountdown();
	}

	/**
	 * Setup all local references to UI elements.
	 */
	private void loadViews() {
		cameraPreview = (SurfaceView)findViewById(R.id.camera_surface);
		countdownLogo = (ImageView)findViewById(R.id.countdown_logo);
		previewHolder = cameraPreview.getHolder();		
		previewHolder.addCallback(this);

		bracketTopLeft = (ImageView)findViewById(R.id.image_bracket_top_left);
		bracketTopRight = (ImageView)findViewById(R.id.image_bracket_top_right);
		bracketBottomLeft = (ImageView)findViewById(R.id.image_bracket_bottom_left);
		bracketBottomRight = (ImageView)findViewById(R.id.image_bracket_bottom_right);

		stepOneCheck = (ImageView)findViewById(R.id.front_check);
		stepTwoCheck = (ImageView)findViewById(R.id.back_check);

		closeButton = (ImageView)findViewById(R.id.close_button);
		helpButton  = (ImageView)findViewById(R.id.help_button);

		frontLabel = (TextView)findViewById(R.id.front_label);
		backLabel = (TextView)findViewById(R.id.back_label);

		captureButton = (Button)findViewById(R.id.capture_button);
		retakeButton = (Button)findViewById(R.id.retake_button);
		tips = (ImageView) findViewById(R.id.capturetips);

		helpButton.setClickable(true);
		captureHelpTextView = (TextView) findViewById(R.id.help_text_wrapper);
	}

	/**
	 * Setup the camera to retake a specified image.
	 * This specified image comes from a provided integer passed as a Bundle extra.
	 */
	private void setupCameraForRetake() {
		final Bundle extras = getIntent().getExtras();
		if(extras != null){
			setupPictureRetake(extras.getInt(BankExtraKeys.RETAKE_PICTURE));
		}
	}

	/**
	 * Deletes the front check image from storage.
	 * @param context the calling context.
	 * @return if the image was deleted.
	 */
	public static boolean deleteFrontImage(final Context context) {
		return deleteImage(FRONT_PICTURE, context);
	}

	/**
	 * Deletes the back check image from storage.
	 * @param context the calling context.
	 * @return if the image was deleted.
	 */
	public static boolean deleteBackImage(final Context context) {
		return deleteImage(BACK_PICTURE, context);
	}

	/**
	 * Finds the absolute path to an imageName and then attempts to delete that image. 
	 * @param imageName the name of an an image to delete
	 * @param context the calling context
	 * @return if the image was deleted.
	 */
	private static boolean deleteImage(final String imageName, final Context context){
		return context.deleteFile(imageName);
	}

	/**
	 * Attempts to delete both check images, one after the other.
	 * @param context the calling context.
	 * @return if both images were deleted.
	 */
	public static boolean deleteBothImages(final Context context) {
		boolean bothImagesDeleted = true;
		bothImagesDeleted &= deleteBackImage(context);
		bothImagesDeleted &= deleteFrontImage(context);

		return bothImagesDeleted;
	}

	/**
	 * Load the drawables that are used for the countdown timer.
	 */
	private void loadDrawables() {
		final Resources res = getResources();
		countdownThree = res.getDrawable(R.drawable.chckdep_3);
		countdownTwo = res.getDrawable(R.drawable.chckdep_2);
		countdownOne = res.getDrawable(R.drawable.chckdep_1);
	}

	/**
	 * Setup the button on the screen.
	 */
	private void setupButtons() {
		retakeButton.setOnClickListener(retakeClickListener);
		setDefaultButtons();
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if(!isStepOneChecked() || !isStepTwoChecked()) {
					clearImageCacheIfNotRetaking();
					setResult(RESULT_CANCELED);    
					finish();
				}
			}
		});


		helpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if(timerTask == null || !(AsyncTask.Status.RUNNING == timerTask.getStatus())){
					showCaptureTips();
				}
			}
		});
	}

	/**
	 * Clears the image cache if the user is not re-taking an image
	 */
	private void clearImageCacheIfNotRetaking() {
		final Bundle extras = getIntent().getExtras();
		int retakeValue = 0;
		if(extras != null) {
			retakeValue = extras.getInt(BankExtraKeys.RETAKE_PICTURE);
		}

		/**
		 * If the user was not re-taking an image we can delete the cache.
		 */
		if(retakeValue < 1) {
			deleteBothImages(this);
		}
	}

	/**
	 * Sets up the front/back breadcrumb trail to display either the front or back
	 * as already been taken if we are retaking an image.
	 * @param retakeValue
	 */
	private void setupPictureRetake(final int retakeValue) {
		if(retakeValue == RETAKE_FRONT){
			stepTwoCheck.setVisibility(View.VISIBLE);
		}else if(retakeValue == RETAKE_BACK){
			setNextCheckVisible();
			goToNextStep();
		}
	}

	/**
	 * Hide the check boundary indicators.
	 */
	private void hideImageBrackets() {
		setImageBracketVisibility(View.INVISIBLE);
	}

	/**
	 * Show the check boundary indicators
	 */
	private void showImageBrackets() {
		setImageBracketVisibility(View.VISIBLE);
	}

	/**
	 * Set the check boundary indicators to a given visibility.
	 * @param visibility the visibility to set the check boundary indicators to.
	 */
	private void setImageBracketVisibility(final int visibility) {
		bracketTopLeft.setVisibility(visibility);
		bracketTopRight.setVisibility(visibility);
		bracketBottomRight.setVisibility(visibility);
		bracketBottomLeft.setVisibility(visibility);
	}

	/**
	 * An OnClickListener that calls the camera's auto focus feature.
	 */
	private final OnClickListener autoFocusClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			if(timerTask == null || !(timerTask.getStatus() == AsyncTask.Status.RUNNING)) {
				focusCamera();
			}
		}
	};

	/**
	 * An OnTouchListener for the capture button.
	 * This touch listener will make the capture button stay pressed upon touch,
	 * and initiate an image to be taken.
	 */
	private final OnTouchListener captureTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(final View v, final MotionEvent event) {
			if( !(isStepOneChecked() && isStepTwoChecked()) ) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					return true;
				}
				if(event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}
				beginCaptureProcess();
			}
			return true;		    
		}
	};

	/**
	 * Starts the process of capturing an image.
	 */
	private void beginCaptureProcess() {
		captureButton.setPressed(true);   
		disableClickListeners();
		startCountdownTimer();
		recordAnalyticsPictureTaking();
	}

	/**
	 * Record the analytics data for this section of the application.
	 */
	private void recordAnalyticsPictureTaking() {

		//Track the taking of pictures
		if(!isStepOneChecked()){
			BankTrackingHelper.forceTrackPage(R.string.bank_capture_front);
		}else if(!isStepTwoChecked()){
			BankTrackingHelper.forceTrackPage(R.string.bank_capture_back);
		}
	}

	/**
	 * Disable the  click listeners for the touch to auto focus and capture button.
	 */
	private void disableClickListeners() {
		captureButton.setOnClickListener(null);
		captureButton.setClickable(false);
		cameraPreview.setOnClickListener(null);
	}	            

	/**
	 * Starts the count down timer.
	 */
	private void startCountdownTimer() {
		if(timerTask == null || !(AsyncTask.Status.RUNNING == timerTask.getStatus())){
			count = THREE;
			timerTask = new CameraCountdownTask();
			timerTask.execute();
		}
	}

	private boolean isStepOneChecked() {
		return stepOneCheck.getVisibility() == View.VISIBLE;
	}

	private boolean isStepTwoChecked() {
		return stepTwoCheck.getVisibility() == View.VISIBLE;
	}
	/**
	 * This is the click listener for the confirm button that is shown directly after an image is taken.
	 * This listener will advance the breadcrumb element to the next position and reset the camera
	 * preview so that the next picture can be taken. It also resets the buttons back to their default state.
	 */
	private final OnClickListener confirmClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			saveLastConfirmedImage();
			goToNextStep();
			//If we are not yet done with capturing images, setup the camera for another picture.
			if(!isStepTwoChecked()){
				resetCamera();
				setDefaultButtons();
				showImageBrackets();
			}
		}
	};


	@Override
	public void onBackPressed() {
		if(View.VISIBLE == tips.getVisibility()){
			fadeTipsLayout();
		}else{
			clearImageCacheIfNotRetaking();
			super.onBackPressed();
		}
	}

	/**
	 * This is the click listener for the retake button. It resets the camera preview and sets up the 
	 * buttons on the screen to their default state.
	 */
	private final OnClickListener retakeClickListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			resetCamera();
			setDefaultButtons();
			showImageBrackets();
			cameraPreview.setOnClickListener(autoFocusClickListener);
			resetCurrentCheckMark();
		}
	};

	/**
	 * Resets the camera so that it is ready to take a new picture.
	 */
	private void resetCamera() {
		camera.stopPreview();
		camera.startPreview();
	}

	/**
	 * Advances the breadcrumb to the next selection, like from Front to Back.
	 */
	private void goToNextStep() {
		if(isStepTwoChecked()){
			this.setResult(Activity.RESULT_OK);
			finish();
		}else if(isStepOneChecked()){
			frontLabel.setTextColor(getResources().getColor(R.color.field_copy));
			backLabel.setTextColor(getResources().getColor(R.color.sub_copy));
			setDefaultButtons();
		}
	}

	/**
	 * Sets the next check mark to be visible, starting from left to right.
	 */
	private void setNextCheckVisible() {

		if(!isStepOneChecked()){
			stepOneCheck.setVisibility(View.VISIBLE);
		}else if(!isStepTwoChecked()) {
			stepTwoCheck.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * The reverse of setNextCheckVisible. This removes one check from right to left.
	 */
	private void resetCurrentCheckMark() {
		final Bundle extras = getIntent().getExtras();
		int retakeValue = 0;

		if(extras != null) {
			retakeValue = extras.getInt(BankExtraKeys.RETAKE_PICTURE);
		}

		if(retakeValue == RETAKE_FRONT){
			stepOneCheck.setVisibility(View.INVISIBLE);
		} else if(isStepTwoChecked()) {
			stepTwoCheck.setVisibility(View.INVISIBLE);
		} else if(isStepOneChecked()) {
			stepOneCheck.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * Reset the countdown timer.
	 * Hide the contdown logo, reset its image, and reset the counter.
	 */
	private void resetCountdown() {
		countdownLogo.setVisibility(View.GONE);
		countdownLogo.setImageDrawable(countdownThree);
	}

	/**
	 * This PictureCallback is used by the camera to store the resulting picture
	 * as a JPEG. It also does some UI setup for the check preview such as,
	 * hiding the check brackets, showing the confirmation buttons and setting
	 * the next check mark to visible.
	 */
	private Bitmap lastPicture = null;
	private final PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(final byte[] data, final Camera camera) {
			setPictureConfirmationButtons();
			lastPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
			playShutterSound();
			hideImageBrackets();
			setNextCheckVisible();
			cameraPreview.setOnClickListener(null);
		}
	};

	/**
	 * Saves the image in the local lastPicture byte array to the device.
	 */
	private void saveLastConfirmedImage() {
		//Write the image to disk.
		try {
			if(lastPicture != null) {
				final int fullQuality = 100;
				final FileOutputStream fos = getFileOutputStream();
				if(shouldResizeImage){
					final int maxImageWidth = 
							Integer.valueOf(DiscoverActivityManager.getString(R.string.bank_deposit_maximum_width));
					final int newHeight = 
							MCDUtils.getAdjustedImageHeight(bestCameraSize.height, bestCameraSize.width, maxImageWidth);
					Bitmap.createScaledBitmap(lastPicture, maxImageWidth, newHeight, true)
					.compress(Bitmap.CompressFormat.JPEG, fullQuality, fos);
				}else{
					lastPicture.compress(Bitmap.CompressFormat.JPEG, fullQuality, fos);
				}
				fos.close();
			}
		} catch (final FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (final IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}

	/**
	 * Setup the buttons on the screen to be confirmation buttons.
	 * The confirmation button changes its text and left drawable, along with its click functionality.
	 * The retake button is also shown.
	 */
	private void setPictureConfirmationButtons() {
		captureButton.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.chckdep_checkmark_white), null, null, null);
		captureButton.setText(R.string.picture_confirm);
		retakeButton.setVisibility(View.VISIBLE);
		captureButton.setOnClickListener(confirmClickListener);
		captureButton.setOnTouchListener(null);
		captureButton.setPressed(false);
		captureButton.setClickable(true);
		closeButton.setVisibility(View.VISIBLE);
		captureHelpTextView.setVisibility(View.GONE);
	}

	/**
	 * Plays a default shutter sound provided by the android operating system.
	 */
	private void playShutterSound() {
		final AudioManager meng = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
		final int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);
		final Uri soundResource = Uri.parse(getResources().getString(R.string.shutter_sound_file));
		MediaPlayer shutterPlayer = null;

		if (volume > 0) {
			if (shutterPlayer == null) {
				shutterPlayer = MediaPlayer.create(getContext(), soundResource);
			}

			if (shutterPlayer != null) {
				shutterPlayer.start();
			}
		}
	}

	/**
	 * Setup the buttons on the screen to be in their default state.
	 * Reset the click listeners, text, and drawables. Hide the retake button.
	 */
	private void setDefaultButtons() {
		captureButton.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.chckdep_camera_icon), null, null, null);
		captureButton.setText(R.string.capture);
		retakeButton.setVisibility(View.INVISIBLE);
		captureButton.setOnTouchListener(captureTouchListener);
		captureButton.setPressed(false);
		captureButton.setClickable(true);
		cameraPreview.setOnClickListener(autoFocusClickListener);
		captureHelpTextView.setVisibility(View.VISIBLE);
	}

	/** 
	 * Create a File for saving the image.
	 * Determines what name to use when saving the file based on the visibility of the check marks next to the
	 * front and back toggle so that the front or back image file stream is returned.
	 */
	private FileOutputStream getFileOutputStream() {
		FileOutputStream fos = null;
		int retakePicture = 0;

		final Bundle extras = getIntent().getExtras();
		if(extras != null) {
			retakePicture = extras.getInt(BankExtraKeys.RETAKE_PICTURE);
		}

		try {
			if(retakePicture == RETAKE_FRONT) { 
				fos = openFileOutput(FRONT_PICTURE, Context.MODE_PRIVATE);
			}
			else if (isStepOneChecked() && isStepTwoChecked()){
				fos = openFileOutput(BACK_PICTURE, Context.MODE_PRIVATE);
			} else {
				fos = openFileOutput(FRONT_PICTURE, Context.MODE_PRIVATE);
			}
		} catch (final FileNotFoundException e) {
			Log.e(TAG, "Cannot find file : " + e);
		}

		return fos;	
	}

	private final Camera.AutoFocusCallback focusAndCaptureCallback = new Camera.AutoFocusCallback() {

		@Override
		public void onAutoFocus(final boolean success, final Camera camera) {
			//Cancel auto focus is called because if not, the flash may stay on.
			camera.cancelAutoFocus();
			camera.takePicture(null, null, mPicture);
		}
	};

	/**
	 * Call the camera's auto focus method then take a picture once it is done.
	 */
	private void focusThenTakePicture() {
		if(camera != null) {
			camera.autoFocus(focusAndCaptureCallback);
		}
	}

	/**
	 * Call the camera's auto focus method.
	 */
	private void focusCamera() {
		camera.autoFocus(autoFocusCallback);
	}

	private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {

		@Override
		public void onAutoFocus(final boolean success, final Camera camera) {
			//Cancel auto focus is called because if not, the flash may stay on.
			camera.cancelAutoFocus();
		}
	};

	/**
	 * Setup the parameters for the camera that might be useful.
	 */
	private void setupCameraParameters() {
		final int maxImageWidth = Integer.valueOf(DiscoverActivityManager.getString(R.string.bank_deposit_maximum_width));
		final int maxJpegQuality = 100;
		final Camera.Parameters parameters = camera.getParameters();
		final List<Size> sizes = parameters.getSupportedPictureSizes();
		//Set the best image size
		bestCameraSize = MCDUtils.getBestImageSize(sizes, maxImageWidth);

		//If the image is larger than 1600 set the boolean shouldResize to true
		if(bestCameraSize.width > maxImageWidth){
			shouldResizeImage = true;
		}else{
			shouldResizeImage = false;
		}

		//Set the camera parameters
		if(bestCameraSize != null) {
			parameters.setPictureSize(bestCameraSize.width, bestCameraSize.height);
		}

		/**
		 * Checking to see if flash mode auto is supported before setting this parameters. 
		 * This was needed because check deposit was crashing on the new Nexus 7.
		 */
		/*
		 * Add a check to see if supportFlashModes returns null.  
		 * -Julian
		 */
		if (null != parameters.getSupportedFlashModes()
				&& parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)){
			parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
		}

		//Setup the picture quality
		parameters.setPictureFormat(ImageFormat.JPEG);
		parameters.setJpegQuality(maxJpegQuality);

		camera.setParameters(parameters);
	}

	/**
	 * Set the orientation of the camera preview to be the same as the orientation of the screen.
	 * @param activity
	 * @param cameraId
	 * @param camera
	 */
	public void setCameraDisplayOrientation(final Activity activity, final int cameraId, 
			final Camera camera) {
		final android.hardware.Camera.CameraInfo info =
				new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		final int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		final int pi = 180;
		final int twoPi = pi << 1;
		final int piHalf = pi >> 1;

		switch (rotation) {
		case Surface.ROTATION_0: 
			degrees = 0; 
			break;
		case Surface.ROTATION_90: 
			degrees = piHalf; 
			break;
		case Surface.ROTATION_180: 
			degrees = pi; 
			break;
		case Surface.ROTATION_270: 
			degrees = twoPi - piHalf; 
			break;
		}

		int result;

		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % twoPi;

			// compensate the mirror
			result = (twoPi - result) % twoPi;  
		} else {  
			// back-facing
			result = (info.orientation - degrees + twoPi) % twoPi;
		}

		camera.stopPreview();
		camera.setDisplayOrientation(result);
		camera.startPreview();
	}

	/**
	 * Start the camera preview.
	 */
	private void startPreview() {

		if(camera != null && cameraConfigured) {			
			camera.startPreview();
		}
	}

	/**
	 * Setup the camera preview.
	 * @param width
	 * @param height
	 */
	private void initPreview(final int width, final int height) {
		if (camera != null && previewHolder.getSurface() != null) {

			if (!cameraConfigured) {
				final Camera.Parameters parameters = camera.getParameters();

				final Camera.Size size = getBestPreviewSize(width, height, parameters);

				if (size!=null) {
					parameters.setPreviewSize(size.width, size.height);
					camera.setParameters(parameters);
					cameraConfigured=true;
				}
			}
		}
	}

	/**
	 * Gets the supported preview sizes for the current camera and finds the best supported size to use.
	 * @param width
	 * @param height
	 * @param parameters
	 * @return
	 */
	private Camera.Size getBestPreviewSize(final int width, final int height, final Camera.Parameters parameters) {
		Camera.Size result = null;

		for (final Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width<=width && size.height<=height) {
				if (result==null) {
					result=size;
				}
				else {
					final int resultArea=result.width*result.height;
					final int newArea=size.width*size.height;

					if (newArea>resultArea) {
						result=size;
					}
				}
			}
		}

		return result;
	}

	/**
	 * A callback for if the preview surface changes.
	 */
	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		if(!isPaused) {
			if(camera != null) {
				initPreview(width, height);
				startPreview();
			}
		}
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		if(camera == null) {
			camera = Camera.open();
			setupCameraParameters();

			try {
				camera.setPreviewDisplay(previewHolder);
			} catch (final IOException e) {			
				Log.e(TAG, "Error during camera setup : " + e);
				camera.release();
				camera = null;
			}	
		}
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		if(camera != null) {
			camera.stopPreview();
			camera.release();

			//This is set to null so that we can check in the autofocus callback to see if the camera has been released
			//so that we do not try to focus a unavailable camera and cause a crash.
			camera = null;
		}
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return null;
	}

	/**
	 * The AsyncTask responsible for displaying the countdown timer before the user takes a picture.
	 * 
	 * @author scottseward
	 *
	 */
	public class CameraCountdownTask extends AsyncTask<Void, Void, Void> {
		private static final int ONE_SECOND = 1000;

		/**
		 * Before the task begins its background processing, make sure that the close button is not visible
		 * and that the countdown logo is visible.
		 */
		@Override
		protected void onPreExecute() {
			closeButton.setVisibility(View.INVISIBLE);
			countdownLogo.setVisibility(View.VISIBLE);
		}

		/**
		 * Wait for a second between changing the countdown image.
		 */
		@Override
		protected Void doInBackground(final Void... params) {
			try {
				Thread.sleep(ONE_SECOND);
			} catch (final InterruptedException e) {
				Log.e(TAG, "Countdown Thread Interrupted: " + e);
			}

			return null;
		}

		/**
		 * After the one second pause in doInBackground, decrement the count and update
		 * the count image. Then check to see if we should keep counting down, or take the picture.
		 */
		@Override
		protected void onPostExecute(final Void result) {
			count--;
			updateCountImage();

			if(count < 1){
				focusThenTakePicture();
				count = THREE;
			}
			else{
				timerTask = new CameraCountdownTask();
				timerTask.execute();
			}
		}

		/**
		 * Update the countdown image based on the count variable value.
		 */
		private void updateCountImage() {
			if(count == THREE) {
				countdownLogo.setImageDrawable(countdownThree);
			} else if (count == 2) {
				countdownLogo.setImageDrawable(countdownTwo);
			} else if(count == 1) {
				countdownLogo.setImageDrawable(countdownOne);
			} else if (count < 1) {
				resetCountdown();
			}
		}
	}

	@Override
	public void onAnimationEnd(final Animation animation) {
		tips.setVisibility(View.GONE);
	}

	@Override
	public void onAnimationRepeat(final Animation animation) {
		//Intentionally left blank
	}

	@Override
	public void onAnimationStart(final Animation animation) {
		//Intentionally left blank
	}

}
