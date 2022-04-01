package com.laslibs;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class LasJavaTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void getMeta(){
        String s = "~VERSION INFORMATION\n" +
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
                "WELL    .       ANY ET AL 12-34-12-34            :WELL";
        LasJava las = new LasJava(s, false);
        String[] st = las.metaData();
    }
}
