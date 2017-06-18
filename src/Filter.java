
import java.util.HashMap;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class Filter {
    
    private String color;
    private boolean on;
    
    public Filter(String color, boolean on) {
        this.color = color;
        this.on = on;
    }
    
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    private static final HashMap<String, Filter> FILTERS = new HashMap();
    
    static {
        FILTERS.put("in progress", new Filter(ANSI_BLUE + "#@!" + ANSI_RESET, true));
        FILTERS.put("todo", new Filter(ANSI_GREEN + "#@!" + ANSI_RESET, true));
        FILTERS.put("done", new Filter(ANSI_YELLOW + "#@!" + ANSI_RESET, true));
        FILTERS.put("none", new Filter(ANSI_WHITE_BACKGROUND + ANSI_WHITE + "#@!" + ANSI_RESET, true));
    }
    
    public static String applyFilters(Item item, String target) {
        for (String name: FILTERS.keySet()) {
            if (!FILTERS.get(name).on) continue;
            if ((name.equals("in progress") && item.isWorking()) ||
                (name.equals("todo") && item.isTodo()) ||
                (name.equals("done") && item.isDone()) ||
                (name.equals("none") && item.isNone())) {
                target = FILTERS.get(name).color.replace("#@!", target);
            }
        }
        return target;
    }
    
    public static void printActiveFilters() {
        String[] activeFilters = FILTERS.keySet().stream().filter(p -> FILTERS.get(p).on).toArray(String[]::new);
        
        System.out.print("Active filters: [");
        for (int i = 0; i < activeFilters.length; ++ i) {
            System.out.print(FILTERS.get(activeFilters[i]).color.replace("#@!", activeFilters[i]));
            if (i < activeFilters.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
    public static void printInactiveFilters() {
        String[] inactiveFilters = FILTERS.keySet().stream().filter(p -> !FILTERS.get(p).on).toArray(String[]::new);
        
        System.out.print("Inactive filters: [");
        for (int i = 0; i < inactiveFilters.length; ++ i) {
            System.out.print(FILTERS.get(inactiveFilters[i]).color.replace("#@!", inactiveFilters[i]));
            if (i < inactiveFilters.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
    public static void printFilters() {
        printActiveFilters();
        printInactiveFilters();
        Main.waitForInput();
    }
    
    public static void filterPrompt() {
        String[] values = FILTERS.keySet().toArray(new String[FILTERS.size()]);

        Object response = JOptionPane.showInputDialog(null, "Category:", "Filter selection", JOptionPane.DEFAULT_OPTION, null, values, values[0]);
        String selectedString = "";
        if (response != null){//null if the user cancels. 
            selectedString = response.toString();
            
            FILTERS.get(selectedString).on = !FILTERS.get(selectedString).on;
        }
        
        printFilters();
    }
}
