package com.discover.rdc.camera.plugin;

import java.util.List;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyCustomCameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
	private static final String TAG = "RDC Camera Preview";
	
	private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;
    
	public MyCustomCameraPreview(Context context, Camera camera)
	{
		super(context);
		
		Log.d(TAG, "Enter MyCustomCameraPreview()");

        mCamera = camera;
        mContext = context;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
		Log.d(TAG, "Exit MyCustomCameraPreview()");
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		Log.d(TAG, "Enter surfaceChanged()");

		// If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try 
        {
            mCamera.stopPreview();
        } 
        catch (Exception e){
        	// ignore: tried to stop a non-existent preview
        	Log.d(TAG, "Error tried to stop a non-existent preview: " + e.getMessage());
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        configureCamera();
        
        // start preview with new settings
        try
        {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } 
        catch (Exception e)
        {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
        
		Log.d(TAG, "Exit surfaceChanged()");
	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.d(TAG, "Enter surfaceCreated()");

		// The Surface has been created, now tell the camera where to draw the preview.
		try 
        {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } 
        catch (Exception e) 
        {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
		
		Log.d(TAG, "Exit surfaceCreated()");
	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{		
		Log.d(TAG, "Enter surfaceDestroyed()");
		Log.d(TAG, "Exit surfaceDestroyed()");
	}
	
	private void configureCamera()
	{
		Log.d(TAG, "Enter configureCamera()");

		try 
        {
			Camera.Parameters parameters = mCamera.getParameters();
	        
			// Set picture size.
			Camera.Size pictureSize = get640x480OrSmallestPictureSize(parameters);
			parameters.setPictureSize(pictureSize.width, pictureSize.height);
			parameters.setPictureFormat(PixelFormat.JPEG);
			mCamera.setParameters(parameters);
	
			// Set a preview size that is closest to the viewfinder height and has
			// the right aspect ratio.
			List<Size> sizes = parameters.getSupportedPreviewSizes();
			Size original = parameters.getPreviewSize();
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			((MyCustomCameraActivity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			
			int screenHeight = displaymetrics.heightPixels;
			int screenWidth = displaymetrics.widthPixels;
			
			Size optimalSize = getOptimalPreviewSize(sizes, screenWidth, screenHeight);
			
			if(optimalSize == null)
			{	
				Log.v(TAG, "No preview size change. Using original preview size: " + original.width + "x" + original.height);
			}
			else if (!original.equals(optimalSize)) {
				Log.v(TAG, "Change Preview Size set to: " + optimalSize.width + "x" + optimalSize.height);
				parameters.setPreviewSize(optimalSize.width, optimalSize.height);
	    	  
				// Zoom related settings will be changed for different preview
				// sizes, so set and read the parameters to get lastest values
				mCamera.setParameters(parameters);
				Log.v(TAG, "Camera Parameters for Preview Size set to: " + optimalSize.width + "x" + optimalSize.height);			    
			}
			
			
        } 
        catch (Exception e) 
        {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
		
		Log.d(TAG, "Exit configureCamera()");
	}
	
	/**
	   * calculate optimal preview size from given parameters
	   * <br>
	   * referenced: http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/graphics/CameraPreview.html
	   * 
	   * @param sizes obtained from camera.getParameters().getSupportedPreviewSizes()
	   * @param w
	   * @param h
	   * @return
	   */
	public static Size getOptimalPreviewSize(List<Size> sizes, int w, int h)
	{
		Log.d(TAG, "Enter getOptimalPreviewSize()");

		final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        
		Log.d(TAG, "Exit getOptimalPreviewSize()");

        return optimalSize;
	}
	
	
	private Camera.Size get640x480OrSmallestPictureSize(Camera.Parameters parameters) 
	{
		Log.d(TAG, "Enter get640x480OrSmallestPictureSize()");

		Camera.Size result = null;
		
		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			
			if(size.width == MyCustomCameraActivity.PICTURE_WIDTH_640 && size.height == MyCustomCameraActivity.PICTURE_HEIGTH_480)
			{
				//Return 640x480 only if is supported
				result = size;
				break;
			}
			else
			{
				//if 640x480 is not supported, return the smallest size
				if (result == null) 
				{
					result = size;
				} 
				else 
				{
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea < resultArea) {
						result = size;
					}
				}
			}
		}
		
		Log.d(TAG, "Exit get640x480OrSmallestPictureSize()");

		return (result);
	}
}
