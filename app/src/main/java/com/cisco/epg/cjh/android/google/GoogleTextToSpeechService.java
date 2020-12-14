package com.cisco.epg.cjh.android.google;

import android.os.Environment;
import android.util.Log;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1beta1.AudioConfig;
import com.google.cloud.texttospeech.v1beta1.AudioEncoding;
import com.google.cloud.texttospeech.v1beta1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1beta1.SynthesisInput;
import com.google.cloud.texttospeech.v1beta1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1beta1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1beta1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1beta1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class GoogleTextToSpeechService {
    private final String TAG = this.getClass().getSimpleName();

    private TextToSpeechClient TEXT_TO_SPEECH_CLIENT = null ;

    private String GOOGLE_AUTH_JSON_PATH = null ;

    private TextToSpeechSettings TEXT_TO_SPEECH_SETTINGS = null ;

    //private RecognitionConfig RECOGNITION_CONFIG = null ;

    private final String LANGUAGE = "ko-KR"; // en-US

    public GoogleTextToSpeechService(InputStream is){
        auth(is);
    }

    private void auth(InputStream is){
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(is);
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

            TEXT_TO_SPEECH_SETTINGS = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .build();

            //RECOGNITION_CONFIG = RecognitionConfig.newBuilder()
            //       .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
//			              .setSampleRateHertz(16000) // 확인좀 필요할 듯
            // .setLanguageCode(LANGUAGE)
            // .build();

            TEXT_TO_SPEECH_CLIENT = TextToSpeechClient.create(TEXT_TO_SPEECH_SETTINGS);
            Log.d(TAG,"text to speech client init finish");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String textToSpeechFile(String fileName , String text){
        SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode("ko-KR")
                .setSsmlGender(SsmlVoiceGender.NEUTRAL).build();

        AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

        SynthesizeSpeechResponse response = TEXT_TO_SPEECH_CLIENT.synthesizeSpeech(input, voice, audioConfig);

        ByteString audioContents = response.getAudioContent();
        if ( !fileName.endsWith(".mp3")){
            fileName = fileName + ".mp3";
        }
        try {
            OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + fileName) ;
            out.write(audioContents.toByteArray());
            Log.d(TAG,"Audio content written to file : " + Environment.getExternalStorageDirectory() + "/" + fileName);
            out.close();

            return Environment.getExternalStorageDirectory() + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null ;
    }
}
