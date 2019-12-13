package com.example.myapplication.utils;

import android.util.Log;

import com.example.myapplication.MainActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Logger {
	
	private BufferedWriter bw;
	private int counter=0;
	
	public Logger(String fname){
		try {
			this.bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fname))));
		} catch (FileNotFoundException e) {
			Log.e(MainActivity.LOG_TAG, "Error creating file", e);
			System.exit(1);
		}
	}

	
	public void write(String s){
		try {
			bw.write(Long.toString(System.currentTimeMillis())+","+s+"\n");
			counter++;
			if (counter>10){
				bw.flush();
				counter=0;
			}
		} catch (IOException e) {
			Log.e(MainActivity.LOG_TAG, "Error adding line", e);
			System.exit(1);
		}
	}
	
	public void flush(){
		try {
			bw.flush();
		} catch (IOException e) {
			Log.e(MainActivity.LOG_TAG, "Error writting file", e);
			System.exit(1);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.flush();
		bw.close();
	}

	
}
