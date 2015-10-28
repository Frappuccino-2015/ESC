package com.example.esc.esc;

/**
 * Created by dongbin on 2015-10-29.
 */
public class CenterInfo {

    public String name;
    public String primaryUrl;
    public String noticeUrl[] = new String[3];
    public String className;
    public int listPage;

    public CenterInfo(String name, String primaryUrl, String noticeUrl, String className){
        this.name = name;
        this.primaryUrl = primaryUrl;
        this.noticeUrl[0] = noticeUrl;
        this.className = className;

        listPage = 1;
    }
}
