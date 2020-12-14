package com.cisco.epg.cjh.android.player;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

public class AudioPlayerService {
    public AudioPlayerService(){

    }

    private static MediaPlayer mPlayer ;

    private static Context CONTEXT ;

    private static String AUDIO_PATH ;

    private static AudioManager mAudioManager ;

    private static AudioAttributes mAudioAttributes ;

    private static AudioFocusRequest mAudioFocustRequest;

    public static void stop(){
        if ( mPlayer != null ) {
            System.out.println("stop");
            mAudioManager.abandonAudioFocusRequest(mAudioFocustRequest);
            mPlayer.stop();
            mPlayer = null ;
        }
    }

    public static void playWithPath(Context context, String audioPath){
        CONTEXT = context ;
        AUDIO_PATH = audioPath;
//        AudioManager.STREAM_ALARM ,
//                AudioManager.STREAM_DTMF ,
//                AudioManager.STREAM_MUSIC ,
//                AudioManager.STREAM_NOTIFICATION ,
//                AudioManager.STREAM_RING ,
//                AudioManager.STREAM_SYSTEM ,
//                AudioManager.STREAM_VOICE_CALL
//
        try {
            stop();




            mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            mAudioAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
//                            .setUsage(AudioAttributes.USAGE_GAME)
//                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)

                            .build();

            mAudioFocustRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)//(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(mAudioAttributes)
                    // .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                        @Override
                        public void onAudioFocusChange(int i) {
                            switch(i){
                                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:

                                case AudioManager.AUDIOFOCUS_GAIN :
                                    if ( mPlayer == null || !mPlayer.isPlaying() ){
                                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(int)(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)),AudioManager.FLAG_PLAY_SOUND);
                                        try {
                                            System.out.println("get gain");
                                            Uri myUri = Uri.parse(AUDIO_PATH);
                                            mPlayer = new MediaPlayer();
                                            mPlayer.setDataSource(CONTEXT, myUri);
                                            mPlayer.prepare();
                                            mPlayer.start();


                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                    break;
                                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                    System.out.println("get AUDIOFOCUS_LOSS_TRANSIENT");
                                    stop();
                                    break ;
                                case AudioManager.AUDIOFOCUS_LOSS:
                                    System.out.println("get AUDIOFOCUS_LOSS");
                                    mAudioManager.abandonAudioFocusRequest(mAudioFocustRequest);
                                    stop();
                                    break ;
                            }
                        }
                    })
                    .build();

            int focusRequest = mAudioManager.requestAudioFocus(mAudioFocustRequest);

            switch (focusRequest) {
                case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    //
                    System.out.println("fail gain");
                    break ;
                case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                    System.out.println("get gain");
                    //
                    if ( mPlayer == null || !mPlayer.isPlaying() ){
                        Uri myUri = Uri.parse(audioPath);
                        mPlayer = new MediaPlayer();
                        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                stop();
                            }
                        });
                        mPlayer.setAudioAttributes(mAudioAttributes);
                        mPlayer.setDataSource(context, myUri);
                        mPlayer.prepare();
                        mPlayer.start();
                    }
                    break ;
                case AudioManager.AUDIOFOCUS_REQUEST_DELAYED:
                    System.out.println("gain AUDIOFOCUS_REQUEST_DELAYED");
                    break;
            }



        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
