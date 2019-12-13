package com.example.myapplication.utils;

import android.util.Log;

import com.example.myapplication.MainActivity;

public class AutoTuner extends Thread {

	private static final int READING_TIMES = 30;
	private static final int WAITING = 200;

	public interface TunerListener {
		public StringBuilder onCPUUsageRead(float cpuUsage, long sleep);
		public void onStable(float cpuUsage);
		public void onUnStable(float cpuUsage);
	}
	
	private float target=0.03f;
	private float threshold=0.02f;
	private boolean alive=true;
	private TunerListener listener;
	private int cpus;

	public AutoTuner() {
		super();
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		long sleep=1;
		float cpuUsage;
		//creates as many cpu consumers as available cores
		CPUUserThread[] cpuUser=new CPUUserThread[this.cpus];
		for(int i=0;i<this.cpus;i++){
			cpuUser[i]=new CPUUserThread();
			cpuUser[i].setSleep(sleep);
			cpuUser[i].start();
		}
		boolean stable=false;
		boolean nowStable;
		float diff;
		long sleepNew;
		//checks cpu usage and adjust sleep times to achieve CPU target load.
		while(alive){
			cpuUsage=cpuUsage();
			diff=cpuUsage/target;
			Log.i(MainActivity.LOG_TAG, "CPU Usage: "+cpuUsage+
					" sleep: "+sleep+" diff: "+diff);
			sleepNew=(long) (sleep*diff);
			nowStable=((-threshold)<(cpuUsage-target))&&((cpuUsage-target)<(threshold));

			if((sleep==sleepNew)
					&&!nowStable){
				if(diff > 1) sleep++;
				else sleep--;
			} else sleep=sleepNew;

			synchronized (this) {
				if(listener!=null){
					//Log.i(AndroidCPUBatteryProfilerActivity.LOG_TAG, "Calling Listener "+nowStable);
					listener.onCPUUsageRead(cpuUsage,sleep);
					if(!stable&&nowStable){
						stable=true;
						listener.onStable(cpuUsage);
					}
					if(stable&&!nowStable){
						stable=false;
						listener.onUnStable(cpuUsage);
					}
				}
			}
			for(int i=0;i<this.cpus;i++)
				cpuUser[i].setSleep(sleep);
		}
		Log.i(MainActivity.LOG_TAG, "End");
		for(int i=0;i<this.cpus;i++)
			cpuUser[i].kill();
	}

	/**
	 * Reads the CPU usage READING_TIMES times with WAITING ms of difference
	 * an them averages the results
	 * @return
     */
	protected synchronized float cpuUsage(){
		float result=0;
		//System.out.println("READ USAGEeeeeeeee");
		for(int i = 0; i< READING_TIMES; i++){
			result+=CPUUtils.readUsage();
			try {
				wait(WAITING);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result/ READING_TIMES;
	}
	
	public void kill(){
		this.alive=false;
	}
	
	public boolean isKilled(){
		return !this.alive;
	}

	public float getTarget() {
		return target;
	}

	public void setTarget(float target) {
		this.target = target;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threashold) {
		this.threshold = threashold;
	}

	public TunerListener getListener() {
		return listener;
	}

	public synchronized void setListener(TunerListener listener) {
		this.listener = listener;
	}

	public void setCPUs(int cpus) {
		this.cpus=cpus;
	}
	
		
}
