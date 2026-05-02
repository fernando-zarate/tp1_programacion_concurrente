package src;

import java.util.ArrayList;

public class Main {

    // GLOBAL CONSTANTS:
    private static final Integer NODE_QUANTITY = 200;
    private static final Integer JOB_QUANTITY = 500;
    private static final Integer SCHEDULER_QUANTITY = 3;
    private static final Integer PRE_EXECUTION_CHECK_QUANTITY = 2;
    private static final Integer WORKER_EXECUTION_QUANTITY = 3;
    private static final Integer POST_PROCESSING_AUDITOR_QUANTITY = 2;

    // GLOBAL VARIABLES:
    // Simulation state.
    static Boolean areStagesRunning = true;

    public static void main(String[] args) {

        // DATA STRUCTURES:
        // Node array.
        Node[] nodes = new Node[NODE_QUANTITY];
        // Job lists.
        ArrayList<Job> jobsContainer = new ArrayList<>();
        ArrayList<Job> jobsInQueue = new ArrayList<>();
        ArrayList<Job> jobsInExecution = new ArrayList<>();
        ArrayList<Job> jobsFinished = new ArrayList<>();
        ArrayList<Job> jobsValidated = new ArrayList<>();
        ArrayList<Job> jobsFailed = new ArrayList<>();
        // Logger thread.
        Thread logger = new Thread(new Logger(nodes, jobsContainer, jobsInQueue, jobsInExecution, jobsFinished, jobsValidated, jobsFailed));
        // Stage threads arrays.
        Thread[] schedulers = new Thread[SCHEDULER_QUANTITY]; // Stage 1: Scheduler.
        Thread[] preExecutionChecks = new Thread[PRE_EXECUTION_CHECK_QUANTITY]; // Stage 2: PreExecutionCheck.
        Thread[] workerExecutions = new Thread[WORKER_EXECUTION_QUANTITY]; // Stage 3: WorkerExecution.
        Thread[] postProcessingAuditors = new Thread[POST_PROCESSING_AUDITOR_QUANTITY]; // Stage 4: PostProcessingAuditor.

        // INITIALIZATIONS:
        // Initialize nodes.
        for (int i = 0; i < NODE_QUANTITY; i++) {
            nodes[i] = new Node(i);
        }
        // Initialize jobs.
        for (int i = 0; i < JOB_QUANTITY; i++) {
            Job job = new Job(i);
            jobsContainer.add(job);
        }
        // Initialize Scheduler threads array.
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            schedulers[i] = new Thread(new Scheduler(nodes, jobsContainer, jobsInQueue));
        }
        // Initialize PreExecutionCheck threads array.
        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++) {
            preExecutionChecks[i] = new Thread(new PreExecutionCheck(nodes, jobsInQueue, jobsInExecution, jobsFailed));
        }
        // Initialize WorkerExecution threads array.
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++) {
            workerExecutions[i] = new Thread(new WorkerExecution(nodes, jobsInExecution, jobsFinished, jobsFailed));
        }
        // Initialize PostProcessingAuditor threads array.
        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++) {
            postProcessingAuditors[i] = new Thread(new PostProcessingAuditor(nodes, jobsFinished, jobsValidated, jobsFailed));
        }

        // START OF ALL STAGES AND LOGGER:
        // Start Logger thread.
        logger.start();
        // Start Scheduler threads.
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            schedulers[i].start();
        }
        // Start PreExecutionCheck threads.
        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++) {
            preExecutionChecks[i].start();
        }
        // Start WorkerExecution threads.
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++) {
            workerExecutions[i].start();
        }
        // Start PostProcessingAuditor threads.
        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++) {
            postProcessingAuditors[i].start();
        }

        // SINCHRONIZATION OF STAGES:
        // Set Join for Scheduler threads.
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            try {
                schedulers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Set Join for PreExecutionCheck threads.
        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++) {
            try {
                preExecutionChecks[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Set Join for WorkerExecution threads.
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++) {
            try {
                workerExecutions[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Set Join for PostProcessingAuditor threads.
        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++) {
            try {
                postProcessingAuditors[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // PRE FINALIZATION:
        // Set areStagesRunning to false to stop Logger thread.
        areStagesRunning = false;
        // Set Join for Logger thread to wait for completion.
        try {
            logger.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // FINALIZATION:
        System.out.println("All stages finished. Program ended.");
        return;
    }

    public static Boolean getAreStagesRunning() {
        return areStagesRunning;
    }
}
