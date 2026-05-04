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
            writer = new BufferedWriter(new FileWriter("log.txt", true)); // cambiar a false si no queremos sobrescribir el archivo en cada ejecución
            long time;
            int failed;
            int verified;
            while (!Thread.currentThread().isInterrupted()) { // Corre hasta que el Main interrumpa el hilo
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
                Thread.sleep(200); // Registra cada 200ms
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restablece el estado de interrupción
        } finally {
            // Al finalizar, escribe el resumen total
            this.finalTime = System.currentTimeMillis() - initialTime;
            try {
                String summary = "Total Time: " + finalTime + " | Total Failed Jobs: " + failedJobs.size() + " | Total Verified Jobs: " + verifiedJobs.size();
                if (writer == null) {
                    writer = new BufferedWriter(new FileWriter("log.txt", false));
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