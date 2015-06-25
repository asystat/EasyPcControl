package com.crashapps.easypccontrol_android;

public class NetworkMessage {

	
	public static final int TYPE_SUCCESS=0;
	public static final int TYPE_ERROR=1;
	public static final int TYPE_ALERT=2;
	
	public static final int TYPE_CHAR=3;
	
	public static final int TYPE_MOUSE_CLICK=4;
	public static final int TYPE_MOUSE_LEFT_DOWN=5;
	public static final int TYPE_MOUSE_LEFT_UP=6;
	public static final int TYPE_MOUSE_RIGHT_DOWN=7;
	public static final int TYPE_MOUSE_RIGHT_UP=8;
	
	public static final int TYPE_MOUSE_MOVE=9;
	
	
	public static final int TYPE_ESC=10;
	public static final int TYPE_SPACE=11;
	public static final int TYPE_MOUSE_SCROLL=12;
	
	
	public static final int VOL_UP = 100;
	public static final int VOL_DOWN = 101;
	public static final int MUTE_TOGGLE = 102;
	public static final int MONITOR_OFF = 103;
	public static final int STANDBY = 104;
	public static final int VOLUME = 105;
	public static final int SHUTDOWN = 106;
	
	public static final int ALT_DOWN = 107;
	public static final int ALT_UP = 108;
	public static final int TAB_DOWN = 109;
	public static final int TAB_UP = 110;
	
	

	
	char character;
	int dx,dy;
	int type;
	String text;

}
