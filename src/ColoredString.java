
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */

/* request: clearColors
    -- useCase: 
*/
public class ColoredString {
    
    public static final String BLACK_BACKGROUND = "\u001B[40m";
    public static final String RED_BACKGROUND = "\u001B[41m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String WHITE_BACKGROUND = "\u001B[47m";
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    private final String ORIGINAL;
    
    private HashMap<Integer, ArrayList<String>> startIndices;
    private HashMap<Integer, ArrayList<String>> endIndices;
    
    
    // start same: ignore until last, but push from beginning to end
    // end same: pop from beginning to end
    
    /**
     * @param original an uncolored string.
     */
    public ColoredString(String original) {
        this.ORIGINAL = original;
        this.startIndices = new HashMap();
        this.endIndices = new HashMap();
    }
    
    private ColoredString(String original, HashMap<Integer, ArrayList<String>> startIndices, HashMap<Integer, ArrayList<String>> endIndices) {
        this.ORIGINAL = original;
        this.startIndices = startIndices;
        this.endIndices = endIndices;
    }
    
    public ColoredString(String original, String color) {
        this(original);
        this.applyColor(color);
    }
    
    public ColoredString(String original, String color, int start, int end) {
        this(original);
        this.applyColor(color, start, end);
    }
    
    /*
    issues:
        if end index is larger than the string then don't need to store it
    */
    
    public void applyColor(String color, int start, int length) {
        if (!startIndices.containsKey(start)) {
            startIndices.put(start, new ArrayList());
        }
        startIndices.get(start).add(color);
        
        int end = start + length;
        if (end > this.ORIGINAL.length()) end = this.ORIGINAL.length();
        if (!endIndices.containsKey(end)) {
            endIndices.put(end, new ArrayList());
        }
        endIndices.get(end).add(color);
    }
    
    public void applyColor(String color) {
        this.applyColor(color, 0, this.ORIGINAL.length());
    }
    
    /* 
    issues: 
        if (0: [red, orange]) but orange is longer than red, then ending red is actually unecessary (but is done anyways)
            -- solution: make a local copy of startIndex, treat the array as a stack, and then perform the same deletion logic to this stack as
            -- to the real 'colorsInEffect' stack
        if the same color has two ending indices (overlap and no), then after the first is removed, need to check if colorsInEffect.isEmpty())
            -- solution: ???
        applying characters from the original string one at a time (inefficient)
            -- solution: only apply characters when ending/starting
    */
    @Override
    public String toString() {
        Stack<String> activeColors = new Stack();
        
        String coloredString = "";
        
        for (int i = 0; i < this.ORIGINAL.length(); i ++) {
            if (this.endIndices.containsKey(i) && !activeColors.isEmpty()) {
                // remove overlapping endpoints in order of applied color to avoid multiple ANSI_RESET
                for (String colorToEnd: this.endIndices.get(i)) {
                    if (!activeColors.isEmpty()) {
                        boolean removed = false;
                        if (activeColors.peek().equals(colorToEnd)) {
                            coloredString += ANSI_RESET;
                            activeColors.pop();
                            removed = true;
                            if (!activeColors.isEmpty()) {
                                coloredString += activeColors.peek();
                            }
                        }
                        if (!removed) {
                            for (String colorToRemove: activeColors) {
                                if (colorToRemove.equals(colorToEnd)) {
                                    activeColors.remove(colorToRemove);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            if (this.startIndices.containsKey(i)) {
                int j = 0;
                if (!activeColors.isEmpty()) {
                    coloredString += ANSI_RESET;
                }
                for (String colorToStart: this.startIndices.get(i)) {
                    activeColors.push(colorToStart);
                    if (j == this.startIndices.get(i).size() - 1) {
                        coloredString += colorToStart;
                    }
                    j ++;
                }
            }
            coloredString += this.ORIGINAL.charAt(i);
        }
        
        if (!activeColors.isEmpty()) {
            coloredString += ANSI_RESET;
        }
        
        return coloredString;
    }
    
    public ColoredString append(String str) {
        return this.append(new ColoredString(str));
    }
    public ColoredString prepend(String str) {
        return this.prepend(new ColoredString(str));
    }
    
    public ColoredString append(ColoredString other) {
        return other.prepend(this);
    }
    
    public ColoredString prepend(ColoredString other) {
        
        String newOriginal = other.ORIGINAL + this.ORIGINAL;
        
        HashMap<Integer, ArrayList<String>> newStartIndices = new HashMap(other.startIndices);
        for (Integer i: this.startIndices.keySet()) {
            newStartIndices.put(i + other.length(), this.startIndices.get(i));
        }
        
        HashMap<Integer, ArrayList<String>> newEndIndices = new HashMap(other.endIndices);
        for (Integer i: this.endIndices.keySet()) {
            newEndIndices.put(i + other.length(), this.endIndices.get(i));
        }
        
        return new ColoredString(newOriginal, newStartIndices, newEndIndices);
    }
    
    public int length() {
        return this.ORIGINAL.length();
    }
}
