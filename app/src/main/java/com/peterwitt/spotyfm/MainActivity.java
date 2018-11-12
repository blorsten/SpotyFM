package com.peterwitt.spotyfm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.peterwitt.spotyfm.Utilites.WebResponse;
import com.peterwitt.spotyfm.Utilites.WebUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements WebResponse {
    TextView txtString;
    public String url= "https://www.dr.dk/playlister/feeds/nowNext/nowPrev.drxml?items=4&cid=P3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtString= (TextView)findViewById(R.id.MainText);
        WebUtils.GetURL(url, this);
    }

    @Override
    public void onWebResponse(final String response) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtString.setText(response);
            }
        });
    }

    @Override
    public void onWebResponseFailue(String reason) {
        Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG);
    }
}
