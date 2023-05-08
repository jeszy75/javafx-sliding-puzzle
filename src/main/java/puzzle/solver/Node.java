package puzzle.solver;

import puzzle.model.Direction;
import puzzle.model.PuzzleState;

import java.util.EnumSet;
import java.util.Optional;

public class Node {

    private PuzzleState state;
    private EnumSet<Direction> operators;
    private Optional<Node> parent;
    private Optional<Direction> direction;

    public Node(PuzzleState state) {
        this.state = state;
        parent = Optional.empty();
        direction = Optional.empty();
        operators = state.getLegalMoves();
    }

    public Node(PuzzleState state, Node parent, Direction direction) {
        this(state);
        this.parent = Optional.of(parent);
        this.direction = Optional.of(direction);
    }

    public PuzzleState getState() {
        return state;
    }

    public Optional<Node> getParent() {
        return parent;
    }

    public Optional<Direction> getDirection() {
        return direction;
    }

    public boolean hasNextChild() {
        return !operators.isEmpty();
    }

    public Optional<Node> nextChild() {
        if (! hasNextChild()) {
            return Optional.empty();
        }
        var iterator = operators.iterator();
        var direction = iterator.next();
        iterator.remove();
        var newState = state.clone();
        newState.move(direction);
        return Optional.of(new Node(newState, this, direction));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return false;
        }
        return (o instanceof Node other) && state.equals(other.getState());
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public String toString() {
        return direction.isPresent() ? String.format("%s %s", direction.get(), state) : state.toString();
    }

}
