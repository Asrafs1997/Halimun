package com.unipro.labisha.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
 * Created by kalaivanan on 21/03/2018.
 */
public class CompareListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,String>> mData;
    private ArrayList<HashMap<String,String>> mDataOriginal;
    private LayoutInflater mInflater;
    private int type = 1;

    public CompareListAdapter(Context applicationContext,int rtype,ArrayList<HashMap<String,String>> data,ArrayList<HashMap<String,String>> Originaldata){
        mInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        type = rtype;
        this.mData = data;
        this.mDataOriginal = Originaldata;
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
            row = mInflater.inflate(R.layout.list_item_compare, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }else{
            holder = (ViewHolder) row.getTag();
        }

        if(type == 1) {
            if (mData != null && !mData.isEmpty() && mDataOriginal != null && !mDataOriginal.isEmpty()) {
                HashMap<String, String> rowData,rowOriginData;
                rowOriginData = new HashMap<>();
                rowData = mData.get(position);
                String   sInventory = rowData.get("sInventory").trim();
                for (int j = 0; j < mDataOriginal.size(); j++) {
                    if( sInventory.equalsIgnoreCase(mDataOriginal.get(j).get("sInventory").trim())){
                        rowOriginData = mDataOriginal.get(j);
                    }
                }
                initValuesExist(holder, rowData,rowOriginData);
            }
        }else{
            if (mData != null && !mData.isEmpty()) {
                HashMap<String, String> rowData;
                rowData = mData.get(position);
                initValuesExtra(holder, rowData);
            }
        }
        return row;
    }
    public void initValuesExist(ViewHolder holder, HashMap<String, String> rowData , HashMap<String, String> rowOriginData) {


        holder.linPoLqty.setVisibility(View.VISIBLE);
        holder.linPoWqty.setVisibility(View.VISIBLE);

        String sPoCtnQty=rowData.get("sPoCtnQty");
        String sPoFocQty=rowData.get("sPoFocQty");
        String sPoLQty=rowData.get("sPoLQty");
        String sCtnQty=rowData.get("sCtnQty");
        String sFocQty=rowData.get("sFocQty");
        String sLQty=rowData.get("sLQty");

        Double dCtnQty=0.0,dLQty=0.0,dFocQty=0.0;
        Double dPoCtnQty=0.0,dPoLQty=0.0,dPoFocQty=0.0;

        dPoCtnQty=Double.parseDouble(sPoCtnQty);
        dPoLQty=Double.parseDouble(sPoLQty);
        dPoFocQty=Double.parseDouble(sPoFocQty);

        dCtnQty=Double.parseDouble(sCtnQty);
        dLQty=Double.parseDouble(sLQty);
        dFocQty=Double.parseDouble(sFocQty);

        if(dPoCtnQty>0){
            holder.txtPoWQty.setText(sPoCtnQty);
        }else{
            holder.txtPoWQty.setText("0");
        }
        if(dPoLQty>0){
            holder.txtPoLQty.setText(sPoLQty);
        }else{
            holder.txtPoLQty.setText("0");
        }

        if(dCtnQty>0){
            holder.txtWQty.setText(sCtnQty);
        }else{
            holder.txtWQty.setText("0");
        }
        if(dLQty>0){
            holder.txtLQty.setText(sLQty);
        }else{
            holder.txtLQty.setText("0");
        }

        if(dCtnQty==0.0 && dLQty == 0.0){
            String text2 = rowData.get("sDescription").trim() + " - Item Missing !!";

            Spannable spannable = new SpannableString(text2);

            spannable.setSpan(new ForegroundColorSpan(Color.RED), rowData.get("sDescription").trim().length(), (rowData.get("sDescription").trim() + " - Item Missing !!").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.txtDescription.setText(spannable, TextView.BufferType.SPANNABLE);
        }else{
            holder.txtDescription.setText(rowData.get("sDescription") );
        }

        holder.txtPrice.setText("$ "+rowData.get("sTotalAmnt"));
        holder.txtUnitCost.setText("$ "+rowData.get("sPrice"));

    }

    public void initValuesExtra(ViewHolder holder, HashMap<String, String> rowData) {

        holder.txtDescription.setText(rowData.get("sDescription") );
        holder.txtPrice.setText("$ "+rowData.get("sTotalAmnt"));
        holder.txtUnitCost.setText("$ "+rowData.get("sPrice"));
        holder.linPoLqty.setVisibility(View.GONE);
        holder.linPoWqty.setVisibility(View.GONE);

        String sCtnQty=rowData.get("sCtnQty");
        String sFocQty=rowData.get("sFocQty");
        String sLQty=rowData.get("sLQty");


        String sSumQty="";
        Double dCtnQty=0.0,dLQty=0.0,dFocQty=0.0;
        dCtnQty=Double.parseDouble(sCtnQty);
        dLQty=Double.parseDouble(sLQty);
        dFocQty=Double.parseDouble(sFocQty);

        if(dCtnQty>0){
            holder.txtWQty.setText(sCtnQty);
        }else{
            holder.txtWQty.setText("0");
        }
        if(dLQty>0){
            holder.txtLQty.setText(sLQty);
        }else{
            holder.txtLQty.setText("0");
        }




    }

    class ViewHolder {
        TextView txtDescription;
        TextView txtPrice;
        TextView txtPoLQty;
        TextView txtPoWQty;
        TextView txtLQty;
        TextView txtWQty;
        TextView txtUnitCost;
        LinearLayout linPoLqty,linPoWqty;


        public ViewHolder(View view) {
            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
            txtPrice=(TextView)view.findViewById(R.id.txtAmnt);
            txtPoLQty = (TextView) view.findViewById(R.id.txtPoLQty);
            txtPoWQty = (TextView) view.findViewById(R.id.txtPoWQty);
            txtLQty = (TextView) view.findViewById(R.id.txtLQty);
            txtWQty = (TextView) view.findViewById(R.id.txtWQty);
            txtUnitCost = (TextView) view.findViewById(R.id.txtUnitCost);

            linPoLqty = (LinearLayout) view.findViewById(R.id.linPoLQty);
            linPoWqty = (LinearLayout) view.findViewById(R.id.linPoWQty);
        }
    }
}
