package com.unipro.labisha.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.menu.MenuListActivity;
import com.unipro.labisha.Screens.purchase.PurchaseOrderList;
import com.unipro.labisha.R;
import com.unipro.labisha.adapter.CompareListAdapter;
import com.unipro.labisha.server.SoapConnection;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

import static com.unipro.labisha.fragments.InvoiceFragment.OrderSummaryList;
import static com.unipro.labisha.fragments.InvoiceFragment.OrderSummaryListCopy;
import static com.unipro.labisha.fragments.SupplierFragment.sVendorCode;

/**
 * Created by kalaivanan on 08/03/2018.
 */
public class CompareFragment extends Fragment {

    ListView lstExist,lstExtra;
    ImageView imgBtnSave,imgBtnCancel;
    SoapConnection soapConn;
    String Uname,Loc_Code,baseUrl,TerminalCode;
    SharedPreferences sprefLogin;
    String sTag="",sPoNo="",DeviceId="",sGstPerc="";
    CompareListAdapter mExistAdapter,mExtraAdapter;

    public static ArrayList<HashMap<String,String>> OrderExistList;
    public static ArrayList<HashMap<String,String>> OrderExtraList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vw=inflater.inflate(R.layout.fragment_compare,container,false);

        lstExist=(ListView)vw.findViewById(R.id.lstExist);
        lstExtra=(ListView)vw.findViewById(R.id.lstExtra);
        imgBtnSave=(ImageView)vw.findViewById(R.id.imgBtnSave);
        imgBtnCancel=(ImageView)vw.findViewById(R.id.imgBtnCancel);

        sTag=getActivity().getIntent().getExtras().getString("Tag");
        soapConn=new SoapConnection();
        sprefLogin = getActivity().getSharedPreferences(LoginActivity.My_pref, 0);
        Uname = sprefLogin.getString("Uname", "");
        TerminalCode=sprefLogin.getString("TerminalCode", "");
        Loc_Code=sprefLogin.getString("LocationCode", "");
        baseUrl=sprefLogin.getString("BaseUrl", "");
        DeviceId=sprefLogin.getString("DeviceId", "");


        imgBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<HashMap<String,String>> DataSummaryList = new ArrayList<>();
                if (OrderSummaryList.size() > 0) {
                    try {
                        for (int i = 0; i < OrderSummaryList.size(); i++) {
                            Double dCtnQty = 0.0, dLQty = 0.0;
                            String sCtnQty = OrderSummaryList.get(i).get("sCtnQty").trim();
                            String sLQty = OrderSummaryList.get(i).get("sLQty").trim();
                            if (sCtnQty.equalsIgnoreCase("")) {
                                sCtnQty = "0.0";
                            }
                            if (sLQty.equalsIgnoreCase("")) {
                                sLQty = "0.0";
                            }
                            dCtnQty = Double.parseDouble(sCtnQty);
                            dLQty = Double.parseDouble(sLQty);
                            if (dCtnQty <= 0.0 && dLQty <= 0.0) {
                              //  OrderSummaryList.remove(i);
                                Log.d("samlog", "removed - " + i);
                            } else {
                                DataSummaryList.add(OrderSummaryList.get(i));
                                Log.d("samlog", "added - " + i);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("samlog", "added - " + e.getMessage());
                    }
                }


                if (DataSummaryList.size() > 0) {
                    JSONArray jsArray = new JSONArray(DataSummaryList);
                    String sJsArray = jsArray.toString();
                    Log.d("samlog", sJsArray);
                    fncSaveInvoice(sJsArray, "UPS");
                } else {
                    Toast.makeText(getActivity(), "No Items found to save", Toast.LENGTH_LONG).show();
                }
            }
        });

        imgBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("Are you Sure to Cancel Order");
                builder.setMessage("Click Ok to cancel");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent in = new Intent(getActivity(), MenuListActivity.class);
                        startActivity(in);
                        getActivity().finishAffinity();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        return vw;
    }


    private void fncSaveInvoice(final String sJsArray, final String sUserCode) {
        try{

            WsCallSavePOHeader wsCallSavePOHeader=new WsCallSavePOHeader();
            wsCallSavePOHeader.sJsArray=sJsArray;
            wsCallSavePOHeader.sVendorCode=sVendorCode;
            wsCallSavePOHeader.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private class WsCallSavePOHeader extends AsyncTask<String,String,String>{
        public String sJsArray;
        String sResp="",sWebMethod="fncSavePOHeader";

        private final ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);
        public String sVendorCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            try{
                sResp=soapConn.fncSavePOHeader(sJsArray, sVendorCode,Loc_Code, Uname, TerminalCode, DeviceId, sWebMethod,baseUrl,InvoiceFragment.isPoedit,PurchaseOrderList.sPoNo);
                return sResp;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                String[] sResponse=s.split(",");
                if (s.startsWith("true")) {
                    Toast.makeText(getActivity(),"Saved Successfully "+sResponse[1],Toast.LENGTH_SHORT).show();
                    OrderSummaryList=new ArrayList<>();
                    Intent in=new Intent(getActivity(),PurchaseOrderList.class);
                    in.putExtra("Tag",sTag);
                    startActivity(in);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(),"Not Saved "+sResponse[1],Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible){
            fncCreateList();
        }
    }


    private void fncCreateList() {
        try{
            OrderExistList = new ArrayList<>();
            OrderExtraList = new ArrayList<>();
            if (OrderSummaryList.size() > 0) {
                for (int i = 0; i < OrderSummaryList.size(); i++) {
                  String   sInventory = OrderSummaryList.get(i).get("sInventory").trim();
                  boolean isexist = false;
                    for (int j = 0; j < OrderSummaryListCopy.size(); j++) {
                        if( sInventory.equalsIgnoreCase(OrderSummaryListCopy.get(j).get("sInventory").trim())){
                            isexist = true ;
                        }
                    }
                  if(isexist){
                      OrderExistList.add(OrderSummaryList.get(i));
                  }else {
                      OrderExtraList.add(OrderSummaryList.get(i));
                  }
                }
            }
            mExistAdapter=new CompareListAdapter(getActivity(),1,OrderExistList,OrderSummaryListCopy);
            lstExist.setAdapter(mExistAdapter);

            mExtraAdapter=new CompareListAdapter(getActivity(),2,OrderExtraList,new ArrayList<HashMap<String, String>>());
            lstExtra.setAdapter(mExtraAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
