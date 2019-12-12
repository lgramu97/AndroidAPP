package com.example.myapplication.Benchmark;

import com.example.myapplication.service.BatteryNotificator;

public class BatteryStopCondition implements StopCondition {
    private double batteryMinLevel;
    private BatteryNotificator batteryNotificator;

    public BatteryStopCondition(double batteryMinLevel, BatteryNotificator batteryNotificator) {
        this.batteryMinLevel = batteryMinLevel;
        this.batteryNotificator = batteryNotificator;
    }

    @Override
    public boolean canContinue() {
        return batteryMinLevel < batteryNotificator.getCurrentLevel();
    }
}
