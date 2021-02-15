package com.md.moktype.network.data.request;

import com.google.gson.annotations.SerializedName;
import com.md.moktype.network.data.BaseReq;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class PushReq extends BaseReq implements Serializable {

    /**
     * 푸시토큰
     */
    @SerializedName("pushToken")
    String pushToken;

    /**
     * OS 구분
     * 구분값 : aos or ios
     */
    @SerializedName("DEV_CD")
    String DEV_CD;

    /**
     * OS 버전
     */
    @SerializedName("DEV_OS")
    String DEV_OS;

    /**
     * 모델명
     */
    @SerializedName("DEV_INFO1")
    String DEV_INFO1;

    /**
     * APP버전
     */
    @SerializedName("DEV_INFO2")
    String DEV_INFO2;
}
