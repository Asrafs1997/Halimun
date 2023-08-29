package com.unipro.labisha.fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.unipro.labisha.Screens.purchase.PoVerificationProcess;
import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.purchase.PurchaseOrderActivity;
import com.unipro.labisha.R;
import com.unipro.labisha.Screens.search.SearchActivity;
import com.unipro.labisha.server.SoapConnection;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kalaivanan on 08/03/2018.
 */


public class InvoiceFragment extends Fragment {
    EditText edtDesc,edtUomPcs,edtUomSize,edtPrice;
    EditText edtCtnQty,edtLQty,edtFocQty;
    EditText edtInvCode;
    EditText edtRetailPrice;
    ImageView imgBtnSearch,btnImgNormal,btnImgFOC;
    ImageView btnImgExchange,imgBtnAdd,imgBtnDone,imgBtnClear,imgBtnScan;
    TextView txtTotalAmt;
    Spinner spnType;
    LinearLayout linSpinner;
    String sTag;
    public ProgressDialog pDialog;
    public StringRequest jsonreq;
    public RequestQueue JsonRequestQueue;
    String Loc_Code,Uname,baseUrl,TerminalCode;
    SharedPreferences sprefLogin;
    String sInventoryCode,sDescription,sPrice;
    String sFocQty,sCtnQty,sLQty,sWnit,sTotAmnt;
    ArrayList<String> arrayReturnType;
    public static ArrayList<HashMap<String,String>> OrderSummaryList;

    public static ArrayList<HashMap<String,String>> OrderSummaryListCopy;
    public static String sReturnType="";
    public static boolean isPoedit = false;
    public static boolean isPredit = false;
    public static boolean isGidedit = false;
    Boolean sFlag = false;
    int length;
    Toast toast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vw=inflater.inflate(R.layout.fragment_add_item,container,false);
        edtInvCode=(EditText)vw.findViewById(R.id.edtInventoryCode);
        edtDesc=(EditText)vw.findViewById(R.id.edtDescription);
        edtUomPcs=(EditText)vw.findViewById(R.id.edtUomPcs);
        edtUomSize=(EditText)vw.findViewById(R.id.edtUomSize);
        edtPrice=(EditText)vw.findViewById(R.id.edtPrice);
        edtCtnQty=(EditText)vw.findViewById(R.id.edtCtnQty);
        edtLQty=(EditText)vw.findViewById(R.id.edtLQty);
        edtFocQty=(EditText)vw.findViewById(R.id.edtFocQty);
        edtRetailPrice = (EditText)vw.findViewById(R.id.edtUnitPrice);
        imgBtnSearch=(ImageView)vw.findViewById(R.id.imgBtnSearch);
        imgBtnScan=(ImageView)vw.findViewById(R.id.btnscan);
        btnImgNormal=(ImageView)vw.findViewById(R.id.btnImgNormalPrice);
        btnImgFOC=(ImageView)vw.findViewById(R.id.btnImgFoc);
        btnImgExchange=(ImageView)vw.findViewById(R.id.btnImgExchange);
        imgBtnAdd=(ImageView)vw.findViewById(R.id.imgBtnAdd);
        imgBtnDone=(ImageView)vw.findViewById(R.id.imgBtnDone);
        imgBtnClear=(ImageView)vw.findViewById(R.id.imgBtnClear);
        txtTotalAmt=(TextView)vw.findViewById(R.id.txtTotalAmt);
        linSpinner=(LinearLayout)vw.findViewById(R.id.lnSpinner);
        spnType=(Spinner)vw.findViewById(R.id.spnType);
        sTag=getActivity().getIntent().getExtras().getString("Tag");
        if(sTag.equalsIgnoreCase("PurchaseReturn")){
            linSpinner.setVisibility(View.VISIBLE);
        }else {
            linSpinner.setVisibility(View.GONE);
        }
        arrayReturnType=new ArrayList<>();
        if(sTag.equalsIgnoreCase("PoVerification")){
           OrderSummaryListCopy = new ArrayList<>();
           OrderSummaryListCopy.addAll(OrderSummaryList);
        }

        if(!isPoedit && !isPredit && !isGidedit){
            OrderSummaryList = new ArrayList<>();
        }


//        if (!isPoedit ) {
//            OrderSummaryList = new ArrayList<>();
//        }

        arrayReturnType.add("RETURN");
        arrayReturnType.add("DAMAGE");
        sprefLogin = getActivity().getSharedPreferences(LoginActivity.My_pref, 0);
        Uname = sprefLogin.getString("Uname", "");
        TerminalCode=sprefLogin.getString("TerminalCode", "");
        Loc_Code=sprefLogin.getString("LocationCode", "");
        baseUrl=sprefLogin.getString("BaseUrl", "");


        imgBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
                    startActivityForResult(intent, 0);
                }
                catch (ActivityNotFoundException ex){
                    Log.e("onCreate", "Scanner Not Found", ex);
                    toast=Toast.makeText(getActivity(),"Scanner Not Found",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        edtInvCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String sInvCode = edtInvCode.getText().toString();
                    if (sInvCode.isEmpty()) {
                        Toast toast = Toast.makeText(getActivity(), "Please Enter Inventory Code", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        fncGetInventoryDetails(sInvCode, Loc_Code);
                    }
                    return true;
                }
                return false;
            }
        });
        edtCtnQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    fncCreateTotal();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edtLQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    fncCreateTotal();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtCtnQty.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        fncCreateTotal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
        edtLQty.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        fncCreateTotal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });
        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent in = new Intent(getActivity(), SearchActivity.class);
                    in.putExtra("value", 4);
                    startActivityForResult(in, 4);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getActivity(),arrayReturnType.get(i),Toast.LENGTH_SHORT).show();
                sReturnType=arrayReturnType.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        imgBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!sTag.equalsIgnoreCase("PoVerification")) {
                    ((PoVerificationProcess) getActivity()).switchFragment(1);
                }else {
                    ((PurchaseOrderActivity) getActivity()).switchFragment(2);
                }
            }
        });
        imgBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fncClearText();
            }
        });
        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    fncCreateTotal();
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    sInventoryCode = edtInvCode.getText().toString().trim();
                    sDescription = edtDesc.getText().toString().trim();
                    sPrice = edtPrice.getText().toString().trim();
                    sFocQty = edtFocQty.getText().toString().trim();
                    sCtnQty = edtCtnQty.getText().toString().trim();
                    sLQty = edtLQty.getText().toString().trim();
                    sWnit = edtUomSize.getText().toString().trim();
                    sTotAmnt = txtTotalAmt.getText().toString();
                    Double dPrice=Double.parseDouble(sPrice);
                    if (sInventoryCode.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Inventory Code Not to Empty", Toast.LENGTH_SHORT).show();
                        edtInvCode.requestFocus();
                    } else if (sLQty.equalsIgnoreCase("") && sCtnQty.equalsIgnoreCase("") && sFocQty.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Quantity Not to Empty", Toast.LENGTH_SHORT).show();
                        edtLQty.requestFocus();
                    } else if(dPrice==0){
                        Toast.makeText(getActivity(), "Price Not to 0", Toast.LENGTH_SHORT).show();
                        edtPrice.requestFocus();
                    } else {
                        HashMap<String, String> mapData = new HashMap<String, String>();
                        Boolean isInserted = false;
                        if (OrderSummaryList.size() > 0) {
                            for (int i = 0; i < OrderSummaryList.size(); i++) {
                                mapData = OrderSummaryList.get(i);
                                if (sInventoryCode.equalsIgnoreCase(mapData.get("sInventory").trim())) {

                                    Log.d("samlog","ctnq"+sCtnQty+"lqt"+sLQty+"foc"+sFocQty);
                                    if (sCtnQty.equalsIgnoreCase("")) {
                                        sCtnQty = "0.0";
                                    }
                                    if (sLQty.equalsIgnoreCase("")) {
                                        sLQty = "0.0";
                                    }
                                    if (sFocQty.equalsIgnoreCase("")) {
                                        sFocQty = "0.0";
                                    }
                                    Double dCtnQty = 0.0, dFocQty = 0.0, dLQty = 0.0, dTotalAmnt = 0.0;

                                    if (sTag.equalsIgnoreCase("PoVerification")) {
                                        dCtnQty = Double.parseDouble(sCtnQty) ;
                                        dFocQty = Double.parseDouble(sFocQty) ;
                                        dLQty = Double.parseDouble(sLQty) ;
                                        dTotalAmnt = Double.parseDouble(sTotAmnt) ;

                                    }else {

                                        dCtnQty = Double.parseDouble(sCtnQty) + Double.parseDouble(mapData.get("sCtnQty"));
                                        dFocQty = Double.parseDouble(sFocQty) + Double.parseDouble(mapData.get("sFocQty"));
                                        dLQty = Double.parseDouble(sLQty) + Double.parseDouble(mapData.get("sLQty"));
                                        dTotalAmnt = Double.parseDouble(sTotAmnt) + Double.parseDouble(mapData.get("sTotalAmnt"));
                                    }
                                    mapData.put("sInventory", sInventoryCode);
                                    mapData.put("sDescription", sDescription);
                                    mapData.put("sPrice", sPrice);
                                    mapData.put("sCtnQty", String.format("%.2f", dCtnQty));
                                    mapData.put("sFocQty", String.format("%.2f", dFocQty));
                                    mapData.put("sLQty", String.format("%.2f", dLQty));
                                    mapData.put("sWnit", sWnit);
                                    if (!sTag.equalsIgnoreCase("PoVerification")) {
                                        mapData.put("sPoCtnQty", "0");
                                        mapData.put("sPoFocQty", "0");
                                        mapData.put("sPoLQty", "0");
                                    }
                                    mapData.put("sTotalAmnt", String.format("%.2f", dTotalAmnt));
                                    Log.e("mapData", mapData + "");
                                    OrderSummaryList.remove(i);
                                    OrderSummaryList.add(mapData);
                                    isInserted = true;
                                    Toast.makeText(getActivity(), "Item Added " + OrderSummaryList.size(), Toast.LENGTH_SHORT).show();
                                    txtTotalAmt.setText("00.00");
                                    fncClearText();
                                }
                            }
                            Log.d("samlog",OrderSummaryList.toString());
                            if (!isInserted) {
                                fncInsertOrder();
                            }
                        } else {
                            fncInsertOrder();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, arrayReturnType);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(mAdapter);
        return vw;
    }

    private void fncCreateTotal() {
        try{
            String sCQty = edtCtnQty.getText().toString();
            sWnit=edtUomSize.getText().toString().trim().equalsIgnoreCase("")?"0":edtUomSize.getText().toString().trim();
            Double dWunit=Double.parseDouble(sWnit);
            Double dQty = sCQty.equalsIgnoreCase("") ? Double.parseDouble("0.0") : Double.parseDouble(sCQty);
            Double dTot=dQty*dWunit;

            sCQty = edtLQty.getText().toString();
            dQty = sCQty.equalsIgnoreCase("") ? Double.parseDouble("0.0") : Double.parseDouble(sCQty);

            dTot+=dQty;

            String sPrice=edtPrice.getText().toString().trim();
            Double dAmnt=Double.parseDouble(sPrice);
            Double dTotalAmnt=dAmnt*dTot;
            txtTotalAmt.setText(String.format("%.2f",dTotalAmnt));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fncInsertOrder() {
        try{
            HashMap<String, String> mapData=new HashMap<>();
            if(sCtnQty.equalsIgnoreCase("")){
                sCtnQty="0.0";
            }
            if(sLQty.equalsIgnoreCase("")){
                sLQty="0.0";
            }
            if(sFocQty.equalsIgnoreCase("")){
                sFocQty="0.0";
            }
            mapData.put("sInventory", sInventoryCode);
            mapData.put("sDescription", sDescription);
            mapData.put("sPrice", sPrice);
            mapData.put("sCtnQty", sCtnQty);
            mapData.put("sFocQty", sFocQty);
            mapData.put("sLQty", sLQty);
            mapData.put("sWnit", sWnit);
            mapData.put("sPoCtnQty", "0");
            mapData.put("sPoFocQty", "0");
            mapData.put("sPoLQty", "0");
            mapData.put("sTotalAmnt", sTotAmnt);
            Log.e("mapData", mapData + "");
            OrderSummaryList.add(mapData);
            Toast.makeText(getActivity(), "Item Added " + OrderSummaryList.size(), Toast.LENGTH_SHORT).show();
            txtTotalAmt.setText("00.00");
            fncClearText();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fncGetInventoryDetails(String sInvCode, String sLocCode) {
        try{
            WsCallGetInventory wsCallGetInventory=new WsCallGetInventory();
            wsCallGetInventory.sItemCode=sInvCode;
            wsCallGetInventory.execute();
            }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fncClearText() {
        try{
            sFlag=false;
            edtInvCode.setText("");
            edtDesc.setText("");
            edtUomPcs.setText("");
            edtUomSize.setText("");
            edtPrice.setText("");
            edtCtnQty.setText("0");
            edtLQty.setText("");
            edtRetailPrice.setText("");
            edtFocQty.setText("0");
            edtInvCode.requestFocus();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class WsCallGetInventory extends AsyncTask<String,String,String>{
        String sWebMethod="fncGetInventoryDetail",sResp;

        private final ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_DARK);
        public String sItemCode;

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
                SoapConnection soapConn=new SoapConnection();
                sResp=soapConn.fncGetInventoryDetail(sItemCode, Uname,Loc_Code, sWebMethod, baseUrl);
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
                if(s.equalsIgnoreCase("false")){
                    Toast.makeText(getActivity(),"ItemCode Not Found",Toast.LENGTH_SHORT).show();
                    edtInvCode.setText("");
                    edtInvCode.requestFocus();
                    sFlag=false;
                }else {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsObj = jsonArray.getJSONObject(0);
                    HashMap<String, String> mapData = new HashMap<>();
                    sInventoryCode = jsObj.getString("InventoryCode").trim();
                    mapData.put("InventoryCode", sInventoryCode);
                    mapData.put("Description", jsObj.getString("Description").trim());
                    mapData.put("UnitCost", jsObj.getString("UnitCost").trim());
//                    mapData.put("BarCode", jsObj.getString("BarCode").trim());
                    mapData.put("UOM", jsObj.getString("UOM").trim());
                    mapData.put("Compatible", jsObj.getString("Compatible").trim());
                    mapData.put("SellingPrice", jsObj.getString("SellingPrice").trim());
                    mapData.put("RetailPrice", jsObj.getString("RetailPrice").trim());
                    mapData.put("QtyonHand", jsObj.getString("QtyonHand").trim());
                    mapData.put("WUnit", jsObj.getString("WUnit").trim());
                    if (sInventoryCode.equalsIgnoreCase("null")) {
                        Toast.makeText(getActivity(), "InventoryCode Not Found", Toast.LENGTH_SHORT).show();
                        fncClearText();
                    } else {
                        edtInvCode.setText(sInventoryCode);
                        edtDesc.setText(jsObj.getString("Description").trim());
                        edtUomPcs.setText(jsObj.getString("UOM").trim());
                        edtUomSize.setText(jsObj.getString("WUnit").trim());
                        edtPrice.setText(jsObj.getString("UnitCost").trim());
                        edtRetailPrice.setText(jsObj.getString("RetailPrice").trim());
                        edtLQty.requestFocus();
                    }
                }
                dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
                dialog.dismiss();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String CustSearch="";
        if(resultCode==0){
            Toast toast=Toast.makeText(getActivity(),"Search Canceled",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            edtInvCode.requestFocus();
        }
        else{
            Bundle bundle = data.getExtras();
            if(requestCode==4 && resultCode==-1){
                CustSearch = bundle.getString("name");
            }else if (requestCode==0 && resultCode==-1){
                CustSearch = bundle.getString("SCAN_RESULT");}
            //String[] Cusdetail=CustSearch.split("/");
            edtInvCode.setText(CustSearch);

            if (CustSearch.equalsIgnoreCase("")) {
                Toast.makeText(getActivity(), "Item Code Not Found", Toast.LENGTH_SHORT).show();
            }
            else {
                WsCallGetInventory  wsCallGetInventory=new WsCallGetInventory();
                wsCallGetInventory.sItemCode=CustSearch;
                wsCallGetInventory.execute();
            }
        }
    }

}
