package com.unipro.labisha.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unipro.labisha.R;
import com.unipro.labisha.utils.CustomButtonClick;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User1 on 27/06/2018.
 */
public class DBTransferListAdapter extends BaseAdapter {
    private ArrayList<HashMap<String, String>> mData;
    private LayoutInflater mInflater;
    CustomButtonClick btnlistener;
    public DBTransferListAdapter(Context applicationContext) {
        mInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public DBTransferListAdapter(Context appContext, CustomButtonClick btnlistener){
        mInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.btnlistener=btnlistener;
    }

    @Override
    public int getCount() {
        if (mData != null && !mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mData != null && !mData.isEmpty()) {
            return mData.get(position);
        }
        return null;
    }

    public void setData(ArrayList<HashMap<String, String>> data) {
        this.mData = data;
        if (data != null) {
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            row = mInflater.inflate(R.layout.dbtransfer_list_items, parent, false);
            LinearLayout linearlist = (LinearLayout) row.findViewById(R.id.linDbItem);
            if (position % 2 == 0) {
                linearlist.setBackgroundColor(Color.parseColor("#80D8FF"));
            }
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        if (mData != null && !mData.isEmpty()) {
            HashMap<String, String> rowData;
            rowData = mData.get(position);
            initValues(holder, rowData);
        }
        return row;
    }

    public void initValues(final ViewHolder holder, final HashMap<String, String> rowData) {
        holder.txtDetails.setText(rowData.get("Details"));
        holder.txtLastSync.setText(rowData.get("LastSync"));
        holder.btnSync.setText(rowData.get("Process"));
        holder.btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnlistener.onSynClick(holder.txtDetails, rowData, "Sync");
            }
        });
    }

    class ViewHolder {
        TextView txtDetails;
        TextView txtLastSync;
        Button btnSync;

        public ViewHolder(View view) {
            txtDetails = (TextView) view.findViewById(R.id.txtDetails);
            txtLastSync = (TextView) view.findViewById(R.id.txtLastSync);
            btnSync=(Button)view.findViewById(R.id.btnSync);
        }
    }
}
