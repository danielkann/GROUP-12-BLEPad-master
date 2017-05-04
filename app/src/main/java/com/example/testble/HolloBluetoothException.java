package com.example.testble;

public class HolloBluetoothException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public static final int ERROR_WAKE_UP_FAILED = 1;
	public static final int ERROR_SEND_FAILED = 2;
	public static final int ERROR_RECEIVE_TIME_OUT = 3;
	public static final int ERROR_RECEIVE_DATA = 4;
	public static final int ERROR_NOT_CONNECT = 5;

	private int mError;
	
	public HolloBluetoothException(int error)
	{
		mError = error;
	}
	

	public String getErrorMsg()
	{
		switch (mError)
		{
		case ERROR_WAKE_UP_FAILED:
			return "";
			
		case ERROR_SEND_FAILED:
			return "";
			
		case ERROR_RECEIVE_TIME_OUT:
			return "";

		case ERROR_RECEIVE_DATA:
			return "";
			
		case ERROR_NOT_CONNECT:
			return "";
					
		default:
			return "";
		}
	}
}
