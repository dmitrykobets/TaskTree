/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class ColoredStringTest {
    
    public ColoredStringTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void length() {
        ColoredString str = new ColoredString("0123");
        assertEquals(4, str.length());
        str.applyColor(ColoredString.RED, 0, 2);
        assertEquals(4, str.length());
        str.applyColor(ColoredString.BLUE, 0, 100);
        assertEquals(4, str.length());
    }

    @Test
    public void noColor() {
        ColoredString str = new ColoredString("0123");
        assertEquals("0123", str.toString());
    }
    
    @Test
    public void singleColor() {
        // 1
        ColoredString str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 1);
        assertEquals(ColoredString.RED + "0" + ColoredString.ANSI_RESET + "123", str.toString());
        
        // 2
        str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 2);
        assertEquals(ColoredString.RED + "01" + ColoredString.ANSI_RESET + "23", str.toString());

        // all
        str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 4);
        assertEquals(ColoredString.RED + "0123" + ColoredString.ANSI_RESET, str.toString());
        
        // no params
        str = new ColoredString("0123");
        str.applyColor(ColoredString.RED);
        assertEquals(ColoredString.RED + "0123" + ColoredString.ANSI_RESET, str.toString());
        
        // rapid initialization no limits
        str = new ColoredString("0123", ColoredString.RED);
        assertEquals(ColoredString.RED + "0123" + ColoredString.ANSI_RESET, str.toString());
        
        // rapid initialization with limits
        str = new ColoredString("01234", ColoredString.RED, 2, 2);
        assertEquals("01" + ColoredString.RED + "23" + ColoredString.ANSI_RESET + "4", str.toString());
    }
    
    @Test
    public void exceedEnd() {
        ColoredString str = new ColoredString("012345");
        str.applyColor(ColoredString.RED, 0, 100);
        assertEquals(ColoredString.RED + "012345" + ColoredString.ANSI_RESET, str.toString());
    }
    
    @Test
    public void twoColorsNoOverlap() {
        // neighbor
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 1);
        str.applyColor(ColoredString.BLUE, 1, 1);
        assertEquals(ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.BLUE + "1" + ColoredString.ANSI_RESET + "234", str.toString());
        
        // gap
        str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 1);
        str.applyColor(ColoredString.BLUE, 2, 1);
        assertEquals(ColoredString.RED + "0" + ColoredString.ANSI_RESET + "1" + ColoredString.BLUE + "2" + ColoredString.ANSI_RESET + "34", str.toString());
    }
    
    @Test
    public void twoColorsOverlapStartAtSamePoint() {
        // gap
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 4);
        str.applyColor(ColoredString.BLUE, 0, 2);
        assertEquals(ColoredString.BLUE + "01" + ColoredString.ANSI_RESET + ColoredString.RED + "23" + ColoredString.ANSI_RESET + "4", str.toString());
        
        // neighbor
        str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 4);
        str.applyColor(ColoredString.BLUE, 0, 3);
        assertEquals(ColoredString.BLUE + "012" + ColoredString.ANSI_RESET + ColoredString.RED + "3" + ColoredString.ANSI_RESET + "4", str.toString());
    }
    
    @Test
    public void twoColorsOverlapEndAtSamePoint() {
        // gap
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 1, 4);
        str.applyColor(ColoredString.BLUE, 3, 2);
        assertEquals("0" + ColoredString.RED + "12" + ColoredString.ANSI_RESET + ColoredString.BLUE + "34" + ColoredString.ANSI_RESET, str.toString());
        
        str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 1, 4);
        str.applyColor(ColoredString.BLUE, 2, 3);
        assertEquals("0" + ColoredString.RED + "1" + ColoredString.ANSI_RESET + ColoredString.BLUE + "234" + ColoredString.ANSI_RESET, str.toString());
    }
    
    @Test
    public void twoColorsOneInMiddle() {
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 5);
        str.applyColor(ColoredString.BLUE, 1, 3);
        assertEquals(ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.BLUE + "123" + ColoredString.ANSI_RESET + ColoredString.RED + "4" + ColoredString.ANSI_RESET, str.toString());
    }
    
    @Test
    public void twoColorsOverlapCompletely() {
        // 1
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 5);
        str.applyColor(ColoredString.BLUE, 1, 3);
        assertEquals(ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.BLUE + "123" + ColoredString.ANSI_RESET + ColoredString.RED + "4" + ColoredString.ANSI_RESET, str.toString());
        
        // multiple with params
        str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 2, 1);
        str.applyColor(ColoredString.BLUE, 2, 1);
        assertEquals("01" + ColoredString.BLUE + "2" + ColoredString.ANSI_RESET + "34", str.toString());
        
        // full with no params
        str = new ColoredString("01234");
        str.applyColor(ColoredString.RED);
        str.applyColor(ColoredString.BLUE);
        assertEquals(ColoredString.BLUE + "01234" + ColoredString.ANSI_RESET, str.toString());
    }
    
    @Test
    public void twoColorsOverlapSiamese() {
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 2);
        str.applyColor(ColoredString.BLUE, 1, 4);
        assertEquals(ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.BLUE + "1234" + ColoredString.ANSI_RESET, str.toString());
    }
    
    @Test
    public void rainbowEndToEnd() {
        ColoredString str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 1);
        str.applyColor(ColoredString.YELLOW, 1, 1);
        str.applyColor(ColoredString.GREEN, 2, 1);
        str.applyColor(ColoredString.BLUE, 3, 1);
        assertEquals(ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.YELLOW + "1" + ColoredString.ANSI_RESET + ColoredString.GREEN + "2" + ColoredString.ANSI_RESET + ColoredString.BLUE + "3" + ColoredString.ANSI_RESET, str.toString());
    }
    
    @Test
    public void rainbowCascade() {
        ColoredString str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 4);
        str.applyColor(ColoredString.YELLOW, 1, 3);
        str.applyColor(ColoredString.GREEN, 2, 2);
        str.applyColor(ColoredString.BLUE, 3, 1);
        assertEquals(ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.YELLOW + "1" + ColoredString.ANSI_RESET + ColoredString.GREEN + "2" + ColoredString.ANSI_RESET + ColoredString.BLUE + "3" + ColoredString.ANSI_RESET, str.toString());
    }
    
    @Test
    public void prepend() {
        ColoredString str1 = new ColoredString("012");
        ColoredString str2 = new ColoredString("345");
        
        ColoredString str3 = str2.prepend(str1);
        assertEquals("012345", str3.toString());
        assertEquals(6, str3.length());
        
        str2.applyColor(ColoredString.RED, 0, 1);
        str3 = str2.prepend(str1);
        assertEquals("012" + ColoredString.RED + "3" + ColoredString.ANSI_RESET + "45", str3.toString());
        
        str1.applyColor(ColoredString.BLUE, 0, 100);
        str3 = str2.prepend(str1);
        assertEquals(ColoredString.BLUE + "012" + ColoredString.ANSI_RESET + ColoredString.RED + "3" + ColoredString.ANSI_RESET + "45", str3.toString());
        
        // pure string
        str3 = str2.prepend("012");
        assertEquals("012" + ColoredString.RED + "3" + ColoredString.ANSI_RESET + "45", str3.toString());
    }
    
    @Test
    public void append() {
        ColoredString str2 = new ColoredString("012");
        ColoredString str1 = new ColoredString("345");
        
        ColoredString str3 = str2.append(str1);
        assertEquals("012345", str3.toString());
        assertEquals(6, str3.length());
        
        str1.applyColor(ColoredString.RED, 0, 1);
        str3 = str2.append(str1);
        assertEquals("012" + ColoredString.RED + "3" + ColoredString.ANSI_RESET + "45", str3.toString());
        
        str2.applyColor(ColoredString.BLUE, 0, 100);
        str3 = str2.append(str1);
        assertEquals(ColoredString.BLUE + "012" + ColoredString.ANSI_RESET + ColoredString.RED + "3" + ColoredString.ANSI_RESET + "45", str3.toString());
        
        // pure string
        str3 = str2.append("345");
        assertEquals(ColoredString.BLUE + "012" + ColoredString.ANSI_RESET + "345", str3.toString());
        
    }
}   