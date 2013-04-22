package com.example.smartalarm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;

public class LocalDataStorage {

	public final String DATA_TYPE_ACCEL = "accel";
	public final String DATA_TYPE_AUDIO = "audio";
	
	private Context context;
	public LocalDataStorage(Context context)
	{
		this.context = context;
	}
	
	public void saveAccelFile(ArrayList<Long>  data)
	{
		GregorianCalendar now = new GregorianCalendar();
		
		//this filename is foolproof!
		String filename = DATA_TYPE_ACCEL + "_" + now.get(Calendar.YEAR) + "_" + now.get(Calendar.MONTH) 
				+ "_" + now.get(Calendar.DAY_OF_MONTH);
		FileOutputStream fos;
		OutputStream out = null;
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			out = new BufferedOutputStream(fos);
			
			for(int i = 0; i < data.size(); i++)
			{
				long point = data.get(i);
				String bin = Long.toBinaryString(point);
				String hex = Long.toHexString(point);
				byte[] buffer = new byte[8];
				int l = hex.length();
				
				//go from end of hex string converting bytes, in case msB of long is 0, and first char of string
				// is not actually the first byte of the array
				for(int b = 0; b < l; b ++)
				{
					int charNum = l - 1 - b;
					buffer[7-b] = Byte.parseByte(hex.substring(charNum, charNum + 1), 16);
				}
				
				out.write(buffer, 0, 8);
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
	}
	
	public ArrayList<Long> readAccelFile(int year, int month, int day)
	{
		ArrayList<Long> outData = new ArrayList<Long>();
		String filename = DATA_TYPE_ACCEL + "_" + year + "_" + month + "_" + day;
		FileInputStream fis;
		BufferedInputStream in = null;
		try{
			fis = context.openFileInput(filename);
			in = new BufferedInputStream(fis);
			boolean done = false;
			do{
				int[] buffer = new int[8];
				for(int b = 0; b < 8; b++)
				{
					int myByte = in.read();
					if(myByte == -1)
					{
						done = true;
						break;
					}
					buffer[b] = myByte;
				}
				long val = 0;
				for(int b = 0; b < 8; b++)
				{
					val += buffer[b] * Math.pow(256, 8-b-1);
				}
				outData.add(val);
				
			}while(!done);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return outData;
	}
	
}
