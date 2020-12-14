package com.cisco.epg.cjh.android.chat;

import android.view.View;

import com.cisco.epg.cjh.android.R;

import java.util.ArrayList;
import java.util.List;

public class ChatObject {
    /**
     * ? ICON
     * */
    public static final int SYSTEM_ICON_CURIOUS_ID = R.drawable.bot_curious ;

    /**
     * ! ICON
     * */
    public static final int SYSTEM_ICON_REPLY_ID = R.drawable.bot_reply ;

    /**
     * SORRY ICON
     * */
    public static final int SYSTEM_ICON_SORRY_ID = R.drawable.bot_sorry ;

    /**
     * 종료시 ICON
     * */
    public static final int SYSTEM_ICON_CLOSE_ID = R.drawable.bot_close ;

    /**
     * 왼쪽 아래 말풍선
     * */
    public static final int SYSTEM_BACKGROUND_RADIUS_1 = R.drawable.system_radius_1;

    /**
     * 왼쪽 위 말풍선
     * */
    public static final int SYSTEM_BACKGROUND_RADIUS_2 = R.drawable.system_radius_2;

    private int background = SYSTEM_BACKGROUND_RADIUS_1;

    private int systemIconId = -1 ;

    private String title ;

    private String text ;

    private List<View> child = new ArrayList<View>();

    private View buttons ;

    private boolean isReplyPlz = false ;

    /**
     * tts 할떄 사용할려고
     * */
    private String audioText ;

    public void setBackground(int background) {
        this.background = background;
    }

    public int getBackground() {
        return background;
    }

    public void setSystemIconId(int systemIconId) {
        this.systemIconId = systemIconId;
    }

    public int getSystemIconId() {
        return systemIconId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public List<View> getChild() {
        return child;
    }

    public void addChild(View view){
        this.child.add(view);
    }

    public View getButtons() {
        return buttons;
    }

    public void setButtons(View buttons) {
        this.buttons = buttons;
    }

    public String getAudioText() {
        return audioText;
    }

    public void setAudioText(String audioText) {
        this.audioText = audioText;
    }

    public boolean isReplyPlz() {
        return isReplyPlz;
    }

    public void setReplyPlz(boolean replyPlz) {
        isReplyPlz = replyPlz;
    }
}
