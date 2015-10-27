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
import android.text.Html;
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

    private static String URL_PRIMARY = "http://www.dongbaek.hs.kr"; //Ȩ������ ���� �ּ��̴�.
    private static String GETNOTICE = "/main.php?menugrp=030100&master=bbs&act=list&master_sid=7"; //Ȩ������ �� �Խ����� ��Ÿ���� �� �ּ�, ����� �Խ��ǵ��� ���� �Ľ��� �����ϹǷ� �����Ͽ� �������.
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
    protected void onStop() { //���߾����� ���̾�α׸� �������ִ� �޼���
        super.onStop();
        if ( progressDialog != null)
            progressDialog.dismiss(); //���̾�αװ� ����������� (!null) ��������ش�
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


        BBSList = (ListView)findViewById(R.id.listView); //����Ʈ����
        BBSAdapter = new BBSListAdapter(this);
        BBSList.setAdapter(BBSAdapter); //����Ʈ�� ����͸� �Կ��ش�.
        BBSList.setOnItemClickListener( //����Ʈ Ŭ���� ����� ���� ����
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                        ListData mData = mListData.get(position); // Ŭ���� �������� �����͸� �����´�.
                        String URL_BCS = mData.mUrl; //������ ������ �� url �κи� �����س���.

                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_PRIMARY + URL_BCS))); //�����س� url �� �̿��� URL_PRIMARY �� ���̰�

                    }
                });


        url = URL_PRIMARY + GETNOTICE; //�Ľ��ϱ��� PRIMARY URL �� �������� URL �� ���� ������ URL �������.

        if(isInternetCon()) { //false ��ȯ�� if ������ ���� ����
            Toast.makeText(MainActivity.this, "���ͳݿ� ��������ʾ� �ҷ����⸦ �ߴ��մϴ�.", Toast.LENGTH_SHORT).show();
            finish();
        }else{ //���ͳ� üũ ����� ������ ����
            try {
                process(); //��Ʈ��ũ ������ ���� �����带 �����ؾ� UI ������� ��ġ�� �ʴ´�. �׷��Ƿ� Thread �� ����� process �޼��带 ȣ���Ѵ�.
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

                Handler Progress = new Handler(Looper.getMainLooper()); //��Ʈ��ũ ������� ������ ���� �ڵ鷯�� �̿��Ͽ� �����带 �����Ѵ�.
                Progress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = ProgressDialog.show(MainActivity.this, "", "�Խ��� ������ ���������� �Դϴ�.");
                    }
                }, 0);

                try {
                    URL = new URL(url);
                    InputStream html = URL.openStream();
                    source = new net.htmlparser.jericho.Source(new InputStreamReader(html, "utf-8")); //�ҽ��� UTF-8 ���ڵ����� �ҷ��´�.
                    source.fullSequentialParse(); //���������� �����м�
                } catch (Exception e) {
                    Log.d("ERROR", e + "");
                }

                List<StartTag> tabletags = source.getAllStartTags(HTMLElementName.DIV); // DIV Ÿ���� ��� �±׵��� �ҷ��´�.

                for(int arrnum = 0;arrnum < tabletags.size(); arrnum++){ //DIV ��� �±��� bbsContent �±װ� ���°���� ���Ѵ�.


                    if(tabletags.get(arrnum).toString().equals("<div class=\"bbsContent\">")) {
                        BBSlocate = arrnum; //DIV Ŭ������ bbsContent �� arrnum ���� BBSlocate �� ���°���� �����Ѵ�.
                        Log.d("BBSLOCATES", arrnum+""); //arrnum �α�
                        break;
                    }
                }



                Element BBS_DIV = (Element) source.getAllElements(HTMLElementName.DIV).get(BBSlocate); //BBSlocate ���ӹ�° �� DIV �� ��� �����´�.
                Element BBS_TABLE = (Element) BBS_DIV.getAllElements(HTMLElementName.TABLE).get(0); //���̺�
                Element BBS_TBODY = (Element) BBS_TABLE.getAllElements(HTMLElementName.TBODY).get(0); //�����Ͱ� �ִ� TBODY �� ����


                for(int C_TR = 0; C_TR < BBS_TBODY.getAllElements(HTMLElementName.TR).size();C_TR++){ //���⼭�� �������� �Խõ� �Խù� �����͸� �ҷ��� �Խ��� �������̽��� ������ ���̴�.


                    // �ҽ��� ȿ������ ���ؼ��� for ���� ����ϴ°��� ������ , ���ظ� �������� �ҽ��� �Ϻη� �÷� �ξ���.

                    try {
                        Element BBS_TR = (Element) BBS_TBODY.getAllElements(HTMLElementName.TR).get(C_TR); //TR ����

                        Element BC_TYPE = (Element) BBS_TR.getAllElements(HTMLElementName.TD).get(0); //Ÿ�� �� �ҷ��´�.

                        Element BC_info = (Element) BBS_TR.getAllElements(HTMLElementName.TD).get(2); //URL(herf) TITLE(title) �� ���� ������ �ҷ��´�.
                        Element BC_a = (Element) BC_info.getAllElements(HTMLElementName.A).get(0); //BC_info ���� a �±׸� �����´�.
                        String BCS_url = BC_a.getAttributeValue("href"); //a �±��� herf �� BCS_url �� ����
                        String BCS_title = BC_a.getAttributeValue("title"); //a �±��� title �� BCS_title �� ����

                        Element BC_writer = (Element) BBS_TR.getAllElements(HTMLElementName.TD).get(3); //�۾��̸� �ҷ��´�.
                        Element BC_date = (Element) BBS_TR.getAllElements(HTMLElementName.TD).get(4); // ��¥�� �ҷ��´�.

                        String BCS_type = BC_TYPE.getContent().toString(); // Ÿ�԰��� ���� ������Ʈ�� �������� ���ڿ��� ��ȯ���� �����´�.
                        String BCS_writer = BC_writer.getContent().toString(); // �ۼ��ڰ��� ���� ������Ʈ�� �������� ���ڿ��� ��ȯ���� �����´�.
                        String BCS_date = BC_date.getContent().toString(); // �ۼ����ڰ��� ���� ������Ʈ�� �������� ���ڿ��� ��ȯ���� �����´�.


                        mListData.add(new ListData(BCS_type, BCS_title, BCS_url, BCS_writer, BCS_date)); //�����Ͱ� ���̸� ������ ����Ʈ Ŭ������ �����͵��� ����Ѵ�.
                        /* Log.d("BCSARR","Ÿ��:"+BCS_type+"\n����:" +BCS_title +"\n�ּ�:"+BCS_url +"\n�۾���:" + BCS_writer + "\n��¥:" + BCS_date);*/



                    }catch(Exception e){
                        Log.d("BCSERROR",e+"");
                    }
                }
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BBSAdapter.notifyDataSetChanged(); //��� �۾��� ������ ����Ʈ ����
                        progressDialog.dismiss(); //��� �۾��� ������ ���̾�α� ����
                    }
                }, 0);
            }

        }.start();

    }

    private boolean isInternetCon() {
        cManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); //����� ������ ����
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //�������� ����
        return !mobile.isConnected() && !wifi.isConnected(); //������� ����
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

        public TextView mType;
        public TextView mTitle;
        public TextView mUrl;
        public TextView mWriter;
        public TextView mDate;
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

                holder.mTitle = (TextView) convertView.findViewById(R.id.item_title);
                holder.mWriter = (TextView) convertView.findViewById(R.id.item_writer);
                holder.mDate = (TextView) convertView.findViewById(R.id.item_date);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData mData = mListData.get(position);


            if(mData.mType.equals("����")){
                holder.mTitle.setText(Html.fromHtml("<font color=#616161>[����] </font>" +mData.mTitle)); //"����" �� ������ �κ������� �ణ ���ϰ� ����.
            }else{
                holder.mTitle.setText(mData.mTitle);
            }

            holder.mWriter.setText(mData.mWriter+" ������"); //�������� ����
            holder.mDate.setText(mData.mDate);

            return convertView;

        }


    }

}
