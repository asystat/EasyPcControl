package com.crashapps.easypccontrol_android.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.crashapps.easypccontrol_android.R;


// TODO: Auto-generated Javadoc
/**
 * The Class RetardedUtils.
 */
public class RetardedUtils {
	
	/** The Constant PREF_FILE_NAME. */
	public static final String PREF_FILE_NAME = "EasyPcControl";

	/**
	 * Checks if is network available.
	 *
	 * @param context the context
	 * @return true, if is network available
	 */
	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Execute task.
	 *
	 * @param theTask the the task
	 */
	@SuppressLint({ "NewApi" })
	public static void executeTask(AsyncTask theTask) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			theTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					(Void[]) null);
		else
			theTask.execute((Void[]) null);
	}

	/**
	 * Checks if is service running.
	 *
	 * @param ctx the ctx
	 * @param theService the the service
	 * @return true, if is service running
	 */
	public static boolean isServiceRunning(Context ctx, Class theService) {
		ActivityManager manager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (theService.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	// Calcula distancia entre 2 coordenadas gps
	/**
	 * Gps2m.
	 *
	 * @param lat_a the lat_a
	 * @param lng_a the lng_a
	 * @param lat_b the lat_b
	 * @param lng_b the lng_b
	 * @return the double
	 */
	public static double gps2m(float lat_a, float lng_a, float lat_b,
			float lng_b) {
		float pk = (float) (180 / 3.14169);

		float a1 = lat_a / pk;
		float a2 = lng_a / pk;
		float b1 = lat_b / pk;
		float b2 = lng_b / pk;

		float t1 = FloatMath.cos(a1) * FloatMath.cos(a2) * FloatMath.cos(b1)
				* FloatMath.cos(b2);
		float t2 = FloatMath.cos(a1) * FloatMath.sin(a2) * FloatMath.cos(b1)
				* FloatMath.sin(b2);
		float t3 = FloatMath.sin(a1) * FloatMath.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		return 6366000 * tt;
	}

	/**
	 * Hide soft key board.
	 *
	 * @param activity the activity
	 */
	public static void hideSoftKeyBoard(Activity activity) {
		InputMethodManager inputManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		View currentFocus = activity.getCurrentFocus();
		if (currentFocus != null) {
			IBinder windowToken = currentFocus.getWindowToken();
			inputManager.hideSoftInputFromWindow(windowToken,
					InputMethodManager.HIDE_NOT_ALWAYS);
		} else {
			activity.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
	}

	/**
	 * Unbind drawables.
	 *
	 * @param view the view
	 */
	public static void unbindDrawables(View view) {
		if (view == null)
			return;
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			try {
				((ViewGroup) view).removeAllViews();
			} catch (UnsupportedOperationException mayHappen) {
				Log.e("Error:", mayHappen.getMessage());
			}
		}
	}

	/**
	 * Gets the color.
	 *
	 * @param position the position
	 * @param transparent the transparent
	 * @return the color
	 */
	public static int getColor(int position, boolean transparent) {
		position = position % 5;
		switch (position) {
		case 0:
			return Color.argb(transparent ? 100 : 255, 240, 91, 0);
		case 1:
			return Color.argb(transparent ? 100 : 255, 246, 117, 184);
		case 2:
			return Color.argb(transparent ? 100 : 255, 187, 187, 1);
		case 3:
			return Color.argb(transparent ? 100 : 255, 0, 156, 239);
		case 4:
			return Color.argb(transparent ? 100 : 255, 0, 177, 4);
		}
		return 0;
	}


	/**
	 * Sets the preference.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public static void setPreference(String key, Object value, Context c) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		// The SharedPreferences editor - must use commit() to submit changes
		SharedPreferences.Editor editor = preferences.edit();
		if (value instanceof Integer)
			editor.putInt(key, ((Integer) value).intValue());
		else if (value instanceof Long)
			editor.putLong(key, ((Long) value));
		else if (value instanceof String)
			editor.putString(key, (String) value);
		else if (value instanceof Boolean)
			editor.putBoolean(key, (Boolean) value);
		else if (value instanceof Float)
			editor.putFloat(key, (Float) value);
		editor.commit();
		Log.e("Preference set: ", key + " => " + value);
	}

	/**
	 * Gets the preference.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the preference
	 */
	public static int getPreference(String key, int defaultValue, Context c) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return preferences.getInt(key, defaultValue);
	}

	/**
	 * Gets the preference.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the preference
	 */
	public static String getPreference(String key, String defaultValue, Context c) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return preferences.getString(key, defaultValue);
	}

	/**
	 * Gets the preference.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the preference
	 */
	public static boolean getPreference(String key, boolean defaultValue, Context c) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return preferences.getBoolean(key, defaultValue);
	}
	

	public static long getPreference(String key, long defaultValue, Context c) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return preferences.getLong(key, defaultValue);
	}
	
	public static float getPreference(String key, float defaultValue, Context c) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return preferences.getFloat(key, defaultValue);
	}

	/**
	 * Dp to pixels.
	 *
	 * @param context the context
	 * @param dp the dp
	 * @return the int
	 */
	public static int dpToPixels(Activity context, int dp) {
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return (int) ((dp * metrics.density) + 0.5);
	}

	/**
	 * Pixels to dp.
	 *
	 * @param context the context
	 * @param px the px
	 * @return the int
	 */
	public static int PixelsToDp(Activity context, int px) {
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return (int) ((px / metrics.density) + 0.5);
	}

	/**
	 * Gets the html.
	 *
	 * @param url the url
	 * @return the html
	 * @throws ClientProtocolException the client protocol exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String getHtml(String url) throws ClientProtocolException,
			IOException {
		HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, 15000);
	    HttpConnectionParams.setSoTimeout(httpParams, 15000);
		HttpClient client = new DefaultHttpClient(httpParams);
	
		HttpGet request = new HttpGet(url);
		
		HttpResponse response = client.execute(request);

		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			str.append(line);
		}
		in.close();
		return str.toString();
	}

	/**
	 * Show alert.
	 *
	 * @param c the c
	 * @param title the title
	 * @param message the message
	 */
	public static void showAlert(Context c, String title, String message) {

		AlertDialog alertDialog = new AlertDialog.Builder(c).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		alertDialog.setIcon(R.drawable.ic_alert);
		alertDialog.show();

	}

	/**
	 * Gets the device id.
	 *
	 * @param context the context
	 * @return the device id
	 */
	public synchronized static String getDeviceId(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wm.getConnectionInfo().getMacAddress();
	}
	
	/**
	 * Checks if is online.
	 *
	 * @return true, if is online
	 */
	public static boolean isOnline(Context c) {
	    ConnectivityManager cm =
	        (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Need internet.
	 *
	 * @param c the c
	 * @return true, if successful
	 */
	public static boolean needInternet(Context c){
		if(!isOnline(c)){
			showAlert(c,"No internet","To add a device, you need to be connected to the Internet!");
			return true;
		}
		return false;
			
	}
	
	/**
	 * Gets the hue color.
	 *
	 * @param position the position
	 * @return the hue color
	 */
	public static float getHueColor(int position) {
		position = position % 5;
		float[] hvs = new float[3];
		switch (position) {
		case 0:
			Color.RGBToHSV(240, 91, 0, hvs);
			break;
		case 1:
			Color.RGBToHSV(246, 117, 184, hvs);
			break;
		case 2:
			Color.RGBToHSV(187, 187, 1, hvs);
			break;
		case 3:
			Color.RGBToHSV(0, 156, 239, hvs);
			break;
		case 4:
			Color.RGBToHSV(0, 177, 4, hvs);
			break;
		}
		return hvs[0];
	}

	/** The vib. */
	Vibrator vib=null;
	
	/**
	 * Vibrate.
	 *
	 * @param i the i
	 */
	public static void vibrate(int i, Context c) {
		Vibrator vib=(Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(i);
	}

}
