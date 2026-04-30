package src;

public class Node {

    // Atributtes
    Integer id;
    String status; // "Free", "Busy", "Out of Service"
    Integer jobsCounter; // Number of jobs executed by this node.

    public Node(Integer id) {
        this.id = id;
        this.status = "Free";
        this.jobsCounter = 0;
    }
}
