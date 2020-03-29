package com.example.susicantix;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.susicantix.Model.User;
import com.example.susicantix.config.Config;
import com.example.susicantix.config.SharedPrefManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
  private Button btn_login;
  private EditText username_login, password_login;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    getSupportActionBar().hide();
    if (SharedPrefManager.getInstance(this).isLoggedIn()) {
      finish();
      startActivity(new Intent(this, ControlActivity.class));
    }
    checkConnection();

    btn_login = findViewById(R.id.loginButton);
    username_login = findViewById(R.id.username);
    password_login = findViewById(R.id.password);
    btn_login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        userLogin();
      }
    });
  }

  private void userLogin() {
    OkHttpClient httpClient = new OkHttpClient();
    final String username = username_login.getText().toString();
    final String password = password_login.getText().toString();

    if (TextUtils.isEmpty(username)) {
      username_login.setError("Masukkan username anda");
      username_login.requestFocus();
      return;
    }

    if (TextUtils.isEmpty(password)) {
      password_login.setError("Masukkan password anda");
      password_login.requestFocus();
      return;
    }

    final ProgressBar progressDialog = new ProgressBar(this);
    progressDialog.setVisibility(View.VISIBLE);

    RequestBody formBody = new FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build();

    Request request = new Request.Builder()
            .url(Config.url_server+"Api.php?apicall=login")
            .post(formBody)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();

    httpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        Toast.makeText(getApplicationContext(), "HTTP Request Failure", Toast.LENGTH_LONG).show();
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, final Response response) throws IOException {
        progressDialog.setVisibility(View.INVISIBLE);
        if (!response.isSuccessful()) {
          throw new IOException("Unexpected code " + response);
        }
        final String responseData = response.body().string();
        try {
          final JSONObject json = new JSONObject(responseData);
          if(json.getBoolean("error") == true){
            LoginActivity.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                try {
                  Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                  e.printStackTrace();
                }
              }
            });
          } else {
            finish();
            SharedPrefManager
                    .getInstance(getApplicationContext())
                    .userLogin(new User(
                            json.getJSONObject("user").getInt("id"),
                            json.getJSONObject("user").getString("username"),
                            json.getJSONObject("user").getString("key_blynk")));
            startActivity(new Intent(getApplicationContext(), ControlActivity.class));
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }

  protected boolean isOnline() {
    ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
      return true;
    } else {
      return false;
    }
  }

  public void checkConnection(){
    if(!isOnline()){
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage("Ponsel tidak terhubung internet")
              .setCancelable(false)
              .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  finish();
                }
              });
      AlertDialog alert = builder.create();
      alert.show();
    }
  }
}
