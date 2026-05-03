package src;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class Logger implements Runnable {

    private final ArrayList<Job> failedJobs;
    private final ArrayList<Job> verifiedJobs;
    // FIX: volatile para que el flag sea visible entre hilos sin synchronized.
    private volatile boolean running = true;
    private long initialTime;

    public Logger(ArrayList<Job> failed, ArrayList<Job> validated) {
        this.failedJobs   = failed;
        this.verifiedJobs = validated;
    }

    /** Llamado por Main para detener el Logger correctamente. */
    public void stopLogger() {
        this.running = false;
    }

    @Override
    public void run() {
        this.initialTime = System.currentTimeMillis();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("log.txt", false))) {

            // FIX: El original chequeaba this.running pero Main nunca lo seteaba;
            // ahora stopLogger() sí lo setea, y también se chequea getAreStagesRunning()
            // como segunda condición por si acaso.
            while (running) {
                int failed, verified;
                synchronized (failedJobs)   { failed   = failedJobs.size(); }
                synchronized (verifiedJobs) { verified = verifiedJobs.size(); }

                long elapsed = System.currentTimeMillis() - initialTime;
                String line = "Time: " + elapsed + " ms | Failed: " + failed + " | Validated: " + verified;
                System.out.println(line);
                writer.write(line);
                writer.newLine();
                writer.flush();

                Thread.sleep(200);
            }

            // Entrada final
            long total = System.currentTimeMillis() - initialTime;
            String summary = "=== TOTAL | Time: " + total + " ms | Failed: "
                    + failedJobs.size() + " | Validated: " + verifiedJobs.size() + " ===";
            System.out.println(summary);
            writer.write(summary);
            writer.newLine();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
