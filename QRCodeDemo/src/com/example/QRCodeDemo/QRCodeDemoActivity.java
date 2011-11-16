package com.example.QRCodeDemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class QRCodeDemoActivity extends Activity implements OnClickListener {
	EditText editText;
	Button button;
	ImageView imageView;
	URL url;
	String encode;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		button=(Button)findViewById(R.id.button1);
		editText=(EditText)findViewById(R.id.editText1);
		imageView=(ImageView)findViewById(R.id.imageView1);
		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v==button) {
			encode=editText.getText().toString();
			String dir="http://chart.apis.google.com/chart?cht=qr&chs=350x350&chld=L&choe=UTF-8&chl="+encode;
			imageView.setImageBitmap(traerImagenDeHttp(dir));
		}

	}
	public Bitmap traerImagenDeHttp(String direccionWeb){
			try {
        	Log.e("src",direccionWeb);
        	URL url = new URL(direccionWeb);
        	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        	connection.setDoInput(true);
        	connection.connect();
        	InputStream input = connection.getInputStream();
        	Bitmap miBitmap = BitmapFactory.decodeStream(input);
        	input.close();
        	return miBitmap;
    	} catch (IOException e) {
        	e.printStackTrace();
        	Log.e("Exception",e.getMessage());
        	return null;
    	}

	}
}