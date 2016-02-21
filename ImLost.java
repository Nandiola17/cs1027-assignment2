/*
 * -interesting mazes; txt
 */

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * ImLost class is an algorithm made to solve custom maze problems using STACKS.
 * All moves are put on a stack and all legal combination are tried till the end is reached.
 * If the algorithm hits a dead end, the algorithm pops the most recent that dont have any other legal alternative moves. Then it revisisits the the most recent moves with alternatives and tries the alternatives. 
 * @author Nandiola XPS
 *
 */
public class ImLost {

	/**
	 * CONSTANT, FINAL, STATIC.
	 * Number of sides on the hexagon. 
	 * This value is the same throughout the maze(s) and is used to calculate how many possible neighbours a hexagon might have.
	 */
	public final static int NUMBER_OF_SIDES = 6;
	
	/**
	 * Global variables. Varies from maze to maze.
	 * Step Counter. Keeps count of the number of steps the algorithm takes to reach the end tile.
	 * Starts as 1 because start tile will always be pushed.
	 */
	public static int steps = 1; 

	/**
	 * Calls all the other functions
	 * Catches all exceptions and prints custom messages
	 * @param filenames Takes in the filename where the maze is located. No UI inside the program
	 */
	public static void main(String filenames[]) {
		// new maze
		Maze maze = null;

		// Try Catch Errors
		try {
			if (filenames.length < 1) {
				throw new IllegalArgumentException();
			}
			//create maze with pased filename
			maze = new Maze(filenames[0]);
		} catch (UnknownMazeCharacterException e) {
			System.out.println("The <MAZE> file is corrupted or could not be read. Please try again.");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.out.println("Please provide a Maze file as a command line argument");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("File could not be found or could not be read. Please ensure the correct filepath is being used.");
			e.printStackTrace();
		}

		try {
			//run algorithm
			ArrayStack <Hexagon> stack = algorithm(maze);

			// algorithm has ended, finalise path
			finish(stack);
		} catch (EmptyCollectionException e) {
			System.out.println("No solution to this maze was found. Make sure the maze file has an end tile and that the start tile and end tile arent walled off.");
		}
	}

	/**
	 * Main algorithm class. 
	 * -Creates a stack
	 * -Pushes start tile and all subsequent legal moves
	 * -Pops any 'useless' tiles. These tiles are dead ends or lead to dead ends. All of their neighbours have been to tested for a path to the end tile. 
	 * -Changes the tile colors by manipulating the hexagon objext as it goes
	 * @param maze takes the maze object and solves. 
	 * @return returns the end stack; which is the solution for the maze.
	 */
	private static ArrayStack <Hexagon> algorithm(Maze maze){
		
		// start tile
		Hexagon startTile = maze.getStart();
		startTile.setStarted();

		// new stack with one tile, start tile
		ArrayStack<Hexagon> stack = new ArrayStack<Hexagon>();
		stack.push(startTile);

		// variables to be used by algorthm
		Hexagon current, next;

		// algorithm;run while stack isnt empty and end hasnt been reached
		while (!stack.isEmpty() && !stack.peek().isEnd()) {
			// current Hexagon
			current = stack.peek();

			// find next legal step
			next = findNext(current);
			// if same object returned, no legal move found
			if (next == current) {
				// pop the hexagon and change color
				stack.pop().setPopped();
			} else {
				// push and change color
				stack.push(next);
				next.setPushed();
			}
			
			// redraw
			maze.repaint();
			//increase counter as either something has been pushed or popped
			steps++;
		}
		
		return stack;
	}
	
	/**
	 * End method of the program. 
	 * Changes the color of the tiles used and the tiles not used. 
	 * Prints the end statement.
	 * @param myStack takes the solution of the maze, the end stack as a param and changes the value based on it. 
	 */
	private static void finish(ArrayStack<Hexagon> myStack) {
		//set end tile as finished
		myStack.peek().setFinished();
		
		//convert true to yes and false to no
		String found;
		if (myStack.peek().isEnd()){
			found = "Yes";
		} else{
			found ="No";
		}
		
		//end statement
		System.out.println("MAZE SOLVED: \n" + 
				"Found: " + found +
				" Number of steps: " + steps +
				" Number of tiles on stack: " + myStack.size());
	}

	/**
	 * Called by the main algorithm class. 
	 * Finds the next possible legal move: move that doesnt go on to a wall tile, already stepped tile or a popped tile
	 * @param now the current position of the algorithm
	 * @return nextMove next move to be taken. Returns now as nextmove if this tile is deemed 'useless' by the function.
	 */
	private static Hexagon findNext(Hexagon now) {
		Hexagon nextMove = null;

		// assume current hexagon is uesless; everytime
		boolean useless;
		useless = true;

		// go through all the sides
		for (int i = 0; i < NUMBER_OF_SIDES; i++) {
			// test for null
			nextMove = now.getNeighbour(i);
			if (nextMove == null) {
				continue;
			}

			// if NOT wall, already visited or useless: push to stack
			if (!nextMove.isPopped() && !nextMove.isPushed() && !nextMove.isWall()) {
				// if legal move found; no longer useless
				useless = false;
				break;
			}
		}
		if (useless) {
			// if useless return the same hexagon. No legal moves found
			nextMove = now;
		}
		return nextMove;
	}
}
