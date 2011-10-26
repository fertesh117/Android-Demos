package com.example.CustomCameraDemo;

import java.io.File;
import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

public class CameraPreview extends Activity {
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	boolean interno = false;
	CameraPreview cameraPreview = this;
	CameraView cameraView;
	ImageView imv;
	ImageButton pictureButton, saveButton, discardButton;
	Bitmap bmp;
	Activity mParent = getParent();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera_preview);
		initComponents();
	}

	private void initComponents() {
		pictureButton = (ImageButton) findViewById(R.id.Button01);
		saveButton = (ImageButton) findViewById(R.id.imageButton1);
		discardButton = (ImageButton) findViewById(R.id.imageButton2);
		imv = (ImageView) this.findViewById(R.id.ImageView01);
		cameraView = (CameraView) this.findViewById(R.id.CameraView01);

		pictureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				pictureButton.setEnabled(false);
				cameraView.autofocus(autofocusCallback);
			}
		});
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String state = Environment.getExternalStorageState();
				if (interno) {
					// Quizas revisar capacidad
					SaveToInternalStorage();
				} else {
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						SaveToExternalStorage();
					} else {
						Toast.makeText(getApplicationContext(),
								"El medio no esta disponible",
								Toast.LENGTH_LONG).show();
					}
				}
				
				finish();
			}
		});
		discardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				imv.setImageBitmap(null);
				imv.invalidate();
				cameraView.setVisibility(View.VISIBLE);
				cameraView.startPreview();
				cameraView.setVisibility(View.VISIBLE);
				bmp = null;
				pictureButton.setVisibility(View.VISIBLE);
				saveButton.setVisibility(View.GONE);
				discardButton.setVisibility(View.GONE);

			}
		});
	}

	// From the Camera.PictureCallback
	Camera.PictureCallback pictureCallBack = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			pictureButton.setEnabled(true);
			pictureButton.setVisibility(View.GONE);
			saveButton.setVisibility(View.VISIBLE);
			discardButton.setVisibility(View.VISIBLE);
			cameraView.setVisibility(View.INVISIBLE);
			imv.setImageBitmap(bmp);
			imv.invalidate();
		}
	};

	Camera.AutoFocusCallback autofocusCallback = new Camera.AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean arg0, Camera camera) {
			cameraView.takePicture(null, null, null, pictureCallBack);
		}
	};

	String fileName = "Test";

	private void SaveToExternalStorage() {

		String PATH = Environment.getExternalStorageDirectory()
				+ "/Android/data/" + getPackageName() + "/cache/";
		Log.d("log_tag", "PATH: " + PATH);
		File file = new File(PATH);
		file.mkdirs();
		File outputFile = new File(file, fileName + ".jpg");
		FileOutputStream pfos;
		try {
			pfos = new FileOutputStream(outputFile);
			// Calidad de la foto 0=minimo 100=maximo
			bmp.compress(CompressFormat.JPEG, 70, pfos);
			pfos.flush();
			pfos.close();
			setResult(RESULT_OK,new Intent().putExtra(FILE_PATH, outputFile.getPath()));
			// Toast.makeText(getApplicationContext(), "Imagen Guardada",
			// Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error al guardar imagen",
					Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
		}
	}

	private void SaveToInternalStorage() {
		try {
			FileOutputStream fos = openFileOutput(fileName,
					Context.MODE_PRIVATE);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.close();
			setResult(RESULT_OK,new Intent().putExtra(FILE_PATH, fileName));
			// Toast.makeText(getApplicationContext(), "Imagen Guardada",
			// Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error al guardar imagen",
					Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
		}
	}

	public void setInternalStorage(boolean b) {
		interno = b;
	}

}
