package com.example.LocationDemo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Fernando
 *
 */
public class MyLocation {
	public static Location currentBestLocation;
	private static LocationManager lm;
	private static boolean gps_enabled = false;
	private static boolean network_enabled = false;
	public static int updateTime = 1000;

	/**
	 * Inicia el location listener y obtiene la mejor ubicacion que no pase de
	 * los 2 minutos de antiguedad
	 * 
	 * @param context
	 * @return retorna true si se logra registrar por lo menos un proveedor, de lo contrario, falso
	 */
	public static boolean startListening(Context context) {
		// I use LocationResult callback class to pass location value from
		// MyLocation to user code.
		if (lm == null)
			lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		System.out.println(lm.getAllProviders().toString());

		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}
		try {
			network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		// don't start listeners if no provider is enabled
		if (!gps_enabled && !network_enabled)
			return false;

		if (gps_enabled) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateTime,
					0, locationListener);
			System.out.println("GPS Registered");
		}
		if (network_enabled) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					updateTime, 0, locationListener);
			System.out.println("Network Registered");
		}

		return true;
	}
	

	/**
	 * @return retorna la mejor ubicacion obtenida dentro de los 2 minutos o la ultima ubicacion conocida.
	 */
	public static Location getLocation() {
		Location gps, net;
		if (currentBestLocation == null) {
			gps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			net = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (gps_enabled & network_enabled) {
				if (isBetterLocation(gps, net)) {
					currentBestLocation = gps;
				} else {
					currentBestLocation = net;
				}
			} else if (gps_enabled) {
				currentBestLocation = gps;
			} else if (network_enabled) {
				currentBestLocation = net;
			}
		}
		return currentBestLocation;
	}

	/**
	 * Detiene los listeners
	 */
	public static void stopListening() {
		try {
			lm.removeUpdates(locationListener);
		} catch (NullPointerException e) {

		}
	}

	/**
	 * @return un string que indica el estado del GPS y red
	 */
	public static String getLocationState() {
		StringBuilder s = new StringBuilder("network: ");
		if (network_enabled) {
			s.append("ON");
		} else {
			s.append("OFF");
		}
		s.append("\ngps: ");
		if (gps_enabled) {
			s.append("ON");
		} else {
			s.append("OFF");
		}
		return s.toString();
	}

	static LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			Log.d("Location", "Location Received: " + location.getLatitude() + "; " + location.getLongitude());
			if (isBetterLocation(location, currentBestLocation)) {
				currentBestLocation = location;
			}

		}
	};

	private static final int TWO_MINUTES = 2 * 60 * 1000;

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected static boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}
