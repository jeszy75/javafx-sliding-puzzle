package puzzle.model;

/**
 * Represents a 2D position.
 */
public record Position(int row, int col) {

    /**
     * {@return the position whose vertical and horizontal distances from this
     * position are equal to the coordinate changes of the direction given}
     *
     * @param direction a direction that specifies a change in the coordinates
     */
    public Position getPosition(Direction direction) {
        return new Position(row + direction.getRowChange(), col + direction.getColChange());
    }

    /**
     * Convenience method that is equivalent to {@code getPosition(Direction.UP)}.
     *
     * @return the position above this position
     */
    public Position getUp() {
        return getPosition(Direction.UP);
    }

    /**
     * Convenience method that is equivalent to {@code getPosition(Direction.RIGHT)}.
     *
     * @return the position to the right of this position
     */
    public Position getRight() {
        return getPosition(Direction.RIGHT);
    }

    /**
     * Convenience method that is equivalent to {@code getPosition(Direction.DOWN)}.
     *
     * @return the position below this position
     */
    public Position getDown() {
        return getPosition(Direction.DOWN);
    }

    /**
     * Convenience method that is equivalent to {@code getPosition(Direction.LEFT)}.
     *
     * @return the position to the left of this position
     */
    public Position getLeft() {
        return getPosition(Direction.LEFT);
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }

}
