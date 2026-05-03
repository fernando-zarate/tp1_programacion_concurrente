package src;

import java.util.ArrayList;

public class Main {

    // CONSTANTES GLOBALES
    private static final int NODE_QUANTITY                  = 200;
    private static final int JOB_QUANTITY                   = 500;
    private static final int SCHEDULER_QUANTITY             = 3;
    private static final int PRE_EXECUTION_CHECK_QUANTITY   = 2;
    private static final int WORKER_EXECUTION_QUANTITY      = 3;
    private static final int POST_PROCESSING_AUDITOR_QUANTITY = 2;

    // FIX: volatile garantiza visibilidad entre hilos sin necesidad de synchronized.
    // Los hilos workers leen esta flag para saber cuándo deben terminar.
    private static volatile boolean areStagesRunning = true;

    public static void main(String[] args) throws InterruptedException {

        // ── Estructuras de datos compartidas ─────────────────────────────────
        Node[]          nodes          = new Node[NODE_QUANTITY];
        ArrayList<Job>  jobsContainer  = new ArrayList<>();
        ArrayList<Job>  jobsInQueue    = new ArrayList<>();
        ArrayList<Job>  jobsInExecution = new ArrayList<>();
        ArrayList<Job>  jobsFinished   = new ArrayList<>();
        ArrayList<Job>  jobsValidated  = new ArrayList<>();
        ArrayList<Job>  jobsFailed     = new ArrayList<>();

        // ── Inicialización ────────────────────────────────────────────────────
        for (int i = 0; i < NODE_QUANTITY; i++) nodes[i] = new Node(i);
        for (int i = 0; i < JOB_QUANTITY;  i++) jobsContainer.add(new Job(i));

        // ── Creación de hilos ─────────────────────────────────────────────────
        Logger loggerRunnable = new Logger(jobsFailed, jobsValidated);
        Thread logger = new Thread(loggerRunnable);

        Thread[] schedulers              = new Thread[SCHEDULER_QUANTITY];
        Thread[] preExecutionChecks      = new Thread[PRE_EXECUTION_CHECK_QUANTITY];
        Thread[] workerExecutions        = new Thread[WORKER_EXECUTION_QUANTITY];
        Thread[] postProcessingAuditors  = new Thread[POST_PROCESSING_AUDITOR_QUANTITY];

        for (int i = 0; i < SCHEDULER_QUANTITY; i++)
            schedulers[i] = new Thread(new Scheduler(jobsContainer, jobsInQueue, nodes));

        for (int i = 0; i < PRE_EXECUTION_CHECK_QUANTITY; i++)
            preExecutionChecks[i] = new Thread(new PreExecutionCheck(jobsInQueue, jobsInExecution, jobsFailed, nodes));

        // FIX: El original pasaba (jobsInExecution, jobsFinished, jobsFailed)
        // pero el constructor de WorkerExecution esperaba (jobsFinalized, jobsExecution, jobsFailed).
        // Ahora el constructor fue corregido para coincidir con este orden lógico.
        for (int i = 0; i < WORKER_EXECUTION_QUANTITY; i++)
            workerExecutions[i] = new Thread(new WorkerExecution(jobsInExecution, jobsFinished, jobsFailed));

        for (int i = 0; i < POST_PROCESSING_AUDITOR_QUANTITY; i++)
            postProcessingAuditors[i] = new Thread(new PostProcessingAuditor(jobsFinished, jobsValidated, jobsFailed));

        // ── Inicio de todos los hilos ─────────────────────────────────────────
        logger.start();
        for (Thread t : schedulers)             t.start();
        for (Thread t : preExecutionChecks)     t.start();
        for (Thread t : workerExecutions)       t.start();
        for (Thread t : postProcessingAuditors) t.start();

        // ── Sincronización por etapas ─────────────────────────────────────────
        // Los Schedulers terminan solos (agotan jobContainer y salen del while).
        for (Thread t : schedulers) t.join();
        System.out.println("[Main] Schedulers finalizados.");

        // Drenamos cada cola en orden: esperamos a que quede vacía antes de
        // avanzar a la siguiente etapa. Un pequeño sleep de asentamiento evita
        // la ventana donde un hilo sacó el último item pero aún no lo depositó.
        boolean allEmpty;
        do {
            allEmpty = true;
            synchronized (jobsInQueue)      { if (!jobsInQueue.isEmpty())      allEmpty = false; }
            synchronized (jobsInExecution)  { if (!jobsInExecution.isEmpty())  allEmpty = false; }
            synchronized (jobsFinished)     { if (!jobsFinished.isEmpty())      allEmpty = false; }
            if (!allEmpty) Thread.sleep(20);
        } while (!allEmpty);
        // Settle: esperamos dos ciclos de procesamiento extra para que los hilos
        // depositen cualquier job que tengan en mano.
        Thread.sleep(200);

        // Todas las colas drenadas → señalizamos parada a los hilos workers.
        areStagesRunning = false;

        for (Thread t : preExecutionChecks)     t.join();
        System.out.println("[Main] PreExecutionChecks finalizados.");
        for (Thread t : workerExecutions)       t.join();
        System.out.println("[Main] WorkerExecutions finalizados.");
        for (Thread t : postProcessingAuditors) t.join();
        System.out.println("[Main] PostProcessingAuditors finalizados.");

        loggerRunnable.stopLogger();
        logger.join();

        // ── Estadísticas finales de nodos ─────────────────────────────────────
        System.out.println("\n=== ESTADÍSTICAS DE NODOS ===");
        int free = 0, busy = 0, outOfService = 0;
        for (Node n : nodes) {
            switch (n.getStatus()) {
                case "Free"           -> free++;
                case "Busy"           -> busy++;
                case "Out of Service" -> outOfService++;
            }
        }
        System.out.println("Nodos libres: "           + free);
        System.out.println("Nodos ocupados: "         + busy);
        System.out.println("Nodos fuera de servicio: " + outOfService);
        System.out.println("\n=== RESULTADO FINAL ===");
        System.out.println("Jobs validados: " + jobsValidated.size());
        System.out.println("Jobs fallidos:  " + jobsFailed.size());
        System.out.println("Total:          " + (jobsValidated.size() + jobsFailed.size()));
        System.out.println("\nPrograma finalizado.");
    }

    /** Usado por los hilos workers para saber si deben seguir esperando jobs. */
    public static boolean getAreStagesRunning() {
        return areStagesRunning;
    }
}
