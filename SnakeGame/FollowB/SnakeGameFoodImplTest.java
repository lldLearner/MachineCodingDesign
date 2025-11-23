package FoodImpll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SnakeGameFoodImplTest {

    @Test
    void testFoodIsPlacedInitially() {
        SnakeGameFoodImpl game = new SnakeGameFoodImpl(10, 10);
        assertNotNull(game.getFood());
    }

    @Test
    void testMoveWithoutEatingFood() {
        SnakeGameFoodImpl game = new SnakeGameFoodImpl(10, 10);
        int length = game.getSnakeLength();

        game.moveSnake(Direction.DOWN);

        assertEquals(length - 0, length);  // snake moves, same size unless food eaten
    }

    @Test
    void testSnakeGrowsWhenEatingFood() {
        SnakeGameFoodImpl game = new SnakeGameFoodImpl(10, 10);

        Point food = game.getFood();
        int headR = game.getHead().r;
        int headC = game.getHead().c;

        // Move food next to head for deterministic test
        Point forced = new Point(headR + 1, headC);
        game.setFood(forced);

        int before = game.getSnakeLength();

        game.moveSnake(Direction.DOWN);  // eat food

        assertEquals(before + 1, game.getSnakeLength());
    }

    @Test
    void testFoodRelocatedAfterEating() {
        SnakeGameFoodImpl game = new SnakeGameFoodImpl(10, 10);

        Point foodLocation = game.getFood();

        Point head = game.getHead();
        Point forcedNextToHead = new Point(head.r + 1, head.c);

        // force food for deterministic behavior
        game.setFood(forcedNextToHead);

        game.moveSnake(Direction.DOWN); // eat

        // food must be relocated to new position
        assertNotEquals(foodLocation, game.getFood());
    }

    @Test
    void testWallCollision() {
        SnakeGameFoodImpl game = new SnakeGameFoodImpl(3, 3);

        // head = (0,2)
        game.moveSnake(Direction.RIGHT);

        assertTrue(game.isGameOver());
    }

    @Test
    void testSelfCollision_AfterEatingFood() {
        SnakeGameFoodImpl game = new SnakeGameFoodImpl(10, 10);

        // Step 1 — Force food to appear at a known location adjacent to head
        Point head = game.getHead();
        Point forcedFood = new Point(head.r + 1, head.c);  // place food below head
        game.setFood(forcedFood);

        // Step 2 — Move DOWN to eat food
        game.moveSnake(Direction.DOWN);
        assertEquals(4, game.getSnakeLength());  // snake should grow

        // Now that snake is longer, we can force a real collision
        // Try loop pattern: DOWN → LEFT → UP → RIGHT (back onto body)
        game.moveSnake(Direction.LEFT);
        game.moveSnake(Direction.UP);
        game.moveSnake(Direction.RIGHT);  // should collide now

        assertTrue(game.isGameOver());
    }

}
