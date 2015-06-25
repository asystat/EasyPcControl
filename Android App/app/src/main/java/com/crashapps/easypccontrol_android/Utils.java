package com.crashapps.easypccontrol_android;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;




public class Utils {

	public static final String REDDITOR="Redditor";	
	
	public static int dpToPixels(Activity context, int dp) {
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return (int) ((dp * metrics.density) + 0.5);
	}

	public static int PixelsToDp(Activity context, int px) {
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return (int) ((px / metrics.density) + 0.5);
	}

	public static boolean hasAds(Context c) {
		boolean purchased=com.crashapps.easypccontrol_android.utils.RetardedUtils.getPreference(PurchaseActivity.NO_ADS, false, c);
		boolean redditor=com.crashapps.easypccontrol_android.utils.RetardedUtils.getPreference(REDDITOR, false, c);
		return (!purchased && !redditor);
	}

	/*public static void AppFuelHelper(Activity c, PromoSize bannerSize, String appKey, int mainLayoutId) {
		
		
		//Step 2: Create a PromoView instance
		PromoView promo = new PromoView(c, bannerSize, appKey);
		promo.setPosition(PromoPosition.TOP_CENTER);
		
		//Step 3: Call the request method. In this case, we set the test mode to true;
		boolean isTestMode = true;
		promo.request(isTestMode);
		
		//Step 4: Add the PromoView to the UI
		RelativeLayout mainLayout = (RelativeLayout) c.findViewById(mainLayoutId);
		mainLayout.addView(promo);
	}*/
}
