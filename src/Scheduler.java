package src;

import java.util.ArrayList;
import java.util.Random;

public class Scheduler implements Runnable {

    private ArrayList<Job> jobContainer;
    private ArrayList<Job> jobQueue;
    private Node[] nodes;

    public Scheduler(ArrayList<Job> jobContainer, ArrayList<Job> jobQueue, Node[] nodes) {
        this.jobContainer = jobContainer;
        this.jobQueue = jobQueue;
        this.nodes = nodes;
    }

    @Override
    public void run() {
        while (true) {
            Job job = null;
            synchronized (jobContainer) {
                if (!jobContainer.isEmpty()) {
                    job = jobContainer.remove(0); // Lo toma y lo saca del container
                }
            }

            if (job != null) {
                // Busca un nodo libre, setea como busy y pasa a la siguiente etapa.
                boolean assigned = false;
                while (!assigned) {
                    int randomIndex = Politic.randomIndex(nodes.length);
                    Node node = nodes[randomIndex];

                    synchronized (node) {
                        if (node.getStatus().equals("Free")) {
                            node.setStatus("Busy");
                            node.incrementJobsCounter();
                            job.stage = 1;
                            assigned = true;
                        }
                    }
                }
            }
            // Si no hay nodos libres, sale del while
            else {
                break;
            }
            // Agrega el job a la cola, sincronizado para evitar que se sobrescriban datos
            synchronized (jobQueue) {
                jobQueue.add(job);
            }
            try {
                Thread.sleep(100); // Tiempo simulado para la etapa 1
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}