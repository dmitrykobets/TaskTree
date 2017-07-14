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
    final static String DATA_PATH = "./data-test/";
    final static String CONFIG_PATH = "./config/";
    final static String META_SUFFIX_FOR_ITEM = "./_meta/";
    
    public static String getWeekPrepath(int weekNum) {
        return DATA_PATH + "week" + weekNum + "/";
    }
}
