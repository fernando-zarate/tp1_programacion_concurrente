package src;

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger implements Runnable {

    ArrayList<Job> failedJobs;
    ArrayList<Job> verifiedJobs;
    long initialTime;
    long finalTime;

    public Logger(ArrayList<Job> failed, ArrayList<Job> validated) {
        this.verifiedJobs = validated;
        this.failedJobs = failed;
    }

    @Override
    public void run() {
        this.initialTime = System.currentTimeMillis();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("log.txt", true));
            long time;
            int failed;
            int verified;
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (failedJobs) {
                    failed = failedJobs.size();
                }
                synchronized (verifiedJobs) {
                    verified = verifiedJobs.size();
                }
                time = System.currentTimeMillis() - initialTime;
                String line = "Time: " + time + " | Failed Jobs: " + failed + " | Verified Jobs: " + verified;
                System.out.println(line);
                writer.write(line);
                writer.newLine();
                Thread.sleep(200);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // graceful stop: fall through to finally to write summary
        } finally {
            this.finalTime = System.currentTimeMillis() - initialTime;
            try {
                String summary = "Total Time: " + finalTime + " | Total Failed Jobs: " + failedJobs.size() + " | Total Verified Jobs: " + verifiedJobs.size();
                if (writer == null) {
                    writer = new BufferedWriter(new FileWriter("log.txt", true));
                }
                System.out.println(summary);
                writer.write(summary);
                writer.newLine();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
