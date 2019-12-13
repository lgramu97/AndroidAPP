package com.example.myapplication.utils;

import android.util.Log;

import com.example.myapplication.MainActivity;

public class CPUUserThread extends Thread {
	
	private long sleep;
	private int cycles=1000000;
	private boolean alive;
	
	public CPUUserThread(){
		super();
		this.alive=true;
		this.setDaemon(true);
	}

	@Override
	/**
	 * Consumes CPU by means of floating point operation and then sleeps
	 */
	public void run() {
		double a=1;
		double b=2;
		while(this.alive){
			synchronized (this) {
				if(this.sleep>0)
					try {
						this.wait(this.sleep);
					} catch (InterruptedException e) {
						Log.e(MainActivity.LOG_TAG, "Fail waiting", e);
					}
			}
			for(int i=0;i<cycles;i++){
				if(a==0)a=1;
				a*=b;
			}
		}
	}

	public synchronized long getSleep() {
		return sleep;
	}

	public synchronized void setSleep(long sleep) {
		this.sleep = sleep;
		this.notify();
	}
	
	public synchronized void kill(){
		this.alive=false;
		this.notify();
	}
}
