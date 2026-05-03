package src;

import java.util.ArrayList;

public class PreExecutionCheck implements Runnable {

    private final ArrayList<Job> jobsQueue;
    private final ArrayList<Job> jobsExecution;
    private final ArrayList<Job> jobsFailed;
    private final Node[] nodes;

    public PreExecutionCheck(ArrayList<Job> jobsQueue, ArrayList<Job> jobsExecution,
                             ArrayList<Job> jobsFailed, Node[] nodes) {
        this.jobsQueue     = jobsQueue;
        this.jobsExecution = jobsExecution;
        this.jobsFailed    = jobsFailed;
        this.nodes         = nodes;
    }

    @Override
    public void run() {

        while (true) {

            Job job = null;
            synchronized (jobsQueue) {
                if (!jobsQueue.isEmpty()) {
                    int idx = Politic.randomIndex(jobsQueue.size());
                    job = jobsQueue.remove(idx);
                }
            }

            if (job == null) {
                // FIX: Condición de parada: si los Schedulers terminaron y la
                // cola está vacía, este hilo ya no tiene trabajo.
                if (!Main.getAreStagesRunning()) {
                    break;
                }
                try { Thread.sleep(5); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                continue;
            }

            // Demora fija de la Etapa 2
            try { Thread.sleep(Politic.PRE_CHECK_DELAY_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }

            if (Politic.isValidJob()) {
                // Válido: liberar UN nodo ocupado y pasar el job a ejecución.
                // FIX: El original no sincronizaba el acceso al nodo → race condition.
                // Ahora se sincroniza sobre el objeto nodo antes de modificarlo.
                boolean nodeFreed = false;
                for (int i = 0; i < nodes.length && !nodeFreed; i++) {
                    synchronized (nodes[i]) {
                        if (nodes[i].getStatus().equals("Busy")) {
                            nodes[i].setStatus("Free");
                            nodes[i].incrementJobsCounter();
                            nodeFreed = true;
                        }
                    }
                }
                job.stage = 2;
                synchronized (jobsExecution) {
                    jobsExecution.add(job);
                }
            } else {
                // Inválido: poner nodo fuera de servicio y marcar job como fallido.
                boolean nodeMarked = false;
                for (int i = 0; i < nodes.length && !nodeMarked; i++) {
                    synchronized (nodes[i]) {
                        if (nodes[i].getStatus().equals("Busy")) {
                            nodes[i].setStatus("Out of Service");
                            nodes[i].incrementJobsCounter();
                            nodeMarked = true;
                        }
                    }
                }
                synchronized (jobsFailed) {
                    jobsFailed.add(job);
                }
            }
        }
    }
}
