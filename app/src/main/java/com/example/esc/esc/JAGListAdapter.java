package com.example.esc.esc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dongbin on 2015-10-27.
 */

public class JAGListAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<JAGListData> mListData = new ArrayList<JAGListData>();

    public class ViewHolder {

        public TextView mTitle;
        public TextView mDong;
        public TextView mDate;
    }

   public JAGListAdapter(Context mContext, ArrayList<JAGListData> mListData){
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
            convertView = inflater.inflate(R.layout.listview_item_jag, null);

            holder.mTitle = (TextView) convertView.findViewById(R.id.item_title);
            holder.mDong = (TextView) convertView.findViewById(R.id.item_dong);
            holder.mDate = (TextView) convertView.findViewById(R.id.item_date);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JAGListData mData = mListData.get(position);

        holder.mTitle.setText(mData.mTitle);
        holder.mDong.setText(mData.mDong);
        holder.mDate.setText(mData.mDate);

        return convertView;

    }
}