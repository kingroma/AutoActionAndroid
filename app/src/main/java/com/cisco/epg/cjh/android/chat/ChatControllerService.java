package com.cisco.epg.cjh.android.chat;

import android.view.View;
import android.widget.LinearLayout;


public interface ChatControllerService {
    public void addUserChat(String message);

    public void addSystemChat(ChatObject co) ;

    public void addGap() ;

    public void addChatGap(LinearLayout linearLayout, int gap) ;

    public void addPlzSaySomething() ;

    public View getSystemTextWithYellow(String text1, String text2) ;

    public View getSystemTextWithCheck(String text) ;

    public void clearSystemIcon();

    public void clearButton();

    public View getImageView(int srcId, int width, int height);

    public View getImageView(String url, int width, int height);

    public View getImageView(String url);

    public View getVideoView(String url, int width, int height);

    public View getButtonView(String buttonText1, String buttonText2,View.OnClickListener listener1 , View.OnClickListener listener2);

    public void addErrorMessage();

    public void addErrorMessage(String str);

    public void addRerecordPlz();
}
