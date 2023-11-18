package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    ImageView avatar_imgview;
    TextView temp_txtview;
    Handler ui_Handler = new Handler();
    Thread get_temp_Thread;
    WebView webview;

    @Override
    protected void onStart() {
      //  get_temp_Thread.start();
        WebSettings webSettings = webview.getSettings();
        webSettings.setSupportMultipleWindows(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setUseWideViewPort(true);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        webview.loadUrl("https://thingsboard.uitprojects.com/dashboard/68a24050-82fb-11ee-a39a-35af10aeb1a4?publicId=d53676f0-839b-11ee-a8a5-8f2584240217");
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        avatar_imgview=findViewById(R.id.imgView_avatar);
        String img_String = getIntent().getStringExtra("image_profile");
        byte[] img_bytes = Base64.getDecoder().decode(img_String);
        Bitmap img_Bitmap = BitmapFactory.decodeByteArray(img_bytes,0,img_bytes.length);
        avatar_imgview.setImageBitmap(img_Bitmap);
        temp_txtview = findViewById(R.id.txtview_temp);
        webview = findViewById(R.id.webview);














        get_temp_Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        CustomRequest get_temp_request = new CustomRequest(
                                "https://server.uitprojects.com/current_temp",
                                "GET",
                                null,
                                null
                        );
                        Map<String,String> response = get_temp_request.sendRequest();
                        ui_Handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String temp = "Temp: ".concat(Objects.requireNonNull(response.get("temp")));
                                temp_txtview.setText(temp);
                            }
                        });
                        Thread.sleep(1000);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            }



        });



    }
}