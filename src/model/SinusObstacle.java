package model;

/**
 * Obstacle qui oscille verticalement en sinusoïde (amplitude = 50 px).
 */
public class SinusObstacle extends Obstacle {
    private static final double AMPLITUDE = 50.0;
    private static final double OMEGA     = 2 * Math.PI; // 1 cycle/s

    private final double y0;   // position y initiale
    private double timeAcc;    // accumulateur de temps

    public SinusObstacle(double xInitial, double yInitial, double rayon) {
        super(xInitial, yInitial, rayon);
        this.y0        = yInitial;
        this.timeAcc   = 0.0;
    }

    @Override
    public void update(double dt, double vitesseXFantome) {
        // 1) défilement horizontal identique au simple
        x -= vitesseXFantome * dt;

        // 2) mouvement sinusoïdal vertical
        timeAcc += dt;
        y = y0 + AMPLITUDE * Math.sin(OMEGA * timeAcc);
    }
}
