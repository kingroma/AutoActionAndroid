package com.cisco.epg.cjh.android.retrofit;

import androidx.annotation.NonNull;

public class AIApiAnswerResponse {
    private String text = null ;

    private String imgUrl = null;

    private String movieUrl = null ;

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb
                .append("[AIApiAnswerResponse ")
                .append("text=")
                .append(text)
                .append(", imgUrl=")
                .append(imgUrl)
                .append(", movieUrl=")
                .append(movieUrl)
                .append(" ]")
        ;

        return sb.toString();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }
}
