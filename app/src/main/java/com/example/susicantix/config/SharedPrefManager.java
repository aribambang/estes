package com.example.susicantix.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.susicantix.Model.User;

public class SharedPrefManager {

  private static SharedPrefManager mInstance;
  private static Context mCtx;

  private static final String SHARED_PREF_NAME = "susicantix";
  private static final String KEY_USER_ID = "keyuserid";
  private static final String KEY_USERNAME = "keyusername";
  private static final String KEY_BLYNK = "keyblynk";

  private SharedPrefManager(Context context) {
    mCtx = context;
  }

  public static synchronized SharedPrefManager getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new SharedPrefManager(context);
    }
    return mInstance;
  }

  public void userLogin(User user) {
    SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(KEY_USER_ID, user.getId());
    editor.putString(KEY_USERNAME, user.getUsername());
    editor.putString(KEY_BLYNK, user.getKey_blynk());

    editor.apply();
  }

  public boolean isLoggedIn() {
    SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    if (sharedPreferences.getString(KEY_USERNAME, null) != null)
      return true;
    return false;
  }

  public User getUser() {
    SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    return new User(
            sharedPreferences.getInt(KEY_USER_ID, 0),
            sharedPreferences.getString(KEY_USERNAME, null),
            sharedPreferences.getString(KEY_BLYNK, null)
    );
  }

  public boolean logout() {
    SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.clear();
    editor.apply();
    return true;
  }
}
