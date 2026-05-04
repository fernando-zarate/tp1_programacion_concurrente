package src;

import java.util.ArrayList;

public class PostProcessingAuditor implements Runnable {

    private ArrayList<Job> jobsFinished;
    private ArrayList<Job> jobsValidated;
    private ArrayList<Job> jobsFailed;

    public PostProcessingAuditor(ArrayList<Job> jobsFinished, ArrayList<Job> jobsValidated,
                                 ArrayList<Job> jobsFailed) {
        this.jobsFinished = jobsFinished;
        this.jobsValidated = jobsValidated;
        this.jobsFailed = jobsFailed;
    }

    @Override
    public void run() {
        while (true) {
            Job job = null;
            synchronized (jobsFinished) {
                if (!jobsFinished.isEmpty()) {
                    job = jobsFinished.remove(0); // Toma el primer nodo de la lista de los finalizados y lo saca
                }
            }
            if (job != null) {
                try {
                    Thread.sleep(100); // Tiempo simulado que tarda en la etapa 4
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Aplica política
                if (Politic.finalCheckCorrect()) { // Valida el porcentaje de validados/fallidos
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
            else {
                if (!Main.getAreStagesRunning()) {
                    break; // Si las etapas anteriores terminaron y no hay más jobs, el hilo sale
                }
                try {
                    Thread.sleep(20); // Un delay corto para evitar consumir CPU innecesariamente
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}