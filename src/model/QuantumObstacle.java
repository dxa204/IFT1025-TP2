package model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Obstacle quantique : téléportation aléatoire de ±30 px toutes les 0.2 s.
 */
public class QuantumObstacle extends Obstacle {
    private static final double TELEPORT_INTERVAL = 0.2;
    private static final double TELEPORT_RANGE    = 30.0;

    private double timeAcc;

    public QuantumObstacle(double xInitial, double yInitial, double rayon) {
        super(xInitial, yInitial, rayon);
        this.timeAcc = 0.0;
    }

    @Override
    public void update(double dt, double vitesseXFantome) {
        // 1) défilement horizontal
        x -= vitesseXFantome * dt;

        // 2) téléportation périodique
        timeAcc += dt;
        if (timeAcc >= TELEPORT_INTERVAL) {
            timeAcc -= TELEPORT_INTERVAL;
            // déplacement aléatoire entre –RANGE et +RANGE
            double dx = ThreadLocalRandom.current().nextDouble(-TELEPORT_RANGE, TELEPORT_RANGE);
            double dy = ThreadLocalRandom.current().nextDouble(-TELEPORT_RANGE, TELEPORT_RANGE);
            x += dx;
            y += dy;
        }
    }
}
