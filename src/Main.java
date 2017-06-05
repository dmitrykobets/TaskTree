
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class Main {
    
    final String DATA_PATH = "./data/";
    final String CONFIG_PATH = "./config/";
    final String META_PATH = "_meta/";
    
    ArrayList<Item> heads = new ArrayList();
    Item selected = null;
    Item secondarySelected = null;
    boolean secondarySelection = false;
    
    String searchStr = "";
    HashMap<Item, Integer> searchResults = new HashMap();
    
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
    
    
    public final String WORKING_FORMAT = ANSI_BLUE;
    public final String DONE_FORMAT = ANSI_GREEN;
    public final String TODO_FORMAT = ANSI_CYAN;
    public final String NONE_FORMAT = ANSI_YELLOW;
    
    int currentWeek = 0;
    int viewWeek = 0;
    
    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }
    
    public void run() {
        loadConfig();
        loadFiles(currentWeek);
        printWeek(false);
        Filter.printFilters();
        
        Scanner in = new Scanner(System.in);
        do {
            System.out.println();
            printAll();
            System.out.print(">> ");
            Integer target = null;
            
            String line = in.nextLine().trim();
            System.out.println(ANSI_RED + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + ANSI_RESET);
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
                    select(target);
                    if (selected != null) bringBranchToBottom(selected);
                } else if (toks[0].equals("new") && !secondarySelection) {
                    makeNew(in);
                } else if (toks[0].equals("clear") || toks[0].equals("clr")) {
                    setSelected(null);
                } else if (toks[0].equals("l")) {
                    navigate('l');
                } else if (toks[0].equals("h")) {
                    navigate('h');
                } else if (toks[0].equals("k")) {
                    navigate('k');
                } else if (toks[0].equals("j")) {
                    navigate('j');
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
                } else if (toks[0].equals("filters")) {
                    Filter.printFilters();
                } else if (toks[0].equals("rm") && !secondarySelection) {
                    deletePrompt();
                } else if (toks[0].equals("pre") && !secondarySelection) {
                    makeNewPre(in);
                } else if (toks[0].equals("filter")) {
                    Filter.filterPrompt();
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
                } else if (toks[0].equals("move") || toks[0].equals("mv")) {
                    move();
                } else if ((toks[0].equals("open") || toks[0].equals("folder")) && !secondarySelection) {
                    openFolder();
                } else if ((toks[0].equals("sort")) && !secondarySelection) {
                    sort(null);
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
    
    public void search(String str, boolean all) {
        searchResults.clear();
        searchStr = str;
        
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
            System.out.println(ANSI_RED + "No search results." + ANSI_RESET);
            waitForInput();
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
        int start = item.getName().indexOf(match);
        searchResults.put(item, start);
    }
    
    public boolean matchesSearch(String str, String search) {
        return str.contains(search);
    }
    
    public void printList(String filter) {
        System.out.println(ANSI_RED + "Items matching '" + filter + "':" + ANSI_RESET);
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
                    navigate(chars[i]);
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
        Item head = item;
        while (!head.isHead()) head = head.getParent();
        moveToLast(heads, head);
    }
    
    public void navigate(char c) {
        switch (c) {
            case 'j':
                if (getSelected() == null) {
                    select(0); 
                } else {
                    select(getCurIdx() + 1);
                }
                break;
            case 'k':
                if (getSelected() == null) {
                    select(0);
                } else {
                    select(getCurIdx() - 1);
                }
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
        System.out.println(ANSI_RED + "Type anything to continue" + ANSI_RESET);
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
    
    public String getWeekPrepath(int weekNum) {
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
            System.out.println(ANSI_RED + "Viewing archived notes: " + ANSI_RESET);
        }
        
        if (!notes.exists()) {
            if (viewWeek != currentWeek) {
                System.out.println(ANSI_RED + "Non-existent archive" + ANSI_RESET);
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
            System.out.println(ANSI_RED + "//////////// END ///////////////" + ANSI_RESET);
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
        
            if (selected.isDone()) {
                newItem.setStatus(Meta.Status.DONE);
                writeMeta(newPath, newItem.getMeta(), false);
            } else if (selected.isWorking() && selected.isLeaf()) {
                newItem.setStatus(Meta.Status.WORKING);
                writeMeta(newPath, newItem.getMeta(), false);
            } else if (selected.isTodo()) {
                newItem.setStatus(Meta.Status.TODO);
                writeMeta(newPath, newItem.getMeta(), false);
            }

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
    
    public void printAll() {
        int count = 0;
        for (Item head: heads) {
            printStructure(head, count ++, 0);
        }
        if (!heads.isEmpty()) {
            System.out.println();
            System.out.println();
        }
    }
    
    public void printStructure(Item item, int count, int offset) {
        if (count > 0) {
            System.out.println(); // end current line
            // extra space
            //System.out.println();
        }
        if (offset != 0 && count != 0) {
            System.out.print(String.format("%" + offset + "s", ""));
        }
        
        String nameStr = item.getName();
        int nameLength = nameStr.length();
        
        
        
        if (searchResults.containsKey(item)) {
            nameStr = Filter.applyFilters(item, nameStr.substring(0, searchResults.get(item))) +
                    ANSI_PURPLE + nameStr.substring(searchResults.get(item), searchResults.get(item) + searchStr.length()) + ANSI_RESET + 
                    Filter.applyFilters(item, nameStr.substring(searchResults.get(item) + searchStr.length()));
            nameStr = ANSI_PURPLE + "[" + ANSI_RESET + nameStr + ANSI_PURPLE + "]" + ANSI_RESET;
            nameLength += 2;
        } else {
            nameStr = Filter.applyFilters(item, nameStr);
            nameStr = "[" + nameStr + "]";
            nameLength += 2;
        }
        
        if (item == getSelected() || item == selected) {
            String format = ANSI_RED;
            if (item == secondarySelected) {
                format = ANSI_GREEN;
            }
            nameStr += format + "*" + ANSI_RESET;
            nameStr = format + "*" + ANSI_RESET + nameStr;
            nameLength += 2;
        }
        
        String pre = count + ". ";
        
        String post = "";
        if (!item.isLeaf()) {
            post = " --> ";
        }

        System.out.print(pre + nameStr + post);
        
        count = 0;
        for (Item child: item.getChildren()) {
            printStructure(child, count ++, offset + pre.length() + nameLength + post.length());
        }
    }
    
    public Meta getMeta(File metaFolder) {
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
                    System.out.println("Error creating file: meta.txt for item path: " + path);
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            PrintWriter writer = new PrintWriter(metaFile);
            String line = "status: " + (meta == null ? Meta.DEFAULT_STATUS : meta.status);
            writer.println(line.toLowerCase());
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
                meta = getMeta(content);
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
    
    public void updateStatus(Meta.Status status, Item item) {
        updateStatusDown(status, item);
        updateStatusUp(item);
    }
    public void updateStatusDown(Meta.Status status, Item item) {
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
            writeMeta(item.getParent().getPath(getWeekPrepath(currentWeek)), item.getParent().getMeta(), false);
            updateStatusUp(item.getParent());
        }
    }
    
    public String getPrimarySelectedPath() {
        if (selected == null) return getWeekPrepath(viewWeek);
        return selected.getPath(getWeekPrepath(viewWeek));
    }
    
    public Item getSelected() {
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
                    System.out.println("Missing attribute: 'status' in file: " + weekFile.getPath());
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
        ArrayList<Item> sorted = new ArrayList();
        
        // Order: none, done, todo, working
        int lastNone = -1;
        int lastDone = -1;
        int lastTodo = -1;
        int lastWorking = -1;
        for (Item item: (head == null ? heads : head.getChildren())) {
            sort(item);
            if (head == null && item == heads.get(heads.size() - 1) && selected != null) {
                sorted.add(item);
                break;
            }
            switch(item.getStatus()) {
                case NONE:
                    sorted.add(lastNone + 1, item);
                    break;
                case DONE:
                    sorted.add(lastDone + 1, item);
                    break;
                case TODO:
                    sorted.add(lastTodo + 1, item);
                    break;
                case WORKING:
                    sorted.add(lastWorking + 1, item);
                    break;
            }
            switch(item.getStatus()) {
                case NONE:
                    lastNone ++;
                case DONE:
                    lastDone ++;
                case TODO:
                    lastTodo ++;
                case WORKING:
                    lastWorking ++;
            }
        }
        if (head == null) {
            heads = new ArrayList(sorted);
        } else {
            head.setChildren(new ArrayList(sorted));
        }
    }
}
