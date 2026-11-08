package com.aditusstudios.bitacoralugares;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private ProgressBar pgbProgreso;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            pgbProgreso.incrementProgressBy(msg.arg1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pgbProgreso = findViewById(R.id.pgbProgreso);
        pgbProgreso.setProgress(0);

        if (mediaPlayer != null)
            mediaPlayer.release();

        mediaPlayer = MediaPlayer.create(this, R.raw.intro);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();

        Thread timer = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    for (int i = 0; i < 10; ++i) {
                        sleep(600);
                        Message msg = Message.obtain();
                        msg.arg1 = 10;
                        handler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent abreVentanaPrincipal = new Intent();
                    abreVentanaPrincipal.setClassName("com.aditusstudios.bitacoralugares",
                            "com.aditusstudios.bitacoralugares.MainActivity");
                    startActivity(abreVentanaPrincipal);
                    finish();
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.release();
    }
}