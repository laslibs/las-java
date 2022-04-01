package com.laslibs;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

/**
 * Unit test for simple App.
 */
public class LasJavaTest
{
    // String
    String lasString;
    @Before
    public void setup(){
        lasString = "~VERSION INFORMATION\n" +
                "VERS.                          2.0 :   CWLS LOG ASCII STANDARD -VERSION 2.0\n" +
                "WRAP.                          NO  :   ONE LINE PER DEPTH STEP\n" +
                "~WELL INFORMATION\n" +
                "#MNEM.UNIT              DATA                       DESCRIPTION\n" +
                "#----- -----            ----------               -------------------------\n" +
                "STRT    .M              1670.0000                :START DEPTH\n" +
                "STOP    .M              1669.7500                :STOP DEPTH\n" +
                "STEP    .M              -0.1250                  :STEP\n" +
                "NULL    .               -999.25                  :NULL VALUE\n" +
                "COMP    .       ANY OIL COMPANY INC.             :COMPANY\n" +
                "WELL    .       ANY ET AL 12-34-12-34            :WELL\n" +
                "FLD     .       WILDCAT                          :FIELD\n" +
                "LOC     .       12-34-12-34W5M                   :LOCATION\n" +
                "PROV    .       ALBERTA                          :PROVINCE\n" +
                "SRVC    .       ANY LOGGING COMPANY INC.         :SERVICE COMPANY\n" +
                "DATE    .       13-DEC-86                        :LOG DATE\n" +
                "UWI     .       100123401234W500                 :UNIQUE WELL ID\n" +
                "~CURVE INFORMATION\n" +
                "#MNEM.UNIT              API CODES                   CURVE DESCRIPTION\n" +
                "#------------------     ------------              -------------------------\n" +
                " DEPT   .M                                       :  1  DEPTH\n" +
                " DT     .US/M           60 520 32 00             :  2  SONIC TRANSIT TIME\n" +
                " RHOB   .K/M3           45 350 01 00             :  3  BULK DENSITY\n" +
                " NPHI   .V/V            42 890 00 00             :  4  NEUTRON POROSITY\n" +
                " SFLU   .OHMM           07 220 04 00             :  5  SHALLOW RESISTIVITY\n" +
                " SFLA   .OHMM           07 222 01 00             :  6  SHALLOW RESISTIVITY\n" +
                " ILM    .OHMM           07 120 44 00             :  7  MEDIUM RESISTIVITY\n" +
                " ILD    .OHMM           07 120 46 00             :  8  DEEP RESISTIVITY\n" +
                "~PARAMETER INFORMATION\n" +
                "#MNEM.UNIT              VALUE             DESCRIPTION\n" +
                "#--------------     ----------------      -----------------------------------------------\n" +
                " MUD    .               GEL CHEM        :   MUD TYPE\n" +
                " BHT    .DEGC           35.5000         :   BOTTOM HOLE TEMPERATURE\n" +
                " BS     .MM             200.0000        :   BIT SIZE\n" +
                " FD     .K/M3           1000.0000       :   FLUID DENSITY\n" +
                " MATR   .               SAND            :   NEUTRON MATRIX\n" +
                " MDEN   .               2710.0000       :   LOGGING MATRIX DENSITY\n" +
                " RMF    .OHMM           0.2160          :   MUD FILTRATE RESISTIVITY\n" +
                " DFD    .K/M3           1525.0000       :   DRILL FLUID DENSITY\n" +
                "~OTHER\n" +
                "     Note: The logging tools became stuck at 625 metres causing the data\n" +
                "     between 625 metres and 615 metres to be invalid.\n" +
                "~A  DEPTH     D/T    RHOB3        NPHI   SFLU    SFLA      ILM      ILD\n" +
                "1670.000   123.450 2550.000    0.450  123.450  123.450  110.200  105.600\n" +
                "1669.875   123.450 2550.000    0.450  123.450  123.450  110.200  105.600\n" +
                "1669.750   123.450 2550.000    0.450  123.450  123.450  110.200  105.600\n" +
                "1669.745   123.450 2550.000    -999.25  123.450  123.450  110.200  105.600";
    }

    @Test
    public void getMetaVersion(){
        LasJava las = new LasJava(lasString, false);
        Double version = las.getVersion();
        Assert.assertNotNull(version);
        Assert.assertEquals(version, Double.valueOf("2.0"));
    }

    @Test
    public void getMetaWrap(){
        LasJava las = new LasJava(lasString, false);
        Boolean version = las.getWrap();
        Assert.assertFalse(version);
    }

    @Test
    public void getCurveParams(){
        LasJava las = new LasJava(lasString, false);
        Map<String, Map<String, String>> value = las.getCurveParams();
        Assert.assertEquals(value.get("DT").get("unit"), "US/M");
    }

    @Test
    public void getWellParams(){
        LasJava las = new LasJava(lasString, false);
        Map<String, Map<String, String>> value = las.getWellParams();
        Assert.assertEquals(value.get("DATE").get("value"), "13-DEC-86");
    }
}
