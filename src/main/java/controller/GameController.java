// File: src/main/java/controller/GameController.java
package controller;

import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import model.Ghost;
import model.Obstacle;
import model.SimpleObstacle;
import model.SinusObstacle;
import model.QuantumObstacle;
import view.GameView;
// At top of GameView.java and GameController.java
import common.GameConstants;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Controller: orchestre le modèle et la vue, gère la boucle de jeu.
 */
public class GameController {
    private final GameView view;
    private Ghost ghost;
    private double gravite;
    private List<Obstacle> obstacles;
    private Set<Obstacle> passed;
    private int score;
    private double tempsDepuisSpawn;

    private AnimationTimer boucle;
    private long dernierTempsNano;

    public GameController(Stage stage) {
        view = new GameView();
        Scene scene = view.getScene();
        stage.setScene(scene);
        stage.setTitle("Flappy Ghost");
        stage.setResizable(false);
        stage.show();
        Platform.runLater(() -> view.getCanvas().requestFocus());

        // Gestion du clavier
        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.SPACE) ghost.saut();
            else if (evt.getCode() == KeyCode.ESCAPE) Platform.exit();
        });

        // Bouton Pause
        view.getPauseButton().setOnAction(e -> {
            if ("Pause".equals(view.getPauseButton().getText())) {
                boucle.stop();
                view.getPauseButton().setText("Resume");
            } else {
                boucle.start();
                view.getPauseButton().setText("Pause");
            }
        });

        initGameModel();
        initGameLoop();
    }

    private void initGameModel() {
        ghost = new Ghost(0, GameConstants.HAUTEUR_JEU / 2.0);
        gravite = GameConstants.INIT_GRAVITE;
        obstacles = new ArrayList<>();
        passed    = new HashSet<>();
        score     = 0;
        tempsDepuisSpawn = 0.0;
        view.getScoreLabel().setText("Score: 0");
    }

    private void initGameLoop() {
        dernierTempsNano = System.nanoTime();
        boucle = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dt = (now - dernierTempsNano) / 1_000_000_000.0;
                dernierTempsNano = now;
                update(dt);
                draw();
            }
        };
        boucle.start();
    }

    private void update(double dt) {
        ghost.update(dt, gravite, GameConstants.HAUTEUR_JEU);

        // Spawn aléatoire
        tempsDepuisSpawn += dt;
        if (tempsDepuisSpawn >= GameConstants.INTERVALLE_SPAWN) {
            tempsDepuisSpawn -= GameConstants.INTERVALLE_SPAWN;
            double rayon = GameConstants.MIN_RAYON + Math.random() * (GameConstants.MAX_RAYON - GameConstants.MIN_RAYON);
            double y     = rayon + Math.random() * (GameConstants.HAUTEUR_JEU - 2 * rayon);
            double x     = GameConstants.LARGEUR + rayon;
            int type = ThreadLocalRandom.current().nextInt(3);
            Obstacle ob;
            switch (type) {
                case 1:
                    ob = new SinusObstacle(x, y, rayon);
                    break;
                case 2:
                    ob = new QuantumObstacle(x, y, rayon);
                    break;
                default:
                    ob = new SimpleObstacle(x, y, rayon);
                    break;
            }
            obstacles.add(ob);
        }

        // Scoring, collision, suppression
        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle ob = it.next();
            ob.update(dt, ghost.getVx());

            // Scoring
            if (!passed.contains(ob)
                    && ghost.getX() - Ghost.RAYON > ob.getX() + ob.getRayon()) {
                passed.add(ob);
                score += 5;
                view.getScoreLabel().setText("Score: " + score);
                if (passed.size() % 2 == 0) {
                    ghost.augmenterVitesse(GameConstants.VITESSE_INCREMENT);
                    gravite += GameConstants.GRAVITE_INCREMENT;
                }
            }

            // Collision (sauf en debug)
            double dx = ghost.getX() - ob.getX();
            double dy = ghost.getY() - ob.getY();
            boolean coll = Math.hypot(dx, dy) < Ghost.RAYON + ob.getRayon();
            if (!view.getDebugCheckBox().isSelected() && coll) {
                initGameModel();
                break;
            }

            // Suppression hors-écran
            if (ob.getX() + ob.getRayon() < 0) {
                it.remove();
            }
        }
    }

    private void draw() {
        GraphicsContext gc = view.getGraphicsContext();
        gc.clearRect(0, 0, GameConstants.LARGEUR, GameConstants.HAUTEUR_JEU);

        // Ghost
        gc.setFill(Color.BLACK);
        gc.fillOval(
                ghost.getX() - Ghost.RAYON,
                ghost.getY() - Ghost.RAYON,
                Ghost.RAYON * 2,
                Ghost.RAYON * 2
        );

        // Obstacles
        for (Obstacle ob : obstacles) {
            if (view.getDebugCheckBox().isSelected()) {
                double dx = ghost.getX() - ob.getX();
                double dy = ghost.getY() - ob.getY();
                boolean c = Math.hypot(dx, dy) < Ghost.RAYON + ob.getRayon();
                gc.setFill(c ? Color.RED : Color.YELLOW);
            } else {
                if (ob instanceof SimpleObstacle)      gc.setFill(Color.RED);
                else if (ob instanceof SinusObstacle)  gc.setFill(Color.YELLOW);
                else                                    gc.setFill(Color.GREEN);
            }
            gc.fillOval(
                    ob.getX() - ob.getRayon(),
                    ob.getY() - ob.getRayon(),
                    ob.getRayon() * 2,
                    ob.getRayon() * 2
            );
        }
    }
}
