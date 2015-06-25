package com.crashapps.easypccontrol_android;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MousePadView extends View{

	private MousePadEventListener theListener;
	private Handler theHandler;
	private Runnable clickRunnable;
	
	public MousePadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		theHandler=new Handler();
		clickRunnable=new Runnable(){

			@Override
			public void run() {
				theListener.mouseClick();
			}};
	}

	public void setListener(MousePadEventListener l){
		theListener=l;
		
	}
	
	int lastX,lastY;
	long timeDown=0;
	
	boolean mouseDown=false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if(event.getAction()==MotionEvent.ACTION_DOWN){
			this.setBackgroundResource(R.drawable.mouse_down);
			lastX=(int) event.getX();
			lastY=(int) event.getY();
			if(System.currentTimeMillis()-timeDown<200){
				theHandler.removeCallbacks(clickRunnable);
				theListener.mouseDown();
				mouseDown=true;
			}
			timeDown=System.currentTimeMillis();
		}
		else if(event.getAction()==MotionEvent.ACTION_MOVE){
			theListener.mouseMove((int)event.getX()-lastX, (int)event.getY()-lastY,event.getPointerCount()==1);
			lastX=(int) event.getX();
			lastY=(int) event.getY();
		}
		else if(event.getAction()==MotionEvent.ACTION_UP){
			this.setBackgroundResource(R.drawable.mouse);
			//el toque fue cortito?
			
			if(System.currentTimeMillis()-timeDown<200){
				//se movió mucho?
				if(Math.abs(event.getX()-lastX) < 10 && Math.abs(event.getY()-lastY) < 10){
					theHandler.postDelayed(clickRunnable,150);
				}
			}
			if(mouseDown){
				mouseDown=false;
				theListener.mouseUp();
			}
		}

		return true;
	}
	
	
	public interface MousePadEventListener{
		public void mouseMove(int deltaX, int deltaY,boolean oneFinger);
		public void mouseClick();
		public void mouseDown();
		public void mouseUp();
		
	}

}
