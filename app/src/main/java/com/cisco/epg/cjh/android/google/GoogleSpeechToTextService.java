package com.cisco.epg.cjh.android.google;

import android.util.Log;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GoogleSpeechToTextService {
    private static final String TAG = "GoogleSpeechToTextService";

    private SpeechClient SPEECH_CLIENT = null ;

    private String GOOGLE_AUTH_JSON_PATH = null ;

    private SpeechSettings SPEECH_SETTINGS = null ;

    private RecognitionConfig RECOGNITION_CONFIG = null ;

    private final String LANGUAGE = "ko-KR"; // en-US

    public GoogleSpeechToTextService(InputStream is){
        auth(is);
    }

    private void auth(InputStream is){
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(is);
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

            SPEECH_SETTINGS = SpeechSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .build();

            RECOGNITION_CONFIG = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
//			              .setSampleRateHertz(16000) // 확인좀 필요할 듯
                    .setLanguageCode(LANGUAGE)
                    .build();

            SPEECH_CLIENT = SpeechClient.create(SPEECH_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String pathToText(String filePath){
        Log.d(TAG,"Wav to Text File Parh = " + filePath);
        String result = null ;

        try {
            Path path = Paths.get(filePath);
            byte[] data = Files.readAllBytes(path);
            result = byteToText(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result ;
    }

    public String fileToText(File file){
        String result = null ;
        try {
            InputStream is = new FileInputStream(file);
            result = streamToText(is);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result ;
    }

    public String streamToText(InputStream is){
        String result = null ;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int read ;
            byte[] data = new byte[1024];

            while ((read = is.read(data , 0 , data.length)) != -1 ) {
                buffer.write(data,0,read);
            }

            result = byteToText(buffer.toByteArray());
        } catch (Exception e){
            e.printStackTrace();
        }

        return result ;
    }

    /*
    keytool -list -v -alias androiddebugkey -keystore %USERPROFILE%\.android\debug.keystore

    *  MD5: 44:3E:E0:4C:C6:0B:55:FE:CA:50:2F:85:E5:E5:93:AD
         SHA1: 13:E6:A8:D0:6D:99:D4:20:DE:D6:D3:84:E3:6C:1E:DD:10:C4:D8:1F
         SHA256: FA:30:76:57:08:27:26:FB:01:ED:43:58:E2:42:23:CB:8D:E7:CD:EB:E5:67:E8:DC:F4:13:D8:EC:4A:DA:11:44
         서명 알고리즘 이름: SHA1withRSA
         버전: 1

    * */
    public String byteToText(byte[] data){
        String result = null ;
        try {
            ByteString audioBytes = ByteString.copyFrom(data);
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            RecognizeResponse response = SPEECH_CLIENT.recognize(RECOGNITION_CONFIG, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult r : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = r.getAlternativesList().get(0);
                Log.d(TAG,"Transcription = " + alternative.getTranscript());
                result = alternative.getTranscript() ;

                break ;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return result ;
    }
}
