package com.example.susicantix.Model;

public class User {
  private int id;
  private String username, key_blynk;

  public User(int id, String username, String key_blynk) {
    this.id = id;
    this.username = username;
    this.key_blynk = key_blynk;
  }

  public int getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getKey_blynk() { return  key_blynk; }
}
