package com.example.esc.esc;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.StartTag;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private final int PALDAL_GU=0, JANGAN_GU=1;
    private final int MAX_PAGE = 5;

    private ArrayList<CenterInfo> centterInfoList;
    private static String CENTER_NAME ="PDG";

    private static String URL_PRIMARY,URL_NOTICE,CLASS_NAME;

    private String url;
    private java.net.URL URL;

    private net.htmlparser.jericho.Source source;
    private ProgressDialog progressDialog;
    private PDGListAdapter mPDGAdapter = null;
    private JAGListAdapter mJAGAdapter = null;
    private MenuKeywordListAdapter keywordListAdapter = null;
    private ListView listView;
    private int tableLocate;

    private ConnectivityManager cManager;
    private NetworkInfo mobile;
    private NetworkInfo wifi;

    private ArrayList<PDGListData> mPDGListData = new ArrayList<>();
    private ArrayList<JAGListData> mJAGListData = new ArrayList<>();

    private Button btnPDG, btnJAG, btnCredit;
    private TextView txtGu,txtPage;
    private String searchedWord = new String();

    private ArrayList<String> keywordList = new ArrayList<>();
    private ListView listViewKeyord;

    private SearchManager searchManager;
    private SearchView mSearchView;

    private Toolbar toolbar;
    private DrawerLayout dlDrawer;
    private ActionBarDrawerToggle dtToggle;
    private LinearLayout menuLayout,mainLayout;
    private RelativeLayout searchingKeywordLayout;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private TextView txtMenuAge,txtMenuSex,txtMenuKeyword;
    private LinearLayout menuProfileLayout;

    private Boolean searchingKeyword = true;
    private ImageView imageSearchingKewordCancel;

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
        menuLayout = (LinearLayout)findViewById(R.id.drawer);
        mainLayout = (LinearLayout)findViewById(R.id.main_layout);
        setSupportActionBar(toolbar);

        dtToggle = new ActionBarDrawerToggle(this, dlDrawer,toolbar, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        centterInfoList = new ArrayList<CenterInfo>();
        setCenterList();

        listView = (ListView)findViewById(R.id.listView);
        mPDGAdapter = new PDGListAdapter(this,mPDGListData);
        mJAGAdapter = new JAGListAdapter(this,mJAGListData);
        keywordListAdapter = new MenuKeywordListAdapter(this,keywordList);


        btnPDG = (Button)findViewById(R.id.btn_menu_pdg);
        btnJAG = (Button)findViewById(R.id.btn_menu_jag);
        btnCredit = (Button)findViewById(R.id.btn_credit);
        btnPDG.setOnClickListener(this);
        btnJAG.setOnClickListener(this);
        btnCredit.setOnClickListener(this);

        txtGu = (TextView)findViewById(R.id.txt_gu);
        txtPage = (TextView)findViewById(R.id.txt_page);
        txtGu.setOnClickListener(this);

        txtMenuAge = (TextView)findViewById(R.id.txt_menu_age);
        txtMenuSex = (TextView)findViewById(R.id.txt_menu_sex);
        txtMenuKeyword = (TextView)findViewById(R.id.txt_menu_keyword);
        listViewKeyord = (ListView)findViewById(R.id.listview_menu_keyword);
        menuProfileLayout = (LinearLayout)findViewById(R.id.linearlayout_menu_profile);
        menuProfileLayout.setOnClickListener(this);
        txtMenuKeyword.setOnClickListener(this);

        searchingKeywordLayout = (RelativeLayout)findViewById(R.id.searching_keyword_layout);
        imageSearchingKewordCancel = (ImageView)findViewById(R.id.searching_keyword_cancel);
        imageSearchingKewordCancel.setOnClickListener(this);

        prefs = this.getSharedPreferences("myPrefsESC", Context.MODE_PRIVATE);
        editor = prefs.edit();

        txtMenuAge.setText(prefs.getString("age", "00"));
        if(prefs.getString("sex","man").equals("man")){
            txtMenuSex.setText("남자");
        }else if(prefs.getString("sex","man").equals("woman")){
            txtMenuSex.setText("여자");
        }

        Set<String> temp = new HashSet<>();
        temp.add("EMPTY");

        Set<String> list =  prefs.getStringSet("keywordList", temp);
        for(String keyword : list){
            keywordList.add(keyword);
        }

//        mainLayout.removeView(searchingKeywordLayout);

        if(isInternetCon()) {
            Toast.makeText(MainActivity.this, "인터넷에 연결되지않아 불러오기를 중단합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            try {
                setList(); //네트워크 관련은 따로 쓰레드를 생성해야 UI 쓰레드와 겹치지 않는다. 그러므로 Thread 가 선언된 setList 메서드를 호출한다.
            } catch (Exception e) {
                Log.d("CONTTECTERROR", e + "");

            }
        }

        listViewKeyord.setAdapter(keywordListAdapter);




        searchedWord = "";
        txtPage.setText(MAX_PAGE + " Page");

        if(CENTER_NAME == "PDG"){
            listView.setAdapter(mPDGAdapter);
        }else if(CENTER_NAME == "JAG"){
            listView.setAdapter(mJAGAdapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if(dlDrawer.isDrawerOpen(menuLayout)){
                    return;
                }

                String URL_VIEW = null;
                if(CENTER_NAME == "PDG"){
                    PDGListData mData = mPDGListData.get(position);
                    URL_VIEW = mData.mUrl;
                }else if(CENTER_NAME == "JAG"){
                    JAGListData mData = mJAGListData.get(position);
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
        mPDGListData.clear();
        mJAGListData.clear();

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

//                        Element PHONE = (Element) TR.getAllElements(HTMLElementName.TD).get(4);
//                        String phone = PHONE.getContent().toString();
                        String phone = "";

//                        Element NUMBER = (Element) TR.getAllElements(HTMLElementName.TD).get(5);
//                        String number = NUMBER.getContent().toString();
                        String number = "";

                        Element DONG = (Element) TR.getAllElements(HTMLElementName.TD).get(6);
                        String dong = DONG.getContent().toString();

                        Element INFO = (Element) TR.getAllElements(HTMLElementName.TD).get(7);
                        Element A = (Element) INFO.getAllElements(HTMLElementName.A).get(0);
                        String url = A.getAttributeValue("href");

                        ArrayList<String> dataList = new ArrayList<String>();
                        dataList.add(title);
                        dataList.add(program);
                        dataList.add(dong);
                        dataList.add(day);
                        dataList.add(time);

                        if(!checkingList(dataList)){
                          continue;
                        }
                        mPDGListData.add(new PDGListData(title, program, day, time, phone, number, dong, url));

                    } catch (Exception e) {
                        Log.d("HTMLERROR", e + "");
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

//                        Element FILE = (Element) TR.getAllElements(HTMLElementName.TD).get(1);
//                        Element FILE_A = (Element) FILE.getAllElements(HTMLElementName.A).get(0);
//                        String fileUrl = FILE_A.getAttributeValue("href");
//                        Element FILE_A_IMG = (Element) FILE_A.getAllElements(HTMLElementName.IMG).get(0);
//                        String fileImgUrl = FILE_A_IMG.getAttributeValue("src");
                        String fileUrl = "";
                        String fileImgUrl = "";

                        Element DONG = (Element) TR.getAllElements(HTMLElementName.TD).get(2);
                        String dong = DONG.getContent().toString();

                        Element DATE = (Element) TR.getAllElements(HTMLElementName.TD).get(3);
                        String date = DATE.getContent().toString();

                        ArrayList<String> dataList = new ArrayList<String>();
                        dataList.add(title);
                        dataList.add(dong);
                        dataList.add(date);

                        if(!checkingList(dataList)){
                            continue;
                        }
                        mJAGListData.add(new JAGListData(title, titleUrl,fileUrl,fileImgUrl, dong, date));

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

    private boolean checkingList(ArrayList<String> dataList){

        ArrayList<String> mKeywordList = new ArrayList<>();
        if(searchingKeyword){
            mKeywordList = keywordList;
            if(searchData(dataList, mKeywordList)) {
                // 통과
            }else{
                return false;
            }
        }

        ArrayList<String> serachList = new ArrayList<>();
        if(searchedWord.equals("")){
            //통과
        }else{
            serachList.add(searchedWord);
            if(searchData(dataList,serachList)) {
                //통과
            }else{
                return false;
            }
        }
        return true;
    }

    private boolean searchData(ArrayList<String> dataList, ArrayList<String> wordList ){

        for(String data : dataList){
            Log.d("Main","dataList :"+ data);
            for(String word : wordList){
                Log.d("Main","wordList :"+ word);
                if(data.contains(word)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        if(v==btnPDG){
            if(CENTER_NAME == "JAG"){
                try {
                    CENTER_NAME = "PDG";
                    txtGu.setText("팔달구");
                    listView.setAdapter(mPDGAdapter);
                    searchedWord = "";
                    setList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else if(v==btnJAG){
            if(CENTER_NAME == "PDG"){
                try {
                    CENTER_NAME = "JAG";
                    txtGu.setText("장안구");
                    listView.setAdapter(mJAGAdapter);
                    searchedWord = "";
                    setList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(v==btnCredit){
            Intent intent = new Intent(MainActivity.this, CreditActivity.class);
            startActivity(intent);
        }else if(v==txtGu){
            if(dlDrawer.isDrawerOpen(menuLayout)){
                return;
            }
            if(CENTER_NAME == "PDG"){
                try {

                    setList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(CENTER_NAME == "JAG"){
                try {
                    searchedWord = "";
                    setList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(v==menuProfileLayout){
            Intent intent = new Intent(MainActivity.this, JoinActivity.class);
            startActivity(intent);
            finish();
        }else if(v==txtMenuKeyword) {
            if(searchingKeyword){
            }else{
                searchingKeyword = true;
                mainLayout.removeView(listView);
                mainLayout.addView(searchingKeywordLayout);
                mainLayout.addView(listView);
                try {
                    setList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if(v==imageSearchingKewordCancel){
            searchingKeyword = false;
            mainLayout.removeView(searchingKeywordLayout);
            try {
                setList();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        dlDrawer.closeDrawer(menuLayout);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchItem);

        return true;
    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(this);
    }

    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public boolean onQueryTextSubmit(String query) {

        searchedWord = query;
        try {
            setList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean onClose() {
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }


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
