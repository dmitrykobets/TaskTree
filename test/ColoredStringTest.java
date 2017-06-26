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
    public void noColor() {
        ColoredString str = new ColoredString("0123");
        assertEquals(str.toString(), "0123");
    }
    
    @Test
    public void singleColor() {
        // 1
        ColoredString str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 1);
        assertEquals(str.toString(), ColoredString.RED + "0" + ColoredString.ANSI_RESET + "123");
        
        // 2
        str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 2);
        assertEquals(str.toString(), ColoredString.RED + "01" + ColoredString.ANSI_RESET + "23");

        // all
        str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 4);
        assertEquals(str.toString(), ColoredString.RED + "0123" + ColoredString.ANSI_RESET);
    }
    
    @Test
    public void exceedEnd() {
        ColoredString str = new ColoredString("012345");
        str.applyColor(ColoredString.RED, 0, 100);
        assertEquals(str.toString(), ColoredString.RED + "012345" + ColoredString.ANSI_RESET);
    }
    
    @Test
    public void twoColorsNoOverlap() {
        // neighbor
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 1);
        str.applyColor(ColoredString.BLUE, 1, 1);
        assertEquals(str.toString(), ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.BLUE + "1" + ColoredString.ANSI_RESET + "234");
        
        // gap
        str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 1);
        str.applyColor(ColoredString.BLUE, 2, 1);
        assertEquals(str.toString(), ColoredString.RED + "0" + ColoredString.ANSI_RESET + "1" + ColoredString.BLUE + "2" + ColoredString.ANSI_RESET + "34");
    }
    
    @Test
    public void twoColorsOverlapStartAtSamePoint() {
        // gap
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 4);
        str.applyColor(ColoredString.BLUE, 0, 2);
        assertEquals(str.toString(), ColoredString.BLUE + "01" + ColoredString.ANSI_RESET + ColoredString.RED + "23" + ColoredString.ANSI_RESET + "4");
        
        // neighbor
        str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 4);
        str.applyColor(ColoredString.BLUE, 0, 3);
        assertEquals(str.toString(), ColoredString.BLUE + "012" + ColoredString.ANSI_RESET + ColoredString.RED + "3" + ColoredString.ANSI_RESET + "4");
    }
    
    @Test
    public void twoColorsOverlapEndAtSamePoint() {
        // gap
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 1, 4);
        str.applyColor(ColoredString.BLUE, 3, 2);
        assertEquals(str.toString(), "0" + ColoredString.RED + "12" + ColoredString.ANSI_RESET + ColoredString.BLUE + "34" + ColoredString.ANSI_RESET);
        
        str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 1, 4);
        str.applyColor(ColoredString.BLUE, 2, 3);
        assertEquals(str.toString(), "0" + ColoredString.RED + "1" + ColoredString.ANSI_RESET + ColoredString.BLUE + "234" + ColoredString.ANSI_RESET);
    }
    
    @Test
    public void twoColorsOneInMiddle() {
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 5);
        str.applyColor(ColoredString.BLUE, 1, 3);
        assertEquals(str.toString(), ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.BLUE + "123" + ColoredString.ANSI_RESET + ColoredString.RED + "4" + ColoredString.ANSI_RESET);
    }
    
    @Test
    public void twoColorsOverlapCompletely() {
        // 1
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 5);
        str.applyColor(ColoredString.BLUE, 1, 3);
        assertEquals(str.toString(), ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.BLUE + "123" + ColoredString.ANSI_RESET + ColoredString.RED + "4" + ColoredString.ANSI_RESET);
        
        // full
        str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 2, 1);
        str.applyColor(ColoredString.BLUE, 2, 1);
        assertEquals(str.toString(), "01" + ColoredString.BLUE + "2" + ColoredString.ANSI_RESET + "34");
    }
    
    @Test
    public void twoColorsOverlapSiamese() {
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 2);
        str.applyColor(ColoredString.BLUE, 1, 4);
        assertEquals(str.toString(), ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.BLUE + "1234" + ColoredString.ANSI_RESET);
    }
    
    @Test
    public void rainbowEndToEnd() {
        ColoredString str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 1);
        str.applyColor(ColoredString.YELLOW, 1, 1);
        str.applyColor(ColoredString.GREEN, 2, 1);
        str.applyColor(ColoredString.BLUE, 3, 1);
        assertEquals(str.toString(), ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.YELLOW + "1" + ColoredString.ANSI_RESET + ColoredString.GREEN + "2" + ColoredString.ANSI_RESET + ColoredString.BLUE + "3" + ColoredString.ANSI_RESET);
    }
    
    @Test
    public void rainbowCascade() {
        ColoredString str = new ColoredString("0123");
        str.applyColor(ColoredString.RED, 0, 4);
        str.applyColor(ColoredString.YELLOW, 1, 3);
        str.applyColor(ColoredString.GREEN, 2, 2);
        str.applyColor(ColoredString.BLUE, 3, 1);
        assertEquals(str.toString(), ColoredString.RED + "0" + ColoredString.ANSI_RESET + ColoredString.YELLOW + "1" + ColoredString.ANSI_RESET + ColoredString.GREEN + "2" + ColoredString.ANSI_RESET + ColoredString.BLUE + "3" + ColoredString.ANSI_RESET);
    }
}
