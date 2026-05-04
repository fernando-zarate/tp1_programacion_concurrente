package src;

import java.util.ArrayList;
import java.util.Random;

public class WorkerExecution implements Runnable {

    ArrayList<Job> jobsExecution;
    ArrayList<Job> jobsFailed;
    ArrayList<Job> jobsFinalized;
    Random random;

    public WorkerExecution(ArrayList<Job> jobsExecution, ArrayList<Job> jobsFinalized, ArrayList<Job> jobsFailed) {
        this.jobsExecution = jobsExecution;
        this.jobsFinalized = jobsFinalized;
        this.jobsFailed = jobsFailed;
        random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            Job job_taken = null;
            synchronized (jobsExecution) {
                if (!jobsExecution.isEmpty()) {
                    int i_random_job = Politic.randomIndex(jobsExecution.size()); // Toma un job aleatorio de los jobs en ejecución
                    job_taken = jobsExecution.remove(i_random_job);
                }
            }

            if (job_taken == null) {
                if (!Main.getAreStagesRunning()) {
                    break; // Si las etapas anteriores terminaron y no hay más jobs, el hilo sale
                }
                try {
                    Thread.sleep(20); // Delay corto para no consumir CPU innecesariamente
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            if (Politic.executionSuccess()) { // Valida el porcentaje de éxito/error
                job_taken.stage = 3;
                synchronized (jobsFinalized) {
                    jobsFinalized.add(job_taken); // El job pasa a finalizados
                }
            } else {
                synchronized (jobsFailed) {
                    jobsFailed.add(job_taken); // El job pasa a fallidos
                }
            }

            try {
                Thread.sleep(100); // Tiempo simulado que tarda en la etapa 3
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}