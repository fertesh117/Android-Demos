package com.example.LocationDemo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ButtonLocationActivity extends Activity implements OnClickListener {

	private Button botonStart, botonGet, botonStop, botonNetState, botonMapa,botonDireccion;
	private TextView textLat, textLon, textPre, textProv, textUltAct,textDireccion;
	private Location location;
	private Address address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_location);
		initComponent();
		botonStart.setOnClickListener(this);
		botonGet.setOnClickListener(this);
		botonStop.setOnClickListener(this);
		botonNetState.setOnClickListener(this);
		botonMapa.setOnClickListener(this);
		botonDireccion.setOnClickListener(this);
	}

	private void initComponent() {
		botonStart = (Button) findViewById(R.id.buttonStart);
		botonGet = (Button) findViewById(R.id.buttonGetL);
		botonStop = (Button) findViewById(R.id.buttonStop);
		botonNetState = (Button) findViewById(R.id.buttonNetState);
		botonMapa = (Button)findViewById(R.id.buttonMap);
		botonDireccion = (Button) findViewById(R.id.buttonDir);
		textLat = (TextView) findViewById(R.id.textViewLatitude);
		textLon = (TextView) findViewById(R.id.textViewLongitude);
		textPre = (TextView) findViewById(R.id.textViewPrecision);
		textProv = (TextView) findViewById(R.id.textViewProveedor);
		textUltAct = (TextView) findViewById(R.id.textViewTiempoUltAct);
		textDireccion = (TextView) findViewById(R.id.textViewDir);
	}

	@Override
	public void onClick(View v) {
		if (v == findViewById(R.id.buttonStart)) {
			MyLocation.startListening(this);
		}
		if (v == findViewById(R.id.buttonGetL)) {
			try {
				UpdateTextView(MyLocation.getLocation());
			} catch (NullPointerException e) {
				Toast.makeText(this, "No hay ubicacion", 50).show();
			}
		}
		if (v == findViewById(R.id.buttonStop)) {
			MyLocation.stopListening();
		}
		if (v == findViewById(R.id.buttonNetState)) {
			Toast.makeText(this, MyLocation.getLocationState(), 500).show();
		}
		if (v == findViewById(R.id.buttonMap)) {
			Intent myIntent = new Intent(this, MapaActivity.class);
			if (location!=null){
			double d[]={location.getLatitude(),location.getLongitude()};
			myIntent.putExtra("location",d);}
			startActivity(myIntent);
		}
		
		//Obtener la direccion fisica.
		if (v==findViewById(R.id.buttonDir)){
			try {
				address = getAddressForLocation(this, location);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(address==null){
					return;
				}
				textDireccion.setText("Direccion: " + address.getAddressLine(0) + "\nArea Administrativa: " +  address.getAdminArea() + "\nPais: " + address.getCountryName());
			}
		}

	}

	@Override
	protected void onDestroy() {
		if (isFinishing()) {
			MyLocation.stopListening();
		}
		super.onDestroy();
	}

	private void UpdateTextView(Location location) {
		
		textLat.setText(location.getLatitude() + "");
		textLon.setText(location.getLongitude() + "");
		textPre.setText(location.getAccuracy() + "");
		textProv.setText(location.getProvider() + "");
		textUltAct.setText((System.currentTimeMillis() - location.getTime())
				/ 1000 + " segundos");
		this.location=location;

	}
	public Address getAddressForLocation(Context context, Location location) throws IOException {

        if (location == null) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        int maxResults = 1;

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

        if (addresses.size() == 1) {
            return addresses.get(0);
        } else {
            return null;
        }
    }

}
