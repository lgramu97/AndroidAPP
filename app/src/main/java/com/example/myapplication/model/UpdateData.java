package com.example.myapplication.model;

public class UpdateData {

    private int cpu_mhz;
    private int battery_Mah;
    private double minStartBatteryLevel;
    private double currentBatteryLevel;

    public UpdateData(int cpu_mhz, int battery_Mah, double minStartBatteryLevel, double currentBatteryLevel) {
        this.cpu_mhz = cpu_mhz;
        this.battery_Mah = battery_Mah;
        this.minStartBatteryLevel = minStartBatteryLevel;
        this.currentBatteryLevel = currentBatteryLevel;
    }


    public int getCpu_mhz() {
        return cpu_mhz;
    }

    public int getBattery_Mah() {
        return battery_Mah;
    }

    public double getMinStartBatteryLevel() {
        return minStartBatteryLevel;
    }

    public double getCurrentBatteryLevel() {
        return currentBatteryLevel;
    }
}
