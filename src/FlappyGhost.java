// Derin Akay 20234040
// Charles Lafontaine 20279271

import javafx.application.Application;
import javafx.stage.Stage;
import controller.GameController;
import javafx.scene.image.Image;


public class FlappyGhost extends Application {
    @Override
    public void start(Stage stage) {
        new GameController(stage);

        // add the ghost icon into the title‐bar
        stage.getIcons().add(
                new Image(getClass().getClassLoader().getResourceAsStream("ghost.png"))
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}

