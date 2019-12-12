package com.example.myapplication.Benchmark;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Variant {

    @SerializedName("variantId")
    @Expose
    private String variantId;
    @SerializedName("paramsSamplingStage")
    @Expose
    private ParamsSamplingStage paramsSamplingStage;
    @SerializedName("paramsRunStage")
    @Expose
    private ParamsRunStage paramsRunStage;
    @SerializedName("energyPreconditionSamplingStage")
    @Expose
    private EnergyPreconditionSamplingStage energyPreconditionSamplingStage;
    @SerializedName("energyPreconditionRunStage")
    @Expose
    private EnergyPreconditionRunStage energyPreconditionRunStage;

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public ParamsSamplingStage getParamsSamplingStage() {
        return paramsSamplingStage;
    }

    public void setParamsSamplingStage(ParamsSamplingStage paramsSamplingStage) {
        this.paramsSamplingStage = paramsSamplingStage;
    }

    public ParamsRunStage getParamsRunStage() {
        return paramsRunStage;
    }

    public void setParamsRunStage(ParamsRunStage paramsRunStage) {
        this.paramsRunStage = paramsRunStage;
    }

    public EnergyPreconditionSamplingStage getEnergyPreconditionSamplingStage() {
        return energyPreconditionSamplingStage;
    }

    public void setEnergyPreconditionSamplingStage(EnergyPreconditionSamplingStage energyPreconditionSamplingStage) {
        this.energyPreconditionSamplingStage = energyPreconditionSamplingStage;
    }

    public EnergyPreconditionRunStage getEnergyPreconditionRunStage() {
        return energyPreconditionRunStage;
    }

    public void setEnergyPreconditionRunStage(EnergyPreconditionRunStage energyPreconditionRunStage) {
        this.energyPreconditionRunStage = energyPreconditionRunStage;
    }

}
