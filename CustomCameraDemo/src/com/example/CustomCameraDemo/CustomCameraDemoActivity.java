package com.example.CustomCameraDemo;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CustomCameraDemoActivity extends Activity {
	/** Called when the activity is first created. */
	ImageButton botonCamara, botonEnviar;
	ImageView imageView;
	String filePath;
	Context context;
	ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		pd = new ProgressDialog(this);
		initComponents();
		setListeners();
	}

	private void initComponents() {
		botonCamara = (ImageButton) findViewById(R.id.imageButton1);
		botonEnviar = (ImageButton) findViewById(R.id.imageButton2);
		imageView = (ImageView) findViewById(R.id.imageView1);

	}

	private void setListeners() {
		botonCamara.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(getApplicationContext(),
						CameraPreview.class), 1);

			}
		});
		botonEnviar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				TareaEnviar te = new TareaEnviar(context);
				
				pd.setTitle("Enviando");
				pd.setMessage("Se esta enviando la imagen");
				pd.show();
				te.execute(filePath);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Bitmap pic = null;
				filePath = data.getStringExtra(CameraPreview.FILE_PATH);
				pic = BitmapFactory.decodeFile(filePath);
				imageView.setImageBitmap(pic);
				imageView.invalidate();
			}
		}
	}

	class TareaEnviar extends AsyncTask<String, Void, Void> {
		Context context;

		public TareaEnviar(Context context) {
			this.context = context;
		}

		@Override
		protected Void doInBackground(String... arg0) {

			File file = new File(arg0[0]);
			try {
				MyHttpUpload.Upload(file);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(context, "Error?", Toast.LENGTH_SHORT).show();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			pd.dismiss();
			super.onPostExecute(result);
		}

	}
}