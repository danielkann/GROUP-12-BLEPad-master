package com.example.testble;


public class ConvertData
{

	public static String bytesToHexString(byte[] bytes, boolean bSpace)
	{
		final StringBuilder sb = new StringBuilder(bytes.length);

		for(byte byteChar : bytes) {
			sb.append(String.format("%02X", byteChar));
		}
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < sb.toString().length(); i+=2) {
			String str = sb.toString().substring(i, i+2);
			output.append((char)Integer.parseInt(str, 16));
		}

		return output.toString();
	}
	

	public static byte[] hexStringToBytes(String hexString)
	{
		if(hexString == null)
			return null;
		
		hexString = hexString.replace(" ", "");
		hexString = hexString.toUpperCase();
		
		int len = (hexString.length() / 2);
		if(len <= 0)
			return null;
		
		byte[] result = new byte[len];
		char[] achar = hexString.toCharArray();
		for (int i = 0; i < len; i++) 
		{
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		
		return result;
	}
	

	public static boolean cpyBytes(byte[] dst, int dstOffset, byte[] src, int srcOffset, int length)
	{
		if(dst == null || src == null || 
				dstOffset > dst.length || srcOffset > src.length ||
				length > (dst.length-dstOffset) || length > (src.length-srcOffset))
		{
			return false;
		}
		
		for (int i = 0; i < length; i++)
		{
			dst[i+dstOffset] = src[i+srcOffset];
		}
		
		return true;
	}
	

	public static boolean cmpBytes(byte[] data1, byte[] data2)
	{
		if (data1 == null && data2 == null)
		{
			return true;
		}
		if (data1 == null || data2 == null) 
		{
			return false;
		}
		if (data1 == data2)
		{
			return true;
		}
		if(data1.length != data2.length)
		{
			return false;
		}
		
		int len = data1.length;
		for (int i = 0; i < len; i++)
		{
			if(data1[i] != data2[i])
				return false;
		}
		
		return true;
	}
	
	private static int toByte(char c) 
	{
	    byte b = (byte) "0123456789ABCDEF".indexOf(c);
	    return b;
	 }
}
