package com.example.myapplication.utils;

import java.io.IOException;
import java.io.RandomAccessFile;

public class CPUUtils {
	
	public static float readUsage() {
	    try {
			//System.out.println("ENTRO");
			String load = null;
			RandomAccessFile reader = null,reader2 = null;

			while (load == null) {
				reader = new RandomAccessFile("/sdcard/Download/cpu-usage-sample-1.txt", "r");
				if (reader != null)
					load = reader.readLine();
			}
			//System.out.println("Primer impresion " + load);

			String[] toks = load.split(" ");


	        long idle1 = Long.parseLong(toks[5]);
	        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			//System.out.println("Primer impresion " + cpu1);
	        try {
	            Thread.sleep(360);
	        } catch (Exception e) {}

	        //reader.seek(0);
			load = null;
			while ( load == null) {
				reader2 = new RandomAccessFile("/sdcard/Download/cpu-usage-sample-2.txt", "r");
				if(reader2 != null)
					load = reader2.readLine();
			}
	        reader.close();
	        reader2.close();
	        toks = load.split(" ");

	        long idle2 = Long.parseLong(toks[5]);
	        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
	            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	       // System.out.println("Segunda impresion " + cpu2);
			return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return 0;
	} 

}
