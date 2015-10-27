package com.example.esc.esc;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static String URL_PRIMARY = "http://paldal.suwon.go.kr";
    private static String GETNOTICE = "/menufiles/residents/di_list.asp?menuid=sub050202";
    private String url;
    private java.net.URL URL;

    private net.htmlparser.jericho.Source source;
    private ProgressDialog progressDialog;
    private BBSListAdapter BBSAdapter = null;
    private ListView BBSList;
    private int BBSlocate;

    private ConnectivityManager cManager;
    private NetworkInfo mobile;
    private NetworkInfo wifi;
    Toolbar toolbar;
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;
    ArrayList<ListData> mListData = new ArrayList<>();

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

        setSupportActionBar(toolbar);

        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);


        BBSList = (ListView)findViewById(R.id.listView);
        BBSAdapter = new BBSListAdapter(this);
        BBSList.setAdapter(BBSAdapter);
        BBSList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                        ListData mData = mListData.get(position);
                        String URL_BCS = mData.mUrl;

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_PRIMARY + URL_BCS)));

                    }
                });


        url = URL_PRIMARY + GETNOTICE;

        if(isInternetCon()) {
            Toast.makeText(MainActivity.this, "인터넷에 연결되지않아 불러오기를 중단합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            try {
                process();
                BBSAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.d("ERROR", e + "");

            }
        }


    }

    private void process() throws IOException {

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

                try {
                    URL = new URL(url);
                    InputStream html = URL.openStream();
                    source = new net.htmlparser.jericho.Source(new InputStreamReader(html, "EUC-KR"));
                    source.fullSequentialParse();
                } catch (Exception e) {
                    Log.d("ERROR", e + "");
                }

                List<StartTag> tabletags = source.getAllStartTags(HTMLElementName.TABLE);

                for(int arrnum = 0;arrnum < tabletags.size(); arrnum++){


                    if(tabletags.get(arrnum).toString().equals("<table class=\"tableLayout05\">")) {
                        BBSlocate = arrnum;
                        Log.d("tableLayout05", arrnum+"");
                        break;
                    }
                }



                Element BBS_TABLE = (Element) source.getAllElements(HTMLElementName.TABLE).get(BBSlocate);
                Element BBS_TBODY = (Element) BBS_TABLE.getAllElements(HTMLElementName.TBODY).get(0);


                for(int C_TR = 0; C_TR < BBS_TBODY.getAllElements(HTMLElementName.TR).size();C_TR++){

                    try {
                        Element BBS_TR = (Element) BBS_TBODY.getAllElements(HTMLElementName.TR).get(C_TR);

                        Element BC_GUBUN = (Element)BBS_TR.getAllElements(HTMLElementName.TD).get(0);
                        Element BC_PROGRAM = (Element)BBS_TR.getAllElements(HTMLElementName.TD).get(1);
                        Element BC_DAY = (Element)BBS_TR.getAllElements(HTMLElementName.TD).get(2);
                        Element BC_TIME = (Element)BBS_TR.getAllElements(HTMLElementName.TD).get(3);
                        Element BC_PHONE = (Element)BBS_TR.getAllElements(HTMLElementName.TD).get(4);
                        Element BC_NUMBER = (Element)BBS_TR.getAllElements(HTMLElementName.TD).get(5);
                        Element BC_DONG = (Element)BBS_TR.getAllElements(HTMLElementName.TD).get(6);
                        Element BC_info = (Element)BBS_TR.getAllElements(HTMLElementName.TD).get(7);
                        Element BC_a = (Element) BC_info.getAllElements(HTMLElementName.A).get(0);
                        String BCS_url = BC_a.getAttributeValue("href");


                        String BCS_GUBUN = BC_GUBUN.getContent().toString();

                        String BCS_PROGRAM = BC_PROGRAM.getContent().toString();
                        String BCS_DAY = BC_DAY.getContent().toString();
                        String BCS_TIME = BC_TIME.getContent().toString();
                        String BCS_PHONE = BC_PHONE.getContent().toString();
                        String BCS_NUMBER = BC_NUMBER.getContent().toString();
                        String BCS_DONG = BC_DONG.getContent().toString();



                        mListData.add(new ListData(BCS_GUBUN, BCS_PROGRAM, BCS_DAY, BCS_TIME,BCS_PHONE,BCS_NUMBER,BCS_DONG,BCS_url));


                    }catch(Exception e){
                        Log.d("BCSERROR",e+"");
                    }
                }
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BBSAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }, 0);
            }

        }.start();

    }

    private boolean isInternetCon() {
        cManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return !mobile.isConnected() && !wifi.isConnected();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    public class ViewHolder {

        public TextView mGubun;
        public TextView mProgram;
        public TextView mUrl;
    }

    public class BBSListAdapter extends BaseAdapter {
        private Context mContext = null;

        public BBSListAdapter(Context mContext) {
            this.mContext = mContext;
        }


        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.itemstyle, null);

                holder.mGubun = (TextView) convertView.findViewById(R.id.item_title);
                holder.mProgram = (TextView) convertView.findViewById(R.id.item_writer);
                holder.mUrl = (TextView) convertView.findViewById(R.id.item_date);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData mData = mListData.get(position);


            holder.mGubun.setText(mData.mGubun);
            holder.mProgram.setText(mData.mProgram);
            holder.mUrl.setText(mData.mUrl);

            return convertView;

        }


    }

}
