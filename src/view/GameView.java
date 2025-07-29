package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import common.GameConstants;

/**
 * View: crée et expose tous les éléments graphiques et la scène.
 */
public class GameView {
    private final BorderPane root;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Button pauseBtn;
    private final CheckBox debugCheck;
    private final Label scoreLabel;
    private final Scene scene;

    public GameView() {
        root = new BorderPane();

        // Canvas setup
        canvas = new Canvas(GameConstants.LARGEUR, GameConstants.HAUTEUR_JEU);
        gc = canvas.getGraphicsContext2D();
        root.setCenter(canvas);

        // UI bar
        HBox uiBar = new HBox(10);
        uiBar.setPrefHeight(GameConstants.HAUTEUR_FENETRE - GameConstants.HAUTEUR_JEU);
        uiBar.setAlignment(Pos.CENTER_LEFT);
        pauseBtn   = new Button("Pause");
        debugCheck = new CheckBox("Debug");
        scoreLabel = new Label("Score: 0");
        uiBar.getChildren().addAll(pauseBtn, debugCheck, scoreLabel);
        root.setBottom(uiBar);

        scene = new Scene(root, GameConstants.LARGEUR, GameConstants.HAUTEUR_FENETRE);
    }

    public Scene getScene() { return scene; }
    public Canvas getCanvas() { return canvas; }
    public GraphicsContext getGraphicsContext() { return gc; }
    public Button getPauseButton() { return pauseBtn; }
    public CheckBox getDebugCheckBox() { return debugCheck; }
    public Label getScoreLabel() { return scoreLabel; }
}
