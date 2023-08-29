package com.unipro.labisha.Screens.transfer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.unipro.labisha.R;
import com.unipro.labisha.Screens.login.LoginActivity;
import com.unipro.labisha.Screens.menu.MenuListActivity;
import com.unipro.labisha.adapter.TransferAdapter;
import com.unipro.labisha.server.SoapConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User1 on 24/04/2018.
 */
public class TransferOutActivity extends AppCompatActivity {
    Spinner spnOutletName,spnFromLoc,spnToLoc;
    ArrayList<HashMap<String,String>> sLocationList;
    ArrayList<String> sListLocation;
    ImageView imggrpClose;
    Button imgBtnBack,imgBtnReload,imgBtnAdd;
    private LayoutInflater mInflater;
    ArrayList<String> sFromStatusList;
    ArrayList<String> sToStatusList;
    int fromLocStatus=2,toLocationStatus=1;
    SharedPreferences sprefLogin;
    String sFromLocation="";
    String Uname,Loc_Code,TerminalCode,baseUrl;
    TextView txtFromLoc;
    ArrayList<HashMap<String,String>> sTransferList;
    ArrayList<HashMap<String,String>> sTransferInList;
    TransferAdapter mTransferAdapter;
    ListView lstTransferItems;
    SoapConnection soapConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferout);
        txtFromLoc=(TextView)findViewById(R.id.txtFromLoc);
        spnOutletName=(Spinner)findViewById(R.id.spnOutletName);
        spnFromLoc=(Spinner)findViewById(R.id.spnFromLoc);
        spnToLoc=(Spinner)findViewById(R.id.spnToLoc);
        lstTransferItems=(ListView)findViewById(R.id.lstTransferItems);
        imgBtnBack=(Button)findViewById(R.id.imgBtnBack);
        imgBtnReload=(Button)findViewById(R.id.imgBtnReload);
        imgBtnAdd=(Button)findViewById(R.id.imgBtnAdd);
        mInflater = LayoutInflater.from(this);
        sFromStatusList=new ArrayList<>();
        sToStatusList=new ArrayList<>();
        sprefLogin = this.getSharedPreferences(LoginActivity.My_pref, 0);
        Uname = sprefLogin.getString("Uname", "");
        TerminalCode=sprefLogin.getString("TerminalCode", "");
        Loc_Code=sprefLogin.getString("LocationCode", "");
        baseUrl=sprefLogin.getString("BaseUrl", "");
        soapConn=new SoapConnection();
        txtFromLoc.setText(Loc_Code);
        sTransferList=new ArrayList<>();
        fncLoadLocationList();
        fncUpdateTransferStatus();
        spnOutletName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String sOutletName = sLocationList.get(position).get("LocationCode");
                if (position == 0) {
                    sFromLocation = "";
                } else {
                    sFromLocation = sOutletName;
                }
//                fncUpdateTransferStatus();
                if(sTransferList.size()>0){
                    fncSyncTransferItems(sFromLocation.trim(), fromLocStatus, toLocationStatus);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnFromLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    fromLocStatus = 2;
                } else {
                    fromLocStatus = 3;
                }
//                fncUpdateTransferStatus();
                if(sTransferList.size()>0){
                    fncSyncTransferItems(sFromLocation.trim(), fromLocStatus, toLocationStatus);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnToLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    toLocationStatus=1;
                }else {
                    toLocationStatus=2;
                }
//                fncUpdateTransferStatus();
                if(sTransferList.size()>0){
                    fncSyncTransferItems(sFromLocation.trim(), fromLocStatus, toLocationStatus);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        imggrpClose=(ImageView)findViewById(R.id.grpClose);
        imggrpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), MenuListActivity.class);
                startActivity(in);
                finishAffinity();
            }
        });
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        imgBtnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fncUpdateTransferStatus();
            }
        });
        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(getApplicationContext(), TransferOutInventory.class);
                in.putExtra("sListLocation",sListLocation);
                in.putExtra("sLocationList",sLocationList);
                startActivityForResult(in, 5);
            }
        });
        lstTransferItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        sFromStatusList.add("Transfered");
        sFromStatusList.add("Revoked");
        sFromStatusList.add("Partialy Revoked");
        ArrayAdapter<String> fromArray = new ArrayAdapter<String>(TransferOutActivity.this, android.R.layout.simple_spinner_dropdown_item, sFromStatusList);
        fromArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFromLoc.setAdapter(fromArray);

        sToStatusList.add("Not Received");
        sToStatusList.add("Accepted");
        sToStatusList.add("Checking");
        sToStatusList.add("Partialy Accepted");
        ArrayAdapter<String> ToArray = new ArrayAdapter<String>(TransferOutActivity.this, android.R.layout.simple_spinner_dropdown_item, sToStatusList);
        ToArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnToLoc.setAdapter(ToArray);
    }

    private void fncSyncTransferItems(String sToLocation, int fromLocStatus, int toLocationStatus) {
        String sQry="";
        sTransferInList=new ArrayList<>();
        Log.d("sambath",sToLocation+" -- "+fromLocStatus+" -- "+toLocationStatus);

        try{
            if(sToLocation.equalsIgnoreCase("")) {
                for(int i=0;i<sTransferList.size();i++){
                    HashMap<String,String> mapData=sTransferList.get(i);
                    if(toLocationStatus==1) {
                        if(mapData.get("FromLocationStatus").trim().equalsIgnoreCase(String.valueOf(fromLocStatus)) && mapData.get("ToLocationStatus").trim().equalsIgnoreCase("0") ||mapData.get("ToLocationStatus").trim().equalsIgnoreCase("1")){
                            sTransferInList.add(mapData);
                        }
                    }else {
                        if(mapData.get("FromLocationStatus").trim().equalsIgnoreCase(String.valueOf(fromLocStatus)) && mapData.get("ToLocationStatus").trim().equalsIgnoreCase(String.valueOf(toLocationStatus))){
                            sTransferInList.add(mapData);
                        }
                    }
                }
            }else {
                for(int i=0;i<sTransferList.size();i++){
                    HashMap<String,String> mapData=sTransferList.get(i);
                    Log.d("sambath",mapData.get("ToLocation") +" --compare--"+sToLocation);
                    if(mapData.get("ToLocation").equalsIgnoreCase(sToLocation)) {
                        if (toLocationStatus == 1) {
                            if (mapData.get("FromLocationStatus").trim().equalsIgnoreCase(String.valueOf(fromLocStatus)) && mapData.get("ToLocationStatus").trim().equalsIgnoreCase("0") || mapData.get("ToLocationStatus").trim().equalsIgnoreCase("1")) {
                                sTransferInList.add(mapData);
                            }
                        } else {
                            if (mapData.get("FromLocationStatus").trim().equalsIgnoreCase(String.valueOf(fromLocStatus)) && mapData.get("ToLocationStatus").trim().equalsIgnoreCase(String.valueOf(toLocationStatus))) {
                                sTransferInList.add(mapData);
                            }
                        }
                    }

                }
            }
            mTransferAdapter=new TransferAdapter(getApplicationContext());
            mTransferAdapter.setData(sTransferInList);
            lstTransferItems.setAdapter(mTransferAdapter);
            setListViewHeightBasedOnChildren(lstTransferItems);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fncLoadLocationList() {
        WsCallLocationList wsCallLocationList=new WsCallLocationList();
        wsCallLocationList.execute();
    }
    private void fncUpdateTransferStatus() {
        WsCallTransferStatus wsCallTransferStatus=new WsCallTransferStatus();
        wsCallTransferStatus.execute();
    }
    private class WsCallLocationList extends AsyncTask<String,String,String> {
        String sMethod="fncGetLocationList",sResp="";
        private final ProgressDialog dialog = new ProgressDialog(TransferOutActivity.this, ProgressDialog.THEME_HOLO_DARK);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Updating FromServer...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                sLocationList=new ArrayList<>();
                sListLocation=new ArrayList<>();
                HashMap<String,String> mapData;
                mapData=new HashMap<>();
                mapData.put("LocationCode","All");
                mapData.put("LocationName", "All");
               // sListLocation.add("All" + " - " + "All");
                sListLocation.add("All");
                sLocationList.add(mapData);
                sResp=soapConn.fncGetLocationList(Uname,Loc_Code,sMethod,baseUrl);
                if(!sResp.equalsIgnoreCase("false")){
                    JSONArray jsonArray=new JSONArray(sResp);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsObj=jsonArray.getJSONObject(i);
                        mapData=new HashMap<>();
                        mapData.put("LocationCode",jsObj.getString("LocationCode"));
                        mapData.put("LocationName", jsObj.getString("LocationName"));
                    //    sListLocation.add(jsObj.getString("LocationCode").trim()+ " - " + jsObj.getString("LocationName").trim());

                        if(!jsObj.getString("LocationCode").trim().equalsIgnoreCase(Loc_Code)) {
                            sListLocation.add(jsObj.getString("LocationCode").trim());
                            sLocationList.add(mapData);
                        }
                    }
                }
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
                if (sListLocation.size() > 0){
                    ArrayAdapter<String> sectionArray = new ArrayAdapter<String>(TransferOutActivity.this, android.R.layout.simple_spinner_dropdown_item, sListLocation);
                    sectionArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnOutletName.setAdapter(sectionArray);
                    for(int i=0;i<sLocationList.size();i++){
                        if(sLocationList.get(i).get("LocationCode").equalsIgnoreCase(Loc_Code)){
                            spnOutletName.setSelection(i);
                            spnOutletName.setEnabled(false);
                        }
                    }
                }

                dialog.dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private String fncFormatNullString(String sData){
        if(sData==null){
            return "";
        }else {
            return sData;
        }
    }

    private class WsCallTransferStatus extends AsyncTask<String,String,String>{
        String sMethod="fncGetTransferOutList",sResp="";
        private final ProgressDialog dialog = new ProgressDialog(TransferOutActivity.this, ProgressDialog.THEME_HOLO_DARK);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCanceledOnTouchOutside(false);
            this.dialog.setMessage("Updating FromServer...");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                sResp=soapConn.fncGetTransferOutList(Loc_Code, Uname, sMethod,baseUrl);
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
                sTransferList=new ArrayList<>();
                JSONArray jsArray=new JSONArray(s);
                JSONObject jsObj=null;
                for(int i=0;i<jsArray.length();i++){
                    jsObj=jsArray.getJSONObject(i);
                    HashMap<String,String>mapData=new HashMap<>();
                    mapData.put("TransferNo", fncFormatNullString(jsObj.getString("TransferNo")).trim());
                    mapData.put("TransferDate", fncFormatNullString(jsObj.getString("TransferDate")).trim());
                    mapData.put("FromLocation", fncFormatNullString(jsObj.getString("FromLocation")).trim());
                    mapData.put("FromLocationStatus", fncFormatNullString(jsObj.getString("FromLocationStatus")).trim());
                    mapData.put("ToLocation", fncFormatNullString(jsObj.getString("ToLocation")).trim());
                    mapData.put("ToLocationStatus", fncFormatNullString(jsObj.getString("ToLocationStatus")).trim());
                    mapData.put("ToLocationUser", fncFormatNullString(jsObj.getString("ToLocationUser")).trim());
                    mapData.put("ToLocationDate", fncFormatNullString(jsObj.getString("ToLocationDate")).trim());
                    mapData.put("Remarks", fncFormatNullString(jsObj.getString("Remarks")).trim());
                    mapData.put("CreateUser", fncFormatNullString(jsObj.getString("CreateUser")).trim());
                    mapData.put("CreateDate", fncFormatNullString(jsObj.getString("CreateDate")).trim());
                    mapData.put("ModifyUser", fncFormatNullString(jsObj.getString("ModifyUser")).trim());
                    mapData.put("ModifyDate", fncFormatNullString(jsObj.getString("ModifyDate")).trim());
                    mapData.put("Sent", fncFormatNullString(jsObj.getString("Sent")).trim());
                    Log.e("Mapdata", mapData + "");
                    sTransferList.add(mapData);
                }
                mTransferAdapter=new TransferAdapter(getApplicationContext());
                mTransferAdapter.setData(sTransferList);
                lstTransferItems.setAdapter(mTransferAdapter);
                setListViewHeightBasedOnChildren(lstTransferItems);
            }catch (Exception e){
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RelativeLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
