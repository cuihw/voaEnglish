package com.example.zztest;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChannelListViewAdapter extends BaseAdapter {

    ArrayList<HashMap<String, Object>> mListItem;
    Context mContext;
    private LayoutInflater mInflater;

    protected static final String TAG = "ChannelListViewAdapter";

    public ChannelListViewAdapter (Context context, ArrayList<HashMap<String, Object>> listItem) {
        mListItem = listItem;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mListItem != null) {
            return mListItem.size();            
        }
        return 0;
    }

    @Override
    public Object getItem(int arg0) {

        if (mListItem != null) {
            return mListItem.get(arg0);            
        }

        return null;
    }

    @Override
    public long getItemId(int arg0) {

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder holder;
        Log.d("MyListViewBase", "getView " + position + " " + convertView);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_list_item, null);
            holder = new ViewHolder();

            holder.title = (TextView) convertView.findViewById(R.id.title_text);
            holder.subChannle = (TextView) convertView.findViewById(R.id.subchannle_text);
            holder.fanyi = (TextView) convertView.findViewById(R.id.fanyi_text);
            holder.zimu = (TextView) convertView.findViewById(R.id.zimu_text);
            holder.download_text = (TextView) convertView.findViewById(R.id.download_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        Log.d(TAG, "index = " + position);
        
        if (mListItem.get(position).get(Constant.title) == null) {

            Log.d(TAG, "index = " + position);
        }

        holder.title.setText(mListItem.get(position).get(Constant.title).toString());

        if (mListItem.get(position).get(Constant.subChannle) != null) {
            holder.subChannle.setVisibility(View.VISIBLE);
            holder.subChannle.setText(mListItem.get(position).get(Constant.subChannle).toString());
        }else {
            holder.subChannle.setVisibility(View.INVISIBLE);
        }

        if (mListItem.get(position).get("fanyi") != null) {
            holder.fanyi.setVisibility(View.VISIBLE);
            holder.fanyi.setText(mListItem.get(position).get("fanyi").toString());
        } else {
            holder.fanyi.setVisibility(View.INVISIBLE);
        }

        if (mListItem.get(position).get("zimu") != null) {
            holder.zimu.setVisibility(View.VISIBLE);
            holder.zimu.setText(mListItem.get(position).get("zimu").toString());
        } else {
            holder.zimu.setVisibility(View.INVISIBLE);
        }
        
        if (mListItem.get(position).get(Constant.already_downloaded) != null) {
            holder.download_text.setVisibility(View.VISIBLE);
            holder.zimu.setText(mListItem.get(position).get(Constant.already_downloaded).toString());
        } else {
            holder.download_text.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public final class ViewHolder{
        public TextView title;
        public TextView subChannle;
        public TextView fanyi;
        public TextView zimu;
        public TextView download_text;
        
    }
}
