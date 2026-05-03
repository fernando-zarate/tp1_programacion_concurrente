package src;
import java.util.ArrayList;
import java.util.Random;

public class WorkerExecution implements Runnable {

    ArrayList<Job> jobsExecution;
    ArrayList<Job> jobsFailed;
    ArrayList<Job> JobsFinalized;
    Node[] nodes;

    Random random;
    
    public WorkerExecution(ArrayList<Job> JobsFinalized, ArrayList<Job> JobsExcecution, ArrayList<Job> JobsFailed, Node[] nodes) {
        
 
        this.jobsExecution = new ArrayList<>();
        this.jobsFailed = new ArrayList<>();
        this.JobsFinalized = new ArrayList<>();
        this.nodes = new Node[200];

        this.JobsFinalized = JobsFinalized;
        this.jobsExecution = JobsExcecution;
        this.jobsFailed = JobsFailed;
        this.nodes = nodes;
        random = new Random();
    }

    @Override
    public void run() {


        while(true)
        {

            if(JobsFinalized.size() == 0)
            {
                try{
                    Thread.sleep(100);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                continue;
            }
            
            // tomamos un job aleatorio de los Jobs en cola
            Job job_taken = null;
            synchronized(JobsFinalized)
            {
                int i_random_job = Politic.randomIndex(JobsFinalized.size());
                job_taken = JobsFinalized.remove(i_random_job);
            }

            if(Politic.executionSuccess())
            {

                // vamos a recorrer el arreglo buscando un nodo ocupado
                synchronized(nodes)
                {
                    for(int i=0; i<nodes.length; i++)
                    {
                        if( nodes[i].getStatus().equals("Busy"))
                        {
                            nodes[i].setStatus("Free");
                            nodes[i].incrementJobsCounter();
                            JobsFinalized.add(job_taken);
                            break;
                        }
                    }
                }

            }
            else
            {   
                synchronized(nodes)
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
            try{
                Thread.sleep(100); 
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }    
    }
}
