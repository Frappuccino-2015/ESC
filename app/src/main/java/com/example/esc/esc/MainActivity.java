package com.example.esc.esc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.StartTag;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private final int PALDAL_GU=0, JANGAN_GU=1;
    private final int MAX_PAGE = 5;

    private ArrayList<CenterInfo> centterInfoList;
    private static String CENTER_NAME ="PDG";

    private static String URL_PRIMARY,URL_NOTICE,CLASS_NAME;

    public String mPrimaryUrl;
    public String mListUrl1;
    public String mListPage;
    public String mListUrl2;

    private String url;
    private java.net.URL URL;

    private net.htmlparser.jericho.Source source;
    private ProgressDialog progressDialog;
    private PDGListAdapter mPDGAdapter = null;
    private JAGListAdapter mJAGAdapter = null;
    private ListView listView;
    private int tableLocate;

    private ConnectivityManager cManager;
    private NetworkInfo mobile;
    private NetworkInfo wifi;

    private ArrayList<PDGListData> mPDGListData = new ArrayList<>();
    private ArrayList<JAGListData> mJAGListData = new ArrayList<>();
    private Button btnPDG, btnJAG, btnCredit;
    private Boolean listFocused = true;

    Toolbar toolbar;
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;
    LinearLayout linearLayout;



    @Override
    protected void onStop() {
        super.onStop();
        if ( progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        linearLayout = (LinearLayout)findViewById(R.id.drawer);

        setSupportActionBar(toolbar);

        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);


        centterInfoList = new ArrayList<CenterInfo>();
        setCenterList();

        listView = (ListView)findViewById(R.id.listView);
        mPDGAdapter = new PDGListAdapter(this,mPDGListData);
        mJAGAdapter = new JAGListAdapter(this,mJAGListData);

        btnPDG = (Button)findViewById(R.id.btn_menu_pdg);
        btnJAG = (Button)findViewById(R.id.btn_menu_jag);
        btnCredit = (Button)findViewById(R.id.btn_credit);
        btnPDG.setOnClickListener(this);
        btnJAG.setOnClickListener(this);
        btnCredit.setOnClickListener(this);


        if(isInternetCon()) {
            Toast.makeText(MainActivity.this, "인터넷에 연결되지않아 불러오기를 중단합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            try {
                setList(); //네트워크 관련은 따로 쓰레드를 생성해야 UI 쓰레드와 겹치지 않는다. 그러므로 Thread 가 선언된 setList 메서드를 호출한다.
            } catch (Exception e) {
                Log.d("ERROR", e + "");

            }
        }

        if(CENTER_NAME == "PDG"){
            listView.setAdapter(mPDGAdapter);
        }else if(CENTER_NAME == "JAG"){
            listView.setAdapter(mJAGAdapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if(dlDrawer.isDrawerOpen(linearLayout)){
                    return;
                }

                String URL_VIEW = null;
                if(CENTER_NAME == "PDG"){
                    PDGListData mData = mPDGListData.get(position);
                    Toast.makeText(MainActivity.this, mData.mProgram, Toast.LENGTH_SHORT).show();
                    URL_VIEW = mData.mUrl;
                }else if(CENTER_NAME == "JAG"){
                    JAGListData mData = mJAGListData.get(position);
                    Toast.makeText(MainActivity.this, mData.mTitle, Toast.LENGTH_SHORT).show();
                    URL_VIEW = mData.mTitleUrl;
                }else{
                    return;
                }

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_PRIMARY + URL_VIEW)));


            }
        });
    }

    private void setCenterList(){

        centterInfoList.add(PALDAL_GU, new CenterInfo(
                "PALDAL_GU",
                "http://paldal.suwon.go.kr/menufiles/residents/",
                "di_list.asp?menuid=sub050202",
                "tableLayout05"));

        centterInfoList.get(PALDAL_GU).noticeUrl[1] = "di_list.asp?code=residents&block=1&page=";
        centterInfoList.get(PALDAL_GU).noticeUrl[2] = "&mnuflag=&tag=&bd_com8=&bd_div=1&bd_com9=&bd_com10=&menuid=sub050202&dong_gubn=";

        centterInfoList.add(JANGAN_GU, new CenterInfo(
                "JANGAN_GU",
                "http://jangan.suwon.go.kr/bbsplus/",
                "list.asp?code=tbl_bbs_sub07020601",
                "board_list"));

        centterInfoList.get(JANGAN_GU).noticeUrl[1] = "list.asp?code=tbl_bbs_sub07020601&block=1&page=";
        centterInfoList.get(JANGAN_GU).noticeUrl[2] = "&strSearch_text=&strSearch_div=&bd_gubn=all&mnuflag=&bd_gubn1=all";

        URL_PRIMARY = centterInfoList.get(0).primaryUrl;
        URL_NOTICE = centterInfoList.get(0).noticeUrl[0];
        CLASS_NAME = centterInfoList.get(0).className;
    }

    private boolean isInternetCon() {
        cManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return !mobile.isConnected() && !wifi.isConnected();
    }

    private void setList() throws IOException {

        new Thread() {

            @Override
            public void run() {

                Handler Progress = new Handler(Looper.getMainLooper());
                Progress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = ProgressDialog.show(MainActivity.this, "", "게시판 정보를 가져오는중 입니다.");
                    }
                }, 0);

                getCommentDB();

                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(CENTER_NAME == "PDG")
                            mPDGAdapter.notifyDataSetChanged();
                        else if(CENTER_NAME == "JAG")
                            mJAGAdapter.notifyDataSetChanged();
                        progressDialog.dismiss(); //모든 작업이 끝나면 다이어로그 종료
                    }
                }, 0);
            }
        }.start();

    }



    private void getCommentDB(){

        if(CENTER_NAME == "PDG"){

            URL_PRIMARY = centterInfoList.get(PALDAL_GU).primaryUrl;
            URL_NOTICE = centterInfoList.get(PALDAL_GU).noticeUrl[0];
            CLASS_NAME = centterInfoList.get(PALDAL_GU).className;

            for(int page=1; page<=MAX_PAGE; page++) {

                setURL(page);

                List<StartTag> tableTags = source.getAllStartTags(HTMLElementName.TABLE);

                for (int arrnum = 0; arrnum < tableTags.size(); arrnum++) {
                    if (tableTags.get(arrnum).toString().equals("<table class=\"" + CLASS_NAME + "\">")) {
                        tableLocate = arrnum;
                        break;
                    }
                }

                Element TABLE = (Element) source.getAllElements(HTMLElementName.TABLE).get(tableLocate);
                Element TBODY = (Element) TABLE.getAllElements(HTMLElementName.TBODY).get(0);

                for (int C_TR = 0; C_TR < TBODY.getAllElements(HTMLElementName.TR).size(); C_TR++) {

                    try {
                        Element TR = (Element) TBODY.getAllElements(HTMLElementName.TR).get(C_TR);

                        Element TITLE = (Element) TR.getAllElements(HTMLElementName.TD).get(0);
                        String title = TITLE.getContent().toString();

                        Element PROGRAME = (Element) TR.getAllElements(HTMLElementName.TD).get(1);
                        String program = PROGRAME.getContent().toString();

                        Element DAY = (Element) TR.getAllElements(HTMLElementName.TD).get(2);
                        String day = DAY.getContent().toString();

                        Element TIME = (Element) TR.getAllElements(HTMLElementName.TD).get(3);
                        String time = TIME.getContent().toString();

                        Element PHONE = (Element) TR.getAllElements(HTMLElementName.TD).get(4);
                        String phone = PHONE.getContent().toString();

                        Element NUMBER = (Element) TR.getAllElements(HTMLElementName.TD).get(5);
                        String number = NUMBER.getContent().toString();

                        Element DONG = (Element) TR.getAllElements(HTMLElementName.TD).get(6);
                        String dong = DONG.getContent().toString();

                        Element INFO = (Element) TR.getAllElements(HTMLElementName.TD).get(7);
                        Element A = (Element) INFO.getAllElements(HTMLElementName.A).get(0);
                        String url = A.getAttributeValue("href");

                        mPDGListData.add(new PDGListData(title, program, day, time, phone, number, dong, url));

                    } catch (Exception e) {
                        Log.d("BCSERROR", e + "");
                    }
                }
            }

        }else if(CENTER_NAME == "JAG") {
            URL_PRIMARY = centterInfoList.get(JANGAN_GU).primaryUrl;
            URL_NOTICE = centterInfoList.get(JANGAN_GU).noticeUrl[0];
            CLASS_NAME = centterInfoList.get(JANGAN_GU).className;

            for (int page = 1; page <= MAX_PAGE; page++) {

                setURL(page);

                List<StartTag> tableTags = source.getAllStartTags(HTMLElementName.TABLE);

                for (int arrnum = 0; arrnum < tableTags.size(); arrnum++) {
                    if (tableTags.get(arrnum).toString().equals("<table class=\"" + CLASS_NAME + "\">")) {
                        tableLocate = arrnum;
                        break;
                    }
                }

                Element TABLE = (Element) source.getAllElements(HTMLElementName.TABLE).get(tableLocate);
                Element TBODY = (Element) TABLE.getAllElements(HTMLElementName.TBODY).get(0);

                for (int C_TR = 0; C_TR < TBODY.getAllElements(HTMLElementName.TR).size(); C_TR++) {

                    try {
                        Element TR = (Element) TBODY.getAllElements(HTMLElementName.TR).get(C_TR);

                        Element TITLE = (Element) TR.getAllElements(HTMLElementName.TD).get(0);
                        Element TITLE_A = (Element) TITLE.getAllElements(HTMLElementName.A).get(0);
                        String title = TITLE_A.getContent().toString();
                        String titleUrl = TITLE_A.getAttributeValue("href");

                        Element FILE = (Element) TR.getAllElements(HTMLElementName.TD).get(1);
                        Element FILE_A = (Element) FILE.getAllElements(HTMLElementName.A).get(0);
                        String fileUrl = FILE_A.getAttributeValue("href");
                        Element FILE_A_IMG = (Element) FILE_A.getAllElements(HTMLElementName.IMG).get(0);
                        String fileImgUrl = FILE_A_IMG.getAttributeValue("src");

                        Element DONG = (Element) TR.getAllElements(HTMLElementName.TD).get(2);
                        String dong = DONG.getContent().toString();

                        Element DATE = (Element) TR.getAllElements(HTMLElementName.TD).get(3);
                        String date = DATE.getContent().toString();

                        mJAGListData.add(new JAGListData(title, titleUrl, fileUrl, fileImgUrl, dong, date));


                    } catch (Exception e) {
                        Log.d("BCSERROR", e + "");
                    }
                }
            }
        }

    }
    private void setURL(int page){

        if(CENTER_NAME == "PDG") {
            URL_NOTICE = centterInfoList.get(PALDAL_GU).noticeUrl[1]+page+centterInfoList.get(PALDAL_GU).noticeUrl[2];
        }else if(CENTER_NAME == "JAG"){
            URL_NOTICE = centterInfoList.get(JANGAN_GU).noticeUrl[1]+page+centterInfoList.get(JANGAN_GU).noticeUrl[2];
        }
        Log.d("Main",URL_NOTICE);
        url = URL_PRIMARY + URL_NOTICE;

        try {
            URL = new URL(url);
            InputStream html = URL.openStream();
            source = new net.htmlparser.jericho.Source(new InputStreamReader(html, "EUC-KR"));
            source.fullSequentialParse();
        } catch (Exception e) {
            Log.d("ERROR", e + "");
        }

    }

    @Override
    public void onClick(View v) {

        if(v==btnPDG){
            if(CENTER_NAME == "JAG"){
                try {
                    CENTER_NAME = "PDG";
                    listView.setAdapter(mPDGAdapter);
                    mPDGListData.clear();
                    setList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else if(v==btnJAG){
            if(CENTER_NAME == "PDG"){
                try {
                    CENTER_NAME = "JAG";
                    listView.setAdapter(mJAGAdapter);
                    mJAGListData.clear();
                    setList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(v==btnCredit){
            Intent intent = new Intent(MainActivity.this, CreditActivity.class);
            startActivity(intent);
        }
        dlDrawer.closeDrawer(linearLayout);


    }

// 우측 상단 메뉴 생성
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    // Sync the toggle state after onRestoreInstanceState has occurred.
        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dtToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
