package puzzle.model;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleStateTest {

    PuzzleState state1 = new PuzzleState(); // the original initial state

    PuzzleState state2 = new PuzzleState(new Position(1, 1),
            new Position(1, 1),
            new Position(1, 1),
            new Position(1, 2)); // a goal state

    PuzzleState state3 = new PuzzleState(new Position(1, 1),
                new Position(2, 0),
                new Position(1, 1),
                new Position(0, 2)); // a non-goal state

    PuzzleState state4 = new PuzzleState(new Position(0, 0),
                new Position(1, 0),
                new Position(0, 1),
                new Position(0, 0)); // a dead-end state with no legal moves

    @Test
    void constructor() {
        var positions = new Position[] {
                new Position(0, 0),
                new Position(2, 0),
                new Position(1, 1),
                new Position(0, 2)
        };
        PuzzleState state = new PuzzleState(positions);
        for (var i = 0; i < 4; i++) {
            assertEquals(positions[i], state.getPosition(i));
        }
    }

    @Test
    void constructor_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState(new Position(0, 0)));
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState(new Position(0, 0),
                new Position(1, 1),
                new Position(2, 2),
                new Position(3, 3)));
        assertThrows(IllegalArgumentException.class, () -> new PuzzleState(new Position(1, 1),
                new Position(1, 1),
                new Position(1, 1),
                new Position(1, 1)));
    }

    @Test
    void isGoal() {
        assertFalse(state1.isGoal());
        assertTrue(state2.isGoal());
        assertFalse(state3.isGoal());
        assertFalse(state4.isGoal());
    }

    @Test
    void canMove_state1() {
        assertFalse(state1.canMove(Direction.UP));
        assertTrue(state1.canMove(Direction.RIGHT));
        assertTrue(state1.canMove(Direction.DOWN));
        assertFalse(state1.canMove(Direction.LEFT));
    }

    @Test
    void canMove_state2() {
        assertTrue(state2.canMove(Direction.UP));
        assertFalse(state2.canMove(Direction.RIGHT));
        assertTrue(state2.canMove(Direction.DOWN));
        assertTrue(state2.canMove(Direction.LEFT));
    }

    @Test
    void canMove_state3() {
        assertTrue(state3.canMove(Direction.UP));
        assertTrue(state3.canMove(Direction.RIGHT));
        assertTrue(state3.canMove(Direction.DOWN));
        assertTrue(state3.canMove(Direction.LEFT));
    }

    @Test
    void canMove_state4() {
        assertFalse(state4.canMove(Direction.UP));
        assertFalse(state4.canMove(Direction.RIGHT));
        assertFalse(state4.canMove(Direction.DOWN));
        assertFalse(state4.canMove(Direction.LEFT));
    }

    @Test
    void move_right_state1() {
        var copy = state1.clone();
        state1.move(Direction.RIGHT);
        assertEquals(copy.getPosition(0).getRight(), state1.getPosition(0));
        assertEquals(copy.getPosition(1), state1.getPosition(1));
        assertEquals(copy.getPosition(2), state1.getPosition(2));
        assertEquals(copy.getPosition(3), state1.getPosition(3));
    }

    @Test
    void move_down_state1() {
        var copy = state1.clone();
        state1.move(Direction.DOWN);
        assertEquals(copy.getPosition(0).getDown(), state1.getPosition(0));
        assertEquals(copy.getPosition(1), state1.getPosition(1));
        assertEquals(copy.getPosition(2), state1.getPosition(2));
        assertEquals(copy.getPosition(3), state1.getPosition(3));
    }

    @Test
    void move_up_state3() {
        var copy = state3.clone();
        state3.move(Direction.UP);
        assertEquals(copy.getPosition(0).getUp(), state3.getPosition(0));
        assertEquals(copy.getPosition(1), state3.getPosition(1));
        assertEquals(copy.getPosition(2), state3.getPosition(2));
        assertEquals(copy.getPosition(3), state3.getPosition(3));
    }

    @Test
    void move_right_state3() {
        var copy = state3.clone();
        state3.move(Direction.RIGHT);
        assertEquals(copy.getPosition(0).getRight(), state3.getPosition(0));
        assertEquals(copy.getPosition(1), state3.getPosition(1));
        assertEquals(copy.getPosition(2).getRight(), state3.getPosition(2));
        assertEquals(copy.getPosition(3), state3.getPosition(3));
    }

    @Test
    void move_down_state3() {
        var copy = state3.clone();
        state3.move(Direction.DOWN);
        assertEquals(copy.getPosition(0).getDown(), state3.getPosition(0));
        assertEquals(copy.getPosition(1), state3.getPosition(1));
        assertEquals(copy.getPosition(2).getDown(), state3.getPosition(2));
        assertEquals(copy.getPosition(3), state3.getPosition(3));
    }

    @Test
    void move_left_state3() {
        var copy = state3.clone();
        state3.move(Direction.LEFT);
        assertEquals(copy.getPosition(0).getLeft(), state3.getPosition(0));
        assertEquals(copy.getPosition(1), state3.getPosition(1));
        assertEquals(copy.getPosition(2).getLeft(), state3.getPosition(2));
        assertEquals(copy.getPosition(3), state3.getPosition(3));
    }

    @Test
    void getLegalMoves() {
        assertEquals(EnumSet.of(Direction.DOWN, Direction.RIGHT), state1.getLegalMoves());
        assertEquals(EnumSet.of(Direction.UP, Direction.DOWN, Direction.LEFT), state2.getLegalMoves());
        assertEquals(EnumSet.allOf(Direction.class), state3.getLegalMoves());
        assertEquals(EnumSet.noneOf(Direction.class), state4.getLegalMoves());
    }

    @Test
    void testEquals() {
        assertTrue(state1.equals(state1));

        var clone = state1.clone();
        clone.move(Direction.RIGHT);
        assertFalse(clone.equals(state1));

        assertFalse(state1.equals(null));
        assertFalse(state1.equals("Hello, World!"));
        assertFalse(state1.equals(state2));
    }

    @Test
    void testHashCode() {
        assertTrue(state1.hashCode() == state1.hashCode());
        assertTrue(state1.hashCode() == state1.clone().hashCode());
    }

    @Test
    void testClone() {
        var clone = state1.clone();
        assertTrue(clone.equals(state1));
        assertNotSame(clone, state1);
    }

    @Test
    void testToString() {
        assertEquals("[(0,0),(2,0),(1,1),(0,2)]", state1.toString());
        assertEquals("[(1,1),(1,1),(1,1),(1,2)]", state2.toString());
        assertEquals("[(1,1),(2,0),(1,1),(0,2)]", state3.toString());
        assertEquals("[(0,0),(1,0),(0,1),(0,0)]", state4.toString());
    }

}
