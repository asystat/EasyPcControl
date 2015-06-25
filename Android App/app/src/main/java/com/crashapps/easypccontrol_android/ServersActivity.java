package com.crashapps.easypccontrol_android;

import java.net.InetAddress;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.crashapps.easypccontrol_android.utils.AppRater;

public class ServersActivity extends SherlockActivity implements OnItemClickListener, OnMenuItemClickListener{

	private ServersAdapter theAdapter;
	private DiscoverTask discoverTask=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.serverlist);
		
		ListView servers=(ListView) findViewById(R.id.servers);
		theAdapter=new ServersAdapter(this);
		servers.setAdapter(theAdapter);
		servers.setOnItemClickListener(this);
		AppRater.app_launched(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startDiscoverThread();
	}

	
	
	@Override
	protected void onPause() {
		if(discoverTask!=null)
			discoverTask.cancel(true);
		super.onPause();
	}

	private void startDiscoverThread() {
		if(discoverTask!=null)
			discoverTask.cancel(true);
		
		discoverTask=new DiscoverTask();
		discoverTask.execute();
	}
	
	public void refresh(View v){
		startDiscoverThread();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		InetAddress theAddress=(InetAddress)theAdapter.getItem(arg2);
		Intent i=new Intent(this, MainActivity.class);
		i.putExtra("ip", theAddress.getHostAddress());
		startActivity(i);
	}
	
	private class DiscoverTask extends AsyncTask<Void, Void, List<InetAddress>>{

		@Override
		protected void onPreExecute() {
			findViewById(R.id.servers).setVisibility(View.INVISIBLE);
			findViewById(R.id.loading).setVisibility(View.VISIBLE);
			findViewById(R.id.nothingfound).setVisibility(View.GONE);
			findViewById(R.id.refresh_layout).setVisibility(View.GONE);
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(List<InetAddress> result) {
			findViewById(R.id.loading).setVisibility(View.GONE);
			findViewById(R.id.refresh_layout).setVisibility(View.VISIBLE);
			if(result==null)
				return;
			theAdapter.updateData(result);
			
			if(result.size()==0){
				findViewById(R.id.nothingfound).setVisibility(View.VISIBLE);
				findViewById(R.id.servers).setVisibility(View.INVISIBLE);
			}
			else
				findViewById(R.id.servers).setVisibility(View.VISIBLE);
			super.onPostExecute(result);
		}
		


		@Override
		protected List<InetAddress> doInBackground(Void... params) {
			List<InetAddress> addresses = EasyPcControlClient.getInstance()
					.discoverServers();
			return addresses;
		}
		
	}
	
	
	MenuItem purchase;
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
 
        purchase=menu.add("Purchase");
        purchase.setIcon(R.drawable.info_icon).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        purchase.setOnMenuItemClickListener(this);
        return true;
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if(item==purchase){
			//purchase.setVisible(false);
			Intent i=new Intent(this, PurchaseActivity.class);
			startActivity(i);
		}
		return true;
	}
	
}
