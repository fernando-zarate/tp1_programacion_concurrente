package src;

public class Node {

    private final Integer id;
    private String  status;       // "Free", "Busy", "Out of Service"
    private Integer jobsCounter;  // Contador de ejecuciones de este nodo

    public Node(Integer id) {
        this.id          = id;
        this.status      = "Free";
        this.jobsCounter = 0;
    }

    public Integer getId()          { return id; }
    public String  getStatus()      { return status; }
    public void    setStatus(String status) { this.status = status; }
    public Integer getJobsCounter() { return jobsCounter; }
    public void    incrementJobsCounter() { this.jobsCounter++; }
}
