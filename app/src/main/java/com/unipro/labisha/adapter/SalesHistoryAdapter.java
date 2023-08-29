package com.unipro.labisha.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unipro.labisha.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User1 on 10/08/2018.
 */
public class SalesHistoryAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,String>> mData;
    private LayoutInflater mInflater;

    public SalesHistoryAdapter(Context applicationContext) {
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
            row = mInflater.inflate(R.layout.sales_history_items, parent, false);
            LinearLayout linearlist=(LinearLayout)row.findViewById(R.id.linearlist);
            /*if(position%2==0){
                linearlist.setBackgroundColor(Color.parseColor("#80D8FF"));
            }*/
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else
        {
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
        try {
            String InvoiceNo = rowData.get("InvoiceNo");
            String InvoiceDate = rowData.get("InvoiceDate");
            Double NetPrice = Double.parseDouble(rowData.get("NetPrice"));
            holder.txtInvoiceNo.setText(InvoiceNo);//rowData.get("InventoryCode")
            holder.txtInvoiceDate.setText(InvoiceDate);//rowData.get("Description")
            holder.txtNetPrice.setText(String.format("%.2f", NetPrice));

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    class ViewHolder {
        TextView txtInvoiceNo;
        TextView txtInvoiceDate;
        TextView txtNetPrice;

        public ViewHolder(View view) {
            txtInvoiceNo = (TextView) view.findViewById(R.id.txtInvoiceNo);
            txtInvoiceDate=(TextView)view.findViewById(R.id.txtInvoiceDate);
            txtNetPrice = (TextView) view.findViewById(R.id.txtNetPrice);
        }
    }
}
