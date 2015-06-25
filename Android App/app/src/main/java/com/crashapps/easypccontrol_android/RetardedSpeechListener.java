package com.crashapps.easypccontrol_android;

import java.util.ArrayList;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

public class RetardedSpeechListener implements RecognitionListener {
public RetardedSpeechListenerObserver theObserver;
	
	public void onReadyForSpeech(Bundle params)
    {

    }
	
	public void registerObserver(RetardedSpeechListenerObserver o){
		theObserver=o;
	}
	
	public void unregisterObserver(){
		theObserver=null;
	}
	
    public void onBeginningOfSpeech()
    {

    }
    public void onRmsChanged(float rmsdB)
    {

    }
    public void onBufferReceived(byte[] buffer)
    {

    }
    public void onEndOfSpeech()
    {

    }
    public void onError(int errorCode)
    {
    	if ((errorCode == SpeechRecognizer.ERROR_NO_MATCH)
                || (errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT))
        {
    		if(theObserver!=null)
    			theObserver.restartRecognition();
        }
        else
        {
        	if(theObserver!=null)
        		theObserver.RecognitionError();
        }
    }
    public void onResults(Bundle results)                   
    {
             String str = new String();
             ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
             for (int i = 0; i < data.size(); i++)
             {
                       str += data.get(i);
                       break;
             }
             if(theObserver!=null)
            	 theObserver.onResults(str);
    }
    public void onPartialResults(Bundle partialResults)
    {
    }
    public void onEvent(int eventType, Bundle params)
    {
    }
    
    public interface RetardedSpeechListenerObserver{
    	public void onResults(String result);
    	public void restartRecognition();
    	public void RecognitionError();
    }

}
