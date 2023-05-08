package puzzle.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;

import java.util.EnumSet;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Represents the state of the puzzle.
 */
public class PuzzleState implements Cloneable {

    /**
     * The size of the board.
     */
    public static final int BOARD_SIZE = 3;

    /**
     * The index of the block.
     */
    public static final int BLOCK = 0;

    /**
     * The index of the red shoe.
     */
    public static final int RED_SHOE = 1;

    /**
     * The index of the blue shoe.
     */
    public static final int BLUE_SHOE = 2;

    /**
     * The index of the black shoe.
     */
    public static final int BLACK_SHOE = 3;

    private ReadOnlyObjectWrapper<Position>[] positions = new ReadOnlyObjectWrapper[4];

    private ReadOnlyBooleanWrapper goal = new ReadOnlyBooleanWrapper();

    /**
     * Creates a {@code PuzzleState} object that corresponds to the original
     * initial state of the puzzle.
     */
    public PuzzleState() {
        this(new Position(0, 0),
                new Position(2, 0),
                new Position(1, 1),
                new Position(0, 2)
        );
    }

    /**
     * Creates a {@code PuzzleState} object initializing the positions of the
     * pieces with the positions specified. The constructor expects an array of
     * four {@code Position} objects or four {@code Position} objects.
     *
     * @param positions the initial positions of the pieces
     */
    public PuzzleState(Position... positions) {
        checkPositions(positions);
        for (var i = 0; i < positions.length; i++) {
            this.positions[i] = new ReadOnlyObjectWrapper<>(positions[i]);
        }
        goal.bind(this.positions[RED_SHOE].isEqualTo(this.positions[BLUE_SHOE]));
    }

    private void checkPositions(Position[] positions) {
        if (positions.length != 4) {
            throw new IllegalArgumentException();
        }
        for (var position : positions) {
            if (!isOnBoard(position)) {
                throw new IllegalArgumentException();
            }
        }
        if (positions[BLUE_SHOE].equals(positions[BLACK_SHOE])) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * {@return a copy of the position of the piece specified}
     *
     * @param n the number of a piece
     */
    public Position getPosition(int n) {
        return positions[n].get();
    }

    public ReadOnlyObjectProperty<Position> positionProperty(int n) {
        return positions[n].getReadOnlyProperty();
    }

    /**
     * {@return whether the puzzle is solved}
     */
    public boolean isGoal() {
        return goal.get();
    }

    public ReadOnlyBooleanProperty goalProperty() {
        return goal.getReadOnlyProperty();
    }

    /**
     * {@return whether the block can be moved to the direction specified}
     *
     * @param direction a direction to which the block is intended to be moved
     */
    public boolean canMove(Direction direction) {
        return switch (direction) {
            case UP -> canMoveUp();
            case RIGHT -> canMoveRight();
            case DOWN -> canMoveDown();
            case LEFT -> canMoveLeft();
        };
    }

    private boolean canMoveUp() {
        return getPosition(BLOCK).row() > 0 && isEmpty(getPosition(BLOCK).getUp());
    }

    private boolean canMoveRight() {
        if (getPosition(BLOCK).col() == BOARD_SIZE - 1) {
            return false;
        }
        var right = getPosition(BLOCK).getRight();
        return isEmpty(right)
                || (getPosition(BLACK_SHOE).equals(right) && !haveEqualPositions(BLOCK, BLUE_SHOE));
    }

    private boolean canMoveDown() {
        if (getPosition(BLOCK).row() == BOARD_SIZE - 1) {
            return false;
        }
        var down = getPosition(BLOCK).getDown();
        if (isEmpty(down)) {
            return true;
        }
        if (haveEqualPositions(BLACK_SHOE, BLOCK)) {
            return false;
        }
        return getPosition(BLUE_SHOE).equals(down)
                || (getPosition(RED_SHOE).equals(down) && !haveEqualPositions(BLUE_SHOE, BLOCK));
    }

    private boolean canMoveLeft() {
        return getPosition(BLOCK).col() > 0 && isEmpty(getPosition(BLOCK).getLeft());
    }

    /**
     * Moves the block to the direction specified.
     *
     * @param direction the direction to which the block is moved
     */
    public void move(Direction direction) {
        switch (direction) {
            case UP -> moveUp();
            case RIGHT -> moveRight();
            case DOWN -> moveDown();
            case LEFT -> moveLeft();
        }
    }

    private void moveUp() {
        if (haveEqualPositions(BLACK_SHOE, BLOCK)) {
            if (haveEqualPositions(RED_SHOE, BLOCK)) {
                movePiece(RED_SHOE, Direction.UP);
            }
            movePiece(BLACK_SHOE, Direction.UP);
        }
        movePiece(BLOCK, Direction.UP);
    }

    private void moveRight() {
        move(Direction.RIGHT, RED_SHOE, BLUE_SHOE, BLACK_SHOE);
    }

    private void moveDown() {
        move(Direction.DOWN, RED_SHOE, BLUE_SHOE, BLACK_SHOE);
    }

    private void moveLeft() {
        move(Direction.LEFT, RED_SHOE, BLUE_SHOE);
    }

    /**
     * Moves the block to the direction specified and also any of the shoes
     * specified that are at the same position with the block.
     *
     * @param direction the direction to which the block is moved
     * @param shoes the shoes that must be moved together with the block
     */
    private void move(Direction direction, int... shoes) {
        for (var i : shoes) {
            if (haveEqualPositions(i, BLOCK)) {
                movePiece(i, direction);
            }
        }
        movePiece(BLOCK, direction);
    }

    private void movePiece(int n, Direction direction) {
        var newPosition = getPosition(n).getPosition(direction);
        positions[n].set(newPosition);
    }

    /**
     * {@return the set of directions to which the block can be moved}
     */
    public EnumSet<Direction> getLegalMoves() {
        var legalMoves = EnumSet.noneOf(Direction.class);
        for (var direction : Direction.values()) {
            if (canMove(direction)) {
                legalMoves.add(direction);
            }
        }
        return legalMoves;
    }

    private boolean haveEqualPositions(int i, int j) {
        return getPosition(i).equals(getPosition(j));
    }

    private boolean isOnBoard(Position position) {
        return position.row() >= 0 && position.row() < BOARD_SIZE &&
                position.col() >= 0 && position.col() < BOARD_SIZE;
    }

    private boolean isEmpty(Position position) {
        for (var p : positions) {
            if (p.get().equals(position)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof PuzzleState other) && equals(positions, other.positions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positions[0].get(), positions[1].get(), positions[2].get(), positions[3].get());
    }

    @Override
    public PuzzleState clone() {
        return new PuzzleState(getPosition(0), getPosition(1), getPosition(2), getPosition(3));
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(",", "[", "]");
        for (var position : positions) {
            sj.add(position.get().toString());
        }
        return sj.toString();
    }

    private boolean equals(ObservableValue[] a1, ObservableValue[] a2) {
        if (a1.length != a2.length) {
            return false;
        }
        for (var i = 0; i < a1.length; i++) {
            if (!a1[i].getValue().equals(a2[i].getValue())) {
                return false;
            }
        }
        return true;
    }

}
