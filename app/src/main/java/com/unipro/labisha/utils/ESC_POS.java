package com.unipro.labisha.utils;

import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * EPSON ESC/POS Commands
 * Created by Kalaivanan on 25.08.2016.
 */
public class ESC_POS {

    public static final byte ESC = 27;
    public static final byte FS = 28;
    public static final byte GS = 29;
    public static final byte DLE = 16;
    public static final byte EOT = 4;
    public static final byte ENQ = 5;
    public static final byte SP = 32;
    public static final byte HT = 9;
    public static final byte LF = 10;
    public static final byte CR = 13;
    public static final byte FF = 12;
    public static final byte CAN = 24;

    public static class CodePage {
        public static final byte PC437       = 0;
        public static final byte KATAKANA    = 1;
        public static final byte PC850       = 2;
        public static final byte PC860       = 3;
        public static final byte PC863       = 4;
        public static final byte PC865       = 5;
        public static final byte WPC1252     = 16;
        public static final byte PC866       = 17;
        public static final byte PC852       = 18;
        public static final byte PC858       = 19;
    }

    public static class BarCode {
        public static final byte UPC_A       = 0;
        public static final byte UPC_E       = 1;
        public static final byte EAN13       = 2;
        public static final byte EAN8        = 3;
        public static final byte CODE39      = 4;
        public static final byte ITF         = 5;
        public static final byte NW7         = 6;
        //public static final byte CODE93      = 72;
        // public static final byte CODE128     = 73;
    }



    public  byte[] print_linefeed()
    {
        byte[] result = new byte[1];
        result[0] = LF;
        return result;
    }

    public  byte[] underline_1dot_on()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 1;
        return result;
    }

    public  byte[] underline_2dot_on()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 2;
        return result;
    }

    public  byte[] underline_off()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 0;
        return result;
    }



    public  byte[] init_printer()
    {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 64;
        return result;
    }

    public  byte[] emphasized_on()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0xF;
        return result;
    }

    public  byte[] emphasized_off()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0;
        return result;
    }

    public  byte[] double_strike_on()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 71;
        result[2] = 0xF;
        return result;
    }

    public  byte[] double_strike_off()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 71;
        result[2] = 0xF;
        return result;
    }

    public  byte[] select_fontA()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 77;
        result[2] = 0;
        return result;
    }

    public  byte[] select_fontB()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 77;
        result[2] = 1;
        return result;
    }
    public  byte[] select_fontC()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 77;
        result[2] = 2;
        return result;
    }

    public  byte[] double_height_width_on()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 56;
        return result;
    }

    public  byte[] double_height_width_off()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 0;
        return result;
    }

    public  byte[] double_height_on()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 16;
        return result;
    }

    public  byte[] double_height_off()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;
        result[2] = 0;
        return result;
    }

    public  byte[] justification_left()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 0;
        return result;
    }

    public  byte[] justification_center()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 1;
        return result;
    }
    public  byte[] justification_right()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 2;
        return result;
    }

    public byte[] print_and_feed_lines(byte n)
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 100;
        result[2] = n;
        return result;
    }

    public byte[] print_and_reverse_feed_lines(byte n)
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 101;
        result[2] = n;
        return result;
    }
    public byte[] drawer_kick()
    {
        byte[] result = new byte[5];
        result[0] = ESC;
        result[1] = 112;
        result[2] = 0;
        result[3] = 60;
        result[4] = 120;
        return result;
    }

    public byte[] select_color1()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 114;
        result[2] = 0;
        return result;
    }

    public byte[] select_color2()
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 114;
        result[2] = 1;
        return result;
    }

    public  byte[] select_code_tab(byte cp)
    {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 116;
        result[2] = cp;
        return result;
    }

    public  byte[] white_printing_on()
    {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 66;
        result[2] = (byte)128;
        return result;
    }

    public  byte[] white_printing_off()
    {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 66;
        result[2] = 0;
        return result;
    }


    public  byte[] feedpapercut()
    {
        byte[] result = new byte[4];
        result[0] = GS;
        result[1] = 86;
        result[2] = 65;
        result[3] = 0;
        return result;
    }

    public  byte[] feedpapercut_partial()
    {
        byte[] result = new byte[4];
        result[0] = GS;
        result[1] = 86;
        result[2] = 66;
        result[3] = 0;
        return result;
    }

    public  byte[] barcode_height(byte dots)
    {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 104;
        result[2] = dots;
        return result;
    }

    public  byte[] select_font_hri( byte n )
    {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 102;
        result[2] = n;
        return result;
    }

    public  byte[] select_position_hri( byte n )
    {
        byte[] result = new byte[3];
        result[0] = GS;
        result[1] = 72;
        result[2] = n;
        return result;
    }

    public  byte[] print_bar_code(byte barcode_typ, String barcode2print )
    {
        byte[] barcodebytes = barcode2print.getBytes();
        byte[] result = new byte[3+barcodebytes.length+1];
        result[0] = GS;
        result[1] = 107;
        result[2] = barcode_typ;
        int idx = 3;
        for ( int i = 0; i < barcodebytes.length; i++ )
        {
            result[idx] = barcodebytes[i];
            idx++;
        }
        result[idx] = 0;
        return result;
    }
    public byte[] setBold(Boolean bool){
        byte[] result = new byte[3];
        result[0]= 0x1B ;
        result[1]=0x45;//"E"
        if(bool)
            result[2]=1;
        else
            result[2]=0;
//        ((int)(bool?1:0));
        return result;
    }
    public byte[] setItalic(Boolean bool){
        byte[] result = new byte[3];
        result[0]= 0x1B ;
        result[1]=0x45;//"E"
        if(bool)
            result[2]=1;
        else
            result[2]=0;
        return result;
    }

    public  byte[] set_HT_position( byte col )
    {
        byte[] result = new byte[4];
        result[0] = ESC;
        result[1] = 68;
        result[2] = col;
        result[3] = 0;
        return result;
    }

    public void print_line( String line)
    {
        if ( line.isEmpty())
            return;
//        mPort.AddData2Printer(line.getBytes(Charset.forName("ISO-8859-1")));
        print_linefeed();
    }

    public void print_text(String line)
    {
        if ( line.isEmpty()) return;
//        //mPort.AddData2Printer(line.getBytes());
//        mPort.AddData2Printer(line.getBytes(Charset.forName("ISO-8859-1")));
    }


    public void print_sample1()
    {
        String test = null;
        init_printer();
        select_fontA();
        select_code_tab(CodePage.WPC1252);
        underline_1dot_on();
        justification_center();
        test = "Sample Receipt 1";
        print_line(test);
        test = "Umlaute";
        print_line(test);
        double_height_width_on();
        test = "ÄÖÜß";
        print_line(test);
        double_height_width_off();

        feedpapercut();
    }


    public Boolean print_sample(Socket sock) throws Exception
    {
        OutputStream oStream=sock.getOutputStream();
        String test = null;

        oStream.write(init_printer());
        oStream.write(select_fontA());
        oStream.write(underline_1dot_on());
        oStream.write(justification_center());
        test = "Sample Receipt";
//        print_line(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(underline_off());
        oStream.write(print_linefeed());
        oStream.write(justification_left());
        test = "Left justification";
//        print_text(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(justification_right());
        test = "right justification";
//        print_line(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(print_linefeed());
        oStream.write(justification_left());
        test = "Testzeile\tTab1\tTab2";
//        print_line(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(set_HT_position((byte) 35)); //Set horizontal tab positions: 35th column
        test = "Testzeile\tTab1";
//        print_line(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(emphasized_on());
        test = "emphasized_on";
//        print_line(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(emphasized_off());
        oStream.write(underline_2dot_on());
        oStream.write(justification_right());
        test = "underline 2dot";
//        print_line(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(underline_off());
        oStream.write(double_strike_on());
        test = "double strike";
//        print_line(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(double_strike_off());
        oStream.write(select_fontB());
        test = "Font B";
//        print_line(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(white_printing_on());
        test = "white printing on";
//        print_line(test);
        oStream.write(test.getBytes(Charset.forName("ISO-8859-1")));
        oStream.write(print_linefeed());
        oStream.write(white_printing_off());
        oStream.write(print_and_feed_lines((byte) 3));
        oStream.write(select_position_hri((byte) 2));
        oStream.write(print_bar_code(BarCode.CODE39, "123456789"));
        oStream.write(print_linefeed());

        oStream.write(print_and_feed_lines((byte) 2));
        oStream.write(barcode_height((byte) 80));
        oStream.write(justification_center());
        oStream.write(select_position_hri((byte) 1));
        oStream.write(print_bar_code(BarCode.EAN13, "9783125171541"));
        oStream.write(print_linefeed());
        oStream.write(feedpapercut());
        oStream.close();
        return true;
    }
}
