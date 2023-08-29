package com.unipro.labisha.adapter;

/**
 * Created by User1 on 17/07/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unipro.labisha.R;

import java.util.ArrayList;
import java.util.HashMap;


public class CollectionListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,String>> mData;
    private LayoutInflater mInflater;
    Boolean isAdd;
    private boolean isTransferOut;

    public CollectionListAdapter(Context applicationContext, Boolean isAdd) {
        mInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isAdd=isAdd;
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
            row = mInflater.inflate( R.layout.collection_list_items, parent, false);
            LinearLayout linearlist=(LinearLayout)row.findViewById(R.id.linearlist);
            if(position%2==0){
                linearlist.setBackgroundColor(Color.parseColor("#80D8FF"));
            }
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
            String CustCode = "";
            String CustName = "";
            String InvNum="";
            String Remarks="";
            String InvDate="";
            String InvCode,TotalAmt;
            Double qty = 0.0, Discount = 0.0;
            String  TotalAmnt ="";
            String sellingprice,Description;
            String sChqNo="";
            String sChqDate = "";
            String sBank = "";





            if (isAdd) {

                CustCode = rowData.get("sCustCode");
                CustName = rowData.get("sCustName");//String.format("%.2f", d)
                InvNum = (rowData.get("sInvNo"));
                InvDate = (rowData.get("sInvDate"));
                TotalAmnt = (rowData.get("sTotalAmnt"));
              //  RefNo = (rowData.get("sRefNo"));
                Remarks = (rowData.get("sRemarks"));
                sChqNo = (rowData.get("sChqNo"));
                sChqDate = (rowData.get("sChqDate"));
                sBank = (rowData.get("sPayMode"));



            } else {
                InvCode = rowData.get("InventoryCode");
                Description = rowData.get("Description");//String.format("%.2f", d)
                qty = Double.parseDouble(rowData.get("Quantity"));
                sellingprice = (rowData.get("SellingPrize"));
                TotalAmnt = (rowData.get("TotalAmount"));
                Discount = Double.parseDouble(rowData.get("Discount"));
            }
//            holder.custcode.setText(CustCode);//rowData.get("InventoryCode")
//            holder.custname.setText(CustName);//rowData.get("Description")
            holder.invno.setText(InvNum);
//            holder.invdate.setText(InvDate);
            holder.totalamount.setText(TotalAmnt);
//            holder.remarks.setText(Remarks);
//            holder.invdate.setText(sChqNo);
            holder.sBank.setText(sBank);
//            holder.remarks.setText(sBank);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setIsTransferOut(boolean isTransferOut) {
        this.isTransferOut = isTransferOut;
    }

    class ViewHolder {
        TextView custcode;
        TextView custname;
        TextView invno;
        TextView invdate;
        TextView totalamount;
        TextView remarks;
        TextView sChqNo;
        TextView sChqDate;
        TextView sBank;


        public ViewHolder(View view) {
//            custcode    = (TextView) view.findViewById(R.id.custcode);
//            custname=(TextView)view.findViewById(R.id.custname);
            invno = (TextView) view.findViewById(R.id.invno);
//            invdate = (TextView) view.findViewById(R.id.invdate);
            totalamount = (TextView) view.findViewById(R.id.txtAmtTotal);
//            remarks=(TextView)view.findViewById(R.id.remarks);
//            sChqNo = (TextView) view.findViewById(R.id.chknum);
//            sChqDate = (TextView) view.findViewById(R.id.chkdate);
            sBank=(TextView)view.findViewById(R.id.bank);

        }
    }
}
