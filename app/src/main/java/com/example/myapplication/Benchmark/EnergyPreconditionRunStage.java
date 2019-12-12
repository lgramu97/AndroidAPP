package com.example.myapplication.Benchmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnergyPreconditionRunStage {

    @SerializedName("requiredBatteryState")
    @Expose
    private String requiredBatteryState;
    @SerializedName("minStartBatteryLevel")
    @Expose
    private Double minStartBatteryLevel;
    @SerializedName("minEndBatteryLevel")
    @Expose
    private Double minEndBatteryLevel;

    public String getRequiredBatteryState() {
        return requiredBatteryState;
    }

    public void setRequiredBatteryState(String requiredBatteryState) {
        this.requiredBatteryState = requiredBatteryState;
    }

    public Double getMinStartBatteryLevel() {
        return minStartBatteryLevel;
    }

    public void setMinStartBatteryLevel(Double minStartBatteryLevel) {
        this.minStartBatteryLevel = minStartBatteryLevel;
    }

    public Double getMinEndBatteryLevel() {
        return minEndBatteryLevel;
    }

    public void setMinEndBatteryLevel(Double minEndBatteryLevel) {
        this.minEndBatteryLevel = minEndBatteryLevel;
    }
}