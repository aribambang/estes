package com.example.susicantix;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
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

public class ControlActivity extends AppCompatActivity {
  ImageView ac, tv, iRtu, iRkd, iKtu, iKta, iplug, itoggleRkd, itoggleRtu, itoggleKta, itoggleKtu, itogglePlug;
  CardView ktu, kta, rkd, rtu, plug;
  SwipeRefreshLayout swipeRefreshLayout;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_appbar, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.mybutton) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setCancelable(false);
      builder.setMessage("Anda ingin Keluar?");
      builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          finish();
          SharedPrefManager.getInstance(getApplicationContext()).logout();
          startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
      });
      builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          dialog.cancel();
        }
      });
      AlertDialog alert = builder.create();
      alert.show();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_control);

    swipeRefreshLayout = findViewById(R.id.swipe);

    ac = findViewById(R.id.acImageView);
    tv = findViewById(R.id.tvImageView);
    ktu = findViewById(R.id.card_view_KTU); //v3
    kta = findViewById(R.id.card_view_KTA); //v5
    rkd = findViewById(R.id.card_view_RKD); //v1
    rtu = findViewById(R.id.card_view_RTU); //v0
    plug = findViewById(R.id.card_view_PLUG);

    iRkd = findViewById(R.id.rkdimageView);
    iRtu = findViewById(R.id.rtuImageView);
    iKta = findViewById(R.id.ktaImageView);
    iKtu = findViewById(R.id.ktuImageView);
    iplug = findViewById(R.id.plugImageView);

    itoggleRkd =findViewById(R.id.toggleRKDImageView);
    itoggleRtu = findViewById(R.id.toglleRTUImageView);
    itoggleKta = findViewById(R.id.toggleKTAImageView);
    itoggleKtu = findViewById(R.id.toggleKTUImageView);
    itogglePlug= findViewById(R.id.togglePlugImageView);

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getRTU();
        getRKD();
        getKTU();
        getKTA();
        getPLUG();
        swipeRefreshLayout.setRefreshing(false);
      }
    });

    rtu.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        String onoff;
        if(rtu.getTag()=="on"){
          onoff = "0";
        } else {
          onoff = "1";
        }
        smartControl("V0", onoff, R.id.card_view_RTU);
      }
    });

    rkd.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        String onoff;
        if(rkd.getTag()=="on"){
          onoff = "0";
        } else {
          onoff = "1";
        }
        smartControl("V1", onoff, R.id.card_view_RKD);
      }
    });

    kta.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        String onoff;
        if(kta.getTag()=="on"){
          onoff = "0";
        } else {
          onoff = "1";
        }
        smartControl("V5", onoff, R.id.card_view_KTA);
      }
    });

    ktu.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        String onoff;
        if(ktu.getTag()=="on"){
          onoff = "0";
        } else {
          onoff = "1";
        }
        smartControl("V3", onoff, R.id.card_view_KTU);
      }
    });

    plug.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        String onoff;
        if(plug.getTag()=="on"){
          onoff = "0";
        } else {
          onoff = "1";
        }
        smartControl("V9", onoff, R.id.card_view_PLUG);
      }
    });
    getRTU();
    getRKD();
    getKTU();
    getKTA();
    getPLUG();
  }

  public Object smartControl(String v, String isi, int id){
    OkHttpClient httpClient = new OkHttpClient();
    final int rid = id;
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/update/"+v+"?value="+isi;
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        ControlActivity.this.runOnUiThread(new Runnable() {
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
        ControlActivity.this.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if(rid == R.id.card_view_KTA){
              if(kta.getTag()=="on"){
                kta.setTag("off");
                iKta.setImageResource(R.drawable.off);
                itoggleKta.setImageResource(R.drawable.toggleblueoff);
              } else {
                kta.setTag("on");
                iKta.setImageResource(R.drawable.on);
                itoggleKta.setImageResource(R.drawable.toggleblueon);
              }
            } else if(rid == R.id.card_view_KTU){
              if(ktu.getTag()=="on"){
                ktu.setTag("off");
                iKtu.setImageResource(R.drawable.off);
                itoggleKtu.setImageResource(R.drawable.toggleblueoff);
              } else {
                ktu.setTag("on");
                iKtu.setImageResource(R.drawable.on);
                itoggleKtu.setImageResource(R.drawable.toggleblueon);
              }
            } else if(rid == R.id.card_view_RKD){
              if(rkd.getTag()=="on"){
                rkd.setTag("off");
                iRkd.setImageResource(R.drawable.off);
                itoggleRkd.setImageResource(R.drawable.toggleblueoff);
              } else {
                rkd.setTag("on");
                iRkd.setImageResource(R.drawable.on);
                itoggleRkd.setImageResource(R.drawable.toggleblueon);
              }
            } else if(rid == R.id.card_view_RTU) {
              if (rtu.getTag() == "on") {
                rtu.setTag("off");
                iRtu.setImageResource(R.drawable.off);
                itoggleRtu.setImageResource(R.drawable.toggleblueoff);
              } else {
                rtu.setTag("on");
                iRtu.setImageResource(R.drawable.on);
                itoggleRtu.setImageResource(R.drawable.toggleblueon);
              }
            } else if(rid == R.id.card_view_PLUG){
              if(plug.getTag()=="on"){
                plug.setTag("off");
                iplug.setImageResource(R.drawable.plugoff);
                itogglePlug.setImageResource(R.drawable.toggleblueoff);
              } else {
                plug.setTag("on");
                iplug.setImageResource(R.drawable.plugon);
                itogglePlug.setImageResource(R.drawable.toggleblueon);
              }
            }
          }
        });
      }
    });
    return null;
  }

  @Override
  public void onBackPressed() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setCancelable(false);
    builder.setMessage("Keluar dari aplikasi?");
    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        ControlActivity.this.finish();
      }
    });
    builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }

  public Object getRTU(){
    OkHttpClient httpClient = new OkHttpClient();
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/get/V0";
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        ControlActivity.this.runOnUiThread(new Runnable() {
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
               rtu.setTag("on");
                iRtu.setImageResource(R.drawable.on);
                itoggleRtu.setImageResource(R.drawable.toggleblueon);
              }
              if(json.getString(0).equals("0")){
                rtu.setTag("off");
                iRtu.setImageResource(R.drawable.off);
                itoggleRtu.setImageResource(R.drawable.toggleblueoff);
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

  public Object getRKD(){
    OkHttpClient httpClient = new OkHttpClient();
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/get/V1";
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        ControlActivity.this.runOnUiThread(new Runnable() {
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
                rkd.setTag("on");
                iRkd.setImageResource(R.drawable.on);
                itoggleRkd.setImageResource(R.drawable.toggleblueon);
              }
              if(json.getString(0).equals("0")){
                rkd.setTag("off");
                iRkd.setImageResource(R.drawable.off);
                itoggleRkd.setImageResource(R.drawable.toggleblueoff);
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

  public Object getKTU(){
    OkHttpClient httpClient = new OkHttpClient();
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/get/V3";
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        ControlActivity.this.runOnUiThread(new Runnable() {
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
                ktu.setTag("on");
                iKtu.setImageResource(R.drawable.on);
                itoggleKtu.setImageResource(R.drawable.toggleblueon);
              }
              if(json.getString(0).equals("0")){
                ktu.setTag("off");
                iKtu.setImageResource(R.drawable.off);
                itoggleKtu.setImageResource(R.drawable.toggleblueoff);
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

  public Object getKTA(){
    OkHttpClient httpClient = new OkHttpClient();
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/get/V5";
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        ControlActivity.this.runOnUiThread(new Runnable() {
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
                kta.setTag("on");
                iKta.setImageResource(R.drawable.on);
                itoggleKta.setImageResource(R.drawable.toggleblueon);
              }
              if(json.getString(0).equals("0")){
                kta.setTag("off");
                iKta.setImageResource(R.drawable.off);
                itoggleKta.setImageResource(R.drawable.toggleblueoff);
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

  public Object getPLUG(){
    OkHttpClient httpClient = new OkHttpClient();
    String url = Config.url+SharedPrefManager.getInstance(this).getUser().getKey_blynk()+"/get/V9";
    Request request = new Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();
    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        ControlActivity.this.runOnUiThread(new Runnable() {
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
                plug.setTag("on");
                iplug.setImageResource(R.drawable.plugon);
                itogglePlug.setImageResource(R.drawable.toggleblueon);
              }
              if(json.getString(0).equals("0")){
                plug.setTag("off");
                iplug.setImageResource(R.drawable.plugoff);
                itogglePlug.setImageResource(R.drawable.toggleblueoff);
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

  public void ACClick(View v){
    Intent mapIntent = new Intent(ControlActivity.this, ACControlActivity.class);
    mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(mapIntent);
  }

  public void TVClick(View v){
    Intent mapIntent = new Intent(ControlActivity.this, TVControlActivity.class);
    mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(mapIntent);
  }
}
