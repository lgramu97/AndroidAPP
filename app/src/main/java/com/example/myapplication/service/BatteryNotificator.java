package com.example.myapplication.service;


public class BatteryNotificator {
    private static BatteryNotificator instance;
    private double currentLevel;

    private BatteryNotificator() {
    }

    public synchronized static BatteryNotificator getInstance() {
        if (instance == null) instance = new BatteryNotificator();
        return instance;
    }

    public synchronized void updateBatteryLevel(double level) {
        this.currentLevel = level;
    }

    public double getCurrentLevel() {
        return currentLevel;
    }
}
