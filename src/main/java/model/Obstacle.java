package model;

/**
 * Classe abstraite représentant un obstacle.
 */
public abstract class Obstacle {

    protected double x;
    protected double y;
    protected final double rayon;

    public Obstacle(double xInitial, double yInitial, double rayon) {
        this.x     = xInitial;
        this.y     = yInitial;
        this.rayon = rayon;
    }

    /**
     * Met à jour la position horizontale de l'obstacle
     * par rapport à la vitesse du fantôme.
     * @param dt      delta‑temps en secondes
     * @param vitesseXFantome vitesse horizontale du fantôme (px/s)
     */
    public abstract void update(double dt, double vitesseXFantome);

    // Getters
    public double getX()     { return x; }
    public double getY()     { return y; }
    public double getRayon(){ return rayon; }
}
