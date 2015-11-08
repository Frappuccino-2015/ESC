package com.example.esc.esc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class JoinActivity extends AppCompatActivity {

    private EditText editAge,editKeyword;
    private ListView listview;
    private RadioGroup radiogroup;
    private RadioButton radioMan, radioWoman;
    private Button btnAddKeyword,btnNext;
    private ArrayList<String> keywordList;
    private KeywordListAdapter keywordListAdapter = null;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        editAge = (EditText)findViewById(R.id.edit_age);
        editKeyword = (EditText)findViewById(R.id.edit_keyword);
        radiogroup = (RadioGroup)findViewById(R.id.radio_group_sex);
        radioMan = (RadioButton)findViewById(R.id.radio_man);
        radioWoman = (RadioButton)findViewById(R.id.radio_woman);
        listview = (ListView)findViewById(R.id.listview_keyword);
        btnAddKeyword = (Button)findViewById(R.id.btn_add_keyword);
        btnNext = (Button)findViewById(R.id.btn_next);

        keywordList = new ArrayList<String>();
        keywordListAdapter = new KeywordListAdapter(this,keywordList);

        listview.setAdapter(keywordListAdapter);

        prefs = this.getSharedPreferences("myPrefsESC", Context.MODE_PRIVATE);
        editor = prefs.edit();

        radioMan.setChecked(true);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                keywordList.remove(position);
                keywordListAdapter.notifyDataSetChanged();
            }
        });

        btnAddKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                keywordList.add(editKeyword.getText().toString());

                keywordListAdapter.notifyDataSetChanged();
                editKeyword.setText("");
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editAge.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "나이를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (keywordList.size()==0) {
                    Toast.makeText(JoinActivity.this, "키워드를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                //data save
                editor.putString("age", editAge.getText().toString());
                if (radioMan.isChecked()) {
                    editor.putString("sex","man");
                }else if(radioWoman.isChecked()){
                    editor.putString("sex", "woman");
                }
                Set<String> stringSet = new HashSet<>();
                stringSet.addAll(keywordList);
                editor.putStringSet("keywordList", stringSet);
                editor.putBoolean("save",true);
                editor.commit();

                Intent intent = new Intent(JoinActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }
}
