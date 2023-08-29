package com.unipro.labisha.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.image.ImageLoader;
import com.sewoo.jpos.image.MobileImageConverter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by User1 on 11/04/2017.
 */
public class SalesPrintByte {
    Context mContext;
    private OutputStream os;
    public static final byte LF = 10;

    public SalesPrintByte(Context applicationContext, OutputStream os) {
        this.mContext=applicationContext;
        this.os=os;
    }
    //fncPrintInvoice
    public Boolean fncPrint_sales_48(String sData,String sBitmap,String loc_Code,Boolean isSalesInvoice,Boolean isReprint){
        Boolean printSuccess=false;
        try {
            String sSql = "";
            String sDetail="";
            String sSalesGSTPerc;
            Boolean isGstSales=false;
            int countrsHDR = 0;
            ESC_POS ec_pos = new ESC_POS();
            JSONArray jsonArray=new JSONArray(sData);
            JSONArray jsonArrayDetail=null;
            JSONObject jobj=null;
            JSONObject jobjDetail;
            if (jsonArray.length()>0) {
                countrsHDR = 1;
                jobj=jsonArray.getJSONObject(0);
                sDetail=jobj.getString("sDetail");
                jsonArrayDetail=new JSONArray(sDetail);
            }
            int countrsDET = 0;
            if (jsonArrayDetail.length()>0) {
                countrsDET = jsonArrayDetail.length();
            }

            String sBorderLine = "------------------------------------------------";
            String sSubBorderLine = "--------------------------";
            String sDottedLine = ".......................................";
            if (jsonArray.length()>0) {
                String sCompanyName = jobj.getString(("Name")).trim();
                String sCompanyAddress1 = jobj.getString(("Address1")).trim();
                String sCompanyAddress2 = jobj.getString(("Address2")).trim();
                String sCompanyCountry = jobj.getString(("Country")).trim();
                String sCompanyPostalCode = jobj.getString(("PostalCode")).trim();
                String sCompanyPhone = jobj.getString(("Phone"));
                String sCompanyFax = jobj.getString(("Fax"));
                String sTaxRegNo=jobj.getString("GSTRegNo").trim();
                if(sTaxRegNo.equalsIgnoreCase("")){
                    isGstSales=false;
                }else {
                    isGstSales=true;
                }
                String sCustCode="";
                String textPrint = "";
                /*String sPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/RetailPOSSFA/Header.jpg";
                printBitmap(sPath, 1, 0, 0);*/
                if(sBitmap.equalsIgnoreCase("")){
                    textPrint = "\r\n";
                }else {
                    printBitmap(sBitmap, 1, 0);
                }
                os.write(ec_pos.select_fontA());
                if (isReprint) {
                    textPrint = fncGetWhiteSpaces(11) + "Duplicate Copy" + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.double_height_on());
                sCompanyName = fncGetWhiteSpaces(11) + sCompanyName + "\r\n";
                os.write(sCompanyName.getBytes());
                os.write(ec_pos.double_height_off());
                if(!isNullOrEmpty(sCompanyAddress1)) {
                    sCompanyAddress1 = fncGetWhiteSpaces(11) + sCompanyAddress1 + "\r\n";
                    os.write(sCompanyAddress1.getBytes());
                }
                if(!isNullOrEmpty(sCompanyAddress2)){
                    sCompanyAddress2 = fncGetWhiteSpaces(11) + sCompanyAddress2 + "\r\n";
                    os.write(sCompanyAddress2.getBytes());
                }
                /*if(!isNullOrEmpty(sState)){
                    String sText="";
                    sText=fncGetWhiteSpaces(11)+ sState.trim();
                    if(!isNullOrEmpty(sCompanyPostalCode)){
                        sText+=","+sCompanyPostalCode.trim();
                    }
                    sText+="\r\n";
                    os.write(sText.getBytes());
                }*/
//                    textPrint = fncGetWhiteSpaces(11) + sCompanyCountry + "-" + sCompanyPostalCode + "\r\n";


                if (!isNullOrEmpty(sCompanyPhone)) {
                    String sContact = "";
                    sContact = fncGetWhiteSpaces(11) + "Tel : " + sCompanyPhone.trim();
                    if (!isNullOrEmpty(sCompanyFax)) {
                        sContact = sContact + "  Fax : " + sCompanyFax.trim();
                    }
                    if(sContact.length()>48){
                        textPrint = fncGetWhiteSpaces(11) +"Tel : " + sCompanyPhone.trim()+"\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = fncGetWhiteSpaces(11) +"Fax : " + sCompanyFax.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }else {
                        textPrint = sContact + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                } else if (!isNullOrEmpty(sCompanyFax)) {
                    textPrint =fncGetWhiteSpaces(11) + "Fax : " + sCompanyFax.trim() + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(true));
                if(isGstSales) {
                    textPrint = fncGetWhiteSpaces(11) + "TRN    : " + sTaxRegNo + "\r\n";
                }else {
                    textPrint = fncGetWhiteSpaces(11) + "TRN    : " + sTaxRegNo + "\r\n";
                }
                os.write(textPrint.getBytes());
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                if (isSalesInvoice) {
                    if(isGstSales) {
                        textPrint = fncGetWhiteSpaces(16) + "TAX INVOICE" + "\r\n";
                    }else {
                        textPrint = fncGetWhiteSpaces(16) + "TAX INVOICE" + "\r\n";
                    }
                    os.write(textPrint.getBytes());
                } else {
                    textPrint = fncGetWhiteSpaces(16) + "SALES ORDER" + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(false));
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                Double dTotalFocQty = 0.00, dTotalWQty  = 0.00, dTotalLQty = 0.0, dTotalAmount = 0.0;
                if (countrsHDR > 0 && countrsDET > 0) {
                    if (isSalesInvoice) {
                        textPrint = "Invoice No     : " + jobj.getString("SalesInvoiceNo").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    } else {
                        textPrint = "SalesOrder  No : " + jobj.getString("sSoNo").trim() + "\r\n";
                        os.write(textPrint.getBytes());

                    }
                    textPrint = "Date           : " + jobj.getString("BillingDate") + " " + jobj.getString("BillingTime") + "\r\n";
                    os.write(textPrint.getBytes());

                    if (!isNullOrEmpty(jobj.getString("sCustomerCode"))) {
                        textPrint = "Cust Code      : " + jobj.getString("sCustomerCode").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                        sCustCode=jobj.getString("sCustomerCode");
                    }
                    os.write(ec_pos.setBold(true));
                    if (!isNullOrEmpty(jobj.getString("CustomerName"))) {
                        if(jobj.getString("CustomerName").trim().length()>48){
                            textPrint=jobj.getString("CustomerName").trim().substring(0,47)+"\r\n";
                        }else {
                            textPrint = jobj.getString("CustomerName").trim() + "\r\n";
                        }
                        os.write(textPrint.getBytes());
                    }
                    os.write(ec_pos.setBold(false));
                    if (!isNullOrEmpty(jobj.getString("CustAddress1"))) {
                        if(jobj.getString("CustAddress1").trim().length()>48){
                            textPrint = jobj.getString("CustAddress1").trim().substring(0,47)+ "\r\n";
                        }else {
                            textPrint = jobj.getString("CustAddress1").trim() + "\r\n";
                        }
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("CustAddress2"))) {
                        if(jobj.getString("CustAddress2").trim().length()>48){
                            textPrint = jobj.getString("CustAddress2").trim().substring(0,47)+ "\r\n";
                        }else {
                            textPrint = jobj.getString("CustAddress2").trim() + "\r\n";
                        }
                        os.write(textPrint.getBytes());
                    }

                    if (!isNullOrEmpty(jobj.getString("CustPhone").trim())) {
                        String sContact = "";
                        sContact = "Phone : " + jobj.getString("CustPhone").trim();
                        if (!isNullOrEmpty(jobj.getString("CustFax"))) {
                            sContact = sContact + "  Fax : " + jobj.getString("CustFax").trim();
                        }
                        if(sContact.length()>48){
                            textPrint = "Phone : " + jobj.getString("CustPhone").trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        }else {
                            textPrint = sContact + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    } else if (!isNullOrEmpty(jobj.getString("CustFax").trim())) {
                        textPrint = "Fax   : " + jobj.getString("CustFax").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    if(!isNullOrEmpty(jobj.getString("GSTRegNo").trim())){
                        textPrint = "TRN   : " + jobj.getString("GSTRegNo").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    if (isSalesInvoice) {//CreatedBy
                        if (!isNullOrEmpty(jobj.getString("SalesManID"))) {
                            textPrint = "SR    : " + jobj.getString("SalesManID").trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    } else {
                        if (!isNullOrEmpty(jobj.getString("SalesManID"))) {
                            textPrint = "SR    : " +jobj.getString("SalesManID").trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = " No Desc                 Qty/FOC  Price     Amt " + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
//                    int rsPosition = 0;
                    for(int i=0;i<countrsDET;i++) {
                        jobjDetail=jsonArrayDetail.getJSONObject(i);
                        os.write(ec_pos.select_fontB());
                        String sDescription = jobjDetail.getString("sDescription").trim();
                        String sShortDesc="";
                        if (sDescription.length() > 25) {
                            sDescription = sDescription.substring(0, 24);
                            /*sSql="Select ShortDescription from tblinventory where InventoryCode="+fncFormatSQLText(jobjDetail.getString("InventoryCode"));
                            ResultSet rsInv=stInv.executeQuery(sSql);
                            if(rsInv.next()){
                                sShortDesc=rsInv.getString(1);
//                                    sDescription=sShortDesc;
                                if(sShortDesc.length()>25){
                                    sDescription=sShortDesc.substring(0,25);
                                }else if(sShortDesc.equalsIgnoreCase("")){
                                    sDescription = sDescription.substring(0, 25);
                                }else {
                                    sDescription=sShortDesc;
                                }
                            }*/
                        }
                        if (i<= 8) {
                            textPrint = "  " + (i+1) + "  " + sDescription.trim();
                        } else {
                            textPrint = " " + (i+1) + "  " + sDescription.trim();
                        }
                        Double dQty = 0.0,FOCQty=0.0,WQty=0.0;
                        dQty = jobjDetail.getDouble("sLQty");
                        FOCQty=jobjDetail.getDouble("sFocQty");
                        WQty=jobjDetail.getDouble("sCtnQty");
                        dTotalLQty+=dQty;
                        dTotalFocQty+=FOCQty;
                        dTotalWQty+=WQty;
                        Double dPrice = 0.0,dDiscountAmount=0.0,dBillDiscountAmount=0.0;
                        dPrice = jobjDetail.getDouble("sPrice");
                        dDiscountAmount=jobjDetail.getDouble("sDiscount");
//                        dBillDiscountAmount=jobjDetail.getDouble("BillDiscountAmount");

                        Double dTotalValue = jobjDetail.getDouble("sTotalAmnt");
                        dTotalAmount += dTotalValue;
                        dTotalValue+=dBillDiscountAmount;
//                        sSalesGSTPerc = jobjDetail.getString("GSTPerc");
                       /* String sSalesGSTType = rsDET.getString("TaxType").trim();
                        if (sSalesGSTType.length() > 4) {
                            sSalesGSTType = sSalesGSTType.substring(1, 4);
                        }*/
                        String sTotalQty=String.format("%.0f", WQty)+"/"+String.format("%.0f", dQty)+"/"+String.format("%.0f", FOCQty);
                        textPrint += fncGetWhiteSpaces(31 - textPrint.length());
                        textPrint += fncGetWhiteSpaces(11 - sTotalQty.length()) + sTotalQty;
                        textPrint += fncGetWhiteSpaces(10 - fncFormatSeperator(dPrice).length()) + fncFormatSeperator(dPrice);
                        textPrint += fncGetWhiteSpaces(11 - fncFormatSeperator(dTotalValue).length()) + fncFormatSeperator(dTotalValue)+ "\r\n";
//                        textPrint += fncGetWhiteSpaces(6 - sSalesGSTType.length()) + sSalesGSTType;
                        os.write(textPrint.getBytes());
                        if(dDiscountAmount>0){
                            textPrint = fncGetWhiteSpaces(43)+"Discount";
                            textPrint += fncGetWhiteSpaces(11 - fncFormatSeperator(dDiscountAmount).length()) +"-"+fncFormatSeperator(dDiscountAmount)+ "\r\n";
                            os.write(textPrint.getBytes());
                        }

                       /* if (dSalesGSTPerc > 0) {
                            dsGSTTotal = dsGSTTotal + rsDET.getDouble("TotalValue");
                        } else {
                            dzGSTTotal = dzGSTTotal + rsDET.getDouble("TotalValue");
                        }*/
                    }
                    os.write(ec_pos.select_fontA());
                    textPrint = fncGetWhiteSpaces(22) + sSubBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                 /*   textPrint=fncGetWhiteSpaces(16-String.format("%.2f",dTotalQty).length())+String.format("%.2f",dTotalQty);
                    textPrint+=fncGetWhiteSpaces(18-String.format("%.2f",dTotalAmount).length())+String.format("%.2f",dTotalAmount)+"\r\n";
                    os.write(textPrint.getBytes());
                    textPrint=sBorderLine+"\r\n";
                    os.write(textPrint.getBytes());*/
                    Double dGst=0.0,dsumSubTotal=0.0,dsumNetTotal=0.0;

                    if (dTotalLQty>0) {
                        String sTotLQty = String.format("%.0f", dTotalLQty);
                        String sTotWQty = String.format("%.0f", dTotalWQty);
                        String sTotFocQty = String.format("%.0f", dTotalFocQty);
                        String sTotAmount = jobj.getString("sOSubTotal");
                        String sTotalQty=sTotWQty+"/"+sTotLQty+"/"+sTotFocQty;

                        dsumSubTotal = Double.parseDouble(sTotAmount);

                        textPrint = fncGetWhiteSpaces(32 - sTotalQty.length()) + sTotalQty + fncGetWhiteSpaces(16 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());



                    String sBillDisc=String.format("%.2f", jobj.getDouble("sBillDisc"));

//                    Double ,dsumNetTotal=0.0,dGst=0.0;
//                    dGst=Double.parseDouble(jobj.getString("Gst"));
                    dsumNetTotal=dsumSubTotal+dGst;
                    dsumNetTotal=dsumNetTotal-Double.parseDouble(sBillDisc);
                    if(dGst>0) {
                        textPrint = "TOTAL EXCL. TAX   : " + fncGetWhiteSpaces(28 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal) + "\r\n";
                        os.write(textPrint.getBytes());

                        textPrint = "Bill Discount     : " + fncGetWhiteSpaces(28 - sBillDisc.length()) + sBillDisc + "\r\n";
                        os.write(textPrint.getBytes());

                        Double dHDRGST = jobj.getDouble("Gst");
//                        String sHdrGst = String.format("%.2f", dHDRGST);
                        String sHdrGst = fncFormatSeperator(dHDRGST);
                        textPrint = "VAT (5%)          : ";
                        textPrint += fncGetWhiteSpaces(28 - fncFormatSeperator(dGst).length()) + fncFormatSeperator(dGst) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_on());
                        textPrint = "NET PAY INCL. TAX : " + fncGetWhiteSpaces(28 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());
                    }else {
                        textPrint = "Bill Discount     : " + fncGetWhiteSpaces(28 - sBillDisc.length()) + sBillDisc + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_on());
                        textPrint = "NET PAYABLE       : " + fncGetWhiteSpaces(28 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());

//                    textPrint = "Customer Signature & Stamp" + "\r\n";
//                    os.write(textPrint.getBytes());
//                    sPath= Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/RetailPOSSFA/Footer.jpg";
//                    printBitmap(sPath, 0, 0, 0);
                    textPrint = "";
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                        /*textPrint = "\r\n";
                        os.write(textPrint.getBytes());*/
                    textPrint = sBorderLine+"\r\n";
                    os.write(textPrint.getBytes());
//                        sSql = "select * from tblReceiptPrintMessage order by MessageCode asc";
                 /*   sSql="select * from tblReceiptPrintMessage where MessageType='Receipt' order by MessageCode asc";
                    ResultSet rsPrintmsg = stmtCmpny.executeQuery(sSql);
                    if (rsPrintmsg.next()) {
                        int i=1;
                        do {
                            String sMsg=rsPrintmsg.getString(2).trim();
                            if(!isNullOrEmpty(sMsg)) {
                                if (sMsg.contains("~")) {
                                    String[] msgArray = sMsg.split("~");
                                    sMsg = "";
                                    for (int k = 0; k < msgArray.length; k++) {
                                        sMsg += msgArray[k] + "\r\n";
                                    }
                                    sPrintMsg += i + " " + sMsg;
                                } else {
                                    sPrintMsg += i + " " + sMsg + "\r\n";
                                }
                            }
                            i++;
                        } while (rsPrintmsg.next());
                    }*/
                    /*os.write(ec_pos.select_fontB());
                    if (!isNullOrEmpty(sPrintMsg)) {
                        textPrint = sPrintMsg.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    os.write(ec_pos.select_fontA());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());*/
                    textPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());

                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("***Print Exception", ex.toString());
        }
        return printSuccess;
    }

    public Boolean fncPrint_sales_70(String sData,String sBitmap,String loc_Code,Boolean isSalesInvoice,Boolean isReprint){
        Boolean printSuccess=false;
        try {
            String sSql = "";
            String sDetail="";
            String sPrintMsg = "";
            String sSalesGSTPerc="0.0";
            Boolean isGstSales=false;
            int countrsHDR = 0;
            ESC_POS ec_pos = new ESC_POS();
            JSONArray jsonArray=new JSONArray(sData);
            JSONArray jsonArrayDetail=null;
            JSONObject jobj=null;
            JSONObject jobjDetail;
            if (jsonArray.length()>0) {
                countrsHDR = 1;
                jobj=jsonArray.getJSONObject(0);
                sDetail=jobj.getString("sDetailStr");
                jsonArrayDetail=new JSONArray(sDetail);
            }
            int countrsDET = 0;
            if (jsonArrayDetail.length()>0) {
                countrsDET = jsonArrayDetail.length();
            }

            String sBorderLine = "---------------------------------------------------------------------";
            String sSubBorderLine = "---------------------------------------";
            if (jsonArray.length()>0) {
                String sCompanyName = jobj.getString(("ComName")).trim();
                String sCompanyAddress1 = jobj.getString(("ComAddr1")).trim();
                String sCompanyAddress2 = jobj.getString(("ComAddr2")).trim();
                String sCompanyCountry = jobj.getString(("ComCountry")).trim();
                String sCompanyPostalCode = jobj.getString(("ComPostalCode")).trim();
                String sCompanyPhone = jobj.getString(("ComPhone"));
                String sCompanyFax = jobj.getString(("ComFax"));
//                String sState=jobj.getString(("State"));
                String sCompanyGSTRegNo = jobj.getString(("TaxRegNo")).trim();
                String sBizRegNo=jobj.getString("BizRegNo").trim();
                sCompanyGSTRegNo="";
                if(sCompanyGSTRegNo.equalsIgnoreCase("")){
                    isGstSales=false;
                }else {
                    isGstSales=true;
                }
                String textPrint = "";
                /*String sPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/RetailPOSSFA/Header.jpg";
                printBitmap(sPath, 1, 0, 0);*/
                if(sBitmap.equalsIgnoreCase("")){
                    textPrint = "\r\n";
                }else {
                    printBitmap(sBitmap, 1, 0);
                }
                os.write(ec_pos.select_fontA());
                if (isReprint) {
                    textPrint = fncGetWhiteSpaces(22) + "Duplicate Copy" + "\r\n";
                    os.write(textPrint.getBytes());
                }
                sCompanyName = fncGetWhiteSpaces(22) + sCompanyName + "\r\n";
                os.write(sCompanyName.getBytes());
                if(!isNullOrEmpty(sCompanyAddress1)) {
                    sCompanyAddress1 = fncGetWhiteSpaces(22) + sCompanyAddress1 + "\r\n";
                    os.write(sCompanyAddress1.getBytes());
                }
                if(!isNullOrEmpty(sCompanyAddress2)){
                    sCompanyAddress2 = fncGetWhiteSpaces(22) + sCompanyAddress2 + "\r\n";
                    os.write(sCompanyAddress2.getBytes());
                }
                /*if(!isNullOrEmpty(sState)){
                    String sText="";
                    sText=fncGetWhiteSpaces(11)+ sState.trim();
                    if(!isNullOrEmpty(sCompanyPostalCode)){
                        sText+=","+sCompanyPostalCode.trim();
                    }
                    sText+="\r\n";
                    os.write(sText.getBytes());
                }*/
//                    textPrint = fncGetWhiteSpaces(11) + sCompanyCountry + "-" + sCompanyPostalCode + "\r\n";


                if (!isNullOrEmpty(sCompanyPhone)) {
                    String sContact = "";
                    sContact = fncGetWhiteSpaces(22) + "Tel : " + sCompanyPhone.trim();
                    if (!isNullOrEmpty(sCompanyFax)) {
                        sContact = sContact + "  Fax : " + sCompanyFax.trim();
                    }
                    if(sContact.length()>70){
                        textPrint = fncGetWhiteSpaces(22) +"Tel : " + sCompanyPhone.trim()+"\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = fncGetWhiteSpaces(22) +"Fax : " + sCompanyFax.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }else {
                        textPrint = sContact + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                } else if (!isNullOrEmpty(sCompanyFax)) {
                    textPrint =fncGetWhiteSpaces(22) + "Fax : " + sCompanyFax.trim() + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(true));
                if(isGstSales) {
                    textPrint = fncGetWhiteSpaces(22) + "GST ID : " + sCompanyGSTRegNo + "\r\n";
                }else {
                    textPrint = fncGetWhiteSpaces(22) + "BRN    : " + sBizRegNo + "\r\n";
                }
                os.write(textPrint.getBytes());
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                if (isSalesInvoice) {
                    if(isGstSales) {
                        textPrint = fncGetWhiteSpaces(27) + "TAX INVOICE" + "\r\n";
                    }else {
                        textPrint = fncGetWhiteSpaces(27) + "INVOICE" + "\r\n";
                    }
                    os.write(textPrint.getBytes());
                } else {
                    textPrint = fncGetWhiteSpaces(27) + "SALES ORDER" + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(false));
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                Double dTotalFocQty = 0.00, dTotalWQty  = 0.00, dTotalLQty = 0.0, dTotalAmount = 0.0;
                if (countrsHDR > 0 && countrsDET > 0) {
                    if (isSalesInvoice) {
                        textPrint = "Invoice No     : " + jobj.getString("SalesInvoiceNo") + "\r\n";
                        os.write(textPrint.getBytes());
                    } else {
                        textPrint = "SalesOrder  No : " + jobj.getString("SalesInvoiceNo") + "\r\n";
                        os.write(textPrint.getBytes());

                    }
                    textPrint = "Date           : " + jobj.getString("BillingDate") + " " + jobj.getString("BillingTime") + "\r\n";
                    os.write(textPrint.getBytes());

                    if (!isNullOrEmpty(jobj.getString("CustomerCode"))) {
                        textPrint = "Cust Code      : " + jobj.getString("CustomerCode").trim() + "\r\n";
                        os.write(textPrint.getBytes());
//                        sCustCode=jobj.getString("CustomerCode");
                    }

                    if (!isNullOrEmpty(jobj.getString("CustomerName"))) {
                        if(jobj.getString("CustomerName").trim().length()>69){
                            textPrint=jobj.getString("CustomerName").trim().substring(0,69)+"\r\n";
                        }else {
                            textPrint = jobj.getString("CustomerName").trim() + "\r\n";
                        }
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("Address1"))) {
                        if(jobj.getString("Address1").trim().length()>69){
                            textPrint = jobj.getString("Address1").trim().substring(0,69)+ "\r\n";
                        }else {
                            textPrint = jobj.getString("Address1").trim() + "\r\n";
                        }
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("Address2"))) {
                        if(jobj.getString("Address2").trim().length()>69){
                            textPrint = jobj.getString("Address2").trim().substring(0,69)+ "\r\n";
                        }else {
                            textPrint = jobj.getString("Address2").trim() + "\r\n";
                        }
                        os.write(textPrint.getBytes());
                    }
                        /*if (!isNullOrEmpty(rsHDR.getString("Address3"))) {
                            if(rsHDR.getString("Address3").trim().length()>48){
                                textPrint=rsHDR.getString("Address3").trim().substring(0,47)+"\r\n";
                            }else {
                                textPrint = rsHDR.getString("Address3").trim() + "\r\n";
                            }
                            os.write(textPrint.getBytes());
                        }*/

                    if (!isNullOrEmpty(jobj.getString("Phone").trim())) {
                        String sContact = "";
                        sContact = "Phone : " + jobj.getString("Phone").trim();
                        if (!isNullOrEmpty(jobj.getString("Fax"))) {
                            sContact = sContact + "  Fax : " + jobj.getString("Fax").trim();
                        }
                        if(sContact.length()>69){
                            textPrint = "Phone : " + jobj.getString("Phone").trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        }else {
                            textPrint = sContact + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    } else if (!isNullOrEmpty(jobj.getString("Fax").trim())) {
                        textPrint = "Fax   : " + jobj.getString("Fax").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    if (isSalesInvoice) {//CreatedBy
                        if (!isNullOrEmpty(jobj.getString("CreateUser"))) {
                            textPrint = "SR    : " + jobj.getString("CreateUser").trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    } else {
                        if (!isNullOrEmpty(jobj.getString("CreateUser"))) {
                            textPrint = "SR    : " +jobj.getString("CreateUser").trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = " No  Description                        Qty/FOC      Price     Amount" + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
//                    int rsPosition = 0;
                    for(int i=0;i<countrsDET;i++) {
                        jobjDetail=jsonArrayDetail.getJSONObject(i);
                        String sDescription = jobjDetail.getString("Description").trim();
                        String sShortDesc="";
                        if (sDescription.length() > 35) {
                            sDescription = sDescription.substring(0, 34);
                        }
                        if (i<= 8) {
                            textPrint = "  " + (i+1) + "  " + sDescription.trim();
                        } else {
                            textPrint = " " + (i+1) + "  " + sDescription.trim();
                        }
                        Double dQty = 0.0,FOCQty=0.0,WQty=0.0;
                        dQty = jobjDetail.getDouble("LQty");
                        FOCQty=jobjDetail.getDouble("FOCQty");
                        WQty=jobjDetail.getDouble("WQty");
                        dTotalLQty+=dQty;
                        dTotalFocQty+=FOCQty;
                        dTotalWQty+=WQty;
                        Double dPrice = 0.0,dDiscountAmount=0.0,dBillDiscountAmount=0.0;
                        dPrice = jobjDetail.getDouble("UnitCost");
                        dDiscountAmount=jobjDetail.getDouble("DiscountAmount");
                        dBillDiscountAmount=jobjDetail.getDouble("BillDiscountAmount");

                        Double dTotalValue = jobjDetail.getDouble("TotalValue");
                        dTotalAmount += dTotalValue;
                        dTotalValue+=dBillDiscountAmount;
                        sSalesGSTPerc = jobjDetail.getString("GSTPerc");
                       /* String sSalesGSTType = rsDET.getString("TaxType").trim();
                        if (sSalesGSTType.length() > 4) {
                            sSalesGSTType = sSalesGSTType.substring(1, 4);
                        }*/
                        String sTotalQty=String.format("%.0f", WQty)+"/"+String.format("%.0f", dQty)+"/"+String.format("%.0f", FOCQty);
                        textPrint += fncGetWhiteSpaces(40 - textPrint.length());
                        textPrint += fncGetWhiteSpaces(7 - sTotalQty.length()) + sTotalQty;
                        textPrint += fncGetWhiteSpaces(11 - fncFormatSeperator(dPrice).length()) + fncFormatSeperator(dPrice);
                        textPrint += fncGetWhiteSpaces(11 - fncFormatSeperator(dTotalValue).length()) + fncFormatSeperator(dTotalValue)+ "\r\n";
//                        textPrint += fncGetWhiteSpaces(6 - sSalesGSTType.length()) + sSalesGSTType;
                        os.write(textPrint.getBytes());
                        if(dDiscountAmount>0){
                            textPrint = fncGetWhiteSpaces(49)+"Discount";
                            textPrint += fncGetWhiteSpaces(12 - fncFormatSeperator(dDiscountAmount).length()) +"-"+fncFormatSeperator(dDiscountAmount)+ "\r\n";
                            os.write(textPrint.getBytes());
                        }

                       /* if (dSalesGSTPerc > 0) {
                            dsGSTTotal = dsGSTTotal + rsDET.getDouble("TotalValue");
                        } else {
                            dzGSTTotal = dzGSTTotal + rsDET.getDouble("TotalValue");
                        }*/
                    }
                    textPrint = fncGetWhiteSpaces(30) + sSubBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                 /*   textPrint=fncGetWhiteSpaces(16-String.format("%.2f",dTotalQty).length())+String.format("%.2f",dTotalQty);
                    textPrint+=fncGetWhiteSpaces(18-String.format("%.2f",dTotalAmount).length())+String.format("%.2f",dTotalAmount)+"\r\n";
                    os.write(textPrint.getBytes());
                    textPrint=sBorderLine+"\r\n";
                    os.write(textPrint.getBytes());*/


                    if (dTotalLQty>0) {
                        String sTotLQty = String.format("%.0f", dTotalLQty);
                        String sTotWQty = String.format("%.0f", dTotalWQty);
                        String sTotFocQty = String.format("%.0f", dTotalFocQty);
                        String sTotAmount = jobj.getString("TotalValue");
                        String sTotalQty=sTotWQty+"/"+sTotLQty+"/"+sTotFocQty;

                        Double dTotAmount = Double.parseDouble(sTotAmount);

                        textPrint = fncGetWhiteSpaces(47 - sTotalQty.length()) + sTotalQty + fncGetWhiteSpaces(22 - fncFormatSeperator(dTotAmount).length()) + fncFormatSeperator(dTotAmount) + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());


                    Double dHDRTotalValue = 0.0;
                    dHDRTotalValue = jobj.getDouble("TotalValue");
//                        String sHdrTotal = String.format("%.2f", dHDRTotalValue);
                    String sHdrTotal=fncFormatSeperator(dHDRTotalValue);
                    String sBillDisc=String.format("%.2f", jobj.getDouble("BillDiscount"));
                    Double dHDRNetTotal = jobj.getDouble("Subtotal");
//                        String sHdrNetTotal = String.format("%.2f", dHDRNetTotal);
                    String sHdrNetTotal=fncFormatSeperator(dHDRNetTotal);


                    if(isGstSales) {
                        textPrint = "TOTAL EXCL. GST   : " + fncGetWhiteSpaces(45 - sHdrTotal.length()) + sHdrTotal + "\r\n";
                        os.write(textPrint.getBytes());

                        textPrint = "Bill Discount     : " + fncGetWhiteSpaces(45 - sBillDisc.length()) + sBillDisc + "\r\n";
                        os.write(textPrint.getBytes());

                        Double dHDRGST = jobj.getDouble("Gst");
//                        String sHdrGst = String.format("%.2f", dHDRGST);
                        String sHdrGst = fncFormatSeperator(dHDRGST);
                        textPrint = "GST AMOUNT        : ";
                        textPrint += fncGetWhiteSpaces(45 - sHdrGst.length()) + sHdrGst + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_on());
                        textPrint = "NET PAY INCL. GST : " + fncGetWhiteSpaces(45 - sHdrNetTotal.length()) + sHdrNetTotal + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());

                        textPrint = sBorderLine + "\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = " GST SUMMARY                       AMOUNT                    TAX     " + "\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = sBorderLine + "\r\n";
                        os.write(textPrint.getBytes());
//                    sSql="Select DISTINCT TaxType,GstPerc from vwARInvoiceDetail where InvoiceNo="+fncFormatSQLText(sInvoiceNo);
                        if (!isNullOrEmpty(jobj.getString("GstType"))) {
                            String sTaxType = jobj.getString("GstType").trim();
                            Double dGstPer = Double.parseDouble(sSalesGSTPerc);
                            String sGstPerc = String.format("%.0f", dGstPer);
                            String sGst = jobj.getString("Gst").trim();
                            Double dGst = Double.parseDouble(sGst);
//                            sGst = String.format("%.2f", dGst);
                            sGst = fncFormatSeperator(dGst);
                            String sTotValue = jobj.getString("Subtotal").trim();
                            Double dTotvalue = Double.parseDouble(sTotValue);
//                            sTotValue = String.format("%.2f", dTotvalue);
                            sTotValue = fncFormatSeperator(dTotvalue);
                            textPrint = " " + sTaxType + "=" + sGstPerc + "%";
                            int xsize = textPrint.length() + sTotValue.length();
                            textPrint += fncGetWhiteSpaces(42 - xsize) + sTotValue;
                            textPrint += fncGetWhiteSpaces(24 - sGst.length()) + sGst + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    }else {
                        textPrint = "Bill Discount     : " + fncGetWhiteSpaces(45 - sBillDisc.length()) + sBillDisc + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_on());
                        textPrint = "NET PAYABLE       : " + fncGetWhiteSpaces(45 - sHdrNetTotal.length()) + sHdrNetTotal + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());

//                    textPrint = "Customer Signature & Stamp" + "\r\n";
//                    os.write(textPrint.getBytes());
//                    sPath= Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/RetailPOSSFA/Footer.jpg";
//                    printBitmap(sPath, 0, 0, 0);
                    textPrint = "";
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                        /*textPrint = "\r\n";
                        os.write(textPrint.getBytes());*/
                    textPrint = sBorderLine+"\r\n";
                    os.write(textPrint.getBytes());
//                        sSql = "select * from tblReceiptPrintMessage order by MessageCode asc";
                 /*   sSql="select * from tblReceiptPrintMessage where MessageType='Receipt' order by MessageCode asc";
                    ResultSet rsPrintmsg = stmtCmpny.executeQuery(sSql);
                    if (rsPrintmsg.next()) {
                        int i=1;
                        do {
                            String sMsg=rsPrintmsg.getString(2).trim();
                            if(!isNullOrEmpty(sMsg)) {
                                if (sMsg.contains("~")) {
                                    String[] msgArray = sMsg.split("~");
                                    sMsg = "";
                                    for (int k = 0; k < msgArray.length; k++) {
                                        sMsg += msgArray[k] + "\r\n";
                                    }
                                    sPrintMsg += i + " " + sMsg;
                                } else {
                                    sPrintMsg += i + " " + sMsg + "\r\n";
                                }
                            }
                            i++;
                        } while (rsPrintmsg.next());
                    }*/
                    /*os.write(ec_pos.select_fontB());
                    if (!isNullOrEmpty(sPrintMsg)) {
                        textPrint = sPrintMsg.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    os.write(ec_pos.select_fontA());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());*/
                    textPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());

                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("***Print Exception", ex.toString());
        }
        return printSuccess;
    }

    public Boolean fncPrint_sales_new_70(String sData,String sBitmap,String loc_Code,Boolean isSalesInvoice,Boolean isReprint,Boolean IsOnline){
        Boolean printSuccess=false;
        try {
            String sSql = "";
            String sDetail="";
            String sPrintMsg = "";
            String sSalesGSTPerc="0.0";
            Boolean isGstSales=false;
            int countrsHDR = 0;
            ESC_POS ec_pos = new ESC_POS();
            JSONArray jsonArray=new JSONArray(sData);
            JSONArray jsonArrayDetail=null;
            JSONObject jobj=null;
            JSONObject jobjDetail;
            if (jsonArray.length()>0) {
                countrsHDR = 1;
                jobj=jsonArray.getJSONObject(0);
                sDetail=jobj.getString("sDetailStr");
                jsonArrayDetail=new JSONArray(sDetail);
            }
            int countrsDET = 0;
            if (jsonArrayDetail.length()>0) {
                countrsDET = jsonArrayDetail.length();
            }

            String sBorderLine = "---------------------------------------------------------------------";
            String sSubBorderLine = "---------------------------------------";
            if (jsonArray.length()>0) {
                String sCompanyName = jobj.getString(("ComName")).trim();
                String sCompanyAddress1 = jobj.getString(("ComAddr1")).trim();
                String sCompanyAddress2 = jobj.getString(("ComAddr2")).trim();
                String sCompanyCountry = jobj.getString(("ComCountry")).trim();
                String sCompanyPostalCode = jobj.getString(("ComPostalCode")).trim();
                String sCompanyPhone = jobj.getString(("ComPhone"));
                String sCompanyFax = jobj.getString(("ComFax"));
                String sTaxRegNo=jobj.getString("TaxRegNo").trim();
                if(sTaxRegNo.equalsIgnoreCase("")){
                    isGstSales=false;
                }else {
                    isGstSales=true;
                }
                String textPrint = "";
                /*String sPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/RetailPOSSFA/Header.jpg";
                printBitmap(sPath, 1, 0, 0);*/
                if(sBitmap.equalsIgnoreCase("")){
                    textPrint = "\r\n";
                }else {
                    printBitmap(sBitmap, 1, 0);
                }
                os.write(ec_pos.select_fontA());
                if (isReprint) {
                    textPrint = fncGetWhiteSpaces(22) + "Duplicate Copy" + "\r\n";
                    os.write(textPrint.getBytes());
                }
                sCompanyName = fncGetWhiteSpaces(22) + sCompanyName + "\r\n";
                os.write(sCompanyName.getBytes());
                if(!isNullOrEmpty(sCompanyAddress1)) {
                    sCompanyAddress1 = fncGetWhiteSpaces(22) + sCompanyAddress1 + "\r\n";
                    os.write(sCompanyAddress1.getBytes());
                }
                if(!isNullOrEmpty(sCompanyAddress2)){
                    sCompanyAddress2 = fncGetWhiteSpaces(22) + sCompanyAddress2 + "\r\n";
                    os.write(sCompanyAddress2.getBytes());
                }
                if (!isNullOrEmpty(sCompanyPhone)) {
                    String sContact = "";
                    sContact = fncGetWhiteSpaces(22) + "Tel : " + sCompanyPhone.trim();
                    if (!isNullOrEmpty(sCompanyFax)) {
                        sContact = sContact + "  Fax : " + sCompanyFax.trim();
                    }
                    if(sContact.length()>70){
                        textPrint = fncGetWhiteSpaces(22) +"Tel : " + sCompanyPhone.trim()+"\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = fncGetWhiteSpaces(22) +"Fax : " + sCompanyFax.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }else {
                        textPrint = sContact + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                } else if (!isNullOrEmpty(sCompanyFax)) {
                    textPrint =fncGetWhiteSpaces(22) + "Fax : " + sCompanyFax.trim() + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(true));
                if(isGstSales) {
                    textPrint = fncGetWhiteSpaces(22) + "TRN    : " + sTaxRegNo + "\r\n";
                }else {
                    textPrint = fncGetWhiteSpaces(22) + "TRN    : " + sTaxRegNo + "\r\n";
                }
                os.write(textPrint.getBytes());
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                if (isSalesInvoice) {
                    if(isGstSales) {
                        textPrint = fncGetWhiteSpaces(27) + "TAX INVOICE" + "\r\n";
                    }else {
                        textPrint = fncGetWhiteSpaces(27) + "TAX INVOICE" + "\r\n";
                    }
                    os.write(textPrint.getBytes());
                } else {
                    textPrint = fncGetWhiteSpaces(27) + "SALES ORDER" + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(false));
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                Double dTotalFocQty = 0.00, dTotalWQty  = 0.00, dTotalLQty = 0.0;
                if (countrsHDR > 0 && countrsDET > 0) {
                    if (isSalesInvoice) {
                        textPrint = "Invoice No     : " + jobj.getString("SalesInvoiceNo") + "\r\n";
                        os.write(textPrint.getBytes());
                    } else {
                        textPrint = "SalesOrder  No : " + jobj.getString("SalesInvoiceNo") + "\r\n";
                        os.write(textPrint.getBytes());

                    }
                    textPrint = "Date           : " + jobj.getString("BillingDate") + " " + jobj.getString("BillingTime") + "\r\n";
                    os.write(textPrint.getBytes());

                    if (!isNullOrEmpty(jobj.getString("CustomerCode"))) {
                        textPrint = "Cust Code      : " + jobj.getString("CustomerCode").trim() + "\r\n";
                        os.write(textPrint.getBytes());
//                        sCustCode=jobj.getString("CustomerCode");
                    }

                    if (!isNullOrEmpty(jobj.getString("CustomerName"))) {
                        if(jobj.getString("CustomerName").trim().length()>69){
                            textPrint=jobj.getString("CustomerName").trim().substring(0,69)+"\r\n";
                        }else {
                            textPrint = jobj.getString("CustomerName").trim() + "\r\n";
                        }
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("Address1"))) {
                        if(jobj.getString("Address1").trim().length()>69){
                            textPrint = jobj.getString("Address1").trim().substring(0,69)+ "\r\n";
                        }else {
                            textPrint = jobj.getString("Address1").trim() + "\r\n";
                        }
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("Address2"))) {
                        if(jobj.getString("Address2").trim().length()>69){
                            textPrint = jobj.getString("Address2").trim().substring(0,69)+ "\r\n";
                        }else {
                            textPrint = jobj.getString("Address2").trim() + "\r\n";
                        }
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("Phone").trim())) {
                        String sContact = "";
                        sContact = "Phone : " + jobj.getString("Phone").trim();
                        if (!isNullOrEmpty(jobj.getString("Fax"))) {
                            sContact = sContact + "  Fax : " + jobj.getString("Fax").trim();
                        }
                        if(sContact.length()>69){
                            textPrint = "Phone : " + jobj.getString("Phone").trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        }else {
                            textPrint = sContact + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    } else if (!isNullOrEmpty(jobj.getString("Fax").trim())) {
                        textPrint = "Fax   : " + jobj.getString("Fax").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }

                    if(!isNullOrEmpty(jobj.getString("GstRegNo").trim())){
                        textPrint = "TRN   : " + jobj.getString("GstRegNo").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    if (isSalesInvoice) {//CreatedBy
                        if (!isNullOrEmpty(jobj.getString("CreateUser"))) {
                            textPrint = "SR    : " + jobj.getString("CreateUser").trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    } else {
                        if (!isNullOrEmpty(jobj.getString("CreateUser"))) {
                            textPrint = "SR    : " +jobj.getString("CreateUser").trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = " No  Description                                  Qty/FOC      Price " + "\r\n";//Amount
                    os.write(textPrint.getBytes());
                    textPrint = "                      SubTotal      Tax(5%)               Net Amount "+ "\r\n";//Disc
                    os.write(textPrint.getBytes());
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
//                    int rsPosition = 0;
                    Double dsumSubTotal=0.0,dsumNetTotal=0.0,dGst=0.0;
                    for(int i=0;i<countrsDET;i++) {
                        jobjDetail = jsonArrayDetail.getJSONObject(i);
                        String sDescription = jobjDetail.getString("Description").trim();
                        String sShortDesc = "";
                        if (sDescription.length() > 40) {
                            sDescription = sDescription.substring(0, 39);
                        }
                        if (i <= 8) {
                            textPrint = "  " + (i + 1) + "  " + sDescription.trim();
                        } else {
                            textPrint = " " + (i + 1) + "  " + sDescription.trim();
                        }
                        Double dQty = 0.0, FOCQty = 0.0, WQty = 0.0;
                        dQty = jobjDetail.getDouble("LQty");
                        FOCQty = jobjDetail.getDouble("FOCQty");
                        WQty = jobjDetail.getDouble("WQty");
                        dTotalLQty += dQty;
                        dTotalFocQty += FOCQty;
                        dTotalWQty += WQty;
                        Double dPrice = 0.0, dDiscountAmount = 0.0, dBillDiscountAmount = 0.0, dGstAmnt = 0.0, dNetTotal = 0.0;
                        dPrice = jobjDetail.getDouble("UnitCost");
                        dDiscountAmount = jobjDetail.getDouble("DiscountAmount");//DiscountAmount
                        dBillDiscountAmount = jobjDetail.getDouble("BillDiscountAmount");
                        sSalesGSTPerc = jobjDetail.getString("GSTPerc");
                        dGstAmnt = jobjDetail.getDouble("GSTAmount");
                        String sGstAmnt = String.format("%.2f", dGstAmnt);

                        Double dTotalValue = jobjDetail.getDouble("TotalValue");

//                        dTotalValue += dBillDiscountAmount + dDiscountAmount;
                        dsumSubTotal += dTotalValue;
                        dNetTotal = dTotalValue + dGstAmnt;
                        dsumNetTotal += dNetTotal;

                        String sTotalQty = String.format("%.0f", WQty) + "/" + String.format("%.0f", dQty) + "/" + String.format("%.0f", FOCQty);
                        textPrint += fncGetWhiteSpaces(45 - textPrint.length());
                        textPrint += fncGetWhiteSpaces(12 - sTotalQty.length()) + sTotalQty;
                        textPrint += fncGetWhiteSpaces(11 - fncFormatSeperator(dPrice).length()) + fncFormatSeperator(dPrice) + "\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = fncGetWhiteSpaces(30 - fncFormatSeperator(dTotalValue).length()) + fncFormatSeperator(dTotalValue);

                        textPrint += fncGetWhiteSpaces(11 - sGstAmnt.length()) + sGstAmnt;
                        textPrint += fncGetWhiteSpaces(11);
//                      textPrint+= fncGetWhiteSpaces(11 - fncFormatSeperator(dDiscountAmount).length()) + fncFormatSeperator(dDiscountAmount);
                        textPrint += fncGetWhiteSpaces(16 - fncFormatSeperator(dNetTotal).length()) + fncFormatSeperator(dNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());

                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    Double dTotAmount = Double.parseDouble(jobj.getString("Subtotal"));
                    String sTotalQty = String.format("%.0f", dTotalWQty) + "/" + String.format("%.0f", dTotalLQty) + "/" + String.format("%.0f", dTotalFocQty);
//                    Double dDetailDiscount=Double.parseDouble(jobj.getString("DetailDiscount"));
//                    Double dNetTotal=Double.parseDouble(jobj.getString("NetTotal"));
                    dGst=Double.parseDouble(jobj.getString("Gst"));
                    textPrint = fncGetWhiteSpaces(30 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal);
                    textPrint+= fncGetWhiteSpaces(11 -fncFormatSeperator(dGst).length()) + fncFormatSeperator(dGst);
                    textPrint+= fncGetWhiteSpaces(16 -sTotalQty.length())+sTotalQty;
//                    textPrint+= fncGetWhiteSpaces(11 - fncFormatSeperator(dDetailDiscount).length()) + fncFormatSeperator(dDetailDiscount);
                    textPrint+= fncGetWhiteSpaces(11 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal)+"\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());

                    Double dHDRTotalValue = 0.0;
                    dHDRTotalValue = jobj.getDouble("TotalValue");
                    String sHdrTotal=fncFormatSeperator(dHDRTotalValue);
                    String sBillDisc=String.format("%.2f", jobj.getDouble("BillDiscount"));
                    Double dHDRNetTotal = jobj.getDouble("NetTotal");
                    String sHdrNetTotal=fncFormatSeperator(dHDRNetTotal);
                    dsumNetTotal=dsumNetTotal-Double.parseDouble(sBillDisc);

                    if(dGst>0) {
                        textPrint = "TOTAL EXCL. TAX   : " + fncGetWhiteSpaces(45 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal) + "\r\n";
                        os.write(textPrint.getBytes());

                        textPrint = "Bill Discount     : " + fncGetWhiteSpaces(45 - sBillDisc.length()) + sBillDisc + "\r\n";
                        os.write(textPrint.getBytes());

                        Double dHDRGST = jobj.getDouble("Gst");
//                        String sHdrGst = String.format("%.2f", dHDRGST);
                        String sHdrGst = fncFormatSeperator(dHDRGST);
                        textPrint = "VAT (5%)          : ";
                        textPrint += fncGetWhiteSpaces(45 - fncFormatSeperator(dGst).length()) + fncFormatSeperator(dGst) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_on());
                        textPrint = "NET PAY INCL. TAX : " + fncGetWhiteSpaces(45 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());

                        /*textPrint = sBorderLine + "\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = " TAX SUMMARY                       AMOUNT                    TAX     " + "\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = sBorderLine + "\r\n";
                        os.write(textPrint.getBytes());
//                    sSql="Select DISTINCT TaxType,GstPerc from vwARInvoiceDetail where InvoiceNo="+fncFormatSQLText(sInvoiceNo);
                        if (!isNullOrEmpty(jobj.getString("GstType"))) {
                            String sTaxType = jobj.getString("GstType").trim();
                            Double dGstPer = Double.parseDouble(sSalesGSTPerc);
                            String sGstPerc = String.format("%.0f", dGstPer);
                            String sGst = jobj.getString("Gst").trim();
                            Double dGst = Double.parseDouble(sGst);
//                            sGst = String.format("%.2f", dGst);
                            sGst = fncFormatSeperator(dGst);
                            String sTotValue = jobj.getString("Subtotal").trim();
                            Double dTotvalue = Double.parseDouble(sTotValue);
//                            sTotValue = String.format("%.2f", dTotvalue);
                            sTotValue = fncFormatSeperator(dTotvalue);
                            textPrint = " " + sTaxType + "=" + sGstPerc + "%";
                            int xsize = textPrint.length() + sTotValue.length();
                            textPrint += fncGetWhiteSpaces(42 - xsize) + sTotValue;
                            textPrint += fncGetWhiteSpaces(24 - sGst.length()) + sGst + "\r\n";
                            os.write(textPrint.getBytes());
                        }*/
                    }else {
                        textPrint = "Bill Discount     : " + fncGetWhiteSpaces(45 - sBillDisc.length()) + sBillDisc + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_on());
                        textPrint = "NET PAYABLE       : " + fncGetWhiteSpaces(45 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());
                    }
                    /*textPrint = "Bill Discount     : " + fncGetWhiteSpaces(45 - sBillDisc.length()) + sBillDisc + "\r\n";
                    os.write(textPrint.getBytes());
                    os.write(emphasized_on());
                    textPrint = "NET PAYABLE       : " + fncGetWhiteSpaces(45 - sHdrNetTotal.length()) + sHdrNetTotal + "\r\n";
                    os.write(textPrint.getBytes());
                    os.write(emphasized_off());*/
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                        /*textPrint = "\r\n";
                        os.write(textPrint.getBytes());*/
                    textPrint = sBorderLine+"\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());

                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("***Print Exception", ex.toString());
        }
        return printSuccess;
    }

    public Boolean fncPrint_Pay_48(String sData,String sBitmap,String loc_Code,Boolean isReprint,Boolean isPrint92){
        Boolean printSuccess=false;
        String sDetail="",sAccReceiveable="";
        ESC_POS ec_pos = new ESC_POS();
        try {
            String sSql = "",sPrintMsg="";
            String sCustCode="";
            int countrsHDR=0;
            /*sSql = "select *,convert(varchar(10),ChequeDate,103) as ChqDate,convert(varchar(10),CreateDate,108) as BillingTime,convert(varchar(10),CreateDate,103) as BillingDate from tblReceiptHeader where ReceiptNo=" + fncFormatSQLText(sReceiptNo);
            Statement stmntInvHdr=DBcon.createStatement();
            ResultSet rsHDR=stmntInvHdr.executeQuery(sSql);
            if(rsHDR.next()){
                countrsHDR=1;
            }
            sSql = "select *,convert(varchar(10),TranDate,103) as BillingDate from tblAccountsReceiptDetail where ReceiptNo=" + fncFormatSQLText(sReceiptNo);
            Statement stmntInvDet=DBcon.createStatement();
            ResultSet rsDET=stmntInvDet.executeQuery(sSql);*/

            JSONArray jsonArray=new JSONArray(sData);
            JSONArray jsonArrayDetail=null;
            JSONArray jsonAccReceivable=null;
            JSONObject jobj=null;
            JSONObject jobjDetail;
            if (jsonArray.length()>0) {
                countrsHDR = 1;
                jobj=jsonArray.getJSONObject(0);
                sDetail=jobj.getString("sDetail");
                jsonArrayDetail=new JSONArray(sDetail);
            }
            int countrsDET = 0;
            if (jsonArrayDetail.length()>0) {
                countrsDET = jsonArrayDetail.length();
            }

            String sBorderLine = "------------------------------------------------";
            String sBorderLine92 = "----------------------------------------------";
            sBorderLine92+=sBorderLine92;
           /* sSql="select * from tblCompany where CompanyId='1'";
            Statement stmtCmpny=DBcon.createStatement();
            ResultSet rsCompany=stmtCmpny.executeQuery(sSql);*/

            if (jsonArray.length()>0) {
                jobj=jsonArray.getJSONObject(0);
                Boolean isGstSales=false;
                String sCompanyName = jobj.getString("Name").trim();
                String sCompanyAddress1 = jobj.getString(("Address1")).trim();
                String sCompanyAddress2 = jobj.getString(("Address2")).trim();
                String sCompanyCountry = jobj.getString(("Country")).trim();
                String sCompanyPostalCode = jobj.getString(("PostalCode")).trim();
                String sCompanyPhone = jobj.getString(("Phone")).trim();
                String sCompanyFax = jobj.getString(("Fax")).trim();
                String sTaxRegNo = jobj.getString(("GSTRegNo")).trim();
                String sBizRegNo=jobj.getString("BRNNo").trim();
                if(sTaxRegNo.equalsIgnoreCase("")){
                    isGstSales=false;
                }else {
                    isGstSales=true;
                }

                String textPrint = "";
                if(sBitmap.equalsIgnoreCase("")){
                    textPrint = "\r\n";
                }else {
                    printBitmap(sBitmap, 1, 0);
                }
                textPrint = "\r\n";
                os.write(textPrint.getBytes());
                if (isReprint) {
                    if(isPrint92){
                        textPrint = fncGetWhiteSpaces(36) + "Duplicate Copy" + "\r\n";
                    }else {
                        textPrint = fncGetWhiteSpaces(11) + "Duplicate Copy" + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                }
                if(isPrint92){
                    sCompanyName = fncGetWhiteSpaces(36) + sCompanyName + "\r\n";
                   /* os.write(ec_pos.double_height_on());
                    os.write(ec_pos.setBold(true));
                    os.write(sCompanyName.getBytes());
                    os.write(ec_pos.setBold(false));
                    os.write(ec_pos.double_height_off());*/
                    os.write(ec_pos.setBold(true));
                    os.write(sCompanyName.getBytes());
                    os.write(ec_pos.setBold(false));
                }else {
                    sCompanyName = fncGetWhiteSpaces(11) + sCompanyName + "\r\n";
                    os.write(sCompanyName.getBytes());
                }
                Log.w("Printer", sCompanyName);
                if(!isNullOrEmpty(sCompanyAddress1)) {
                    if (isPrint92) {
                        sCompanyAddress1 = fncGetWhiteSpaces(36) + sCompanyAddress1 + "\r\n";
                    } else {
                        sCompanyAddress1 = fncGetWhiteSpaces(11) + sCompanyAddress1 + "\r\n";
                    }
                    Log.w("Printer", sCompanyAddress1);
                    os.write(sCompanyAddress1.getBytes());
                }
                if(!isNullOrEmpty(sCompanyAddress2)){
                    sCompanyAddress2 = fncGetWhiteSpaces(11) + sCompanyAddress2 + "\r\n";
                    os.write(sCompanyAddress2.getBytes());
                }
                /*if(!isNullOrEmpty(sState)){
                    String sText="";
                    sText=fncGetWhiteSpaces(11)+ sState.trim();
                    if(!isNullOrEmpty(sCompanyPostalCode)){
                        sText+=","+sCompanyPostalCode.trim();
                    }
                    sText+="\r\n";
                    os.write(sText.getBytes());
                }*/
//                    textPrint = fncGetWhiteSpaces(11) + sCompanyCountry + "-" + sCompanyPostalCode + "\r\n";


                if (!isNullOrEmpty(sCompanyPhone)) {
                    String sContact = "";
                    sContact = fncGetWhiteSpaces(11) + "Tel : " + sCompanyPhone.trim();
                    if (!isNullOrEmpty(sCompanyFax)) {
                        sContact = sContact + "  Fax : " + sCompanyFax.trim();
                    }
                    if(sContact.length()>48){
                        textPrint = fncGetWhiteSpaces(11) +"Tel : " + sCompanyPhone.trim()+"\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = fncGetWhiteSpaces(11) +"Fax : " + sCompanyFax.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }else {
                        textPrint = sContact + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                } else if (!isNullOrEmpty(sCompanyFax)) {
                    textPrint =fncGetWhiteSpaces(11) + "Fax : " + sCompanyFax.trim() + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(true));
                if(isGstSales) {
                    textPrint = fncGetWhiteSpaces(11) + "GST ID : " + sTaxRegNo + "\r\n";
                }else {
                    textPrint = fncGetWhiteSpaces(11) + "BRN    : " + sBizRegNo + "\r\n";
                }
                os.write(textPrint.getBytes());
                if(isPrint92){
                    textPrint = sBorderLine92 + "\r\n";
                }else {
                    textPrint = sBorderLine + "\r\n";
                }
                Log.w("Printer", textPrint);
                os.write(textPrint.getBytes());
                if(isPrint92){
                    textPrint = fncGetWhiteSpaces(41) + "RECEIPT" + "\r\n";
                }else {
                    textPrint = fncGetWhiteSpaces(16) + "RECEIPT" + "\r\n";
                }
                Log.w("Printer", textPrint);
                os.write(textPrint.getBytes());
                os.write(emphasized_off());

                if(isPrint92){
                    textPrint = sBorderLine92 + "\r\n";
                }else {
                    textPrint = sBorderLine + "\r\n";
                }
                Log.w("Printer", textPrint);
                os.write(textPrint.getBytes());
                Double dsGSTTotal = 0.00, dzGSTTotal = 0.00;
                if (countrsHDR > 0 ) {
                    textPrint="Entry No   : " + jobj.getString("EntryNo")+"\r\n";
                    os.write(textPrint.getBytes());
                    textPrint="Date       : " + jobj.getString("BillingDate")+" Time : "+ jobj.getString("BillingTime")+"\r\n";
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    if (!isNullOrEmpty(jobj.getString("CustomerCode").trim())) {
                        sCustCode=jobj.getString("CustomerCode").trim();
                        textPrint = "Cust Code  : " + sCustCode +"\r\n";
                        Log.w("Printer", textPrint);
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("CustomerName").trim())) {
                        textPrint = jobj.getString("CustomerName").trim()+"\r\n";
                        //fncGetWhiteSpaces(66 - rsHDR.getString("CustomerName").trim().length())+"SR     : "+rsHDR.getString("CreateUser").trim() +
                        Log.w("Printer", textPrint);
                        os.write(textPrint.getBytes());
                    }

                    /*if (!isNullOrEmpty(jobj.getString("VoucherRef").trim())) {
                        textPrint ="Reference No : "+jobj.getString("VoucherRef").trim()+"\r\n";
                        Log.w("Printer", textPrint);
                        os.write(textPrint.getBytes());
                    }*/
                    if(isPrint92){
                        textPrint = sBorderLine92 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());

                    if(isPrint92){
                        textPrint="     DOCUMENT       "+fncGetWhiteSpaces(20)+"DATE      "+fncGetWhiteSpaces(20)+"AMOUNT    "+"\r\n";
//                        textPrint = "  DOCUMENT           DATE           AMOUNT " + "\r\n";
                    }else {
                        textPrint = "  DOCUMENT           DATE           AMOUNT " + "\r\n";
                    }
                    Log.w("Printer",textPrint);
                    os.write(textPrint.getBytes());
                    if(isPrint92){
                        textPrint = sBorderLine92 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());

                    String sTransNo="",sDate="",sAmount="";
                    Double dTotAmnt=0.0,dAmt=0.0;
                    for (int i=0;i<jsonArrayDetail.length();i++){
                        jobjDetail=jsonArrayDetail.getJSONObject(i);
                        sTransNo=jobjDetail.getString("InvoiceNo").trim();
                        sDate=jobjDetail.getString("InvoiceDate").trim();
                        sAmount=jobjDetail.getString("TotalAmount").trim();
                        dAmt=Double.parseDouble(sAmount);
                        if(isPrint92){
                            textPrint = "     " + sTransNo + fncGetWhiteSpaces(35 - sTransNo.length()) + sDate + fncGetWhiteSpaces(20 - sDate.length()) + fncGetWhiteSpaces(16 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt) + "\r\n";
                        }else {
                            textPrint = " " + sTransNo + fncGetWhiteSpaces(14 - sTransNo.length()) + fncGetWhiteSpaces(14 - sDate.length()) + sDate + fncGetWhiteSpaces(13 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt) + "\r\n";
                        }
                        Log.w("Printer", textPrint);
                        os.write(textPrint.getBytes());
                        dTotAmnt+=dAmt;
                    }
                    /*while (rsTrans.next()){
                        sTransNo=rsTrans.getString("TranNo").trim();
                        sDate=rsTrans.getString("BillingDate").trim();
                        sAmount=rsTrans.getString("TranAmt").trim();
                        dAmt=Double.parseDouble(sAmount);
                        textPrint="     "+sTransNo+fncGetWhiteSpaces(35-sTransNo.length())+sDate+fncGetWhiteSpaces(25-sDate.length())+fncGetWhiteSpaces(10-String.format("%.2f",dAmt).length())+String.format("%.2f", dAmt)+"\r\n";
//                        os.write(textPrint.getBytes());
                        dTotAmnt=Double.parseDouble(sAmount);
                    }*/
                    if(isPrint92){
                        textPrint = sBorderLine92 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                   /* os.write(textPrint.getBytes());
                    if(isPrint92){
                        textPrint = "     Amount Due " + fncGetWhiteSpaces(60 - fncFormatSeperator(dTotAmnt).length()) + fncFormatSeperator(dTotAmnt) + "\r\n";
                    }else {
                        textPrint = " Amount Due " + fncGetWhiteSpaces(30 - fncFormatSeperator(dTotAmnt).length()) + fncFormatSeperator(dTotAmnt) + "\r\n";
                    }*/

                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());


                    if(isPrint92){
                        textPrint = sBorderLine92 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    os.write(emphasized_on());
                    if(isPrint92){
                        textPrint = "   MODE        BANKNAME                 CHEQUE NO           DATE                 AMOUNT     " + "\r\n";
                    }else {
                        textPrint = " MODE    BANKNAME  CHEQUE NO  DATE        AMOUNT" + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    os.write(emphasized_off());
                    if(isPrint92){
                        textPrint = sBorderLine92 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    String sAmnt="",sCheckNo="";
                    for(int i=0;i<jsonArrayDetail.length();i++){
                        jobjDetail=jsonArrayDetail.getJSONObject(i);
                        if(isPrint92){
                            String sMode = jobjDetail.getString("PayMode").trim();
                            String sBank = jobjDetail.getString("Bank");
                            sCheckNo = jobjDetail.getString("TotalAmount").trim();
                            if(sBank.length()>24){
                                sBank=sBank.substring(0,22);
                            }
                            if(sMode.equalsIgnoreCase("")){
                                sMode="Cash";
                            }
                            String sReceiptDate = jobjDetail.getString("ChequeDate");
                            if(sMode.equalsIgnoreCase("Cash")) {
                                sReceiptDate=jobj.getString("BillingDate");
                            }
                            sAmnt = jobjDetail.getString("TotalAmount");
                            dAmt = Double.parseDouble(sAmnt);
                            String dTot = fncFormatSeperator(dAmt);

                            textPrint = "   " + sMode + fncGetWhiteSpaces(12 - sMode.length()) + sBank + fncGetWhiteSpaces(24 - sBank.length()) + sCheckNo + fncGetWhiteSpaces(20 - sCheckNo.length());
                            textPrint += sReceiptDate + fncGetWhiteSpaces(14 - sReceiptDate.length()) + fncGetWhiteSpaces(17 - dTot.length()) + dTot + "\r\n";
                            Log.w("Printer", textPrint);
                            os.write(textPrint.getBytes());
                            Log.w("Printer", textPrint);

                        }else {
                            os.write(ec_pos.select_fontB());
                            String sMode = jobjDetail.getString("PayMode").trim();
                            String sBank = jobjDetail.getString("Bank");
                            sCheckNo = jobjDetail.getString("ChequeNo").trim();
                            if (sBank.length() > 12) {
                                sBank = sBank.substring(0, 11);
                            }
                            if(sMode.equalsIgnoreCase("")){
                                sMode="Cash";
                            }
                            String sReceiptDate="";
                            try {
                                sReceiptDate = jobjDetail.getString("ChequeDate");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (sMode.equalsIgnoreCase("Cash")) {
                                sReceiptDate = jobj.getString("BillingDate");
                            }
                            sAmnt = jobjDetail.getString("TotalAmount");
                            dAmt = Double.parseDouble(sAmnt);
                            String dTot = fncFormatSeperator(dAmt);
//                        textPrint=sReceiptNo+fncGetWhiteSpaces(13-sReceiptNo.length())+sMode+fncGetWhiteSpaces(8 - sMode.length())+sBank+"\r\n";
                            textPrint = " " + sMode + fncGetWhiteSpaces(11 - sMode.length()) + sBank + fncGetWhiteSpaces(13 - sBank.length()) + sCheckNo + fncGetWhiteSpaces(15 - sCheckNo.length());
                            textPrint += sReceiptDate + fncGetWhiteSpaces(12 - sReceiptDate.length()) + fncGetWhiteSpaces(11 - dTot.length()) + dTot + "\r\n";
                            Log.w("Printer", textPrint);
                            os.write(textPrint.getBytes());
                            Log.w("Printer", textPrint);
                        }
                    }
                    os.write(ec_pos.select_fontA());
                    if(isPrint92){
                        textPrint = sBorderLine92 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    os.write(emphasized_on());//fncFormatSeperator
                    //textPrint= " Total   :"+ fncGetWhiteSpaces(32-String.format("%.2f",dAmt ).length())+String.format("%.2f", dAmt)+ "\r\n";
                    sAmnt = jobj.getString("TotalAmount");
                    dAmt = Double.parseDouble(sAmnt);
                    if(isPrint92){
                        textPrint = "      Total   :" + fncGetWhiteSpaces(75 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt) + "\r\n";
                    }else {
                        textPrint = " Total   :" + fncGetWhiteSpaces(32 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt) + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    os.write(emphasized_off());
                    if(isPrint92){
                        textPrint= sBorderLine92 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    textPrint= "\r\n";
                    textPrint+= "\r\n";
                    os.write(textPrint.getBytes());
                    /*os.write(esc_pos.select_fontB());
                    if (!isNullOrEmpty(sDisp1)) {
                        textPrint =i+" "+sDisp1+"\r\n";
                        Log.w("Printer", textPrint);
                        os.write(textPrint.getBytes());
                        i++;
                    }
                    if (!isNullOrEmpty(sDisp2)) {
                        textPrint =i+" "+sDisp2+"\r\n";
                        Log.w("Printer",textPrint);
                        os.write(textPrint.getBytes());
                    }*/
                   /* sSql="select * from tblReceiptPrintMessage where MessageType='Payment' order by MessageCode asc";
                    ResultSet rsPrintmsg = stmtCmpny.executeQuery(sSql);
                    if (rsPrintmsg.next()) {
                        int i=1;
                        do {
                            String sMsg=rsPrintmsg.getString(2).trim();
                            if(!isNullOrEmpty(sMsg)) {
                                if (sMsg.contains("~")) {
                                    String[] msgArray = sMsg.split("~");
                                    sMsg = "";
                                    for (int k = 0; k < msgArray.length; k++) {
                                        sMsg += msgArray[k] + "\r\n";
                                    }
                                    sPrintMsg += i + " " + sMsg;
                                } else {
                                    sPrintMsg += i + " " + sMsg + "\r\n";
                                }
                            }
                            i++;
                        } while (rsPrintmsg.next());
                        NoLines=NoLines-i;
                    }
                    os.write(ec_pos.select_fontB());
                    if (!isNullOrEmpty(sPrintMsg)) {
                        textPrint = sPrintMsg.trim() + "\r\n";
                        os.write(textPrint.getBytes());

                    }*/
                    os.write(ec_pos.select_fontA());
                    textPrint= "\r\n";
                    textPrint+= "\r\n";
                    textPrint+= "\r\n";
                    textPrint+= "\r\n";
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                }
            }

            printSuccess=true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e("***Print Exception",ex.toString());
            try{
                os.write(ec_pos.select_fontA());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return printSuccess;
    }
    public Boolean fncPrint_Pay_70(String sData,String sBitmap,String loc_Code,Boolean isReprint,Boolean isPrint70){
        Boolean printSuccess=false;
        String sDetail="",sAccReceiveable="";
        ESC_POS ec_pos = new ESC_POS();
        try {
            String sCustCode="";
            int countrsHDR=0;
            JSONArray jsonArray=new JSONArray(sData);
            JSONArray jsonArrayDetail=null;
            JSONArray jsonAccReceivable=null;
            JSONObject jobj=null;
            JSONObject jobjDetail;
            if (jsonArray.length()>0) {
                countrsHDR = 1;
                jobj=jsonArray.getJSONObject(0);
                sDetail=jobj.getString("sDetailStr");
                jsonArrayDetail=new JSONArray(sDetail);
            }
            String sBorderLine = "------------------------------------------------";
            String sBorderLine70 = "---------------------------------------------------------------------";
            if (jsonArray.length()>0) {
                jobj=jsonArray.getJSONObject(0);
                Boolean isGstSales=false;
                String sCompanyName = jobj.getString("ComName").trim();
                String sCompanyAddress1 = jobj.getString(("ComAddr1")).trim();
                String sCompanyAddress2 = jobj.getString(("ComAddr2")).trim();
                String sCompanyCountry = jobj.getString(("ComCountry")).trim();
                String sCompanyPostalCode = jobj.getString(("ComPostalCode")).trim();
                String sCompanyPhone = jobj.getString(("ComPhone")).trim();
                String sCompanyFax = jobj.getString(("ComFax")).trim();
                String sTaxRegNo = jobj.getString(("TaxRegNo")).trim();
                String sBizRegNo=jobj.getString("BizRegNo").trim();
                if(sTaxRegNo.equalsIgnoreCase("")){
                    isGstSales=false;
                }else {
                    isGstSales=true;
                }

                String textPrint = "";
                if(sBitmap.equalsIgnoreCase("")){
                    textPrint = "\r\n";
                }else {
                    printBitmap(sBitmap, 1, 0);
                }
                textPrint = "\r\n";
                os.write(textPrint.getBytes());
                if (isReprint) {
                    if(isPrint70){
                        textPrint = fncGetWhiteSpaces(22) + "Duplicate Copy" + "\r\n";
                    }else {
                        textPrint = fncGetWhiteSpaces(11) + "Duplicate Copy" + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                }
                if(isPrint70){
                    sCompanyName = fncGetWhiteSpaces(22) + sCompanyName + "\r\n";
                    os.write(ec_pos.setBold(true));
                    os.write(sCompanyName.getBytes());
                    os.write(ec_pos.setBold(false));
                }else {
                    sCompanyName = fncGetWhiteSpaces(11) + sCompanyName + "\r\n";
                    os.write(sCompanyName.getBytes());
                }
                Log.w("Printer", sCompanyName);
                if(!isNullOrEmpty(sCompanyAddress1)) {
                    if (isPrint70) {
                        sCompanyAddress1 = fncGetWhiteSpaces(22) + sCompanyAddress1 + "\r\n";
                    } else {
                        sCompanyAddress1 = fncGetWhiteSpaces(11) + sCompanyAddress1 + "\r\n";
                    }
                    Log.w("Printer", sCompanyAddress1);
                    os.write(sCompanyAddress1.getBytes());
                }
                if(!isNullOrEmpty(sCompanyAddress2)){
                    if (isPrint70) {
                        sCompanyAddress2 = fncGetWhiteSpaces(22) + sCompanyAddress2 + "\r\n";
                    }else {
                        sCompanyAddress2 = fncGetWhiteSpaces(11) + sCompanyAddress2 + "\r\n";
                    }
                    os.write(sCompanyAddress2.getBytes());
                }
                if (!isNullOrEmpty(sCompanyPhone)) {
                    String sContact = "";
                    if (isPrint70) {
                        sContact = fncGetWhiteSpaces(22) + "Tel : " + sCompanyPhone.trim();
                        if (!isNullOrEmpty(sCompanyFax)) {
                            sContact = sContact + "  Fax : " + sCompanyFax.trim();
                        }
                    }else {
                        sContact = fncGetWhiteSpaces(11) + "Tel : " + sCompanyPhone.trim();
                        if (!isNullOrEmpty(sCompanyFax)) {
                            sContact = sContact + "  Fax : " + sCompanyFax.trim();
                        }
                    }
                    if(isPrint70){
                        if (sContact.length() > 70) {
                            textPrint = fncGetWhiteSpaces(22) + "Tel : " + sCompanyPhone.trim() + "\r\n";
                            os.write(textPrint.getBytes());
                            textPrint = fncGetWhiteSpaces(22) + "Fax : " + sCompanyFax.trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        } else {
                            textPrint = sContact + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    }else {
                        if (sContact.length() > 48) {
                            textPrint = fncGetWhiteSpaces(11) + "Tel : " + sCompanyPhone.trim() + "\r\n";
                            os.write(textPrint.getBytes());
                            textPrint = fncGetWhiteSpaces(11) + "Fax : " + sCompanyFax.trim() + "\r\n";
                            os.write(textPrint.getBytes());
                        } else {
                            textPrint = sContact + "\r\n";
                            os.write(textPrint.getBytes());
                        }
                    }
                } else if (!isNullOrEmpty(sCompanyFax)) {
                    if(isPrint70){
                        textPrint = fncGetWhiteSpaces(22) + "Fax : " + sCompanyFax.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }else {
                        textPrint = fncGetWhiteSpaces(11) + "Fax : " + sCompanyFax.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                }
                os.write(ec_pos.setBold(true));
                if(isGstSales) {
                    if (isPrint70){
                        textPrint = fncGetWhiteSpaces(22) + "GST ID : " + sTaxRegNo + "\r\n";
                    }else {
                        textPrint = fncGetWhiteSpaces(11) + "GST ID : " + sTaxRegNo + "\r\n";
                    }
                }else {
                    if(isPrint70){
                        textPrint = fncGetWhiteSpaces(22) + "BRN    : " + sBizRegNo + "\r\n";
                    }else {
                        textPrint = fncGetWhiteSpaces(11) + "BRN    : " + sBizRegNo + "\r\n";
                    }
                }
                os.write(textPrint.getBytes());
                if(isPrint70){
                    textPrint = sBorderLine70 + "\r\n";
                }else {
                    textPrint = sBorderLine + "\r\n";
                }
                Log.w("Printer", textPrint);
                os.write(textPrint.getBytes());
                if(isPrint70){
                    textPrint = fncGetWhiteSpaces(27) + "RECEIPT" + "\r\n";
                }else {
                    textPrint = fncGetWhiteSpaces(16) + "RECEIPT" + "\r\n";
                }
                Log.w("Printer", textPrint);
                os.write(textPrint.getBytes());
                os.write(emphasized_off());

                if(isPrint70){
                    textPrint = sBorderLine70 + "\r\n";
                }else {
                    textPrint = sBorderLine + "\r\n";
                }
                Log.w("Printer", textPrint);
                os.write(textPrint.getBytes());
                Double dsGSTTotal = 0.00, dzGSTTotal = 0.00;
                if (countrsHDR > 0 ) {
                    textPrint="Receipt No : " + jobj.getString("ReceiptNo")+"\r\n";
                    os.write(textPrint.getBytes());
                    textPrint="Date       : " + jobj.getString("BillingDate")+" Time : "+ jobj.getString("BillingTime")+"\r\n";
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    if (!isNullOrEmpty(jobj.getString("CustomerCode").trim())) {
                        sCustCode=jobj.getString("CustomerCode").trim();
                        textPrint = "Cust Code  : " + sCustCode +"\r\n";
                        Log.w("Printer", textPrint);
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("CustomerName").trim())) {
                        textPrint = jobj.getString("CustomerName").trim()+"\r\n";
                        Log.w("Printer", textPrint);
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("VoucherRef").trim())) {
                        textPrint ="Reference No : "+jobj.getString("VoucherRef").trim()+"\r\n";
                        Log.w("Printer", textPrint);
                        os.write(textPrint.getBytes());
                    }
                    if(isPrint70){
                        textPrint = sBorderLine70 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());

                    if(isPrint70){
                        textPrint="     DOCUMENT       "+fncGetWhiteSpaces(10)+"DATE      "+fncGetWhiteSpaces(10)+"    AMOUNT"+"\r\n";
//                        textPrint = "  DOCUMENT           DATE           AMOUNT " + "\r\n";
                    }else {
                        textPrint = "  DOCUMENT           DATE           AMOUNT " + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    if(isPrint70){
                        textPrint = sBorderLine70 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());

                    String sTransNo="",sDate="",sAmount="";
                    Double dTotAmnt=0.0,dAmt=0.0;
                    for (int i=0;i<jsonArrayDetail.length();i++){
                        jobjDetail=jsonArrayDetail.getJSONObject(i);
                        sTransNo=jobjDetail.getString("TranNo").trim();
                        sDate=jobjDetail.getString("BillingDate").trim();
                        sAmount=jobjDetail.getString("TranAmt").trim();
                        dAmt=Double.parseDouble(sAmount);
                        if(isPrint70){
                            textPrint = "     " + sTransNo + fncGetWhiteSpaces(25 - sTransNo.length()) + sDate + fncGetWhiteSpaces(15 - sDate.length()) + fncGetWhiteSpaces(15 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt) + "\r\n";
                        }else {
                            textPrint = " " + sTransNo + fncGetWhiteSpaces(14 - sTransNo.length()) + fncGetWhiteSpaces(14 - sDate.length()) + sDate + fncGetWhiteSpaces(13 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt) + "\r\n";
                        }
                        Log.w("Printer", textPrint);
                        os.write(textPrint.getBytes());
                        dTotAmnt+=dAmt;
                    }
                    if(isPrint70){
                        textPrint = sBorderLine70 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    if(isPrint70){
                        textPrint = "     Amount Due " + fncGetWhiteSpaces(44 - fncFormatSeperator(dTotAmnt).length()) + fncFormatSeperator(dTotAmnt) + "\r\n";
                    }else {
                        textPrint = " Amount Due " + fncGetWhiteSpaces(30 - fncFormatSeperator(dTotAmnt).length()) + fncFormatSeperator(dTotAmnt) + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());

                    if(isPrint70){
                        textPrint = sBorderLine70 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    os.write(emphasized_on());
                    if(isPrint70){
                        textPrint = "  MODE    BANKNAME            CHEQUE NO        DATE            AMOUNT" + "\r\n";
                    }else {
                        textPrint = " MODE    BANKNAME  CHEQUE NO  DATE        AMOUNT" + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    os.write(emphasized_off());
                    if(isPrint70){
                        textPrint = sBorderLine70 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    String sAmnt="",sCheckNo="";
                    for(int i=0;i<jsonArray.length();i++){
                        if(isPrint70){
                            String sMode = jobj.getString("ReceiptType").trim();
                            String sBank = jobj.getString("BankName");
                            sCheckNo = jobj.getString("ChequeNo").trim();
                            if(sBank.length()>20){
                                sBank=sBank.substring(0,19);
                            }
                            if(sCheckNo.length()>17){
                                sCheckNo=sCheckNo.substring(0,16);
                            }
                            if(sMode.equalsIgnoreCase("")){
                                sMode="Cash";
                            }
                            String sReceiptDate = "";
                            if(sMode.equalsIgnoreCase("Cash")) {
                                sReceiptDate=jobj.getString("BillingDate");
                            }else {
                                sReceiptDate = jobj.getString("ChqDate");
                            }
                            sAmnt = jobj.getString("ChequeAmt");
                            dAmt = Double.parseDouble(sAmnt);
                            String dTot = fncFormatSeperator(dAmt);

                            textPrint = "  " + sMode + fncGetWhiteSpaces(8 - sMode.length()) + sBank + fncGetWhiteSpaces(20 - sBank.length()) + sCheckNo + fncGetWhiteSpaces(17 - sCheckNo.length());
                            textPrint += sReceiptDate + fncGetWhiteSpaces(10 - sReceiptDate.length()) + fncGetWhiteSpaces(12 - dTot.length()) + dTot + "\r\n";
                            Log.w("Printer", textPrint);
                            os.write(textPrint.getBytes());
                            Log.w("Printer", textPrint);

                        }else {
                            os.write(ec_pos.select_fontB());
                            String sMode = jobj.getString("ReceiptType").trim();
                            String sBank = jobj.getString("BankName");
                            sCheckNo = jobj.getString("ChequeNo").trim();
                            if (sBank.length() > 12) {
                                sBank = sBank.substring(0, 11);
                            }
                            if(sMode.equalsIgnoreCase("")){
                                sMode="Cash";
                            }
                            String sReceiptDate="";
                            try {
                                sReceiptDate = jobj.getString("ChqDate");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (sMode.equalsIgnoreCase("Cash")) {
                                sReceiptDate = jobj.getString("BillingDate");
                            }
                            sAmnt = jobj.getString("ChequeAmt");
                            dAmt = Double.parseDouble(sAmnt);
                            String dTot = fncFormatSeperator(dAmt);
//                        textPrint=sReceiptNo+fncGetWhiteSpaces(13-sReceiptNo.length())+sMode+fncGetWhiteSpaces(8 - sMode.length())+sBank+"\r\n";
                            textPrint = " " + sMode + fncGetWhiteSpaces(11 - sMode.length()) + sBank + fncGetWhiteSpaces(13 - sBank.length()) + sCheckNo + fncGetWhiteSpaces(15 - sCheckNo.length());
                            textPrint += sReceiptDate + fncGetWhiteSpaces(12 - sReceiptDate.length()) + fncGetWhiteSpaces(11 - dTot.length()) + dTot + "\r\n";
                            Log.w("Printer", textPrint);
                            os.write(textPrint.getBytes());
                            Log.w("Printer", textPrint);
                        }
                    }
                    os.write(ec_pos.select_fontA());
                    if(isPrint70){
                        textPrint = sBorderLine70 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    os.write(emphasized_on());//fncFormatSeperator
                    //textPrint= " Total   :"+ fncGetWhiteSpaces(32-String.format("%.2f",dAmt ).length())+String.format("%.2f", dAmt)+ "\r\n";
                    if(isPrint70){
                        textPrint = "      Total   :" + fncGetWhiteSpaces(50 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt) + "\r\n";
                    }else {
                        textPrint = " Total   :" + fncGetWhiteSpaces(32 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt) + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    os.write(emphasized_off());
                    if(isPrint70){
                        textPrint= sBorderLine70 + "\r\n";
                    }else {
                        textPrint = sBorderLine + "\r\n";
                    }
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                    textPrint= "\r\n";
                    textPrint+= "\r\n";
                    os.write(textPrint.getBytes());
                    os.write(ec_pos.select_fontA());
                    textPrint= "\r\n";
                    textPrint+= "\r\n";
                    textPrint+= "\r\n";
                    textPrint+= "\r\n";
                    Log.w("Printer", textPrint);
                    os.write(textPrint.getBytes());
                }
            }

            printSuccess=true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e("***Print Exception",ex.toString());
            try{
                os.write(ec_pos.select_fontA());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return printSuccess;
    }
    public Boolean fncPrint_Summary_48(ArrayList<HashMap<String, String>> summaryList, String sSelectedType, String sCurrentDate,Boolean isPrint70){
        try{
            String mTxtPrint="";
            Boolean isPrinted=false;
            String sBorderLine="................................................"+ "\r\n";
            String sBorderLine70="......................................................................"+ "\r\n";
//            mTxtPrint = fncGetWhiteSpaces(11) + sSelectedType +" - "+sCurrentDate + "\r\n";
//            os.write(mTxtPrint.getBytes());
            switch (sSelectedType){
                case "INV":
                    if(isPrint70) {
                        mTxtPrint = fncGetWhiteSpaces(16) + "SalesInvoice Summary" + " - " + sCurrentDate + "\r\n";
                    }else {
                        mTxtPrint = fncGetWhiteSpaces(6) + "SalesInvoice Summary" + " - " + sCurrentDate + "\r\n";
                    }
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint= "\r\n";
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint = "\r\n";
                    os.write(mTxtPrint.getBytes());
                    if(isPrint70){
                        os.write(sBorderLine70.getBytes());
                    }else {
                        os.write(sBorderLine.getBytes());
                    }
                    isPrinted=fncPrintBillDetails(summaryList,isPrint70);
                    break;
                case "PAY":
                    if(isPrint70){
                        mTxtPrint = fncGetWhiteSpaces(16) + "PayMent Summary" + " - " + sCurrentDate + "\r\n";
                    }else {
                        mTxtPrint = fncGetWhiteSpaces(6) + "PayMent Summary" + " - " + sCurrentDate + "\r\n";
                    }
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint = "\r\n";
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint = "\r\n";
                    os.write(mTxtPrint.getBytes());
                    if(isPrint70){
                        mTxtPrint = " No  Receipt.No  Cust..Name              Payed      Type     BalanAmt" + "\r\n";
                    }else {
                        mTxtPrint = " No  Receipt.No  Cust..Name" + "\r\n";
                        mTxtPrint += fncGetWhiteSpaces(20) + "Payed     Type    BalanAmt  " + "\r\n";
                    }
                    os.write(mTxtPrint.getBytes());
                    if(isPrint70){
                        os.write(sBorderLine70.getBytes());
                    }else {
                        os.write(sBorderLine.getBytes());
                    }
                    isPrinted=fncPrintPayDetails(summaryList,isPrint70);
                    break;
                case "TIN":
                    if(isPrint70){
                        mTxtPrint = fncGetWhiteSpaces(16) + "TransferIn Summary" + " - " + sCurrentDate + "\r\n";
                    }else {
                        mTxtPrint = fncGetWhiteSpaces(6) + "TransferIn Summary" + " - " + sCurrentDate + "\r\n";
                    }
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint = "\r\n";
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint = "\r\n";
                    os.write(mTxtPrint.getBytes());
                    if(isPrint70){
                        mTxtPrint = " No  Transfer.No              FromLocation                TransferAmt" + "\r\n";
                    }else {
                        mTxtPrint = " No  Transfer.No   FromLocation    TransferAmt  " + "\r\n";
                    }
                    os.write(mTxtPrint.getBytes());
                    if(isPrint70){
                        os.write(sBorderLine70.getBytes());
                    }else {
                        os.write(sBorderLine.getBytes());
                    }
                    isPrinted=fncPrintTransferDetails(summaryList,isPrint70);
                    break;
                case "TOUT":
                    if(isPrint70){
                        mTxtPrint = fncGetWhiteSpaces(16) + "TransferOut Summary" + " - " + sCurrentDate + "\r\n";
                    }else {
                        mTxtPrint = fncGetWhiteSpaces(6) + "TransferOut Summary" + " - " + sCurrentDate + "\r\n";
                    }
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint = "\r\n";
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint = "\r\n";
                    os.write(mTxtPrint.getBytes());
                    if(isPrint70){
                        mTxtPrint = " No  Transfer.No              ToLocation                  TransferAmt" + "\r\n";
                    }else {
                        mTxtPrint = " No  Transfer.No   ToLocation      TransferAmt  " + "\r\n";
                    }
                    os.write(mTxtPrint.getBytes());
                    if(isPrint70){
                        os.write(sBorderLine70.getBytes());
                    }else {
                        os.write(sBorderLine.getBytes());
                    }
                    isPrinted=fncPrintTransferDetails(summaryList,isPrint70);
                    break;

                case "STOCK":
                    if(isPrint70){
                        mTxtPrint = fncGetWhiteSpaces(16) + "InventoryStock Summary" + " - " + sCurrentDate + "\r\n";
                    }else {
                        mTxtPrint = fncGetWhiteSpaces(6) + "InventoryStock Summary" + " - " + sCurrentDate + "\r\n";
                    }
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint = "\r\n";
                    os.write(mTxtPrint.getBytes());
                    mTxtPrint = "\r\n";
                    os.write(mTxtPrint.getBytes());
                    if(isPrint70){
                        mTxtPrint = " No  Inv.Code       Description                                 Qty  " + "\r\n";
                    }else {
                        mTxtPrint = " No  Inv.Code  Description                  Qty " + "\r\n";
                    }
                    os.write(mTxtPrint.getBytes());
                    if(isPrint70){
                        os.write(sBorderLine70.getBytes());
                    }else {
                        os.write(sBorderLine.getBytes());
                    }
                    isPrinted=fncPrintInventoryStock(summaryList,isPrint70);
                    break;
                default:
                    break;
            }
            return isPrinted;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Boolean fncPrintPayDetails(ArrayList<HashMap<String, String>> summaryList, Boolean isPrint70) {
        try {
            String sBorderLine="................................................"+ "\r\n";
            String sBorderLine70="......................................................................"+ "\r\n";
            String mTextPrint = "";
            Double dTotal,dNetTotal;
            String sTotal,sPayType,sNetTotal;
            int mNo = 1;
            for (int i = 0; i < summaryList.size(); i++) {
                HashMap<String, String> mapData = summaryList.get(i);
                dTotal=Double.parseDouble(mapData.get("TotalValue"));
                dNetTotal=Double.parseDouble(mapData.get("NetTotal"));
                sTotal=String.format("%.2f", dTotal);
                sPayType=mapData.get("Discount");
                sNetTotal=String.format("%.2f",dNetTotal);
                if(sPayType.length()>8){
                    sPayType=sPayType.substring(0,7);
                }
                mNo = i + 1;
                if (mNo < 10) {
                    mTextPrint = " " + mNo + "   " + mapData.get("InvoiceNo") + "  " + mapData.get("CustomerName");
                } else {
                    mTextPrint = " " + mNo + "  " + mapData.get("InvoiceNo") + "  " + mapData.get("CustomerName");
                }
                // No  Receipt.No  Cust..Name            Payed       Type      BalanAmt
                if(isPrint70){
                    if(mTextPrint.length()>35){
                        mTextPrint=mTextPrint.substring(0,34);
                    }
                    mTextPrint+=fncGetWhiteSpaces(35-mTextPrint.length())+fncGetWhiteSpaces(12 - sTotal.length()) + sTotal+fncGetWhiteSpaces(10 - sPayType.length()) + sPayType+fncGetWhiteSpaces(12 - sNetTotal.length()) + sNetTotal + "\r\n";;
                }else {
                    mTextPrint+="\r\n"+fncGetWhiteSpaces(15)+fncGetWhiteSpaces(11-sTotal.length())+sTotal+fncGetWhiteSpaces(8-sPayType.length())+sPayType+fncGetWhiteSpaces(12-sNetTotal.length())+sNetTotal+"\r\n";
                }
                os.write(mTextPrint.getBytes());
            }
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            if(isPrint70){
                os.write(sBorderLine70.getBytes());
            }else {
                os.write(sBorderLine.getBytes());
            }
            mTextPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
            mTextPrint += "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    private Boolean fncPrintInventoryStock(ArrayList<HashMap<String, String>> summaryList, Boolean isPrint70) {
        try{
            String sBorderLine="------------------------------------------------"+ "\r\n";
            String sBorderLine70="---------------------------------------------------------------------"+ "\r\n";
            String mTextPrint = "",sDesc="",sQty="";
            Double dNetTotal;
            int mNo = 1;
            for (int i = 0; i < summaryList.size(); i++) {
                HashMap<String, String> mapData = summaryList.get(i);
                sDesc=mapData.get("Description");
                sQty=mapData.get("QtyOnHand");
                if(sDesc.length()>27){
                    sDesc=sDesc.substring(0,26);
                }
                mNo = i + 1;
                if (mNo < 10) {
                    mTextPrint = "  " + mNo + "  " + mapData.get("InventoryCode");
                } else {
                    mTextPrint = " " + mNo + "  " + mapData.get("InventoryCode");
                }
                // No  Inv.Code       Description                                 Qty
                if(isPrint70){
                    mTextPrint += fncGetWhiteSpaces(20 - mTextPrint.length()) + sDesc + fncGetWhiteSpaces(40 - sDesc.length());
                    mTextPrint += fncGetWhiteSpaces(8 - sQty.length()) + sQty + "\r\n";
                }else {
                    mTextPrint += fncGetWhiteSpaces(15 - mTextPrint.length()) + sDesc + fncGetWhiteSpaces(27 - sDesc.length());
                    mTextPrint += fncGetWhiteSpaces(6 - sQty.length()) + sQty + "\r\n";
                }
                os.write(mTextPrint.getBytes());
            }
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            if(isPrint70){
                os.write(sBorderLine70.getBytes());
            }else {
                os.write(sBorderLine.getBytes());
            }
            mTextPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
            mTextPrint += "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Boolean fncPrintTransferDetails(ArrayList<HashMap<String, String>> summaryList, Boolean isPrint70) {
        try{
            String sBorderLine="................................................"+ "\r\n";
            String sBorderLine70="......................................................................"+ "\r\n";
            String mTextPrint = "",sNetTotal;
            Double dNetTotal;
            int mNo = 1;
            for (int i = 0; i < summaryList.size(); i++) {
                HashMap<String, String> mapData = summaryList.get(i);
                sNetTotal=mapData.get("NetTotal");
                if(sNetTotal.equalsIgnoreCase("")){
                    sNetTotal="0.00";
                }
                dNetTotal=Double.parseDouble(sNetTotal);
                sNetTotal=String.format("%.2f", dNetTotal);
                mNo = i + 1;
                if (mNo < 10) {
                    mTextPrint = " " + mNo + "   " + mapData.get("InvoiceNo");
                }else {
                    mTextPrint = " " + mNo + "  " + mapData.get("InvoiceNo");
                }
                // No  Transfer.No              FromLocation                TransferAmt
                if(isPrint70){
                    mTextPrint+=fncGetWhiteSpaces(30-mTextPrint.length())+mapData.get("CustomerName");
                    mTextPrint +=fncGetWhiteSpaces(42-mTextPrint.length())+ fncGetWhiteSpaces(27 - sNetTotal.length()) + sNetTotal + "\r\n";
                }else {
                    mTextPrint+=fncGetWhiteSpaces(9)+mapData.get("CustomerName");
                    mTextPrint += fncGetWhiteSpaces(34 - mTextPrint.length()) + fncGetWhiteSpaces(12 - sNetTotal.length()) + sNetTotal + "\r\n";
                }
                os.write(mTextPrint.getBytes());
            }
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            if(isPrint70){
                os.write(sBorderLine70.getBytes());
            }else {
                os.write(sBorderLine.getBytes());
            }
            mTextPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
            mTextPrint += "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Boolean fncPrintBillDetails(ArrayList<HashMap<String, String>> summaryList, Boolean isPrint70) {
        try {
            String sBorderLine="................................................"+ "\r\n";
            String sBorderLine70="......................................................................"+ "\r\n";
            String mTextPrint = "";
            Double dTotal,dDiscount,dNetTotal;
            String sTotal,sDisc,sNetTotal;
            int mNo = 1;
            for (int i = 0; i < summaryList.size(); i++) {
                HashMap<String, String> mapData = summaryList.get(i);
                dTotal=Double.parseDouble(mapData.get("TotalValue"));
                dDiscount=Double.parseDouble(mapData.get("Discount"));
                dNetTotal=Double.parseDouble(mapData.get("NetTotal"));
                sTotal=String.format("%.2f", dTotal);
                sDisc=String.format("%.2f", dDiscount);
                sNetTotal=String.format("%.2f",dNetTotal);
                mNo = i + 1;
                if (mNo < 10) {
                    mTextPrint = " " + mNo + "   " + mapData.get("InvoiceNo") + "  " + mapData.get("CustomerName");
                } else {
                    mTextPrint = " " + mNo + "  " + mapData.get("InvoiceNo") + "  " + mapData.get("CustomerName");
                }
                // No  Invoice.No  Cust..Name            Total        Disc     NetTotal
                if(isPrint70){
                    if(mTextPrint.length()>35){
                        mTextPrint=mTextPrint.substring(0,34);
                    }
                    mTextPrint+=fncGetWhiteSpaces(35-mTextPrint.length())+fncGetWhiteSpaces(12 - sTotal.length()) + sTotal+fncGetWhiteSpaces(10 - sDisc.length()) + sDisc+fncGetWhiteSpaces(12 - sNetTotal.length()) + sNetTotal + "\r\n";
                }else {
                    mTextPrint +="\r\n"+ fncGetWhiteSpaces(15) + fncGetWhiteSpaces(11 - sTotal.length()) + sTotal + fncGetWhiteSpaces(8 - sDisc.length()) + sDisc + fncGetWhiteSpaces(12 - sNetTotal.length()) + sNetTotal + "\r\n";
                }
                os.write(mTextPrint.getBytes());
            }
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            if(isPrint70){
                os.write(sBorderLine70.getBytes());
            }else {
                os.write(sBorderLine.getBytes());
            }
            mTextPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
            mTextPrint += "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            mTextPrint = "\r\n";
            os.write(mTextPrint.getBytes());
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }
    private String fncFormatSeperator(Double dAmout){
        try {
            NumberFormat nf = NumberFormat.getInstance();
            DecimalFormat df = (DecimalFormat) nf;
            df.applyPattern("#,###.00");
            if(dAmout>1) {
                return df.format(dAmout);
            }else {
                return String.format("%.2f",dAmout);
            }
        }catch (Exception e){
            e.printStackTrace();
            return String.format("%.2f",dAmout);
        }
    }
    private String fncFormatSQLDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= new Date(System.currentTimeMillis());
        String systemDate = df.format(date);
        return systemDate;
    }
    private String fncFormatSQLDateConvert(String sDate){
        try {
            //2017-02-08
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = (Date) df.parse(sDate);
            String systemDate = sdf.format(date);
            return systemDate;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
    private String fncGetWhiteSpaces(int iNumberOfSpaces)
    {
        try {
            if(iNumberOfSpaces>0) {
                String sFormat = "%-" + iNumberOfSpaces + "s";
                return String.format(sFormat, "");
            }else {
                return "";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
    private String fncFormatSQLText(String sText)
    {
        String sGenString = "";
        sGenString = "'" + sText.replace("'", "''") + "'";
        return sGenString;
    }
    public  byte[] print_linefeed()
    {
        byte[] result = new byte[1];
        result[0] = LF;
        return result;
    }
    public  byte[] emphasized_on()
    {
        byte[] result = new byte[3];
        result[0] = 27;
        result[1] = 69;
        result[2] = 0xF;
        return result;
    }
    public  byte[] emphasized_off()
    {
        byte[] result = new byte[3];
        result[0] = 27;
        result[1] = 69;
        result[2] = 0;
        return result;
    }
    private int printBitmap(String bitmapName, int alignment, int size, int mode) throws IOException {
        ESCPOS escpos = new ESCPOS();
        ImageLoader imageLoader = new ImageLoader();
        int[][] img = imageLoader.imageLoadGrayscale(bitmapName);
        if(img != null) {
            MobileImageConverter mConverter = new MobileImageConverter();
            byte[] bimg = mConverter.convertBitImage(img, imageLoader.getThresHoldValue()+60);
            int s=img.length;
            os.write(escpos.ESC_a(alignment));
            os.write(escpos.GS_v(size, mConverter.getxL(), mConverter.getxH(), mConverter.getyL(), mConverter.getyH(), bimg));
            os.write(escpos.ESC_a(0));
            return 0;
        }else {
            return -1;
        }
    }
    private int printBitmap(String bitmap, int alignment, int size) throws IOException {
        ESCPOS escpos = new ESCPOS();
        ImageLoader imageLoader = new ImageLoader();
        byte[] decodedString = Base64.decode(bitmap, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        int[][] img = getByteArrayGrayscale(decodedByte);
        if(img != null) {
            MobileImageConverter mConverter = new MobileImageConverter();
            byte[] bimg = mConverter.convertBitImage(img, imageLoader.getThresHoldValue()+60);
            int s=img.length;
            os.write(escpos.ESC_a(alignment));
            os.write(escpos.GS_v(size, mConverter.getxL(), mConverter.getxH(), mConverter.getyL(), mConverter.getyH(), bimg));
            os.write(escpos.ESC_a(0));
            return 0;
        }else {
            return -1;
        }
    }
    public int[][] getByteArrayGrayscale(Bitmap image) {
        int thresHoldValue;
        int maxColor = 0;
        int minColor = 255;
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] tpix = new int[width][height];

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                tpix[x][y] = this.convertRGBGrayscale(image.getPixel(x, y));
                if(tpix[x][y] < minColor) {
                    minColor = tpix[x][y];
                }

                if(tpix[x][y] > maxColor) {
                    maxColor = tpix[x][y];
                }
            }
        }

        thresHoldValue = (minColor + maxColor) / 2;
        return tpix;
    }
    private int convertRGBGrayscale(int ARGB) {
        int[] rgb = new int[]{(ARGB & 16711680) >> 16, (ARGB & '\uff00') >> 8, ARGB & 255};
        return (int)((double)rgb[0] * 0.3D) + (int)((double)rgb[1] * 0.59D) + (int)((double)rgb[2] * 0.11D);
    }

    public Boolean fncPrintStockRequest(String sPrintData, String uname, String loc_code) {
        Boolean printSuccess=false;
        try {
            String sDetail = "";
            int countrsHDR = 0;
            ESC_POS ec_pos = new ESC_POS();
            JSONArray jsonArray = new JSONArray(sPrintData);
            JSONArray jsonArrayDetail = null;
            JSONObject jobj = null;
            JSONObject jobjDetail;
            if (jsonArray.length() > 0) {
                countrsHDR = 1;
                jobj = jsonArray.getJSONObject(0);
                sDetail = jobj.getString("sDetailStr");
                jsonArrayDetail = new JSONArray(sDetail);
            }
            int countrsDET = 0;
            if (jsonArrayDetail.length() > 0) {
                countrsDET = jsonArrayDetail.length();
            }

            String sBorderLine = "---------------------------------------------------------------------";
            String sSubBorderLine = "---------------------------------------";
            if (jsonArray.length() > 0) {
                String sCompanyName = jobj.getString(("ComName")).trim();
                String sCompanyAddress1 = jobj.getString(("ComAddr1")).trim();
                String sCompanyAddress2 = jobj.getString(("ComAddr2")).trim();
                String sCompanyCountry = jobj.getString(("ComCountry")).trim();
                String sCompanyPostalCode = jobj.getString(("ComPostalCode")).trim();
                String sCompanyPhone = jobj.getString(("ComPhone"));
                String sCompanyFax = jobj.getString(("ComFax"));
//                String sState=jobj.getString(("State"));
                String sTaxRegNo = jobj.getString(("TaxRegNo")).trim();
                String sBizRegNo = jobj.getString("BizRegNo").trim();
                String textPrint = "";
                os.write(ec_pos.select_fontA());
                sCompanyName = fncGetWhiteSpaces(22) + sCompanyName + "\r\n";
                os.write(sCompanyName.getBytes());
                if (!isNullOrEmpty(sCompanyAddress1)) {
                    sCompanyAddress1 = fncGetWhiteSpaces(22) + sCompanyAddress1 + "\r\n";
                    os.write(sCompanyAddress1.getBytes());
                }
                if (!isNullOrEmpty(sCompanyAddress2)) {
                    sCompanyAddress2 = fncGetWhiteSpaces(22) + sCompanyAddress2 + "\r\n";
                    os.write(sCompanyAddress2.getBytes());
                }
                if (!isNullOrEmpty(sCompanyPhone)) {
                    String sContact = "";
                    sContact = fncGetWhiteSpaces(22) + "Tel : " + sCompanyPhone.trim();
                    if (!isNullOrEmpty(sCompanyFax)) {
                        sContact = sContact + "  Fax : " + sCompanyFax.trim();
                    }
                    if (sContact.length() > 70) {
                        textPrint = fncGetWhiteSpaces(22) + "Tel : " + sCompanyPhone.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = fncGetWhiteSpaces(22) + "Fax : " + sCompanyFax.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    } else {
                        textPrint = sContact + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                } else if (!isNullOrEmpty(sCompanyFax)) {
                    textPrint = fncGetWhiteSpaces(22) + "Fax : " + sCompanyFax.trim() + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(true));
                textPrint = fncGetWhiteSpaces(22) + "TRN    : " + sTaxRegNo + "\r\n";
                os.write(textPrint.getBytes());
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                textPrint = fncGetWhiteSpaces(27) + "STOCK REQUEST" + "\r\n";
                os.write(textPrint.getBytes());
                os.write(ec_pos.setBold(false));
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                if (countrsHDR > 0 && countrsDET > 0) {
                    textPrint = "StockReq No    : " + jobj.getString("SalesInvoiceNo") + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "Date           : " + jobj.getString("BillingDate") + " " + jobj.getString("BillingTime") + "\r\n";
                    os.write(textPrint.getBytes());

                    if (!isNullOrEmpty(jobj.getString("CustomerCode"))) {
                        textPrint = "From Loc       : " + jobj.getString("CustomerCode").trim() + "\r\n";
                        os.write(textPrint.getBytes());
//                        sCustCode=jobj.getString("CustomerCode");
                    }

                    if (!isNullOrEmpty(jobj.getString("CustomerName"))) {
                        textPrint ="To  Loc        : "+ jobj.getString("CustomerName").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("CreateUser"))) {
                        textPrint = "SR    : " + jobj.getString("CreateUser").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = " No  Description                             QoH     Price     R.Qty " + "\r\n";//Amount
                    os.write(textPrint.getBytes());
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());

                    for(int i=0;i<countrsDET;i++) {
                        jobjDetail=jsonArrayDetail.getJSONObject(i);
                        String sDescription = jobjDetail.getString("Description").trim();
                        String sShortDesc="";
                        if (sDescription.length() > 35) {
                            sDescription = sDescription.substring(0, 34);
                        }
                        if (i<= 8) {
                            textPrint = "  " + (i+1) + "  " + sDescription.trim();
                        } else {
                            textPrint = " " + (i+1) + "  " + sDescription.trim();
                        }
                        Double dQty = 0.0,FOCQty=0.0,WQty=0.0,dPrice=0.0;
                        dQty = jobjDetail.getDouble("LQty");
                        FOCQty=jobjDetail.getDouble("FOCQty");
                        WQty=jobjDetail.getDouble("WQty");
                        dPrice = jobjDetail.getDouble("UnitCost");

                        textPrint += fncGetWhiteSpaces(40 - textPrint.length());
                        textPrint += fncGetWhiteSpaces(8 - String.format("%.1f", FOCQty).length()) + String.format("%.1f", FOCQty);
                        textPrint += fncGetWhiteSpaces(11 - String.format("%.2f", dPrice).length()) + String.format("%.2f", dPrice);
                        textPrint += fncGetWhiteSpaces(10- String.format("%.1f",dQty).length()) + String.format("%.1f",dQty)+"\r\n";
                        os.write(textPrint.getBytes());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    Double dTotalQty=jobj.getDouble("TotalValue");
                    textPrint =fncGetWhiteSpaces(38)+"Total Request Qty : "+fncGetWhiteSpaces(11 - String.format("%.1f", dTotalQty).length()) + String.format("%.1f", dTotalQty) + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return printSuccess;
    }

    public Boolean fncPrint_return_new_70(String sData, String sBitmap, String loc_code, boolean b, boolean isReprint, Boolean isOnline){
        Boolean printSuccess=false;
        try {
            String sSql = "";
            String sDetail="";
            String sPrintMsg = "";
            String sSalesGSTPerc="0.0";
            Boolean isGstSales=false;
            int countrsHDR = 0;
            ESC_POS ec_pos = new ESC_POS();
            JSONArray jsonArray=new JSONArray(sData);
            JSONArray jsonArrayDetail=null;
            JSONObject jobj=null;
            JSONObject jobjDetail;
            if (jsonArray.length()>0) {
                countrsHDR = 1;
                jobj=jsonArray.getJSONObject(0);
                sDetail=jobj.getString("sDetailStr");
                jsonArrayDetail=new JSONArray(sDetail);
            }
            int countrsDET = 0;
            if (jsonArrayDetail.length()>0) {
                countrsDET = jsonArrayDetail.length();
            }

            String sBorderLine = "---------------------------------------------------------------------";
            String sSubBorderLine = "---------------------------------------";
            if (jsonArray.length()>0) {
                String sCompanyName = jobj.getString(("ComName")).trim();
                String sCompanyAddress1 = jobj.getString(("ComAddr1")).trim();
                String sCompanyAddress2 = jobj.getString(("ComAddr2")).trim();
                String sCompanyCountry = jobj.getString(("ComCountry")).trim();
                String sCompanyPostalCode = jobj.getString(("ComPostalCode")).trim();
                String sCompanyPhone = jobj.getString(("ComPhone"));
                String sCompanyFax = jobj.getString(("ComFax"));
                String sTaxRegNo=jobj.getString("TaxRegNo").trim();
                if(sTaxRegNo.equalsIgnoreCase("")){
                    isGstSales=false;
                }else {
                    isGstSales=true;
                }
                String textPrint = "";
                /*String sPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/RetailPOSSFA/Header.jpg";
                printBitmap(sPath, 1, 0, 0);*/
                if(sBitmap.equalsIgnoreCase("")){
                    textPrint = "\r\n";
                }else {
                    printBitmap(sBitmap, 1, 0);
                }
                os.write(ec_pos.select_fontA());
                if (isReprint) {
                    textPrint = fncGetWhiteSpaces(22) + "Duplicate Copy" + "\r\n";
                    os.write(textPrint.getBytes());
                }
                sCompanyName = fncGetWhiteSpaces(22) + sCompanyName + "\r\n";
                os.write(sCompanyName.getBytes());
                if(!isNullOrEmpty(sCompanyAddress1)) {
                    sCompanyAddress1 = fncGetWhiteSpaces(22) + sCompanyAddress1 + "\r\n";
                    os.write(sCompanyAddress1.getBytes());
                }
                if(!isNullOrEmpty(sCompanyAddress2)){
                    sCompanyAddress2 = fncGetWhiteSpaces(22) + sCompanyAddress2 + "\r\n";
                    os.write(sCompanyAddress2.getBytes());
                }
                if (!isNullOrEmpty(sCompanyPhone)) {
                    String sContact = "";
                    sContact = fncGetWhiteSpaces(22) + "Tel : " + sCompanyPhone.trim();
                    if (!isNullOrEmpty(sCompanyFax)) {
                        sContact = sContact + "  Fax : " + sCompanyFax.trim();
                    }
                    if(sContact.length()>70){
                        textPrint = fncGetWhiteSpaces(22) +"Tel : " + sCompanyPhone.trim()+"\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = fncGetWhiteSpaces(22) +"Fax : " + sCompanyFax.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }else {
                        textPrint = sContact + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                } else if (!isNullOrEmpty(sCompanyFax)) {
                    textPrint =fncGetWhiteSpaces(22) + "Fax : " + sCompanyFax.trim() + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(true));
                if(isGstSales) {
                    textPrint = fncGetWhiteSpaces(22) + "TRN    : " + sTaxRegNo + "\r\n";
                }else {
                    textPrint = fncGetWhiteSpaces(22) + "TRN    : " + sTaxRegNo + "\r\n";
                }
                os.write(textPrint.getBytes());
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                textPrint = fncGetWhiteSpaces(27) + "SALES RETURN" + "\r\n";
                os.write(textPrint.getBytes());
                os.write(ec_pos.setBold(false));
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                Double dTotalFocQty = 0.00, dTotalWQty  = 0.00, dTotalLQty = 0.0;
                if (countrsHDR > 0 && countrsDET > 0) {
                    textPrint = "Return No     : " + jobj.getString("SalesInvoiceNo") + "\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = "Date           : " + jobj.getString("BillingDate") + " " + jobj.getString("BillingTime") + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "Return Type   : " + jobj.getString("CustomerName") + "\r\n";
                    os.write(textPrint.getBytes());
                    if (!isNullOrEmpty(jobj.getString("CustomerCode"))) {
                        textPrint = "Cust Code      : " + jobj.getString("CustomerCode").trim() + "\r\n";
                        os.write(textPrint.getBytes());
//                        sCustCode=jobj.getString("CustomerCode");
                    }
                    if(!isNullOrEmpty(jobj.getString("GstRegNo").trim())){
                        textPrint = "TRN   : " + jobj.getString("GstRegNo").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("CreateUser"))) {
                        textPrint = "SR    : " +jobj.getString("CreateUser").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = " No  Description                                  Qty          Price " + "\r\n";//Amount
                    os.write(textPrint.getBytes());
                    textPrint = "                      SubTotal      Tax(5%)               Net Amount "+ "\r\n";//Disc
                    os.write(textPrint.getBytes());
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
//                    int rsPosition = 0;
                    Double dsumSubTotal=0.0,dsumNetTotal=0.0,dGst=0.0;
                    for(int i=0;i<countrsDET;i++) {
                        jobjDetail = jsonArrayDetail.getJSONObject(i);
                        String sDescription = jobjDetail.getString("Description").trim();
                        String sShortDesc = "";
                        if (sDescription.length() > 40) {
                            sDescription = sDescription.substring(0, 39);
                        }
                        if (i <= 8) {
                            textPrint = "  " + (i + 1) + "  " + sDescription.trim();
                        } else {
                            textPrint = " " + (i + 1) + "  " + sDescription.trim();
                        }
                        Double dQty = 0.0,  WQty = 0.0;
                        dQty = jobjDetail.getDouble("LQty");
                        WQty = jobjDetail.getDouble("WQty");
                        dTotalLQty += dQty;
                        dTotalWQty += WQty;
                        Double dPrice = 0.0, dDiscountAmount = 0.0, dBillDiscountAmount = 0.0, dGstAmnt = 0.0, dNetTotal = 0.0;
                        sSalesGSTPerc = jobjDetail.getString("GSTPerc");
                        dGstAmnt = jobjDetail.getDouble("GSTAmount");
                        String sGstAmnt = String.format("%.2f", dGstAmnt);

                        Double dTotalValue = jobjDetail.getDouble("TotalValue");

//                        dTotalValue += dBillDiscountAmount + dDiscountAmount;
                        dsumSubTotal += dTotalValue;
                        dNetTotal = dTotalValue + dGstAmnt;
                        dsumNetTotal += dNetTotal;

                        String sTotalQty = String.format("%.0f", WQty) + "/" + String.format("%.0f", dQty);
                        textPrint += fncGetWhiteSpaces(45 - textPrint.length());
                        textPrint += fncGetWhiteSpaces(12 - sTotalQty.length()) + sTotalQty;
                        textPrint += fncGetWhiteSpaces(11 - fncFormatSeperator(dPrice).length()) + fncFormatSeperator(dPrice) + "\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = fncGetWhiteSpaces(30 - fncFormatSeperator(dTotalValue).length()) + fncFormatSeperator(dTotalValue);

                        textPrint += fncGetWhiteSpaces(11 - sGstAmnt.length()) + sGstAmnt;
                        textPrint += fncGetWhiteSpaces(11);
//                      textPrint+= fncGetWhiteSpaces(11 - fncFormatSeperator(dDiscountAmount).length()) + fncFormatSeperator(dDiscountAmount);
                        textPrint += fncGetWhiteSpaces(16 - fncFormatSeperator(dNetTotal).length()) + fncFormatSeperator(dNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());

                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    Double dTotAmount = Double.parseDouble(jobj.getString("Subtotal"));
                    String sTotalQty = String.format("%.0f", dTotalWQty) + "/" + String.format("%.0f", dTotalLQty) + "/" + String.format("%.0f", dTotalFocQty);
//                    Double dDetailDiscount=Double.parseDouble(jobj.getString("DetailDiscount"));
//                    Double dNetTotal=Double.parseDouble(jobj.getString("NetTotal"));
                    dGst=Double.parseDouble(jobj.getString("Gst"));
                    textPrint = fncGetWhiteSpaces(30 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal);
                    textPrint+= fncGetWhiteSpaces(11 -fncFormatSeperator(dGst).length()) + fncFormatSeperator(dGst);
                    textPrint+= fncGetWhiteSpaces(16 -sTotalQty.length())+sTotalQty;
//                    textPrint+= fncGetWhiteSpaces(11 - fncFormatSeperator(dDetailDiscount).length()) + fncFormatSeperator(dDetailDiscount);
                    textPrint+= fncGetWhiteSpaces(11 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal)+"\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());

                    Double dHDRTotalValue = 0.0;
                    dHDRTotalValue = jobj.getDouble("TotalValue");
                    String sHdrTotal=fncFormatSeperator(dHDRTotalValue);
                    Double dHDRNetTotal = jobj.getDouble("Subtotal");
                    String sHdrNetTotal=fncFormatSeperator(dHDRNetTotal);

                    if(dGst>0) {
                        textPrint = "TOTAL EXCL. TAX   : " + fncGetWhiteSpaces(45 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal) + "\r\n";
                        os.write(textPrint.getBytes());

                        Double dHDRGST = jobj.getDouble("Gst");
//                        String sHdrGst = String.format("%.2f", dHDRGST);
                        String sHdrGst = fncFormatSeperator(dHDRGST);
                        textPrint = "VAT (5%)          : ";
                        textPrint += fncGetWhiteSpaces(45 - fncFormatSeperator(dGst).length()) + fncFormatSeperator(dGst) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_on());
                        textPrint = "NET PAY INCL. TAX : " + fncGetWhiteSpaces(45 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());
                    }else {
                        os.write(emphasized_on());
                        textPrint = "NET PAYABLE       : " + fncGetWhiteSpaces(45 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());
                    }
                    /*textPrint = "Bill Discount     : " + fncGetWhiteSpaces(45 - sBillDisc.length()) + sBillDisc + "\r\n";
                    os.write(textPrint.getBytes());
                    os.write(emphasized_on());
                    textPrint = "NET PAYABLE       : " + fncGetWhiteSpaces(45 - sHdrNetTotal.length()) + sHdrNetTotal + "\r\n";
                    os.write(textPrint.getBytes());
                    os.write(emphasized_off());*/
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                        /*textPrint = "\r\n";
                        os.write(textPrint.getBytes());*/
                    textPrint = sBorderLine+"\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());

                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("***Print Exception", ex.toString());
        }
        return printSuccess;
    }

    public Boolean fncPrint_return_48(String sData, String sBitmap, String loc_code, boolean b, boolean isReprint) {
        Boolean printSuccess=false;
        try {
            String sSql = "";
            String sDetail="";
            String sSalesGSTPerc;
            Boolean isGstSales=false;
            int countrsHDR = 0;
            ESC_POS ec_pos = new ESC_POS();
            JSONArray jsonArray=new JSONArray(sData);
            JSONArray jsonArrayDetail=null;
            JSONObject jobj=null;
            JSONObject jobjDetail;
            if (jsonArray.length()>0) {
                countrsHDR = 1;
                jobj=jsonArray.getJSONObject(0);
                sDetail=jobj.getString("sDetailStr");
                jsonArrayDetail=new JSONArray(sDetail);
            }
            int countrsDET = 0;
            if (jsonArrayDetail.length()>0) {
                countrsDET = jsonArrayDetail.length();
            }

            String sBorderLine = "------------------------------------------------";
            String sSubBorderLine = "--------------------------";
            String sDottedLine = ".......................................";
            if (jsonArray.length()>0) {
                String sCompanyName = jobj.getString(("ComName")).trim();
                String sCompanyAddress1 = jobj.getString(("ComAddr1")).trim();
                String sCompanyAddress2 = jobj.getString(("ComAddr2")).trim();
                String sCompanyCountry = jobj.getString(("ComCountry")).trim();
                String sCompanyPostalCode = jobj.getString(("ComPostalCode")).trim();
                String sCompanyPhone = jobj.getString(("ComPhone"));
                String sCompanyFax = jobj.getString(("ComFax"));
                String sTaxRegNo=jobj.getString("TaxRegNo").trim();
                if(sTaxRegNo.equalsIgnoreCase("")){
                    isGstSales=false;
                }else {
                    isGstSales=true;
                }
                String sCustCode="";
                String textPrint = "";
                /*String sPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/RetailPOSSFA/Header.jpg";
                printBitmap(sPath, 1, 0, 0);*/
                if(sBitmap.equalsIgnoreCase("")){
                    textPrint = "\r\n";
                }else {
                    printBitmap(sBitmap, 1, 0);
                }
                os.write(ec_pos.select_fontA());
                if (isReprint) {
                    textPrint = fncGetWhiteSpaces(11) + "Duplicate Copy" + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.double_height_on());
                sCompanyName = fncGetWhiteSpaces(11) + sCompanyName + "\r\n";
                os.write(sCompanyName.getBytes());
                os.write(ec_pos.double_height_off());
                if(!isNullOrEmpty(sCompanyAddress1)) {
                    sCompanyAddress1 = fncGetWhiteSpaces(11) + sCompanyAddress1 + "\r\n";
                    os.write(sCompanyAddress1.getBytes());
                }
                if(!isNullOrEmpty(sCompanyAddress2)){
                    sCompanyAddress2 = fncGetWhiteSpaces(11) + sCompanyAddress2 + "\r\n";
                    os.write(sCompanyAddress2.getBytes());
                }


                if (!isNullOrEmpty(sCompanyPhone)) {
                    String sContact = "";
                    sContact = fncGetWhiteSpaces(11) + "Tel : " + sCompanyPhone.trim();
                    if (!isNullOrEmpty(sCompanyFax)) {
                        sContact = sContact + "  Fax : " + sCompanyFax.trim();
                    }
                    if(sContact.length()>48){
                        textPrint = fncGetWhiteSpaces(11) +"Tel : " + sCompanyPhone.trim()+"\r\n";
                        os.write(textPrint.getBytes());
                        textPrint = fncGetWhiteSpaces(11) +"Fax : " + sCompanyFax.trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }else {
                        textPrint = sContact + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                } else if (!isNullOrEmpty(sCompanyFax)) {
                    textPrint =fncGetWhiteSpaces(11) + "Fax : " + sCompanyFax.trim() + "\r\n";
                    os.write(textPrint.getBytes());
                }
                os.write(ec_pos.setBold(true));
                if(isGstSales) {
                    textPrint = fncGetWhiteSpaces(11) + "TRN    : " + sTaxRegNo + "\r\n";
                }else {
                    textPrint = fncGetWhiteSpaces(11) + "TRN    : " + sTaxRegNo + "\r\n";
                }
                os.write(textPrint.getBytes());
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                textPrint = fncGetWhiteSpaces(16) + "SALES RETURN" + "\r\n";
                os.write(textPrint.getBytes());
                os.write(ec_pos.setBold(false));
                textPrint = sBorderLine + "\r\n";
                os.write(textPrint.getBytes());
                Double dTotalFocQty = 0.00, dTotalWQty  = 0.00, dTotalLQty = 0.0, dTotalAmount = 0.0;
                if (countrsHDR > 0 && countrsDET > 0) {
                    textPrint = "Return  No     : " + jobj.getString("SalesInvoiceNo") + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "Date           : " + jobj.getString("BillingDate") + " " + jobj.getString("BillingTime") + "\r\n";
                    os.write(textPrint.getBytes());
                    os.write(ec_pos.setBold(true));
                    textPrint = "Return  Type   : " + jobj.getString("CustomerName") + "\r\n";
                    os.write(textPrint.getBytes());
                    if (!isNullOrEmpty(jobj.getString("CustomerCode"))) {
                        textPrint = "Cust Code      : " + jobj.getString("CustomerCode").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                        sCustCode=jobj.getString("CustomerCode");
                    }
                    os.write(ec_pos.setBold(false));

                    if(!isNullOrEmpty(jobj.getString("GstRegNo").trim())){
                        textPrint = "TRN   : " + jobj.getString("GstRegNo").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    if (!isNullOrEmpty(jobj.getString("CreateUser"))) {
                        textPrint = "SR    : " +jobj.getString("CreateUser").trim() + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = " No Desc                    Qty   Price     Amt " + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
//                    int rsPosition = 0;
                    for(int i=0;i<countrsDET;i++) {
                        jobjDetail=jsonArrayDetail.getJSONObject(i);
                        os.write(ec_pos.select_fontB());
                        String sDescription = jobjDetail.getString("Description").trim();
                        String sShortDesc="";
                        if (sDescription.length() > 25) {
                            sDescription = sDescription.substring(0, 24);

                        }
                        if (i<= 8) {
                            textPrint = "  " + (i+1) + "  " + sDescription.trim();
                        } else {
                            textPrint = " " + (i+1) + "  " + sDescription.trim();
                        }
                        Double dQty = 0.0,WQty=0.0;
                        dQty = jobjDetail.getDouble("LQty");
                        WQty=jobjDetail.getDouble("WQty");
                        dTotalLQty+=dQty;
                        dTotalWQty+=WQty;
                        Double dPrice = 0.0,dDiscountAmount=0.0,dBillDiscountAmount=0.0;

                        Double dTotalValue = jobjDetail.getDouble("TotalValue");
                        dTotalAmount += dTotalValue;
                        dTotalValue+=dBillDiscountAmount;

                        String sTotalQty=String.format("%.0f", WQty)+"/"+String.format("%.0f", dQty);
                        textPrint += fncGetWhiteSpaces(31 - textPrint.length());
                        textPrint +=  fncGetWhiteSpaces(11 - sTotalQty.length())+sTotalQty ;
                        textPrint += fncGetWhiteSpaces(10 - fncFormatSeperator(dPrice).length()) + fncFormatSeperator(dPrice);
                        textPrint += fncGetWhiteSpaces(11 - fncFormatSeperator(dTotalValue).length()) + fncFormatSeperator(dTotalValue)+ "\r\n";
//                        textPrint += fncGetWhiteSpaces(6 - sSalesGSTType.length()) + sSalesGSTType;
                        os.write(textPrint.getBytes());
                        if(dDiscountAmount>0){
                            textPrint = fncGetWhiteSpaces(43)+"Discount";
                            textPrint += fncGetWhiteSpaces(11 - fncFormatSeperator(dDiscountAmount).length()) +"-"+fncFormatSeperator(dDiscountAmount)+ "\r\n";
                            os.write(textPrint.getBytes());
                        }

                    }
                    os.write(ec_pos.select_fontA());
                    textPrint = fncGetWhiteSpaces(22) + sSubBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    Double dGst=0.0,dsumSubTotal=0.0,dsumNetTotal=0.0;

                    if (dTotalLQty>0) {
                        String sTotLQty = String.format("%.0f", dTotalLQty);
                        String sTotWQty = String.format("%.0f", dTotalWQty);
                        String sTotAmount = jobj.getString("TotalValue");
                        String sTotalQty=sTotWQty+"/"+sTotLQty;

                        dsumSubTotal = Double.parseDouble(sTotAmount);

                        textPrint = fncGetWhiteSpaces(32 - sTotalQty.length()) + sTotalQty + fncGetWhiteSpaces(16 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());


//                    Double ,dsumNetTotal=0.0,dGst=0.0;
                    dGst=Double.parseDouble(jobj.getString("Gst"));
                    dsumNetTotal=dsumSubTotal+dGst;

                    if(dGst>0) {
                        textPrint = "TOTAL EXCL. TAX   : " + fncGetWhiteSpaces(28 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal) + "\r\n";
                        os.write(textPrint.getBytes());


                        Double dHDRGST = jobj.getDouble("Gst");
//                        String sHdrGst = String.format("%.2f", dHDRGST);
                        String sHdrGst = fncFormatSeperator(dHDRGST);
                        textPrint = "VAT (5%)          : ";
                        textPrint += fncGetWhiteSpaces(28 - fncFormatSeperator(dGst).length()) + fncFormatSeperator(dGst) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_on());
                        textPrint = "NET PAY INCL. TAX : " + fncGetWhiteSpaces(28 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());
                    }else {
                        os.write(emphasized_on());
                        textPrint = "NET PAYABLE       : " + fncGetWhiteSpaces(28 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                        os.write(textPrint.getBytes());
                        os.write(emphasized_off());
                    }
                    textPrint = sBorderLine + "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = "";
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                        /*textPrint = "\r\n";
                        os.write(textPrint.getBytes());*/
                    textPrint = sBorderLine+"\r\n";
                    os.write(textPrint.getBytes());

                    textPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
                    textPrint += "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());
                    textPrint = "\r\n";
                    os.write(textPrint.getBytes());

                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("***Print Exception", ex.toString());
        }
        return printSuccess;
    }
}
