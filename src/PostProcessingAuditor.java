package src;

import java.util.ArrayList;

public class PostProcessingAuditor implements Runnable {

    private final ArrayList<Job> jobsFinished;
    private final ArrayList<Job> jobsValidated;
    private final ArrayList<Job> jobsFailed;

    public PostProcessingAuditor(ArrayList<Job> jobsFinished, ArrayList<Job> jobsValidated,
                                 ArrayList<Job> jobsFailed) {
        this.jobsFinished  = jobsFinished;
        this.jobsValidated = jobsValidated;
        this.jobsFailed    = jobsFailed;
    }

    @Override
    public void run() {

        while (true) {

            Job job = null;
            synchronized (jobsFinished) {
                if (!jobsFinished.isEmpty()) {
                    job = jobsFinished.remove(0);
                }
            }

            if (job == null) {
                // FIX: Condición de parada: si las etapas anteriores terminaron
                // y no quedan jobs finalizados por auditar, este hilo sale.
                if (!Main.getAreStagesRunning()) {
                    break;
                }
                try { Thread.sleep(5); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                continue;
            }

            // Demora fija de la Etapa 4
            try { Thread.sleep(Politic.AUDITOR_DELAY_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }

            if (Politic.finalCheckCorrect()) {
                job.stage = 4;
                synchronized (jobsValidated) {
                    jobsValidated.add(job);
                }
            } else {
                synchronized (jobsFailed) {
                    jobsFailed.add(job);
                }
            }
        }
    }
}
