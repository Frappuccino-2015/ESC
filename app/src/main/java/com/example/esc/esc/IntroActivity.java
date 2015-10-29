package com.example.esc.esc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by HyeJi on 2015-10-11.
 */
public class IntroActivity extends MainActivity{
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);

    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      public void run() {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
        finish();
      }
    }, 2000);
  }
}
