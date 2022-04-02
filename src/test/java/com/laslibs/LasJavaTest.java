package com.laslibs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;


public class LasJavaTest
{
    LasJava las;
    @Before
    public void setup(){
        las = new LasJava("./src/test/java/com/laslibs/sample/example1.las", true);
    }

    @Test
    public void getLas(){
        Assert.assertNotNull(las);
    }

    @Test
    public void getMetaVersion(){
        Double version = las.getVersion();
        Assert.assertNotNull(version);
        Assert.assertEquals(version, Double.valueOf("2.0"));
    }

    @Test
    public void getMetaWrap(){
        Boolean version = las.getWrap();
        Assert.assertFalse(version);
    }

    @Test
    public void getCurveParams(){
        Map<String, Map<String, String>> value = las.getCurveParams();
        Assert.assertEquals(value.get("DT").get("unit"), "US/M");
    }

    @Test
    public void getWellParams(){
        Map<String, Map<String, String>> value = las.getWellParams();
        Assert.assertEquals(value.get("DATE").get("value"), "13-DEC-86");
    }

    @Test
    public void getOther(){
        String value = las.other();
        Assert.assertFalse(value.isEmpty());
    }

    @Test
    public void getHeader(){
        String[] value = las.getHeader();
        Assert.assertTrue(Arrays.asList(value).contains("DT"));
    }

    @Test
    public void getHeaderAndDescr(){
        Map<String, String> value = las.getHeaderAndDescr();
        Assert.assertEquals(value.get("DT"), "2  SONIC TRANSIT TIME");
    }

    @Test
    public void getDataStripped(){
        String[][] value = las.dataStripped();
        Assert.assertEquals(value.length, 3);
        Assert.assertEquals(value[0][0], "1670.000");
    }

    @Test
    public void toCsv(){
        Assert.assertTrue(las.toCsv("mainTest"));
    }

    @Test
    public void toCsvStripped(){
        Assert.assertTrue(las.toCsvStripped("mainTest"));
    }

    @Test
    public void getColumn(){
        String[] value = las.getColumn("dept");
        Assert.assertEquals(value[0], "1670.000");
    }

    @Test
    public void getColumnStripped(){
        String[] value = las.getColumnStripped("dept");
        Assert.assertEquals(value[0], "1670.000");
    }
}
