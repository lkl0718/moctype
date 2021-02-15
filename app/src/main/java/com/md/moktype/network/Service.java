package com.md.moktype.network;

import com.md.moktype.network.data.request.AlarmReq;
import com.md.moktype.network.data.request.AuthReq;
import com.md.moktype.network.data.BaseReq;
import com.md.moktype.network.data.request.EmailReq;
import com.md.moktype.network.data.request.PassoMeterReq;
import com.md.moktype.network.data.request.PushReq;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Service {

    //푸시토큰 전달
    @POST("app/appapi/appApiCall.do")
    Call<Object> sendDeviceInfo(@Body PushReq pushReq);

    //알람 설정 가져오기
    @POST("app/appapi/appApiCall.do")
    Call<Object> requestAlarmSetting(@Body BaseReq req);

    //알람 설정
    @POST("app/appapi/appApiCall.do")
    Call<Object> sendAlarmSetting(@Body AlarmReq req);

    //이메일 인증코드 발송
    @POST("app/appapi/appApiCall.do")
    Call<Object> sendEmail(@Body EmailReq req);

    //이메일 인증코드 검증
    @POST("app/appapi/appApiCall.do")
    Call<Object> authEmail(@Body AuthReq req);

    //걸음 수 저장
    @POST("app/appapi/appApiCall.do")
    Call<Object> sendSteps(@Body PassoMeterReq req);

    //////////

    // 버전체크
    @POST("appVersion.do")
    Call<Object> getVersion();

    // 이미지 전송
    @Multipart
    @POST("app/appapi/appApiCall.do")
    Call<Object> uploadImages(@Part MultipartBody.Part file,
                                @Part("CMD") RequestBody cmd);

}
