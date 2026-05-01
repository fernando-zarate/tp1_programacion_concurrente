package src;

import java.util.Random;

public class Politic {

    private static final Random random = new Random();

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
        return random.nextInt(size);
    }
}