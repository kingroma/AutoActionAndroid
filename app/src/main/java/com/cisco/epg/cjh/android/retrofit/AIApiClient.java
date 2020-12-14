package com.cisco.epg.cjh.android.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AIApiClient {
    private static AIApiClient INSTANCE = null ;
    private static Retrofit RETROFIT = null ;

    public AIApiService getAiApiService(){
        return RETROFIT.create(AIApiService.class);
    }

    private AIApiClient(){
        try {
            RETROFIT = new Retrofit.Builder()
                    // .baseUrl("http://58.140.89.77:8080/api/") // 외부
                    .baseUrl("http://10.20.0.165:8080/api/") // 내부
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static AIApiClient getInstace(){
        if ( INSTANCE == null ) {
            INSTANCE = new AIApiClient();
        }

        return INSTANCE;
    }
}
