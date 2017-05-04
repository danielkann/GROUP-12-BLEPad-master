package com.example.testble;

import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class BluetoothLe
{
    private final static String TAG = BluetoothLe.class.getSimpleName();
    
    private Context mContext;

    protected BluetoothManager mBluetoothManager;
    protected BluetoothAdapter mBluetoothAdapter;
    protected String mBluetoothDeviceAddress;
    protected BluetoothGatt mBluetoothGatt;
    
    private BluetoothAdapter.LeScanCallback mScanCallback;

    public final static UUID UUID_CLIENT_CHARACTERISTIC_CONFIG = 
    		UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    
	public BluetoothLe(Context context)
	{
		mContext = context;
	}
	
	public BluetoothGatt getGatt()
	{
		return mBluetoothGatt;
	}
	

	public boolean isBleSupported()
	{
		if(!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
			return false;
		
		return true;
	}
	

	public boolean isOpened()
	{
		if(!mBluetoothAdapter.isEnabled())
			return false;
		
		return true;
	}
	

	public boolean setScanCallBack(BluetoothAdapter.LeScanCallback scanCallback)
	{
		if(scanCallback == null)
			return false;
		
		mScanCallback = scanCallback;
		return true;
	}
	

	public boolean startLeScan()
	{
		if(mScanCallback == null)
			return false;

		return mBluetoothAdapter.startLeScan(mScanCallback);		
	}
	

	public boolean stopLeScan()
	{
		if(mScanCallback == null)
			return false;
		
		mBluetoothAdapter.stopLeScan(mScanCallback);	
		
		return true;
	}
	

	public boolean connectLocalDevice()
	{
        if (mBluetoothManager == null) 
        {
            mBluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) 
            {
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) 
        {
            return false;
        }

        return true;
	}
	

    public void disconnectLocalDevice() 
    {
        if (mBluetoothGatt == null) 
        {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    

    public boolean connectDevice(final String address, BluetoothGattCallback gattCallback) 
    {
        if (mBluetoothAdapter == null || address == null) 
        {
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null)
        {
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(mContext, false, gattCallback);
        if(mBluetoothGatt == null)
        	return false;
        
        mBluetoothDeviceAddress = address;
        return true;
    }
    

    public void disconnectDevice() 
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) 
        {
            return ;
        }
        mBluetoothGatt.disconnect();
        Log.d(TAG, "Bluetooth disconnect");
    }
    

    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) 
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) 
        {
            return false;
        }
        return mBluetoothGatt.readCharacteristic(characteristic);
    }
    

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) 
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) 
        {
            return false;
        }
        //characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }


    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic,boolean enabled) 
    {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) 
        {
            return false;
        }
        if(!mBluetoothGatt.setCharacteristicNotification(characteristic, enabled))
        	return false;

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG);
        if(descriptor == null)
        	return false;
        
        byte[] data;
        if(enabled)
        	data = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
        else 
        	data = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        
        if(!descriptor.setValue(data))
        	return false;
        return mBluetoothGatt.writeDescriptor(descriptor);
    }


    public List<BluetoothGattService> getSupportedGattServices() 
    {
        if (mBluetoothGatt == null) 
        	return null;

        return mBluetoothGatt.getServices();
    }
}
