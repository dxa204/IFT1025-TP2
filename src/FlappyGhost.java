/**
 * Classe principale qui initialise l'application JavaFX et lance le contrôleur du jeu.
 *
 * @author Derin Akay 20234040
 * @author Charles Lafontaine 20279271
 */


import javafx.application.Application;
import javafx.stage.Stage;
import controller.GameController;
import javafx.scene.image.Image;


public class FlappyGhost extends Application {
    /**
     * Appelée lors du démarrage de l'application.
     * Initialise la fenêtre principale (Stage) et crée le contrôleur du jeu.
     *
     * @param stage La fenêtre principale de l'application.
     */
    @Override
    public void start(Stage stage) {
        // Crée et initialise le contrôleur du jeu avec la fenêtre principale
        new GameController(stage);

        // Ajoute l'icône du fantôme dans la barre de titre
        stage.getIcons().add(
                new Image(getClass().getClassLoader().getResourceAsStream("ghost.png"))
        );
    }

    /**
     * Lance l'application JavaFX.
     *
     * @param args Les arguments de la ligne de commande (pas utiles ici).
     */
    public static void main(String[] args) {
        launch(args);
    }
}

