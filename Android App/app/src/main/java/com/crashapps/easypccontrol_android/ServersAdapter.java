package com.crashapps.easypccontrol_android;

import java.net.InetAddress;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServersAdapter extends BaseAdapter{

	private List<InetAddress> addresses;
	private Activity mContext;
	
	public ServersAdapter(Activity context){
		mContext=context;
	}
	
	@Override
	public int getCount() {
		if(addresses==null)
			return 0;
		return addresses.size();
	}

	@Override
	public Object getItem(int position) {
		if(addresses==null)
			return null;
		return addresses.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null)
			convertView=mContext.getLayoutInflater().inflate(R.layout.server_item, null);
		
		TextView ip=(TextView)convertView.findViewById(R.id.ip);
		InetAddress theAddress=addresses.get(position);
		ip.setText(theAddress.getHostAddress());
		return convertView;
	}
	
	public void updateData(List<InetAddress> a){
		addresses=a;
		this.notifyDataSetChanged();
	}

}
