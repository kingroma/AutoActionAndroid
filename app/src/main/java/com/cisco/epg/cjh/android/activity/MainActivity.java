package com.cisco.epg.cjh.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.VideoView;


import com.cisco.epg.cjh.android.chat.ChatControllerService;
import com.cisco.epg.cjh.android.google.GoogleSpeechToTextService;
import com.cisco.epg.cjh.android.google.GoogleTextToSpeechService;
import com.cisco.epg.cjh.android.player.AudioPlayerService;
import com.cisco.epg.cjh.android.record.*;
import com.cisco.epg.cjh.android.R;
import com.cisco.epg.cjh.android.chat.ChatControllerImpl_1280;
import com.cisco.epg.cjh.android.chat.ChatObject;
import com.cisco.epg.cjh.android.retrofit.AIApiClient;
import com.cisco.epg.cjh.android.retrofit.AIApiParser;
import com.cisco.epg.cjh.android.retrofit.AIApiHelper;
import com.cisco.epg.cjh.android.retrofit.AIApiResponse;
import com.cisco.epg.cjh.android.retrofit.AIApiService;
import com.cisco.epg.cjh.android.util.PingService;


import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Response;


/*
 * 협의 확인사항
 * 1. response 올때 tts 어디까지 틀어줘야되나요
 * 2. API 서버 에러 발생했을때의 메세지
 * 3. ( 음성 인식 FAIL 시 혹은 서버 리턴 에러시 ) 다시 얘기해주세요라는 메시지 UI UX 혹은 그냥 TEXT로 ?
 * 4. ^^ / @@ 이거 두개 나누는것 보단 new line 을 두번 하면됨 ex > ^^^^
 * 5. VIDEO 플레이를 어떻게 ? 그 화면에서 / 종료는 어떤 event ? 시나리오상 종료에대한 시나리오는 없음
 * */

/*
 * 참고 사항
 * 1. GOOGLE API 연동 시 STB DATE / TIME 이 한국 시간과 1시간차이 안으로 나야지 ERROR 발생 안함
 * */

/*
 * 협의 후 TODO LIST
 * 1. 영상 재생
 * 2. API TO ChatObject Parser
 * 3. 에러 발생했을때 UI
 * 4. STT 에러 발생시 or null
 * 5. Global Key Listener
 * 6. TTS 시 음소거기능 추가
 * 7. 동영상 재생 추가 VISIBLE INVISIBLE
 * 8. Re Activity시 그냥 놔두는걸로 하고 clear 하는쪽으로하면어떨까
 * 9. 버튼 이용해서 동영상 종료 / 시작
 * 10. 예제로 몇개 해서 해볼까 그냐ㅐㅇ
 * */
public class MainActivity extends Activity {
    private final static String TAG = "MainActivity"; // .getClass().getSimpleName();

    public static String status = "C";

    private final static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() ;
    private final String FILE_NAME = "record.wav" ;
    private final String DEVICE_ID = UUID.randomUUID().toString();

    private static RecordService rs = null ;
    private static RecordListener rl = null ;
    private static ChatControllerService cc = null ;
    private static GoogleSpeechToTextService gs ;
    private static GoogleTextToSpeechService gt ;

    private AIApiClient aiApiClient = AIApiClient.getInstace();
    private static AIApiService aiApiService = null ;
    private static AIApiHelper helper = AIApiHelper.getInstance();

    public static VideoView videoView = null ;
    public static boolean isRecording = false ;
    private String result = null ;
    private Response<AIApiResponse> response = null ;
    private AIApiParser parser = null ;

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG,"onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        Log.d(TAG,"onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop()");
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        status = "C";

        this.init();


        Log.d(TAG,">> "+Settings.canDrawOverlays(getApplicationContext()));

        if ( !Settings.canDrawOverlays(getApplicationContext()) ){
            // Uri uri = Uri.fromParts("package","com.cisco.epg.cjh.android.activity",null);
            try {
                Log.d(TAG,getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:$" + getPackageName()));

                startActivityForResult(intent,12345);
            } catch(Exception e){
                Log.d(TAG,"ERROR " + e.getMessage());

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"onActivityResult()");
        super.onActivityResult(requestCode, resultCode, data);

        if ( !Settings.canDrawOverlays(getApplicationContext()) ) {
            Log.d(TAG,"권한실패");
        } else {
            Log.d(TAG,"권한성공");
            // this.init();
        }
    }

    private void init(){
        ScrollView scroll = findViewById(R.id.scroll);
        LinearLayout parent = findViewById(R.id.parent);
        videoView = findViewById(R.id.testVideo);
        // surfaceView = findViewById(R.id.surfaceVideo);

//        AudioAttributes aa =
//                new AudioAttributes.Builder()
//                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
//        videoView.setAudioAttributes(aa);

        if ( gs == null ) {
            InputStream auth = getResources().openRawResource(R.raw.credential);
            gs = new GoogleSpeechToTextService(auth);
        }

        if ( gt == null ) {
            InputStream auth2 = getResources().openRawResource(R.raw.credential);
            gt = new GoogleTextToSpeechService(auth2);
        }

        cc = new ChatControllerImpl_1280(getApplicationContext(), parent, scroll);

        if ( aiApiService == null ) {
            aiApiService = aiApiClient.getAiApiService();
        }

        // AFTER RECORD LISTENER
        if ( rl == null ) {
            rl = new RecordListener() {
                @Override
                public void after() {
                    File file = new File(PATH + "/" + FILE_NAME );
                    if ( file.exists() ) {
                        try {
                            result = gs.fileToText(file);
                            // result = "Alexa 도와줘";
                            // result = "시청연령제한 방법을 알려주세요";
                            if ( result != null && !result.isEmpty() ) {
                                try {
                                    // ==================== TEST ======================
    //                                cc.addUserChat(result);
    //                                String audioPath = gt.textToSpeechFile("audio",result);
    //
    //                                if ( !audioPath.startsWith("file:")) {
    //                                    audioPath = "file:" + audioPath ;
    //                                }
    //
    //                                AudioPlayerService.playWithPath(getApplicationContext(),audioPath);
    //
    //                                ChatObject co = new ChatObject();
    //                                co.setText(result);
    //                                cc.addSystemChat(co);
                                    // ==================== TEST ======================



                                    // TODO AI API
                                    cc.addUserChat(helper.userChatPatternRepace(result));

                                    new Thread(){
                                        @Override
                                        public void run() {
                                            try {
                                                parser = new AIApiParser(cc);
                                                response = aiApiService.request(parser.request(DEVICE_ID,helper.userChatPatternRepace(result))).execute();

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.d(TAG,"runOnUiThread");
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if ( !response.isSuccessful() ) {
                                                                    cc.addErrorMessage("Response Error");
                                                                    return ;
                                                                }

                                                                // Log.d(TAG,"Success " + text);
                                                                AIApiResponse aiApiResponse = response.body();
                                                                Log.d(TAG,"Response >  " + aiApiResponse.toString());

                                                                if ( aiApiResponse.isError() ) {
                                                                    Log.d(TAG,"response error");
                                                                    cc.addErrorMessage(aiApiResponse.getError().toString());
                                                                    return ;
                                                                }

                                                                if ( aiApiResponse.getAnswer() == null || aiApiResponse.getAnswer().getText() == null || aiApiResponse.getAnswer().getText().isEmpty() ){
                                                                    Log.d(TAG,"response text 에러");
                                                                    cc.addErrorMessage("response test 에러");
                                                                    return ;
                                                                }

                                                                Log.d(TAG,aiApiResponse.getAnswer().getText());

                                                                // User Chat add
                                                                // cc.addUserChat(helper.userChatPatternRepace(aiApiResponse.getQuery()));

                                                                List<ChatObject> list = parser.parseToChatObject(aiApiResponse);

                                                                if ( list != null ) {
                                                                    int idx = 0 ;
                                                                    for ( ChatObject co : list ) {
                                                                        // AI System Chat add
                                                                        cc.addSystemChat(co);

                                                                        // AI audio play
                                                                        String audioText = parser.getAllText();
                                                                        Log.d(TAG,"audioText "+audioText);
                                                                        if ( audioText != null && !audioText.isEmpty() && idx == 0 ) {
                                                                            String audioPath = gt.textToSpeechFile("audio",audioText);

                                                                            if ( !audioPath.startsWith("file:")) {
                                                                                audioPath = "file:" + audioPath ;
                                                                            }

                                                                            AudioPlayerService.playWithPath(getApplicationContext(),audioPath);
                                                                        }
                                                                        idx ++ ;
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                }).start();

                                            } catch(Exception e){
                                                e.printStackTrace();
                                            }

                                        }
                                    }.start();

                                }catch(Exception e){
                                    Log.d(TAG,"ERROR2");
                                    cc.addErrorMessage();
                                    e.printStackTrace();
                                }
                            } else {
                                // TODO 아무말 인식을 못했을떄 ~
                                Log.d(TAG,"GOOGLE STT 인식 불가");
                                cc.addErrorMessage("GOOGLE STT 인식 불가");
                            }
                        } catch(Exception e){
                            Log.d(TAG,"ERROR1");
                            cc.addErrorMessage();
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG,"file not exists");
                    }
                }
            };
        }
    }


    private static final int types[] = {
            AudioManager.STREAM_ALARM ,
            AudioManager.STREAM_DTMF ,
            AudioManager.STREAM_MUSIC ,
            AudioManager.STREAM_NOTIFICATION ,
            AudioManager.STREAM_RING ,
            AudioManager.STREAM_SYSTEM ,
            AudioManager.STREAM_VOICE_CALL
    } ;

    private static Map<Integer,Integer> soundMap = new HashMap<Integer,Integer>();

    private void clearSound(){
        AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        for ( int type : types ) {
            soundMap.put(type,mAudioManager.getStreamVolume(type));
            mAudioManager.setStreamVolume(type,0,AudioManager.FLAG_PLAY_SOUND);

        }

        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                (int)(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) * 1),
                AudioManager.FLAG_PLAY_SOUND);
    }

    private void setPrevSound(){
        AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        for ( Integer type : soundMap.keySet() ){
            mAudioManager.setStreamVolume(type,soundMap.get(type),AudioManager.FLAG_PLAY_SOUND);
        }
    }

//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy()");
        AudioPlayerService.stop();
        stopVideo();
        stopRecording();
        status = "D";

        PingService pingService = new PingService();

        Intent intent = new Intent(
                getApplicationContext(),//현재제어권자
                PingService.class); // 이동할 컴포넌트
        startService(intent); // 서비스 시작


        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d(TAG,"onStart()");
        status = "S";
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume()");
        status = "R";
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause()");
        status = "P";
        super.onPause();
    }

    public static void startRecording(){
        if ( rs == null && isRecording == false ) {
            Log.d(TAG,"START RECORDING");
            isRecording = true ;
            rs = new RecordService(PATH, "record.wav",rl);
            rs.start();
        } else {

        }
    }

    public static void stopRecording(){
        if ( rs != null && isRecording == true ) {
            Log.d(TAG,"STOP RECORDING");
            isRecording = false ;
            rs.stopRecording();
            rs = null ;
        } else {

        }
    }

    private static String video_url = "https://developers.google.com/training/images/tacoma_narrows.mp4";
    // private static String video_url2 = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    // private static String video_url3 = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_2mb.mp4";

    public static void playVideo(String url){
        // clearSound();
        AudioPlayerService.stop();


        Uri uri = Uri.parse(url);
        videoView.setVideoURI(uri);
        // MediaController ctlr = new MediaController(this);
        // ctlr.setMediaPlayer(videoView);
        // videoView.setMediaController(ctlr);
        videoView.requestFocus();
        videoView.setVisibility(View.VISIBLE);

        videoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        // Implementation here.
                        videoView.seekTo(0);
                        videoView.start();
                    }
                });

    }
    public static void stopVideo(){
        if ( videoView.isPlaying() ) {
            videoView.pause();
            videoView.setVisibility(View.INVISIBLE);
        }
    }

//    public static SurfaceView surfaceView ;
//
//    public static SurfaceHolder surfaceHolder;
//
//    public static MediaPlayer surfaceMediaPlayer ;

//    public static String surfaceUri ;
//    public static void playVideo2(String url){
//        AudioPlayerService.stop();
//
//        // surfaceUri = Uri.parse(url);
//        surfaceUri = url ;
//        surfaceHolder = surfaceView.getHolder();
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder surfaceHolder) {
//                if ( surfaceMediaPlayer != null ){
//                    surfaceMediaPlayer.stop();
//                    surfaceMediaPlayer = null ;
//                }
//                surfaceMediaPlayer = new MediaPlayer();
//                try {
//                    surfaceMediaPlayer.setDataSource(surfaceUri);
//                    surfaceMediaPlayer.setDisplay(surfaceHolder);
//                    surfaceMediaPlayer.prepare();
//
//                    surfaceMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            mediaPlayer.start();
//                        }
//                    });
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//
//            }
//        });
//    }
//
//    public static void stopVideo2(){
//        if ( surfaceMediaPlayer != null ){
//            surfaceMediaPlayer.stop();;
//            surfaceMediaPlayer = null ;
//        }
//    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG,"onKeyUp > " + keyCode);

        if ( keyCode == 21 ) {
            // startRecording();
            // playVideo(null);
        }

        if ( keyCode == 22 ) {
            // stopRecording();
            // stopVideo();
        }

        if ( keyCode == 89 ){
            // 좌
            startRecording();
        }

        if ( keyCode == 0 ) {
            // 유
            stopRecording();
        }

        return super.onKeyUp(keyCode, event);
    }

    public void initChat(){
        // ====================================
        cc.addUserChat(helper.userChatPatternRepace("알렉사 도와줘"));

        // ====================================
        ChatObject co1 = new ChatObject();
        co1.setSystemIconId(ChatObject.SYSTEM_ICON_CURIOUS_ID);
        co1.setText("무엇을 도와드릴까요?");
        cc.addSystemChat(co1);

        cc.addPlzSaySomething();

        ChatObject co2 = new ChatObject();
        co2.setBackground(ChatObject.SYSTEM_BACKGROUND_RADIUS_2);
        co2.setTitle("고객센터");
        co2.setText("고객님들이 많이 문의하는 질문이에요");
        co2.addChild(cc.getSystemTextWithYellow("1번'","리모컨 사용법, 고장 문의"));
        co2.addChild(cc.getSystemTextWithYellow("2번'","A/S 신청, 취소"));
        co2.addChild(cc.getSystemTextWithYellow("3번'","인터넷 연결 상태"));
        // co2.setButtons(cc.getButtonView("문의 더보기","종료"));
        cc.addSystemChat(co2);
    }

    // SAMPLE 1
    public void test(){

        // ====================================
        cc.addUserChat(helper.userChatPatternRepace("알렉사 도와줘"));

        // ====================================
        ChatObject co1 = new ChatObject();
        co1.setSystemIconId(ChatObject.SYSTEM_ICON_CURIOUS_ID);
        co1.setText("무엇을 도와드릴까요?");
        cc.addSystemChat(co1);

        cc.addPlzSaySomething();

        ChatObject co2 = new ChatObject();
        co2.setBackground(ChatObject.SYSTEM_BACKGROUND_RADIUS_2);
        co2.setTitle("고객센터");
        co2.setText("고객님들이 많이 문의하는 질문이에요");
        co2.addChild(cc.getSystemTextWithYellow("1번'","리모컨 사용법, 고장 문의"));
        co2.addChild(cc.getSystemTextWithYellow("2번'","A/S 신청, 취소"));
        co2.addChild(cc.getSystemTextWithYellow("3번'","인터넷 연결 상태"));
        // co2.setButtons(cc.getButtonView("문의 더보기","종료"));
        cc.addSystemChat(co2);

        // ====================================
        cc.addUserChat("1번");

        // ====================================
        ChatObject co3 = new ChatObject();
        co3.setTitle("리모컨 설명서");
        co3.setSystemIconId(ChatObject.SYSTEM_ICON_REPLY_ID);
        if ( cc.getClass().getName().equals("ChatControllerImpl")){
            co3.addChild(cc.getImageView(R.drawable.img_rcu,1142,545));
        }else {
            co3.addChild(cc.getImageView(R.drawable.img_rcu,761,363));
        }
        cc.addSystemChat(co3);

        // ====================================
        cc.addUserChat("Alexa 도와줘");

        // ====================================
        ChatObject co4 = new ChatObject();
        co4.setSystemIconId(ChatObject.SYSTEM_ICON_CURIOUS_ID);
        co4.setText("무엇을 도와드릴까요");
        cc.addSystemChat(co4);
        cc.addPlzSaySomething();

        // ====================================
        cc.addUserChat("시청 연령 제한 방법을 알려줘");

        // ====================================
        ChatObject co5 = new ChatObject();
        co5.setSystemIconId(ChatObject.SYSTEM_ICON_REPLY_ID);
        co5.setText("시청연령 제한입니다.");
        cc.addSystemChat(co5);

        ChatObject co6 = new ChatObject();
        co6.setBackground(ChatObject.SYSTEM_BACKGROUND_RADIUS_2);
        co6.setText("메뉴 > 설정 > 시청 연령 제한 메뉴에서 확인할 수 있어요");
        cc.addSystemChat(co6);

        ChatObject co7 = new ChatObject();
        co7.setBackground(ChatObject.SYSTEM_BACKGROUND_RADIUS_2);
        co7.setTitle("시청 연령 제한");
        co7.setText("자세한 내용이 궁금하다면, '영상 시청'을 선택하세요");
        if ( cc.getClass().getName().equals("ChatControllerImpl")) {
            co7.addChild(cc.getImageView(R.drawable.img_thumb,363,204));
        } else {
            co7.addChild(cc.getImageView(R.drawable.img_thumb,241,135));
        }
        // co7.setButtons(cc.getButtonView("영상시청","종료"));
        cc.addSystemChat(co7);

        ChatObject co8 = new ChatObject();
        co8.setBackground(ChatObject.SYSTEM_BACKGROUND_RADIUS_2);
        co8.setText("도움이 되었나요? 종료 할까요?");
        cc.addSystemChat(co8);

        cc.addPlzSaySomething();

        // ====================================
        cc.addUserChat("예");

        // ====================================
        cc.addUserChat("이어폰을 혼자 듣기 하려면 어떻게 하나요?");

        // ====================================
        ChatObject co9 = new ChatObject();
        co9.setSystemIconId(ChatObject.SYSTEM_ICON_REPLY_ID);
        co9.setText("보유하고 계신 휴대폰/PC용 일반 이어폰을 사용하시면 됩니다");
        cc.addSystemChat(co9);

        cc.addPlzSaySomething();

        // ====================================
        cc.addUserChat("혼자 듣기 할 떄 볼륨 조절은 어떻게 하나요?");

        // ====================================
        ChatObject co10 = new ChatObject();
        co10.setSystemIconId(ChatObject.SYSTEM_ICON_REPLY_ID);
        co10.setText(
                "혼자듣기 이용 시 음량은 리모컨의 전면부의 음량 +/- 키로 조절해주시기 바랍니다\n\n" +
                        "자체 볼륨 설정이 가능한 이어폰/헤드폰을 이용하신다면 자체 볼륨을 최대로 설정하여 이용하시기를 권유드립니다.");
        cc.addSystemChat(co10);

        cc.addPlzSaySomething();

        ChatObject co11 = new ChatObject();
        co11.setTitle("고객 센터");
        co11.setText("혼자 듣기 이용 안내");
        co11.addChild(cc.getSystemTextWithCheck("리모컨에 이어폰을 연결하면 연결음이 들리고, TV화면 왼쪽 상단에 [혼자 듣기]가 팝업된 후 실행"));
        co11.addChild(cc.getSystemTextWithCheck("혼자 듣기 실행 시 TV는 자동으로 음소거 모드 작동"));
        cc.addSystemChat(co11);

        // ====================================
        ChatObject co12 = new ChatObject();
        co12.setText("아무 버튼이나 눌러서 리모컨 상단에 불이 들어 오는지 지금 확인해 보세요");
        cc.addSystemChat(co12);

        cc.addPlzSaySomething();

        cc.addUserChat("불이 들어오네요");

        ChatObject co13 = new ChatObject();
        co13.setText("빨간 불인가요? 노란 불인가요?");
        cc.addSystemChat(co13);

        cc.addPlzSaySomething();

        cc.addUserChat("노란 불입니다");

        ChatObject co14 = new ChatObject();
        co14.setSystemIconId(ChatObject.SYSTEM_ICON_REPLY_ID);
        co14.setText("노란불이 켜지면 베터리교체 신호입니다 베터리를 교체 후 사용해 보세요");
        cc.addSystemChat(co14);

        cc.addPlzSaySomething();

        ChatObject co15 = new ChatObject();
        if ( cc.getClass().getName().equals("ChatControllerImpl")) {
            co15.addChild(cc.getImageView(R.drawable.img_remote_battery,442,248));
        } else {
            co15.addChild(cc.getImageView(R.drawable.img_remote_battery,294,165));
        }
        cc.addSystemChat(co15);

        cc.addUserChat("알겠습니다");

        // ====================================
        cc.addUserChat("평소와 달리 키가 느려요");

        // ====================================
        ChatObject co16 = new ChatObject();
        co16.setSystemIconId(ChatObject.SYSTEM_ICON_SORRY_ID);
        co16.setText("다음과 같은 상황에서 일시적으로 느릴 수 있습니다");
        cc.addSystemChat(co16);

        ChatObject co17 = new ChatObject();
        co17.setBackground(ChatObject.SYSTEM_BACKGROUND_RADIUS_2);
        co17.addChild(cc.getSystemTextWithCheck("셋톱박스 Upgrade를 하는경우"));
        co17.addChild(cc.getSystemTextWithCheck("일시적인 네트워크 양이 늘어나는 경우"));
        cc.addSystemChat(co17);

        ChatObject co18 = new ChatObject();
        co18.setBackground(ChatObject.SYSTEM_BACKGROUND_RADIUS_2);
        co18.setText("10분 정도 지난 후 계속 느릴경우 다음 번호로 연락 주시기 바랍니다.불편을 드려 죄송합니답 \n\nCall center : 02-000-0000");
        cc.addSystemChat(co18);

        // ====================================
        ChatObject co19 = new ChatObject();
        co19.setSystemIconId(ChatObject.SYSTEM_ICON_CLOSE_ID);
        co19.setText("이용해 주셔서 감사합니다");
        cc.addSystemChat(co19);

        //

    }
}
