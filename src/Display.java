
import java.util.ArrayList;
import org.joda.time.format.PeriodFormat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
TODO: 
resolve issues
add option for appending to beginning and end of original string (+=)
*/

/**
 *
 * @author Dmitry
 */
public class Display {

    public static void printTrees(ArrayList<Item> heads) {
        int count = 0;
        for (Item head: heads) {
            printTree(head, count ++, 0);
            System.out.println("");
        }
        if (Main.timeMode) {
            Main.printTaskTimes();
        }
        if (!heads.isEmpty()) {
            System.out.println();
        }
    }

    public static void printTree(Item head, int count, int offset) {
        if (count != 0 && offset != 0) {
            System.out.print(String.format("%" + offset + "s", ""));
        }
        
        ColoredString nameStr = new ColoredString(head.getName(), Main.getStatusColor(head.getStatus()));
        
        if (Main.searchResults.containsKey(head)) {
            nameStr.applyColor(ColoredString.PURPLE, Main.searchResults.get(head), Main.searchStr.length());
            
            nameStr = nameStr.prepend(new ColoredString("[ ", ColoredString.PURPLE, 0, 1));
            nameStr = nameStr.append(new ColoredString(" ]", ColoredString.PURPLE, 1, 1));
        }
        
        if (head == Main.getSelected() || head == Main.selected) {
            ColoredString star = new ColoredString("*");
            if (head == Main.secondarySelected) {
                star.applyColor(ColoredString.GREEN);
            } else if (head == Main.selected) {
                star.applyColor(ColoredString.RED);
            }
            nameStr = nameStr.prepend(star.append(" ")).append(star.prepend(" "));
        }
        
        if (Main.timeMode && head.getTime() != null) {
            ColoredString timeStr = new ColoredString(PeriodFormat.getDefault().print(head.getTime()), ColoredString.CYAN_BACKGROUND);
            nameStr = nameStr.append(" -- ").append(timeStr);
        }
        
        String pre = count + ". ";
        
        String post = "";
        if (!head.isLeaf()) {
            post = " --> ";
        }

        System.out.print(pre + nameStr + post);
        
        if (head.isLeaf()) {
            System.out.println();
        } else {
            count = 0;
            for (Item child: head.getChildren()) {
                printTree(child, count ++, offset + pre.length() + nameStr.length() + post.length());
            }
        }
    }
}
