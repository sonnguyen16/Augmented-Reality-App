package com.google.ar.sceneform.samples.gltf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 3200;
    ImageView imgLogo;
    Animation topAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);

        imgLogo = findViewById(R.id.img_logo);

        imgLogo.setAnimation(topAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isLogin();
            }
        }, SPLASH_SCREEN);
    }

    /**
     * Hàm định nghĩa phương thức kiểm tra người dùng đã đăng nhập hay chưa
     * Nếu Đã đăng nhập thì sẽ chuển qua màn hình main
     * Nếu chưa đăng nhập sẽ chuyển qua màn hình login
     */
    private void isLogin() {
//         Lấy user hiện tại trên Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Intent intent = new Intent(SplashScreen.this, login.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(SplashScreen.this, home.class);

            startActivity(intent);
            finish();
        }
    }
}