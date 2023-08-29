package com.unipro.labisha.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by kalaivanan on 22/03/2018.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    Context _context;

    public static final String DATABASE_NAME = ConnectLocalDB()+"/UpsMobilePDA";
    public static final String FilePath=ConnectLocalDB();
    public static File sDBDirectory =null;

    String tblInvVendor="CREATE TABLE if not exists [tblInvVendor]([VendorCode] [varchar](20) NOT NULL,[VendorName] [varchar](50) NULL,[Address1] [varchar](50) NULL,[Address2] [varchar](50) NULL,[Country] [varchar](50) NULL," +
            "[ZipCode] [varchar](20) NULL,[Phone] [varchar](15) NULL,[Fax] [varchar](15) NULL,[SMSNo] [varchar](50) NULL,[Email] [varchar](50) NULL,[Url] [varchar](50) NULL,[TermsCode] [varchar](10) NULL," +
            "[PurchaseLimit] [numeric](18, 0) NULL,[Attn] [varchar](50) NULL,[BarCode] [varchar](20) NULL,[Remarks] [varchar](50) NULL,[GST] [varchar](1) NULL,[BankCode] [varchar](20) NULL," +
            "[BankBranchCode] [varchar](20) NULL,[BankAccountNo] [varchar](40) NULL,[PaymentMode] [varchar](10) NULL,[CreditLimit] [decimal](18, 4) NULL,[CreditDays] [int] NULL,[PODiscount] [decimal](18, 2) NULL," +
            "[CurrencyCode] [varchar](10) NULL,[CreateUser] [varchar](10) NULL,[CreateDate] [smalldatetime] NULL,[ModifyUser] [varchar](10) NULL,[ModifyDate] [smalldatetime] NULL,[IsBank] [int] NULL," +
            "[ApplyLC] [int] NULL,[POExport] [char](1) NULL,[POExportTime] [char](2) NULL,[AutoPOFax] [char](1) NULL,[AutoPOFaxTime] [char](2) NULL,[GLNCode] [varchar](20) NULL,[PORemarks] [varchar](50) NULL,[TradeType] [varchar](50) NULL)";

    String tblVwInventory="CREATE TABLE if not exists [tblVwInventory]([InventoryCode] [char](20) NOT NULL,[Description] [nchar](50) NULL,[Model] [varchar](20) NULL,[BarCode] [char](20) NULL,[InventoryType] [bit],[UOM] [char](15) NULL,[DepartmentCode] [char](15) NULL,[BrandCode] [char](20) NULL,[CategoryCode] [char](20) NULL,[Origin] [char](10) NULL,[Compatible] [varchar](50) NULL," +
            "[Remarks] [nvarchar](50) NULL,[VendorCode] [varchar](15) NULL,[AllowExpireDate] [bit] NULL,[PackageItem] [bit] NULL,[MerchandiseCode] [char](10) NULL,[BinNo] [char](10) NULL,[ReleaseDate] [datetime] NULL,[CustomerRequired] [bit] NULL,[IMEI] [bit] NULL,[Mobile] [bit] NULL,[IMSI] [bit] NULL,[SellingPrice] [decimal](18, 6) NULL,[AverageCost] [decimal](18, 6) NULL,[FirstInQty] [decimal](18, 3) NULL," +
            "[MidInQty] [decimal](18, 3) NULL,[LastInQty] [decimal](18, 3) NULL,[FirstInCost] [decimal](18, 4) NULL,[MidInCost] [decimal](18, 4) NULL,[LastInCost] [decimal](18, 4) NULL,[MinProfitPercent] [decimal](5, 2) NULL,[MinProfitAmount] [decimal](18, 2) NULL,[PromotionPrice] [decimal](18, 2) NULL,[PromotionFromDate] [smalldatetime] NULL,[PromotionToDate] [smalldatetime] NULL," +
            "[LastSalesDate] [smalldatetime] NULL,[LastPurchaseDate] [smalldatetime] NULL,[LastCashBillNo] [char](20) NULL,[LastPurchaseInvoiceNo] [char](20) NULL,[MinimumQtyLevel] [decimal](18, 3) NULL,[MaximumQtyLevel] [decimal](18, 3) NULL,[ReOrderLevel] [decimal](18, 3) NULL,[SerialNoRequired] [char](1) NULL,[DistributionPrice] [decimal](18, 2) NULL,[Chilled] [char](1) NULL," +
            "[ChilledPrice] [decimal](18, 6) NULL,[DiscountPriceCode] [char](20) NULL,[AllowMemberDiscount] [bit] NULL,[AllowReward] [bit] NULL,[CatalogueNo] [char](50) NULL,[UnitCost] [decimal](18, 6) NULL,[GTINCode] [varchar](14) NULL,[WareHouse] [varchar](10) NULL,[ChangeDescription] [char](1) NULL,[Weight] [decimal](18, 0) NULL,[LstStkTakQty] [decimal](18, 3) NULL," +
            "[LstStkTakDate] [datetime] NULL,[InActive] [char](1) NULL,[InActiveDate] [smalldatetime] NULL,[LstDmgStkTakQty] [decimal](18, 3) NULL,[LstDmgStkTakDate] [datetime] NULL,[RetailPrice] [decimal](18, 6) NULL,[SizeCode] [char](20) NULL,[ColorCode] [char](20) NULL,[CreateUser] [char](10) NOT NULL,[CreateDate] [smalldatetime] NOT NULL,[ModifyUser] [char](10) NULL," +
            "[ModifyDate] [smalldatetime] NOT NULL,[ShortDescription] [varchar](30) NULL,[FreeItemStatus] [char](1) NULL,[SLPromotionStatus] [char](1) NULL,[PackingCost] [decimal](18, 2) NULL,[LastLogStkTakeQty] [decimal](18, 3) NULL,[InventoryGroup] [char](10) NULL,[AllowMRP] [bit] NULL,[MRP] [decimal](18, 6) NULL,[AllowBatchStock] [bit] NULL,[AllowNegativeStock] [bit] NULL,[AllowMultipleBin] [bit] NULL," +
            "[InputTax] [numeric](18, 2) NULL,[OutputTax] [numeric](18, 2) NULL,[AllowPaymodeCommision] [bit] NULL,[AllowZeroPrice] [bit] NULL,[NonTaxItem] [bit] NULL,[OnlineSales] [bit] NULL,[LocationCode] [char](10) NOT NULL,[QtyonHand] [decimal](18, 3) NOT NULL,[DmgQtyOnHand] [decimal](18, 3),[toTransfer] [decimal](18, 3) NULL,[ToOrder] [decimal](18, 3) NULL,[stkPrice] [money] NULL," +
            "[stkAverageCost] [money] NULL,[OutletSellingPrice] [money] NULL,[OutletRetailPrice] [money] NULL,[MinQty] [decimal](18, 3) NULL,[ConsignmentQty] [decimal](18, 2),[MaxQty] [decimal](18, 3) NULL,[WUnit] [numeric](17, 6),[FreightCost] [decimal](18, 6) NULL,[Discontinued] [char](1) NULL,[ReserveQty] [decimal](18, 2) NULL)";

    String tblCollectionentryDetail="CREATE TABLE if not exists [TBLCOLLECTIONENTRYDETAIL]([ENTRYNO] [varchar](100) NOT NULL,[ENTRYDATE] [datetime] NOT NULL,[CUSTOMERCODE] [varchar](100) NOT NULL,[CUSTOMERNAME] [varchar](500) NOT NULL,[SALESMANID] [varchar](100) NOT NULL,[SALESMANNAME] [varchar](500) NOT NULL,[INVOICENO] [varchar](100) NOT NULL,[INVOICEDATE] [datetime] NOT NULL,[PAYMODE] [varchar](100) NOT NULL,[CHEQUENO] [varchar](100) NOT NULL,[CHEQUEDATE] [datetime] NOT NULL,[BANK] VARCHAR(100) NOT NULL," +
            "[REFNO] [varchar](100) NOT NULL,[TOTALAMOUNT] [decimal](18, 2) NOT NULL,[REMARKS] [varchar](500) NOT NULL,[SUBCOMPANYCODE] [varchar](100) NOT NULL,[DEVICEID] [varchar](100) NOT NULL,[CREATEDBY] [varchar](100) NOT NULL,[CREATEDON] [datetime] NOT NULL,[MODIFIEDBY] [varchar](100) NOT NULL,[MODIFIEDON] [datetime] NOT NULL)";

    String tblCollectionentryHeader="CREATE TABLE if not exists [TBLCOLLECTIONENTRYHEADER]([ENTRYNO] [varchar](100) NOT NULL,[ENTRYDATE] [datetime] NOT NULL,[CUSTOMERCODE] [varchar](100) NOT NULL,[CUSTOMERNAME] [varchar](500) NOT NULL,[SALESMANID] [varchar](100) NOT NULL,[SALESMANNAME] [varchar](500) NOT NULL,[TOTALAMOUNT] [decimal](18, 2) NOT NULL,[REMARKS] [varchar](500) NOT NULL,[SUBCOMPANYCODE] [varchar](100) NOT NULL,[DEVIDEID] [varchar](100) NOT NULL,[CREATEDBY] [varchar](100) NOT NULL,[CREATEDON] [datetime] NOT NULL," +
            "[MODIFIEDBY] [varchar](100) NOT NULL,[MODIFIEDON] [datetime] NOT NULL)";

    String tblCollectionEntryNo="CREATE TABLE if not exists [TBLCOLLECTIONENTRYNO]([COLLECTIONENTRYNO] [bigint] NOT NULL)";

    String tblArCustomer="CREATE TABLE if not exists [tblARCustomer]([CustomerCode] [char](20) NOT NULL,[CustomerName] [varchar](50) NULL,[GroupCode] [char](50) NULL,[Address1] [varchar](50) NULL,[Address2] [varchar](50) NULL,[Country] [varchar](50) NULL,[ZipCode] [char](20) NULL,[Phone] [char](30) NULL,[Fax] [char](15) NULL,[FaxOption] [char](1) NULL,[Email] [varchar](50) NULL,[EmailOption] [char](1) NULL,[Url] [varchar](50) NULL,[TermsCode] [varchar](15) NULL,[SMSNo] [char](50) NULL,[CreditLimit] [money] NULL,[Attn] [char](50) NULL," +
            "[Remarks] [char](50) NULL,[Status] [char](20) NULL,[Gst] [int] NULL,[GstRegNo] [varchar](20) NULL,[ROCNo] [varchar](20) NULL,[SalesManId] [varchar](10) NULL,[CreateUser] [char](50) NULL,[CreateDate] [smalldatetime] NULL,[ModifyUser] [char](50) NULL,[ModifyDate] [smalldatetime] NULL,[Zone] [varchar](20) NULL,[PriceGroupCode] [char](20) NULL,[CurrencyCode] [char](10) NULL,[WalkIn] [char](1) NULL,[TradeType] [char](20) NULL,[GSTType] [char](1) NULL)";

    private static final String tblUserMaster="create table if not exists [tblUserMaster]([UserCode] [char](10) NOT NULL,[UserGroupCode] [char](6) NOT NULL,[UserName] [char](50) NOT NULL,[Password] [char](10) NULL,[ExpiryDate] [smalldatetime] NULL,"+
            " [Designation] [char](50) NULL,[Email] [char](50) NULL,[Phone] [char](20) NULL,[Fax] [char](20) NULL,[StoreCode] [char](15) NULL,[POLimit] [money] NULL,[Admin] [smallint] NOT NULL,"+
            " [UpdateApprovedPo] [smallint] NULL,[POSDiscount] [char](1) NULL,[DayOfValid] [smallint] NULL,[DaysOfWarning] [smallint] NULL,[ChangePassword] [char](1) NULL,[ChangeDate] [smalldatetime] NULL,"+
            " [FirstLogDate] [smalldatetime] NULL,[LastLogDate] [smalldatetime] NULL,[UpdateGst] [decimal](18, 2) NULL,[Contact] [varchar](50) NULL,[CreateDate] [smalldatetime] NOT NULL,[CreateUser] [char](10) NOT NULL,"+
            " [ModifyDate] [smalldatetime] NULL,[ModifyUser] [char](10) NULL,[SInvPriceChange] [char](1) NULL,[EODReport] [char](1) NULL,[LastSync] [smalldatetime] NULL)";

    private static final String tblSalesMan="CREATE TABLE if not exists [tblSalesMan]([SalesManId] [char](10) NOT NULL,[SalesManName] [varchar](50) NULL,[CommPerc] [decimal](18, 2) NULL," +
            "[CreateUser] [char](10) NOT NULL,[CreateDate] [smalldatetime] NOT NULL,[ModifyUser] [char](10) NULL,[ModifyDate] [smalldatetime] NULL," +
            "[location] [varchar](10) NULL,[Password] [varchar](10) NULL)";

    private static final String tblBarCode = "CREATE TABLE if not exists [tblBarcode]([InventoryCode] [char](20) NOT NULL,[BarCode] [char](20) NOT NULL,[Description] [nvarchar](50) NULL," +
            "[CreateUser] [char](10) NOT NULL,[CreateDate] [smalldatetime] NOT NULL,[ModifyUser] [char](10) NULL,[ModifyDate] [smalldatetime] NOT NULL)";

    private static final String tblCompany="CREATE TABLE if not exists [tblCompany]([CompanyID] [char](15) NOT NULL,[Name] [varchar](50) NULL,[Address1] [char](50) NULL,[Address2] [char](50) NULL,[City] [char](50) NULL," +
            "[State] [char](50) NULL,[Country] [char](50) NULL,[PostalCode] [char](10) NULL,[Phone] [char](35) NULL,[Fax] [char](15) NULL,[Email] [char](50) NULL,[Url] [char](50) NULL," +
            "[LocationCode] [varchar](10) NULL,[MainLocationCode] [varchar](10) NULL,[BRNNo] [varchar](50) NULL,[GSTRegNo] [char](25) NULL,[NextMailID] [decimal](18, 0) NULL,[NextPosNo] [decimal](18, 2) NULL," +
            "[NextInvoice] [decimal](18, 2) NULL,[GstPerc] [decimal](18, 2) NULL,[GstType] [char](1) NULL,[DisplayMessage1] [char](20) NULL,[DisplayMessage2] [char](20) NULL,[DisplayComport] [char](1) NULL," +
            "[NextPaymentVoucherNo] [numeric](8, 0) NULL,[NextPosCreditNo] [numeric](8, 0) NULL,[NextReciptNo] [decimal](18, 2) NULL,[NextAvilableRange] [decimal](18, 2) NULL,[NextTransNo] [decimal](18, 2) NULL," +
            "[MaxDiscountAllowed] [decimal](18, 2) NULL,[MaxPriceChangeAllowed] [decimal](18, 2) NULL,[BarCodeCOMPort] [numeric](8, 0) NULL,[ExpiredTime] [decimal](18, 0) NULL,[NextPONo] [char](10) NULL," +
            "[NextGIDNo] [char](10) NULL,[NextAverageCostPercentage] [decimal](18, 0) NULL,[TaxIN] [bit] NULL,[NextTransferNo] [decimal](18, 0) NULL,[NextEODDate] [smalldatetime] NULL,[NextPurchaseno] [char](10) NULL," +
            "[NextPurReNo] [char](10) NULL,[NextDmgReNo] [char](10) NULL,[SalesReturnRefNo] [char](10) NULL,[TaxTaken] [char](1) NULL,[HQTransNo] [decimal](18, 0) NULL,[BillPrint] [char](1) NULL," +
            "[UseSalesQty] [char](10) NOT NULL,[Remarks] [varchar](50) NULL,[CompanyShortCode] [char](3) NULL,[POOffReqNo] [decimal](18, 0) NULL,[SalesReturnReqNo] [varchar](10) NULL,[NextPendingNo] [char](10) NULL," +
            "[NextStkTakeAdjNo] [char](10) NULL,[Version] [varchar](10) NULL,[NextStkAdjNo] [varchar](10) NULL,[NextCustSONo] [varchar](10) NULL,[DistributionNo] [varchar](10) NULL,[PRReqNo] [varchar](10) NULL," +
            "[NextDmgStkTakeAdjNo] [varchar](10) NULL,[FaxServer] [varchar](20) NULL,[StockClosingDt] [datetime] NULL,[NextSONo] [varchar](10) NULL,[NextDONo] [varchar](10) NULL,[CreateUser] [char](10) NULL," +
            "[CreateDate] [smalldatetime] NULL,[ModifyUser] [char](10) NULL,[ModifyDate] [smalldatetime] NULL,[NextPCNo] [varchar](10) NULL,[LastLogStkTakeDate] [smalldatetime] NULL,[NextMemPointAdjNo] [varchar](10) NULL," +
            "[GrossProfit] [varchar](10) NULL,[PCReqNo] [varchar](10) NULL,[NextDGIDNo] [varchar](10) NULL,[NextConsNo] [char](10) NULL,[NextConsRetnNo] [decimal](18, 0) NULL,[NextGSTRefundNo] [varchar](20) NULL," +
            "[NextPOSSettleNo] [varchar](10) NULL,[POSVersion] [varchar](10) NULL,[ExPensesNo] [varchar](10) NULL,[NextECRNo] [varchar](10) NULL,[NextMarginAnalysedNo] [varchar](10) NULL,[SRReqNo] [varchar](10) NULL," +
            "[NextConReqNo] [varchar](10) NULL,[NextTranHoldNo] [varchar](10) NULL,[NextMemberNo] [varchar](10) NULL,[NextPkgSalesNo] [varchar](10) NULL,[NextDepositNo] [varchar](10) NULL,[NextGIDHoldNo] [varchar](10) NULL," +
            "[PurchaseGstType] [char](1) NULL,[CreditSalesGstType] [char](1) NULL,[NextPaymentNo] [varchar](10) NULL,[NextCreditNoteNo] [varchar](10) NULL,[NextDebitNoteNo] [varchar](10) NULL,[NextReceiptNo] [varchar](10) NULL," +
            "[NextPurInvoiceNo] [varchar](50) NULL,[NextAppointmentNo] [decimal](18, 0) NULL,[NextToCreateNo] [decimal](18, 0) NULL,[NextTIAcceptNo] [decimal](18, 0) NULL,[UASACVersion] [char](10) NULL,[EODDate] [datetime] NULL," +
            "[PrintPRMInReceipt] [numeric](8, 0) NULL,[NextRepairNo] [decimal](18, 0) NULL,[VersionType] [char](10) NULL,[MainLocationVersionType] [char](10) NULL,[NextSupplierInvoiceNo] [varchar](10) NULL,[NextCustomerCreditNoteNo] [varchar](10) NULL," +
            "[ChatMessageNo] [decimal](18, 0) NULL,[BinTransferNO] [decimal](18, 0) NULL,[Server] [varchar](100) NULL,[DisplayName] [varchar](100) NULL,[FromEmailId] [varchar](100) NULL,[Password] [varchar](100) NULL," +
            "[MinBillvalue] [decimal](18, 2) NULL,[NextTransferHoldNo] [varchar](20) NULL,[NextSerialTranNo] [varchar](15) NULL,[NextSalesCommissionNo] [varchar](10) NULL,[NextQuotationNo] [char](10) NULL," +
            "[NextMemberChildNo] [decimal](18, 0) NULL,[ApprovalNo] [char](25) NULL)";

    private static final String tblSubCompany="CREATE TABLE if not exists [tblSubCompany]([SubCompanyCode] [varchar](15) NULL,[Name] [varchar](50) NULL,[Address1] [varchar](100) NULL,[Address2] [varchar](100) NULL,[Address3] [varchar](100) NULL,[Country] [char](50) NULL," +
            "[ZipCode] [char](20) NULL,[Phone] [char](20) NULL,[Fax] [char](20) NULL,[Email] [char](50) NULL,[Url] [char](50) NULL,[NextPONo] [decimal](18, 0) NULL,[NextGIDNo] [decimal](18, 0) NULL,[NextSONo] [decimal](18, 0) NULL,[NextDONo] [decimal](18, 0) NULL," +
            "[NextInvoiceNo] [decimal](18, 0) NULL,[NextCreditInvoiceNo] [decimal](18, 0) NULL,[NextPaymentVoucherNo] [decimal](18, 0) NULL,[NextReceiptNo] [decimal](18, 0) NULL,[NextTransferNo] [decimal](18, 0) NULL,[NextPurReturnNo] [decimal](18, 0) NULL,[NextSalesReturnNo] [decimal](18, 0) NULL," +
            "[MaxDiscountAllowed] [decimal](18, 0) NULL,[MaxPriceChangeAllowd] [decimal](18, 0) NULL,[NextCustomerCreditNoteNo] [decimal](18, 0) NULL,[NextCustomerDebitNoteNo] [decimal](18, 0) NULL,[NextCustomerOpeningCNNo] [decimal](18, 0) NULL,[NextCustomerOpeningDNNo] [decimal](18, 0) NULL," +
            "[NextSupplierInvoiceNo] [decimal](18, 0) NULL,[NextSupplierCreditNoteNo] [decimal](18, 0) NULL,[NextSupplierDebitNoteNo] [decimal](18, 0) NULL,[NextSupplierOpeningCNNo] [decimal](18, 0) NULL,[NextSupplierOpeningDNNo] [decimal](18, 0) NULL,[NextSupplierReceiptNo] [decimal](18, 0) NULL," +
            "[SubCompanyShortCode] [varchar](10) NULL,[DisplayMessage1] [varchar](20) NULL,[DisplayMessage2] [varchar](20) NULL,[CreatedBy] [char](15) NOT NULL,[CreatedDate] [datetime] NOT NULL,[ModifyBy] [char](15) NOT NULL,[ModifyDate] [datetime] NOT NULL)";

    private static final String tblAndroidDeviceConfig="CREATE TABLE if not Exists [tblAndroidDeviceConfig]([DeviceId] [varchar](100) PRIMARY KEY,[DeviceName] [varchar](50) NOT NULL," +
            "[TerminalCode] [varchar](10) NOT NULL,[TerminalName] [varchar](50) NOT NULL,[CreateUser] [varchar](50) NOT NULL,[CreateDate] [datetime] NOT NULL,[ModifyUser] [varchar](50) NULL,[ModifyDate] [datetime] NULL)";

    private static final String tblARCustomerShippingAddress="CREATE TABLE if not Exists [tblARCustomerShippingAddress]([CustomerCode] [char](20) NOT NULL,[SlNo] [int] NOT NULL,[ContactPerson1] [varchar](50) NULL,[ContactPerson2] [varchar](50) NULL," +
            "[ContactPerson3] [varchar](50) NULL,[Address1] [varchar](50) NULL,[Address2] [varchar](50) NULL,[Country] [varchar](50) NULL,[ZipCode] [varchar](10) NULL,[Phone] [char](30) NULL,[Fax] [varchar](15) NULL," +
            "[ShippingCode] [varchar](15) NULL,[ShippingName] [varchar](50) NULL,[SalesmanId] [varchar](15) NULL,[Zone] [varchar](20) NULL)";

    private static final String tblParamMaster="CREATE TABLE if not exists [tblParamMaster]([CompanyCode] [char](5) NOT NULL,[ParamCode] [char](30) NOT NULL,[Description] [varchar](300) NULL,[Value] [varchar](10) NOT NULL," +
            "[CreatedBy] [varchar](15) NOT NULL,[CreatedDate] [datetime] NOT NULL,[ModifiedBy] [varchar](15) NOT NULL,[ModifiedDate] [datetime] NOT NULL)";

    private static final String tblbank="CREATE TABLE if not Exists [tblbank]([Bankcode] [varchar](15) NULL,[Bankname] [varchar](50) NULL,[branchCode] [varchar](15) NULL,[Branchname] [varchar](50) NULL,[AccountNo] [varchar](15) NULL,[GiroFormat] [varchar](15) NULL,"+
            "[GiroBatchNo] [varchar](15) NULL,[ChequeBatchNo] [varchar](15) NULL,[ControlChequeNo] [varchar](15) NULL,[CreateUser] [varchar](10) NULL,[createDate] [smalldatetime] NULL,[Modifyuser] [varchar](10) NULL,[Modifydate] [smalldatetime] NULL)";

    private static final String tblSODetail="CREATE TABLE if not Exists [tblSODetail]([SONO] [char](20) NOT NULL,[PoNo] [char](20) NOT NULL,[ReleaseNumber] [tinyint] NOT NULL,[ItemCode] [char](12) NOT NULL,[ItemDesc] [nvarchar](100) NULL,[StoreCode] [char](15) NOT NULL,[PrcCode] [char](15) NOT NULL," +
            "[DeptCode] [char](15) NULL,[Quotation] [char](15) NULL,[LUomCode] [char](10) NOT NULL,[WUomCode] [char](15) NOT NULL,[WQty] [decimal](18, 3) NOT NULL,[LQty] [decimal](18, 3) NULL,[BaseUomCode] [char](15) NULL,[BaseQty] [decimal](18, 3) NULL,[PoPrice] [money] NULL," +
            "[UnitPrice] [money] NULL,[Disc1Perc] [numeric](18, 2) NULL,[Disc2Perc] [numeric](18, 2) NULL,[Disc3Perc] [numeric](18, 2) NULL,[Disc4Perc] [numeric](18, 2) NULL,[DiscAmount] [money] NULL,[Discount] [decimal](18, 4) NULL,[DiscUnitPrice] [money] NULL,[TotalValue] [money] NULL," +
            "[DeliveryDate] [datetime] NULL,[Remarks] [nvarchar](100) NULL,[PoReqNo] [decimal](10, 0) NULL,[DeliverQty] [decimal](18, 3) NOT NULL,[InvoiceQty] [decimal](18, 3) NOT NULL,[AcctCode] [char](15) NULL,[DrCostCentre] [char](15) NULL,[DrGlAccount] [char](15) NULL,[CrCostCentre] [char](15) NULL," +
            "[CrGlAccount] [char](15) NULL,[FocQty] [decimal](18, 3) NULL,[OffSetWQty] [decimal](18, 3) NULL,[WUnit] [smallint] NOT NULL,[CreatedBy] [varchar](15) NOT NULL,[CreatedDate] [datetime] NOT NULL,[ModifiedBy] [varchar](15) NOT NULL,[ModifiedDate] [datetime] NOT NULL,[DBaseQty] [decimal](18, 2) NULL," +
            "[DFOCQty] [decimal](18, 2) NULL,[GPCost] [decimal](18, 6) NULL,[DBaseQty2] [decimal](18, 2) NULL,[DFocQTy2] [decimal](18, 2) NULL,[GSTPerc] [decimal](18, 2) NULL,[GSTAmount] [decimal](18, 4) NULL,[NetDiscount] [decimal](18, 4) NULL,[GSTDisc] [decimal](18, 4) NULL,[GSTRoundOff] [decimal](18, 4) NULL," +
            "[NetPrice] [decimal](18, 4) NULL,[NetAmount] [decimal](18, 4) NULL,[ActualSales] [decimal](18, 4) NULL,[MAItemCode] [char](20) NOT NULL,[MAPRCCode] [char](10) NOT NULL,[SlNo] [numeric](18, 0)  NOT NULL,[Origin] [char](10) NULL,[MeasureUnit] [varchar](20) NULL,[ForeignCurrencyCode] [varchar](10) NULL," +
            "[ForeignCurrencyRate] [decimal](18, 6) NULL,[ForeignTotalValue] [decimal](18, 2) NULL,[FUnitPrice] [decimal](18, 4) NULL,[FGrossPrice] [decimal](18, 4) NULL,[FDiscUnitPrice] [numeric](18, 4) NULL,[FDiscount] [numeric](18, 2) NULL,[FTotalValue] [numeric](18, 4) NULL,[FGst] [numeric](18, 4) NULL,[FNetDiscount] [numeric](18, 4) NULL," +
            "[FNetCharges] [numeric](18, 4) NULL,[FGSTDisc] [decimal](19, 3) NULL,[FGSTRoundOff] [decimal](18, 4) NULL,[FNetPrice] [decimal](19, 4) NULL,[FNetAmount] [numeric](18, 4) NULL,[FActualSales] [numeric](18, 2) NULL,[BarCode] [char](30) NULL,[InvWQty] [decimal](18, 3) NULL,[InvLQty] [decimal](18, 3) NULL,[InvFOCQty] [decimal](18, 3) NULL)";

    private static final String tblSODetail_log="CREATE TABLE if not Exists [tblSODetail_log]([SONO] [char](20) NOT NULL,[PoNo] [char](20) NOT NULL,[ReleaseNumber] [tinyint] NOT NULL,[ItemCode] [char](12) NOT NULL,[ItemDesc] [nvarchar](100) NULL,[StoreCode] [char](15) NOT NULL,[PrcCode] [char](15) NOT NULL," +
            "[DeptCode] [char](15) NULL,[Quotation] [char](15) NULL,[LUomCode] [char](10) NOT NULL,[WUomCode] [char](15) NOT NULL,[WQty] [decimal](18, 3) NOT NULL,[LQty] [decimal](18, 3) NULL,[BaseUomCode] [char](15) NULL,[BaseQty] [decimal](18, 3) NULL,[PoPrice] [money] NULL," +
            "[UnitPrice] [money] NULL,[Disc1Perc] [numeric](18, 2) NULL,[Disc2Perc] [numeric](18, 2) NULL,[Disc3Perc] [numeric](18, 2) NULL,[Disc4Perc] [numeric](18, 2) NULL,[DiscAmount] [money] NULL,[Discount] [decimal](18, 4) NULL,[DiscUnitPrice] [money] NULL,[TotalValue] [money] NULL," +
            "[DeliveryDate] [datetime] NULL,[Remarks] [nvarchar](100) NULL,[PoReqNo] [decimal](10, 0) NULL,[DeliverQty] [decimal](18, 3) NOT NULL,[InvoiceQty] [decimal](18, 3) NOT NULL,[AcctCode] [char](15) NULL,[DrCostCentre] [char](15) NULL,[DrGlAccount] [char](15) NULL,[CrCostCentre] [char](15) NULL," +
            "[CrGlAccount] [char](15) NULL,[FocQty] [decimal](18, 3) NULL,[OffSetWQty] [decimal](18, 3) NULL,[WUnit] [smallint] NOT NULL,[CreatedBy] [varchar](15) NOT NULL,[CreatedDate] [datetime] NOT NULL,[ModifiedBy] [varchar](15) NOT NULL,[ModifiedDate] [datetime] NOT NULL,[DBaseQty] [decimal](18, 2) NULL," +
            "[DFOCQty] [decimal](18, 2) NULL,[GPCost] [decimal](18, 6) NULL,[DBaseQty2] [decimal](18, 2) NULL,[DFocQTy2] [decimal](18, 2) NULL,[GSTPerc] [decimal](18, 2) NULL,[GSTAmount] [decimal](18, 4) NULL,[NetDiscount] [decimal](18, 4) NULL,[GSTDisc] [decimal](18, 4) NULL,[GSTRoundOff] [decimal](18, 4) NULL," +
            "[NetPrice] [decimal](18, 4) NULL,[NetAmount] [decimal](18, 4) NULL,[ActualSales] [decimal](18, 4) NULL,[MAItemCode] [char](20) NOT NULL,[MAPRCCode] [char](10) NOT NULL,[SlNo] [numeric](18, 0)  NOT NULL,[Origin] [char](10) NULL,[MeasureUnit] [varchar](20) NULL,[ForeignCurrencyCode] [varchar](10) NULL," +
            "[ForeignCurrencyRate] [decimal](18, 6) NULL,[ForeignTotalValue] [decimal](18, 2) NULL,[FUnitPrice] [decimal](18, 4) NULL,[FGrossPrice] [decimal](18, 4) NULL,[FDiscUnitPrice] [numeric](18, 4) NULL,[FDiscount] [numeric](18, 2) NULL,[FTotalValue] [numeric](18, 4) NULL,[FGst] [numeric](18, 4) NULL,[FNetDiscount] [numeric](18, 4) NULL," +
            "[FNetCharges] [numeric](18, 4) NULL,[FGSTDisc] [decimal](19, 3) NULL,[FGSTRoundOff] [decimal](18, 4) NULL,[FNetPrice] [decimal](19, 4) NULL,[FNetAmount] [numeric](18, 4) NULL,[FActualSales] [numeric](18, 2) NULL,[BarCode] [char](30) NULL,[InvWQty] [decimal](18, 3) NULL,[InvLQty] [decimal](18, 3) NULL,[InvFOCQty] [decimal](18, 3) NULL)";

    private static final String tblSOHeader="CREATE TABLE if not Exists [tblSOHeader]([SoNo] [char](15) NOT NULL,[SoType] [char](3) NOT NULL,[SoVersion] [tinyint] NOT NULL,[InvoiceExpect] [char](1) NOT NULL,[CustomerCode] [char](15) NOT NULL,[CustomerName] [varchar](50) NULL,[SoDate] [datetime] NOT NULL,[InvoiceNo] [char](15) NOT NULL,[InvoiceDueDate] [datetime] NULL," +
            "[DeliveryDate] [datetime] NULL,[CurrCode] [char](3) NOT NULL,[CurrRate] [smallmoney] NULL,[ShipCode] [char](15) NULL,[Description] [varchar](100) NULL,[soTotalValue] [money] NULL,[sODiscPerc1] [numeric](18, 2) NULL,[sODiscPerc2] [numeric](18, 2) NULL,[sODiscPerc3] [numeric](18, 2) NULL,[sODiscPerc4] [numeric](18, 2) NULL,[sODiscAmount] [money] NULL," +
            "[sOSubTotal] [money] NULL,[Gst] [smallmoney] NULL,[Custom] [smallmoney] NULL,[Freight] [smallmoney] NULL,[Insurance] [smallmoney] NULL,[Discount] [smallmoney] NULL,[Others] [smallmoney] NULL,[soNetValue] [money] NULL,[Cancelled] [char](1) NOT NULL,[Printed] [char](1) NOT NULL,[NoCopies] [tinyint] NULL,[Faxed] [char](1) NOT NULL,[Email] [char](1) NOT NULL," +
            "[ApprovedBy] [char](15) NULL,[ApprovedDate] [datetime] NULL,[Status] [char](1) NOT NULL,[soAtoDate] [datetime] NULL,[CompanyCode] [char](5) NOT NULL,[Attn] [varchar](50) NULL,[soReqNo] [int] NOT NULL,[PendingItem] [char](1) NULL,[CreatedBy] [varchar](15) NOT NULL,[CreatedDate] [datetime] NOT NULL,[ModifiedBy] [varchar](15) NOT NULL,[ModifiedDate] [datetime] NULL," +
            "[AccDeptCode] [varchar](10) NULL,[AccDeptName] [varchar](50) NULL,[Address1] [varchar](50) NULL,[Address2] [varchar](50) NULL,[Address3] [varchar](50) NULL,[Phone] [varchar](12) NULL,[Fax] [varchar](12) NULL,[SalesManID] [char](10) NULL,[Terms] [varchar](10) NULL,[ShippingSlNo] [varchar](15) NULL,[ShippingTo] [varchar](50) NULL,[ShippingAddr1] [varchar](50) NULL," +
            "[ShippingAddr2] [varchar](50) NULL,[ShippingPhone] [varchar](12) NULL,[ShippingFax] [varchar](12) NULL,[sODiscPerc] [decimal](18, 2) NULL,[TransportAmount] [decimal](18, 2) NULL,[GstType] [char](1) NULL,[PONo] [varchar](20) NULL,[Remarks] [varchar](50) NULL,[LocInvoiceNo] [varchar](15) NULL,[SODiscount] [decimal](18, 2) NULL,[GSTRoundOff] [decimal](18, 2) NULL," +
            "[ForeignCurrencyCode] [varchar](10) NULL,[ForeignCurrencyRate] [decimal](18, 6) NULL,[FSoTotalValue] [money] NULL,[FSoDiscAmount] [money] NULL,[FSoSubTotal] [money] NULL,[FGst] [smallmoney] NULL,[FDiscount] [smallmoney] NULL,[FSoNetValue] [money] NULL,[FSoDiscount] [decimal](18, 2) NULL,[FGSTRoundOff] [decimal](18, 2) NULL,[SubCompanyCode] [varchar](15) NULL)";

    private static final String tblSOHeader_log="CREATE TABLE if not Exists [tblSOHeader_log]([SoNo] [char](15) NOT NULL,[SoType] [char](3) NOT NULL,[SoVersion] [tinyint] NOT NULL,[InvoiceExpect] [char](1) NOT NULL,[CustomerCode] [char](15) NOT NULL,[CustomerName] [varchar](50) NULL,[SoDate] [datetime] NOT NULL,[InvoiceNo] [char](15) NOT NULL,[InvoiceDueDate] [datetime] NULL," +
            "[DeliveryDate] [datetime] NULL,[CurrCode] [char](3) NOT NULL,[CurrRate] [smallmoney] NULL,[ShipCode] [char](15) NULL,[Description] [varchar](100) NULL,[soTotalValue] [money] NULL,[sODiscPerc1] [numeric](18, 2) NULL,[sODiscPerc2] [numeric](18, 2) NULL,[sODiscPerc3] [numeric](18, 2) NULL,[sODiscPerc4] [numeric](18, 2) NULL,[sODiscAmount] [money] NULL," +
            "[sOSubTotal] [money] NULL,[Gst] [smallmoney] NULL,[Custom] [smallmoney] NULL,[Freight] [smallmoney] NULL,[Insurance] [smallmoney] NULL,[Discount] [smallmoney] NULL,[Others] [smallmoney] NULL,[soNetValue] [money] NULL,[Cancelled] [char](1) NOT NULL,[Printed] [char](1) NOT NULL,[NoCopies] [tinyint] NULL,[Faxed] [char](1) NOT NULL,[Email] [char](1) NOT NULL," +
            "[ApprovedBy] [char](15) NULL,[ApprovedDate] [datetime] NULL,[Status] [char](1) NOT NULL,[soAtoDate] [datetime] NULL,[CompanyCode] [char](5) NOT NULL,[Attn] [varchar](50) NULL,[soReqNo] [int] NOT NULL,[PendingItem] [char](1) NULL,[CreatedBy] [varchar](15) NOT NULL,[CreatedDate] [datetime] NOT NULL,[ModifiedBy] [varchar](15) NOT NULL,[ModifiedDate] [datetime] NULL," +
            "[AccDeptCode] [varchar](10) NULL,[AccDeptName] [varchar](50) NULL,[Address1] [varchar](50) NULL,[Address2] [varchar](50) NULL,[Address3] [varchar](50) NULL,[Phone] [varchar](12) NULL,[Fax] [varchar](12) NULL,[SalesManID] [char](10) NULL,[Terms] [varchar](10) NULL,[ShippingSlNo] [varchar](15) NULL,[ShippingTo] [varchar](50) NULL,[ShippingAddr1] [varchar](50) NULL," +
            "[ShippingAddr2] [varchar](50) NULL,[ShippingPhone] [varchar](12) NULL,[ShippingFax] [varchar](12) NULL,[sODiscPerc] [decimal](18, 2) NULL,[TransportAmount] [decimal](18, 2) NULL,[GstType] [char](1) NULL,[PONo] [varchar](20) NULL,[Remarks] [varchar](50) NULL,[LocInvoiceNo] [varchar](15) NULL,[SODiscount] [decimal](18, 2) NULL,[GSTRoundOff] [decimal](18, 2) NULL," +
            "[ForeignCurrencyCode] [varchar](10) NULL,[ForeignCurrencyRate] [decimal](18, 6) NULL,[FSoTotalValue] [money] NULL,[FSoDiscAmount] [money] NULL,[FSoSubTotal] [money] NULL,[FGst] [smallmoney] NULL,[FDiscount] [smallmoney] NULL,[FSoNetValue] [money] NULL,[FSoDiscount] [decimal](18, 2) NULL,[FGSTRoundOff] [decimal](18, 2) NULL,[SubCompanyCode] [varchar](15) NULL)";

    private static final String tblARInvoiceDetail="CREATE TABLE if not Exists [tblARInvoiceDetail]([InvoiceNo] [varchar](15) NOT NULL,[SlNo] [decimal](18, 0) NOT NULL,[InvoiceSlNo] [smallint] NULL,[InventoryCode] [varchar](20) NOT NULL,[Description] [varchar](100) NULL,[LUOMCode] [varchar](10) NOT NULL,[WUOMCode] [varchar](15) NULL,[WUnit] [smallint] NULL,[PrcCode] [varchar](10) NOT NULL,[WQty] [decimal](18, 3) NULL,[LQty] [decimal](18, 3) NULL,[FOCQty] [decimal](18, 3) NULL,[UnitPrice] [decimal](18, 4) NULL,[GrossPrice] [decimal](18, 4) NULL,[DiscUnitPrice] [numeric](18, 4) NULL,[Discount] [numeric](18, 2) NULL,[TotalValue] [numeric](18, 4) NULL,[Disc1] [decimal](18, 2) NULL,[Disc2] [decimal](18, 2) NULL,[Disc3] [decimal](18, 2) NULL,[DiscAmount] [decimal](18, 2) NULL,[DBaseQty] [decimal](18, 2) NULL,[DFOCQty] [decimal](18, 2) NULL,[DBaseQty2] [decimal](18, 2) NULL," +
            "[DFOCQty2] [decimal](18, 2) NULL,[GSTPerc] [decimal](19, 4) NULL,[Gst] [numeric](18, 4) NULL,[NetDiscount] [numeric](18, 4) NULL,[NetCharges] [numeric](18, 4) NULL,[GSTDisc] [decimal](19, 4) NULL,[GSTRoundOff] [decimal](18, 4) NULL,[NetPrice] [decimal](19, 4) NULL,[NetAmount] [numeric](18, 4) NULL,[ActualSales] [numeric](18, 2) NULL,[AverageCost] [numeric](18, 2) NULL,[SalesManId] [varchar](10) NULL,[LocationCode] [char](20) NULL,[DeptCode] [varchar](50) NULL,[CategoryCode] [varchar](50) NULL,[BrandCode] [varchar](50) NULL,[OutletPrice] [numeric](18, 2) NULL,[MemberDiscount] [numeric](18, 2) NULL,[BaseQty] [decimal](18, 3) NULL,[SalesByMonth] [decimal](18, 3) NULL,[SalesByYear] [decimal](18, 3) NULL,[ReturnByMonth] [decimal](18, 3) NULL,[ReturnByYear] [decimal](18, 3) NULL,[BalanceStock] [decimal](18, 3) NULL,[OutletStock] [decimal](18, 3) NULL," +
            "[RecomRetailPrice] [decimal](18, 2) NULL,[CreateUser] [varchar](10) NOT NULL,[CreateDate] [smalldatetime] NOT NULL,[ModifyUser] [varchar](10) NULL,[ModifyDate] [smalldatetime] NULL,[GPCost] [decimal](18, 6) NULL,[MRP] [decimal](18, 2) NOT NULL,[MAItemCode] [char](20) NOT NULL,[MAPRCCode] [char](10) NOT NULL,[MAMRP] [decimal](18, 2) NOT NULL,[Origin] [char](10) NULL,[MeasureUnit] [varchar](20) NULL,[ForeignCurrencyCode] [varchar](10) NULL,[ForeignCurrencyRate] [decimal](18, 6) NULL,[ForeignTotalValue] [decimal](18, 2) NULL,[FUnitPrice] [decimal](18, 4) NULL,[FGrossPrice] [decimal](18, 4) NULL,[FDiscUnitPrice] [numeric](18, 4) NULL,[FDiscount] [numeric](18, 2) NULL,[FTotalValue] [numeric](18, 4) NULL,[FGst] [numeric](18, 4) NULL,[FNetDiscount] [numeric](18, 4) NULL,[FNetCharges] [numeric](18, 4) NULL,[FGSTDisc] [decimal](19, 3) NULL," +
            "[FGSTRoundOff] [decimal](18, 4) NULL,[FNetPrice] [decimal](19, 4) NULL,[FNetAmount] [numeric](18, 4) NULL,[FActualSales] [numeric](18, 2) NULL,[BarCode] [char](30) NULL,[DeliveredWQty] [decimal](18, 3) NULL,[DeliveredLQty] [decimal](18, 3) NULL,[DeliveredFOCQty] [decimal](18, 3) NULL)";

    private static final String tblARInvoiceHeader="CREATE TABLE if not Exists [tblARInvoiceHeader]([InvoiceNo] [varchar](15) NOT NULL,[InvoiceDate] [smalldatetime] NULL,[AccDeptCode] [varchar](10) NULL,[AccDeptName] [varchar](50) NULL,[TotalValue] [numeric](18, 2) NULL,[Gst] [numeric](18, 4) NULL,[DetailDiscount] [numeric](18, 2) NULL,[NetDiscount] [numeric](18, 2) NULL,[Discount] [numeric](18, 2) NULL,[DiscountType] [char](15) NULL,[NetTotal] [numeric](18, 4) NULL,[CashierID] [char](10) NULL,[LocationCode] [char](6) NULL,[TerminalCode] [char](10) NOT NULL,[TotalLines] [int] NULL,[TenderAmount] [numeric](18, 2) NULL,[Balance] [numeric](18, 2) NULL,[SalesType] [char](10) NULL,[CustomerCode] [varchar](20) NULL,[CustomerName] [varchar](50) NULL,[Address1] [varchar](50) NULL,[Address2] [varchar](50) NULL,[Address3] [varchar](50) NULL,[Phone] [char](15) NULL,[Fax] [char](15) NULL,[MemberDiscount] [numeric](18, 0) NULL,[GstType] [char](10) NULL,[PaidStatus] [char](10) NULL,[ChequeNo] [char](10) NULL,[ChequeDate] [smalldatetime] NULL," +
            "[SoNo] [varchar](15) NULL,[AccFlag] [char](1) NULL,[SalesManID] [char](10) NULL,[CommPerc] [decimal](8, 2) NULL,[PONo] [varchar](15) NULL,[Verified] [char](1) NULL,[VerifiedBy] [varchar](10) NULL,[VerifiedDate] [smalldatetime] NULL,[InvoiceType] [char](1) NULL,[Remarks] [varchar](50) NULL,[Delivered] [char](1) NULL,[PikLstPrint] [char](1) NULL,[BCPRint] [char](1) NULL,[Printed] [char](1) NULL,[Terms] [varchar](10) NULL,[ShippingSlNo] [varchar](15) NULL,[ShippingTo] [varchar](50) NULL,[ShippingAddr1] [varchar](50) NULL,[ShippingAddr2] [varchar](50) NULL,[ShippingPhone] [varchar](12) NULL,[ShippingFax] [varchar](12) NULL,[CreateUser] [char](10) NOT NULL,[CreateDate] [smalldatetime] NOT NULL,[ModifyUser] [char](10) NULL,[ModifyDate] [smalldatetime] NULL,[NetDiscountPerc] [decimal](18, 2) NULL,[TransportAmount] [decimal](18, 2) NULL,[BCDelayPrint] [char](1) NULL,[MiscDescription] [varchar](300) NULL,[MiscAmount] [decimal](18, 2) NULL,[DeliveryManID] [char](10) NULL,[SubTotal] [decimal](18, 2) NULL,[GSTRoundOff] [decimal](18, 2) NULL," +
            "[TotalCharges] [decimal](18, 2) NULL,[GSTDiscount] [decimal](18, 2) NULL,[Cancelled] [char](1) NULL,[SONetDiscount] [decimal](18, 2) NULL,[DeliveryDate] [datetime] NULL,[ForeignCurrencyCode] [varchar](10) NULL,[ForeignCurrencyRate] [decimal](18, 6) NULL,[FTotalValue] [numeric](18, 2) NULL,[FGst] [numeric](18, 4) NULL,[FDetailDiscount] [numeric](18, 2) NULL,[FNetDiscount] [numeric](18, 2) NULL,[FDiscount] [numeric](18, 2) NULL,[FNetTotal] [numeric](18, 4) NULL,[FSubTotal] [decimal](18, 2) NULL,[FGSTRoundOff] [decimal](18, 2) NULL,[FTotalCharges] [decimal](18, 2) NULL,[FGSTDiscount] [decimal](18, 2) NULL,[PriceGroupCode] [char](20) NULL,[SubCompanyCode] [varchar](10) NULL)";

    private static final String tblDownloadLog="Create Table if not Exists tblDownloadLog([InvLastSync] [datetime] NULL,[CustLastSync] [datetime] NULL,[UserLastSync] [datetime] NULL)";

    private static final String tblInventoryStockupdatefromPDA="Create Table if not Exists tblInventoryStockupdatefromPDA ([InventoryCode] [char](20) NOT NULL,[PreviousQOH] [char](10) NOT NULL,[NewQOH] [char](10) NOT NULL,[CreateUser] [char](10) NOT NULL,[CreateDate] [smalldatetime] NOT NULL)";

    private static final String tblInventoryStockupdate="Create Table if not Exists tblInventoryStockupdate ([InventoryCode] [char](20) NOT NULL,[NewQOH] [char](10) NOT NULL,[CreateUser] [char](10) NOT NULL,[CreateDate] [smalldatetime] NOT NULL)";

    private static final String tblStockUpdateLog="Create Table if not Exists tblStockUpdateLog ([InvStkLastSync] [char] (20) NOT NULL)";

    public DbHelper(Context context) {
//        super(context, name, factory, version);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this._context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase mDb) {
//        mDb.execSQL("Drop Table tblARCustomer");
        try {
            mDb.execSQL(tblInvVendor);
            mDb.execSQL(tblVwInventory);
            mDb.execSQL(tblCollectionentryDetail);
            mDb.execSQL(tblCollectionentryHeader);
            mDb.execSQL(tblCollectionEntryNo);
            mDb.execSQL(tblArCustomer);
            mDb.execSQL(tblUserMaster);
            mDb.execSQL(tblSalesMan);
            mDb.execSQL(tblBarCode);
            mDb.execSQL(tblCompany);
            mDb.execSQL(tblAndroidDeviceConfig);
            mDb.execSQL(tblParamMaster);
            mDb.execSQL(tblARCustomerShippingAddress);
            mDb.execSQL(tblSODetail);
            mDb.execSQL(tblSODetail_log);
            mDb.execSQL(tblSOHeader);
            mDb.execSQL(tblSOHeader_log);
            mDb.execSQL(tblbank);
            mDb.execSQL(tblDownloadLog);
            mDb.execSQL(tblSubCompany);
            mDb.execSQL(tblARInvoiceDetail);
            mDb.execSQL(tblARInvoiceHeader);
            mDb.execSQL(tblInventoryStockupdatefromPDA);
            mDb.execSQL(tblInventoryStockupdate);
            mDb.execSQL(tblStockUpdateLog);
            Log.e("Table","Created Successfully");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase mDb, int i, int i1) {
        mDb.execSQL("DROP TABLE IF EXISTS tblSOHeader");
        mDb.execSQL("DROP TABLE IF EXISTS tblSOHeader_log");
        mDb.execSQL("DROP TABLE IF EXISTS tblCompany");
        mDb.execSQL("DROP TABLE IF EXISTS tblSubCompany");
        onCreate(mDb);
    }

    public static String ConnectLocalDB() {
        if(isRunningOnEmulator())
        {
            sDBDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/Unipro/");
        }
        else
        {
            if (Environment.isExternalStorageRemovable())
            {
                sDBDirectory=new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/Unipro/");
            }
            else
            {
                sDBDirectory=new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/Unipro/");
            }
        }
        if (!sDBDirectory.exists())
        {
            sDBDirectory.mkdir();
        }
        Log.e("***LocalDB Path", sDBDirectory.toString());
        return sDBDirectory.toString();
    }
    private static boolean isRunningOnEmulator(){
        boolean result=//
                Build.FINGERPRINT.startsWith("generic")//
                        ||Build.FINGERPRINT.startsWith("unknown")//
                        ||Build.MODEL.contains("google_sdk")//
                        ||Build.MODEL.contains("Emulator")//
                        ||Build.MODEL.contains("Android SDK built for x86")
                        ||Build.MANUFACTURER.contains("Genymotion");
        if(result)
            return true;
        result|=Build.BRAND.startsWith("generic")&& Build.DEVICE.startsWith("generic");
        if(result)
            return true;
        result|="google_sdk".equals(Build.PRODUCT);
        return result;
    }
}
