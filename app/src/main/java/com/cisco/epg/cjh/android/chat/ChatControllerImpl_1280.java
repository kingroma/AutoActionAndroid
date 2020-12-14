package com.cisco.epg.cjh.android.chat;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cisco.epg.cjh.android.R;

public class ChatControllerImpl_1280 extends ChatControllerImpl implements ChatControllerService {


    public ChatControllerImpl_1280(Context context, LinearLayout parent, ScrollView scroll){
        super(context,parent,scroll);

        SYSTEM_ICON_WIDTH = 46;
        SYSTEM_ICON_HEIGHT = 80;

        DEFAULT_SYSTEM_GAP_SIZE = 6 ;
        DEFAULT_CHAT_GAP_SIZE = 10 ;
        DEFAULT_CHAT_BUTTON_GAP_SIZE = 26 ;
    }

    public void addUserChat(String message){
        clearButton();

        View view = getView(R.layout.user_chat_1280);

        TextView textView = view.findViewById(R.id.text);
        textView.setText(message);

        addView(view);
    }

    public void addSystemChat(ChatObject co){
        if ( co != null ) {
            View view = getView(R.layout.system_chat_1280);
            LinearLayout systemParent = view.findViewById(R.id.systemParent);
            systemParent.setBackgroundResource(co.getBackground());

            int idx = 0 ;

            if ( co.getSystemIconId() != -1 ) {
                ImageView systemIcon = view.findViewById(R.id.systemIcon);
                systemIcon.setImageResource(co.getSystemIconId());
                systemIcon.setLayoutParams(new LinearLayout.LayoutParams(SYSTEM_ICON_WIDTH,SYSTEM_ICON_HEIGHT));

                clearSystemIcon();

                prevSystemIcon = systemIcon ;
            }

            if ( co.getTitle() != null && !co.getTitle().isEmpty() ) {
                View temp = getView(R.layout.system_title_1280);
                TextView title = temp.findViewById(R.id.title);
                title.setText(co.getTitle());

                systemParent.addView(temp);

                idx ++ ;
            }

            if ( co.getText() != null && !co.getText().isEmpty() ) {
                if ( idx > 0 ) { addChatGap(systemParent,DEFAULT_CHAT_GAP_SIZE); }

                View temp = getView(R.layout.system_text_1280);
                TextView text = temp.findViewById(R.id.text);
                text.setText(co.getText());

                systemParent.addView(temp);

                idx ++ ;
            }

            if ( co.getChild() != null && co.getChild().size() > 0 ) {
                if ( idx > 0 ) { addChatGap(systemParent,DEFAULT_CHAT_GAP_SIZE); }

                for ( View temp : co.getChild() ) {
                    systemParent.addView(temp);
                }

                idx ++ ;
            }

            if ( co.getButtons() != null ) {
                if ( idx > 0 ) { addChatGap(systemParent,DEFAULT_CHAT_BUTTON_GAP_SIZE); }

                systemParent.addView(co.getButtons());

                prevButtonView = co.getButtons();

                idx ++ ;
            }

            if (co.isReplyPlz() ){
                this.addPlzSaySomething();
                return ;
            }

            addView(view);
        }
    }

    public void addPlzSaySomething(){
        addView(getView(R.layout.system_plz_say_something_1280));
    }

    public View getSystemTextWithYellow(String text1, String text2) {
        View view = getView(R.layout.system_text_with_yellow_1280);

        TextView textView1 = view.findViewById(R.id.text1);
        textView1.setText(text1);

        TextView textView2 = view.findViewById(R.id.text2);
        textView2.setText(text2);

        return view;
    }

    public View getSystemTextWithCheck(String text) {
        View view = getView(R.layout.system_text_with_check_1280);

        TextView textView = view.findViewById(R.id.text);
        textView.setText(text);

        return view;
    }

    public View getButtonView(String buttonText1, String buttonText2,View.OnClickListener listener1 , View.OnClickListener listener2){
        View view = getView(R.layout.system_buttons_1280);

        Button button1 = view.findViewById(R.id.buttonText1);
        button1.setText(buttonText1);
        button1.setOnClickListener(listener1);

        Button button2 = view.findViewById(R.id.buttonText2);
        button2.setText(buttonText2);
        button2.setOnClickListener(listener2);

        return view ;
    }

}
