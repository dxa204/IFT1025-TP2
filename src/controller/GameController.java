package controller;

import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;

import model.Ghost;
import model.Obstacle;
import model.SimpleObstacle;
import model.SinusObstacle;
import model.QuantumObstacle;
import view.GameView;
import common.GameConstants;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
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
    private boolean paused = false;

    // Image resources
    private final Image bgImage;
    private final Image ghostImage;
    private final List<Image> obstacleImages = new ArrayList<>();
    private final Map<Obstacle, Image> obstacleImageMap = new HashMap<>();

    /**
     * Initialise la fenêtre avec les différentes exigences.
     * @param stage La fenêtre principale de l'application.
     */
    public GameController(Stage stage) {
        view = new GameView();
        Scene scene = view.getScene();
        stage.setScene(scene);
        stage.setTitle("Flappy Ghost");
        stage.setResizable(false);
        stage.show();

        // Initial focus for keyboard
        Platform.runLater(() -> view.getCanvas().requestFocus());
        // Restore focus when clicking elsewhere
        scene.setOnMouseClicked(e -> view.getCanvas().requestFocus());
        System.out.println("bg.png URL = " + getClass().getResource("bg.png"));

        // Load images (ensure these files are in src/main/resources)
        InputStream is = getClass().getClassLoader().getResourceAsStream("bg.png");
        if (is == null) throw new RuntimeException("bg.png not found on classpath");
        bgImage = new Image(is);

        InputStream gis = getClass().getClassLoader().getResourceAsStream("ghost.png");
        if (gis == null) throw new RuntimeException("ghost.png not found on classpath");
        ghostImage = new Image(gis);

        for (int i = 0; i <= 26; i++) {
            String fn = i + ".png";
            InputStream obsIs = getClass().getClassLoader().getResourceAsStream("obstacles/" + fn);
            if (obsIs == null) throw new RuntimeException(fn + " not found on classpath");
            obstacleImages.add(new Image(obsIs));
        }


        // Keyboard input
        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.SPACE) ghost.saut();
            else if (evt.getCode() == KeyCode.ESCAPE) Platform.exit();
        });

        // Pause button
        view.getPauseButton().setOnAction(e -> {
            if ("Pause".equals(view.getPauseButton().getText())) {
                paused = true;
                boucle.stop();
                view.getPauseButton().setText("Resume");
            } else {
                paused = false;
                // Reset last time to prevent a huge dt
                dernierTempsNano = System.nanoTime();
                boucle.start();
                view.getPauseButton().setText("Pause");
            }
        });

        initGameModel();
        initGameLoop();
    }
    
    /**
     * Démarrage du jeu.
     */
    private void initGameModel() {
        ghost = new Ghost(0, GameConstants.HAUTEUR_JEU / 2.0);
        gravite = GameConstants.INIT_GRAVITE;
        obstacles = new ArrayList<>();
        passed    = new HashSet<>();
        score     = 0;
        tempsDepuisSpawn = 0.0;
        view.getScoreLabel().setText("Score: 0");
        obstacleImageMap.clear();
    }
    
    /**
     * Démarre la loop pour actualiser ce qui est affiché continuellement.
     */
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
    
    /**
     * Méthode qui met à jour ce qui est affiché sur l'application.
     * @param dt intervalle de temps    .
     */
    private void update(double dt) {
        ghost.update(dt, gravite, GameConstants.HAUTEUR_JEU);

        // Spawn obstacles at intervals
        tempsDepuisSpawn += dt;
        if (tempsDepuisSpawn >= GameConstants.INTERVALLE_SPAWN) {
            tempsDepuisSpawn -= GameConstants.INTERVALLE_SPAWN;
            double rayon = GameConstants.MIN_RAYON + Math.random() * (GameConstants.MAX_RAYON - GameConstants.MIN_RAYON);
            double y     = rayon + Math.random() * (GameConstants.HAUTEUR_JEU - 2 * rayon);
            double cameraX = ghost.getX();
            double x = cameraX + GameConstants.LARGEUR / 2.0 + rayon;
            int type = ThreadLocalRandom.current().nextInt(3);
            Obstacle ob;
            switch (type) {
                case 1:  ob = new SinusObstacle(x, y, rayon);    break;
                case 2:  ob = new QuantumObstacle(x, y, rayon); break;
                default: ob = new SimpleObstacle(x, y, rayon);  break;
            }
            obstacles.add(ob);
            // Assign a random sprite
            Image sprite = obstacleImages.get(
                    ThreadLocalRandom.current().nextInt(obstacleImages.size())
            );
            obstacleImageMap.put(ob, sprite);
        }

        // Scoring & collision & cleanup
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
            // Collision (disable in debug mode)
            double dx = ghost.getX() - ob.getX();
            double dy = ghost.getY() - ob.getY();
            boolean coll = Math.hypot(dx, dy) < Ghost.RAYON + ob.getRayon();
            if (!view.getDebugCheckBox().isSelected() && coll) {
                initGameModel();
                break;
            }
            // Remove off-screen
            double cameraX = ghost.getX();
            if (ob.getX() + ob.getRayon() < cameraX - GameConstants.LARGEUR / 2.0) it.remove();
        }
    }

    /**
     * Permet d'afficher les images aux emplacements des obstacles/ghost
     * ainsi qu'afficher des cercles si debug est activé.
     */
    private void draw() {
        GraphicsContext gc = view.getGraphicsContext();

        // Camera: always center on ghost's X position
        double cameraX = ghost.getX();

        // --- 1. Draw the background (scrolling, tiling as needed) ---
        double offset = cameraX % bgImage.getWidth();
        gc.clearRect(0, 0, GameConstants.LARGEUR, GameConstants.HAUTEUR_JEU);
        gc.drawImage(bgImage, -offset, 0);
        gc.drawImage(bgImage, -offset + bgImage.getWidth(), 0);

        // --- 2. Draw obstacles relative to camera ---
        for (Obstacle ob : obstacles) {
            double screenX = ob.getX() - cameraX + GameConstants.LARGEUR / 2.0;
            double ox = screenX - ob.getRayon();
            double oy = ob.getY() - ob.getRayon();
            double os = ob.getRayon() * 2;

            if (view.getDebugCheckBox().isSelected()) {
                double dx = GameConstants.LARGEUR / 2.0 - screenX;
                double dy = ghost.getY() - ob.getY();
                boolean c = Math.hypot(dx, dy) < Ghost.RAYON + ob.getRayon();
                gc.setFill(c ? Color.RED : Color.YELLOW);
                gc.fillOval(ox, oy, os, os);
            } else {
                Image sprite = obstacleImageMap.get(ob);
                if (sprite != null) {
                    gc.drawImage(sprite, ox, oy, os, os);
                } else {
                    gc.setFill(ob instanceof SimpleObstacle ? Color.RED :
                            ob instanceof SinusObstacle ? Color.YELLOW : Color.GREEN);
                    gc.fillOval(ox, oy, os, os);
                }
            }
        }

        // --- 3. Draw the ghost centered horizontally ---
        double ghostDrawX = GameConstants.LARGEUR / 2.0 - Ghost.RAYON;
        double gy = ghost.getY() - Ghost.RAYON;
        double gs = Ghost.RAYON * 2;
        if (view.getDebugCheckBox().isSelected()) {
            gc.setFill(Color.BLACK);
            gc.fillOval(ghostDrawX, gy, gs, gs);
        } else {
            gc.drawImage(ghostImage, ghostDrawX, gy, gs, gs);
        }
    }

}
