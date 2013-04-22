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
import android.util.Log;

public class LocalDataStorage {

	public final String DATA_TYPE_ACCEL = "accel";
	public final String DATA_TYPE_AUDIO = "audio";
	private String delim = "\n";
	private Context context;
	public LocalDataStorage(Context context)
	{
		this.context = context;
	}
	
	public void saveAccelFile(ArrayList<Long>  data)
	{
		GregorianCalendar now = new GregorianCalendar();
		
		//this filename is foolproof!
		String filename = DATA_TYPE_ACCEL + "_" + now.get(Calendar.YEAR) + "_" + (now.get(Calendar.MONTH) +1) //zero-based wtf
				+ "_" + now.get(Calendar.DAY_OF_MONTH);
		Log.d("generated Filename:", filename);
		FileOutputStream fos;
		OutputStream out = null;
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			out = new BufferedOutputStream(fos);
			
			for(int i = 0; i < data.size(); i++)
			{
				String line = Long.toString(data.get(i)) +delim;
				Log.d("write data:", line);
				out.write(line.getBytes());
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
			ArrayList<Byte> bytes = new ArrayList<Byte>();
			do{
				int myByte = in.read();
				if(myByte == -1)
				{
					done = true;
				}
				else
				{
					bytes.add((byte)myByte);
					byte[] bts = new byte[bytes.size()];
					for(int f = 0; f < bytes.size(); f++)
					{
						bts[f] = bytes.get(f);
					}
					String number = new String(bts);
					if(number.endsWith(delim)){
						outData.add(Long.parseLong(number.replace(delim, "")));
						bytes.clear();
					}
					
				}
				
				
			}while(!done);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				if(in != null)
					in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return outData;
	}
	
}
