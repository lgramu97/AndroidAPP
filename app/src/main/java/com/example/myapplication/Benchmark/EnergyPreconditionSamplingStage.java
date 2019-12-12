package com.example.myapplication.Benchmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnergyPreconditionSamplingStage {

    @SerializedName("requiredBatteryState")
    @Expose
    private String requiredBatteryState;
    @SerializedName("minStartBatteryLevel")
    @Expose
    private Double minStartBatteryLevel;

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

}
