package com.crashapps.easypccontrol_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.crashapps.easypccontrol_android.EasyPcControlClient.ClientListener;
import com.crashapps.easypccontrol_android.MousePadView.MousePadEventListener;
import com.crashapps.easypccontrol_android.RetardedSpeechListener.RetardedSpeechListenerObserver;
import com.crashapps.easypccontrol_android.utils.AdsManager;
import com.crashapps.easypccontrol_android.utils.RetardedUtils;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;

import java.net.InetAddress;
import java.net.UnknownHostException;

import it.sephiroth.android.wheel.view.Wheel;
import it.sephiroth.android.wheel.view.Wheel.OnScrollListener;

public class MainActivity extends SherlockActivity implements
		MousePadEventListener, OnClickListener, OnTouchListener, OnKeyListener,
		ClientListener, OnMenuItemClickListener, RetardedSpeechListenerObserver {

	Button space, mouseleft, mouseright;
	String theIp;
	ImageView esc, keyboard;
	TextView hiddenEditText;
	ImageView toggleVoice, alt, tab;
	SlidingMenu menu;
	Wheel mWheel;
	private SpeechRecognizer sr;
	boolean voiceActivated = false;
	com.crashapps.easypccontrol_android.RetardedSpeechListener rsl;
	float sensitivity=1f;
	int wheelOrientation=1;

	
	/*
	 * TODO
	 * Scrollbar (LISTO)
	 * Control de volumen mas intuitivo
	 * shutdown/suspend confirmacion
	 * sensivilidad del mouse (la variable ya existe)
	 * orientaci�n del scroll (la variable ya existe)
	 * opci�n de flechas para presentaciones (tal vez que se vaya el trackpad y aparezcan botones)
	 * shedule shutdown/screen off/suspend
	 * cambiar titulo de PurchaseActivity
	 * 
	 * */
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		voiceActivated = false;

		menu = new SlidingMenu(this);
		menu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
				stopSpeechRecognition();
			}
		});
		menu.setOnOpenedListener(new OnOpenedListener() {

			@Override
			public void onOpened() {
				if (RetardedUtils.getPreference("winMessage", true, MainActivity.this)) {
					RetardedUtils.setPreference("winMessage", false,
									MainActivity.this);
					RetardedUtils.showAlert(
									MainActivity.this,
									"Windows only!",
									"Most of this features work only if the computer you are controlling is using Windows.\nStay tuned for updates and more Mac/Linux compatibility!");
				}
			}
		});
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		menu.setFadeDegree(0.35f);
		menu.setBehindOffset(com.crashapps.easypccontrol_android.Utils.dpToPixels(this, 60));
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.extras_layout);

		mWheel = (Wheel) menu.findViewById(R.id.volume_wheel);
		mWheel.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStarted(Wheel view, float value, int roundValue) {
			}

			@Override
			public void onScrollFinished(Wheel view, float value, int roundValue) {
			}

			// int lastValue=;
			@Override
			public void onScroll(Wheel view, float value, int roundValue) {
				float realValue = (value + 1f) / 2;
				int volValue = (int) (realValue * 65530);
				sendVolumeRequest(volValue);
			}
		});

		menu.findViewById(R.id.mute).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendRequest(com.crashapps.easypccontrol_android.NetworkMessage.MUTE_TOGGLE);
			}
		});

		menu.findViewById(R.id.shutdown).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						shutDown();

					}
				});

		menu.findViewById(R.id.screen_off).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						sendRequest(com.crashapps.easypccontrol_android.NetworkMessage.MONITOR_OFF);
					}
				});

		menu.findViewById(R.id.screen_on).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						sendRequest(com.crashapps.easypccontrol_android.NetworkMessage.TYPE_MOUSE_CLICK);
					}
				});

		menu.findViewById(R.id.suspend).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						sendRequest(com.crashapps.easypccontrol_android.NetworkMessage.STANDBY);
					}
				});

		menu.findViewById(R.id.alt).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					sendRequest(com.crashapps.easypccontrol_android.NetworkMessage.ALT_DOWN);
				}
				else if(event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_OUTSIDE){
					sendRequest(com.crashapps.easypccontrol_android.NetworkMessage.ALT_UP);
				}
				
				return true;
			}
		});

		menu.findViewById(R.id.tab).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN){
					sendRequest(com.crashapps.easypccontrol_android.NetworkMessage.TAB_DOWN);
				}
				else if(event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_OUTSIDE){
					sendRequest(com.crashapps.easypccontrol_android.NetworkMessage.TAB_UP);
				}
				
				return true;
			}
		});

		toggleVoice = (ImageView) menu.findViewById(R.id.toggle_voice);
		toggleVoice.setOnClickListener(this);

		space = (Button) findViewById(R.id.space);
		space.setOnClickListener(this);

		mouseleft = (Button) findViewById(R.id.mouse_left);
		mouseleft.setOnTouchListener(this);

		mouseright = (Button) findViewById(R.id.mouse_right);
		mouseright.setOnTouchListener(this);

		esc = (ImageView) findViewById(R.id.esc);
		esc.setOnClickListener(this);

		hiddenEditText = (EditText) findViewById(R.id.hiddenEditText);
		hiddenEditText.addTextChangedListener(new MyTextWatcher());

		keyboard = (ImageView) findViewById(R.id.keyboard);
		keyboard.setOnClickListener(this);

		com.crashapps.easypccontrol_android.MousePadView mpv = (com.crashapps.easypccontrol_android.MousePadView) findViewById(R.id.mousepad);

		if (savedInstanceState != null && savedInstanceState.containsKey("ip"))
			theIp = savedInstanceState.getString("ip");
		else
			theIp = getIntent().getExtras().getString("ip");
		mpv.setListener(this);
		rsl = new com.crashapps.easypccontrol_android.RetardedSpeechListener();

		
		//Utils.AppFuelHelper(this,PromoSize.BANNER, "51c0b3d4e4b0e98169c5544f", R.id.mainLayout);
		
	}

	private synchronized void sendRequest(final int type) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendRequest(type);
			}
		});
		t.start();
	}

	private void shutDown() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Shutdown PC")
				.setMessage("Are you sure?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								sendRequest(com.crashapps.easypccontrol_android.NetworkMessage.SHUTDOWN);
							}

						}).setNegativeButton("No", null).show();
	}

	private synchronized void sendVolumeRequest(final int value) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendVolumeRequest(value);
			}
		});
		t.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (theDialog != null && theDialog.isShowing())
			theDialog.dismiss();
		connectThread();
		rsl.registerObserver(this);
		if(com.crashapps.easypccontrol_android.Utils.hasAds(this)){
			AdsManager.flush();
			AdsManager.configureTimes(2);
			AdsManager.getInstance(this).insertBannerDelayed((FrameLayout)findViewById(R.id.adframe));
		}
		sensitivity=RetardedUtils.getPreference("mouse_sensitivity", 1f, this);
		wheelOrientation=RetardedUtils.getPreference("wheel_orientation", 1, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().removeListener();
		rsl.unregisterObserver();
		if(com.crashapps.easypccontrol_android.Utils.hasAds(this)){
			AdsManager.softFlush();
		}
	}

	public void toggleMenu(View v) {
		menu.toggle(true);
	}

	private void connectThread() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				InetAddress[] address = null;
				try {
					address = InetAddress.getAllByName(theIp);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (address == null)
					finish();
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().connect(address[0]);
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance()
						.setListener(MainActivity.this);
			}
		});
		t.start();
	}

	@Override
	public void mouseMove(int deltaX, int deltaY, boolean oneFinger) {
		if(oneFinger)
			startMouseMoveThread((int)(deltaX*sensitivity), (int)(deltaY*sensitivity));
		else
			startMouseScrollThread(deltaY*wheelOrientation);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("ip", theIp);
	}

	private synchronized void startMouseMoveThread(final int deltaX,
			final int deltaY) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendMouseMoveRequest(deltaX,
						deltaY);
			}
		});
		t.start();
	}
	
	private synchronized void startMouseScrollThread(final int deltaY) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendMouseScrollRequest(deltaY);
			}
		});
		t.start();
	}

	@Override
	public void mouseClick() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendMouseClickRequest();
			}
		});
		t.start();
	}

	@Override
	public void mouseDown() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendRequest(
						com.crashapps.easypccontrol_android.NetworkMessage.TYPE_MOUSE_LEFT_DOWN);
			}
		});
		t.start();

	}

	@Override
	public void mouseUp() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendRequest(
						com.crashapps.easypccontrol_android.NetworkMessage.TYPE_MOUSE_LEFT_UP);
			}
		});
		t.start();

	}

	@Override
	public void onClick(View v) {
		if (v == space) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendRequest(
							com.crashapps.easypccontrol_android.NetworkMessage.TYPE_SPACE);
				}
			});
			t.start();
		} else if (v == esc) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendRequest(
							com.crashapps.easypccontrol_android.NetworkMessage.TYPE_ESC);
				}
			});
			t.start();
		} else if (v == keyboard) {
			hiddenEditText.setOnKeyListener(this);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(hiddenEditText, InputMethodManager.SHOW_IMPLICIT);
		} else if (v == toggleVoice) {
			if (voiceActivated) {
				stopSpeechRecognition();

			} else {
				startSpeechRecognition();
			}
		}
	}

	private void stopSpeechRecognition() {
		voiceActivated = false;
		toggleVoice.setImageResource(R.drawable.mic_icon_off);
		if (sr != null)
			sr.stopListening();
	}

	private void startSpeechRecognition() {
		voiceActivated = true;
		sr = SpeechRecognizer.createSpeechRecognizer(this);

		sr.setRecognitionListener(rsl);
		toggleVoice.setImageResource(R.drawable.mic_icon);
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				"com.crashapps.easypccontrol_android");

		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		sr.startListening(intent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int type = 0;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			v.setBackgroundResource(R.drawable.mousebutton_down);
			if (v == mouseleft)
				type = com.crashapps.easypccontrol_android.NetworkMessage.TYPE_MOUSE_LEFT_DOWN;
			else if (v == mouseright)
				type = com.crashapps.easypccontrol_android.NetworkMessage.TYPE_MOUSE_RIGHT_DOWN;
			else
				return true;

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			v.setBackgroundResource(R.drawable.mousebutton);
			if (v == mouseleft)
				type = com.crashapps.easypccontrol_android.NetworkMessage.TYPE_MOUSE_LEFT_UP;
			else if (v == mouseright)
				type = com.crashapps.easypccontrol_android.NetworkMessage.TYPE_MOUSE_RIGHT_UP;
			else
				return true;
		}

		final int sendType = type;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendRequest(sendType);
			}
		});
		t.start();

		return true;
	}

	boolean send = false;

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (send) {
			if (keyCode == 67)
				sendCharThread(Character.toChars(8)[0]);
			else if (keyCode == 68)
				sendCharThread(Character.toChars(13)[0]);
			else if (keyCode == 66)
				sendCharThread('\n');
			else if (keyCode == 4)
				finish();

		}
		send = !send;
		return true;
	}

	private void sendCharThread(final char c) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendCharRequest(c);
			}
		});
		t.start();
	}

	private void sendStringThread(final String s) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < s.length(); i++) {
					com.crashapps.easypccontrol_android.EasyPcControlClient.getInstance().sendCharRequest(
							s.charAt(i));
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
	}

	private class MyTextWatcher implements TextWatcher {
		CharSequence lastText = "";

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			processString(s);

		}

		private synchronized void processString(Editable s) {
			String newText = s.toString();

			if (newText.length() <= lastText.length()) {
				sendCharThread(Character.toChars(8)[0]);
			} else {
				CharSequence newChars = newText.subSequence(lastText.length(),
						newText.length());
				for (int i = 0; i < newChars.length(); i++) {
					sendCharThread(newChars.charAt(i));
				}
			}
			lastText = "" + newText;
		}
	}

	@Override
	public void disconnected() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				showAlertAndFinish(MainActivity.this, "Disconnected",
						"Check your wifi connection and if the server is running on your PC");
			}

		});

	}

	MenuItem purchase;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		purchase = menu.add("Purchase");
		purchase.setIcon(R.drawable.info_icon).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS);
		purchase.setOnMenuItemClickListener(this);
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if (item == purchase) {
			Intent i=new Intent(this, com.crashapps.easypccontrol_android.PurchaseActivity.class);
			startActivity(i);
			//purchase.setVisible(false);
		}
		return true;
	}

	AlertDialog theDialog;

	public static void showAlertAndFinish(final Context c, String title,
			String message) {

		AlertDialog theDialog = new AlertDialog.Builder(c).create();
		theDialog.setTitle(title);
		theDialog.setMessage(message);
		theDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Close",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						((Activity) c).finish();
					}
				});
		theDialog.setCancelable(false);
		theDialog.setIcon(R.drawable.ic_alert);
		theDialog.show();

	}

	@Override
	public void onResults(String result) {
		sendStringThread(result);
		startSpeechRecognition();
	}

	@Override
	public void restartRecognition() {
		// stopSpeechRecognition();
	}

	@Override
	public void RecognitionError() {
		// stopSpeechRecognition();
	}

}
