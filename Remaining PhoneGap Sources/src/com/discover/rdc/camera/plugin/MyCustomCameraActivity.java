package com.discover.rdc.camera.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.discoverfinancial.mobile.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyCustomCameraActivity extends Activity
{
	private boolean mTestMode = false; 
	private LayoutInflater mLayoutInflater;
	private TextView mFrontTitleTextView, mCounterTextView;
	private Button mFrontCheckButton, mRetakePictureButton, mContinuebutton,
			mCloseButton, mTakePictureButton, mInfoButton, mBackButton,
			mCancelButton, mCancelDepositButton, mDontCancelDepositButton, 
			mSelectAccountButton, mEnterAmountButton;
	
	private ImageView mPreviewCheckImageView, mSquareMarksImageView,
			mBlockScreenImageView;
	private PictureCallback mPictureCallbackJPEG;
	private AutoFocusCallback mAutoFocusCallback;
	private RelativeLayout mGrayModalInfoLayout, mHelpModalLayout,
			mGrayModalCounterLayout, mCancelDepositLayout;
	
	private MyCountDown myCountDown;
	private Bitmap mImageFromGallery;

	private static final String FRONT_CHECK_ATTR = "isFronOfCheck";
	private final int REQUEST_CODE_PHOTO_GALLERY = 1;

	private boolean inCameraPreviewMode = false;
	private boolean isFrontOfCheck = true;
	private boolean isApplicationInForeground = true;
	private boolean userSelectedImageFromGallery = false;
	private boolean inCheckPicturePreview = false;
	private boolean releaseAllCameraResources = false;

	private int currentAction = -1, initalRequestedAction = -1;
	private int mScreenHeight;
	private int mScreenWidth;

    public static final int PICTURE_WIDTH_640 = 640;
    public static final int PICTURE_HEIGTH_480 = 480;
    private static final long INITIAL_COUNTDOWN_MILLISECONDS = 5000;
	private static final long INTERVARL_COUNTDOWN_MILLISECONDS = 1000;
	private static final String TAG = "RDC Camera Activity";
    
    private MyCustomCameraPreview mCameraPreview;
	private Camera mCamera;

	@Override
	/**
	 * Called when Activity is first launched
	 */
	protected void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "Enter onCreate()");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.customcamera);
		getWindow().setFormat(PixelFormat.UNKNOWN);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		mScreenHeight = displaymetrics.heightPixels;
		mScreenWidth = displaymetrics.widthPixels;
		
		initCustomCameraControlsFromLayout();
		initCameraCallbackFunctions();
		initUIComponents();
		
		initalRequestedAction = Integer.valueOf(getIntent().getAction()).intValue();
		
		switch (initalRequestedAction) 
		{
			case MyCustomCameraPlugin.ACTION_CAPTURE_FRONT:
				initCameraAndPreview();
				currentAction = MyCustomCameraPlugin.ACTION_CAPTURE_FRONT;
				break;
	
			case MyCustomCameraPlugin.ACTION_CAPTURE_ONLY_FRONT:
				initCameraAndPreview();
				currentAction = MyCustomCameraPlugin.ACTION_CAPTURE_FRONT;
				break;
	
			case MyCustomCameraPlugin.ACTION_CAPTURE_ONLY_BACK:
				initCameraAndPreview();
				isFrontOfCheck = false;
				handlingUIRetakeOnlyBackCheck();
				currentAction = MyCustomCameraPlugin.ACTION_CAPTURE_BACK;
				break;
	
			case MyCustomCameraPlugin.ACTION_VIEW_ONLY_FRONT:
				currentAction = MyCustomCameraPlugin.ACTION_VIEW_FRONT;
				handlingUIViewPicture();
				break;
	
			case MyCustomCameraPlugin.ACTION_VIEW_ONLY_BACK:
				isFrontOfCheck = false;
				currentAction = MyCustomCameraPlugin.ACTION_VIEW_BACK;
				handlingUIViewPicture();
				break;
				
			case MyCustomCameraPlugin.ACTION_VIEW_BACK:
				isFrontOfCheck = false;
				currentAction = MyCustomCameraPlugin.ACTION_VIEW_BACK;
				handlingUIViewPicture();
				break;
		}

		storeCurrentState();
		
		Log.d(TAG, "Exit onCreate()");
	}

	/**
	 * Store important state data that has to be restored on restart
	 */
	private void storeCurrentState() 
	{
		Log.d(TAG, "Enter storeCurrentState()");
		
		SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.PREFERENCES_FILE_NAME), 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(FRONT_CHECK_ATTR, isFrontOfCheck);
		editor.commit();
		Log.d(TAG, "stored isFrontOfCheck: " + isFrontOfCheck);
		
		Log.d(TAG, "Exit storeCurrentState()");
	}

	@Override
	/**
	 * Called after activity stopped but process is still in memory 
	 * and user navigates back to the activity
	 */
	protected void onRestart() 
	{
		Log.d(TAG, "Enter onRestart()");
		
		if (!inCheckPicturePreview) 
		{
			initCameraAndPreview();
		}
		
		initCameraCallbackFunctions();
		enableButtons();
		super.onRestart();
		
		Log.d(TAG, "Exit onRestart()");

	}

	@Override
	/**
	 * Called after onCreate() or onRestart()
	 */
	protected void onStart() 
	{
		Log.d(TAG, "Enter onStart()");

		SharedPreferences settings = getSharedPreferences(getResources().getString(R.string.PREFERENCES_FILE_NAME), 0);
		isFrontOfCheck = settings.getBoolean(FRONT_CHECK_ATTR, true);
		releaseAllCameraResources = settings.getBoolean(getResources().getString(R.string.PREFERENCES_KEY_RELEASE_ALL_CAMERA_RESOURCES), false);
		Log.d(TAG, "Entered onStart()");
		Log.d(TAG, "isFronOfCheck: " + isFrontOfCheck);
		Log.d(TAG, "currentAction: " + currentAction);
		Log.d(TAG, "mScreenHeight: " + mScreenHeight);
		Log.d(TAG, "mScreenWidth: " + mScreenWidth);
		Log.d(TAG, "inCheckPicturePreview: " + inCheckPicturePreview);
		Log.d(TAG, "releaseAllCameraResources: " + releaseAllCameraResources);

		super.onStart();
		
		Log.d(TAG, "Exit onStart()");

	}

	@Override
	/**
	 * Called when activity is no longer visible
	 */
	protected void onStop() 
	{
		Log.d(TAG, "Enter onStop()");
		
		super.onStop();
		// Home button pressed
		resetCountDown();
		mCameraPreview = null;
		
		Log.d(TAG, "Exit onStop()");

	}

	@Override
	/**
	 * Called by the system before activity is destroyed. 
	 */
	protected void onDestroy() 
	{
		Log.d(TAG, "Enter onDestroy()");
		
		unbindDrawables(findViewById(R.id.background));
		mPreviewCheckImageView.setImageBitmap(null);
		mBlockScreenImageView.setImageBitmap(null);
		mSquareMarksImageView.setImageBitmap(null);
		mPreviewCheckImageView = null;
		mSquareMarksImageView = null;
		mBlockScreenImageView = null;
				
		super.onDestroy();
		
		Log.d(TAG, "Exit onDestroy()");

	}

	private void unbindDrawables(View view) 
	{
		Log.d(TAG, "Enter unbindDrawables()");
		
		if (view.getBackground() != null) {
			Log.d(TAG, "Entered processing unbindDrawables()");
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			try {
				((ViewGroup) view).removeAllViews();
			} catch (Exception ex) {
			}
		}
		
		Log.d(TAG, "Exit unbindDrawables()");

	}

	@Override
	/**
	 * Called when another activity comes into foreground
	 */
	protected void onPause() 
	{
		Log.d(TAG, "Enter onPause()");
		
		releaseCameraResources();
		resetCountDown();
		super.onPause();
		
		Log.d(TAG, "Exit onPause()");

	}

	@Override
	/**
	 * Called after user returns to activity
	 */
	protected void onResume() 
	{
		Log.d(TAG, "Enter onResume()");
		
		super.onResume();
		
		mTestMode = Boolean.valueOf( getResources().getString(R.string.TEST_MODE) ); // new Boolean(getResources().getString(R.string.TEST_MODE));

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		mScreenHeight = displaymetrics.heightPixels;
		mScreenWidth = displaymetrics.widthPixels;
		
		if (!isApplicationInForeground) {
			Log.d(TAG, "App not in foreground");
			isApplicationInForeground = true;
			initUIComponents();

			if (mTestMode) {
				if (!userSelectedImageFromGallery) {
					if (inCheckPicturePreview) {
						stopCameraPreview();
						handlingUIAfterTakePicture();
					} else {
						handlingUIAfterAcceptPicture();
					}

				} else {
					stopCameraPreview();
					userSelectedImageFromGallery = false;
					proccessPictureCapturedOrSelected(mImageFromGallery);
					handlingUIAfterTakePicture();
					
					if(!mImageFromGallery.isRecycled())
					{
						mImageFromGallery.recycle();
						mImageFromGallery = null;
					}
					
				}
			} 
			else {
				if (inCheckPicturePreview) {
					stopCameraPreview();
					handlingUIAfterTakePicture();
				}
			}
		}		
		Log.d(TAG, "Exit onResume()");
	}

	@Override
	public void onBackPressed() {
		Log.d(TAG, "Enter onBackPressed()");
		// Disable back button
		Log.d(TAG, "Exit onBackPressed()");
		
		return;
	}

	private void resetCountDown()
	{
		Log.d(TAG, "Enter resetCountDown()");

		if (myCountDown != null) {
			isApplicationInForeground = false;
			myCountDown.cancel();
		}
		
		Log.d(TAG, "Exit resetCountDown()");
	}

	private void initUIComponents() 
	{
		Log.d(TAG, "Enter initUIComponents()");

		mCancelButton = (Button) findViewById(R.id.cancelbutton);

		mBlockScreenImageView = (ImageView) findViewById(R.id.blockscreen);
		mBlockScreenImageView.setVisibility(View.GONE);

		mGrayModalInfoLayout = (RelativeLayout) findViewById(R.id.grayModalLayout);
		mGrayModalInfoLayout.setVisibility(View.VISIBLE);
		mFrontTitleTextView = (TextView) findViewById(R.id.frontTitleTextView);

		mGrayModalCounterLayout = (RelativeLayout) findViewById(R.id.grayModalCounterLayout);
		mGrayModalCounterLayout.setVisibility(View.GONE);
		mCounterTextView = (TextView) findViewById(R.id.frontTitleCounterView);


		mPreviewCheckImageView = (ImageView) findViewById(R.id.previewCheck);
		mPreviewCheckImageView.setVisibility(View.GONE);

		mSquareMarksImageView = (ImageView) findViewById(R.id.bgcustom);
		
		mHelpModalLayout = (RelativeLayout) findViewById(R.id.helpModalLayout);
		mHelpModalLayout.setVisibility(View.GONE);
		
		mCancelDepositLayout = (RelativeLayout) findViewById(R.id.cancelModalLayout);
		mCancelDepositLayout.setVisibility(View.GONE);

		setupTakePictureButton();
		setupRetakePictureButton();
		setupContinueButton();		
		setupCloseButton();
		setupInfoButton();
		setupBackButton();		
		setupAmountButton();
		setupAccountButton();
		setupCancelButton();
		setupFrontCheckButton();
		setupCancelDepositButton();
		setupDontCancelDepositButton();
		
		Log.d(TAG, "Exit initUIComponents()");		
	}

	private void setupBackButton()
	{
		Log.d(TAG, "Enter setupBackButton()");

		mBackButton = (Button) findViewById(R.id.backbutton);
		mBackButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) 
			{
				Log.d(TAG, "Back button onclick()");

				switch (currentAction)
				{	
					case MyCustomCameraPlugin.ACTION_VIEW_BACK:
						
						if(initalRequestedAction == MyCustomCameraPlugin.ACTION_VIEW_ONLY_BACK)
						{
							//Redirect to confirm & authorize
							releaseCameraResources();
							MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(false);
							MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
							finish();
						}
						else
						{
							//Redirect to capture back check
							isFrontOfCheck = false;
							currentAction = MyCustomCameraPlugin.ACTION_CAPTURE_BACK;
							handlingUIAfterAcceptPicture();
						}
						break;
						
					case MyCustomCameraPlugin.ACTION_CAPTURE_BACK:
						
						if(initalRequestedAction == MyCustomCameraPlugin.ACTION_CAPTURE_ONLY_BACK ||
								initalRequestedAction == MyCustomCameraPlugin.ACTION_VIEW_ONLY_BACK)
						{
							//Redirect to confirm & authorize
							releaseCameraResources();
							MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(false);
							MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
							finish();
						}
						else
						{
							//Redirect to view front check
							isFrontOfCheck = true;
							currentAction = MyCustomCameraPlugin.ACTION_VIEW_FRONT;
							stopCameraPreview();
							handlingUIViewPicture();
						}
						break;
					
					case MyCustomCameraPlugin.ACTION_VIEW_FRONT:
						
						if(initalRequestedAction == MyCustomCameraPlugin.ACTION_VIEW_ONLY_FRONT)
						{
							//Redirect to confirm & authorize
							releaseCameraResources();
							MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(false);
							MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
							finish();
						}
						else
						{
							//Redirect to capture front check
							isFrontOfCheck = true;
							currentAction = MyCustomCameraPlugin.ACTION_CAPTURE_FRONT;
							handlingUIAfterAcceptPicture();
						}
						break;
					
					case MyCustomCameraPlugin.ACTION_CAPTURE_FRONT:
						
						if(initalRequestedAction == MyCustomCameraPlugin.ACTION_CAPTURE_ONLY_FRONT ||
								initalRequestedAction == MyCustomCameraPlugin.ACTION_VIEW_ONLY_FRONT)
						{
							//Redirect to confirm & authorize
							releaseCameraResources();
							MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(false);
							MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
							finish();
						}
						else
						{
							//Redirect to enter amount
							MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(true);
							MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
							finish();
						}
						break;
				}
			}
		});
		
		Log.d(TAG, "Exit setupBackButton()");

	}
	
	private void setupInfoButton()
	{
		Log.d(TAG, "Enter setupInfoButton()");

		mInfoButton = (Button) findViewById(R.id.displayinfo);
		mInfoButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) 
			{
				Log.d(TAG, "Info button onclick()");
				handlingUIAfterOpenHelpModal();
			}
		});
		
		Log.d(TAG, "Exit setupInfoButton()");

	}

	private void setupCloseButton() 
	{
		Log.d(TAG, "Enter setupCloseButton()");

		mCloseButton = (Button) findViewById(R.id.closeButton);
		mCloseButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View arg0) 
			{
				Log.d(TAG, "Close button onclick()");
				handlingUIAfterCloseHelpModal();
			}
		});
		
		Log.d(TAG, "Exit setupCloseButton()");

	}
	
	private void setupCancelDepositButton() 
	{
		Log.d(TAG, "Enter setupCancelDepositButton()");

		mCancelDepositButton = (Button) findViewById(R.id.cancelDepositButton);
		mCancelDepositButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View arg0)
			{
				Log.d(TAG, "Cancel Deposit button onclick()");

				MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(true);
				MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
				finish();
			}
		});
		
		Log.d(TAG, "Exit setupCancelDepositButton()");

	}
	
	private void setupDontCancelDepositButton()
	{
		Log.d(TAG, "Enter setupDontCancelDepositButton()");

		mDontCancelDepositButton = (Button) findViewById(R.id.dontCancelDepositButton);
		mDontCancelDepositButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View arg0) 
			{
				Log.d(TAG, "Don't Cancel Deposit button onclick()");
				handlingUIAfterCloseCancelDepositModal();
			}
		});
		
		Log.d(TAG, "Exit setupDontCancelDepositButton()");

	}

	private void setupContinueButton() 
	{
		Log.d(TAG, "Enter setupContinueButton()");

		mContinuebutton = (Button) findViewById(R.id.continuebutton);
		mContinuebutton.setVisibility(View.GONE);
		mContinuebutton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View arg0) 
			{
				Log.d(TAG, "Continue button onclick()");
								
				switch (currentAction) 
				{
					case MyCustomCameraPlugin.ACTION_VIEW_FRONT:
						
						if(initalRequestedAction == MyCustomCameraPlugin.ACTION_CAPTURE_ONLY_FRONT ||
								initalRequestedAction == MyCustomCameraPlugin.ACTION_VIEW_ONLY_FRONT)
						{
							//Redirect to confirm & authorize
							releaseCameraResources();
							MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(false);
							MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
							finish();
						}
						else
						{
							isFrontOfCheck = false;
							currentAction = MyCustomCameraPlugin.ACTION_CAPTURE_BACK;
							storeCurrentState();
							handlingUIAfterAcceptPicture();
						}
						break;
					
					case MyCustomCameraPlugin.ACTION_VIEW_BACK:
						//Redirect to confirm & authorize
						releaseCameraResources();
						MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(false);
						MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
						finish();
						break;
				}
				
				inCheckPicturePreview = false;
			}			
		});
		
		Log.d(TAG, "Exit setupContinueButton()");
	}

	private void setupRetakePictureButton() 
	{
		Log.d(TAG, "Enter setupRetakePictureButton()");

		mRetakePictureButton = (Button) findViewById(R.id.retakepicture);
		mRetakePictureButton.setVisibility(View.GONE);
		mRetakePictureButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) 
			{
				Log.d(TAG, "Retake button onclick()");

				if(mImageFromGallery != null && !mImageFromGallery.isRecycled()){mImageFromGallery.recycle(); mImageFromGallery = null;}
				
				switch (currentAction)
				{
					case MyCustomCameraPlugin.ACTION_VIEW_FRONT:
						currentAction = MyCustomCameraPlugin.ACTION_CAPTURE_FRONT;
						break;
					
					case MyCustomCameraPlugin.ACTION_VIEW_BACK:
						currentAction = MyCustomCameraPlugin.ACTION_CAPTURE_BACK;
						break;
				}
				
				handlingUIRetakeOnClick();
			}
		});
		
		Log.d(TAG, "Exit setupRetakePictureButton()");
	}

	private void setupTakePictureButton() 
	{
		Log.d(TAG, "Enter setupTakePictureButton()");

		mTakePictureButton = (Button) findViewById(R.id.takepicture);
		mTakePictureButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.capture));
		mTakePictureButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) 
			{
				Log.d(TAG, "Capture button onclick()");

				// Setup count down 3..2..1
				myCountDown = new MyCountDown(INITIAL_COUNTDOWN_MILLISECONDS,
						INTERVARL_COUNTDOWN_MILLISECONDS);
				myCountDown.start();
			}
		});
		
		Log.d(TAG, "Exit setupTakePictureButton()");
	}
	
	private void setupAmountButton() 
	{
		Log.d(TAG, "Enter setupAmountButton()");

		mEnterAmountButton = (Button) findViewById(R.id.enteramountbutton);
		mEnterAmountButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) 
			{
				Log.d(TAG, "Enter amount button onclick()");

				releaseCameraResources();
				MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(true);
				MyCustomCameraPlugin.setShouldRedirecToSelectAccount(false);
				finish();
			}

		});
		
		Log.d(TAG, "Exit setupAmountButton()");
	}

	private void setupAccountButton() 
	{
		Log.d(TAG, "Enter setupAccountButton()");

		mSelectAccountButton = (Button) findViewById(R.id.selectaccountbutton);
		mSelectAccountButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) 
			{
				Log.d(TAG, "Select account button onclick()");

				releaseCameraResources();
				MyCustomCameraPlugin.setShouldRedirecToEnterAmmount(false);
				MyCustomCameraPlugin.setShouldRedirecToSelectAccount(true);
				finish();
			}

		});
		
		Log.d(TAG, "Exit setupAccountButton()");
	}
	
	private void setupFrontCheckButton() 
	{
		Log.d(TAG, "Enter setupFrontCheckButton()");

		mFrontCheckButton = (Button) findViewById(R.id.frontcheckedbutton);
		
		Log.d(TAG, "Exit setupFrontCheckButton()");

	}

	private void setupCancelButton() 
	{
		Log.d(TAG, "Enter setupCancelButton()");

		mCancelButton = (Button) findViewById(R.id.cancelbutton);
		mCancelButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v)
			{
				Log.d(TAG, "Cancel button onclick()");

				handlingUIAfterOpenCancelDepositModal();
			}

		});
		
		Log.d(TAG, "Exit setupCancelButton()");

	}


	public class MyCountDown extends CountDownTimer {
		public MyCountDown(long target, long countInterval) 
		{
			super(target, countInterval);
			Log.d(TAG, "Enter MyCountDown(" + target + ", " + countInterval + ")");
			handlingUICountingDown();
			Log.d(TAG, "Exit MyCountDown(" + target + ", " + countInterval + ")");

		}

		@Override
		public void onFinish() {
			
			Log.d(TAG, "Enter MyCountDown onFinish()");

			if (mTestMode)
			{
				// Take picture from photo gallery
				Intent photoGallery = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(photoGallery, REQUEST_CODE_PHOTO_GALLERY);
			} 
			else 
			{
				/* Comment out this block
				// Taking picture
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setPictureFormat( PixelFormat.JPEG );
				parameters.setJpegQuality( 100 );
				parameters.setPictureSize( 1600, 1200 );
				
				//if( parameters.getSupportedFlashModes().contains( Camera.Parameters.FLASH_MODE_AUTO ) ) {
				//	parameters.setFlashMode( Camera.Parameters.FLASH_MODE_AUTO );
				//}

				mCamera.setParameters( parameters );
				*/
				mCamera.takePicture(null, null, mPictureCallbackJPEG);
			}
			
			Log.d(TAG, "Exit MyCountDown onFinish()");

		}

		@Override
		public void onTick(long millisUntilFinished) 
		{
			Log.d(TAG, "Enter MyCountDown onTick()");

			if (millisUntilFinished < 2000) 
			{
				// dismiss modal counter
				mGrayModalCounterLayout.setVisibility(View.GONE);

				if (!mTestMode) {
					Camera.Parameters p = mCamera.getParameters();
					List<String> focusModes = p.getSupportedFocusModes();
					
					if(focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
						// Camera auto focus
						Log.d(TAG, "Camera supports autofocus. Setting autofocus callback.");
						mCamera.autoFocus(mAutoFocusCallback);
					}
					else {
					    //Phone does not support autofocus!
						Log.d(TAG, "Camera does not support autofocus.");
					}
				}

			} 
			else 
			{
				// Refresh counter
				mCounterTextView.setText(String
						.valueOf(millisUntilFinished / 1000 - 1));
				Log.d(TAG, String.valueOf(millisUntilFinished));
			}
			
			Log.d(TAG, "Exit MyCountDown onTick()");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		Log.d(TAG, "Enter onActivityResult()");

		switch (requestCode) 
		{
			case REQUEST_CODE_PHOTO_GALLERY:
				if (resultCode == RESULT_OK) 
				{
					Uri uriImageSelected = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
	
					Cursor cursor = getContentResolver().query(uriImageSelected,
							filePathColumn, null, null, null);
	
					// User selected image from gallery
					userSelectedImageFromGallery = true;
	
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();
					
					BitmapFactory.Options o = new BitmapFactory.Options();
					o.inJustDecodeBounds = true;
			       
					mImageFromGallery = BitmapFactory.decodeFile(filePath, o);
					
					if((o.outWidth * o.outHeight) >= (PICTURE_WIDTH_640 * PICTURE_HEIGTH_480))
					{	
						Log.d(TAG, "Image selected is larger than 640 x 480.");
						 o = new BitmapFactory.Options();
				         o.inSampleSize = 3;
				         mImageFromGallery = BitmapFactory.decodeFile(filePath, o);
	
					}
					else{
						Log.d(TAG, "Image selected is smaller than 640 x 480.");
						mImageFromGallery = BitmapFactory.decodeFile(filePath);
					}
					
					if(currentAction == MyCustomCameraPlugin.ACTION_CAPTURE_FRONT)
					{
						currentAction = MyCustomCameraPlugin.ACTION_VIEW_FRONT;
					}
					else
					{
						currentAction = MyCustomCameraPlugin.ACTION_VIEW_BACK;
					}
	
				}
				break;
		}
		
		Log.d(TAG, "Exit onActivityResult()");
	}

	private void initCameraCallbackFunctions() 
	{
		Log.d(TAG, "Enter initCameraCallbackFunctions()");

		mPictureCallbackJPEG = new PictureCallback() 
		{
			public void onPictureTaken(byte[] data, Camera camera) 
			{
				Log.d(TAG, "Enter onPictureTaken()");

				stopCameraPreview();

				Log.d(TAG, "Decode original capture");
				
				BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
			    bmpFactoryOptions.inScaled=false;
			    
				Bitmap original = BitmapFactory.decodeByteArray(data, 0,
						data.length, bmpFactoryOptions);

				proccessPictureCapturedOrSelected(original);
				
				if(currentAction == MyCustomCameraPlugin.ACTION_CAPTURE_FRONT)
				{
					currentAction = MyCustomCameraPlugin.ACTION_VIEW_FRONT;
				}
				else
				{
					currentAction = MyCustomCameraPlugin.ACTION_VIEW_BACK;
				}
				
				if(!original.isRecycled())
				{
					original.recycle();
					original = null;	
				}				
				
				handlingUIAfterTakePicture();
				
				Log.d(TAG, "Exit onPictureTaken()");
			}
		};

		mAutoFocusCallback = new AutoFocusCallback() {
			public void onAutoFocus(boolean success, Camera camera) {
			}
		};
		
		Log.d(TAG, "Exit initCameraCallbackFunctions()");

	}
	
	private void proccessPictureCapturedOrSelected(Bitmap picture) {
		Log.d(TAG, "Enter proccessPictureCapturedOrSelected()");
		
		int width = picture.getWidth();
		int height = picture.getHeight();
		
		if( width > 1600 ) {
			height = height * ( 1600 / width );
			width = 1600;
		}
		
		Bitmap check = Bitmap.createScaledBitmap( picture, width, height, false );
		
		if( check != null ) {
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				check.compress( Bitmap.CompressFormat.JPEG, 100, os );

				final byte[] encoded = os.toByteArray();
				this.localStoreBytes( encoded, isFrontOfCheck ? getFrontCheckFilename() : getBackCheckFilename() );
			} finally {
				try {
					os.close();
				} catch (IOException e) { }				
			}

		}
		
		if( !check.isRecycled() ) {
			check.recycle();
			check = null;
		}
		
		Log.d(TAG, "Exit proccessPictureCapturedOrSelected()");
	}

	/*
	private void proccessPictureCapturedOrSelected(Bitmap picture) 
	{
		Log.d(TAG, "Enter proccessPictureCapturedOrSelected()");

		// Define proportions
		float dw = 0.80f;
		float dh = 0.66f;

		// Calculate dimensions of the picture to be cropped
		int width = (int) (mScreenWidth * dw);
		int height = (int) (mScreenHeight * dh);

		Bitmap checkImageCropped = cropCheckImage(picture, width, height);
		
		if(checkImageCropped != null)
		{	
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			checkImageCropped.compress(Bitmap.CompressFormat.JPEG,
					100, output);
			byte[] imageEncoded = output.toByteArray();
			localStoreBytes(imageEncoded, isFrontOfCheck ? getFrontCheckFilename() : getBackCheckFilename());
		}
		
		if(!checkImageCropped.isRecycled())
		{
			checkImageCropped.recycle();
			checkImageCropped = null;
		}
		
		Log.d(TAG, "Exit proccessPictureCapturedOrSelected()");
	}
	
	private Bitmap cropCheckImage(Bitmap original, int width, int height) 
	{
		Log.d(TAG, "Enter cropCheckImage()");
		
		if(original == null)
			return original;
		
		 try{
			 
			 Paint paint = new Paint();
	         paint.setFilterBitmap(true);	          
	 
	         int targetWidth  = (int) (original.getWidth() * 0.80f);
             int targetHeight = (int) (original.getHeight() * 0.53f);
	         
             int offsetX = (int)((original.getWidth() - targetWidth) / 2);
             int offsetY = (int)((original.getHeight() - targetHeight) / 2);
 
             Bitmap targetBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
 
             Canvas canvas = new Canvas(targetBitmap);
             canvas.drawColor(Color.WHITE);
             
             Rect src = new Rect(offsetX, offsetY, original.getWidth() - offsetX, original.getHeight() - offsetY);
             Rect dest = new Rect(0, 0, width, height);
             
             canvas.drawBitmap(original, src,
                                   dest, paint);
 
             Matrix matrix = new Matrix();
             matrix.postScale(1f, 1f);
             Bitmap resizedBitmap = Bitmap.createBitmap(targetBitmap, 0, 0, width, height, matrix, true);
             
             if(!targetBitmap.isRecycled())
             {
            	 targetBitmap.recycle();
                 targetBitmap = null;
             }
             
    		 Log.d(TAG, "Exit cropCheckImage()");

             return resizedBitmap;
	     }
	     catch(Exception e)
	     {
	          System.out.println("Error1 : " + e.getMessage() + e.toString());
	          Log.d(TAG, "Exit cropCheckImage()");

	          return original;
	      }
	}
	*/
	private void initCameraAndPreview()
	{
		Log.d(TAG, "Enter initCameraAndPreview()");

		if(mCamera == null)
		{
			// Create an instance of Camera
			mCamera = getCameraInstance();
			
			// Create our Preview view and set it as the content of our activity.
			mCameraPreview = new MyCustomCameraPreview(this, mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mCameraPreview);
			
			inCameraPreviewMode = true;
		}
		
		Log.d(TAG, "Exit initCameraAndPreview()");

	}
	
	private void initCustomCameraControlsFromLayout() 
	{
		Log.d(TAG, "Enter initCustomCameraControlsFromLayout()");

		mLayoutInflater = LayoutInflater.from(getApplicationContext());
		View mCustomControls = mLayoutInflater.inflate(R.layout.control, null);
		LayoutParams layoutCustomControlsParams = new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		addContentView(mCustomControls, layoutCustomControlsParams);
		
		Log.d(TAG, "Exit initCustomCameraControlsFromLayout()");
	}

	private void releaseCameraResources() 
	{
		Log.d(TAG, "Enter releaseCameraResources()");

		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
			mCameraPreview = null;
		}
		
		inCameraPreviewMode = false;
		
		Log.d(TAG, "Exit releaseCameraResources()");

	}

	private void startCameraPreview() 
	{
		Log.d(TAG, "Enter startCameraPreview()");
		
		if (!inCameraPreviewMode)
		{
			if(releaseAllCameraResources)
			{
				initCameraAndPreview();
			}
			else
			{
				if(mCamera == null)
				{
					initCameraAndPreview();
				}
				else
				{
					mCamera.startPreview();
					inCameraPreviewMode = true;
				}
			}
			
			Log.d(TAG, "Started Camera Preview()");
		}
		
		Log.d(TAG, "Exit startCameraPreview()");
	}

	private void stopCameraPreview() 
	{
		Log.d(TAG, "Enter stopCameraPreview()");
		
		if (inCameraPreviewMode && mCamera != null) 
		{
			if(releaseAllCameraResources)
			{
				releaseCameraResources();
			}
			else
			{
				if(mCamera != null)
				{
					mCamera.stopPreview();
				}
				
				inCameraPreviewMode = false;
			}
			
			Log.d(TAG, "Stoped Camera Preview()");
		}
		
		Log.d(TAG, "Exit stopCameraPreview()");
	}

	/*
	 * METHODS TO HANDLE UI CHANGES
	 */

	private void handlingUIAfterTakePicture() 
	{
		Log.d(TAG, "Enter handlingUIAfterTakePicture()");

		mTakePictureButton.setVisibility(View.GONE);
		mGrayModalInfoLayout.setVisibility(View.GONE);

		mSquareMarksImageView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.camerabgnomarks));
				
		byte[] imageSource = readFromLocalStorage(isFrontOfCheck ? getFrontCheckFilename() : getBackCheckFilename());
				
		Bitmap imagePreview = BitmapFactory.decodeByteArray(imageSource, 0, imageSource.length);
		
		mPreviewCheckImageView.setImageBitmap(imagePreview);
		mPreviewCheckImageView.setVisibility(View.VISIBLE);
		mContinuebutton.setVisibility(View.VISIBLE);
		mRetakePictureButton.setVisibility(View.VISIBLE);

		inCheckPicturePreview = true;
		enableButtons();
		
		Log.d(TAG, "Exit handlingUIAfterTakePicture()");
	}

	private void handlingUIAfterAcceptPicture() 
	{
		Log.d(TAG, "Enter handlingUIAfterAcceptPicture()");
		
		startCameraPreview();
		
		if (isFrontOfCheck) {
			mFrontCheckButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.frontunchecked));
			mFrontTitleTextView.setText(getResources().getString(
					R.string.titlefrontcheck));
		} else {
			mFrontCheckButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.frontchecked));
			mFrontTitleTextView.setText(getResources().getString(
					R.string.titlebackcheck));
		}

		mTakePictureButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.capture));
		
		mPreviewCheckImageView.setImageBitmap(null);
		mPreviewCheckImageView.setVisibility(View.GONE);
		mContinuebutton.setVisibility(View.GONE);
		mRetakePictureButton.setVisibility(View.GONE);
		mGrayModalInfoLayout.setVisibility(View.GONE);

		mTakePictureButton.setVisibility(View.VISIBLE);
		mGrayModalInfoLayout.setVisibility(View.VISIBLE);
		
		mSquareMarksImageView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.camerabgmarks));

		enableButtons();
		
		Log.d(TAG, "Exit handlingUIAfterAcceptPicture()");
	}

	private void handlingUICountingDown() 
	{
		Log.d(TAG, "Enter handlingUICountingDown()");

		mGrayModalInfoLayout.setVisibility(View.GONE);
		mTakePictureButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.captureblue));

		mCounterTextView.setText(R.string.titleCounter);
		mGrayModalCounterLayout.setVisibility(View.VISIBLE);

		disableButtons();
		
		Log.d(TAG, "Exit handlingUICountingDown()");
	}

	private void handlingUIRetakeOnClick() 
	{
		Log.d(TAG, "Enter handlingUIRetakeOnClick()");

		startCameraPreview();
		
		mPreviewCheckImageView.setVisibility(View.GONE);
		mPreviewCheckImageView.setImageBitmap(null);

		mContinuebutton.setVisibility(View.GONE);
		mRetakePictureButton.setVisibility(View.GONE);

		mTakePictureButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.capture));
		mTakePictureButton.setVisibility(View.VISIBLE);

		mSquareMarksImageView.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.camerabgmarks));

		if (isFrontOfCheck) {
			mFrontTitleTextView.setText(getResources().getString(
					R.string.titlefrontcheck));
		} else {
			mFrontTitleTextView.setText(getResources().getString(
					R.string.titlebackcheck));
		}
		
		inCheckPicturePreview = false;
		
		mGrayModalInfoLayout.setVisibility(View.VISIBLE);
		
		Log.d(TAG, "Exit handlingUIRetakeOnClick()");
	}

	private void handlingUIRetakeOnlyBackCheck() 
	{
		Log.d(TAG, "Enter handlingUIRetakeOnlyBackCheck()");

		mFrontTitleTextView.setText(getResources().getString(
				R.string.titlebackcheck));
		mFrontCheckButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.frontchecked));
		
		Log.d(TAG, "Exit handlingUIRetakeOnlyBackCheck()");
	}

	private void handlingUIAfterOpenHelpModal() 
	{
		Log.d(TAG, "Enter handlingUIAfterOpenHelpModal()");

		mBlockScreenImageView.setVisibility(View.VISIBLE);
		mHelpModalLayout.setVisibility(View.VISIBLE);
		mHelpModalLayout.bringToFront();
		mTakePictureButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.captureblue));

		disableButtons();
		
		Log.d(TAG, "Exit handlingUIAfterOpenHelpModal()");
	}
	
	private void handlingUIAfterOpenCancelDepositModal()
	{
		mBlockScreenImageView.setVisibility(View.VISIBLE);
		mCancelDepositLayout.setVisibility(View.VISIBLE);
		mCancelDepositLayout.bringToFront();

		disableButtons();
	}

	private void handlingUIAfterCloseHelpModal() 
	{
		Log.d(TAG, "Enter handlingUIAfterCloseHelpModal()");

		mHelpModalLayout.setVisibility(View.GONE);
		mBlockScreenImageView.setVisibility(View.GONE);
		mTakePictureButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.capture));

		enableButtons();
		
		Log.d(TAG, "Exit handlingUIAfterCloseHelpModal()");
	}
	
	private void handlingUIAfterCloseCancelDepositModal()
	{
		Log.d(TAG, "Enter handlingUIAfterCloseCancelDepositModal()");

		mCancelDepositLayout.setVisibility(View.GONE);
		mBlockScreenImageView.setVisibility(View.GONE);
		
		enableButtons();
		
		Log.d(TAG, "Enter handlingUIAfterCloseCancelDepositModal()");
	}

	private void enableButtons() 
	{
		Log.d(TAG, "Enter enableButtons()");

		// Enable buttons
		mTakePictureButton.setEnabled(true);
		mBackButton.setEnabled(true);
		mInfoButton.setEnabled(true);
		mCancelButton.setEnabled(true);
		mRetakePictureButton.setEnabled(true);
		mContinuebutton.setEnabled(true);
		mSelectAccountButton.setEnabled(true);
		mEnterAmountButton.setEnabled(true);
		
		Log.d(TAG, "Exit enableButtons()");
	}

	private void disableButtons() 
	{
		Log.d(TAG, "Enter disableButtons()");

		// Disable buttons
		mTakePictureButton.setEnabled(false);
		mBackButton.setEnabled(false);
		mInfoButton.setEnabled(false);
		mCancelButton.setEnabled(false);
		mRetakePictureButton.setEnabled(false);
		mContinuebutton.setEnabled(false);
		mSelectAccountButton.setEnabled(false);
		mEnterAmountButton.setEnabled(false);
		
		Log.d(TAG, "Exit disableButtons()");
	}

	private void handlingUIViewPicture() 
	{
		Log.d(TAG, "Enter handlingUIViewPicture()");
		
		if (MyCustomCameraPlugin.ACTION_VIEW_FRONT == currentAction 
				|| MyCustomCameraPlugin.ACTION_CAPTURE_FRONT == currentAction){
			mFrontCheckButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.frontunchecked));
		} 
		else {
			mFrontCheckButton.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.frontchecked));
		}
		
		handlingUIAfterTakePicture();
		
		Log.d(TAG, "Exit handlingUIViewPicture()");
	}

	private byte[] readFromLocalStorage(String filename) 
	{
		Log.d(TAG, "Enter readFromLocalStorage(" + filename + ")");

		byte[] data = new byte[0];
		File file = new File(getFilesDir() + "/" + filename);
		if(file.exists())
		{	try{
				
				FileInputStream fis = openFileInput(filename);
				
				data = new byte[(int)file.length()];
				fis.read(data);
				fis.close();
			}
			catch(Exception ex)
			{
				Log.e(TAG, "Error reading image from local storage: " + filename);
			}
		}
		
		Log.d(TAG, "Exit readFromLocalStorage(" + filename + ")");

		return data;
	}
	
	private void localStoreBytes(byte[] byteArray, String FILENAME) {
		
		Log.d(TAG, "Enter localStoreBytes(" + FILENAME + ")");

		try {
			FileOutputStream fos = openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			fos.write(byteArray);
			fos.close();
		} catch (Exception ex) {
			Log.d(TAG, "problem storing " + FILENAME);
		}
		
		Log.d(TAG, "Exit localStoreBytes(" + FILENAME + ")");
	}
	
	private String getFrontCheckFilename()
	{
		Log.d(TAG, "Enter getFrontCheckFilename()");
		Log.d(TAG, "Exit getFrontCheckFilename()");
		return getResources().getString(R.string.FRONT_CHECK_FILENAME);
	}
	
	private String getBackCheckFilename()
	{
		Log.d(TAG, "Enter getBackCheckFilename()");
		Log.d(TAG, "Exit getBackCheckFilename()");
		return getResources().getString(R.string.BACK_CHECK_FILENAME);
	}
	
	private Camera getCameraInstance()
	{
		Log.d(TAG, "Enter getCameraInstance()");

	    Camera c = null;
	    
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    	Log.e(TAG, "Camera is not available (in use or does not exist): " + e);
	    }
	    
		Log.d(TAG, "Exit getCameraInstance()");

	    return c; // returns null if camera is unavailable
	}
}