package com.example.myapplication.service;


public class ThresholdNotificator {
    private static ThresholdNotificator instance;
    private double currentLevel;

    private ThresholdNotificator() {
    }

    synchronized static ThresholdNotificator getInstance() {
        if (instance == null) instance = new ThresholdNotificator();
        return instance;
    }

    public synchronized void updateThresholdLevel(double level) {
        this.currentLevel = level;
    }

    public double getCurrentLevel() {
        return currentLevel;
    }
}
