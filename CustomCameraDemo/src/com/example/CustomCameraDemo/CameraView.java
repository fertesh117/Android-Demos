package com.example.CustomCameraDemo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "CameraView";
	SurfaceHolder mHolder;
	int width;
	int height;

	Camera mCamera;

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holderCreation();
		
	}

	public CameraView(Context context) {
		super(context);
		holderCreation();
	}

	public void takePicture(Camera.ShutterCallback shutter,
			Camera.PictureCallback raw,Camera.PictureCallback postview, Camera.PictureCallback jpeg) {
		mCamera.takePicture(shutter, raw, postview, jpeg);
	}
	public void autofocus(Camera.AutoFocusCallback cb){
		mCamera.autoFocus(cb);
	}
	

	public void holderCreation() {
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where to
		// draw.
		initCamera();
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		width = w;
		height = h;
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		if (mCamera==null){
			initCamera();
		}
		Camera.Parameters parameters = mCamera.getParameters();
		 parameters.setPreviewSize(w, h);
		mCamera.setParameters(parameters);
		mCamera.startPreview();
	}
	public void startPreview(){
		mCamera.startPreview();
	}
	private void initCamera(){
		mCamera = Camera.open();
		Parameters params = mCamera.getParameters();
		// If we aren't landscape (the default), tell the camera we want
		// portrait mode
		if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			params.set("orientation", "portrait"); // "landscape"
			// And Rotate the final picture if possible
			// This works on 2.0 and higher only
			// params.setRotation(90);
			// Use reflection to see if it exists and to call it so you can
			// support older versions
			try {
				Method rotateSet = Camera.Parameters.class.getMethod(
						"setRotation", new Class[] { Integer.TYPE });
				Object arguments[] = new Object[] { new Integer(90) };
				rotateSet.invoke(params, arguments);
			} catch (NoSuchMethodException nsme) {
				// Older Device
				Log.v(TAG, "No Set Rotation");
			} catch (IllegalArgumentException e) {
				Log.v(TAG, "Exception IllegalArgument");
			} catch (IllegalAccessException e) {
				Log.v(TAG, "Illegal Access Exception");
			} catch (InvocationTargetException e) {
				Log.v(TAG, "Invocation Target Exception");
			}
		}
		mCamera.setParameters(params);
	}
	public void clearSurface(){
		Canvas canvas = new Canvas();
		canvas.drawColor(Color.BLACK);
		invalidate();
		draw(canvas);
		invalidate();
	}

}
