package src;

public class Node {

    // Atributes
    private int id;
    private String status; // "Free", "Busy", "Out of Service"
    private int jobsCounter; // Number of jobs executed by this node.

    public Node(int id) {
        this.id = id;
        this.status = "Free";
        this.jobsCounter = 0;
    }

    public int getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getJobsCounter() { return jobsCounter; }
    public void incrementJobsCounter() { this.jobsCounter++; }
}
