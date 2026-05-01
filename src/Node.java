package src;

public class Node {

    // Atributtes
    private Integer id;
    private String status; // "Free", "Busy", "Out of Service"
    private Integer jobsCounter; // Number of jobs executed by this node.

    public Node(Integer id) {
        this.id = id;
        this.status = "Free";
        this.jobsCounter = 0;
    }

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getJobsCounter() {
        return jobsCounter;
    }

    public void incrementJobsCounter() {
        this.jobsCounter++;
    }
}
