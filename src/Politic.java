package src;

import java.util.Random;

public class Politic {

    private static final Random random = new Random();

    // ─── Demoras fijas por etapa (en milisegundos) ───────────────────────────
    // El análisis teórico con estos valores da ~20 s para 500 jobs:
    //   Etapa 1 (Scheduler, 3 hilos):           500 jobs / 3  * 30 ms ≈  5 s
    //   Etapa 2 (PreExecutionCheck, 2 hilos):   500 jobs / 2  * 40 ms ≈ 10 s  ← cuello de botella
    //   Etapa 3 (WorkerExecution, 3 hilos):     ~425 jobs / 3 * 20 ms ≈  3 s
    //   Etapa 4 (PostProcessingAuditor, 2 hilos):~382 jobs / 2 * 15 ms ≈  3 s
    public static final int SCHEDULER_DELAY_MS = 30;
    public static final int PRE_CHECK_DELAY_MS = 40;
    public static final int WORKER_DELAY_MS    = 20;
    public static final int AUDITOR_DELAY_MS   = 15;

    // ─── Políticas de resultado ───────────────────────────────────────────────
    public static boolean isValidJob() {
        return random.nextInt(100) < 85; // 85% válido
    }

    public static boolean executionSuccess() {
        return random.nextInt(100) < 90; // 90% éxito
    }

    public static boolean finalCheckCorrect() {
        return random.nextInt(100) < 95; // 95% correcto
    }

    public static int randomIndex(int size) {
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        return random.nextInt(size);
    }
}
