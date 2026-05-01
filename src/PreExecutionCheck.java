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
    public void run() 
    {

        while(true)
        {

            if(JobsQueue.size() == 0)
            {
                continue;
            }
            
            // tomamos un job aleatorio de los Jobs en cola
            int i_random_job = Politic.randomIndex(JobsQueue.size());
            Job job_taken = JobsQueue.remove(i_random_job);

            if(Politic.executionSuccess())
            {

                // vamos a recorrer el arreglo buscando un nodo ocupado
                for(int i=0; i<nodes.length; i++)
                {
                    if( nodes[i].getStatus().equals("Busy"))
                    {
                        nodes[i].setStatus("Free");
                        nodes[i].incrementJobsCounter();
                        jobsExecution.add(job_taken);
                        break;
                    }
                }

            }
            else
            {
                for(int i=0; i<nodes.length; i++)
                {
                    if( nodes[i].getStatus().equals("Busy"))
                    {
                        nodes[i].setStatus("Out of Service");
                        nodes[i].incrementJobsCounter();
                        jobsFailed.add(job_taken);
                        break;
                    }
                }
            }
        }    
    }
}
