package src;

import java.util.ArrayList;
import java.util.Random;

public class PreExecutionCheck implements Runnable {

    ArrayList<Job> jobsQueue;
    ArrayList<Job> jobsExecution;
    ArrayList<Job> jobsFailed;
    Node[] nodes;
    Random random;

    public PreExecutionCheck(ArrayList<Job> jobsQueue, ArrayList<Job> jobsExecution,
                             ArrayList<Job> jobsFailed, Node[] nodes) {
        this.jobsQueue = jobsQueue;
        this.jobsExecution = jobsExecution;
        this.jobsFailed = jobsFailed;
        this.nodes = nodes;
        random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            Job job_taken = null;
            synchronized (jobsQueue) {
                if (!jobsQueue.isEmpty()) {
                    int i_random_job = Politic.randomIndex(jobsQueue.size()); // Toma un job aleatorio de la cola
                    job_taken = jobsQueue.remove(i_random_job);
                }
            }

            if (job_taken == null) {
                if (!Main.getAreStagesRunning()) {
                    break; // Si las etapas anteriores terminaron y la cola está vacía, el hilo sale
                }
                try {
                    Thread.sleep(20); // Delay corto para no consumir CPU innecesariamente
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            if (Politic.isValidJob()) { // Valida el porcentaje de válidos/inválidos (85%/15%)
                //for (int i = 0; i < nodes.length; i++) {

                    synchronized (nodes[job_taken.getAssignedNodeId()]) {
                        if (nodes[job_taken.getAssignedNodeId()].getStatus().equals("Busy")) {
                            nodes[job_taken.getAssignedNodeId()].setStatus("Free"); // El nodo vuelve a estado libre
                            nodes[job_taken.getAssignedNodeId()].incrementJobsCounter();
                            job_taken.stage = 2;
                            // continue normally to move job to execution
                        }
                    }
                //}
                // El job siempre pasa a ejecución
                synchronized (jobsExecution) {
                    jobsExecution.add(job_taken);
                }
            } else {
                //for (int i = 0; i < nodes.length; i++) {
                    synchronized (nodes[job_taken.getAssignedNodeId()]) {
                        if (nodes[job_taken.getAssignedNodeId()].getStatus().equals("Busy")) {
                            nodes[job_taken.getAssignedNodeId()].setStatus("Out of Service"); // El nodo pasa a fuera de servicio
                            nodes[job_taken.getAssignedNodeId()].incrementJobsCounter();
                            // continue normally to mark job as failed
                        }
                    }
                //}
                // El job siempre va a fallidos
                synchronized (jobsFailed) {
                    jobsFailed.add(job_taken);
                }
            }

            try {
                Thread.sleep(70); // Tiempo simulado que tarda en la etapa 2
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}