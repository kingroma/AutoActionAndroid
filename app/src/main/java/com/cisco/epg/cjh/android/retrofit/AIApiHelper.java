package com.cisco.epg.cjh.android.retrofit;

public class AIApiHelper {

    private static AIApiHelper INSTANCE = null ;

    public static AIApiHelper getInstance(){
        if ( INSTANCE == null ) {
            INSTANCE = new AIApiHelper();
        }

        return INSTANCE ;
    }

    private AIApiHelper(){}

    private static final String ALEXA_REGEX = "[알얼] ?[렉랙넥낵] ?[서사스]";
    private static final String ALEXA_REPLACE = "Alexa";

    private static final String ALEXA_REGEX2 = "도 ?와 ?줘";
    private static final String ALEXA_REPLACE2 = "도와줘";
    public String userChatPatternRepace(String text){
        if ( text != null && !text.isEmpty() ){
            // 알렉사 REPLACE
            text = text.replaceAll(ALEXA_REGEX,ALEXA_REPLACE);
            text = text.replaceAll(ALEXA_REGEX2,ALEXA_REPLACE2);
        }

        return text ;
    }
}
