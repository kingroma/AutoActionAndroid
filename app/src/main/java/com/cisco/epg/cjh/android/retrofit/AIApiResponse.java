package com.cisco.epg.cjh.android.retrofit;

import androidx.annotation.NonNull;

import java.util.List;

public class AIApiResponse {


    private String status = null ;

    /**
     * text 정리해서 주기
     * */
    private String query = null ;

    private AIApiErrorResponse error = null ;

    private AIApiAnswerResponse answer = null ;

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb
            .append("[AIApiResponse ")
            .append("status=")
            .append(status)
            .append(", query=")
            .append(query)
            .append(", error=")
            .append(error)
            .append(", answer=")
            .append(answer)
            .append(" ]")
        ;

        return sb.toString();
    }

    public boolean isError(){
        if ( error == null ) {
            return false ;
        } else {
            return true ;
        }
    }

    public void setAnswer(AIApiAnswerResponse answer) {
        this.answer = answer;
    }

    public AIApiAnswerResponse getAnswer() {
        return answer;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setError(AIApiErrorResponse error) {
        this.error = error;
    }

    public AIApiErrorResponse getError() {
        return error;
    }
}
