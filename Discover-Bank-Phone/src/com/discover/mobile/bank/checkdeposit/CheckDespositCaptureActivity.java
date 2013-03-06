package com.discover.mobile.bank.checkdeposit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;

public class CheckDespositCaptureActivity extends BaseActivity implements SurfaceHolder.Callback {
	private final String TAG = CheckDespositCaptureActivity.class.getSimpleName();
	
	//BUTTONS
	private Button captureButton;
	private Button retakeButton;
	
	private SurfaceView cameraPreview;
	private SurfaceHolder previewHolder;
	private Camera camera;
	private CameraCountdownTask timerTask;

	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	protected boolean isPaused = false;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.check_deposit_capture);
		cameraPreview = (SurfaceView)findViewById(R.id.camera_preview);
				
		previewHolder = cameraPreview.getHolder();
		previewHolder.addCallback(this);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		timerTask = new CameraCountdownTask();
		
		setupButtons();
		setupPreviewTouchToFocus();
	}
	
	private void setupPreviewTouchToFocus() {
		cameraPreview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				focusCamera();
			}
		});
	}
	
	private void setupButtons() {
		captureButton = (Button)findViewById(R.id.capture_button);
		retakeButton = (Button)findViewById(R.id.retake_button);
		
		captureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if(!timerTask.isRunning){
					timerTask = new CameraCountdownTask();
					timerTask.execute();
				}
			}
		});
		
		retakeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				camera.stopPreview();
				camera.startPreview();
				setupCameraParameters();
			}
		});
	}
	
	/**
	 * If the async task is not finished when the activity stops, we need to cancel it.
	 */
	@Override
	public void onStop() {
		super.onStop();
		final TextView countdownLabel = (TextView)findViewById(R.id.countdown_label);
		countdownLabel.setVisibility(View.GONE);
		countdownLabel.setText("3");
		timerTask.cancel(true);
	}
	
	private void takePicture() {
		camera.takePicture(null, null, mPicture);
	}

	private final PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(final byte[] data, final Camera camera) {
	    	
	        final File pictureFile = getOutputMediaFile();
	        if (pictureFile == null){
	            Log.d(TAG, "Error creating media file, check storage permissions: ");
	            return;
	        }

            final FileOutputStream fos;
	        try {
	        	fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	        } catch (final FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        } catch (final IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }
	        
	        Log.d(TAG, "PICTURE TAKEN");
	    }
	};
	
	/** Create a File for saving the image */

	private static File getOutputMediaFile(){

		final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "MyCameraApp");
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

		return mediaFile;
	}

	private void focusThenTakePicture() {
		 
		camera.autoFocus(new Camera.AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(final boolean success, final Camera camera) {
				camera.cancelAutoFocus();
				takePicture();
			}
		});
	}
	
	private void focusCamera() {
		camera.autoFocus(new Camera.AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(final boolean success, final Camera camera) {
				camera.cancelAutoFocus();
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		isPaused = false;
		camera = Camera.open();
		startPreview();

		setCameraDisplayOrientation(this, 0, camera);
		setupCameraParameters();
	}
	
	private void setupCameraParameters() {
		final Camera.Parameters parameters = camera.getParameters();
		parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
		parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
		camera.setParameters(parameters);
	}

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

	private void startPreview() {
		if(camera != null && cameraConfigured) {
			camera.startPreview();
			inPreview = true;
		}
	}

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
		// TODO Auto-generated method stub
		return null;
	}
	
	public class CameraCountdownTask extends AsyncTask<Void, Void, Void> {
		private TextView countdownLabel;
		private int count;
		public boolean isRunning = false;
		
		@Override
		protected void onPreExecute() {
			isRunning = true;
			countdownLabel = (TextView)findViewById(R.id.countdown_label);
			countdownLabel.setVisibility(View.VISIBLE);
			count = Integer.parseInt(countdownLabel.getText().toString());
		}
		
		@Override
		protected Void doInBackground(final Void... params) {
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {

			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(final Void result) {
			count--;
			
			countdownLabel.setText("" + count);
			
			if(count < 1){
				countdownLabel.setVisibility(View.GONE);
				countdownLabel.setText("3");
				focusThenTakePicture();
				isRunning = false;
			}
			else{
				timerTask = new CameraCountdownTask();
				timerTask.execute();
			}
		}
		
	}

}
