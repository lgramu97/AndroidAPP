package com.example.myapplication.Benchmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParamsRunStage {

    @SerializedName("cpuLevel")
    @Expose
    private Double cpuLevel;
    @SerializedName("screenState")
    @Expose
    private String screenState;

    public Double getCpuLevel() {
        return cpuLevel;
    }

    public void setCpuLevel(Double cpuLevel) {
        this.cpuLevel = cpuLevel;
    }

    public String getScreenState() {
        return screenState;
    }

    public void setScreenState(String screenState) {
        this.screenState = screenState;
    }
}

