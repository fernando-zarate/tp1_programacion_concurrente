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

    // Estado de la simulación, volatile para que sea visible entre hilos
    private static volatile boolean areStagesRunning = true;

    public static boolean getAreStagesRunning() {
        return areStagesRunning;
    }

    public static void main(String[] args) {

        // Arreglo de nodos
        Node[] nodes = new Node[NODE_QUANTITY];
        // Listas de jobs
        ArrayList<Job> jobsContainer = new ArrayList<>();
        ArrayList<Job> jobsInQueue = new ArrayList<>();
        ArrayList<Job> jobsInExecution = new ArrayList<>();
        ArrayList<Job> jobsFinished = new ArrayList<>();
        ArrayList<Job> jobsValidated = new ArrayList<>();
        ArrayList<Job> jobsFailed = new ArrayList<>();
        // Instancia e hilo del logger
        Logger logger = new Logger(jobsFailed, jobsValidated);
        Thread loggerThread = new Thread(logger);
        // Arreglos de hilos por etapa
        Thread[] schedulers = new Thread[SCHEDULER_QUANTITY]; // Etapa 1: Scheduler
        Thread[] preExecutionChecks = new Thread[PRE_EXECUTION_CHECK_QUANTITY]; // Etapa 2: PreExecutionCheck
        Thread[] workerExecutions = new Thread[WORKER_EXECUTION_QUANTITY]; // Etapa 3: WorkerExecution
        Thread[] postProcessingAuditors = new Thread[POST_PROCESSING_AUDITOR_QUANTITY]; // Etapa 4: PostProcessingAuditor

        // Inicializar nodos
        for (int i = 0; i < NODE_QUANTITY; i++) {
            nodes[i] = new Node(i);
        }
        // Inicializar jobs
        for (int i = 0; i < JOB_QUANTITY; i++) {
            jobsContainer.add(new Job(i));
        }
        // Inicializar hilos de Scheduler
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            schedulers[i] = new Thread(new Scheduler(jobsContainer, jobsInQueue, nodes));
        }
        // Inicializar hilos de PreExecutionCheck
        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++) {
            preExecutionChecks[i] = new Thread(new PreExecutionCheck(jobsInQueue, jobsInExecution, jobsFailed, nodes));
        }
        // Inicializar hilos de WorkerExecution
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++) {
            workerExecutions[i] = new Thread(new WorkerExecution(jobsInExecution, jobsFinished, jobsFailed));
        }
        // Inicializar hilos de PostProcessingAuditor
        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++) {
            postProcessingAuditors[i] = new Thread(new PostProcessingAuditor(jobsFinished, jobsValidated, jobsFailed));
        }

        // Iniciar hilo del Logger
        loggerThread.start();
        // Iniciar hilos de Scheduler
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            schedulers[i].start();
        }
        // Iniciar hilos de PreExecutionCheck
        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++) {
            preExecutionChecks[i].start();
        }
        // Iniciar hilos de WorkerExecution
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++) {
            workerExecutions[i].start();
        }
        // Iniciar hilos de PostProcessingAuditor
        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++) {
            postProcessingAuditors[i].start();
        }

        // Esperamos a que terminen los Schedulers (terminan cuando el container queda vacío)
        for (int i = 0; i < SCHEDULER_QUANTITY; i++) {
            try {
                schedulers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Esperamos hasta que todos los jobs lleguen a su etapa final (validados o fallidos)
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
                Thread.sleep(20); // Un delay para evitar consumir CPU innecesariamente mientras esperamos a que los jobs terminen
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Señalamos a las etapas que terminen y detenemos el logger
        areStagesRunning = false;
        loggerThread.interrupt(); // Interrumpimos el logger para detener su loop

        // Esperamos a que terminen los hilos restantes
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

        // Esperamos a que el logger termine de escribir
        try {
            loggerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Fin del programa
        System.out.println("Todas las etapas finalizaron. Programa terminado.");
    }
}