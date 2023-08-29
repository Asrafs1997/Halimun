package com.unipro.labisha.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.unipro.labisha.R;
import com.unipro.labisha.utils.CustomItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User1 on 20/03/2017.
 */
public class TransferAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,String>> mData;
    private LayoutInflater mInflater;
    CustomItemClickListener listener;

    public TransferAdapter(Context applicationContext, CustomItemClickListener customItemClickListener) {
        mInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listener=customItemClickListener;
    }
    public TransferAdapter(Context applicationContext){
        mInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public ArrayList<HashMap<String,String>> getData()
    {
        return mData;
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

    public void setData(ArrayList<HashMap<String,String>> data)
    {
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

        if (row == null)
        {
            row = mInflater.inflate(R.layout.invoice_list_items, parent, false);
            LinearLayout linearlist=(LinearLayout)row.findViewById(R.id.linearlist);
//            if(position%2==0){
//                linearlist.setBackgroundColor(Color.parseColor("#80D8FF"));
//            }
            holder = new ViewHolder(row);
            row.setTag(holder);
        }else{
            holder = (ViewHolder) row.getTag();
        }
        if (mData != null && !mData.isEmpty()) {
            HashMap<String,String> rowData;
            rowData=mData.get(position);
            initValues(holder, rowData);
        }
       /* row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v,v.getP);
            }
        });*/
        return row;
    }
    public void initValues(ViewHolder holder, HashMap<String, String> rowData) {

        holder.txtTransferNo.setText(rowData.get("TransferNo"));
        holder.txtTransferDate.setText(rowData.get("TransferDate"));
        holder.txtFromLocation.setText(rowData.get("FromLocation"));
        holder.txtToLocation.setText(rowData.get("ToLocation"));
        holder.txtCreatedBy.setVisibility(View.GONE);
        holder.txtCreatedDate.setVisibility(View.GONE);
//        holder.txtCreatedBy.setText(rowData.get("CreateUser"));
//        holder.txtCreatedDate.setText(rowData.get("CreateDate"));
    }
    class ViewHolder {
        TextView txtTransferNo;
        TextView txtTransferDate;
        TextView txtFromLocation;
        TextView txtToLocation;
        TextView txtCreatedDate;
        TextView txtCreatedBy;

        public ViewHolder(View view) {
            txtTransferNo = (TextView) view.findViewById(R.id.txtInvNo);
            txtTransferDate=(TextView)view.findViewById(R.id.txtInvDate);
            txtFromLocation = (TextView) view.findViewById(R.id.txtCustName);
            txtToLocation=(TextView)view.findViewById(R.id.txtNetTotal);
            // txtCreatedDate=(TextView)view.findViewById(R.id.txtNetTotal);
            txtCreatedBy = (TextView) view.findViewById(R.id.txtCreatedUser);
            txtCreatedDate = (TextView) view.findViewById(R.id.txtCreatedOn);

        }
    }
}
