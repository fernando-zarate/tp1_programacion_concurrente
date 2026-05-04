package src;
import java.util.ArrayList;
import java.util.Random;

public class PreExecutionCheck implements Runnable {

    ArrayList<Job> JobsQueue;
    ArrayList<Job> jobsExecution;
    ArrayList<Job> jobsFailed;
    Node[] nodes;
    Random random;
    
    public PreExecutionCheck(ArrayList<Job> JobsQueue, ArrayList<Job> JobsExcecution, ArrayList<Job> JobsFailed, Node[] nodes) {
        this.JobsQueue = new ArrayList<>();
        this.jobsExecution = new ArrayList<>();
        this.jobsFailed = new ArrayList<>();
        this.nodes = new Node[200];
        this.JobsQueue = JobsQueue;
        this.jobsExecution = JobsExcecution;
        this.jobsFailed = JobsFailed;
        this.nodes = nodes;
        random = new Random();
    }

    @Override
    public void run() {
        while (Main.getAreStagesRunning() || !JobsQueue.isEmpty()) {
            Job job_taken = null;
            synchronized (JobsQueue) {
                if (!JobsQueue.isEmpty()) {
                    // tomamos un job aleatorio de los Jobs en cola
                    int i_random_job = Politic.randomIndex(JobsQueue.size());
                    job_taken = JobsQueue.remove(i_random_job);
                }
            }
            if (job_taken == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }
            if (Politic.executionSuccess()) {
                // vamos a recorrer el arreglo buscando un nodo ocupado
                for (int i = 0; i < nodes.length; i++) {
                    Node node = nodes[i];
                    synchronized (node) {
                        if (node.getStatus().equals("Busy")) {
                            node.setStatus("Free");
                            node.incrementJobsCounter();
                            synchronized (jobsExecution) {
                                jobsExecution.add(job_taken);
                            }
                            break;
                        }
                    }
                }
            } else {
                for (int i = 0; i < nodes.length; i++) {
                    Node node = nodes[i];
                    synchronized (node) {
                        if (node.getStatus().equals("Busy")) {
                            node.setStatus("Out of Service");
                            node.incrementJobsCounter();
                            synchronized (jobsFailed) {
                                jobsFailed.add(job_taken);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
