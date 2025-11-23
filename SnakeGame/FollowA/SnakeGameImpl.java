package FollowA;

import java.util.*;

enum Direction {
    UP, DOWN, LEFT, RIGHT
}

class Point {
    int r, c;
    Point(int r, int c) { this.r = r; this.c = c; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) return false;
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

class SnakeGameImpl implements SnakeGame {

    private final int rows, cols;
    private final Deque<Point> snake = new ArrayDeque<>();
    private final Set<Point> occupied = new HashSet<>();
    private boolean gameOver = false;
    private int moves = 0;

    public SnakeGameImpl(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        // Initial snake of length 3 at (0,0), (0,1), (0,2)
        snake.addFirst(new Point(0, 2));
        snake.addLast(new Point(0, 1));
        snake.addLast(new Point(0, 0));

        occupied.addAll(snake);
    }

    public int getSnakeLength() {
        return snake.size();
    }
    @Override
    public void moveSnake(Direction dir) {
        if (gameOver) return;

        moves++;
        Point head = snake.peekFirst();
        Point newHead = getNext(head, dir);

        // Hit wall?
        if (newHead.r < 0 || newHead.r >= rows || newHead.c < 0 || newHead.c >= cols) {
            gameOver = true;
            return;
        }

        // Hit itself?
        if (occupied.contains(newHead)) {
            gameOver = true;
            return;
        }

        // Insert new head
        snake.addFirst(newHead);
        occupied.add(newHead);

        // Growth rule: grow every 5 moves
        if (moves % 5 != 0) {
            // remove tail
            Point tail = snake.removeLast();
            occupied.remove(tail);
        }
    }

    private Point getNext(Point p, Direction d) {
        switch (d) {
            case UP: return new Point(p.r - 1, p.c);
            case DOWN: return new Point(p.r + 1, p.c);
            case LEFT: return new Point(p.r, p.c - 1);
            default: return new Point(p.r, p.c + 1);
        }
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }
    
    public static void main(String[] args) {
        SnakeGameImpl game = new SnakeGameImpl(10, 10);

        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.DOWN);
        game.moveSnake(Direction.RIGHT);

        System.out.println("Game Over: " + game.isGameOver());
        System.out.println("Length: " + game.getSnakeLength());
    }

}
