/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class Config { // Convention: all folders end with /
    private final static String DATA_FOLDER_STRING = "data/";
    private final static String CONFIG_FOLDER_STRING = "config/";
    private final static String CONFIG_FILE_STRING = "config.txt";
    private final static String ITEM_META_FOLDER_STRING = "_meta/";
    private final static String ITEM_META_FILE_STRING = "meta.txt";
    private final static String ITEM_TIME_FILE_STRING = "time.txt";
    
    // consider merging this method&variable with getWeekFolder
    final static String WEEK_FOLDER_PREFIX = "week";
    private static String getWeekFolderName(int weekNum) {
        return WEEK_FOLDER_PREFIX + weekNum + "/";
    }
    
    private static String getWeekFolder(int weekNum) {
        return DATA_FOLDER_STRING + getWeekFolderName(weekNum);
    }
    
    private static String getItemMetaFolderString(Item item, int week) {
        return item.getPath(getWeekFolder(week)) + ITEM_META_FOLDER_STRING;
    }
    
    public static String getItemTimeString(Item item, int week) {
        return getItemMetaFolderString(item, week) + ITEM_TIME_FILE_STRING;
    }
    public static String getItemMetaFileString(Item item, int week) {
        return getItemMetaFolderString(item, week) + ITEM_META_FILE_STRING;
    }
}
