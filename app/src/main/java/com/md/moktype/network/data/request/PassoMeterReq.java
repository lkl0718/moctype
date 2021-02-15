package com.md.moktype.network.data.request;

import com.google.gson.annotations.SerializedName;
import com.md.moktype.network.data.BaseReq;

import java.io.Serializable;

import lombok.Data;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class PassoMeterReq extends BaseReq implements Serializable {

    /**
     * 걸음 수
     */
    @SerializedName("passoMeter")
    int passoMeter;

}
