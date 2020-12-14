package com.cisco.epg.cjh.android.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class ChatImageDownloadFileTask extends AsyncTask<String,Void, Bitmap> {
    private ImageView target = null ;

    public ChatImageDownloadFileTask(){}

    public ChatImageDownloadFileTask(ImageView target){
        this.target = target ;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bmp = null;
        try {
            String img_url = strings[0]; //url of the image
            URL url = new URL(img_url);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        // doInBackground 에서 받아온 total 값 사용 장소
        if ( target != null ) {
            target.setImageBitmap(result);
        }
    }
}