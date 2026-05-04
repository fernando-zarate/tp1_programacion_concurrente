package src;

public class Job {

    // Atributes
    int id;
    int stage; // 0: Not started, 1: Scheduler, 2: PreExecutionCheck, 3: WorkerExecution, 4: PostProcessingAuditor.
    boolean isWorking; // True if the job is being processed by a thread, false otherwise.

    public Job(int id) {
        this.id = id;
        this.stage = 0;
        this.isWorking = false;
    }
}
