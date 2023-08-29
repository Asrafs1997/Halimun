package com.unipro.labisha.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.unipro.labisha.R;
import com.unipro.labisha.utils.FastScrollRecyclerViewInterface;
import com.unipro.labisha.utils.SearchItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User1 on 13/10/2016.
 */
public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.ViewHolder> implements FastScrollRecyclerViewInterface {

    private ArrayList<String> mDataDesc;
    private ArrayList<String> mDataCode;
    private ArrayList<String> mDataPrice;
    private HashMap<String, Integer> mMapIndex;
    SearchItemClickListener clickListener;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextName;
        public TextView mTextCode;
        public TextView mTextPrice;
        public ViewHolder(View v) {
            super(v);
            mTextName=(TextView)v.findViewById(R.id.name);
            mTextCode = (TextView)v.findViewById(R.id.code);
            mTextPrice = (TextView) v.findViewById(R.id.addr);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String,String> mapData=new HashMap<String, String>();
                    clickListener.OnRecycleItemClick(mapData, mTextCode.getText().toString());
                }
            });
            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    clickListener.OnRecycleItemClick(mTextCode.getText().toString(), mTextName.getText().toString());
                }
            });*/
        }
    }

    public SearchItemAdapter(ArrayList<String> myDataset, HashMap<String, Integer> mapIndex) {
        mDataDesc = myDataset;
        mMapIndex = mapIndex;
    }
    public SearchItemAdapter(ArrayList<String> myDataname, ArrayList<String> myDatacode, ArrayList<String> myDataPrc, HashMap<String, Integer> mapIndex) {
        mDataDesc = myDataname;
        mDataCode=myDatacode;
        mDataPrice=myDataPrc;
        mMapIndex = mapIndex;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_search, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextName.setText(mDataDesc.get(position));
        holder.mTextCode.setText(mDataCode.get(position));
        holder.mTextPrice.setText(mDataPrice.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataDesc.size();
    }

    @Override
    public HashMap<String, Integer> getMapIndex() {
        return this.mMapIndex;
    }

    public void setOnItemClickListener(final SearchItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}
