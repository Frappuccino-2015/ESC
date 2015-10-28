package com.example.esc.esc;

/**
 * Created by dongbin on 2015-10-27.
 */
public class JAGListData {

    public String mTitle;
    public String mTitleUrl;
    public String mFileUrl;
    public String mFileImgUrl;
    public String mDong;
    public String mDate;


    public JAGListData()  {
    }

    public JAGListData(String mTitle, String mTitleUrl, String mFileUrl, String mFileImgUrl, String mDong, String mDate)  {

        this.mTitle = mTitle;
        this.mTitleUrl = mTitleUrl;
        this.mFileUrl = mFileUrl;
        this.mFileImgUrl = mFileImgUrl;
        this.mDong = mDong;
        this.mDate = mDate;
        cleaned();
    }
    public void cleaned(){
        mTitle = mTitle.replace("&nbsp;", "");
        mTitleUrl = mTitleUrl.replace("&nbsp;","");
        mFileUrl = mFileUrl.replace("&nbsp;","");
        mFileImgUrl = mFileImgUrl.replace("&nbsp;","");
        mDong = mDong.replace("&nbsp;","");
        mDate = mDate.replace("&nbsp;","");
    }
}
