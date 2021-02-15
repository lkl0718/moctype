package com.md.moktype.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class BaseReq implements Serializable {

    /**
     * 전문 구분값
     */
    @SerializedName("CMD")
    String CMD;

}
