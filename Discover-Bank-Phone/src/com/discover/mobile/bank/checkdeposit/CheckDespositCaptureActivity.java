package com.discover.mobile.bank.checkdeposit;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;

public class CheckDespositCaptureActivity extends BaseActivity implements SurfaceHolder.Callback {
	private SurfaceView cameraPreview;
	private SurfaceHolder previewHolder;
	private Camera camera;

	private boolean inPreview = false;
	private boolean cameraConfigured = false;

//	@Override
//	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
//		final View mainView = inflater.inflate(R.layout.check_deposit_capture, null);
//
//		cameraPreview = (SurfaceView)mainView.findViewById(R.id.camera_view);
//
//		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		
//		previewHolder = cameraPreview.getHolder();
//		previewHolder.addCallback(this);
//
//		getActivity().getWindow().setFormat(PixelFormat.UNKNOWN);
//		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		return mainView;
//	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.check_deposit_capture);
		cameraPreview = (SurfaceView)findViewById(R.id.camera_view);
				
		previewHolder = cameraPreview.getHolder();
		previewHolder.addCallback(this);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		camera = Camera.open();

		startPreview();

		setCameraDisplayOrientation(this, 0, camera);

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
		camera.setDisplayOrientation(result);
	}

	@Override
	public void onPause() {
		super.onPause();
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
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
				parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

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
		initPreview(width, height);
		startPreview();
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

}
