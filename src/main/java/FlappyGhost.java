import javafx.application.Application;
import javafx.stage.Stage;
import controller.GameController;

public class FlappyGhost extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Delegate *all* setup & game‑loop work to the controller
        new GameController(primaryStage);
    }
}
