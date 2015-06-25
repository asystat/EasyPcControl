package com.crashapps.easypccontrol_android;

import java.net.InetAddress;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class EasyPcControlClient {
	private static EasyPcControlClient singleton=null;
	
	private ClientListener theListener;
	private Client client;
	private int intentos=0;
	InetAddress lastAddress=null;
	private EasyPcControlClient(){
		client=new Client();
		client.start();
		client.addListener(new Listener(){

			@Override
			public void disconnected(Connection connection) {
				super.disconnected(connection);
				processDisconnected();
			}
			
			@Override
			public void connected(Connection connection) {
				intentos=0;
			}

		});
		Kryo kryo = client.getKryo();
		kryo.register(NetworkMessage.class);
	}
	
	private void processDisconnected(){
		if(intentos<3 && lastAddress!=null){
			intentos++;
			connect(lastAddress);
		}
		else if(theListener!=null)
			theListener.disconnected();
	}
	
	public static EasyPcControlClient getInstance(){
		if(singleton==null)
			singleton=new EasyPcControlClient();
		return singleton;
	}
	
	//llamar asíncronamente
	public InetAddress discoverServer(){
		InetAddress address = client.discoverHost(54777, 7000);
		return address;
	}
	
	public List<InetAddress> discoverServers(){
		return client.discoverHosts(54777, 3000);
	}

	public boolean connect(InetAddress address){
		lastAddress=address;
		try {
			client.connect(5000, address, 54555, 54777);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			processDisconnected();
		}
		return false;
	}
	
	public boolean sendRequest(int type){
		if(!client.isConnected())
			return false;
		NetworkMessage request = new NetworkMessage();
		request.text = "Button request - "+type;
		request.type = type;
		client.sendTCP(request);
		return true;
	}

	public void sendMouseMoveRequest(int deltaX, int deltaY) {
		NetworkMessage request = new NetworkMessage();
		request.text = "Move the mouse!";
		request.type = NetworkMessage.TYPE_MOUSE_MOVE;
		request.dx=deltaX;
		request.dy=deltaY;
		client.sendUDP(request);		
	}
	
	public void sendMouseScrollRequest(int deltaY) {
		NetworkMessage request = new NetworkMessage();
		request.text = "Scroll!";
		request.type = NetworkMessage.TYPE_MOUSE_SCROLL;
		request.dy=deltaY;
		client.sendUDP(request);
	}

	public void sendMouseClickRequest() {
		NetworkMessage request = new NetworkMessage();
		request.text = "Here is the request!";
		request.type = NetworkMessage.TYPE_MOUSE_CLICK;
		client.sendTCP(request);		
	}

	public synchronized void sendCharRequest(char c) {
		NetworkMessage request = new NetworkMessage();
		request.text = "Char request!";
		request.type = NetworkMessage.TYPE_CHAR;
		request.character=c;
		client.sendTCP(request);
	}
	




	public void setListener(MainActivity mainActivity) {
		theListener=mainActivity;
	}



	
	public interface ClientListener{
		public void disconnected();
	}


	public void sendVolumeRequest(int value) {
		if(!client.isConnected())
			return;
		NetworkMessage request = new NetworkMessage();
		request.dx=value;
		request.type = NetworkMessage.VOLUME;
		client.sendUDP(request);
	}

	public void removeListener() {
		theListener=null;
	}
	
	
}
