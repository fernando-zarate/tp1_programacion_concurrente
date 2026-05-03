package src;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;


public class Logger implements Runnable {
    ArrayList<Job> failedJobs;
    ArrayList<Job> verifiedJobs;
    boolean running;
    long initialTime;
    long finalTime;

    public Logger(ArrayList<Job> failed, ArrayList<Job> validated) {
        this.verifiedJobs = validated;
        this.failedJobs = failed;
        this.running = true;
    }

    public void stopLogger() {
        this.running = false;
    }

    @Override
    public void run() {

        this.initialTime = System.currentTimeMillis();

        try {
            FileWriter file = new FileWriter("log.txt", true);
            BufferedWriter writer = new BufferedWriter(file);

            while (running) {
                int failed = failedJobs.size();
                int verified = verifiedJobs.size();
                long time = System.currentTimeMillis() - initialTime;

                String line = "Time: " + time + " | Failed Jobs: " + failed + " | Verified Jobs: " + verified;
                System.out.println(line);
                writer.write(line);
                writer.newLine();

                Thread.sleep(200);
            }
            this.finalTime = System.currentTimeMillis() - initialTime;

            writer.write("Total Time: " + finalTime + " | Total Failed Jobs: " + failedJobs.size() + " | Total Verified Jobs: " + verifiedJobs.size());
            writer.newLine();
            writer.close();

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

