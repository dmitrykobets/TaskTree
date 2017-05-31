/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class Meta {
    public static enum Status {
        NONE, WORKING, DONE, TODO;
    };
    public static Status DEFAULT_STATUS = Status.NONE;
    
    public Status status;
    
    public static Status getStatusFromString(String stat) {
        if (stat.equals("none")) {
            return Status.NONE;
        } else if (stat.equals("working") || stat.equals("in progress")) {
            return Status.WORKING;
        } else if (stat.equals("done")) {
            return Status.DONE;
        } else if (stat.equals("todo")) {
            return Status.TODO;
        }
        System.out.println("Invalid status: " + stat);
        return DEFAULT_STATUS;
    }
    
    public void setDefaults() {
        this.status = DEFAULT_STATUS;
    }
}
