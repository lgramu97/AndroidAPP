package com.example.myapplication.Benchmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParamsSamplingStage {

    @SerializedName("convergenceThreshold")
    @Expose
    private Double convergenceThreshold;

    public Double getConvergenceThreshold() {
        return convergenceThreshold;
    }

    public void setConvergenceThreshold(Double convergenceThreshold) {
        this.convergenceThreshold = convergenceThreshold;
    }

}
