package com.example.myapplication.utils;

import android.util.Log;

import com.example.myapplication.MainActivity;

public class AutoTunerZeroCPUUse extends AutoTuner {

	@Override
	//CPU autotuner for not 0% CPU load target.
	public void run() {
		boolean stable=false;
		boolean nowStable;
		while(!this.isKilled()){
			synchronized (this) {
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				float cpuUsage=cpuUsage();
				Log.i(MainActivity.LOG_TAG, "CPU Usage: "+cpuUsage);
				nowStable=cpuUsage<getThreshold();
				if(getListener()!=null){
					//Log.i(AndroidCPUBatteryProfilerActivity.LOG_TAG, "Calling Listener "+nowStable);
					getListener().onCPUUsageRead(cpuUsage,0);
					if(!stable&&nowStable){
						stable=true;
						getListener().onStable(cpuUsage);
					}
					if(stable&&!nowStable){
						stable=false;
						getListener().onUnStable(cpuUsage);
					}
				}
			}
		}
	}
}
