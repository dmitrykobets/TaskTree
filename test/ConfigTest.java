/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dmitry
 */
public class ConfigTest {
    
    ArrayList<Item> emptyArray = new ArrayList();
    Item item = null;
    int week = 2;
    
    @Before
    public void before() {
        item = new Item("leaf", emptyArray, null);
        ArrayList<Item> children = new ArrayList();
        children.add(item);
        Item parent = new Item("parent", children, null);
        item.setParent(parent);
    }
    @Before
    public void after() {
        emptyArray.clear();
        item = null;
    }
    
    @Test
    public void getItemTimePath() {
        assertEquals("data/week2/parent/leaf/_meta/time.txt", Config.getItemTimeString(item, week));
    }
    
    @Test
    public void getItemMetaFile() {
        assertEquals("data/week2/parent/leaf/_meta/meta.txt", Config.getItemMetaFileString(item, week));
    }
}
