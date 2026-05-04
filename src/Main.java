package src;

import java.util.ArrayList;

public class Main {

    // Constantes globales
    private static final int NODE_QUANTITY = 200;
    private static final int JOB_QUANTITY = 500;
    private static final int SCHEDULER_QUANTITY = 3;
    private static final int PRE_EXECUTION_CHECK_QUANTITY = 2;
    private static final int WORKER_EXECUTION_QUANTITY = 3;
    private static final int POST_PROCESSING_AUDITOR_QUANTITY = 2;

    // Simulation state
    private static volatile boolean areStagesRunning = true;

    public static boolean getAreStagesRunning() {
        return areStagesRunning;
    }

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
        // Logger instance and thread
        Logger logger = new Logger(jobsFailed, jobsValidated);
        Thread loggerThread = new Thread(logger);
        // Stage threads arrays
        Thread[] schedulers = new Thread[SCHEDULER_QUANTITY]; // Stage 1: Scheduler.
        Thread[] preExecutionChecks = new Thread[PRE_EXECUTION_CHECK_QUANTITY]; // Stage 2: PreExecutionCheck.
        Thread[] workerExecutions = new Thread[WORKER_EXECUTION_QUANTITY]; // Stage 3: WorkerExecution.
        Thread[] postProcessingAuditors = new Thread[POST_PROCESSING_AUDITOR_QUANTITY]; // Stage 4: PostProcessingAuditor.

        // Initialize nodes
        for (int i = 0; i < NODE_QUANTITY; i++) {
            nodes[i] = new Node(i);
        }
        // Initialize jobs
        for (int i = 0; i < JOB_QUANTITY; i++) {
            Job job = new Job(i);
            jobsContainer.add(job);
        }
        // Initialize Scheduler threads array
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            schedulers[i] = new Thread(new Scheduler(jobsContainer, jobsInQueue, nodes));
        }
        // Initialize PreExecutionCheck threads array
        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++) {
            preExecutionChecks[i] = new Thread(new PreExecutionCheck(jobsInQueue, jobsInExecution, jobsFailed, nodes));
        }
        // Initialize WorkerExecution threads array
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++) {
            workerExecutions[i] = new Thread(new WorkerExecution(jobsInExecution, jobsFinished, jobsFailed));
        }
        // Initialize PostProcessingAuditor threads array
        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++) {
            postProcessingAuditors[i] = new Thread(new PostProcessingAuditor(jobsFinished, jobsValidated, jobsFailed));
        }

        // Start Logger thread
        loggerThread.start();
        // Start Scheduler threads
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            schedulers[i].start();
        }
        // Start PreExecutionCheck threads
        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++) {
            preExecutionChecks[i].start();
        }
        // Start WorkerExecution threads
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++) {
            workerExecutions[i].start();
        }
        // Start PostProcessingAuditor threads
        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++) {
            postProcessingAuditors[i].start();
        }

        // Join Scheduler threads first (they finish when job container is empty).
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            try {
                schedulers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Wait until all jobs reach final stage (validated or failed)
        while (true) {
            int failedCount;
            int validatedCount;
            synchronized (jobsFailed) {
                failedCount = jobsFailed.size();
            }
            synchronized (jobsValidated) {
                validatedCount = jobsValidated.size();
            }
            if (failedCount + validatedCount >= JOB_QUANTITY) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Signal stages to stop and stop logger
        areStagesRunning = false;
        // Interrupt logger thread to stop its loop
        loggerThread.interrupt();

        // Join remaining stage threads
        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++) {
            try {
                preExecutionChecks[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++) {
            try {
                workerExecutions[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++) {
            try {
                postProcessingAuditors[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Wait for logger to finish writing
        try {
            loggerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // FINALIZATION:
        System.out.println("All stages finished. Program ended.");
        return;
    }
}