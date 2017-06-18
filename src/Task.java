
import org.joda.time.Period;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dmitry
 */
public class Task {
    
    private Period duration;
    private boolean inProgress;
    private String name;
    
    public Task(String name, Period duration, boolean inProgress) {
        this.name = name;
        this.duration = duration;
        this.inProgress = inProgress;
    }
    
    public boolean isInProgress() {
        return this.inProgress;
    }
    
    public void setTime(Period duration) {
        this.duration = duration;
    }
    public Period getTime() {
        return this.duration;
    }
    
    public String getName() {
        return this.name;
    }
}
