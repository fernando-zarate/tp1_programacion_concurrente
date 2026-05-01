package src;

import java.util.ArrayList;

public class Main {

    // Constants
    private static final Integer NODE_QUANTITY = 200;
    private static final Integer JOB_QUANTITY = 500;
    private static final Integer SCHEDULER_QUANTITY = 3;
    private static final Integer PRE_EXECUTION_CHECK_QUANTITY = 2;
    private static final Integer WORKER_EXECUTION_QUANTITY = 3;
    private static final Integer POST_PROCESSING_AUDITOR_QUANTITY = 2;

    public static void main(String[] args) {

        // Node array
        Node[] nodes = new Node[NODE_QUANTITY];
        // Job lists
        ArrayList<Job> jobsContainer = new ArrayList<>();
        ArrayList<Job> jobsInQueue = new ArrayList<>();
        ArrayList<Job> jobsInExecution = new ArrayList<>();
        ArrayList<Job> jobsFinished = new ArrayList<>();
        ArrayList<Job> jobsValidated = new ArrayList<>();
        ArrayList<Job> jobsFailed = new ArrayList<>();
        // Threads arrays
        Scheduler[] schedulers = new Scheduler[SCHEDULER_QUANTITY];
        PreExecutionCheck[] preExecutionChecks = new PreExecutionCheck[PRE_EXECUTION_CHECK_QUANTITY];
        WorkerExecution[] workerExecutions = new WorkerExecution[WORKER_EXECUTION_QUANTITY];
        PostProcessingAuditor[] postProcessingAuditors = new PostProcessingAuditor[POST_PROCESSING_AUDITOR_QUANTITY];

        // Initialize nodes
        for (int i = 0; i < NODE_QUANTITY; i++) {
            nodes[i] = new Node(i);
        }
        // Initialize jobs
        for (int i = 0; i < JOB_QUANTITY; i++) {
            Job job = new Job(i);
            jobsContainer.add(job);
        }
        
        // Start threads
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            schedulers[i] = new Scheduler(jobsContainer, jobsInQueue);
            schedulers[i].start();
        }
        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++) {
            preExecutionChecks[i] = new PreExecutionCheck(xx);
            preExecutionChecks[i].start();
        }
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++) {
            workerExecutions[i] = new WorkerExecution(xx);
            workerExecutions[i].start();
        }
        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++) {
            postProcessingAuditors[i] = new PostProcessingAuditor(xx);
            postProcessingAuditors[i].start();
        }

        // Wait for threads to finish
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            try {
                schedulers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // End of program
        return;
    }
}
