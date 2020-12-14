package com.cisco.epg.cjh.android.retrofit;

import androidx.annotation.NonNull;

/*
* {

        "error": {

        "status": "400",

        "code": "A0101",

        "message": " 요청 정보가 올바르지 않습니다. (입력 오류)",

        "detail": "InvalidInputException: Parameter [deviceId] has wrong value"

        }

        }
        *
        *
A0000 205 클라이언트 접속 종료
A0101 400 요청 정보가 올바르지 않습니다. (입력 오류)
A0102 404 요청 정보가 올바르지 않습니다. (존재하지 않는 자원)
A9997 503 잠시만 기다려 주십시오.
A9998 502 챗봇 서버 연동 오류가 발생했습니다.
A9999 500 확인되지 않는 오류가 발생했습니다.
* */
public class AIApiErrorResponse {

    private String status = null ;

    private String code = null ;

    private String message = null ;

    private String detail = null ;

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb
                .append("[AIApiErrorResponse ")
                .append("status=")
                .append(status)
                .append(", code=")
                .append(code)
                .append(", message=")
                .append(message)
                .append(", detail=")
                .append(detail)
                .append(" ]")
        ;

        return sb.toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
