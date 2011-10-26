package com.example.LocationDemo;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class MapaActivity<MyItemizedOverlay> extends MapActivity {
	MapView mapView;
	MapController mapController;
	List<Overlay> mapOverlays;
	Drawable drawable;
	ItemizedOverlay itemizedOverlay;
	double d[];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		Bundle b = getIntent().getExtras();
		//obtiene el mapa
		mapView = (MapView) findViewById(R.id.mapview);
		//muestra el control del zoom
		mapView.setBuiltInZoomControls(true);
		
		
		//revisa si tiene coordenadas
		if (b != null) {
			//obtiene las coordenadas
			d = b.getDoubleArray("location");
			//obtiene el mapOverlay
			mapOverlays = mapView.getOverlays();
			//obtiene el icono a mostrar
			drawable = this.getResources()
					.getDrawable(R.drawable.androidmarker);
			//instancia el overlay
			itemizedOverlay = new ItemizedOverlay(drawable);
			//convierte las coordenadas en un geopoint
			GeoPoint point = new GeoPoint((int) (d[0] * 1e6),
					(int) (d[1] * 1e6));
			//adjunta el geopoint al punto a mostrar
			OverlayItem overlayitem = new OverlayItem(point, "", "");
			//agrega el punto al icono
			itemizedOverlay.addOverlay(overlayitem);
			//agrega el icono en el mapa
			mapOverlays.add(itemizedOverlay);
			//obtiene el controlador del mapa
			mapController = mapView.getController();
			//centra la vista en el punto
			mapController.setCenter(point);
			
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
