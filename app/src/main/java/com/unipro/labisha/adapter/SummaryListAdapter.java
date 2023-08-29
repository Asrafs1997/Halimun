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

/**
 * Created by kalaivanan on 21/03/2018.
 */
public class SummaryListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,String>> mData;
    private ArrayList<HashMap<String,String>> mDataOriginal;
    private LayoutInflater mInflater;
//    CustomItemClickListener listener;

    public SummaryListAdapter(Context applicationContext){
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

    public void setDataForOriginal(ArrayList<HashMap<String,String>> Originaldata)
    {
        this.mDataOriginal = Originaldata;
        if (mDataOriginal != null) {
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
            row = mInflater.inflate(R.layout.list_item_summary, parent, false);
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

        String sCtnQty=rowData.get("sCtnQty");
        String sFocQty=rowData.get("sFocQty");
        String sLQty=rowData.get("sLQty");
        String sSumQty="";
        Double dCtnQty=0.0,dLQty=0.0,dFocQty=0.0;
        dCtnQty=Double.parseDouble(sCtnQty);
        dLQty=Double.parseDouble(sLQty);
        dFocQty=Double.parseDouble(sFocQty);
        if(dCtnQty>0){
            sSumQty+=sCtnQty +" Ctn,";
        }
        if(dLQty>0){
            sSumQty+=sLQty+" Pcs,";
        }
        if(dFocQty>0){
            sSumQty+=sFocQty+" Pcs";
        }
        sSumQty+=" at $"+rowData.get("sPrice")+"/Pc";

        holder.txtDescription.setText(rowData.get("sDescription") );
        holder.txtPrice.setText("$ "+rowData.get("sTotalAmnt"));
        holder.txtQty.setText(sSumQty);
    }
    class ViewHolder {
        TextView txtDescription;
        TextView txtPrice;
        TextView txtQty;

        public ViewHolder(View view) {
            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
            txtPrice=(TextView)view.findViewById(R.id.txtAmnt);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
        }
    }
}
