package src;

import java.util.ArrayList;

public class PostProcessingAuditor implements Runnable {

    private ArrayList<Job> jobsFinished;
    private ArrayList<Job> jobsValidated;
    private ArrayList<Job> jobsFailed;

    public PostProcessingAuditor(ArrayList<Job> jobsFinished, ArrayList<Job> jobsValidated, ArrayList<Job> jobsFailed) {
        this.jobsFinished = jobsFinished;
        this.jobsValidated = jobsValidated;
        this.jobsFailed = jobsFailed;
    }

    @Override
    public void run() {
        while (Main.getAreStagesRunning() || !jobsFinished.isEmpty()) {
            Job job = null;
            synchronized (jobsFinished) {
                if (!jobsFinished.isEmpty()) {
                    job = jobsFinished.remove(0);
                }
            }
            if (job != null) {
                // Acá tenemos que simular un tiempo pero habria que ver cuanto por etapa
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Aplica política
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
}
