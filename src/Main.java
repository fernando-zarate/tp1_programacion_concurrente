package src;

import java.util.ArrayList;

public class Main {

    // Constants
    private static final Integer NODE_QUANTITY = 200;
    private static final Integer JOB_QUANTITY = 500;

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
        Scheduler[] schedulers = new Scheduler[NODE_QUANTITY];
        PreExecutionCheck[] preExecutionChecks = new PreExecutionCheck[NODE_QUANTITY];
        WorkerExecution[] workerExecutions = new WorkerExecution[NODE_QUANTITY];
        PostProcessingAuditor[] postProcessingAuditors = new PostProcessingAuditor[NODE_QUANTITY];

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

        // Wait for threads to finish

        // End of program
        return;
    }
}
