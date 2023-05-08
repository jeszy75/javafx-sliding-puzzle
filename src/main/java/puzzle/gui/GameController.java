package puzzle.gui;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.tinylog.Logger;
import puzzle.model.Direction;
import puzzle.model.PuzzleState;
import puzzle.util.ImageStorage;
import puzzle.util.OrdinalImageStorage;

import java.util.Optional;

public class GameController {

    @FXML
    private GridPane grid;

    @FXML
    private TextField numberOfMovesField;

    private ImageStorage<Integer> imageStorage = new OrdinalImageStorage("/images",
            "block.png",
            "red-shoe.png",
            "blue-shoe.png",
            "black-shoe.png");

    private PuzzleState state;

    private IntegerProperty numberOfMoves = new SimpleIntegerProperty(0);

    @FXML
    private void initialize() {
        createControlBindings();
        restartGame();
        populateGrid();
        registerKeyEventHandler();
    }

    private void createControlBindings() {
        numberOfMovesField.textProperty().bind(numberOfMoves.asString());
    }

    private void restartGame() {
        state = new PuzzleState();
        numberOfMoves.set(0);
        populateGrid();
        state.goalProperty().addListener(this::handleGameOver);
    }

    private void registerKeyEventHandler() {
        Platform.runLater(() -> grid.getScene().setOnKeyPressed(this::handleKeyPress));
    }

    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        var restartKeyCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        var quitKeyCombination = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        if (restartKeyCombination.match(keyEvent)) {
            Logger.debug("Restarting game");
            restartGame();
        } else if (quitKeyCombination.match(keyEvent)) {
            Logger.debug("Exiting");
            Platform.exit();
        } else if (keyEvent.getCode() == KeyCode.UP) {
            Logger.debug("UP pressed");
            performMove(Direction.UP);
        } else if (keyEvent.getCode() == KeyCode.RIGHT) {
            Logger.debug("RIGHT pressed");
            performMove(Direction.RIGHT);
        } else if (keyEvent.getCode() == KeyCode.DOWN) {
            Logger.debug("DOWN pressed");
            performMove(Direction.DOWN);
        } else if (keyEvent.getCode() == KeyCode.LEFT) {
            Logger.debug("LEFT pressed");
            performMove(Direction.LEFT);
        }
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var source = (Node) event.getSource();
        var row = GridPane.getRowIndex(source);
        var col = GridPane.getColumnIndex(source);
        Logger.debug("Click on square ({},{})", row, col);
        var direction = getDirectionFromClick(row, col);
        direction.ifPresentOrElse(this::performMove,
                () -> Logger.warn("Click does not correspond to any direction"));
    }

    private void performMove(Direction direction) {
        if (state.canMove(direction)) {
            Logger.info("Moving {}", direction);
            state.move(direction);
            Logger.trace("New state: {}", state);
            numberOfMoves.set(numberOfMoves.get() + 1);
        } else {
            Logger.warn("Invalid move: {}", direction);
        }
    }

    private void handleGameOver(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Game Over");
            alert.setContentText("Congratulations, you have solved the puzzle!");
            alert.showAndWait();
            restartGame();
        }
    }

    private void populateGrid() {
        for (var row = 0; row < grid.getRowCount(); row++) {
            for (var col = 0; col < grid.getColumnCount(); col++) {
                var square = createSquare(row, col);
                grid.add(square, col, row);
            }
        }
    }

    private StackPane createSquare(int row, int col) {
        var square = new StackPane();
        square.getStyleClass().add("square");
        square.getStyleClass().add((row + col) % 2 == 0 ? "light": "dark");
        for (var i = 0; i < 4; i++) {
            var pieceView = new ImageView(imageStorage.get(i));
            pieceView.visibleProperty().bind(createBindingForPieceAtPosition(i, row, col));
            square.getChildren().add(pieceView);
        }
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    private BooleanBinding createBindingForPieceAtPosition(int n, int row, int col) {
        return new BooleanBinding() {
            {
                super.bind(state.positionProperty(n));
            }
            @Override
            protected boolean computeValue() {
                var pos = state.positionProperty(n).get();
                return pos.row() == row && pos.col() == col;
            }
        };
    }

    private Optional<Direction> getDirectionFromClick(int row, int col) {
        var blockPos = state.getPosition(PuzzleState.BLOCK);
        Direction direction = null;
        try {
            direction = Direction.of(row - blockPos.row(), col - blockPos.col());
        } catch (IllegalArgumentException e) {
        }
        return Optional.ofNullable(direction);
    }

}