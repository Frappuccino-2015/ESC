package com.example.esc.esc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dongbin on 2015-10-30.
 */

public class KeywordListAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<String> mListData = new ArrayList<String>();

    public class ViewHolder {

        public TextView mTitle;

    }


    public KeywordListAdapter(Context mContext, ArrayList<String> mListData) {
        this.mContext = mContext;
        this.mListData = mListData;
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
            convertView = inflater.inflate(R.layout.listview_item_keyword, null);

            holder.mTitle = (TextView) convertView.findViewById(R.id.item_keyword);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTitle.setText(mListData.get(position));

        return convertView;

    }
}