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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.menu.MenuListActivity;
import com.unipro.labisha.Screens.purchase.PurchaseOrderActivity;
import com.unipro.labisha.Screens.purchase.PurchaseOrderList;
import com.unipro.labisha.R;
import com.unipro.labisha.adapter.SummaryListAdapter;
import com.unipro.labisha.server.SoapConnection;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static com.unipro.labisha.fragments.InvoiceFragment.OrderSummaryList;
import static com.unipro.labisha.fragments.InvoiceFragment.OrderSummaryListCopy;
import static com.unipro.labisha.fragments.SupplierFragment.sVendorCode;

/**
 * Created by kalaivanan on 08/03/2018.
 */
public class SummaryFragment extends Fragment {
    public static String sPoOrderNo="";
    ListView lstSummary;
    EditText edtDONo;
    TextView txtSubTotal,txtTax,txtNetTotal;
    TextView txtTaxPerc;
    ImageView imgBtnSave,imgBtnCancel;
    SummaryListAdapter mAdapter;
    public StringRequest jsonreq;
    public RequestQueue JsonRequestQueue;
    String sTag="",sPoNo="",DeviceId="",sGstPerc="";
    SharedPreferences sprefLogin;
    String Uname,Loc_Code,baseUrl,TerminalCode;
    private LayoutInflater mInflater;
    SoapConnection soapConn;
    EditText mTextQty;
    Button mButtonSave,mButtonDelete,mButtonCancel,mButtonAdd;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vw=inflater.inflate(R.layout.fragment_summary,container,false);
        lstSummary=(ListView)vw.findViewById(R.id.lstSummary);
        txtSubTotal=(TextView)vw.findViewById(R.id.txtSubtotal);
        txtNetTotal=(TextView)vw.findViewById(R.id.txtNetTotal);
        txtTaxPerc=(TextView)vw.findViewById(R.id.txtTaxPerc);
        txtTax=(TextView)vw.findViewById(R.id.txtTax);
        mButtonAdd = (Button)vw.findViewById(R.id.btnAddItems);
        imgBtnSave=(ImageView)vw.findViewById(R.id.imgBtnSave);
        imgBtnCancel=(ImageView)vw.findViewById(R.id.imgBtnCancel);
        mInflater = LayoutInflater.from(getActivity());
        sTag=getActivity().getIntent().getExtras().getString("Tag");
//        Toast.makeText(getActivity(),sTag,Toast.LENGTH_SHORT).show();
        sprefLogin = getActivity().getSharedPreferences(LoginActivity.My_pref, 0);
        Uname = sprefLogin.getString("Uname", "");
        TerminalCode=sprefLogin.getString("TerminalCode", "");
        Loc_Code=sprefLogin.getString("LocationCode", "");
        baseUrl=sprefLogin.getString("BaseUrl", "");
        DeviceId=sprefLogin.getString("DeviceId", "");
        sGstPerc=sprefLogin.getString("sGstPerc","0");
        txtTaxPerc.setText("Tax "+sGstPerc+" %");
        soapConn=new SoapConnection();
        imgBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OrderSummaryList.size() > 0) {
                    JSONArray jsArray = new JSONArray(OrderSummaryList);
                    String sJsArray = jsArray.toString();
                    Log.e("Jsdata", sJsArray);
                    if (sTag.equalsIgnoreCase("PurchaseOrder") || sTag.equalsIgnoreCase("PoVerification")) {
                        fncSaveInvoice(sJsArray, "UPS");
                    } else if (sTag.equalsIgnoreCase("PurchaseReturn")) {
                        fncSaveReturn(sJsArray, "UPS");
                    } else if (sTag.equalsIgnoreCase("GID")) {
                        String sDono=SupplierFragment.edtDoNo.getText().toString();
                        if(sDono.equalsIgnoreCase("")){
                            Toast.makeText(getActivity(),"DoNo Not to be Empty",Toast.LENGTH_SHORT).show();
                            View dialogView = mInflater.inflate(R.layout.dialog_dono, null);
                            ShowDODialog(dialogView, sJsArray);
                            AlertDialog dialog = showDialog(dialogView);
                            edtDONo.setTag(dialog);
                        }else {
//                            Toast.makeText(getActivity(),sDono,Toast.LENGTH_SHORT).show();
                            fncSaveGID(sJsArray,sDono);
                        }

                    }
                    //                            fncSaveInvoiceAsync(sJsArray, sUserCode);
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
        lstSummary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map = OrderSummaryList.get(i);
                    if (map != null) {
                        View dialogView = mInflater.inflate(R.layout.dialog_insert, null);
                        initDialog(dialogView, map, i);
                        AlertDialog dialog = showDialog(dialogView);
                        mButtonSave.setTag(R.id.object_person, map);
                        mButtonSave.setTag(R.id.object_dialog, dialog);
                        mButtonDelete.setTag(R.id.object_person, map);
                        mButtonDelete.setTag(R.id.object_dialog, dialog);
                        mButtonCancel.setTag(R.id.object_dialog, dialog);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PurchaseOrderActivity) getActivity()).switchFragment(1);
            }
        });
        return vw;
    }

    private void ShowDODialog(View dialogView, final String sJsArray) {
        try{
            edtDONo=(EditText)dialogView.findViewById(R.id.edtDoNo);
            edtDONo.requestFocus();
            Button btnSave=(Button)dialogView.findViewById(R.id.btnDOSave);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sDono=edtDONo.getText().toString();
                    if(sDono.equalsIgnoreCase("")){
                        Toast.makeText(getActivity(),"Enter DO No",Toast.LENGTH_SHORT).show();
                        edtDONo.requestFocus();
                    }else {
                        try {
                            fncSaveGID(sJsArray, sDono);
                            AlertDialog dialog = (AlertDialog) edtDONo.getTag();
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initDialog(View view, final HashMap<String, String> Orderlistmap, final int position) {
        EditText edtInvCode = (EditText) view.findViewById(R.id.edtInvCode);
        EditText edtDesc = (EditText) view.findViewById(R.id.edtDesc);
        mTextQty = (EditText) view.findViewById(R.id.edtQty);
        EditText mTextPrice = (EditText) view.findViewById(R.id.edtPrice);
        TextView mTextId=(TextView)view.findViewById(R.id.txtId);
        ImageView imgPlus=(ImageView)view.findViewById(R.id.imgPlus);
        ImageView imgMinus=(ImageView)view.findViewById(R.id.imgMinus);
        mButtonSave = (Button) view.findViewById(R.id.btnUpdate);
        mButtonDelete =(Button)view.findViewById(R.id.btnDelete);
        mButtonCancel=(Button)view.findViewById(R.id.btnCancel);
        edtInvCode.setText(Orderlistmap.get("sInventory"));
        edtDesc.setText(Orderlistmap.get("sDescription"));
        mTextPrice.setText(Orderlistmap.get("sPrice"));
        /*mapData.put("sCtnQty", jsObj.getString("WQty"));
          mapData.put("sFocQty", jsObj.getString("FocQty"));
          mapData.put("sLQty", jsObj.getString("LQty"));
           mapData.put("sWnit", jsObj.getString("WUnit"));*/
        Double dLQty=Double.parseDouble(Orderlistmap.get("sLQty"));
        Double dWQty=Double.parseDouble(Orderlistmap.get("sCtnQty"));
        final Double dwUnit=Double.parseDouble(Orderlistmap.get("sWnit"));
        Double dTotalQty=(dWQty*dwUnit)+dLQty;
        mTextQty.setText(String.format("%.2f",dTotalQty));
        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double LQty = Double.parseDouble(mTextQty.getText().toString());
                    LQty = LQty + 1;
                    mTextQty.setText(String.valueOf(LQty));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double LQty = Double.parseDouble(mTextQty.getText().toString());
                    if (LQty > 1) {
                        LQty = LQty - 1;
                    } else {
                        LQty = 1.0;
                    }
                    mTextQty.setText(String.valueOf(LQty));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OrderSummaryList.remove(position);
                    fncCreateList();
                    AlertDialog dialog = (AlertDialog) v.getTag(R.id.object_dialog);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OrderSummaryList.remove(position);
                    Double dQty=Double.parseDouble(mTextQty.getText().toString());
                    Double dWQty=dQty/dwUnit;
                    Double dLQty=dQty%dwUnit;
                    Double dTotal=dQty*Double.parseDouble(Orderlistmap.get("sPrice"));
                    Orderlistmap.put("sCtnQty", String.format("%.2f",dWQty));
                    Orderlistmap.put("sLQty",String.format("%.2f",dLQty));
                    Orderlistmap.put("sTotalAmnt",String.format("%.2f",dTotal));
                    OrderSummaryList.add(position,Orderlistmap);
                    fncCreateList();
                    AlertDialog dialog = (AlertDialog) v.getTag(R.id.object_dialog);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog dialog = (AlertDialog) v.getTag(R.id.object_dialog);
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void fncSaveGID(String sJsArray, String sDono) {
        try{
            String paramString="";
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("sData", sJsArray);
            postDataParams.put("sUser", "");
            postDataParams.put("sLocCode",Loc_Code);
            postDataParams.put("sVendor", sVendorCode);
            postDataParams.put("sPoNo", sPoOrderNo);
            Log.e("params", postDataParams.toString());

            paramString=getPostDataString(postDataParams);

            WsCallSaveGID wsCallSaveGID=new WsCallSaveGID();
            wsCallSaveGID.sjsArray=sJsArray;
            wsCallSaveGID.sDoNo=sDono;
            wsCallSaveGID.sPoNo=sPoOrderNo;
            wsCallSaveGID.sVendorCode=sVendorCode;
            wsCallSaveGID.execute();

            /*String url="http://192.168.0.88:82/MobileERP/api/PurchaseOrder?"+paramString;
            Log.e("Url GID", url);
            final ProgressDialog pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Saving Invoice ...");
            pDialog.show();
            JsonRequestQueue = Volley.newRequestQueue(getActivity());
            jsonreq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("Summary", response);
                        response=response.replace("\"","");
                        String[] sData = response.split(",");
                        if (sData[0].equalsIgnoreCase("true")) {
                            Toast.makeText(getActivity(),sData[1]+" Saved Successfully",Toast.LENGTH_LONG).show();
                            OrderSummaryList.clear();
                            Intent in = new Intent(getActivity(), PurchaseOrderList.class);
                            startActivity(in);
                            getActivity().finish();
                        }else {
                            Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("VehicleMaster", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });
            jsonreq.setRetryPolicy(new DefaultRetryPolicy(120000, 1, 1.0f));
            jsonreq.setTag("images");
            JsonRequestQueue.add(this.jsonreq);
            jsonreq.setShouldCache(true);*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public AlertDialog showDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(null)
                .setView(view);
        return builder.show();
    }

    private void fncSaveReturn(String sJsArray, String sUserCode) {
        try{
            WsCallSavePOReturn wsCallSavePOReturn=new WsCallSavePOReturn();
            wsCallSavePOReturn.sjsArray=sJsArray;
            wsCallSavePOReturn.sVendorCode=sVendorCode;
            wsCallSavePOReturn.execute();
            /*String paramString="";
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("sData", sJsArray);
            postDataParams.put("sUser", sUserCode);
            postDataParams.put("sLocCode", sUserCode);
            postDataParams.put("sVendor",sVendorCode);
            postDataParams.put("sReturnType",InvoiceFragment.sReturnType);
            Log.e("params", postDataParams.toString());

            paramString=getPostDataString(postDataParams);

            String url="http://192.168.0.88:82/MobileERP/api/PurchaseOrder?"+paramString;
            Log.e("Url return", url);
            final ProgressDialog pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Saving Invoice ...");
            pDialog.show();
            JsonRequestQueue = Volley.newRequestQueue(getActivity());
            jsonreq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("Summary", response);
                        response=response.replace("\"","");
                        String[] sData = response.split(",");
                        if (sData[0].equalsIgnoreCase("true")) {
                            Toast.makeText(getActivity(),sData[1]+" Saved Successfully",Toast.LENGTH_LONG).show();
                            OrderSummaryList.clear();
                            Intent in = new Intent(getActivity(), PurchaseOrderList.class);
                            startActivity(in);
                            getActivity().finish();
                        }else {
                            Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("VehicleMaster", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });
            jsonreq.setRetryPolicy(new DefaultRetryPolicy(120000, 1, 1.0f));
            jsonreq.setTag("images");
            JsonRequestQueue.add(this.jsonreq);
            jsonreq.setShouldCache(true);*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible){
            try {
                if (sTag.equalsIgnoreCase("GID")) {
                    sPoNo = getActivity().getIntent().getExtras().getString("PoNo");
                    Toast.makeText(getActivity(), sPoNo, Toast.LENGTH_SHORT).show();
                    if(OrderSummaryList.size()== 0) {
                        fncGetPoDetails();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            fncCreateList();
        }
    }

    private void fncCreateTotal() {
        try {
            if (OrderSummaryList.size() > 0) {
                Double dSubTotal = 0.0, dNetTotal = 0.0, dTax = 0.0;
                for (int i = 0; i < OrderSummaryList.size(); i++) {
                    HashMap<String, String> mapData = OrderSummaryList.get(i);
                    dSubTotal += Double.parseDouble(mapData.get("sTotalAmnt"));
                }
                Double dGst=Double.parseDouble(sGstPerc);
                dTax = dSubTotal / 100 * dGst;
                dNetTotal = dSubTotal + dTax;
                txtSubTotal.setText(String.format("%.2f", dSubTotal));
                txtTax.setText(String.format("%.2f", dTax));
                txtNetTotal.setText(String.format("%.2f", dNetTotal));
//                fncCreateList();
            } else {
                Toast.makeText(getActivity(), "No Item Found", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void fncGetPoDetails() {
        try{
            WsCallGetPOGID wsCallGetPOGID=new WsCallGetPOGID();
            wsCallGetPOGID.sPoNo=sPoNo;
            wsCallGetPOGID.execute();
            /*OrderSummaryList=new ArrayList<>();
            String url="http://192.168.0.88:82/MobileERP/api/PurchaseOrder?sPoNo="+sPoNo+"&sUser=Admin";
            Log.e("Url", url);
            final ProgressDialog pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Saving Invoice ...");
            pDialog.show();
            JsonRequestQueue = Volley.newRequestQueue(getActivity());
            jsonreq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("Summary", response);
                        JSONObject jsObj=new JSONObject(response);
                        String sDetail=jsObj.getString("sDetail");
                        String sHeader=jsObj.getString("sHeader");
                        JSONArray jsArrayDeatil=new JSONArray(sDetail);
                        JSONArray jsArrayHeader=new JSONArray(sHeader);
                        int mCountHdr=jsArrayHeader.length();
                        int mCountDet=jsArrayDeatil.length();
                        jsObj=jsArrayHeader.getJSONObject(0);
                        sVendorCode=jsObj.getString("Vendorcode");
                        sPoOrderNo=jsObj.getString("PoNo");
                        for(int i=0;i<mCountDet;i++) {
                            jsObj=jsArrayDeatil.getJSONObject(i);
                            HashMap<String, String> mapData = new HashMap<>();
                            mapData.put("sInventory", jsObj.getString("InventoryCode"));
                            mapData.put("sDescription", jsObj.getString("Description"));
                            mapData.put("sPrice", jsObj.getString("UnitCost"));
                            mapData.put("sCtnQty", jsObj.getString("WQty"));
                            mapData.put("sFocQty", jsObj.getString("FocQty"));
                            mapData.put("sLQty", jsObj.getString("LQty"));
                            mapData.put("sPoCtnQty", jsObj.getString("WQty"));
                            mapData.put("sPoFocQty", jsObj.getString("FocQty"));
                            mapData.put("sPoLQty", jsObj.getString("LQty"));
                            mapData.put("sWnit", jsObj.getString("WUnit"));
                            mapData.put("sTotalAmnt", jsObj.getString("TotalValue"));
                            Log.e("mapData", mapData + "");
                            OrderSummaryList.add(mapData);
                        }
                        if (OrderSummaryList.size() > 0) {
                            Double dSubTotal = 0.0, dNetTotal = 0.0, dTax = 0.0;
                            for (int i = 0; i < OrderSummaryList.size(); i++) {
                                HashMap<String, String> mapData = OrderSummaryList.get(i);
                                dSubTotal += Double.parseDouble(mapData.get("sTotalAmnt"));
                            }
                            dTax = dSubTotal / 100 * 7;
                            dNetTotal = dSubTotal + dTax;
                            txtSubTotal.setText(String.format("%.2f", dSubTotal));
                            txtTax.setText(String.format("%.2f", dTax));
                            txtNetTotal.setText(String.format("%.2f", dNetTotal));
                            fncCreateList();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("VehicleMaster", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });
            jsonreq.setRetryPolicy(new DefaultRetryPolicy(120000, 1, 1.0f));
            jsonreq.setTag("images");
            JsonRequestQueue.add(this.jsonreq);
            jsonreq.setShouldCache(true);*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fncCreateList() {
        try{
            mAdapter=new SummaryListAdapter(getActivity());
            mAdapter.setData(OrderSummaryList);
            if(sTag.equalsIgnoreCase("PoVerification")){
               mAdapter.setDataForOriginal(OrderSummaryListCopy);
            }
            lstSummary.setAdapter(mAdapter);
            fncCreateTotal();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fncSaveInvoice(final String sJsArray, final String sUserCode) {
        try{

            WsCallSavePOHeader wsCallSavePOHeader=new WsCallSavePOHeader();
            wsCallSavePOHeader.sJsArray=sJsArray;
            wsCallSavePOHeader.sVendorCode=sVendorCode;
            wsCallSavePOHeader.execute();
//            String paramString = URLEncoder.encode("sData="+sJsArray+"&sUser="+sUserCode, "utf-8");
           /* String paramString="sData="+sJsArray+"&sUser="+sUserCode;

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("sData", sJsArray);
            postDataParams.put("sUser", sUserCode);
            postDataParams.put("sLocCode", sUserCode);
            postDataParams.put("sVendor",sVendorCode);
            Log.e("params", postDataParams.toString());

            paramString=getPostDataString(postDataParams);

            String url="http://192.168.0.88:82/MobileERP/api/PurchaseOrder?"+paramString;
            Log.e("Url", url);
            final ProgressDialog pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Saving Invoice ...");
            pDialog.show();
            JsonRequestQueue = Volley.newRequestQueue(getActivity());
            jsonreq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("Summary", response);
                        response=response.replace("\"","");
                        String[] sData = response.split(",");
                        if (sData[0].equalsIgnoreCase("true")) {
                            Toast.makeText(getActivity(),sData[1]+" Saved Successfully",Toast.LENGTH_LONG).show();
                            OrderSummaryList.clear();
                            Intent in = new Intent(getActivity(), PurchaseOrderList.class);
                            startActivity(in);
                            getActivity().finish();
                        }else {
                            Toast.makeText(getActivity(),response,Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("VehicleMaster", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            });
            jsonreq.setRetryPolicy(new DefaultRetryPolicy(120000, 1, 1.0f));
            jsonreq.setTag("images");
            JsonRequestQueue.add(this.jsonreq);
            jsonreq.setShouldCache(true);*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
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

    private class WsCallSavePOReturn extends AsyncTask<String,String,String>{
        public String sjsArray;
        String sResp="",sWebMethod="fncSavePOReturn";

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
                sResp=soapConn.fncSavePOReturn(sjsArray, sVendorCode, Loc_Code, Uname, TerminalCode, InvoiceFragment.sReturnType, sWebMethod,baseUrl);
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
                dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
                dialog.dismiss();
            }
        }
    }

    private class WsCallGetPOGID extends AsyncTask<String,String,String>{

        public String sPoNo;
        String sWebMethod="fncGetPoGid",sResp="";
        private final ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);

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
                sResp=soapConn.fncGetPoGid(sPoNo,Uname,sWebMethod,baseUrl);
                return sResp;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.startsWith("false")){
                    String[] sResponse=s.split(",");
                    Toast.makeText(getActivity(),sResponse[1],Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject jsObj=new JSONObject(s);
                    String sDetail=jsObj.getString("sDetail");
                    String sHeader=jsObj.getString("sHeader");
                    JSONArray jsArrayDeatil=new JSONArray(sDetail);
                    JSONArray jsArrayHeader=new JSONArray(sHeader);
                    int mCountHdr=jsArrayHeader.length();
                    int mCountDet=jsArrayDeatil.length();
                    jsObj=jsArrayHeader.getJSONObject(0);
                    sVendorCode=jsObj.getString("VendorCode");
                    sPoOrderNo=jsObj.getString("PoNo");
                    for(int i=0;i<mCountDet;i++) {
                        jsObj=jsArrayDeatil.getJSONObject(i);
                        HashMap<String, String> mapData = new HashMap<>();
                        mapData.put("sInventory", jsObj.getString("ItemCode"));
                        mapData.put("sDescription", jsObj.getString("ItemDesc"));
                        mapData.put("sPrice", jsObj.getString("UnitCost"));
                        mapData.put("sCtnQty", jsObj.getString("WQty"));
                        mapData.put("sFocQty", jsObj.getString("FocQty"));
                        mapData.put("sLQty", jsObj.getString("LQty"));
                        mapData.put("sPoCtnQty", jsObj.getString("WQty"));
                        mapData.put("sPoFocQty", jsObj.getString("FocQty"));
                        mapData.put("sPoLQty", jsObj.getString("LQty"));
                        mapData.put("sWnit", jsObj.getString("WUnit"));
                        mapData.put("sTotalAmnt", jsObj.getString("TotalValue"));
                        Log.e("mapData", mapData + "");
                        OrderSummaryList.add(mapData);
                    }
                    if (OrderSummaryList.size() > 0) {
                        Double dSubTotal = 0.0, dNetTotal = 0.0, dTax = 0.0;
                        for (int i = 0; i < OrderSummaryList.size(); i++) {
                            HashMap<String, String> mapData = OrderSummaryList.get(i);
                            dSubTotal += Double.parseDouble(mapData.get("sTotalAmnt"));
                        }
                        dTax = dSubTotal / 100 * Double.parseDouble(sGstPerc);
                        dNetTotal = dSubTotal + dTax;
                        txtSubTotal.setText(String.format("%.2f", dSubTotal));
                        txtTax.setText(String.format("%.2f", dTax));
                        txtNetTotal.setText(String.format("%.2f", dNetTotal));
                        fncCreateList();
                    }
                }
                dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
                dialog.dismiss();
            }
        }
    }

    private class WsCallSaveGID extends AsyncTask<String,String,String>{
        public String sjsArray;
        String sResp="",sWebMethod="fncSaveGID";

        private final ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);
        public String sVendorCode;
        public String sPoNo;
        public String sDoNo;

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
                sResp=soapConn.fncSaveGID(sjsArray, sVendorCode, Loc_Code, Uname, TerminalCode,sPoNo,sDoNo, sWebMethod, baseUrl);
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
}
