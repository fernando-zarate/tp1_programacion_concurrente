package src;

import java.util.ArrayList;

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
                    job = jobContainer.remove(0);
                }
            }
            if (job != null) {
                // Buscar un nodo libre, setear como busy y pasar a stage=1
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
            else {
                break;
            }
            // Agregar job a la cola, sincronizado para evitar que se sobrescriban datos
            synchronized (jobQueue) {
                jobQueue.add(job);
            }
            try {
                Thread.sleep(100); // Aca depsues ponemos el tiempo que tarda
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
