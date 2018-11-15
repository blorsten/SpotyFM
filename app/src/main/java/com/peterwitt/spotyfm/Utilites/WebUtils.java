package com.peterwitt.spotyfm.Utilites;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class WebUtils {
    public static void GetURL(String url, final WebResponse callback){
        //Initialize OKHTTP and build request
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        //Set callback to return the response as a string when done
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                callback.onWebResponseFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onWebResponse(response.body().string());
            }
        });
    }
}
