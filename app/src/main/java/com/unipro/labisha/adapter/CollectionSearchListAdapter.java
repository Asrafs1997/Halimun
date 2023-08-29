package com.unipro.labisha.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unipro.labisha.R;

import java.util.ArrayList;
import java.util.HashMap;


public class CollectionSearchListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,String>> mData;
    private LayoutInflater mInflater;
//    CustomItemClickListener listener;

    /*    public PurchaseListAdapter(Context applicationContext, CustomItemClickListener customItemClickListener) {
            mInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.listener=customItemClickListener;
        }*/
    public CollectionSearchListAdapter(Context applicationContext){
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
            row = mInflater.inflate(R.layout.purchase_list_items, parent, false);
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
        return row;
    }
    public void initValues(ViewHolder holder, HashMap<String, String> rowData) {
     //   String InvDate=rowData.get("PoDate").substring(0,10);
        String entrynum=rowData.get("ENTRYNO");
        String entrydate=rowData.get("ENTRYDATE");
        String custcode=rowData.get("CUSTOMERCODE");
        String totalamount=rowData.get("TOTALAMOUNT");

        holder.txtPoNo.setText(entrynum );
        holder.txtDate.setText(entrydate);
        holder.txtSupplier.setText(custcode);
        holder.txtAmount.setText(totalamount);
    }
    class ViewHolder {
        TextView txtPoNo;
        TextView txtDate;
        TextView txtSupplier;
        TextView txtAmount;

        public ViewHolder(View view) {
            txtPoNo = (TextView) view.findViewById(R.id.txtPONo);
            txtDate=(TextView)view.findViewById(R.id.txtDate);
            txtSupplier = (TextView) view.findViewById(R.id.txtSupplier);
            txtAmount = (TextView) view.findViewById(R.id.txtAmount);
        }
    }
}
