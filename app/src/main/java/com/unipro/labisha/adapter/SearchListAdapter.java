package com.unipro.labisha.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.unipro.labisha.R;
import com.unipro.labisha.utils.FastScrollRecyclerViewInterface;
import com.unipro.labisha.utils.SearchItemClickListener;
import com.unipro.labisha.utils.UnCaughtException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kalaivanan on 13/10/2016.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> implements FastScrollRecyclerViewInterface {

    private ArrayList<HashMap<String,String>> DishMenuList;
    private HashMap<String, Integer> mMapIndex;
    SearchItemClickListener clickListener;
    Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextDishName;
        public TextView mTextDishDesc;
        public TextView mTextDishPrice;
        EditText edtCount;
        ImageView imgPlus,imgMinus,imgCart;

        public ViewHolder(View v) {
            super(v);
            mTextDishName = (TextView) v.findViewById(R.id.txtDishName);
            mTextDishDesc=(TextView)v.findViewById(R.id.txtDishDesc);
            mTextDishPrice=(TextView)v.findViewById(R.id.txtDishPrice);
            edtCount=(EditText)v.findViewById(R.id.edtCount);
            imgPlus=(ImageView)v.findViewById(R.id.imgPlus);
            imgMinus=(ImageView)v.findViewById(R.id.imgMinus);
            imgCart=(ImageView)v.findViewById(R.id.imgCart);
            edtCount.setVisibility(View.GONE);
            imgPlus.setVisibility(View.GONE);
            imgMinus.setVisibility(View.GONE);
            imgCart.setVisibility(View.GONE);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String,String> mapData=new HashMap<String, String>();
                    clickListener.OnRecycleItemClick(mapData, mTextDishName.getTag().toString());
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

    public SearchListAdapter(Context context, ArrayList<HashMap<String, String>> dishList, HashMap<String, Integer> mapIndex) {
        this.mContext=context;
        this.DishMenuList=dishList;
        mMapIndex = mapIndex;
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(mContext));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_menu_items_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            String sText=DishMenuList.get(position).get("InventoryCode")+"-"+DishMenuList.get(position).get("Description");
            holder.mTextDishName.setText(sText);
            holder.mTextDishName.setTag(DishMenuList.get(position).get("InventoryCode"));
            holder.mTextDishDesc.setTag(DishMenuList.get(position));
            Double dAmt = Double.parseDouble(DishMenuList.get(position).get("RetailPrice"));
            holder.mTextDishPrice.setText("S$ " + String.format("%.2f", dAmt));
            holder.edtCount.setText("0");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return DishMenuList.size();
    }

    @Override
    public HashMap<String, Integer> getMapIndex() {
        return this.mMapIndex;
    }

    public void setOnItemClickListener(SearchItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}
