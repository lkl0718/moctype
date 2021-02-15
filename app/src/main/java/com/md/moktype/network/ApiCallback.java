package com.md.moktype.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        // 결과값이 없거나 HTTP Error
        if(null == response.body() || !response.isSuccessful()) {
            onFail(response.code(), response.message(), new Throwable("http code " + response.code()));
            return;
        }

        try {
            JSONObject obj = new JSONObject(new Gson().toJson(response.body()));

            //결과
            int result = obj.has("Data") ? obj.getJSONObject("Data").getInt("result") : obj.getInt("result");
            if(result == 0) {
                onSuccess(obj.has("Data") ? obj.getJSONObject("Data") : obj);
            }else{
                onFail(result, null, new Throwable());
            }

        } catch (JSONException e) {
            onFail(-1, e.getMessage(), e);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFail(-1, t.getMessage(), t);
    }

    public abstract void onSuccess(@NonNull JSONObject object);
    public abstract void onFail(int code, @Nullable String message, @NonNull Throwable t);
}
