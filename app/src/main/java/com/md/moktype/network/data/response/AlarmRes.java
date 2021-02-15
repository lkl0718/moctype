package com.md.moktype.network.data.response;

import com.google.gson.annotations.SerializedName;
import com.md.moktype.network.data.BaseRes;

import java.io.Serializable;

import lombok.Data;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
@Data
public class AlarmRes extends BaseRes implements Serializable {

    /**
     * 주요 공지사항 알람
     */
    @SerializedName("noticeAlarm")
    boolean noticeAlarm;

    /**
     * 챌린지 초대
     */
    @SerializedName("challengeAlarm")
    boolean challengeAlarm;

    /**
     * 문의/제안 답변
     */
    @SerializedName("answerAlarm")
    boolean answerAlarm;

    /**
     * 이벤트
     */
    @SerializedName("eventAlarm")
    boolean eventAlarm;

    /**
     * Quick Poll 및 Quiz
     */
    @SerializedName("quizAlarm")
    boolean quizAlarm;

}
