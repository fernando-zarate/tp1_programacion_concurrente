package src;

import java.util.ArrayList;

public class Scheduler implements Runnable {

    public ArrayList<Job> jobContainer;
    public ArrayList<Job> jobQueue;
    
    public Scheduler(ArrayList<Job> jobContainer, ArrayList<Job> jobQueue) {
        this.jobContainer = jobContainer;
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        
    }
}
