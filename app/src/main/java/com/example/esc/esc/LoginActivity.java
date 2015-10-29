package com.example.esc.esc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class LoginActivity extends Activity {
    //change
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_login = (Button)findViewById(R.id.btn_login);
        RadioButton option1 = (RadioButton)findViewById(R.id.option1);
        RadioButton option2 = (RadioButton)findViewById(R.id.option2);
        final EditText EText_id = (EditText)findViewById(R.id.EText_id);
        final EditText EText_pwd = (EditText)findViewById(R.id.EText_pwd);

        btn_login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (EText_id.getText().toString().equals("") || EText_pwd.getText().toString().equals("")){
                    Toast.makeText(
                            LoginActivity.this,
                            "아이디 혹은 비밀번호를 입력해주세요.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}