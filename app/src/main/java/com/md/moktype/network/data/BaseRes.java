package com.md.moktype.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class BaseRes implements Serializable {

    /**
     * 결과값
     *      0 : 성공
     *      1 : 실패
     *
     * 이메일 인증번호 발송
     *      0 : 성공
     *      1 : 이메일과 로그인 ID 불일치
     *      2 : 인증 DB 오류
     *      3 : 인증번호 발급 오류
     *
     * 이메일 인증번호 확인
     *      0 : 성공
     *      1 : 인증번호 불일치
     *      2 : 인증 DB 오류
     */
    @SerializedName("result")
    int result;

}
