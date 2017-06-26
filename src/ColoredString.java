
import java.util.ArrayList;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class ColoredString {
    
    private final String ORIGINAL;
    private ArrayList<String> parts;
    
    /**
     * @param original an uncolored string.
     */
    public ColoredString(String original) {
        this.ORIGINAL = original;
        this.parts = new ArrayList();
        this.parts.add(this.ORIGINAL);
    }
    
    public void colorSubstring2(String color1, String color2, int startIdx, int length) {
        /*
        int idx = 0;
        for (int i = 0; i < parts.size() && idx < startIdx + length; i ++) {
            for (int j = 0; j < parts.get(i).length() && idx < startIdx + length; j ++) {
                System.out.println(i + " " + j + " " + idx);
                idx ++;
            }
        }*/
        this.splitAtIdx(startIdx + length - 1);
    }
    
    private void splitAtIdx(int target) {
        int idx = 0;
        int i = 0;
        int j = 0;
        for (i = 0; i < parts.size() && idx < target; i ++) {
            for (j = 0; j < parts.get(i).length() && idx < target; j ++) {
                System.out.println(i + " " + j + " " + idx);
                idx ++;
            }
        }
        System.out.println("end: " + i + " " + j + " " + idx);
    }
    
    public static String colorString(String str, String color) {
        return colorString(str, color, "");
    }
    public static String colorString(String str, String color1, String color2) {
        return colorSubstring(str, color1, color2, 0);
    }
    public static String colorSubstring(String str, String color, int startIdx) {
        return colorSubstring(str, color, "", startIdx);
    }
    public static String colorSubstring(String str, String color1, String color2, int startIdx) {
        return colorSubstring(str, color1, color2, startIdx, str.length() - startIdx); 
    }
    public static String colorSubstring(String str, String color, int startIdx, int length) {
        return colorSubstring(str, color, "", startIdx, length);
    }
    public static String colorSubstring(String str, String color1, String color2, int startIdx, int length) {
        
        //return str.substring(0, startIdx) + color1 + color2 + str.substring(startIdx, startIdx + length) + ANSI_RESET + str.substring(startIdx + length, str.length());
        return null;
    }
    
    @Override
    public String toString() {
        return null;
    }
}
