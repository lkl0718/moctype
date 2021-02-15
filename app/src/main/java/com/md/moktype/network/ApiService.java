package com.md.moktype.network;

import android.text.TextUtils;

import com.md.moktype.BuildConfig;
import com.md.moktype.common.Constants;
import com.md.moktype.utils.Prefs;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private static Service service;

    public static synchronized Service getInstance() {
        if(service != null) {
            return service;
        }

        service = new Retrofit.Builder()
                .baseUrl(Constants.SERVER_URL)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Service.class);

        return service;
    }

    private static OkHttpClient getHttpClient() {

        //header
        Interceptor interceptorHeader = chain -> {
            String token = Prefs.getString(Constants.PREFS_LOGIN_TOKEN);
            if(!TextUtils.isEmpty(token)) {
                Request.Builder builder = chain.request().newBuilder()
                        .header("LOGIN_TOKEN", token);

                return chain.proceed(builder.build());
            }

            return chain.proceed(chain.request());
        };

        //HTTP Log
        if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptorLogging = new HttpLoggingInterceptor();
            interceptorLogging.setLevel(HttpLoggingInterceptor.Level.BODY);

            return new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptorLogging)
                    .addInterceptor(interceptorHeader)
                    .build();
        }

        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptorHeader)
                .build();
    }
}
