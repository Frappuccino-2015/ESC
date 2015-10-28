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

public class PDGListAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<PDGListData> mListData = new ArrayList<PDGListData>();

    public class ViewHolder {

        public TextView mTitle;
        public TextView mProgram;
        public TextView mDong;
        public TextView mDay;
        public TextView mTime;
        public TextView mPhone;
        public TextView mNumber;

    }


    public PDGListAdapter(Context mContext, ArrayList<PDGListData> mListData) {
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
            convertView = inflater.inflate(R.layout.listview_item_pdg, null);

            holder.mTitle = (TextView) convertView.findViewById(R.id.item_title);
            holder.mProgram = (TextView) convertView.findViewById(R.id.item_program);
            holder.mDong = (TextView) convertView.findViewById(R.id.item_dong);
            holder.mDay = (TextView) convertView.findViewById(R.id.item_day);
            holder.mTime = (TextView) convertView.findViewById(R.id.item_time);
            holder.mPhone = (TextView) convertView.findViewById(R.id.item_phone);
            holder.mNumber = (TextView) convertView.findViewById(R.id.item_number);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PDGListData mData = mListData.get(position);

        holder.mTitle.setText(mData.mTitle);
        holder.mProgram.setText(mData.mProgram);
        holder.mDong.setText(mData.mDong);
        holder.mDay.setText(mData.mDay);
        holder.mTime.setText(mData.mTime);
        holder.mPhone.setText(mData.mPhone);
        holder.mNumber.setText(mData.mNumber);

        return convertView;

    }
}