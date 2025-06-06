package com.example.f5acadmmy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.f5acadmmy.R;
import com.example.f5acadmmy.login;

public class Splash extends AppCompatActivity {

    Handler handler = new Handler(Looper.getMainLooper()); // atualizado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove título da Activity
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Coloca tela cheia
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Oculta a ActionBar (se existir)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Define o layout da tela splash
        setContentView(R.layout.activity_splash);

        // Espera 2 segundos e abre a MainActivity
        handler.postDelayed(() -> {
            Intent intent = new Intent(Splash.this, login.class);
            startActivity(intent);
            finish();
        }, 2000); // 2000ms = 2 segundos
    }
}
