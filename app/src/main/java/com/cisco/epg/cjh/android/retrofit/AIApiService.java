package com.cisco.epg.cjh.android.retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AIApiService {

    /**
     * request
     * {
     *        "deviceId":[string],
     *        "query":[string]
     * }
     *
     * response
     * {
     *      "status": "OK",
     *      "query":"도와줘 리모콘이 잘 안돼",
     *      "answer":[
     *          {text:"무었을 도와 드릴까요", imgUrl:"http://aa.co.kr/img/test.png","movieUrl":""}
     *      ]
     * }
     */
    @POST("talk")
    public Call<AIApiResponse> request(@Body JsonObject jsonObject);
}
