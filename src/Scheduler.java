package src;

import java.util.ArrayList;

public class Scheduler implements Runnable {

    private final ArrayList<Job> jobContainer;
    private final ArrayList<Job> jobQueue;
    private final Node[] nodes;

    public Scheduler(ArrayList<Job> jobContainer, ArrayList<Job> jobQueue, Node[] nodes) {
        this.jobContainer = jobContainer;
        this.jobQueue     = jobQueue;
        this.nodes        = nodes;
    }

    @Override
    public void run() {

        while (true) {

            Job job = null;
            synchronized (jobContainer) {
                if (!jobContainer.isEmpty()) {
                    job = jobContainer.remove(0);
                }
            }

            // FIX: El original tenía jobQueue.add(job) FUERA del if,
            // por lo que se añadía null cuando no había jobs.
            // Ahora el break y el add están correctamente dentro de cada rama.
            if (job == null) {
                break; // No hay más jobs que schedulear → este hilo termina
            }

            // Buscar un nodo libre de forma aleatoria
            boolean assigned = false;
            while (!assigned) {
                int idx  = Politic.randomIndex(nodes.length);
                Node node = nodes[idx];

                // FIX: Cada nodo se bloquea individualmente para evitar que
                // dos hilos Scheduler asignen el mismo nodo simultáneamente.
                synchronized (node) {
                    if (node.getStatus().equals("Free")) {
                        node.setStatus("Busy");
                        node.incrementJobsCounter();
                        job.stage = 1;
                        assigned  = true;
                    }
                }
            }

            // Agregar a la cola solo después de una asignación exitosa
            synchronized (jobQueue) {
                jobQueue.add(job);
            }

            // Demora fija de la Etapa 1
            try { Thread.sleep(Politic.SCHEDULER_DELAY_MS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
    }
}
