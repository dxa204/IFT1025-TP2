package model;

/**
 * Obstacle fixe (ne se déplace pas verticalement).
 * Se décale vers la gauche à la vitesse du fantôme.
 */
public class SimpleObstacle extends Obstacle {

    public SimpleObstacle(double xInitial, double yInitial, double rayon) {
        super(xInitial, yInitial, rayon);
    }

    @Override
    public void update(double dt, double vitesseXFantome) {
        // On simule le « défilement » : l'obstacle se décale vers la gauche
        x -= vitesseXFantome * dt;
    }
}
