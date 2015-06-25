package com.crashapps.easypccontrol_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;

public class PurchaseActivity extends Activity {

	IabHelper mHelper;
	String base64EncodedPublicKey;
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

	public static final String NO_ADS = "easypccontrol_no_ads";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase);

		createKey();
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		startHelper();

		//semana gratis para redditors
		if (System.currentTimeMillis() > 1372422759557L) {
			findViewById(R.id.b_redditor).setVisibility(View.GONE);
		}

	}

	private void createKey() {
		String fpartofkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsQS7RcWoqNEh3zXYxdzaQgjSOdb7rFbkVnDn9GN7AuPzCKFjh+fZAW3XeaeBNqyMQ6HVw6OurMhyV7";

		// pa despistar :P
		String dummyString = "MAFEAIFadasdkljfgewqEWQGH543QEFasdfJuWAWEFTGT012389075h4fhn1107849t1hccg79196134by8cc13489gf0q7fofgb7842";

		String scndpartofkey = "xPtNN1gNdqSgMitiVIww8Ao7nCoMqrasv7uazmFUSPdm1+QkDasvVcURLT5RY+pPAW5zXtJU3NlFXzJuAMRHmVuG7yHAUYS8Qap0eZBQKe0/7JaIBLXfRtBdMosRjyner3T3ZLxD1dMfwpn3HtnWIYza3ekzNXjLk8R80fnccovBADRwofilRiejMYCcsd/jxUeB4IRLICpQ7+aR2HcrEYpepF/Hlgpbp22iHhllRmsaM+gtl3euH2QftGtE/YxnJq7rMP0wIDAQAB";
		base64EncodedPublicKey = fpartofkey + scndpartofkey;
	}

	private void startHelper() {
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Log.d("Billing: ", "Problem setting up In-app Billing: "
							+ result);

					com.crashapps.easypccontrol_android.utils.RetardedUtils
							.showAlert(
									PurchaseActivity.this,
									"Error",
									"Unable to connect to the billing service. Please check your internet connection and try again!");
					finish();
				}
				queryInventory();
			}
		});

	}

	private void queryInventory() {
		IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
			public void onQueryInventoryFinished(IabResult result,
					Inventory inventory) {
				if (result.isFailure()) {
					com.crashapps.easypccontrol_android.utils.RetardedUtils
							.showAlert(
									PurchaseActivity.this,
									"Error",
									"Unable to connect to the billing service. Please check your internet connection and try again!");
					finish();
					return;
				}

				findViewById(R.id.loading).setVisibility(View.GONE);
				if (inventory.hasPurchase(NO_ADS)) {
					findViewById(R.id.purchase).setVisibility(View.GONE);
					findViewById(R.id.purchased).setVisibility(View.VISIBLE);
					com.crashapps.easypccontrol_android.utils.RetardedUtils
							.setPreference(NO_ADS, true, PurchaseActivity.this);
				} else {
					findViewById(R.id.purchased).setVisibility(View.GONE);
					findViewById(R.id.purchase).setVisibility(View.VISIBLE);
					if (com.crashapps.easypccontrol_android.utils.RetardedUtils
							.getPreference(Utils.REDDITOR, false,
									PurchaseActivity.this)) {
						findViewById(R.id.b_redditor).setEnabled(false);
					}
					com.crashapps.easypccontrol_android.utils.RetardedUtils
							.setPreference(NO_ADS, false, PurchaseActivity.this);

				}
			}
		};
		mHelper.queryInventoryAsync(mQueryFinishedListener);
	}

	public void purchase(View v) {
		if (mHelper == null)
			mHelper = new IabHelper(this, base64EncodedPublicKey);
		initPurchaseListener();
		mHelper.launchPurchaseFlow(this, NO_ADS, 10001,
				mPurchaseFinishedListener,
				"bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");

	}

	public void close(View v) {
		finish();
	}

	private void initPurchaseListener() {
		mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
			public void onIabPurchaseFinished(IabResult result,
					Purchase purchase) {
				if (result.isFailure()) {
					com.crashapps.easypccontrol_android.utils.RetardedUtils.showAlert(
							PurchaseActivity.this, "Billing Alert",
							"The payment was canceled!");
					return;
				} else if (purchase.getSku().equals(NO_ADS)) {
					com.crashapps.easypccontrol_android.utils.RetardedUtils
							.showAlert(PurchaseActivity.this,
									"Congratulations!",
									"Your donation was received. Thank you very much and enjoy!");
					findViewById(R.id.purchase).setVisibility(View.GONE);
					findViewById(R.id.purchased).setVisibility(View.VISIBLE);
					com.crashapps.easypccontrol_android.utils.RetardedUtils
							.setPreference(NO_ADS, true, PurchaseActivity.this);
				}
			}
		};

	}

	public void redditor(View v) {
		com.crashapps.easypccontrol_android.utils.RetardedUtils
				.showAlert(
						PurchaseActivity.this,
						"Congratulations!",
						"As a redditor, you have now the full version of Easy Pc Control with no ads\n\nEnjoy and don't forget to rate if you like this app!");
		com.crashapps.easypccontrol_android.utils.RetardedUtils.setPreference(
				Utils.REDDITOR, true, this);
		findViewById(R.id.b_redditor).setEnabled(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("ActivityResult:", "onActivityResult(" + requestCode + ","
				+ resultCode + "," + data);

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d("ActivityResult:", "onActivityResult handled by IABUtil.");
		}
	}

}
