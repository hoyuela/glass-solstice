package com.discover.mobile.bank.checkdeposit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.ui.modals.ModalAlertWithOneButton;
import com.discover.mobile.common.ui.modals.ModalDefaultOneButtonBottomView;
import com.discover.mobile.common.ui.modals.ModalDefaultTopView;

public class CheckDepositCaptureActivity extends BaseActivity implements SurfaceHolder.Callback {
	private final String TAG = CheckDepositCaptureActivity.class.getSimpleName();
	
	public static final int RETAKE_FRONT = 1;
	public static final int RETAKE_BACK  = 2;
	public static final String FRONT_PICTURE = "pic1";
	public static final String BACK_PICTURE = "pic2";
	
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
	
	//BREADCRUMB CHECKMARKS
	private ImageView stepOneCheck;
	private ImageView stepTwoCheck;
	
	//CAMERA
	private SurfaceView cameraPreview;
	private SurfaceHolder previewHolder;
	private Camera camera;
	private CameraCountdownTask timerTask;
	
	//OTHER
	private Drawable COUNT_3;
	private Drawable COUNT_2;
	private Drawable COUNT_1;
	
	final int THREE = 3;
	private int count = THREE;

	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	protected boolean isPaused = false;

	
	/**
	 * Setup the Activity. Loads all UI elements to local references and starts camera setup.
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.check_deposit_capture);

		loadViews();

		timerTask = new CameraCountdownTask();
		
		getWindow().setFormat(PixelFormat.UNKNOWN);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		loadDrawables();
		setupButtons();
		cameraPreview.setOnClickListener(autoFocusClickListener);
		final Bundle extras = getIntent().getExtras();
		
		if(extras != null)
			setupPictureRetake(extras.getInt(BankExtraKeys.RETAKE_PICTURE));
		
	}
	
	/**
	 * On resume of the Activity, get the camera ready to use.
	 */
	@Override
	public void onResume() {
		super.onResume();
		isPaused = false;
		camera = Camera.open();
		startPreview();

		setCameraDisplayOrientation(this, 0, camera);
		setupCameraParameters();

	}

	/**
	 * When this Activity gets paused, release control of the camera.
	 */
	@Override
	public void onPause() {
		super.onPause();
		isPaused = true;
		if(inPreview) {
			camera.stopPreview();
		}
		
		camera.release();
		inPreview = false;
	}
	
	/**
	 * If the async task is not finished when the activity stops, we need to cancel it.
	 */
	@Override
	public void onStop() {
		super.onStop();
		if(timerTask.isRunning())
			timerTask.cancel(true);

		resetCountdown();
	}
	
	/**
	 * Setup all local references to UI elements.
	 */
	private void loadViews() {
		cameraPreview = (SurfaceView)findViewById(R.id.camera_preview);
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
		
		helpButton.setClickable(true);
	}
	
	/**
	 * Load the drawables that are used for the countdown timer.
	 */
	private void loadDrawables() {
		final Resources res = getResources();
		COUNT_3 = res.getDrawable(R.drawable.chckdep_3);
		COUNT_2 = res.getDrawable(R.drawable.chckdep_2);
		COUNT_1 = res.getDrawable(R.drawable.chckdep_1);
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
				finish();
			}
		});
		
		final String windowService = this.WINDOW_SERVICE;
		helpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				final ModalAlertWithOneButton modal = getHelpModal();
				modal.show();
				
				final Display display = ((WindowManager)getSystemService(windowService)).getDefaultDisplay();
				modal.getWindow().setLayout(display.getWidth(), display.getHeight());
				
			}
		});
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
			if(!timerTask.isRunning())
				focusCamera();
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
			if(event.getAction()==MotionEvent.ACTION_DOWN) return true;
            if(event.getAction()!=MotionEvent.ACTION_UP) return false;
            cameraPreview.setOnClickListener(null);
            captureButton.setPressed(true);   
            captureButton.setClickable(false);
            captureButton.setOnTouchListener(null);
			if(!timerTask.isRunning()){
				timerTask = new CameraCountdownTask();
				timerTask.execute();
			}
            return true;		    
		}
	};
	
	/**
	 * This is the click listener for the confirm button that is shown directly after an image is taken.
	 * This listener will advance the breadcrumb element to the next position and reset the camera
	 * preview so that the next picture can be taken. It also resets the buttons back to their default state.
	 */
	private final OnClickListener confirmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(final View v) {
			goToNextStep();
			resetCamera();
			setDefaultButtons();
			showImageBrackets();
		}
	};
	
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
		setupCameraParameters();
	}
	
	/**
	 * Advances the breadcrumb to the next selection, like from Front to Back.
	 */
	private void goToNextStep() {
		final boolean stepOneChecked = stepOneCheck.getVisibility() == View.VISIBLE;
		final boolean stepTwoChecked = stepTwoCheck.getVisibility() == View.VISIBLE;
		if(stepTwoChecked){
			Log.d(TAG, "FINISHED!");
		}else if(stepOneChecked){
			frontLabel.setTextColor(getResources().getColor(R.color.field_copy));
			backLabel.setTextColor(getResources().getColor(R.color.sub_copy));
			setDefaultButtons();
		}
	}
	
	/**
	 * Sets the next check mark to be visible, starting from left to right.
	 */
	private void setNextCheckVisible() {
		final boolean stepOneChecked = stepOneCheck.getVisibility() == View.VISIBLE;
		final boolean stepTwoChecked = stepTwoCheck.getVisibility() == View.VISIBLE;
		
		if(!stepOneChecked){
			stepOneCheck.setVisibility(View.VISIBLE);
		}else if(!stepTwoChecked) {
			stepTwoCheck.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * The reverse of setNextCheckVisible. This removes one check from right to left.
	 */
	private void resetCurrentCheckMark() {
		final boolean stepOneChecked = stepOneCheck.getVisibility() == View.VISIBLE;
		final boolean stepTwoChecked = stepTwoCheck.getVisibility() == View.VISIBLE;
		final Bundle extras = getIntent().getExtras();
		int retakeValue = 0;
		
		if(extras != null)
			retakeValue = extras.getInt(BankExtraKeys.RETAKE_PICTURE);
		
		if(retakeValue == RETAKE_FRONT){
			stepOneCheck.setVisibility(View.INVISIBLE);
		}
		else if(stepTwoChecked)
			stepTwoCheck.setVisibility(View.INVISIBLE);
		else if(stepOneChecked)
			stepOneCheck.setVisibility(View.INVISIBLE);
		
	}
	
	/**
	 * Reset the countdown timer.
	 * Hide the contdown logo, reset its image, and reset the counter.
	 */
	private void resetCountdown() {
		countdownLogo.setVisibility(View.GONE);
		countdownLogo.setImageDrawable(COUNT_3);
	}
	
	/**
	 * Tell the camera to take a picture.
	 */
	private void takePicture() {
		camera.takePicture(null, null, mPicture);
	}

	/**
	 * This PictureCallback is used by the camera to store the resulting picture
	 * as a JPEG. It also does some UI setup for the check preview such as,
	 * hiding the check brackets, showing the confirmatoin buttons and setting
	 * the next check mark to visible.
	 */
	private final PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(final byte[] data, final Camera camera) {
	    	cameraPreview.setOnClickListener(null);
	    	
	    	setPictureConfirmationButtons();
	    	hideImageBrackets();
	    	setNextCheckVisible();

	    	//Write the image to disk.
	        try {
	        	final FileOutputStream fos = getFileOutputStream();
	            fos.write(data);
	            fos.close();
	        } catch (final FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        } catch (final IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }
	        
	    }
	};
	
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
		
	}
	
	/** 
	 * Create a File for saving the image 
	 *	This method will possibly be removed once Andy gives his input on how best to save and resize images. 
	 */
	private FileOutputStream getFileOutputStream() {
		FileOutputStream fos = null;
		int retakePicture = 0;
		
		final Bundle extras = getIntent().getExtras();
		if(extras != null)
			retakePicture = extras.getInt(BankExtraKeys.RETAKE_PICTURE);
		
		try {
			if(retakePicture == RETAKE_FRONT) { 
				fos = openFileOutput(FRONT_PICTURE, Context.MODE_PRIVATE);
			}
			else if (stepOneCheck.getVisibility() == View.VISIBLE && stepTwoCheck.getVisibility() == View.VISIBLE){
				fos = openFileOutput(BACK_PICTURE, Context.MODE_PRIVATE);
			}else
				fos = openFileOutput(FRONT_PICTURE, Context.MODE_PRIVATE);
		} catch (final FileNotFoundException e) {
			Log.e(TAG, "Cannot find file : " + e);
		}
		
		return fos;	
	}

	/**
	 * Call the camera's auto focus method then take a picture once it is done.
	 */
	private void focusThenTakePicture() {
		 
		camera.autoFocus(new Camera.AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(final boolean success, final Camera camera) {
				//Cancel auto focus is called because if not, the flash may stay on.
				camera.cancelAutoFocus();
				takePicture();
			}
		});
	}
	
	/**
	 * Call the camera's auto focus method.
	 */
	private void focusCamera() {
		camera.autoFocus(new Camera.AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(final boolean success, final Camera camera) {
				//Cancel auto focus is called because if not, the flash may stay on.
				camera.cancelAutoFocus();
			}
		});
	}
	
	/**
	 * Setup the parameters for the camera that might be useful.
	 */
	private void setupCameraParameters() {
		final Camera.Parameters parameters = camera.getParameters();
		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
		parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
		camera.setParameters(parameters);
	}

	/**
	 * Set the orientation of the camera preview to be the same as the orientation of the screen.
	 * @param activity
	 * @param cameraId
	 * @param camera
	 */
	public static void setCameraDisplayOrientation(final Activity activity,
			final int cameraId, final android.hardware.Camera camera) {
		final android.hardware.Camera.CameraInfo info =
				new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		final int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		final int pi = 180;
		
		switch (rotation) {
			case Surface.ROTATION_0: 
				degrees = 0; 
				break;
			case Surface.ROTATION_90: 
				degrees = pi >> 1; 
				break;
			case Surface.ROTATION_180: 
				degrees = pi; 
				break;
			case Surface.ROTATION_270: 
				degrees = (pi << 1) - (pi >> 1); 
				break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % (pi << 1);
			result = ((pi << 1) - result) % (pi << 1);  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + (pi << 1)) % (pi << 1);
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
			inPreview = true;
		}
	}

	/**
	 * Setup the camera preview.
	 * @param width
	 * @param height
	 */
	private void initPreview(final int width, final int height) {
		if (camera!=null && previewHolder.getSurface()!=null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			}
			catch (final Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
			}

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
	 * Returns a constructed modal dialog that provides instructions on how to use the
	 * check capture feature.
	 * @return a modal dialog with check capture help content.
	 */
	private ModalAlertWithOneButton getHelpModal() {
		final Spanned helpContent = Html.fromHtml(
				getResources().getString(R.string.bank_deposit_capture_help_content));

		final ModalDefaultTopView top = new ModalDefaultTopView(this, null);
		final ModalDefaultOneButtonBottomView bottom = new ModalDefaultOneButtonBottomView(this, null);
		final CheckDepositModal modal = new CheckDepositModal(this, top, bottom);
		top.setTitle(R.string.bank_deposit_capture_help_title);
		top.getContentTextView().setText(helpContent, TextView.BufferType.SPANNABLE);
		top.showErrorIcon(false);
		top.hideNeedHelpFooter();
		bottom.setButtonText(R.string.ok);
		bottom.getButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(final View v){
				modal.dismiss();
			}
		});
		
		return modal;
	}

	/**
	 * Gets the supported preview sizes for the current camera and finds the best supported size to use.
	 * @param width
	 * @param height
	 * @param parameters
	 * @return
	 */
	private Camera.Size getBestPreviewSize(final int width, final int height, final Camera.Parameters parameters) {
		Camera.Size result=null;

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

		return(result);
	}

	/**
	 * A callback for if the preview surface changes.
	 */
	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width,
			final int height) {
		if(!isPaused) {
			initPreview(width, height);
			startPreview();
		}
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {

	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {

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
		private boolean isRunning = false;
		final int ONE_SECOND = 1000;
		
		/**
		 * Before the task begins its background processing, make sure that the close button is not visible
		 * and that the countdown logo is visible.
		 */
		@Override
		protected void onPreExecute() {
			isRunning = true;
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
				isRunning = false;
			}
			else{
				timerTask = new CameraCountdownTask();
				timerTask.execute();
			}
		}
		
		/**
		 * Returns if the current task is running.
		 * @return if the current task is running.
		 */
		public boolean isRunning() {
			return isRunning;
		}
		
	}
	
	/**
	 * Update the countdown image based on the count variable value.
	 */
	private void updateCountImage() {
		if(count == THREE)
			countdownLogo.setImageDrawable(COUNT_3);
		else if (count == 2)
			countdownLogo.setImageDrawable(COUNT_2);
		else if(count == 1)
			countdownLogo.setImageDrawable(COUNT_1);
		else if (count < 1)
			resetCountdown();
	}

}
