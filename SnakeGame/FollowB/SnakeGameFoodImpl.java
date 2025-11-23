package FoodImpll;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

enum Direction {
	UP, DOWN, LEFT, RIGHT
}

class Point {
	int r, c;

	Point(int r, int c) {
		this.r = r;
		this.c = c;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point))
			return false;
		Point p = (Point) o;
		return r == p.r && c == p.c;
	}

	@Override
	public int hashCode() {
		return Objects.hash(r, c);
	}
}

interface SnakeGame {
	void moveSnake(Direction dir);

	boolean isGameOver();
}

class SnakeGameFoodImpl implements SnakeGame {

	private final int rows, cols;
	private final Deque<Point> snake = new ArrayDeque<>();
	private final Set<Point> occupied = new HashSet<>();
	private Point food;
	private boolean gameOver = false;
	private final Random rand = new Random();

	public SnakeGameFoodImpl(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;

		// Initial snake
		snake.addFirst(new Point(0, 2));
		snake.addLast(new Point(0, 1));
		snake.addLast(new Point(0, 0));

		occupied.addAll(snake);
		dropFood();
	}
	
	public Point getFood() {
	    return food;
	}

	public int getSnakeLength() {
	    return snake.size();
	}

	public Point getHead() {
		return snake.peekFirst();
	}
	
	public void setFood(Point point) {
		// TODO Auto-generated method stub
		food = point;
	}

	@Override
	public void moveSnake(Direction dir) {
		if (gameOver)
			return;

		Point head = snake.peekFirst();
		Point newHead = next(head, dir);

		// wall hit
		if (newHead.r < 0 || newHead.r >= rows || newHead.c < 0 || newHead.c >= cols) {
			gameOver = true;
			return;
		}

		// self hit
		if (occupied.contains(newHead)) {
			gameOver = true;
			return;
		}

		snake.addFirst(newHead);
		occupied.add(newHead);

		if (newHead.equals(food)) {
			// eat → grow → do NOT remove tail
			dropFood();
		} else {
			// normal move → remove tail
			Point tail = snake.removeLast();
			occupied.remove(tail);
		}
	}

	private Point next(Point p, Direction d) {
		switch (d) {
		case UP:
			return new Point(p.r - 1, p.c);
		case DOWN:
			return new Point(p.r + 1, p.c);
		case LEFT:
			return new Point(p.r, p.c - 1);
		default:
			return new Point(p.r, p.c + 1);
		}
	}

	private void dropFood() {
		while (true) {
			int r = rand.nextInt(rows);
			int c = rand.nextInt(cols);
			Point p = new Point(r, c);
			if (!occupied.contains(p)) {
				food = p;
				return;
			}
		}
	}

	@Override
	public boolean isGameOver() {
		return gameOver;
	}
	
	public static void main(String[] args) {
	    SnakeGameFoodImpl game = new SnakeGameFoodImpl(10, 10);

	    game.moveSnake(Direction.DOWN);
	    game.moveSnake(Direction.RIGHT);
	    game.moveSnake(Direction.UP);

	    System.out.println("Game Over: " + game.isGameOver());
	    System.out.println("Length: " + game.getSnakeLength());
	    System.out.println("Food at: " + game.getFood().r + "," + game.getFood().c);
	}

}
