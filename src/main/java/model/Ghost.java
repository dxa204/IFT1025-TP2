package model;

/**
 * Représente le fantôme (joueur).
 */
public class Ghost {

    // --- Constantes physiques ---
    public static final double RAYON = 30.0;
    private static final double VITESSE_INIT_X = 120.0;   // px/s
    private static final double LIMITE_VY = 300.0;        // |vy| max
    private static final double IMPULSION_SAUT = -300.0;  // vx négatif = vers le haut

    // --- État dynamique ---
    private double x;
    private double y;
    private double vx;
    private double vy;

    public Ghost(double xInitial, double yInitial) {
        this.x = xInitial;
        this.y = yInitial;
        this.vx = VITESSE_INIT_X;
        this.vy = 0.0;
    }

    public void saut() {
        vy = IMPULSION_SAUT;
    }

    /**
     * Met à jour la position du fantôme.
     * @param dt      delta temps en secondes
     * @param gravite accélération verticale (positive vers le bas)
     * @param hauteurLimite hauteur maximale du canvas de jeu (rebonds)
     */
    public void update(double dt, double gravite, double hauteurLimite) {
        // appliquer gravité
        vy += gravite * dt;

        // clamp vy
        if (vy >  LIMITE_VY) vy =  LIMITE_VY;
        if (vy < -LIMITE_VY) vy = -LIMITE_VY;

        // avancer
        x += vx * dt;
        y += vy * dt;

        // rebonds haut/bas
        if (y - RAYON < 0) {
            y = RAYON;
            vy = Math.abs(vy); // rebond vers le bas
        } else if (y + RAYON > hauteurLimite) {
            y = hauteurLimite - RAYON;
            vy = -Math.abs(vy); // rebond vers le haut
        }
    }

    // --- Getters ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }

    /**
     * Augmente la vitesse horizontale du fantôme.
     * @param deltaV en px/s
     */
    public void augmenterVitesse(double deltaV) {
        vx += deltaV;
    }

    /**
     * Réinitialise le fantôme (pour après collision).
     */
    public void reset(double xInitial, double yInitial) {
        this.x  = xInitial;
        this.y  = yInitial;
        this.vx = VITESSE_INIT_X;
        this.vy = 0.0;
    }

}

