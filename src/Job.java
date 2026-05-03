package src;

public class Job {

    Integer id;
    Integer stage;     // 0: sin empezar, 1: Scheduler, 2: PreCheck, 3: Worker, 4: Auditor
    Boolean isWorking;

    public Job(Integer id) {
        this.id        = id;
        this.stage     = 0;
        this.isWorking = false;
    }
}
