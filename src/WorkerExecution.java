package src;
import java.util.ArrayList;
import java.util.Random;

public class WorkerExecution implements Runnable {

    ArrayList<Job> jobsExecution;
    ArrayList<Job> jobsFailed;
    ArrayList<Job> jobsFinalized;

    Random random;
    
    public WorkerExecution(ArrayList<Job> jobsFinalized, ArrayList<Job> jobsExecution, ArrayList<Job> jobsFailed) {
        
        this.jobsFinalized = jobsFinalized;
        this.jobsExecution = jobsExecution;
        this.jobsFailed = jobsFailed;
        random = new Random();
    }

    @Override
    public void run() {


        while(true)
        {

            
            // tomamos un job aleatorio de los Jobs en ejecucion
            Job job_taken = null;
            synchronized(jobsExecution)
            {
                if(!jobsExecution.isEmpty())
                {
                    int i_random_job = Politic.randomIndex(jobsExecution.size());
                    job_taken = jobsExecution.remove(i_random_job);
                }
            }
           
            if(job_taken == null)
            {
                try{
                    Thread.sleep(1);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                continue;
            }

            if(Politic.executionSuccess())
            {
            synchronized(jobsFinalized)
            {
                jobsFinalized.add(job_taken);
            }

            try{
                Thread.sleep(1); 
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

            break;

            }

            else
            {   
                synchronized(jobsFailed)
                    {
                        jobsFailed.add(job_taken);
                    }
                try
                {
                    Thread.sleep(1); 
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            break;
            }

            
        }    
    }
}
