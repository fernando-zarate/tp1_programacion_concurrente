package src;

public class Job {

    Integer id;
    Integer stage;     // 0: sin empezar, 1: Scheduler, 2: PreCheck, 3: Worker, 4: Auditor
    Boolean isWorking;
    private int assignedNodeId; // ID del nodo asignado, -1 si no tiene nodo asignado

    public Job(Integer id) {
        this.id        = id;
        this.stage     = 0;
        this.isWorking = false;
        this.assignedNodeId = -1;
    }

    public int getAssignedNodeId() {
        return assignedNodeId;
    }

    public void setAssignedNodeId(int assignedNodeId) {
        this.assignedNodeId = assignedNodeId;
    }
}
