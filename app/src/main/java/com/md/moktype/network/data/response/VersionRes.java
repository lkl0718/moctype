package com.md.moktype.network.data.response;

import com.google.gson.annotations.SerializedName;
import com.md.moktype.network.data.BaseRes;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class VersionRes extends BaseRes implements Serializable {

    /**
     * 안드로이드 버전정보
     */
    @SerializedName("aos")
    String aos;

    /**
     * 아이폰 버전정보
     */
    @SerializedName("ios")
    String ios;

}
