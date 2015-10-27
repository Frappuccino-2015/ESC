package com.example.esc.esc;

/**
 * Created by dongbin on 2015-10-27.
 */
public class ListData {
    public String mType;
    public String mTitle;
    public String mUrl;
    public String mWriter;
    public String mDate;


    public ListData()  {
    }

    public ListData(String mType,String mTitle,String mUrl,String mWriter,String mDate)  { //데이터를 받는 클래스 메서드
        this.mType = mType;
        this.mTitle = mTitle;
        this.mUrl = mUrl;
        this.mWriter = mWriter;
        this.mDate = mDate;

    }
}
