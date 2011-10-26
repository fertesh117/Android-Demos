package com.example.CustomCameraDemo;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
	Context context = this.getApplication();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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
}

class TareaEnviar extends AsyncTask<String, Void, Void> {
Context context;
	public TareaEnviar(Context context){
		this.context = context;
	}
	@Override
	protected Void doInBackground(String... arg0) {
		String url = "http://14.240.132.90:81/imagen/CargaJava/RegistrarFoto.aspx";
		File file = new File(arg0[0]);
		try {
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new HttpPost(url);

			InputStreamEntity reqEntity = new InputStreamEntity(
					new FileInputStream(file), -1);
			reqEntity.setContentType("binary/octet-stream");
			reqEntity.setChunked(true); // Send in multiple parts if needed
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			Toast.makeText(context, "Enviado", Toast.LENGTH_SHORT).show();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}