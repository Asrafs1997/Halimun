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
 * Created by kalaivanan on 06/03/2018.
 */
public class PackageListAdapter extends BaseAdapter {

    private ArrayList<HashMap<String, String>> mData;
    private LayoutInflater mInflater;

    private boolean isPackages;

//    CustomItemClickListener listener;

    /*    public PurchaseListAdapter(Context applicationContext, CustomItemClickListener customItemClickListener) {
            mInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.listener=customItemClickListener;
        }*/
    public PackageListAdapter(Context applicationContext, boolean isPackage) {
        mInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        isPackages= isPackage;
    }

    public ArrayList<HashMap<String, String>> getData() {
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
            row = mInflater.inflate(R.layout.package_list_items, parent, false);
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

       // holder.sno.setText(String.valueOf(position+1));
     //   holder.sno2.setText(String.valueOf(position+1));

        return row;
    }

    public void initValues(ViewHolder holder, HashMap<String, String> rowData) {

        holder.txtInvCode.setText(rowData.get("InventoryCode"));
        holder.txtInvCode2.setText(rowData.get("InventoryCode"));
        holder.txtDescription.setText(rowData.get("Description1"));
        holder.txtDescription2.setText(rowData.get("Description1"));
        holder.txtOnhand.setText(rowData.get("QtyonHand"));
        holder.txtQty.setText(rowData.get("Qty"));
        holder.txt_totalQty.setText(rowData.get("Qty"));
        holder.txtPrice.setText(rowData.get("RetailPrice"));
        holder.txtPrice2.setText(rowData.get("RetailPrice"));
        holder.ispackage_llv.setVisibility(View.GONE);
        holder.notpackage_llv.setVisibility(View.GONE);

        if (isPackages) {
            holder.ispackage_llv.setVisibility(View.VISIBLE);
            holder.notpackage_llv.setVisibility(View.GONE);
        } else {
            holder.ispackage_llv.setVisibility(View.GONE);
            holder.notpackage_llv.setVisibility(View.VISIBLE);
        }


    }

    class ViewHolder {
        TextView txtInvCode, txtDescription, txtOnhand, txtQty, txtPrice;
        TextView txtDescription2, txtInvCode2, txtPrice2,txt_totalQty;
        LinearLayout ispackage_llv, notpackage_llv;

        public ViewHolder(View view) {
            txtInvCode = (TextView) view.findViewById(R.id.txtInvCode);
            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
            txtOnhand = (TextView) view.findViewById(R.id.txtOnhand);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);


            txtDescription2 = (TextView) view.findViewById(R.id.txtDescription2);
            txtInvCode2 = (TextView) view.findViewById(R.id.txtInvCode2);
            txtPrice2 = (TextView) view.findViewById(R.id.txtPrice2);
            txt_totalQty = (TextView) view.findViewById(R.id.txt_totalQty);
            notpackage_llv = (LinearLayout) view.findViewById(R.id.notpackage_llv);
            ispackage_llv = (LinearLayout) view.findViewById(R.id.ispackage_llv);
        }
    }
}
