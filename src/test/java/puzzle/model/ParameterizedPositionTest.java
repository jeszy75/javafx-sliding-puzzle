package puzzle.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParameterizedPositionTest {

    void assertPosition(int expectedRow, int expectedCol, Position position) {
        assertAll("position",
                () -> assertEquals(expectedRow, position.row()),
                () -> assertEquals(expectedCol, position.col())
        );
    }

    static Stream<Position> positionProvider() {
        return Stream.of(new Position(0, 0),
                new Position(0, 2),
                new Position(2, 0),
                new Position(2, 2));
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void getPosition(Position position) {
        assertPosition(position.row() - 1, position.col(), position.getPosition(Direction.UP));
        assertPosition(position.row(), position.col() + 1, position.getPosition(Direction.RIGHT));
        assertPosition(position.row() + 1, position.col(), position.getPosition(Direction.DOWN));
        assertPosition(position.row(), position.col() - 1, position.getPosition(Direction.LEFT));
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void getUp(Position position) {
        assertPosition(position.row() - 1, position.col(), position.getUp());
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void getRight(Position position) {
        assertPosition(position.row(), position.col() + 1, position.getRight());
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void getDown(Position position) {
        assertPosition(position.row() + 1, position.col(), position.getDown());
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void getLeft(Position position) {
        assertPosition(position.row(), position.col() - 1, position.getLeft());
    }

    @ParameterizedTest
    @MethodSource("positionProvider")
    void testToString(Position position) {
        assertEquals(String.format("(%d,%d)", position.row(), position.col()), position.toString());
    }

}
