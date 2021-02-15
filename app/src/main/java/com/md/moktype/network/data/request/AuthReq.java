package com.md.moktype.network.data.request;

import com.google.gson.annotations.SerializedName;
import com.md.moktype.network.data.BaseReq;

import java.io.Serializable;

import lombok.Data;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class AuthReq extends BaseReq implements Serializable {

    /**
     * 이메일 인증번호
     */
    @SerializedName("authCode")
    String authCode;

}
