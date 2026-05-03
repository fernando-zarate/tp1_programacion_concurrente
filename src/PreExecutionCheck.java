package src;
import java.util.ArrayList;
import java.util.Random;

public class PreExecutionCheck implements Runnable {

    ArrayList<Job> jobsQueue;
    ArrayList<Job> jobsExecution;
    ArrayList<Job> jobsFailed;
    Node[] nodes;

    Random random;
    
    public PreExecutionCheck(ArrayList<Job> jobsQueue, ArrayList<Job> jobsExecution, ArrayList<Job> jobsFailed, Node[] nodes) {
        
        this.jobsQueue = jobsQueue;
        this.jobsExecution = jobsExecution;
        this.jobsFailed = jobsFailed;
        this.nodes = nodes;

        random = new Random();
    }

    @Override
    public void run() 
    {

        while(true)
        {
            
            // tomamos un job aleatorio de los Jobs en cola
            Job job_taken = null;
            synchronized(jobsQueue)
            {
                if(!jobsQueue.isEmpty())
                {
                    int i_random_job = Politic.randomIndex(jobsQueue.size());
                    job_taken = jobsQueue.remove(i_random_job);
                }
                
            }

            if(job_taken == null)
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

            if(Politic.isValidJob())
            {

                // vamos a recorrer el arreglo buscando un nodo ocupado
                synchronized(nodes)
                {
                    for(int i=0; i<nodes.length; i++)
                    {
                        if( nodes[i].getStatus().equals("Busy"))
                        {
                            nodes[i].setStatus("Free");
                            //nodes[i].incrementJobsCounter();
                            synchronized(jobsExecution)
                            {
                                jobsExecution.add(job_taken);
                            }
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
                            //nodes[i].incrementJobsCounter();
                            synchronized(jobsFailed)
                            {
                                jobsFailed.add(job_taken);
                            }
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
