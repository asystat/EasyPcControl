package com.crashapps.easypccontrol_android.utils;

import java.util.Timer;
import java.util.TimerTask;

import com.crashapps.easypccontrol_android.R;
import com.google.ads.Ad;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdListener;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AdsManager {

	public static int SMALL_TIME_BETWEEN_X = 50000;
	public static int SMALL_TIME_BETWEEN = 20000;
	public static int SMALL_TIME_AD_SHOWED = 30000;
	public static int SMALL_TIME_STARTDELAY = 15000;
	public static int INTERSTITIAL_TIME_STARTDELAY = 90000;
	public static int INTERSTITIAL_TIME_RECURRENTDELAY = 200000;
	public static int SMALL_CLICK_RELOAD_TIME = 7200000; // 2 horas
	public static final String ADS_LEVEL="adsLevel"; //no usar aqui...es para el keystore
	
	
	private static boolean smallAdsOn = true;

	private static AdsManager instance = null;
	private Context mContext;
	private AdView adView;
	private FrameLayout bannerLayout;
	private Timer closedAdTimer, startDelayTimer, hideAdTimer,istartDelayTimer;

	private String admobAppKey;
	
	private AdsManager(Context c) {
		mContext = c;
		initAdmobKey();
	}
	
	private void initAdmobKey(){
		String firstPart="a151c43";
		//para despistar:
		String dummyPart="fla1345";
		String secondPart="7cfe91d4";
		admobAppKey=firstPart+secondPart;
	}

	public static AdsManager getInstance(Context c) {
		if (instance == null) {
			instance = new AdsManager(c);
		} else {
			instance.mContext = c;
		}
		return instance;
	}
	
	public void insertBannerDelayed(final FrameLayout layout){
		bannerLayout=layout;
		startDelayTimer = new Timer();
		startDelayTimer.schedule(new TimerTask() {			
			@Override
			public void run() {
				if(mContext==null)
					return;
				((Activity)mContext).runOnUiThread(insertAdDelayedTask);
			}
			  
		}, SMALL_TIME_STARTDELAY, 300000);
	}

	private Runnable insertAdDelayedTask = new Runnable() {
		public void run() {
			insertBanner(bannerLayout);
			startDelayTimer.cancel();
			startDelayTimer.purge();
		}
	};

	private Runnable hideAdTask = new Runnable() {
		public void run() {
			if (hideAdTimer != null) {
				hideAdTimer.cancel();
				hideAdTimer.purge();
			}

			if (mContext == null || bannerLayout == null)
				return;
			cerrarBanner(SMALL_TIME_BETWEEN);
		}
	};

	public void insertBanner(final FrameLayout layout) {
		if (propagandaClikada() || !smallAdsOn)
			return;
		bannerLayout = layout;
		// Create the adView
		adView = new AdView((Activity) mContext, AdSize.BANNER,admobAppKey);

		// Lookup your LinearLayout assuming itÕs been given
		// the attribute android:id="@+id/mainLayout"

		// Add the adView to it
		FrameLayout.LayoutParams alp = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		layout.addView(adView, alp);

		adView.setAdListener(new AdListener() {

			public void onDismissScreen(Ad arg0) {
				bannerClikado();
			}

			
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				// TODO Auto-generated method stub

			}

			public void onLeaveApplication(Ad arg0) {
				// TODO Auto-generated method stub

			}

			
			public void onPresentScreen(Ad arg0) {
				// TODO Auto-generated method stub

			}

			
			public void onReceiveAd(Ad arg0) {
				if (mContext == null)
					return;
				int adCloseSize = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, 17, mContext
								.getResources().getDisplayMetrics());
				FrameLayout.LayoutParams aclp = new FrameLayout.LayoutParams(
						adCloseSize, adCloseSize, Gravity.TOP | Gravity.RIGHT);
				if (mContext == null)
					return;
				ImageView closeAds = new ImageView(mContext);
				closeAds.setImageResource(R.drawable.closead);
				closeAds.setClickable(true);
				closeAds.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						if(hideAdTimer!=null){
							hideAdTimer.cancel();
							hideAdTimer.purge();
						}
						cerrarBanner(SMALL_TIME_BETWEEN_X);
					}
				});
				layout.addView(closeAds, aclp);

				hideAdTimer = new Timer();
				hideAdTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (mContext == null)
							return;
						((Activity) mContext).runOnUiThread(hideAdTask);
					}

				}, SMALL_TIME_AD_SHOWED, 300000);

			}
		});

		AdRequest request = new AdRequest();
		//request.addTestDevice("8F82693BC4E21965D14FBAE5F37BD942");
		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());
	}

	public boolean propagandaClikada() { // retorna true si es que se ha clikado
											// la publicidad en las ultimas 24
											// horas
		long lastClick=RetardedUtils.getPreference("lastClick", (long)0, mContext);
		return !(System.currentTimeMillis()	- lastClick > SMALL_CLICK_RELOAD_TIME);
	}

	protected void bannerClikado() {
		// TODO Auto-generated method stub
		RetardedUtils.setPreference("lastClick", System.currentTimeMillis(), mContext);
		bannerLayout.removeAllViews();
	}

	protected void cerrarBanner(int tiempoReOpen) {
		if (bannerLayout == null || adView == null)
			return;
		adView.stopLoading();
		bannerLayout.removeAllViews();

		closedAdTimer = new Timer();
		closedAdTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(mContext!=null){
					((Activity) mContext).runOnUiThread(insertAdTask);
				}
			}

		}, tiempoReOpen, 300000);
	}

	private Runnable insertAdTask = new Runnable() {
		public void run() {
			if (bannerLayout == null)
				return;
			insertBanner(bannerLayout);
			if (closedAdTimer != null) {
				closedAdTimer.cancel();
				closedAdTimer.purge();
			}
		}
	};

	public void destroyAds() {
		
		
		if (hideAdTimer != null) {
			hideAdTimer.cancel();
			hideAdTimer.purge();
			hideAdTimer = null;
		}
		if (closedAdTimer != null) {
			closedAdTimer.cancel();
			closedAdTimer.purge();
			closedAdTimer = null;
		}
		if (startDelayTimer != null) {
			startDelayTimer.cancel();
			startDelayTimer.purge();
			startDelayTimer = null;
		}

		mContext = null;
		if(bannerLayout!=null)
			bannerLayout.removeAllViews();
		bannerLayout = null;
		if (adView != null){
			adView.destroy();
			adView=null;
		}
	}
	
	public void softDestroyAds(){
		if (hideAdTimer != null) {
			hideAdTimer.cancel();
			hideAdTimer.purge();
			hideAdTimer = null;
		}
		if (closedAdTimer != null) {
			closedAdTimer.cancel();
			closedAdTimer.purge();
			closedAdTimer = null;
		}
		if (startDelayTimer != null) {
			startDelayTimer.cancel();
			startDelayTimer.purge();
			startDelayTimer = null;
		}

		if(bannerLayout!=null)
			bannerLayout.removeAllViews();
	}

	public static void configureTimes(int adsLevel) {

	switch (adsLevel) {
		case 0:
			smallAdsOn = false;
			break;
		case 1:
			smallAdsOn = true;
			SMALL_TIME_BETWEEN_X = 5*60*1000; // 5 min.
			SMALL_TIME_BETWEEN = 3*60*1000; // 3 min.
			SMALL_TIME_AD_SHOWED = 10*1000;; // 10 seg.
			SMALL_TIME_STARTDELAY = 30*1000; // 30 seg
			break;
		case 2:
			smallAdsOn = true;
			SMALL_TIME_BETWEEN_X = 4*60*1000; // 5 min.
			SMALL_TIME_BETWEEN = 2*60*1000; // 4 min.
			SMALL_TIME_AD_SHOWED = 30*1000;; // 10 seg.
			SMALL_TIME_STARTDELAY = 15*1000; // 30 seg
			break;
		case 3:
			smallAdsOn = true;
			SMALL_TIME_BETWEEN_X = 3*60*1000; // 5 min.
			SMALL_TIME_BETWEEN = 1*60*1000; // 4 min.
			SMALL_TIME_AD_SHOWED = 30*1000;; // 10 seg.
			SMALL_TIME_STARTDELAY = 20*1000; // 30 seg
			break;
		case 4:
			smallAdsOn = true;
			SMALL_TIME_BETWEEN_X = 2*60*1000; // 5 min.
			SMALL_TIME_BETWEEN = 1*60*1000; // 4 min.
			SMALL_TIME_AD_SHOWED = 30*1000;; // 10 seg.
			SMALL_TIME_STARTDELAY = 15*1000; // 30 seg
			break;
		case 5:
			smallAdsOn = true;
			SMALL_TIME_BETWEEN_X = 1*60*1000; // 5 min.
			SMALL_TIME_BETWEEN = 30*1000; // 4 min.
			SMALL_TIME_AD_SHOWED = 40*1000;; // 10 seg.
			SMALL_TIME_STARTDELAY = 7*1000; // 30 seg
			break;
		default:
			smallAdsOn = true;
			SMALL_TIME_BETWEEN_X = 1*60*1000; // 5 min.
			SMALL_TIME_BETWEEN = 30*1000; // 4 min.
			SMALL_TIME_AD_SHOWED = 40*1000;; // 10 seg.
			SMALL_TIME_STARTDELAY = 1000; // 30 seg
		}
	Log.e("AdsManager", "SMALL_TIME_BETWEEN_X: "+SMALL_TIME_BETWEEN_X);
	Log.e("AdsManager", "SMALL_TIME_BETWEEN: "+SMALL_TIME_BETWEEN);
	Log.e("AdsManager", "SMALL_TIME_AD_SHOWED: "+SMALL_TIME_AD_SHOWED);
	Log.e("AdsManager", "SMALL_TIME_STARTDELAY: "+SMALL_TIME_STARTDELAY);
		
	}

	public static void flush() {
		if (instance != null)
			instance.destroyAds();
	}
	
	public static void softFlush(){
		if(instance!=null)
			instance.softDestroyAds();
	}

}
