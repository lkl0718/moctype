package com.md.moktype.network.data.response;

import com.google.gson.annotations.SerializedName;
import com.md.moktype.network.data.BaseRes;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class EmailRes extends BaseRes implements Serializable {

    /**
     * 휴대폰번호
     */
    @SerializedName("tel_no")
    String telNo;

    /**
     * Quick Poll 및 Quiz
     */
    @SerializedName("email")
    String email;

}
