package com.example.esc.esc;

/**
 * Created by dongbin on 2015-10-27.
 */
public class PDGListData {

    public String mTitle;
    public String mProgram;
    public String mDay;
    public String mTime;
    public String mPhone;
    public String mNumber;
    public String mDong;
    public String mUrl;


    public PDGListData()  {
    }

    public PDGListData(String mTitle, String mProgram, String mDay, String mTime, String mPhone, String mNumber, String mDong, String mUrl)  {

        this.mTitle = mTitle;
        this.mProgram = mProgram;
        this.mDay = mDay;
        this.mTime = mTime;
        this.mPhone = mPhone;
        this.mNumber = mNumber;
        this.mDong = mDong;
        this.mUrl = mUrl;
        cleaned();
    }
    public void cleaned(){
        mTitle = mTitle.replace("&nbsp;", "");
        mProgram = mProgram.replace("&nbsp;","");
        mDay = mDay.replace("&nbsp;","");
        mTime = mTime.replace("&nbsp;","");
        mPhone = mPhone.replace("&nbsp;","");
        mNumber = mNumber.replace("&nbsp;","");
        mDong = mDong.replace("&nbsp;","");
        mUrl = mUrl.replace("&nbsp;","");
    }
}
