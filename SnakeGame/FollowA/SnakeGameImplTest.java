package FollowA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SnakeGameImplTest {

    @Test
    void testInitialState() {
        SnakeGameImpl game = new SnakeGameImpl(10, 10);
        assertFalse(game.isGameOver());
    }

    @Test
    void testMoveWithoutCollision() {
        SnakeGameImpl game = new SnakeGameImpl(10, 10);
        game.moveSnake(Direction.RIGHT);  // should survive
        assertFalse(game.isGameOver());
    }

    @Test
    void testWallCollision() {
        SnakeGameImpl game = new SnakeGameImpl(3, 3);

        // initial snake head at (0,2)
        // moving right hits wall
        game.moveSnake(Direction.RIGHT);

        assertTrue(game.isGameOver());
    }

    @Test
    void testSnakeGrowsEvery5Moves() {
        SnakeGameImpl game = new SnakeGameImpl(20, 20);

        // initial length = 3
        int initialSize = game.getSnakeLength();

        for (int i = 0; i < 5; i++) {
            game.moveSnake(Direction.DOWN);
        }

        // grows by +1 after 5 moves
        assertEquals(initialSize + 1, game.getSnakeLength());
    }

    @Test
    void testSnakeDoesNotGrowBefore5Moves() {
        SnakeGameImpl game = new SnakeGameImpl(20, 20);

        int initial = game.getSnakeLength();
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN);

        // no growth yet
        assertEquals(initial, game.getSnakeLength());
    }

    @Test
    void testSelfCollision() {
        SnakeGameImpl game = new SnakeGameImpl(10, 10);
    	 // Grow snake first (5 moves → +1 growth)
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN); // grows here, length = 4

        // Now snake is long enough to collide with itself
        game.moveSnake(Direction.LEFT);
        game.moveSnake(Direction.UP);
        game.moveSnake(Direction.RIGHT); // collide into its own body

        assertTrue(game.isGameOver());
    }
}

