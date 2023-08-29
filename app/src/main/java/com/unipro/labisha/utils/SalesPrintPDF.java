package com.unipro.labisha.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.sewoo.jpos.command.ESCPOSConst;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.unipro.labisha.utils.DbHelper.FilePath;


/**
 * Created by User1 on 09/08/2018.
 */
public class SalesPrintPDF {
    Context mContext;

    public SalesPrintPDF(Context applicationContext) {
        this.mContext=applicationContext;
    }

    public Boolean sPrintInvOnline_ws(String sInvoiceNo, String ImageString, String loc_Code,Boolean isReprint){
        Boolean printSuccess=false;
        Document doc = new Document();
        String pdfName="";
        if(!isReprint) {
            pdfName = "Droid" + sInvoiceNo + ".pdf";
        }else {
            pdfName = "DuplicateCopy" + sInvoiceNo + ".pdf";
        }
        try{
            File pdfDirPath = new File(FilePath+"/Receipt/");
            //pdfDirPath=new File("/storage/sdcard/RetailPOSSFA/");
            if(!pdfDirPath.exists()) {
                pdfDirPath.mkdirs();
            }
            File file = new File(pdfDirPath,pdfName);
            //Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.fileprovider", file);
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);
//                document.writeTo(os);
//                document.close();
            /*Intent in=new Intent(getActivity(),ClsPDFView.class);
            in.putExtra("sInvoiceNo",sInvoiceNo);
            startActivityForResult(in, 6);            */

        }catch (Exception e){
            e.printStackTrace();
        }
        try {

            String sSql = "";
            int countrsHDR=0;
            sSql="Select *,convert(varchar(10),CreateDate,108) as BillingTime,convert(varchar(10),InvoiceDate,101) as BillingDate from vwARInvoiceHeader where InvoiceNo = " + fncFormatSQLText(sInvoiceNo) + " and LocationCode = " + fncFormatSQLText(loc_Code);
//            sSql = "select *,Date(InvoiceDate) as BillingDate,Time(CreateDate) as BillingTime from tblARInvoiceHeader where Trim(InvoiceNo)=" + fncFormatSQLText(sInvoiceNo);
            Statement stmntInvHdr=null;
            ResultSet rsHDR=stmntInvHdr.executeQuery(sSql);
            if(rsHDR.next()){
                countrsHDR=1;
            }
//            Cursor crHDR =DB.rawQuery(sSql,null);
            sSql="select COUNT(*) as number from vwARInvoiceDetail where InvoiceNo ="+ fncFormatSQLText(sInvoiceNo);
            Log.e("***Count",sSql);
            Statement stmntCount=null;
            ResultSet rsCount=stmntCount.executeQuery(sSql);
            int countrsDET=0;
            if(rsCount.next()) {
                countrsDET = rsCount.getInt(1);
            }
            Log.e("***Count Resp",countrsDET+"");
            sSql="Select * from vwARInvoiceDetail where InvoiceNo = " + fncFormatSQLText(sInvoiceNo);
            Statement stmntInvDet=null;
            ResultSet rsDET=stmntInvDet.executeQuery(sSql);

//            sSql = "select * from tblARInvoiceDetail where Trim(InvoiceNo)=" + fncFormatSQLText(sInvoiceNo) + " order by InvoiceSlNo asc";
//            Cursor crDET =DB.rawQuery(sSql,null);
            OutputStream os;
            int iHeight = 430;
            iHeight = iHeight + (20*countrsDET);
            if(countrsDET>0 && countrsHDR>0)//rsDET.next() && rsHDR.next()
            {
//                crDET.moveToFirst();
//                crHDR.moveToFirst();
                if(!isNullOrEmpty(rsHDR.getString("CustomerName").trim()))
                {
                    iHeight=iHeight+10;

                }
                if(!isNullOrEmpty(rsHDR.getString("Address1").trim()))
                {
                    iHeight=iHeight+10;
                }
                if(!isNullOrEmpty(rsHDR.getString("Address2").trim()))
                {
                    iHeight=iHeight+10;
                }
                if(!isNullOrEmpty(rsHDR.getString("Address3").trim()))
                {
                    iHeight=iHeight+10;
                }
                if(!isNullOrEmpty(rsHDR.getString("Phone").trim()))
                {
                    iHeight=iHeight+10;
                }
                else if(!isNullOrEmpty(rsHDR.getString("Fax").trim()))
                {
                    iHeight=iHeight+10;
                }



                if(!isNullOrEmpty(rsHDR.getString("ShippingTo").trim()))
                {
                    iHeight=iHeight+10;
                }
                if(!isNullOrEmpty(rsHDR.getString("ShippingAddr1").trim()))
                {
                    iHeight=iHeight+10;
                }
                if(!isNullOrEmpty(rsHDR.getString("ShippingAddr2").trim()))
                {
                    iHeight=iHeight+10;
                }
                if(!isNullOrEmpty(rsHDR.getString("ShippingPhone").trim()))
                {
                    iHeight=iHeight+10;

                }
                else if(!isNullOrEmpty(rsHDR.getString("ShippingFax").trim()))
                {
                    iHeight=iHeight+10;
                }

            }
            if(isReprint){
                iHeight=iHeight+100;
            }else {
                if(ImageString!=null) {
                    iHeight = iHeight + 200;
                }else {
                    iHeight=iHeight+100;
                }
            }
            Rectangle size=new Rectangle(216,iHeight);
            doc.setPageSize(size);
            doc.setMargins(10,5,10,5);
            doc.open();
            int xPos = 0;
            int yPos=0;

            String sBorderLine = "_________________________________________";
            String sDottedLine = ".......................................";
//            sSql = "select * from tblCompany where Trim(CompanyID) = '1'";
            sSql="select * from tblCompany where CompanyId='1'";
            Statement stmtCmpny=null;
            ResultSet rsCompany=stmtCmpny.executeQuery(sSql);
//            Cursor crCompany = DB.rawQuery(sSql, null);
            String sFontPath = "fonts/cour.ttf";
            Typeface tfFont = Typeface.createFromAsset(mContext.getAssets(), sFontPath);
            Typeface Bold = Typeface.create(tfFont, Typeface.BOLD);
            Typeface Normal = Typeface.create(tfFont, Typeface.NORMAL);
            BaseFont bSfont = BaseFont.createFont("assets/fonts/cour.ttf", "UTF-8", BaseFont.EMBEDDED);
            String FONT = "assets/fonts/cour.ttf";
            Font paraFont = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// "Cp1250"
//            Font paraFont = new Font(bSfont,8.0f,Font.NORMAL, harmony.java.awt.Color.BLACK);
//            Font paraFont= new Font(Font.COURIER,8,Color.BLACK);
            paraFont.setSize(8.0f);
            paraFont.setStyle(Font.BOLD);
            Font paraFontNormal = FontFactory.getFont(FONT, "Cp1250", BaseFont.EMBEDDED);
            paraFontNormal.setSize(8.0f);
            paraFontNormal.setStyle(Font.NORMAL);
            Font paraFont9 = FontFactory.getFont(FONT,BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// "Cp1250"
            paraFont9.setSize(9.0f);
            paraFont9.setStyle(Font.BOLD);
            Font paraFontNormal9 = FontFactory.getFont(FONT,BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// "Cp1250"
            paraFontNormal9.setSize(9.0f);
            paraFontNormal9.setStyle(Font.NORMAL);
//            crCompany.moveToFirst();
            if (rsCompany.next()) {

                String sCompanyName = rsCompany.getString(("Name")).trim();
                String sCompanyAddress1 = rsCompany.getString(("Address1")).trim();
                String sCompanyAddress2 = rsCompany.getString(("Address2")).trim();
                String sCompanyCountry = rsCompany.getString(("Country")).trim();
                String sCompanyPostalCode = rsCompany.getString(("PostalCode")).trim();
                String sCompanyPhone = rsCompany.getString(("Phone")).trim();
                String sCompanyFax = rsCompany.getString(("Fax")).trim();
                String sCompanyGSTRegNo = rsCompany.getString(("GSTRegNo")).trim();
                rsCompany.close();
                // yPos = yPos + 10;
                yPos = yPos + 10;
                Paint paint = new Paint();
                paint.setTypeface(tfFont);
                paint.setColor(Color.BLACK);
                paint.setTextSize(8);
                paint.setTypeface(Bold);
                if(isReprint) {
                    doc.add(new Paragraph("Duplicate Copy", paraFontNormal));
                }
                doc.add(new Paragraph(sCompanyName,paraFont));
                yPos = yPos + 10;
                doc.add(new Paragraph(sCompanyAddress1,paraFontNormal));
                yPos = yPos + 10;
                doc.add(new Paragraph(sCompanyAddress2,paraFontNormal));
                yPos = yPos + 10;
                doc.add(new Paragraph(sCompanyCountry + "-" + sCompanyPostalCode,paraFontNormal));
                yPos = yPos + 10;
                doc.add(new Paragraph("Ph : " + sCompanyPhone + "  " + "Fax : " + sCompanyFax,paraFontNormal));
                yPos = yPos + 10;
                doc.add(new Paragraph("GSTRegNo : "  + sCompanyGSTRegNo,paraFontNormal));
                yPos = yPos + 5;
                doc.add(new Paragraph(sBorderLine, paraFontNormal));
                yPos = yPos + 10;
                doc.add(new Paragraph("TAX INVOICE",paraFont9));
                yPos = yPos + 10;
                paint.setTypeface(Normal);
                sSql="";
                Double dsGSTTotal=0.00,dzGSTTotal=0.00;
                if(countrsHDR>0 && countrsDET>0)
                {
                    doc.add(new Paragraph("Tax Invoice No : " + sInvoiceNo, paraFontNormal));
                    yPos = yPos + 10;
                    doc.add(new Paragraph("Invoice Date   : " + rsHDR.getString("BillingDate") + " " + rsHDR.getString("BillingTime"),paraFontNormal));
                    yPos = yPos + 10;
                    doc.add(new Paragraph("Terms          : " + rsHDR.getString("Terms"), paraFontNormal));
                    yPos = yPos + 5;
                    doc.add(new Paragraph(sBorderLine, paraFontNormal));
                    yPos=yPos+10;
                    doc.add(new Paragraph("S.No Desc/Qty  Price   Amount  Type",paraFont));
                    yPos=yPos+5;
                    doc.add(new Paragraph(sBorderLine, paraFontNormal));
//                    for (crDET.moveToFirst(); !crDET.isAfterLast(); crDET.moveToNext()) {
                    int rsPosition=0;
                    while (rsDET.next()){
                        yPos = yPos + 10;
                        String sDescription = rsDET.getString("Description").trim();
                        if (sDescription.length() > 20) {
                            sDescription = sDescription.substring(1, 20);
                        }
                        doc.add(new Paragraph("  " + ( rsPosition + 1) + "  " + rsDET.getString("InventoryCode").trim() + "-" + sDescription.trim() + "-" + rsDET.getString("LUOMCode").trim(),paraFontNormal));
                        yPos = yPos + 10;
                        Double dQty = rsDET.getDouble("LQty");
                        Double dPrice = rsDET.getDouble("GrossPrice");
                        Double dTotalValue = rsDET.getDouble("TotalValue");
                        Double dSalesGSTPerc = rsDET.getDouble("GSTPerc");
                        String sSalesGSTType = rsDET.getString("TaxType").trim();
                        Paragraph p10=new Paragraph(fncGetWhiteSpaces(11 - String.format("%.2f", dQty).length()) + String.format("%.2f", dQty),paraFontNormal);
//                        doc.add(p10);
                        p10.add(fncGetWhiteSpaces(9-String.format("%.2f", dPrice).length()) + String.format("%.2f", dPrice));
//                        doc.add(p10);
                        p10.add(fncGetWhiteSpaces(9-String.format("%.2f", dTotalValue).length()) + String.format("%.2f", dTotalValue));
//                        doc.add(p10);
                        p10.add(fncGetWhiteSpaces(2) + sSalesGSTType);
                        doc.add(p10);
                        if (dSalesGSTPerc>0) {
                            dsGSTTotal = dsGSTTotal + rsDET.getDouble("TotalValue");
                        }
                        else
                        {
                            dzGSTTotal = dzGSTTotal + rsDET.getDouble("TotalValue");
                        }
                        rsPosition++;
                    }
                    yPos=yPos+5;
                    doc.add(new Paragraph(sBorderLine, paraFontNormal));
                    yPos=yPos+10;
                    Paragraph psub=new Paragraph(fncGetWhiteSpaces(6) + "Sub Total",paraFontNormal);
                    Double dHDRTotalValue  = rsHDR.getDouble("TotalValue") - (rsHDR.getDouble("NetDiscount")+rsHDR.getDouble("TransportAmount"));
                    psub.add(fncGetWhiteSpaces(25-String.format("%.2f", dHDRTotalValue).length()) + String.format("%.2f", dHDRTotalValue));
                    doc.add(psub);
                    yPos=yPos+10;
                    Paragraph pGst=new Paragraph(fncGetWhiteSpaces(6)+ "GST",paraFontNormal);
                    Double dHDRGST  = rsHDR.getDouble("Gst");
                    pGst.add(fncGetWhiteSpaces(31-String.format("%.2f", dHDRGST).length()) + String.format("%.2f", dHDRGST));
                    doc.add(pGst);
                    yPos=yPos+10;
                    Double dHDRNetTotal  = rsHDR.getDouble("NetTotal");
                    //doc.add(new Paragraph(fncGetWhiteSpaces(23-String.format("%.2f",dHDRNetTotal).length()) + String.format("%.2f",dHDRNetTotal)));
                    Paragraph pGTotal=new Paragraph(fncGetWhiteSpaces(6) + "Grand Total", paraFontNormal);
                    pGTotal.add(fncGetWhiteSpaces(23-String.format("%.2f", dHDRNetTotal).length()) + String.format("%.2f", dHDRNetTotal));
                    doc.add(pGTotal);
                    yPos=yPos+5;
                    doc.add(new Paragraph(sBorderLine,paraFontNormal));
                    yPos=yPos+10;
                    doc.add(new Paragraph("CUSTOMER : ", paraFont));
                    if(!isNullOrEmpty(rsHDR.getString("CustomerName").trim()))
                    {
                        yPos=yPos+10;
                        doc.add(new Paragraph(rsHDR.getString("CustomerName").trim(),paraFontNormal));
                    }
                    if(!isNullOrEmpty(rsHDR.getString("Address1").trim()))
                    {
                        yPos=yPos+10;
                        doc.add(new Paragraph(rsHDR.getString("Address1").trim(),paraFontNormal));
                    }
                    if(!isNullOrEmpty(rsHDR.getString("Address2").trim()))
                    {
                        yPos=yPos+10;
                        doc.add(new Paragraph(rsHDR.getString("Address2").trim(),paraFontNormal));
                    }
                    if(!isNullOrEmpty(rsHDR.getString("Address3").trim()))
                    {
                        yPos=yPos+10;
                        doc.add(new Paragraph(rsHDR.getString("Address3").trim(),paraFontNormal));
                    }
                    if(!isNullOrEmpty(String.valueOf(rsHDR.getString("Phone")).trim()))
                    {
                        String sContact="";
                        yPos=yPos+10;
                        sContact = "Phone : " + rsHDR.getString("Phone").trim();
                        if(!isNullOrEmpty(rsHDR.getString("Fax").trim()))
                        {
                            sContact =sContact + "  Fax : "  + rsHDR.getString("Fax").trim();
                        }
                        doc.add(new Paragraph(sContact,paraFontNormal));
                    }
                    else if(!isNullOrEmpty(rsHDR.getString("Fax").trim()))
                    {
                        yPos=yPos+10;
                        doc.add(new Paragraph( "Fax : "  + rsHDR.getString("Fax").trim(), paraFontNormal));
                    }
                    yPos=yPos+20;
                    doc.add( Chunk.NEWLINE);
                    doc.add(new Paragraph( "DELIVER TO : ",paraFont));

                    if(!isNullOrEmpty(rsHDR.getString("ShippingTo").trim()))
                    {
                        yPos=yPos+10;
                        doc.add(new Paragraph( rsHDR.getString("ShippingTo").trim(),paraFontNormal));
                    }
                    if(!isNullOrEmpty(rsHDR.getString("ShippingAddr1").trim()))
                    {
                        yPos=yPos+10;
                        doc.add(new Paragraph(rsHDR.getString("ShippingAddr1").trim(),paraFontNormal));
                    }
                    if(!isNullOrEmpty(rsHDR.getString("ShippingAddr2").trim()))
                    {
                        yPos=yPos+10;
                        doc.add(new Paragraph(rsHDR.getString("ShippingAddr2").trim(),paraFontNormal));
                    }
                    if(!isNullOrEmpty(rsHDR.getString("ShippingPhone").trim()))
                    {
                        String sContact="";
                        yPos=yPos+10;
                        sContact = "Phone : " + rsHDR.getString("ShippingPhone").trim();
                        if(!isNullOrEmpty(rsHDR.getString("ShippingFax").trim()))
                        {
                            sContact =sContact +  "  Fax : "  + rsHDR.getString("ShippingFax").trim();
                        }
                        doc.add(new Paragraph(sContact,paraFontNormal));
                    }
                    else if(!isNullOrEmpty(rsHDR.getString("ShippingFax").trim()))
                    {
                        yPos=yPos+10;
                        doc.add(new Paragraph("Fax : "  + rsHDR.getString("ShippingFax").trim(),paraFontNormal));
                    }
                    yPos=yPos+10;
                    doc.add(new Paragraph(sBorderLine, paraFontNormal));
                    yPos=yPos+10;
                    doc.add(new Paragraph("Goods sold are not returnable", paraFontNormal));
                    yPos=yPos+10;
                    doc.add(new Paragraph("Received the above goods in ", paraFontNormal));
                    yPos=yPos+10;
                    doc.add(new Paragraph("good order and condition",paraFontNormal));
                    yPos=yPos+10;
                    yPos=yPos+10;
                    doc.add(Chunk.NEWLINE);
                    doc.add(new Paragraph("GST Summary     GOODSGST      Value", paraFont));
                    yPos=yPos+10;
                    Paragraph pGstsum=new Paragraph("    6%", paraFontNormal);
//                    doc.add(new Paragraph(fncGetWhiteSpaces(24 - String.format("%.2f", dsGSTTotal).length()) + String.format("%.2f", dsGSTTotal), paraFontNormal));
                    pGstsum.add(fncGetWhiteSpaces(18- String.format("%.2f", dsGSTTotal).length()) + String.format("%.2f", dsGSTTotal));
//                    doc.add(new Paragraph(fncGetWhiteSpaces(34 - String.format("%.2f", crHDR.getDouble(crHDR.getColumnIndex("Gst"))).length()) + String.format("%.2f", crHDR.getDouble(crHDR.getColumnIndex("Gst"))), paraFontNormal));
                    pGstsum.add(fncGetWhiteSpaces(11- String.format("%.2f", rsHDR.getDouble("Gst")).length()) + String.format("%.2f", rsHDR.getDouble("Gst")));
                    doc.add(pGstsum);
//                    doc.add(new Paragraph("    6%", paraFontNormal));
                    yPos=yPos+10;
//                    doc.add(new Paragraph("    0%                        0.00", paraFontNormal));
                    Paragraph pSgst=new Paragraph("    0%              0.00", paraFontNormal);
//                    doc.add(new Paragraph(fncGetWhiteSpaces(24 - String.format("%.2f", dzGSTTotal).length()) + String.format("%.2f", dzGSTTotal), paraFontNormal));
                    pSgst.add(fncGetWhiteSpaces(11 - String.format("%.2f", dzGSTTotal).length()) + String.format("%.2f", dzGSTTotal));
                    doc.add(pSgst);
                    yPos=yPos+40;
                    doc.add(new Paragraph(sDottedLine,paraFontNormal));
                    yPos=yPos+10;
                    doc.add(new Paragraph("      CUSTOMER", paraFont9));
                    yPos=yPos+40;
                    doc.add(new Paragraph(sDottedLine,paraFontNormal));
                    yPos=yPos+10;
                    doc.add(new Paragraph("      " + sCompanyName, paraFont9));
                }
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e("***Print Exception",ex.toString());
        }finally {
            doc.close();
//            printSuccess=fncPdftobitmap(pdfName);
            printSuccess=true;
            Log.e("***Print","Print sucess");
        }

        return printSuccess;
    }

    public Boolean fncPrint_sales_48(String sData, String sImg, String loc_code, boolean isSalesInvoice, boolean isReprint) {
        try{
            String sDetail="";
            String sDocPrint="";
            Document doc = new Document();
            String pdfName="";
            int countDET = 0,countHdr=0;
            Boolean isGstSales=false,isPrintSuccess=false;
            Double dTotalFocQty = 0.00, dTotalWQty  = 0.00, dTotalLQty = 0.0, dTotalAmount = 0.0;
            String sCompanyName,sCompanyAddress1,sCompanyAddress2,sCompanyCountry;
            String sCompanyPostalCode,sCompanyPhone,sCompanyFax,sTaxRegNo;
            JSONArray jsonArray=new JSONArray(sData);
            JSONArray jsonArrayDetail=null;
            JSONObject jobj=null;
            countHdr=jsonArray.length();
            if (jsonArray.length()>0) {
                jobj=jsonArray.getJSONObject(0);
                sDetail=jobj.getString("sDetail");
                jsonArrayDetail=new JSONArray(sDetail);
            }
            if (jsonArrayDetail.length()>0) {
                countDET = jsonArrayDetail.length();
            }

            if (jsonArray.length()>0) {
                sCompanyName = jobj.getString(("Name")).trim();
                sCompanyAddress1 = jobj.getString(("Address1")).trim();
                sCompanyAddress2 = jobj.getString(("Address2")).trim();
                sCompanyCountry = jobj.getString(("Country")).trim();
                sCompanyPostalCode = jobj.getString(("PostalCode")).trim();
                sCompanyPhone = jobj.getString(("Phone"));
                sCompanyFax = jobj.getString(("Fax"));
                sTaxRegNo = jobj.getString("GSTRegNo").trim();
                if (sTaxRegNo.equalsIgnoreCase("")) {
                    isGstSales = false;
                } else {
                    isGstSales = true;
                }
                if(!isReprint) {
                    pdfName = "Droid" + jobj.getString("sSoNo") + ".pdf";
                }else {
                    pdfName = "DuplicateCopy" + jobj.getString("sSoNo") + ".pdf";
                }
                try{
                    File pdfDirPath = new File(FilePath+"/Receipt/");
                    if(!pdfDirPath.exists()) {
                        pdfDirPath.mkdirs();
                    }
                    File file = new File(pdfDirPath,pdfName);
                    FileOutputStream fOut = new FileOutputStream(file);
                    PdfWriter.getInstance(doc, fOut);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    OutputStream os=null;
                    int iHeight = 430;
                    iHeight = iHeight + (20*countDET);
                    if(countDET>0 && countHdr>0) {
                        if(!isNullOrEmpty(jobj.getString("CustomerName").trim()))
                        {
                            iHeight=iHeight+10;

                        }
                        if(!isNullOrEmpty(jobj.getString("CustAddress1").trim()))
                        {
                            iHeight=iHeight+10;
                        }
                        if(!isNullOrEmpty(jobj.getString("CustAddress2").trim()))
                        {
                            iHeight=iHeight+10;
                        }
                        if(!isNullOrEmpty(jobj.getString("CustPhone").trim()))
                        {
                            iHeight=iHeight+10;
                        }
                        else if(!isNullOrEmpty(jobj.getString("CustFax").trim()))
                        {
                            iHeight=iHeight+10;
                        }

                    }
                 /*   if(isReprint){
                        iHeight=iHeight+100;
                    }else {
                        if(ImageString!=null) {
                            iHeight = iHeight + 200;
                        }else {
                            iHeight=iHeight+100;
                        }
                    }*/
                    Rectangle size=new Rectangle(320,iHeight);
                    doc.setPageSize(size);
                    doc.setMargins(10,5,10,5);
                    doc.open();
                    int xPos = 0;
                    int yPos=0;

                    String sBorderLine = "------------------------------------------------";
                    String sDottedLine = "................................................";
                    String sFontPath = "fonts/cour.ttf";
                    Typeface tfFont = Typeface.createFromAsset(mContext.getAssets(), sFontPath);
                    Typeface Bold = Typeface.create(tfFont, Typeface.BOLD);
                    Typeface Normal = Typeface.create(tfFont, Typeface.NORMAL);
                    BaseFont bSfont = BaseFont.createFont("assets/fonts/cour.ttf", "UTF-8", BaseFont.EMBEDDED);
                    String FONT = "assets/fonts/cour.ttf";
                    Font paraFont = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// "Cp1250"
                    paraFont.setSize(8.0f);
                    paraFont.setStyle(Font.BOLD);
                    Font paraFontNormal = FontFactory.getFont(FONT, "Cp1250", BaseFont.EMBEDDED);
                    paraFontNormal.setSize(8.0f);
                    paraFontNormal.setStyle(Font.NORMAL);
                    Font paraFont9 = FontFactory.getFont(FONT,BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// "Cp1250"
                    paraFont9.setSize(9.0f);
                    paraFont9.setStyle(Font.BOLD);
                    Font paraFontNormal9 = FontFactory.getFont(FONT,BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// "Cp1250"
                    paraFontNormal9.setSize(9.0f);
                    paraFontNormal9.setStyle(Font.NORMAL);


                        yPos = yPos + 10;
                        Paint paint = new Paint();
                        paint.setTypeface(tfFont);
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(8);
                        paint.setTypeface(Bold);
                        if(isReprint) {
                            doc.add(new Paragraph("Duplicate Copy", paraFontNormal));
                        }
                        doc.add(new Paragraph(sCompanyName,paraFont));
                        yPos = yPos + 10;
                        doc.add(new Paragraph(sCompanyAddress1,paraFontNormal));
                        yPos = yPos + 10;
                        doc.add(new Paragraph(sCompanyAddress2,paraFontNormal));
                        yPos = yPos + 10;
                        doc.add(new Paragraph(sCompanyCountry + "-" + sCompanyPostalCode,paraFontNormal));
                        yPos = yPos + 10;
                        doc.add(new Paragraph("Ph : " + sCompanyPhone + "  " + "Fax : " + sCompanyFax,paraFontNormal));
                        yPos = yPos + 10;
                        doc.add(new Paragraph("GSTRegNo : "  + sTaxRegNo,paraFontNormal));
                        yPos = yPos + 5;
                        doc.add(new Paragraph(sBorderLine, paraFontNormal));
                        yPos = yPos + 10;
                        doc.add(new Paragraph("SALES ORDER",paraFont9));
                        yPos = yPos + 10;
                        paint.setTypeface(Normal);
                        Double dsGSTTotal=0.00,dzGSTTotal=0.00;
                        if(countHdr>0 && countDET>0) {
                            doc.add(new Paragraph("SalesOrder  No : " + jobj.getString("sSoNo").trim(), paraFontNormal));
                            yPos = yPos + 10;
                            doc.add(new Paragraph("Date           : " + jobj.getString("BillingDate") + " " + jobj.getString("BillingTime"),paraFontNormal));
                            yPos = yPos + 10;
                            /*doc.add(new Paragraph("Terms          : " + rsHDR.getString("Terms"), paraFontNormal));
                            yPos = yPos + 5;*/
                            if (!isNullOrEmpty(jobj.getString("sCustomerCode"))) {
                                doc.add(new Paragraph("Cust Code      : " + jobj.getString("sCustomerCode"),paraFontNormal));
                                yPos = yPos + 10;
                            }
                            if (!isNullOrEmpty(jobj.getString("CustomerName"))) {
                                doc.add(new Paragraph(jobj.getString("CustomerName"),paraFontNormal));
                                yPos = yPos + 10;
                            }
                            if (!isNullOrEmpty(jobj.getString("SalesManID"))) {
                                doc.add(new Paragraph("SR    : " +jobj.getString("SalesManID").trim(),paraFontNormal));
                                yPos = yPos + 10;
                            }
                            doc.add(new Paragraph(sBorderLine, paraFontNormal));
                            yPos=yPos+10;
                            doc.add(new Paragraph(" No Desc                 Qty/FOC  Price     Amt ",paraFont));
                            yPos=yPos+5;
                            doc.add(new Paragraph(sBorderLine, paraFontNormal));
                            int rsPosition=0;
                            for(int i=0;i<countDET;i++) {
                                JSONObject jobjDetail=jsonArrayDetail.getJSONObject(i);
                                Double dQty = 0.0,FOCQty=0.0,WQty=0.0;
                                Double dPrice = 0.0,dDiscountAmount=0.0,dBillDiscountAmount=0.0;
                                yPos = yPos + 10;
                                String sDescription =jobjDetail.getString("sDescription").trim();
                                if (sDescription.length() > 25) {
                                    sDescription = sDescription.substring(0, 24);
                                }
                                if (i<= 8) {
                                    sDocPrint = "  " + (i+1) + "  " + sDescription.trim();
                                } else {
                                    sDocPrint = " " + (i+1) + "  " + sDescription.trim();
                                }
                                dQty = jobjDetail.getDouble("sLQty");
                                FOCQty=jobjDetail.getDouble("sFocQty");
                                WQty=jobjDetail.getDouble("sCtnQty");
                                dTotalLQty+=dQty;
                                dTotalFocQty+=FOCQty;
                                dTotalWQty+=WQty;
                                dPrice = jobjDetail.getDouble("sPrice");
                                dDiscountAmount=jobjDetail.getDouble("sDiscount");
                                Double dTotalValue = jobjDetail.getDouble("sTotalAmnt");
                                dTotalAmount += dTotalValue;
                                dTotalValue+=dBillDiscountAmount;

                                String sTotalQty=String.format("%.0f", WQty)+"/"+String.format("%.0f", dQty)+"/"+String.format("%.0f", FOCQty);

                                sDocPrint += fncGetWhiteSpaces(31 - sDocPrint.length());
                                /*doc.add(new Paragraph(sDocPrint.trim(),paraFontNormal));
                                yPos = yPos + 10;*/
                                sDocPrint += fncGetWhiteSpaces(32 - sTotalQty.length()) + sTotalQty;
                                sDocPrint += fncGetWhiteSpaces(8 - fncFormatSeperator(dPrice).length()) + fncFormatSeperator(dPrice);
                                sDocPrint += fncGetWhiteSpaces(8 - fncFormatSeperator(dTotalValue).length()) + fncFormatSeperator(dTotalValue);
                                doc.add(new Paragraph(sDocPrint.trim(),paraFontNormal));
                                yPos = yPos + 10;

                                if(dDiscountAmount>0){
                                    sDocPrint = fncGetWhiteSpaces(43)+"Discount";
                                    sDocPrint += fncGetWhiteSpaces(11 - fncFormatSeperator(dDiscountAmount).length()) +"-"+fncFormatSeperator(dDiscountAmount)+ "\r\n";
                                    doc.add(new Paragraph(sDocPrint.trim(),paraFontNormal));
                                    yPos = yPos + 10;
                                }

                              /*  Paragraph p10=new Paragraph(fncGetWhiteSpaces(11 - String.format("%.2f", dQty).length()) + String.format("%.2f", dQty),paraFontNormal);
//                        doc.add(p10);
                                p10.add(fncGetWhiteSpaces(9-String.format("%.2f", dPrice).length()) + String.format("%.2f", dPrice));
//                        doc.add(p10);
                                p10.add(fncGetWhiteSpaces(9-String.format("%.2f", dTotalValue).length()) + String.format("%.2f", dTotalValue));
//                        doc.add(p10);
                                p10.add(fncGetWhiteSpaces(2) + sSalesGSTType);
                                doc.add(p10);
                                if (dSalesGSTPerc>0) {
                                    dsGSTTotal = dsGSTTotal + rsDET.getDouble("TotalValue");
                                }
                                else
                                {
                                    dzGSTTotal = dzGSTTotal + rsDET.getDouble("TotalValue");
                                }*/
                                rsPosition++;
                            }
                            yPos=yPos+5;
                            String sSubBorderLine = "--------------------------";
                            sDocPrint = fncGetWhiteSpaces(22) + sSubBorderLine ;
                            doc.add(new Paragraph(sDocPrint, paraFontNormal));
                            yPos=yPos+10;
                            Double dGst=0.0,dsumSubTotal=0.0,dsumNetTotal=0.0;

                            if (dTotalLQty>0) {
                                String sTotLQty = String.format("%.0f", dTotalLQty);
                                String sTotWQty = String.format("%.0f", dTotalWQty);
                                String sTotFocQty = String.format("%.0f", dTotalFocQty);
                                String sTotAmount = jobj.getString("sOSubTotal");
                                String sTotalQty=sTotWQty+"/"+sTotLQty+"/"+sTotFocQty;

                                dsumSubTotal = Double.parseDouble(sTotAmount);

                                sDocPrint = fncGetWhiteSpaces(32 - sTotalQty.length()) + sTotalQty + fncGetWhiteSpaces(16 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal);
                                doc.add(new Paragraph(sDocPrint, paraFontNormal));
                                yPos=yPos+10;
                            }
                            doc.add(new Paragraph(sBorderLine, paraFontNormal));
                            yPos=yPos+10;

                            String sBillDisc=String.format("%.2f", jobj.getDouble("sBillDisc"));
                            dsumNetTotal=dsumSubTotal+dGst;
                            dsumNetTotal=dsumNetTotal-Double.parseDouble(sBillDisc);
                            if(dGst>0) {
                                sDocPrint = "TOTAL EXCL. TAX   : " + fncGetWhiteSpaces(28 - fncFormatSeperator(dsumSubTotal).length()) + fncFormatSeperator(dsumSubTotal) + "\r\n";
                                doc.add(new Paragraph(sDocPrint, paraFontNormal));
                                yPos=yPos+10;

                                sDocPrint = "Bill Discount     : " + fncGetWhiteSpaces(28 - sBillDisc.length()) + sBillDisc + "\r\n";
                                doc.add(new Paragraph(sDocPrint, paraFontNormal));
                                yPos=yPos+10;

                                Double dHDRGST = jobj.getDouble("Gst");
                                String sHdrGst = fncFormatSeperator(dHDRGST);
                                sDocPrint = "VAT (5%)          : ";
                                sDocPrint += fncGetWhiteSpaces(28 - fncFormatSeperator(dGst).length()) + fncFormatSeperator(dGst) + "\r\n";
                                doc.add(new Paragraph(sDocPrint, paraFontNormal));
                                yPos=yPos+10;
                                sDocPrint = "NET PAY INCL. TAX : " + fncGetWhiteSpaces(28 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                                doc.add(new Paragraph(sDocPrint, paraFontNormal));
                                yPos=yPos+10;
                            }else {
                                sDocPrint = "Bill Discount     : " + fncGetWhiteSpaces(28 - sBillDisc.length()) + sBillDisc + "\r\n";
                                doc.add(new Paragraph(sDocPrint, paraFontNormal));
                                yPos=yPos+10;
                                sDocPrint = "NET PAYABLE       : " + fncGetWhiteSpaces(28 - fncFormatSeperator(dsumNetTotal).length()) + fncFormatSeperator(dsumNetTotal) + "\r\n";
                                doc.add(new Paragraph(sDocPrint, paraFontNormal));
                                yPos=yPos+10;
                            }
                            doc.add(new Paragraph(sBorderLine,paraFontNormal));
                            yPos=yPos+10;
                            /*doc.add(new Paragraph("CUSTOMER : ", paraFont));
                            if(!isNullOrEmpty(rsHDR.getString("CustomerName").trim()))
                            {
                                yPos=yPos+10;
                                doc.add(new Paragraph(rsHDR.getString("CustomerName").trim(),paraFontNormal));
                            }
                            if(!isNullOrEmpty(rsHDR.getString("Address1").trim()))
                            {
                                yPos=yPos+10;
                                doc.add(new Paragraph(rsHDR.getString("Address1").trim(),paraFontNormal));
                            }
                            if(!isNullOrEmpty(rsHDR.getString("Address2").trim()))
                            {
                                yPos=yPos+10;
                                doc.add(new Paragraph(rsHDR.getString("Address2").trim(),paraFontNormal));
                            }
                            if(!isNullOrEmpty(rsHDR.getString("Address3").trim()))
                            {
                                yPos=yPos+10;
                                doc.add(new Paragraph(rsHDR.getString("Address3").trim(),paraFontNormal));
                            }
                            if(!isNullOrEmpty(String.valueOf(rsHDR.getString("Phone")).trim()))
                            {
                                String sContact="";
                                yPos=yPos+10;
                                sContact = "Phone : " + rsHDR.getString("Phone").trim();
                                if(!isNullOrEmpty(rsHDR.getString("Fax").trim()))
                                {
                                    sContact =sContact + "  Fax : "  + rsHDR.getString("Fax").trim();
                                }
                                doc.add(new Paragraph(sContact,paraFontNormal));
                            }
                            else if(!isNullOrEmpty(rsHDR.getString("Fax").trim()))
                            {
                                yPos=yPos+10;
                                doc.add(new Paragraph( "Fax : "  + rsHDR.getString("Fax").trim(), paraFontNormal));
                            }
                            yPos=yPos+20;
                            doc.add( Chunk.NEWLINE);
                            doc.add(new Paragraph( "DELIVER TO : ",paraFont));

                            if(!isNullOrEmpty(rsHDR.getString("ShippingTo").trim()))
                            {
                                yPos=yPos+10;
                                doc.add(new Paragraph( rsHDR.getString("ShippingTo").trim(),paraFontNormal));
                            }
                            if(!isNullOrEmpty(rsHDR.getString("ShippingAddr1").trim()))
                            {
                                yPos=yPos+10;
                                doc.add(new Paragraph(rsHDR.getString("ShippingAddr1").trim(),paraFontNormal));
                            }
                            if(!isNullOrEmpty(rsHDR.getString("ShippingAddr2").trim()))
                            {
                                yPos=yPos+10;
                                doc.add(new Paragraph(rsHDR.getString("ShippingAddr2").trim(),paraFontNormal));
                            }
                            if(!isNullOrEmpty(rsHDR.getString("ShippingPhone").trim()))
                            {
                                String sContact="";
                                yPos=yPos+10;
                                sContact = "Phone : " + rsHDR.getString("ShippingPhone").trim();
                                if(!isNullOrEmpty(rsHDR.getString("ShippingFax").trim()))
                                {
                                    sContact =sContact +  "  Fax : "  + rsHDR.getString("ShippingFax").trim();
                                }
                                doc.add(new Paragraph(sContact,paraFontNormal));
                            }
                            else if(!isNullOrEmpty(rsHDR.getString("ShippingFax").trim()))
                            {
                                yPos=yPos+10;
                                doc.add(new Paragraph("Fax : "  + rsHDR.getString("ShippingFax").trim(),paraFontNormal));
                            }
                            yPos=yPos+10;*/
                            doc.add(Chunk.NEWLINE);
                            doc.add(Chunk.NEWLINE);
                            doc.add(Chunk.NEWLINE);
                            doc.add(Chunk.NEWLINE);
                            sDocPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
                            doc.add(new Paragraph(sDocPrint.trim(),paraFontNormal));
                            yPos=yPos+10;
                            doc.add(Chunk.NEWLINE);
                            doc.add(Chunk.NEWLINE);
                            doc.add(Chunk.NEWLINE);
                            doc.add(Chunk.NEWLINE);
                        }

                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    Log.e("***Print Exception",ex.toString());
                }finally {
                    doc.close();
//            printSuccess=fncPdftobitmap(pdfName);
                    isPrintSuccess=true;
                    Log.e("***Print", "Print sucess");
                }

            }
            return isPrintSuccess;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Boolean fncPrint_Pay_48(String sData,String sBitmap,String loc_Code,Boolean isReprint){
       try{
           String sDocPrint="",sDetail="";
           Document doc = new Document();
           String pdfName="";
           int countDET = 0,countHdr=0;
           Boolean isGstSales=false,isPrintSuccess=false;
           String sCompanyName,sCompanyAddress1,sCompanyAddress2,sCompanyCountry;
           String sCompanyPostalCode,sCompanyPhone,sCompanyFax,sTaxRegNo;
           JSONArray jsonArray=new JSONArray(sData);
           JSONArray jsonArrayDetail=null;
           JSONObject jobj=null;
           countHdr=jsonArray.length();
           if (jsonArray.length()>0) {
               jobj=jsonArray.getJSONObject(0);
               sDetail=jobj.getString("sDetail");
               jsonArrayDetail=new JSONArray(sDetail);
           }
           if (jsonArrayDetail.length()>0) {
               countDET = jsonArrayDetail.length();
           }

           if (jsonArray.length()>0) {
               sCompanyName = jobj.getString(("Name")).trim();
               sCompanyAddress1 = jobj.getString(("Address1")).trim();
               sCompanyAddress2 = jobj.getString(("Address2")).trim();
               sCompanyCountry = jobj.getString(("Country")).trim();
               sCompanyPostalCode = jobj.getString(("PostalCode")).trim();
               sCompanyPhone = jobj.getString(("Phone"));
               sCompanyFax = jobj.getString(("Fax"));
               sTaxRegNo = jobj.getString("GSTRegNo").trim();
               if (sTaxRegNo.equalsIgnoreCase("")) {
                   isGstSales = false;
               } else {
                   isGstSales = true;
               }
               if (!isReprint) {
                   pdfName = "Collection" + jobj.getString("EntryNo") + ".pdf";
               } else {
                   pdfName = "DuplicateCopy" + jobj.getString("EntryNo") + ".pdf";
               }
               try {
                   File pdfDirPath = new File(FilePath + "/Receipt/");
                   if (!pdfDirPath.exists()) {
                       pdfDirPath.mkdirs();
                   }
                   File file = new File(pdfDirPath, pdfName);
                   FileOutputStream fOut = new FileOutputStream(file);
                   PdfWriter.getInstance(doc, fOut);
               } catch (Exception e) {
                   e.printStackTrace();
               }
               try {
                   OutputStream os = null;
                   int iHeight = 430;
                   iHeight = iHeight + (20 * countDET);
                   if (countDET > 0 && countHdr > 0) {
                       if (!isNullOrEmpty(jobj.getString("CustomerName").trim())) {
                           iHeight = iHeight + 10;

                       }
                   }
                   Rectangle size = new Rectangle(320, iHeight);
                   doc.setPageSize(size);
                   doc.setMargins(10, 5, 10, 5);
                   doc.open();
                   int xPos = 0;
                   int yPos = 0;

                   String sBorderLine = "------------------------------------------------";
                   String sDottedLine = "................................................";
                   String sFontPath = "fonts/cour.ttf";
                   Typeface tfFont = Typeface.createFromAsset(mContext.getAssets(), sFontPath);
                   Typeface Bold = Typeface.create(tfFont, Typeface.BOLD);
                   Typeface Normal = Typeface.create(tfFont, Typeface.NORMAL);
                   BaseFont bSfont = BaseFont.createFont("assets/fonts/cour.ttf", "UTF-8", BaseFont.EMBEDDED);
                   String FONT = "assets/fonts/cour.ttf";
                   Font paraFont = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// "Cp1250"
                   paraFont.setSize(8.0f);
                   paraFont.setStyle(Font.BOLD);
                   Font paraFontNormal = FontFactory.getFont(FONT, "Cp1250", BaseFont.EMBEDDED);
                   paraFontNormal.setSize(8.0f);
                   paraFontNormal.setStyle(Font.NORMAL);
                   Font paraFont9 = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// "Cp1250"
                   paraFont9.setSize(9.0f);
                   paraFont9.setStyle(Font.BOLD);
                   Font paraFontNormal9 = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// "Cp1250"
                   paraFontNormal9.setSize(9.0f);
                   paraFontNormal9.setStyle(Font.NORMAL);


                   yPos = yPos + 10;
                   Paint paint = new Paint();
                   paint.setTypeface(tfFont);
                   paint.setColor(Color.BLACK);
                   paint.setTextSize(8);
                   paint.setTypeface(Bold);
                   if (isReprint) {
                       doc.add(new Paragraph("Duplicate Copy", paraFontNormal));
                   }
                   doc.add(new Paragraph(sCompanyName, paraFont));
                   yPos = yPos + 10;
                   doc.add(new Paragraph(sCompanyAddress1, paraFontNormal));
                   yPos = yPos + 10;
                   doc.add(new Paragraph(sCompanyAddress2, paraFontNormal));
                   yPos = yPos + 10;
                   doc.add(new Paragraph(sCompanyCountry + "-" + sCompanyPostalCode, paraFontNormal));
                   yPos = yPos + 10;
                   doc.add(new Paragraph("Ph : " + sCompanyPhone + "  " + "Fax : " + sCompanyFax, paraFontNormal));
                   yPos = yPos + 10;
                   doc.add(new Paragraph("GSTRegNo : " + sTaxRegNo, paraFontNormal));
                   yPos = yPos + 5;
                   doc.add(new Paragraph(sBorderLine, paraFontNormal));
                   yPos = yPos + 10;
                   doc.add(new Paragraph("RECEIPT", paraFont9));
                   yPos = yPos + 10;
                   paint.setTypeface(Normal);
                   Double dsGSTTotal = 0.00, dzGSTTotal = 0.00;
                   if (countHdr > 0 && countDET > 0) {
                       doc.add(new Paragraph("Entry  No      : " + jobj.getString("EntryNo").trim(), paraFontNormal));
                       yPos = yPos + 10;
                       doc.add(new Paragraph("Date           : " + jobj.getString("BillingDate") + " " + jobj.getString("BillingTime"), paraFontNormal));
                       yPos = yPos + 10;
                            /*doc.add(new Paragraph("Terms          : " + rsHDR.getString("Terms"), paraFontNormal));
                            yPos = yPos + 5;*/
                       if (!isNullOrEmpty(jobj.getString("CustomerCode"))) {
                           doc.add(new Paragraph("Cust Code      : " + jobj.getString("CustomerCode"), paraFontNormal));
                           yPos = yPos + 10;
                       }
                       if (!isNullOrEmpty(jobj.getString("CustomerName"))) {
                           doc.add(new Paragraph(jobj.getString("CustomerName"), paraFontNormal));
                           yPos = yPos + 10;
                       }
                      /* if (!isNullOrEmpty(jobj.getString("SalesManID"))) {
                           doc.add(new Paragraph("SR    : " + jobj.getString("SalesManID").trim(), paraFontNormal));
                           yPos = yPos + 10;
                       }*/
                       doc.add(new Paragraph(sBorderLine, paraFontNormal));
                       yPos = yPos + 10;
                       sDocPrint = "  DOCUMENT           DATE           AMOUNT ";
                       doc.add(new Paragraph(sDocPrint, paraFontNormal));
                       yPos = yPos + 10;
                       doc.add(new Paragraph(sBorderLine, paraFontNormal));
                       yPos = yPos + 10;
                       String sTransNo="",sDate="",sAmount="";
                       Double dTotAmnt=0.0,dAmt=0.0;
                       for (int i=0;i<jsonArrayDetail.length();i++){
                           JSONObject jobjDetail=jsonArrayDetail.getJSONObject(i);
                           sTransNo=jobjDetail.getString("InvoiceNo").trim();
                           sDate=jobjDetail.getString("InvoiceDate").trim();
                           sAmount=jobjDetail.getString("TotalAmount").trim();
                           dAmt=Double.parseDouble(sAmount);
                           sDocPrint = " " + sTransNo + fncGetWhiteSpaces(14 - sTransNo.length()) + fncGetWhiteSpaces(14 - sDate.length()) + sDate + fncGetWhiteSpaces(13 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt);
                           doc.add(new Paragraph(sDocPrint, paraFontNormal));
                           yPos = yPos + 10;
                           dTotAmnt+=dAmt;
                       }
                       doc.add(new Paragraph(sBorderLine, paraFontNormal));
                       yPos = yPos + 10;
                       sDocPrint = " MODE    BANKNAME  CHEQUE NO  DATE        AMOUNT";
                       doc.add(new Paragraph(sDocPrint, paraFontNormal));
                       yPos = yPos + 10;
                       doc.add(new Paragraph(sBorderLine, paraFontNormal));
                       yPos = yPos + 10;

                       String sAmnt="",sCheckNo="";
                       for(int i=0;i<jsonArrayDetail.length();i++){
                           JSONObject jobjDetail=jsonArrayDetail.getJSONObject(i);
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
                           sDocPrint = " " + sMode + fncGetWhiteSpaces(11 - sMode.length()) + sBank + fncGetWhiteSpaces(13 - sBank.length()) + sCheckNo + fncGetWhiteSpaces(15 - sCheckNo.length());
                           sDocPrint += sReceiptDate + fncGetWhiteSpaces(12 - sReceiptDate.length()) + fncGetWhiteSpaces(11 - dTot.length()) + dTot ;
                           doc.add(new Paragraph(sDocPrint, paraFontNormal));
                           yPos = yPos + 10;
                       }
                       doc.add(new Paragraph(sBorderLine, paraFontNormal));
                       yPos = yPos + 10;
                       sAmnt = jobj.getString("TotalAmount");
                       dAmt = Double.parseDouble(sAmnt);
                       sDocPrint = " Total   :" + fncGetWhiteSpaces(32 - fncFormatSeperator(dAmt).length()) + fncFormatSeperator(dAmt);
                       doc.add(new Paragraph(sDocPrint, paraFontNormal));
                       yPos = yPos + 10;
                       doc.add(new Paragraph(sBorderLine, paraFontNormal));
                       yPos = yPos + 10;
                       doc.add(Chunk.NEWLINE);
                       doc.add(Chunk.NEWLINE);
                       doc.add(Chunk.NEWLINE);
                       doc.add(Chunk.NEWLINE);
                       sDocPrint = "Printed On :" + fncFormatSQLDate() + "\r\n";
                       doc.add(new Paragraph(sDocPrint.trim(),paraFontNormal));
                       yPos=yPos+10;
                       doc.add(Chunk.NEWLINE);
                       doc.add(Chunk.NEWLINE);
                       doc.add(Chunk.NEWLINE);
                       doc.add(Chunk.NEWLINE);
                       isPrintSuccess=true;
                   }
               }catch (Exception e){
                   e.printStackTrace();
                   isPrintSuccess=false;
               }finally {
                   doc.close();
//            printSuccess=fncPdftobitmap(pdfName);
                   isPrintSuccess=true;
                   Log.e("***Print", "Print sucess");
               }
           }
           return isPrintSuccess;
       }catch (Exception ex) {
            ex.printStackTrace();
            Log.e("***Print Exception",ex.toString());
           return false;
       }

    }
    private String fncFormatSQLDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= new Date(System.currentTimeMillis());
        String systemDate = df.format(date);
        return systemDate;
    }
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }
    private String fncFormatSQLText(String sText)
    {
        String sGenString = "";
        sGenString = "'" + sText.replace("'", "''") + "'";
        return sGenString;
    }
    /*private Boolean fncPdftobitmap(String pdfName){
        DecodeServiceBase decodeService = new DecodeServiceBase(new PdfContext());
        decodeService.setContentResolver(mContext.getContentResolver());
        File pdf=new File(FilePath+"/Receipt/",pdfName);
        decodeService.open(Uri.fromFile(pdf));
        pdfName=pdfName.replace(".pdf","");
        int pageCount = decodeService.getPageCount();
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = (PdfPage) decodeService.getPage(i);
            RectF rectF = new RectF(0, 0, 1, 1);

            double scaleBy = Math.min(2240/ (double) page.getWidth(),1560 / (double) page.getHeight());
            int with = (int) (page.getWidth() * scaleBy);
            int height = (int) (page.getHeight() * scaleBy);
            Log.e("***scale", scaleBy + " width " + with + " height " + height);
            Log.e("**Pagewidth ",page.getWidth()+" ht "+page.getHeight());

            Bitmap bitmap = page.renderBitmap(with, height, rectF);

            try {
                String mOutput=pdfName+System.currentTimeMillis();
                File outputFile = new File(FilePath+"/Receipt/", mOutput+".jpg");
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                outputStream.close();
                sImagePath=FilePath+"/Receipt/"+mOutput+".jpg";
                Log.e("**PDF Image Saved ", mOutput +"Page "+i);
                return true;
            } catch (IOException e) {
                Log.e("***PDF to bitmap Error",e.toString());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }*/
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
    private String fncGetWhiteSpaces(int iNumberOfSpaces)
    {
        String sFormat = "%-"+ iNumberOfSpaces+ "s";
        return String.format(sFormat, "");
    }



}
