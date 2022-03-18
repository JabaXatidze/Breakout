/*
 * File: Breakout.java
 * -------------------
 * Name: Tato Gurgenidze
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1)
			* BRICK_SEP)
			/ NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	private static final int DELAY = 5;

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		System.out.println("hello");
		setup();
		addMouseListeners();
		play();
		if (numberOfTurnsLeft > 0) {
			removedBricks = 0;
			run();
		}
	}

	private void setup() {
		setUpTheBricks();
		setUpThePaddle();
		setUpTheBall();
		stats();
	}

	private void play() {
		vx = rgen.nextDouble(1.0, 3.0);
		vy = 2.0;
		if (rgen.nextBoolean())
			vx = -vx;
		while (gameIsStillGoing()) {
			ball.move(vx, vy);
			pause(DELAY);
			bounce();
			checkForCollision();
		}
		finishGame();
	}

	// draws the bricks
	private void setUpTheBricks() {
		int x = (WIDTH - BRICK_WIDTH * NBRICKS_PER_ROW - (NBRICKS_PER_ROW - 1)
				* BRICK_SEP) / 2;
		for (int i = 0; i < NBRICKS_PER_ROW; i++) {
			int y = BRICK_Y_OFFSET;
			for (int j = 0; j < NBRICK_ROWS; j++) {
				add(createBricks(x, y, j));
				y += BRICK_HEIGHT + BRICK_SEP;
			}
			x += BRICK_WIDTH + BRICK_SEP;
		}
	}

	// returns the bricks
	private GRect createBricks(int x, int y, int i) {
		GRect bricks = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		if ((i % 10) >= 8) {
			bricks.setColor(Color.cyan);
			bricks.setFillColor(Color.cyan);
		} else if ((i % 10) >= 6) {
			bricks.setColor(Color.green);
			bricks.setFillColor(Color.green);
		} else if ((i % 10) >= 4) {
			bricks.setColor(Color.yellow);
			bricks.setFillColor(Color.yellow);
		} else if ((i % 10) >= 2) {
			bricks.setColor(Color.orange);
			bricks.setFillColor(Color.orange);
		} else if ((i % 10) >= 0) {
			bricks.setColor(Color.red);
			bricks.setFillColor(Color.red);
		}
		bricks.setFilled(true);
		return bricks;
	}

	// draws the paddle
	private void setUpThePaddle() {
		paddle = new GRect((WIDTH - PADDLE_WIDTH) / 2, HEIGHT - PADDLE_Y_OFFSET
				- PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
	}

	public void mouseMoved(MouseEvent e) {
		if (PADDLE_WIDTH / 2 < e.getX() && e.getX() < WIDTH - PADDLE_WIDTH / 2) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH / 2, HEIGHT
					- PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		}
	}

	// draws the ball
	private void setUpTheBall() {
		ball = new GOval(WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS,
				2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
	}

	// adds statistics label
	private void stats() {
		stats = new GLabel("Turns Left: " + numberOfTurnsLeft);
		add(stats, 0, stats.getHeight());
	}

	// when ball collides with frames, changes direction
	private void bounce() {
		if (collidesWithSideWalls()) {
			vx = -vx;
		}
		if (collidesWithRoof()) {
			vy = -vy;
		}
	}

	// checks for collision, and when necessary changes ball's direction
	private void checkForCollision() {
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			if (rgen.nextBoolean())
				vx = -vx;
			vy = -vy;
		} else if (collider != null && collider != stats) {
			remove(collider);
			removedBricks++;
			if (rgen.nextBoolean())
				vx = -vx;
			vy = -vy;
		}
	}

	// when game is finished, displays interactive message
	private void finishGame() {
		if (removedBricks != NBRICK_ROWS * NBRICKS_PER_ROW) {
			GLabel loser = new GLabel("You Are Loser");
			loser.setFont("Sylfaen-italic-40");
			int x = (int) ((WIDTH - loser.getWidth()) / 2);
			int y = (int) ((HEIGHT + loser.getAscent()) / 2);
			removeAll();
			add(loser, x, y);
			numberOfTurnsLeft--;
		} else {
			GLabel winner = new GLabel("You Are Awesome");
			winner.setFont("Sylfaen-italic-40");
			int x = (int) ((WIDTH - winner.getWidth()) / 2);
			int y = (int) ((HEIGHT + winner.getAscent()) / 2);
			removeAll();
			add(winner, x, y);
			numberOfTurnsLeft--;
		}
		pause(350);
	}

	private boolean gameIsStillGoing() {
		return (ball.getY() < HEIGHT - 2 * BALL_RADIUS && removedBricks < NBRICK_ROWS
				* NBRICKS_PER_ROW);
	}

	private boolean collidesWithSideWalls() {
		return (ball.getX() < 0 || ball.getX() > WIDTH - 2 * BALL_RADIUS);
	}

	private boolean collidesWithRoof() {
		return (ball.getY() < 0);
	}

	private GObject getCollidingObject() {
		GObject collider = null;
		if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
				* BALL_RADIUS) != null) {
			collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, +2
					* BALL_RADIUS);
		} else if (getElementAt(ball.getX(), ball.getY()) != null) {
			collider = getElementAt(ball.getX(), ball.getY());
			if (collider == paddle)
				return null;
		} else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
			collider = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
			if (collider == paddle)
				return null;
		}
		return collider;
	}

	private GRect paddle;
	private GOval ball;
	private GLabel stats;
	private double vx;
	private double vy;
	private int removedBricks = 0;
	private int numberOfTurnsLeft = NTURNS;
	RandomGenerator rgen = RandomGenerator.getInstance();
}
