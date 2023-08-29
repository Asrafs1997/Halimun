package com.unipro.labisha.server;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by User1 on 17/04/2018.
 */
public class SoapConnection {
    Object response = null;
    private String responseJSON;
    public final String SOAP_ACTION = "http://tempuri.org/";
    public final String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";

    public SoapConnection() {
    }

    public String fncValidateLogin(String userName, String password, String url, String webMethod) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
       /* pi.setName("sUserName");
        pi.setValue(userName);
        pi.setType(String.class);
        request.addProperty(pi);
        pi=new PropertyInfo();*/
        pi.setName("sPassword");
        pi.setValue(password);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = url;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(Url);

        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            response = envelope.getResponse();
        } catch (Exception exception) {
            response = exception.toString();
            response = "Exception";
        }
        return response.toString();
    }

    public String WsDeviceAuth(String id, String url, String webMethod) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("DeviceID");
        pi.setValue(id);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = url;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(Url);

        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception e) {
            e.printStackTrace();
            response = "Exception";
        }
        return response.toString();
    }

    public String GetInventory(String itemCode, String url, String terminalcode, String webMethod, Boolean isBarcode) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("InventoryCode");
        pi.setValue(itemCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("TerminalCode");
        pi.setValue(terminalcode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("isBarCode");
        pi.setValue(isBarcode);
        pi.setType(Boolean.class);
        request.addProperty(pi);
        String Url = url;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(Url);

        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncCheckStockId(String stockId, String sUser, String sUrl, String webMethod) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("stockId");
        pi.setValue(stockId);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncGetInventoryStock(String stockId, String itemCode, String sUser, String sUrl, String webMethod) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("stockId");
        pi.setValue(stockId);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sInvCode");
        pi.setValue(itemCode);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncSaveStockTake(String sItemCode, String sUser, String sLocCode, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sItemCode");
        pi.setValue(sItemCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(sLocCode);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncGetInventoryDetail(String sItemCode, String sUser, String sLocCode, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sItemCode");
        pi.setValue(sItemCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);//
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(sLocCode);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncGetPackageItemList(String sItemCode, boolean isPackage, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sItemCode");
        pi.setValue(sItemCode);
        pi.setType(String.class);
        request.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("isPackage");
        pi.setValue(isPackage);
        pi.setType(String.class);
        request.addProperty(pi);//



        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncSavePriceChange(String sData, String sItemCode, String sUser, String sLocCode, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sData");
        pi.setValue(sData);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sItemCode");
        pi.setValue(sItemCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(sLocCode);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncGetLocationList(String sUser, String sLocCode, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(sLocCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncGetTransferInList(String sUser, String sLocCode, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(sLocCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncSaveTransferIn(String sTransferNo, String sUser, String sLocCode, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sTransferNo");
        pi.setValue(sTransferNo);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(sLocCode);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncSaveTransferOut(String sJsonData, String sUser, String sLocationCode, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sData");
        pi.setValue(sJsonData);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(sLocationCode);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncGetTransferOutList(String sLocCode, String sUser, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(sLocCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(sUser);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncGetTerminal(String android_id, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sDeviceId");
        pi.setValue(android_id);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncCheckLogin(String android_id, String sUserName, String sPassword, String webMethod, String sUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sDeviceId");
        pi.setValue(android_id);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUserCode");
        pi.setValue(sUserName);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sPass");
        pi.setValue(sPassword);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = sUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncGetPoList(String loc_code, String uname, String sSelected, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sType");//
        pi.setValue(sSelected);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncGetGidList(String loc_code, String sSelected, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sSelected");
        pi.setValue(sSelected);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncGetPoReturnList(String loc_code, String sSelected, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sSelected");
        pi.setValue(sSelected);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncSavePOHeader(String sJsArray, String sVendorCode, String loc_code, String uname, String terminalCode, String deviceId, String webMethod, String baseUrl, boolean isPoedit, String sPoNo) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sData");
        pi.setValue(sJsArray);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sVendor");
        pi.setValue(sVendorCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sTerminal");
        pi.setValue(terminalCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sDeviceId");
        pi.setValue(deviceId);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("isPoedit");
        pi.setValue(isPoedit);
        pi.setType(Boolean.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sPoNo");
        pi.setValue(sPoNo);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncSavePOReturn(String sjsArray, String sVendorCode, String loc_code, String uname, String terminalCode, String sReturnType, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sData");
        pi.setValue(sjsArray);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sVendor");
        pi.setValue(sVendorCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sTerminal");
        pi.setValue(terminalCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sReturnType");
        pi.setValue(sReturnType);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncGetPoGid(String sPoNo, String uname, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sPoNo");
        pi.setValue(sPoNo);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncGetInventoryList(String loc_code, String uname, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {

            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();

//            httpTransport.call(SOAP_ACTION+webMethod, envelope);
//            httpTransport.debug=true;
//            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
//            response = resp.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncGetInventorySearch(String loc_code, String sQuery, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sDesc");
        pi.setValue(sQuery);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncSaveGID(String sjsArray, String sVendorCode, String loc_code, String uname, String terminalCode, String sPoNo, String sDoNo, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sData");
        pi.setValue(sjsArray);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sVendor");
        pi.setValue(sVendorCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sTerminal");
        pi.setValue(terminalCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sPoNo");
        pi.setValue(sPoNo);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sDono");
        pi.setValue(sDoNo);
        pi.setType(String.class);
        request.addProperty(pi);

        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncSaveStockUpdate(String itemCode, String sQty, String uname, String loc_code, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sItemCode");
        pi.setValue(itemCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sQty");
        pi.setValue(sQty);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncGetCompanyList(String uname, String loc_code, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {


            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncSaveCollectionEntry(String sData, String custCode, String uname, String loc_code, String webMethod, String baseUrl, String sTerminal) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sData");
        pi.setValue(sData);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("custCode");
        pi.setValue(custCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);//sTerminalCode
        pi = new PropertyInfo();
        pi.setName("sTerminalCode");
        pi.setValue(sTerminal);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = "false," + exception.toString();
        }
        return response.toString();
    }

    public String fncGetSoList(String sFromDate, String sToDate, String uname, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sFromDate");
        pi.setValue(sFromDate);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sToDate");
        pi.setValue(sToDate);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = "false," + exception.toString();
        }
        return response.toString();
    }

    public String fncSaveSoOrder(String sData, String sSubCompCode, String sBillDisc, String sCustomerCode, Boolean isEditorder, String sSoOrderNo, String sTerminal, String uname, String loc_code, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sData");
        pi.setValue(sData);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sSubCompCode");
        pi.setValue(sSubCompCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sCustomer");
        pi.setValue(sCustomerCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("isEditorder");
        pi.setValue(isEditorder);
        pi.setType(Boolean.class);
        request.addProperty(pi);//sSoOrderNo
        pi = new PropertyInfo();
        pi.setName("sSoOrderNo");
        pi.setValue(sSoOrderNo);
        pi.setType(String.class);
        request.addProperty(pi);//sSoOrderNo
        pi = new PropertyInfo();
        pi.setName("sTerminal");
        pi.setValue(sTerminal);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sBillDisc");
        pi.setValue(sBillDisc);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = "false," + exception.toString();
        }
        Log.e("Response", response + "");
        return response.toString();
    }

    public String fncGetDeviceList(String uname, String loc_code, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        return response.toString();
    }

    public String fncUploadCollection(String sData, String uname, String loc_code, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sData");
        pi.setValue(sData);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("response", response.toString());
        return response.toString();
    }

    public String fncUploadStockDetails(String InvCode, String NQty, String OQty, String Uname, String createdate, String Loc_Code, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("InvCode");
        pi.setValue(InvCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("NQty");
        pi.setValue(NQty);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("OQty");
        pi.setValue(OQty);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("Uname");
        pi.setValue(Uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("createdate");
        pi.setValue(createdate);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(Loc_Code);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = exception.toString();
        }
        Log.e("response", response.toString());
        return response.toString();
    }

    public String fncGetSoDetail(String sSoNo, String uname, String loc_code, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sSoNo");
        pi.setValue(sSoNo);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = "false," + exception.toString();
        }
        return response.toString();
    }

    public String fncGetSOInventory(String sItemCode, String sCustCode, String uname, String loc_code, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sItemCode");
        pi.setValue(sItemCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sCust");
        pi.setValue(sCustCode);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = "false," + exception.toString();
        }
        return response.toString();
    }

    public String fncGetPoDetail(String sPoNo, String loc_code, String uname, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sPoNo");
        pi.setValue(sPoNo);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sLocCode");
        pi.setValue(loc_code);
        pi.setType(String.class);
        request.addProperty(pi);
        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = "false," + exception.toString();
        }
        return response.toString();
    }

    public String fncGetCollectionList(String sFromDate, String sToDate, String uname, String webMethod, String baseUrl) {
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, webMethod);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("sFromDate");
        pi.setValue(sFromDate);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sToDate");
        pi.setValue(sToDate);
        pi.setType(String.class);
        request.addProperty(pi);
        pi = new PropertyInfo();
        pi.setName("sUser");
        pi.setValue(uname);
        pi.setType(String.class);
        request.addProperty(pi);


        String Url = baseUrl;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.e("Request", request + "");
        HttpTransportSE httpTransport = new HttpTransportSE(Url);
        try {
            httpTransport.call(SOAP_ACTION + webMethod, envelope);
            httpTransport.debug = true;
            SoapPrimitive resp = (SoapPrimitive) envelope.getResponse();
            response = resp.toString();
        } catch (Exception exception) {
            response = "false," + exception.toString();
        }
        return response.toString();
    }
}
