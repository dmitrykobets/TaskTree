
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class Main {
    
    final static String DATA_PATH = "./data-test/";
    final static String CONFIG_PATH = "./config/";
    final static String META_PATH = "_meta/";
    
    static ArrayList<Item> heads = new ArrayList();
    static Item selected = null;
    static Item secondarySelected = null;
    static boolean secondarySelection = false;
    static boolean isPriority = false;
    static boolean timeMode = false;
    static int originalPriorityIndex = 0;
    static int numRunning = 0;
    
    static String searchStr = "";
    static HashMap<Item, Integer> searchResults = new HashMap();
    
    static int currentWeek = 0;
    static int viewWeek = 0;
    
    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }
    
    public void runO() {
        ColoredString str = new ColoredString("01234");
        str.applyColor(ColoredString.RED, 0, 4);
        str.applyColor(ColoredString.BLUE, 0, 2);
        System.out.println(str);
    }
    
    public void run() {
        loadConfig();
        loadFiles(currentWeek);
        printWeek(false);
        
        Scanner in = new Scanner(System.in);
        do {
            System.out.println();
            Display.printTrees(heads);
            System.out.print(">> ");
            Integer target = null;
            
            String line = in.nextLine().trim();
            System.out.println(ColoredString.RED + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + ColoredString.ANSI_RESET);
            if (line.isEmpty()) continue;
            String[] toks = line.toLowerCase().split("\\s+");
            
            if (toks[0].startsWith("/")) {
                if (toks[0].startsWith("//")) {
                    if (line.length() > 2) {
                        search(line.substring(2), false);
                    } else {
                        searchResults.clear();
                        searchStr = "";
                    }
                } else if (line.length() > 1) {
                    search(line.substring(1), true);
                } else {
                    searchResults.clear();
                    searchStr = "";
                }
            }
            
            if (toks.length == 1) {
                if (toks[0].equals("q") || toks[0].equals("quit") || toks[0].equals("exit")) {
                    if (secondarySelection) {
                        secondarySelection = false;
                        secondarySelected = null;
                    } else {
                        break;    
                    }
                }
                else if ((target = parseInt(toks[0])) != null){
                    if (isPriority) {
                        selectPriority(target);
                    } else {
                        select(target);
                        bringBranchToBottom(selected);
                    }
                } else if (toks[0].equals("new") && !secondarySelection) {
                    makeNew(in);
                } else if (toks[0].equals("clear") || toks[0].equals("clr")) {
                    setSelected(null);
                } else if (toks[0].equals("l")) {
                    navigate('l', false);
                } else if (toks[0].equals("h")) {
                    navigate('h', false);
                } else if (toks[0].equals("k")) {
                    navigate('k', false);
                } else if (toks[0].equals("j")) {
                    navigate('j', false);
                } else if ((toks[0].equals("edit") || toks[0].equals("notes") || toks[0].equals("note")) && !secondarySelection) {
                    editSelected();
                } else if ((toks[0].equals("status") || toks[0].equals("s") || toks[0].equals("stat")) && !secondarySelection) {
                    changeStatusPrompt();
                } else if ((target = parseInt(toks[0].substring(1))) != null) {
                    if (toks[0].charAt(0) == 'h') {
                        selectUp(target);
                    } else if (toks[0].charAt(0) == 'l') {
                        selectDown(target);
                    }
                } else if (toks[0].equals("rm") && !secondarySelection) {
                    deletePrompt();
                } else if (toks[0].equals("pre") && !secondarySelection) {
                    makeNewPre(in);
                } else if (toks[0].equals("vw") && !secondarySelection) {
                    Main.this.changeViewWeek(in);
                } else if (toks[0].equals("week") || toks[0].equals("weeks")) {
                    printWeek(true);
                } else if (toks[0].equals("rn") && !secondarySelection) {
                    renamePrompt();
                } else if (toks[0].equals("e")) {
                    selectEnd();
                } else if (toks[0].equals("b")) {
                    selectTop();
                } else if (toks[0].matches("^[hjkl0-9]+$")) {
                    parseComboMovement(toks[0].toCharArray());
                } else if (toks[0].equals("mv") || toks[0].equals("move")) {
                    move();
                } else if ((toks[0].equals("open") || toks[0].equals("folder")) && !secondarySelection) {
                    openFolder();
                } else if ((toks[0].equals("sort")) && !secondarySelection) {
                    sort(null);
                } else if (toks[0].equals("rr") || toks[0].equals("rearrange") || toks[0].equals("p") || toks[0].equals("priority")) {
                    togglePriorityMode();
                } else if (toks[0].equals("time")) {
                    toggleTimeMode();
                }
            } else if (toks.length == 2) {
                if ((toks[0].equals("vw")) && !secondarySelection) {
                    Main.this.changeViewWeek(toks[1]);
                } else if (toks[0].equals("start") && !secondarySelection) {
                    if (toks[1].equals("new") || toks[1].equals("week")) {
                        startNewWeek();
                    }
                } else if (toks[0].equals("print") && !secondarySelection) {
                    printList(toks[1]);
                }
            }
            
            if (toks.length != 1) {
                if (toks[0].equals("rn") && !secondarySelection) {
                    rename(line.replaceFirst(toks[0], "").trim());
                } else if (toks[0].equals("new") && !secondarySelection) {
                    makeNew(line.replaceFirst(toks[0], "").trim());
                } else if (toks[0].equals("pre") && !secondarySelection) {
                    makeNewPre(line.replaceFirst(toks[0], "").trim());
                }
            }
        } while (true);
    }
    
    public void toggleTimeMode() {
        if (secondarySelection) return;
        selected = null;
        timeMode = !timeMode;
        if (timeMode) {
            parseTimes();
        }
    }
    public void parseTimes() {
        for (Item head: heads) {
            parseTime(head);
        }
    }
    public void parseTime(Item item) {
        if (item.isLeaf()) {
            File timeFile = new File(item.getPath(getWeekPrepath(currentWeek)) + META_PATH + "time.txt");
            if (!timeFile.exists()) {
                item.setTime(null);
            } else {
                try (BufferedReader br = new BufferedReader(new FileReader(timeFile))) {
                    String line;
                    String startStr = "";
                    Period duration = null;
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("s:")) {
                            startStr = line.substring(2);
                        } else {
                            DateTime start = formatter.parseDateTime(startStr);
                            DateTime end = formatter.parseDateTime(line.substring(2));
                            if (duration == null) duration = new Period(start, end);
                            else duration = duration.plus(new Period(start, end));
                        }
                    }
                    item.setTime(duration);
                } catch (Exception e) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        } else {
            for (Item child: item.getChildren()) {
                parseTime(child);
            }
        }
    }
    
    public void togglePriorityMode() {
        if (selected == null || secondarySelection || currentWeek != viewWeek) return;
        if (this.isPriority) {
            if (originalPriorityIndex != getCurIdx()) {
                recalculatePriorities();
                writePriorities();
            }
        } else {
            sort(selected.getParent());
            originalPriorityIndex = getCurIdx();
        }
        this.isPriority = !this.isPriority;
    }
    
    public void writePriorities() {
        for (Item item: selected.isHead() ? heads : selected.getParent().getChildren()) {
            writeMeta(item.getPath(getWeekPrepath(currentWeek)), item.getMeta(), false);
        }
    }
    
    public void recalculatePriorities() {
        ArrayList<Item> container = selected.isHead() ? heads : selected.getParent().getChildren();
        int curIdx = getCurIdx();
        boolean inGroup = false;
        while (!inGroup) {
            Meta.Status upStatus = curIdx == container.size() - 1 ? null : container.get(curIdx + 1).getStatus();
            if (upStatus != selected.getStatus()) {
                Meta.Status downStatus = curIdx == 0 ? null : container.get(curIdx - 1).getStatus();
                if (downStatus != selected.getStatus()) {
                    int downDir = downStatus == null ? 1 : selected.getStatus().ordinal() - downStatus.ordinal();
                    int upDir = upStatus == null ? -1 : selected.getStatus().ordinal() - upStatus.ordinal();
                    if (downDir != upDir) inGroup = true;
                    else {
                        cycle(container, curIdx, upDir);
                    }
                } else {
                    inGroup = true;
                }
            } else {
                inGroup = true;
            }
        }
        
        int adjust = 0;
        if (curIdx == 0) {
            if (container.get(curIdx + 1).getPriority() == 0) {
                selected.setPriority(0);
                adjust = 1;
            } else {
                selected.setPriority(container.get(curIdx + 1).getPriority() - 1);
            }
        } else {
            selected.setPriority(container.get(curIdx - 1).getPriority() + 1);
            adjust = 2;
        }
        if (adjust == 0) return;
        
        boolean recalibrate = selected.getPriority() >= 1000;
        for (int i = curIdx + 1; i < container.size(); ++ i) {
            container.get(i).setPriority(container.get(i).getPriority() + 2);
            if (container.get(i).getPriority() >= 1000) recalibrate = true;
        }
        
        if (recalibrate) {
            for (int i = 1; i < container.size(); ++ i) {
                int diff = container.get(i).getPriority() - container.get(i - 1).getPriority();
                if (diff > 1) {
                    -- diff;
                    for (int j = i; j < container.size(); ++ j) {
                        container.get(j).setPriority(container.get(j).getPriority() - diff);
                    }
                }
            }
        }
    }
    
    public <T> void cycle(ArrayList<T> container, int i, int dir) {
        T t = container.get(i);
        if (dir == 1 && i == container.size() - 1) {
            container.remove(i);
            container.add(0, t);
        } else if (dir == -1 && i == 0) {
            container.remove(i);
            container.add(t);
        } else {
            Collections.swap(container, i, i + dir);
        }
    }
    
    public void search(String str, boolean all) {
        searchResults.clear();
        searchStr = str.toLowerCase();
        
        for (int i = heads.size() - 1; i >= 0; -- i) {
            if ((all && searchRecur(heads.get(i), str)) || (!all && matchesSearch(heads.get(i).getName(), str))) {
                if (!all) {
                    addToSearchResults(heads.get(i), str);
                }
                if (i != heads.size() - 1) {
                    if (selected == null) {
                        moveToLast(heads, i);
                    } else {
                        moveToSecondLast(heads, i);
                    }
                }
            }
        }
        
        if (searchResults.isEmpty()) {
            System.out.println(ColoredString.RED + "No search results." + ColoredString.ANSI_RESET);
            waitForInput();
        } else {
            setSelected(null);
        }
    }
    
    public <T> void moveToSecondLast(ArrayList<T> container, T item) {
        container.add(container.size() - 1, item);
        container.remove(item);
    }
    public <T> void moveToSecondLast(ArrayList<T> container, int i) {
        container.add(container.size() - 1, container.get(i));
        container.remove(i);
    }
    public <T> void moveToLast(ArrayList<T> container, T item) {
        container.remove(item);
        container.add(item);
    }
    public <T> void moveToLast(ArrayList<T> container, int i) {
        container.add(container.get(i));
        container.remove(i);
    }
    
    public boolean searchRecur(Item item, String str) {
        boolean found = false;
        if (matchesSearch(item.getName(), str)) {
            addToSearchResults(item, str);
            found = true;
        }
        for (Item child: item.getChildren()) {
            found |= searchRecur(child, str);
        }
        return found;
    }
    
    public void addToSearchResults(Item item, String match) {
        int start = item.getName().toLowerCase().indexOf(match.toLowerCase());
        searchResults.put(item, start);
    }
    
    public boolean matchesSearch(String str, String search) {
        return str.toLowerCase().contains(search.toLowerCase());
    }
    
    public void printList(String filter) {
        System.out.println(ColoredString.RED + "Items matching '" + filter + "':" + ColoredString.ANSI_RESET);
        for (Item item: (getSelected() == null ? heads : getSelected().getChildren())) {
            printListRecur(item, Meta.getStatusFromString(filter), item.getName());
        }
        waitForInput();
    }
    public void printListRecur(Item item, Meta.Status status, String head) {
        if (item.isLeaf() && item.getStatus() == status) {
            System.out.println(head + " ... " + item.getName());
        } else {
            for (Item child: item.getChildren()) {
                printListRecur(child, status, head);
            }
        }
    }
    
    public void parseComboMovement(char[] chars) {
        String num = "";
        Item toBringToBottom = null;
        for (int i = 0; i < chars.length; i ++) {
            if (Character.isDigit(chars[i])) {
                num += chars[i];
            } else {
                int n = num.isEmpty() ? 1 : Integer.parseInt(num);
                for (int j = 0; j < n; j ++) {
                    navigate(chars[i], true);
                }
                num = "";
                
                if (chars[i] == 'k' || chars[0] == 'j') {
                    toBringToBottom = getSelected();
                }
            }
        }
        if (toBringToBottom != null) {
            bringBranchToBottom(toBringToBottom);
        }
    }
    
    public void openFolder() {
        if (selected == null) return;
        
        try {
            Desktop.getDesktop().open(new File(getPrimarySelectedPath()));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean move(Item source, Item target) {
        if (source == target) return false;
        if (target != null) {
            if (target.getChildren().stream().filter(i -> i.getName().equals(source.getName())).count() != 0) return false;

            if (target.isDescendantOf(source)) {
                if (source.isHead()) {
                    if (heads.stream().filter(i -> i.getName().equals(target.getName())).count() != 0) return false;
                } else {
                    if (source.getParent().getChildren().stream().filter(i -> i.getName().equals(target.getName())).count() != 0) return false;
                }
                if (!move(target, source.getParent())) return false;
            }
        }
        
        // move folders
        File sourceFolder = new File(source.getPath(getWeekPrepath(currentWeek)));
        File targetFolder;
        if (target == null) {
            targetFolder = new File(getWeekPrepath(currentWeek) + source.getName() + "/");
        } else {
            targetFolder = new File(target.getPath(getWeekPrepath(currentWeek)) + source.getName() + "/");
        }

        try {
            Files.move(sourceFolder.toPath(), targetFolder.toPath());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        // unlink
        if (source.isHead()) {
            heads.remove(source);
        } else {
            source.getParent().getChildren().remove(source);
            updateStatusUp(source);
            source.setParent(null);
        }
        //link
        if (target == null) {
            heads.add(source);
        } else {
            target.getChildren().add(source);
            source.setParent(target);
            updateStatusUp(source);
        }
        
        return true;
    }
    
    public void move() {
        if (selected == null || viewWeek != currentWeek) return;
        
        if (!secondarySelection) {
            secondarySelection = true;
            setSelected(selected);
        } else {
            if (move(selected, secondarySelected)) {
                secondarySelection = false;
                secondarySelected = null;
            }
        }
    }
    
    public void setSelected(Item item) {
        if (secondarySelection) {
            secondarySelected = item;
        } else {
            selected = item;
        }
    }
    
    public void bringBranchToBottom(Item item) {
        if (item == null) return;
        Item head = item;
        while (!head.isHead()) head = head.getParent();
        moveToLast(heads, head);
    }
    
    public void navigatePriority(char c) {
        if (selected == null || secondarySelection || currentWeek != viewWeek) return;

        ArrayList<Item> collection = selected.isHead() ? heads : selected.getParent().getChildren();
        int curIdx = getCurIdx();
        int movement = 0;
        if (c == 'k') {
            movement = -1;
        } else {
            movement = 1;
        }
        
        cycle(collection, curIdx, movement);
    }
    
    public void navigate(char c, boolean combo) {
        if (this.isPriority) {
            if (c == 'k' || c == 'j') {
                navigatePriority(c);
            }
            return;
        }
        switch (c) {
            case 'j':
                if (getSelected() == null) {
                    select(0);
                } else {
                    select(getCurIdx() + 1);
                }
                if (!combo)
                    bringBranchToBottom(selected);
                break;
            case 'k':
                if (getSelected() == null) {
                    select(0);
                } else {
                    select(getCurIdx() - 1);
                }
                if (!combo)
                    bringBranchToBottom(selected);
                break;
            case 'l':
                if (getSelected() == null) {
                    select(0);
                } else {
                    selectDown(0);
                }
                break;
            case 'h':
                selectParent();
                break;
        }
    }
    
    public void selectEnd() {
        do {
            selectDown(0);
        } while (getSelected() != null && !getSelected().isLeaf());
    }
    public void selectTop() {
        do {
            selectParent();
        } while (getSelected() != null && !getSelected().isHead());
    }
    
    public void renamePrompt() {
        if (selected == null || viewWeek != currentWeek) return;
        
        System.out.print("New name\n>> ");
        Scanner in = new Scanner(System.in);
        rename(in.nextLine());
    }
    public void rename(String newName) {
        File curFolder = new File(getPrimarySelectedPath());
        try {
            Files.move(curFolder.toPath(), curFolder.toPath().resolveSibling(newName));
            selected.setName(newName);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getPathRelativeToWeek(File file, String weekPath) {
        return weekPath + file.getPath().substring(file.getPath().indexOf('\\', getWeekPrepath(0).length() - 1));
    }
    
    public void recurseCopy(File file, String newPrePath) {
        try {
            File target = new File(getPathRelativeToWeek(file, newPrePath));
            Files.copy(file.toPath(), target.toPath());
            if (!file.isFile()) {
                for (File subFile: file.listFiles()) {
                    recurseCopy(subFile, newPrePath);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    public void printWeek(boolean waitForInput) {
        System.out.println("Current week: " + currentWeek);
        System.out.println("Viewing week: " + viewWeek);
        if (waitForInput) {
            waitForInput();    
        }
    }
    
    public static void waitForInput() {
        System.out.println(ColoredString.RED + "Type anything to continue" + ColoredString.ANSI_RESET);
        Scanner in = new Scanner(System.in);
        in.nextLine();
    }
    
    public void startNewWeek() {
        if (viewWeek != currentWeek) return;
        
        File newWeek = new File(getWeekPrepath(currentWeek + 1));
        newWeek.mkdir();
        
        File curWeek = new File(getWeekPrepath(currentWeek));
        for (File file: curWeek.listFiles()) {
            recurseCopy(file, newWeek.getPath() + "/");
        }
        
        currentWeek ++;
        viewWeek ++;
        writeConfig(false);
        printWeek(true);
    }
    
    public static String getWeekPrepath(int weekNum) {
        return DATA_PATH + "week" + weekNum + "/";
    }
    
    public void changeViewWeek(Scanner in) {
        Integer parseWeek = null;
        while (parseWeek == null) {
            System.out.print("Week to view\n>> ");
            parseWeek = parseInt(in.nextLine());
        }
        changeViewWeek(parseWeek);
    }
    public void changeViewWeek(String str) {
        Integer weekInt = parseInt(str);
        if (weekInt != null) changeViewWeek(weekInt);
    }
    public void changeViewWeek(int num) {
        viewWeek = num;
        loadFiles(viewWeek);
        printWeek(true);
    }
    
    public void deletePrompt() {
        if (selected == null || viewWeek != currentWeek) return;
        
        int response = JOptionPane.showConfirmDialog(null, "Delete selected item?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION){//null if the user cancels. 
            deleteItem(selected);
        }
    }
    
    public void deleteDown(Item item) {
        for (int i = item.getChildren().size() - 1; i >= 0; -- i) {
            deleteDown(item.getChildren().get(i));
        }
        
        if (item.isHead()) {
            heads.remove(item);
        } else {
            item.getParent().getChildren().remove(item);
        }
        
        deleteLeafFolder(item.getPath(getWeekPrepath(currentWeek)));
    }
    
    /*
        Deletes any item (not just selected)
    */
    public void deleteItem(Item item) {
        
        // select new item
        if (item.isHead()) {
            if (heads.size() == 1) {
                setSelected(null);
            } else {
                if (getCurIdx() == heads.size() - 1) {
                    select(getCurIdx() - 1);
                } else {
                    select(getCurIdx() + 1);
                }
            }
        } else {
            if (item.getParent().getChildren().size() == 1) {
                selectParent();
            } else {
                if (getCurIdx() == selected.getParent().getChildren().size() - 1) {
                    select(getCurIdx() - 1);
                } else {
                    select(getCurIdx() + 1);
                }
            }
        }
        
        item.setStatus(Meta.Status.NONE);
        updateStatusUp(item);
        deleteDown(item);
    }
    
    public void deleteLeafFolder(String path) {
        File folder = new File(path);
        for (File file: folder.listFiles()) {
            if (!file.isFile()) deleteLeafFolder(file.getPath());
            else file.delete();
        }
        folder.delete();
    }
    
    public void editSelected() {
        if (selected == null) return;
        
        File notes = new File(getPrimarySelectedPath() + "/" + META_PATH + "notes.txt");
        
        if (viewWeek != currentWeek) {
            System.out.println(ColoredString.RED + "Viewing archived notes: " + ColoredString.ANSI_RESET);
        }
        
        if (!notes.exists()) {
            if (viewWeek != currentWeek) {
                System.out.println(ColoredString.RED + "Non-existent archive" + ColoredString.ANSI_RESET);
                return;
            }
            try {
                notes.createNewFile();
            } catch (IOException ex) {
                System.out.println("Error creating file: notes.txt for item: " + getPrimarySelectedPath());
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (viewWeek != currentWeek) {
            
            try (BufferedReader br = new BufferedReader(new FileReader(notes))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                System.out.println("Error reading file: " + notes.getPath());
            }
            System.out.println(ColoredString.RED + "//////////// END ///////////////" + ColoredString.ANSI_RESET);
        } else {
            try {
                String cmd = "cmd.exe /c start \"\" \"" + notes.getAbsolutePath() + "\"";
                Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void makeNew(String name) {
        if (viewWeek  != currentWeek) return;
        
        // avoid duplicating a child by name
        if (selected != null) {
            for (Item child: selected.getChildren()) {
                if (child.getName().equals(name)) {
                    return;
                }
            }
        } else {
            for (Item head: heads) {
                if (head.getName().equals(name)) {
                    return;
                }
            }
        }
        
        // make the folder
        String newPath = getPrimarySelectedPath() + name + "/";
        File newFolder = new File(newPath);
        newFolder.mkdir();
        
        // add meta file
        writeMeta(newPath, null, true);
        
        Item newItem = parseItem(newFolder, currentWeek);
        if (selected != null) {
            if (selected.isDone() && selected.isLeaf()) {
                newItem.setStatus(Meta.Status.DONE);
                writeMeta(newPath, newItem.getMeta(), false);
            } else if (selected.isWorking() && selected.isLeaf()) {
                newItem.setStatus(Meta.Status.WORKING);
                writeMeta(newPath, newItem.getMeta(), false);
            } else if (selected.isTodo() && selected.isLeaf()) {
                newItem.setStatus(Meta.Status.TODO);
                writeMeta(newPath, newItem.getMeta(), false);
            }

            selected.getChildren().add(newItem); 
            newItem.setParent(selected);
        } else {
            heads.add(newItem);
        }
    }
    
    public void makeNewPre(Scanner in) {
        if (viewWeek  != currentWeek) return;
        
        System.out.print("Name\n>> ");
        makeNewPre(in.nextLine());
    }
    
    public void makeNewPre(String name) {
        if (selected == null || viewWeek  != currentWeek) return;

        String newPath;
        if (selected.isHead()) {
            newPath = getWeekPrepath(currentWeek) + name + "/";
        } else {
            newPath = selected.getParent().getPath(getWeekPrepath(currentWeek)) + name + "/";
        }
        
        boolean merge = false;
        File newFolder = new File(newPath);
        if (!newFolder.exists()) {
            newFolder.mkdir();
        } else {
            merge = true;
        }
        
        // add meta file
        writeMeta(newPath, null, true);
        
        // move selected item and its contents into new folder (since selected is now a child)
        File selectedNewDestFolder = new File(newPath + selected.getName() + "/");
        File selectedFolder = new File(getPrimarySelectedPath());
        try {
            Files.move(selectedFolder.toPath(), selectedNewDestFolder.toPath());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (merge) {
            Item target;
            
            if (!selected.isHead()) {
                target = selected.getParent().getChildren().stream().filter(i -> i.getName().equals(name)).findFirst().get();
                selected.getParent().getChildren().remove(selected);
            } else {
                target = heads.stream().filter(i -> i.getName().equals(name)).findFirst().get();
                heads.remove(selected);
            }
            selected.setParent(target);
            target.getChildren().add(selected);
            
            updateStatusUp(selected);
        } else {
            Item newItem = parseItem(newFolder, currentWeek);
        
            newItem.setStatus(selected.getStatus());
            writeMeta(newPath, newItem.getMeta(), false);
            
            // link
            if (!selected.isHead()) {
                selected.getParent().getChildren().add(newItem);
                newItem.setParent(selected.getParent());
                selected.getParent().getChildren().remove(selected);
            } else {
                heads.add(newItem);
                heads.remove(selected);
            }
            selected.setParent(newItem);
            
            setSelected(newItem.getChildren().stream().filter(c -> c.getName().equals(selected.getName())).findFirst().get());
        }
    }
    
    
    public void makeNew(Scanner in) {
        if (viewWeek != currentWeek) return;
        System.out.print("Name\n>> ");
        makeNew(in.nextLine());
    }
    
    public void selectDown(int idx) {
        if (idx < 0) return;
        
        // don't want to select past the end, since navigating back will be a pain
        //  -- this can be solved with navigation history
        if (getSelected() != null && !getSelected().isLeaf()) {
            if (idx < getSelected().getChildren().size()) {
                setSelected(getSelected().getChildren().get(idx));
            }
        }
    }
    
    public void selectParent() {
        if (getSelected() == null || getSelected().isHead()) {
            setSelected(null);
        } else {
            setSelected(getSelected().getParent());
        }
    }
    
    public void selectUp(int idx) {
        if (idx < 0) return;

        if (getSelected() == null || getSelected().isHead()) {
            setSelected(null);
        } else {
            Item oldSelected = getSelected();
            selectParent();
            if (!select(idx)) {
                setSelected(oldSelected);
            }
        }
    }
    
    public void selectPriority(int idx) {
        if (selected == null || secondarySelection || currentWeek != viewWeek) return;
        
        ArrayList<Item> collection = selected.isHead() ? heads : selected.getParent().getChildren();
        int curIdx = getCurIdx();
        collection.remove(curIdx);
        collection.add(idx, selected);
    }
    
    public boolean select(int idx) {
        if (idx < 0) return false;
        
        boolean success = false;
        
        if (getSelected() == null || getSelected().isHead()) {
            if (idx < heads.size()) {
                setSelected(heads.get(idx));
                success = true;
            }
        } else {
            if (idx < getSelected().getParent().getChildren().size()) {
                setSelected(getSelected().getParent().getChildren().get(idx));
                success = true;
            }
        }
        
        return success;
    }
    
    public Integer parseInt(String str) {
        try {
            Integer i = Integer.parseInt(str);
            return i;
        } catch (Exception e) {
            return null;
        }
    }
    
    public Meta loadMeta(File metaFolder) {
        Meta meta = new Meta();

        File metaFile = new File(metaFolder.getPath() + "/week.txt");
        if (!metaFile.exists()) {
            meta.setDefaults();
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(metaFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.toLowerCase();
                    String[] parts = line.split("\\s*:\\s*");
                    String key = parts[0].trim();
                    if (key.equals("status")) {
                        meta.status = Meta.getStatusFromString(parts[1]);
                    } else if (key.equals("priority")) {
                        Integer priorityNum = parseInt(parts[1]);
                        if (priorityNum == null || priorityNum < 0) {
                            System.out.println("Invalid value for attribute: 'priority' in file: " + metaFile.getPath());
                        } else {
                            meta.priority = priorityNum;
                        }
                    }
                }
                if (meta.status == null) {
                    System.out.println("Missing attribute: 'status' in file: " + metaFile.getPath());
                    meta.status = Meta.DEFAULT_STATUS;
                }
            } catch (Exception e) {
                System.out.println("Error reading file: " + metaFile.getPath());
            }
        }
        
        return meta;
    }
    
    public void writeMeta(String path, Meta meta, boolean suppressWarning) {
        try {
            File metaFolder = new File(path + META_PATH);
            if (!metaFolder.exists()) {
                if (!suppressWarning) {
                    System.out.println("Cannot find folder: " + META_PATH + " for item path: " + path);
                }
                metaFolder.mkdir();
            }
            File metaFile = new File(path + META_PATH + "week.txt");
            if (!metaFile.exists()) {
                if (!suppressWarning) {
                    System.out.println("Cannot find file: week.txt, for item path: " + path);
                }
                try {
                    metaFile.createNewFile();
                } catch (IOException ex) {
                    System.out.println("Error creating file: week.txt for item path: " + path);
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            PrintWriter writer = new PrintWriter(metaFile);
            String line = "status: " + (meta == null ? Meta.DEFAULT_STATUS : meta.status);
            writer.println(line.toLowerCase());
            if (meta != null) {
                line = "priority: " + meta.priority;
                writer.println(line);
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadFiles(int week) {
        heads.clear();
        setSelected(null);
        
        File folder = new File(getWeekPrepath(viewWeek));
        File[] contents = folder.listFiles();
        
        for (File content: contents) {
            if (!content.isFile()) {
                Item newItem = parseItem(content, week);
                if (newItem == null) {
                    return;
                } else {
                    heads.add(newItem);
                }
            }
        }
        sort(null);
    }
    
    public Item parseItem(File folder, int week) {
        File[] contents = folder.listFiles();
        
        String name = folder.getName();
        ArrayList<Item> children = new ArrayList();
        Meta meta = null;
        
        for (File content: contents) {
            if (!content.isFile() && content.getName().equals(META_PATH.substring(0, META_PATH.length() - 1))) {
                meta = loadMeta(content);
            } else if (!content.isFile()) {
                Item child = parseItem(content, week);
                if (child != null) {
                    children.add(child);
                }
            }
        }
        if (meta == null) {
            System.out.println("Error reading meta for item: " + folder.getPath());
        }
        
        Item newItem = new Item(name, children, meta);
        for (Item child: newItem.getChildren()) {
            child.setParent(newItem);
        }
        return newItem;
    }
    
    public void changeStatusPrompt() {
        if (selected == null || viewWeek != currentWeek) return;
        
        String[] values = {"in progress", "done", "todo", "none"};
        String defaultSel;
        if (selected.isNone()) defaultSel = "todo";
        else if (selected.isTodo()) defaultSel = "in progress";
        else if (selected.isWorking()) defaultSel = "done";
        else defaultSel = "none";

        Object response = JOptionPane.showInputDialog(null, "Work status:", "Selection", JOptionPane.DEFAULT_OPTION, null, values, defaultSel);
        String selectedString = "";
        if (response != null){//null if the user cancels. 
            selectedString = response.toString();
        }
        
        if (!selectedString.isEmpty()) {
            updateStatus(Meta.getStatusFromString(selectedString), selected);
        }
    }
    
    public void writeTimer(Item item, boolean start) {
        if (start) numRunning ++;
        else numRunning --;
        parseTime(item);
        File timeFile = new File(item.getPath(getWeekPrepath(currentWeek)) + META_PATH + "time.txt");
        if (!timeFile.exists()) {
            try {
                timeFile.createNewFile();
            } catch (IOException ex) {
                System.out.println("Could not create time file: " + timeFile.getPath());
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(timeFile))) {
            ArrayList<String> lines = new ArrayList();
            String line = "";
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
            
            DateTime now = formatter.parseDateTime(new DateTime().toString(formatter));
            PrintWriter writer = null;
            if (start && (lines.isEmpty() || lines.get(lines.size() - 1).startsWith("e:"))) {
                writer = new PrintWriter(new FileOutputStream(timeFile, true));
                writer.println(("s:") + formatter.print(now));
            } else if (!start && !lines.isEmpty() && lines.get(lines.size() - 1).startsWith("s:")) {
                DateTime lastTime = formatter.parseDateTime(lines.get(lines.size() - 1).substring(2));
                if (lastTime.isEqual(now)) {
                    writer = new PrintWriter(new FileWriter(timeFile));
                    lines.remove(lines.size() - 1);
                    for (String str: lines) {
                        writer.println(str);
                    }
                } else {
                    writer = new PrintWriter(new FileOutputStream(timeFile, true));
                    writer.println(("e:") + formatter.print(now));
                }
            }
            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
            System.out.println("Error writing start time");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void updateStatus(Meta.Status status, Item item) {
        if (status == Meta.Status.WORKING && !item.isWorking()) {
            writeTimer(item, true);
        } else if (status != Meta.Status.WORKING && item.isWorking()) {
            writeTimer(item, false);
        }
        updateStatusDown(status, item);
        updateStatusUp(item);
    }
    public void updateStatusDown(Meta.Status status, Item item) {
        if (status == Meta.Status.WORKING && !item.isWorking()) {
            writeTimer(item, true);
        } else if (status != Meta.Status.WORKING && item.isWorking()) {
            writeTimer(item, false);
        }
        item.setStatus(status);
        writeMeta(item.getPath(getWeekPrepath(currentWeek)), item.getMeta(), false);
        for (Item child: item.getChildren()) {
            updateStatusDown(status, child);
        }
    }
    public void updateStatusUp(Item item) {
        if (item.isHead()) return;
        
        boolean working = false;
        boolean done = false;
        boolean todo = false;
        
        for (Item child: item.getParent().getChildren()) {
            working |= child.isWorking();
            done |= child.isDone();
            todo |= child.isTodo();
            if (working) break;
        }
        
        Meta.Status originalStat = item.getParent().getStatus();
        
        if (working) {
            item.getParent().setStatus(Meta.Status.WORKING);
        } else if (todo) {
            item.getParent().setStatus(Meta.Status.TODO);
        } else if (done) {
            item.getParent().setStatus(Meta.Status.DONE);
        } else {
            item.getParent().setStatus(Meta.Status.NONE);
        }
        
        if (item.getParent().getStatus() != originalStat) {
            writeTimer(item.getParent(), item.getParent().getStatus() == Meta.Status.WORKING);
            writeMeta(item.getParent().getPath(getWeekPrepath(currentWeek)), item.getParent().getMeta(), false);
            updateStatusUp(item.getParent());
        }
    }
    
    public String getPrimarySelectedPath() {
        if (selected == null) return getWeekPrepath(viewWeek);
        return selected.getPath(getWeekPrepath(viewWeek));
    }
    
    public static Item getSelected() {
        if (secondarySelection) return secondarySelected;
        return selected;
    }
    
    public Integer getCurIdx() {
        if (getSelected() == null) return null;
        if (getSelected().isHead()) {
            return heads.indexOf(getSelected());
        } else {
            return getSelected().getParent().getChildren().indexOf(getSelected());
        }
    }
    
    public void loadConfig() {
        File weekFile = new File(CONFIG_PATH + "week.txt");
        if (!weekFile.exists()) {
            try {
                System.out.println("Creating file: week");
                weekFile.createNewFile();
                writeConfig(true);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Integer weekInteger = null;
        boolean weekIntegerRead = false;
        try (BufferedReader br = new BufferedReader(new FileReader(weekFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                String[] parts = line.split("\\s*:\\s*");
                String key = parts[0].trim();
                if (key.equals("week")) {
                    weekIntegerRead = true;
                    weekInteger = parseInt(parts[1]);
                }
            }
            if (!weekIntegerRead || weekInteger == null || weekInteger < 0) {
                if (!weekIntegerRead) {
                    System.out.println("Missing attribute: 'week' in file: " + weekFile.getPath());
                } else if (weekInteger == null) {
                    System.out.println("Week not a number in file: " + weekFile.getPath());
                } else {
                    System.out.println("Invalid week: " + weekInteger + ", in file: " + weekFile.getPath());
                }
                this.currentWeek = 0;
            } else {
                this.currentWeek = weekInteger;
            }
            this.viewWeek = this.currentWeek;
        } catch (Exception e) {
            System.out.println("Error reading file: " + weekFile.getPath());
        }
    }
    
    public void writeConfig(boolean suppressWarning) {
        try {
            File weekFile = new File(CONFIG_PATH + "week.txt");
            if (!weekFile.exists()) {
                if (!suppressWarning) {
                    System.out.println("Cannot find file: week.txt, for item path: " + weekFile.getPath());
                }
                try {
                    weekFile.createNewFile();
                } catch (IOException ex) {
                    System.out.println("Error creating file: week.txt for item path: " + weekFile.getPath());
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            PrintWriter writer = new PrintWriter(weekFile);
            String line = "week: " + currentWeek;
            writer.println(line.toLowerCase());
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sort(Item head) {
        if (head != null && head.isLeaf()) return;
        ArrayList<Item> sorted = new ArrayList();
        
        ArrayList<Item> none = new ArrayList();
        ArrayList<Item> done = new ArrayList();
        ArrayList<Item> todo = new ArrayList();
        ArrayList<Item> working = new ArrayList();
        
        Item selectedHead = null;
        // Order: none, done, todo, working
        for (Item item: (head == null ? heads : head.getChildren())) {
            sort(item);
            // if this is the last item in 'heads' and a selection is made, then the selection is part of this tree
            if (head == null && item == heads.get(heads.size() - 1) && selected != null) {
                selectedHead = item;
                break;
            }
            switch(item.getStatus()) {
                case NONE:
                    none.add(item);
                    break;
                case DONE:
                    done.add(item);
                    break;
                case TODO:
                    todo.add(item);
                    break;
                case WORKING:
                    working.add(item);
                    break;
            }
        }
        
        none = new ArrayList(sortByPriority(none));
        done = new ArrayList(sortByPriority(done));
        todo = new ArrayList(sortByPriority(todo));
        working = new ArrayList(sortByPriority(working));
        
        sorted.addAll(none);
        sorted.addAll(done);
        sorted.addAll(todo);
        sorted.addAll(working);
        
        if (selectedHead != null) {
            sorted.add(selectedHead);
        }
        
        if (head == null) {
            heads = new ArrayList(sorted);
        } else {
            head.setChildren(new ArrayList(sorted));
        }
    }
    
    public List<Item> sortByPriority(ArrayList<Item> items) {
        return items.stream().sorted((a, b) -> comparePriority(a, b)).collect(Collectors.toList());
    }
    private int comparePriority(Item a, Item b) {
        if (a.getPriority() < b.getPriority()) return -1;
        if (a.getPriority() == b.getPriority()) return 0;
        return 1;
    }
    
    public static String getStatusColor(Meta.Status status) {
        switch (status) {
            case WORKING:
                return ColoredString.GREEN;
            case TODO:
                return ColoredString.BLUE;
            case NONE:
                return ColoredString.YELLOW;
            case DONE:
                return ColoredString.WHITE;
            default:
                return "";
        }
    }
    
    /*
    
    formatting: 
    
    green, yellow, blue, gray STATUS
    black brackets []
    
    red stars for selected
    green stars for secondary selected
    
    purple highlight for search result
    */
}
