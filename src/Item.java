
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
public class Item {
    private String name;
    
    private Meta meta;
    
    private ArrayList<Item> children;
    private Item parent = null;
    
    public Item(String name, ArrayList<Item> children, Meta meta) {
        this.name = name;
        this.children = children;
        this.meta = meta;
    }
    public Item(String name) {
        this.name = name;
        this.children = new ArrayList();
    }
    
    public void setParent(Item parent) {
        this.parent = parent;
    }
    
    public Meta getMeta() {
        return this.meta;
    }
    
    public void setStatus(Meta.Status status) {
        this.meta.status = status;
    }
    public boolean isWorking() {
        return this.meta.status == Meta.Status.WORKING;
    }
    public boolean isDone() {
        return this.meta.status == Meta.Status.DONE;
    }
    public boolean isNone() {
        return this.meta.status == Meta.Status.NONE;
    }
    public boolean isTodo() {
        return this.meta.status == Meta.Status.TODO;
    }
    
    public ArrayList<Item> getChildren() {
        return this.children;
    }
    public Item getParent() {
        return this.parent;
    }
    
    public boolean isLeaf() {
        return this.children.isEmpty();
    }
    public boolean isHead() {
        return this.parent == null;
    }
    
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public Meta.Status getStatus() {
        return this.meta.status;
    }
    
    public String getPath(String root) {
        if (this.isHead()) {
            return root + this.name + "/";
        }
        return this.parent.getPath(root) + this.name + "/";
    }
    
    public boolean isDescendantOf(Item other) {
        Item cur = this;
        while (!cur.isHead()) {
            cur = cur.getParent();
            if (cur.getName().equals(other.getName())) return true;
        }
        
        return false;
    }
}
