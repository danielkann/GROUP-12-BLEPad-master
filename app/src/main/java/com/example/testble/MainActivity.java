package com.example.testble;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity
{
	private HolloBluetooth mble;
	private static final int REQUEST_ENABLE_BT = 1;
	private Handler mHandler=new Handler();
	private boolean mScanning=true;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;
    
    private LeDeviceListAdapter mLeDeviceListAdapter;
    
    private String TAG = "MainActivity";
    private Button mScanBt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(R.string.title_devices);

		setContentView(R.layout.activity_main);
		

		mble = HolloBluetooth.getInstance(getApplicationContext());

		if(!mble.isBleSupported() || !mble.connectLocalDevice())
		{
			Toast.makeText(this, "BLE is not supported on the device",Toast.LENGTH_SHORT).show();
			finish();
			return ;
		}
		mScanBt = (Button)findViewById(R.id.scanBt);
		mScanBt.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(mScanning)
				{
					 scanLeDevice(false);
					 mScanBt.setText("Scan");
				}
				else 
				{
					mLeDeviceListAdapter.clear();
		            scanLeDevice(true);
		            mScanBt.setText("Stop Scan");
				}

			}
		});
		
	}
	

	@Override
	protected void onResume()
	{
		super.onResume();

		if(!mble.isOpened())
		{
			Intent openIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(openIntent, REQUEST_ENABLE_BT);
		}

		mble.setScanCallBack(mLeScanCallback);
        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        setListAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);
		mScanBt.setText("Stop Scan");
	}
	

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}


	@Override
	protected void onPause()
	{
		super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        
        final Intent intent = new Intent(this, BluetoothControlActivity.class);
        intent.putExtra(BluetoothControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(BluetoothControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) 
        {
            mble.stopLeScan();
            mScanning = false;
        }
		
        startActivity(intent);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if(resultCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED)
		{
			finish();
			return ;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) 
        {
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } 
        else 
        {
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{


        return true;
	}
    
    
    Runnable cancelScan = new Runnable()
	{		
		@Override
		public void run()
		{
            mble.stopLeScan();
            try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            mble.startLeScan();
            mHandler.postDelayed(cancelScan,SCAN_PERIOD);
            invalidateOptionsMenu();
		}
	};


	private void scanLeDevice(final boolean enable) 
    {
        if (enable) 
        {
            // SCAN_PERIOD
            mHandler.postDelayed(cancelScan,SCAN_PERIOD);

            mScanning = true;
            mble.startLeScan();
        } 
        else 
        {

        	mHandler.removeCallbacks(cancelScan);
        	mScanning = false;
        	mble.stopLeScan();
        }
        invalidateOptionsMenu();
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() 
    {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) 
        {
            runOnUiThread(new Runnable() 
            {
                @Override
                public void run() 
                {                	
                	String deviceName = device.getName();
                	
                	if(deviceName == null || deviceName.length() <= 0)
                		deviceName = "unknow device";
                //	Log.d(TAG, deviceName);
                //	Log.d(TAG, device.getAddress());
//                    Log.d(TAG,":"+ConvertData.bytesToHexString(scanRecord, false));


                	//02010603035869
                	if(scanRecord.length < 7 || scanRecord[0] != 0x02 || scanRecord[1] != 0x01 ||
                			scanRecord[2] != 0x06 || scanRecord[3] != 0x03 || scanRecord[4] != 0x03 ||
                            scanRecord[5] != 0x58 || scanRecord[6] != 0x69) {


                        if (scanRecord.length < 9 || scanRecord[0] != 0x02 || scanRecord[1] != 0x01 ||
                                scanRecord[2] != 0x06 || scanRecord[3] != 0x05 || scanRecord[4] != 0x03 ||
                                scanRecord[5] != 0x58 || scanRecord[6] != 0x69 || scanRecord[7] != (byte)0xE7 ||
                                scanRecord[8] != (byte)0xFE) {

                            if (scanRecord.length < 2 || scanRecord[0] != 0x1A || scanRecord[1] != 0xFF)
                                return;
                        }
                    }

                	
                	byte[] temp =scanRecord;
                	Log.d(TAG, ConvertData.bytesToHexString(temp, false));
                    mLeDeviceListAdapter.addDevice(device,Integer.valueOf(rssi),ConvertData.bytesToHexString(temp, false));
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    
    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter 
    {
        private ArrayList<BluetoothDevice> mLeDevices;
        private ArrayList<Integer> mLeRssi;
        private ArrayList<String> mLeRecord;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter(Context context) 
        {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mLeRssi = new ArrayList<Integer>();
            mLeRecord = new ArrayList<String>();
            mInflator = LayoutInflater.from(context);
        }

        public void addDevice(BluetoothDevice device, Integer rssi, String record) 
        {
            if(!mLeDevices.contains(device)) 
            {
                mLeDevices.add(device);
                mLeRssi.add(rssi);
                mLeRecord.add(record);
            }
        }

        public BluetoothDevice getDevice(int position) 
        {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            mLeRecord.clear();
            mLeRssi.clear();
        }

        @Override
        public int getCount() 
        {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) 
        {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) 
        {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) 
        {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) 
            {
//
            	view = mInflator.inflate(R.layout.list, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceRecord = (TextView)view.findViewById(R.id.device_record);
                view.setTag(viewHolder);
            } 
            else 
            {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            Integer rssi = mLeRssi.get(i);
            String record = mLeRecord.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            
            viewHolder.deviceAddress.setText("address:"+device.getAddress() + "     RSSI:"+rssi+"dB");
            viewHolder.deviceRecord.setText("broadcast:"+record);

            return view;
        }
    }
    
    static class ViewHolder
    {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRecord;
    }
}
