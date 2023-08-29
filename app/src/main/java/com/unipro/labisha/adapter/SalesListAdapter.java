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
 * Created by Kalaivanan on 24/04/2018.
 */
public class SalesListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String,String>> mData;
    private LayoutInflater mInflater;
    Boolean IsDeliveryOrder;
    private boolean isTransferOut;

    public SalesListAdapter(Context applicationContext, Boolean isDeliveryOrder) {
        mInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.IsDeliveryOrder=isDeliveryOrder;
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
            row = mInflater.inflate(R.layout.sales_list_items, parent, false);
            LinearLayout linearlist=(LinearLayout)row.findViewById(R.id.linearlist);
//             if(position%2==0){
//                linearlist.setBackgroundColor(Color.parseColor("#80D8FF"));
//            }
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
            String InvCode = "";
            String Description = "";
            Double qty = 0.0, Discount = 0.0;
            Double sellingprice = 0.0, TotalAmnt = 0.0;
            if (IsDeliveryOrder) {
                InvCode = rowData.get("ItemCode");
                Description = rowData.get("ItemDescription");//String.format("%.2f", d)
                qty = Double.parseDouble(rowData.get("Quantity"));
                sellingprice = Double.parseDouble(rowData.get("SellingPrice"));
                TotalAmnt = Double.parseDouble(rowData.get("QTotal"));
            } else {
                InvCode = rowData.get("InventoryCode");
                Description = rowData.get("Description");//String.format("%.2f", d)
                qty = Double.parseDouble(rowData.get("Quantity"));
                sellingprice = Double.parseDouble(rowData.get("SellingPrize"));
                TotalAmnt = Double.parseDouble(rowData.get("TotalAmount"));
                Discount = Double.parseDouble(rowData.get("Discount"));
            }
            holder.txtInvCode.setText(InvCode);//rowData.get("InventoryCode")
            holder.txtDesc.setText(Description);//rowData.get("Description")
            holder.txtQty.setText(String.format("%.2f", qty));
            holder.txtPrice.setText(String.format("%.2f", sellingprice));
            holder.txtTotalAmount.setText(String.format("%.2f", TotalAmnt));
            holder.txtDiscount.setText(String.format("%.2f", Discount));
            if(isTransferOut){
                holder.txtDiscount.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setIsTransferOut(boolean isTransferOut) {
        this.isTransferOut = isTransferOut;
    }

    class ViewHolder {
        TextView txtInvCode;
        TextView txtDesc;
        TextView txtQty;
        TextView txtPrice;
        TextView txtTotalAmount;
        TextView txtDiscount;

        public ViewHolder(View view) {
            txtInvCode = (TextView) view.findViewById(R.id.txtInvCode);
            txtDesc=(TextView)view.findViewById(R.id.txtDescription);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtTotalAmount = (TextView) view.findViewById(R.id.txtTotal);
            txtDiscount=(TextView)view.findViewById(R.id.txtDiscount);
        }
    }
}
