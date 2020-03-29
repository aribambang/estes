package com.example.susicantix;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.susicantix.config.Config;
import com.example.susicantix.config.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TVControlActivity extends AppCompatActivity {
  ImageView onOffButton, volumeUp, volumeDown, channelUp, channelDown;
  SwipeRefreshLayout swipeRefreshLayouttv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tv_control);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    swipeRefreshLayouttv=findViewById(R.id.swipeTV);

    onOffButton = findViewById(R.id.switchImageView);
    volumeUp = findViewById(R.id.volumeUpImageView);
    volumeDown = findViewById(R.id.volumeDownImageView);
    channelUp = findViewById(R.id.channelUpImageView);
    channelDown = findViewById(R.id.ChannelDownImageView);

    swipeRefreshLayouttv.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getOnOff();
        swipeRefreshLayouttv.setRefreshing(false);
      }
    });

    onOffButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String onoff;
        if(onOffButton.getTag()=="on"){
          onoff = "0";
        } else {
          onoff = "1";
        }
        smartTv("V12", onoff, R.id.switchImageView);
      }
    });

    volumeDown.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        smartTv("V16", "1",R.id.volumeDownImageView);
      }
    });

    volumeUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        smartTv("V15", "1", R.id.volumeUpImageView);
      }
    });

    channelUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        smartTv("V13", "1", R.id.channelUpImageView);
      }
    });

    channelDown.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        smartTv("V14", "1", R.id.ChannelDownImageView);
      }
    });

    getOnOff();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        super.onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public Object smartTv (String v, String isi, int id){
      OkHttpClient httpClient = new OkHttpClient();
      final int rid = id;
      String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/update/"+v+"?value="+isi;

    if(rid == R.id.volumeUpImageView){
      volumeUp.setTag("on");
      volumeUp.setImageResource(R.drawable.plusyellow);
    }
    else if(rid == R.id.volumeDownImageView){
      volumeDown.setTag("on");
      volumeDown.setImageResource(R.drawable.minusyellow);
    }
    else if(rid == R.id.channelUpImageView){
      channelUp.setTag("on");
      channelUp.setImageResource(R.drawable.upyellow);
    }
    else if(rid == R.id.ChannelDownImageView){
      channelDown.setTag("on");
      channelDown.setImageResource(R.drawable.upyellow);
    }
      Request request = new Request.Builder()
              .url(url)
              .build();
      httpClient.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          TVControlActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(getApplicationContext(), "HTTP Request Failure", Toast.LENGTH_LONG).show();
            }
          });
          e.printStackTrace();
        }
        @Override
        public void onResponse(Call call, final Response response) throws IOException {
          if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
          }
          final String responseData = response.body().string();
          TVControlActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if(rid == R.id.switchImageView) {
                if (onOffButton.getTag() == "on") {
                  onOffButton.setTag("off");
                  onOffButton.setImageResource(R.drawable.iconspboff);
                } else{
                    onOffButton.setTag("on");
                    onOffButton.setImageResource(R.drawable.iconspbon);
                }
              }
                else if(rid == R.id.volumeUpImageView) {
                  if (volumeUp.getTag() == "on") {
                    volumeUp.setTag("off");
                    volumeUp.setImageResource(R.drawable.plus);
                  }
                }
              else if(rid == R.id.volumeDownImageView) {
                if (volumeDown.getTag() == "on") {
                  volumeDown.setTag("off");
                  volumeDown.setImageResource(R.drawable.minus);
                }
              }
              else if(rid == R.id.channelUpImageView) {
                if (channelUp.getTag() == "on") {
                  channelUp.setTag("off");
                  channelUp.setImageResource(R.drawable.up);
                }
              }
              else if(rid == R.id.ChannelDownImageView) {
                if (channelDown.getTag() == "on") {
                  channelDown.setTag("off");
                  channelDown.setImageResource(R.drawable.up);
                }
              }
            }
          });
        }
      });
      return null;
    }

  public Object getOnOff(){
    OkHttpClient httpClient = new OkHttpClient();
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/get/V12";
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        TVControlActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(getApplicationContext(), "HTTP Request Failure", Toast.LENGTH_LONG).show();
          }
        });
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, final Response response) throws IOException {
        if (!response.isSuccessful()) {
          throw new IOException("Unexpected code " + response);
        }
        final String responseData = response.body().string();
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            try {
              final JSONArray json = new JSONArray(responseData);
              if(json.getString(0).equals("1")){
                onOffButton.setTag("on");
                onOffButton.setImageResource(R.drawable.iconspbon);
              }
              if(json.getString(0).equals("0")){
                onOffButton.setTag("off");
                onOffButton.setImageResource(R.drawable.iconspboff);
              }
              response.close();
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
      }
    });
    return null;
  }

}

