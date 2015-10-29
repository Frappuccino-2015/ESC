package com.example.esc.esc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by HyeJi on 2015-10-11.
 */
public class IntroActivity extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);

    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      public void run() {
        //회원가입 안 되어 있으면 회원가입창
        //회원가입 되어 있으면 밑으로
        Intent intent = new Intent(IntroActivity.this, JoinActivity.class);
        startActivity(intent);
        // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
        finish();
      }
    }, 2000);
  }
}
