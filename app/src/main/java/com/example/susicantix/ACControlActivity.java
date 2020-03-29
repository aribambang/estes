package com.example.susicantix;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

public class ACControlActivity extends AppCompatActivity {

  ImageView powerButton, tempUp, tempDown,fanSlow, fanMedium, fanHigh;
  TextView setTemp,setFan;

  SwipeRefreshLayout swipeRefreshLayoutAC;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ac_control);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    swipeRefreshLayoutAC =findViewById(R.id.swipeAC);

    powerButton= findViewById(R.id.powerButtonImageView);
    tempUp=findViewById(R.id.tempUpImageView);
    tempDown=findViewById(R.id.tempDownimageView);
    fanSlow=findViewById(R.id.lowFanImageView);
    fanMedium=findViewById(R.id.mediumFanImageView);
    fanHigh=findViewById(R.id.fanFastImageView);

    setTemp = (TextView) findViewById(R.id.setTemp);
    setFan = (TextView) findViewById(R.id.fanStatusTextView);

    swipeRefreshLayoutAC.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {

        getPowerButton();
        getTemperature();
        getFan();

        swipeRefreshLayoutAC.setRefreshing(false);
      }
    });

    powerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String onoff;
        if(powerButton.getTag()=="on"){
          onoff = "0";
        } else {
          onoff = "1";
        }
        smartAc("V7", onoff, R.id.powerButtonImageView);
      }
    });

    tempDown.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int a = Integer.parseInt(setTemp.getText().toString());
        int b;
        if (a <= 18) {
          b= 18;
        } else {
          b = a-1;
          smartAc("V8", Integer.toString(b), R.id.tempDownimageView);
          setTemp.setText((Integer.toString(b)));
        }
      }
    });

    tempUp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int a = Integer.parseInt(setTemp.getText().toString());
        int b;
        if (a >= 32) {
          b= 32;
        } else {
          b = a+1;
          smartAc("V8", Integer.toString(b), R.id.tempUpImageView);
          setTemp.setText((Integer.toString(b)));
        }
      }
    });

    getTemperature();
    getFan();
    getPowerButton();
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

  public void fanSlow (View v){
    setFan.setText("Slow");
    smartAc("V11", "1", 0);
  }

  public void fanMedium (View v){
    setFan.setText("Medium");
    smartAc("V11", "2", 0);
  }

  public void fanHigh (View v){
    setFan.setText("Fast");
    smartAc("V11", "3", 0);
  }

  public Object getTemperature(){
    OkHttpClient httpClient = new OkHttpClient();
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/get/V8";
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        ACControlActivity.this.runOnUiThread(new Runnable() {
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
        ACControlActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            try {
              final JSONArray json = new JSONArray(responseData);
              setTemp.setText(json.getString(0));
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

  public Object getFan(){
    OkHttpClient httpClient = new OkHttpClient();
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/get/V11";
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        ACControlActivity.this.runOnUiThread(new Runnable() {
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
        ACControlActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            try {
              final JSONArray json = new JSONArray(responseData);
              if(json.getString(0).equals("1")){
                setFan.setText("Slow");
              }
              if(json.getString(0).equals("2")){
                setFan.setText("Medium");
              }
              if(json.getString(0).equals("3")){
                setFan.setText("Fast");
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

  public Object smartAc (String v, String isi, int id){
    OkHttpClient httpClient = new OkHttpClient();
    final int rid = id;
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/update/"+v+"?value="+isi;

    if(rid == R.id.tempUpImageView){
      tempUp.setTag("on");
      tempUp.setImageResource(R.drawable.plusyellow);
    }
    else if(rid == R.id.tempDownimageView){
      tempDown.setTag("on");
      tempDown.setImageResource(R.drawable.minusyellow);
    }
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override

      public void onFailure(Call call, IOException e) {
        ACControlActivity.this.runOnUiThread(new Runnable() {
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
        ACControlActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if(rid == R.id.powerButtonImageView) {
              if (powerButton.getTag() == "on") {
                powerButton.setTag("off");
                powerButton.setImageResource(R.drawable.iconspboff);
              }else{
                powerButton.setTag("on");
                powerButton.setImageResource(R.drawable.iconspbon);
              }
            }
            else if(rid == R.id.tempUpImageView) {
              if (tempUp.getTag() == "on") {
                tempUp.setTag("off");
                tempUp.setImageResource(R.drawable.plus);
              }
            }
            else if(rid == R.id.tempDownimageView) {
              if (tempDown.getTag() == "on") {
                tempDown.setTag("off");
                tempDown.setImageResource(R.drawable.minus);
              }
            }
          }
        });
      }
    });
    return null;
  }
  public Object getPowerButton(){
    OkHttpClient httpClient = new OkHttpClient();
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/get/V7";
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        ACControlActivity.this.runOnUiThread(new Runnable() {
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
                powerButton.setTag("on");
                powerButton.setImageResource(R.drawable.iconspbon);
              }
              if(json.getString(0).equals("0")){
                powerButton.setTag("off");
                powerButton.setImageResource(R.drawable.iconspboff);
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

