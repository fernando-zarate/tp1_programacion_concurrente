package src;
import java.util.ArrayList;

public class WorkerExecution implements Runnable {

    private final ArrayList<Job> jobsExecution;
    private final ArrayList<Job> jobsFinished;
    private final ArrayList<Job> jobsFailed;

    // FIX: El orden de parámetros estaba invertido en el original.
    // Ahora coincide exactamente con cómo Main los pasa:
    // new WorkerExecution(jobsInExecution, jobsFinished, jobsFailed)
    public WorkerExecution(ArrayList<Job> jobsExecution, ArrayList<Job> jobsFinished, ArrayList<Job> jobsFailed) {
        this.jobsExecution  = jobsExecution;
        this.jobsFinished   = jobsFinished;
        this.jobsFailed     = jobsFailed;
    }

    @Override
    public void run() {

        while (true) {

            Job job = null;
            synchronized (jobsExecution) {
                if (!jobsExecution.isEmpty()) {
                    int idx = Politic.randomIndex(jobsExecution.size());
                    job = jobsExecution.remove(idx);
                }
            }

            if (job == null) {
                // FIX: Condición de parada: si la etapa anterior (PreExecutionCheck)
                // ya terminó y no quedan jobs en ejecución, este hilo puede salir.
                if (!Main.getAreStagesRunning()) {
                    break;
                }
                try { Thread.sleep(5); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                continue;
            }

            // Simula tiempo de ejecución (Etapa 3)
            try { Thread.sleep(Politic.WORKER_DELAY_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }

            if (Politic.executionSuccess()) {
                job.stage = 3;
                synchronized (jobsFinished) {
                    jobsFinished.add(job);
                }
            } else {
                synchronized (jobsFailed) {
                    jobsFailed.add(job);
                }
            }
        }
    }
}
