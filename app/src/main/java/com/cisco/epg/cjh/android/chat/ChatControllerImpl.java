package com.cisco.epg.cjh.android.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.cisco.epg.cjh.android.R;

import java.net.URL;

public class ChatControllerImpl implements ChatControllerService {
    protected int SYSTEM_ICON_WIDTH = 70;
    protected int SYSTEM_ICON_HEIGHT = 120;

    protected int DEFAULT_SYSTEM_GAP_SIZE = 10 ;
    protected int DEFAULT_CHAT_GAP_SIZE = 16 ;
    protected int DEFAULT_CHAT_BUTTON_GAP_SIZE = 40 ;

    protected Context context = null ;
    protected LinearLayout parent = null ;
    protected ScrollView scroll = null ;

    protected ImageView prevSystemIcon = null ;

    protected View prevButtonGapView = null ;
    protected View prevButtonView = null ;

    public ChatControllerImpl(Context context, LinearLayout parent, ScrollView scroll){
        this.context = context;
        this.parent = parent ;
        this.scroll = scroll ;
    }

    /**
     * 부모에 뷰 추가
     * */
    protected void addView(View view){
        parent.addView(view);
        addGap();

        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.scrollTo(0,parent.getHeight());
            }
        });
    }

    /**
     * layout 에서 view 꺼내오기
     * */
    protected View getView(int srcId) {
        return LayoutInflater.from(context).inflate(srcId,parent,false);
    }

    /**
     * 유저 채팅 추가
     * */
    public void addUserChat(String message){
        clearButton();

        View view = getView(R.layout.user_chat);

        TextView textView = view.findViewById(R.id.text);
        textView.setText(message);

        addView(view);
    }

    /**
     * 시스템 체팅 추가
     * */
    public void addSystemChat(ChatObject co){
        if ( co != null ) {
            View view = getView(R.layout.system_chat);
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
                View temp = getView(R.layout.system_title);
                TextView title = temp.findViewById(R.id.title);
                title.setText(co.getTitle());

                systemParent.addView(temp);

                idx ++ ;
            }

            if ( co.getText() != null && !co.getText().isEmpty() ) {
                if ( idx > 0 ) { addChatGap(systemParent,DEFAULT_CHAT_GAP_SIZE); }

                View temp = getView(R.layout.system_text);
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

    /**
     * 메시지간의 겝
     * */
    public void addGap(){
        // systemGap
        View view = getView(R.layout.system_gap);

        LinearLayout systemGap = view.findViewById(R.id.systemGap);
        systemGap.setLayoutParams(new LinearLayout.LayoutParams(0,DEFAULT_SYSTEM_GAP_SIZE));

        parent.addView(view);
    }

    /**
     * 메시지 채팅안의 겝
     * */
    public void addChatGap(LinearLayout linearLayout,int gap){
        View view = getView(R.layout.system_chat_gap);

        LinearLayout chatGap = view.findViewById(R.id.chatGap);
        chatGap.setLayoutParams(new LinearLayout.LayoutParams(0,gap));

        linearLayout.addView(view);

        if ( gap == DEFAULT_CHAT_BUTTON_GAP_SIZE ) {
            prevButtonGapView = view ;
        }
    }

    /**
     * 음성으로 답해주세요 메세지
     * */
    public void addPlzSaySomething(){
        addView(getView(R.layout.system_plz_say_something));
    }

    /**
     * 왼쪽 노랑색 / 오른쪽 하얀색 메세지
     * */
    public View getSystemTextWithYellow(String text1, String text2) {
        View view = getView(R.layout.system_text_with_yellow);

        TextView textView1 = view.findViewById(R.id.text1);
        textView1.setText(text1);

        TextView textView2 = view.findViewById(R.id.text2);
        textView2.setText(text2);

        return view;
    }

    /**
     * 왼쪽 체크박스 / 오른쪽 메시지
     * */
    public View getSystemTextWithCheck(String text) {
        View view = getView(R.layout.system_text_with_check);

        TextView textView = view.findViewById(R.id.text);
        textView.setText(text);

        return view;
    }

    /**
     * 이전 ICON들 없애기
     * */
    public void clearSystemIcon(){
        if ( prevSystemIcon != null ) {
            prevSystemIcon.setVisibility(View.INVISIBLE);
            // prevSystemIcon.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            prevSystemIcon = null ;
        }
    }

    /**
     * 이전 Button들 없애기
     * */
    public void clearButton(){
        if ( prevButtonView != null ) {
            prevButtonView.setVisibility(View.INVISIBLE);
            prevButtonView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            prevButtonView = null ;
        }

        if ( prevButtonGapView != null ) {
            prevButtonGapView.setVisibility(View.INVISIBLE);
            prevButtonGapView.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            prevButtonGapView = null ;
        }

    }

    /**
     * 이미지 꺼내오기
     * */
    public View getImageView(int srcId , int width , int height){
        View view = getView(R.layout.system_image);

        ImageView image = view.findViewById(R.id.image);
        image.setImageResource(srcId);
        image.setLayoutParams(new LinearLayout.LayoutParams(width,height));
        image.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.scrollTo(0,parent.getHeight());
                    }
                });
            }

            @Override
            public void onViewDetachedFromWindow(View view) {

            }
        });
        image.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.scrollTo(0,parent.getHeight());
                    }
                });
            }
        });



        return view ;
    }

    // https://www.google.com/logos/doodles/2020/december-holidays-days-2-30-6753651837108830.3-law.gif
    /**
     * 이미지 꺼내오기2 URL
     * */
    public View getImageView(String url, int width , int height){
        View view = getView(R.layout.system_image);

        ImageView image = view.findViewById(R.id.image);
        image.setLayoutParams(new LinearLayout.LayoutParams(width,height));
        ChatImageDownloadFileTask downloader = new ChatImageDownloadFileTask(image);

        downloader.execute(url);

        return view ;
    }

    public View getImageView(String url){
        View view = getView(R.layout.system_image);

        ImageView image = view.findViewById(R.id.image);
        ChatImageDownloadFileTask downloader = new ChatImageDownloadFileTask(image);

        downloader.execute(url);

        return view ;
    }

    /**
     * 비디오 꺼내오기 URL
     * */
    public View getVideoView(String url, int width , int height){
        View view = getView(R.layout.system_video);

        VideoView video = view.findViewById(R.id.video);
        video.setLayoutParams(new LinearLayout.LayoutParams(width,height));
        // video.setVideoURI(Uri.parse(url));


        // Bitmap thumb = ThumbnailUtils.createVideoThumbnail("https://www.google.com/logos/doodles/2020/december-holidays-days-2-30-6753651837108830.3-law.gif",
        //         MediaStore.Images.Thumbnails);
        // BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
        // video.setBackgroundDrawable(bitmapDrawable);

        try {
            Bitmap bmp = BitmapFactory.decodeStream(new URL("https://www.google.com/logos/doodles/2020/december-holidays-days-2-30-6753651837108830.3-law.gif").openConnection().getInputStream());
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
            video.setBackground(bitmapDrawable);


        } catch ( Exception e ){
            e.printStackTrace();
        }




        if ( video.isPlaying() ){
            System.out.println("playing");
        } else {
            System.out.println("is not play");

            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    System.out.println("Prepared");
                    // mp.start();
                }
            });
        }

        return view ;
    }


    /**
     * 버튼 뷰 꺼내오기
     * */
    public View getButtonView(String buttonText1, String buttonText2,View.OnClickListener listener1 , View.OnClickListener listener2){
        View view = getView(R.layout.system_buttons);

        Button button1 = view.findViewById(R.id.buttonText1);
        button1.setText(buttonText1);
        button1.setOnClickListener(listener1);

        Button button2 = view.findViewById(R.id.buttonText2);
        button2.setText(buttonText2);
        button2.setOnClickListener(listener2);

        return view ;
    }

    public void addErrorMessage(){
        this.addErrorMessage("에러 발생 ㅠㅠ");
    }

    public void addErrorMessage(String str){
        ChatObject co1 = new ChatObject();
        co1.setSystemIconId(ChatObject.SYSTEM_ICON_SORRY_ID);
        co1.setText(str);
        this.addSystemChat(co1);
    }

    public void addRerecordPlz(){
        ChatObject co1 = new ChatObject();
        co1.setSystemIconId(ChatObject.SYSTEM_ICON_CURIOUS_ID);
        co1.setText("다시 말씀해주세요");
        this.addSystemChat(co1);
        // this.addPlzSaySomething();
    }


}
